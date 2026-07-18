package com.tenure.domain.item.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tenure.domain.item.dto.ItemCreateRequest;
import com.tenure.domain.item.dto.ItemHistoryResponse;
import com.tenure.domain.item.entity.Category;
import com.tenure.domain.item.entity.Item;
import com.tenure.domain.item.entity.ItemHistory;
import com.tenure.domain.item.enums.AcquisitionType;
import com.tenure.domain.item.enums.EndReason;
import com.tenure.domain.item.enums.WearingTarget;
import com.tenure.domain.item.exception.ItemErrorCode;
import com.tenure.domain.item.repository.CategoryRepository;
import com.tenure.domain.item.repository.ItemHistoryRepository;
import com.tenure.domain.item.repository.ItemRepository;
import com.tenure.domain.product.repository.ProductRepository;
import com.tenure.domain.tag.repository.OotdTagRepository;
import com.tenure.domain.user.entity.User;
import com.tenure.domain.user.repository.UserRepository;
import com.tenure.global.exception.CustomException;
import com.tenure.global.response.PageResponse;
import java.lang.reflect.Constructor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    private static final Long CURRENT_USER_ID = 1L;
    private static final Long ITEM_ID = 10L;

    @Mock
    private OotdTagRepository ootdTagRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemHistoryRepository itemHistoryRepository;

    private ItemService itemService;

    @BeforeEach
    void setUp() {
        itemService = new ItemService(
                ootdTagRepository, productRepository, itemRepository,
                categoryRepository, userRepository, itemHistoryRepository
        );
    }

    @Test
    void createItem_savesOpenFirstRegisteredHistoryStartingAtFirstOwnedAt() {
        User owner = user(CURRENT_USER_ID);
        Category large = category(1L, "상의", 1, null);
        Category small = category(2L, "후디", 2, large);
        ItemCreateRequest request = new ItemCreateRequest(
                "Nike", "Gray Hoodie", WearingTarget.UNISEX, "상의", "후디",
                "KR", "L", LocalDate.of(2025, 10, 1), "https://image.url/item.jpg"
        );

        when(userRepository.findById(CURRENT_USER_ID)).thenReturn(Optional.of(owner));
        when(categoryRepository.findByNameAndDepthAndIsActiveTrue("상의", 1)).thenReturn(Optional.of(large));
        when(categoryRepository.findByNameAndParentAndDepthAndIsActiveTrue("후디", large, 2)).thenReturn(Optional.of(small));
        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));

        itemService.createItem(CURRENT_USER_ID, request);

        ArgumentCaptor<ItemHistory> captor = ArgumentCaptor.forClass(ItemHistory.class);
        verify(itemHistoryRepository).save(captor.capture());
        ItemHistory savedHistory = captor.getValue();
        assertThat(savedHistory.getOwner()).isEqualTo(owner);
        assertThat(savedHistory.getAcquisitionType()).isEqualTo(AcquisitionType.FIRST_REGISTERED);
        assertThat(savedHistory.getStartedAt()).isEqualTo(request.firstOwnedAt().atStartOfDay());
        assertThat(savedHistory.getEndReason()).isNull();
        assertThat(savedHistory.getEndedAt()).isNull();
    }

    @Test
    void createItem_futureFirstOwnedAt_throwsCustomException() {
        ItemCreateRequest request = new ItemCreateRequest(
                "Nike", "Gray Hoodie", WearingTarget.UNISEX, "상의", "후디",
                "KR", "L", LocalDate.now().plusDays(1), "https://image.url/item.jpg"
        );

        assertThatThrownBy(() -> itemService.createItem(CURRENT_USER_ID, request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ItemErrorCode.FIRST_OWNED_AT_IN_FUTURE);

        verify(itemRepository, never()).save(any(Item.class));
        verify(itemHistoryRepository, never()).save(any(ItemHistory.class));
    }

    @Test
    void getItemHistories_mapsOwnerAcquisitionTypeAndEndFields() {
        Item item = item(ITEM_ID);
        User owner = user(CURRENT_USER_ID);
        ReflectionTestUtils.setField(item, "owner", owner);
        when(itemRepository.findById(ITEM_ID)).thenReturn(Optional.of(item));

        ItemHistory closedHistory = ItemHistory.ofFirstRegistration(
                item, owner, LocalDateTime.of(2025, 1, 1, 0, 0)
        );
        closedHistory.close(EndReason.TENURE_TRADE, LocalDateTime.of(2025, 6, 1, 0, 0));
        Page<ItemHistory> page = new PageImpl<>(List.of(closedHistory));
        when(itemHistoryRepository.findByItemIdOrderByStartedAtDesc(eq(ITEM_ID), any(Pageable.class)))
                .thenReturn(page);

        PageResponse<ItemHistoryResponse> response =
                itemService.getItemHistories(CURRENT_USER_ID, ITEM_ID, PageRequest.of(0, 20));

        ItemHistoryResponse dto = response.getContent().get(0);
        assertThat(dto.ownerUserId()).isEqualTo(owner.getId());
        assertThat(dto.acquisitionType()).isEqualTo(AcquisitionType.FIRST_REGISTERED);
        assertThat(dto.endReason()).isEqualTo(EndReason.TENURE_TRADE);
        assertThat(dto.startedAt()).isEqualTo(LocalDateTime.of(2025, 1, 1, 0, 0));
        assertThat(dto.endedAt()).isEqualTo(LocalDateTime.of(2025, 6, 1, 0, 0));
    }

    private Item item(Long id) {
        Item item = instantiate(Item.class);
        ReflectionTestUtils.setField(item, "id", id);
        return item;
    }

    private Category category(Long id, String name, Integer depth, Category parent) {
        Category category = instantiate(Category.class);
        ReflectionTestUtils.setField(category, "id", id);
        ReflectionTestUtils.setField(category, "name", name);
        ReflectionTestUtils.setField(category, "depth", depth);
        ReflectionTestUtils.setField(category, "parent", parent);
        return category;
    }

    private User user(Long id) {
        User user = instantiate(User.class);
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }

    private <T> T instantiate(Class<T> type) {
        try {
            Constructor<T> constructor = type.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }
}
