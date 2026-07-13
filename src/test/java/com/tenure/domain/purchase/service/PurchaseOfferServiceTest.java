package com.tenure.domain.purchase.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tenure.domain.address.entity.DeliveryAddress;
import com.tenure.domain.address.repository.DeliveryAddressRepository;
import com.tenure.domain.common.enums.PaymentAuthorizationStatus;
import com.tenure.domain.item.entity.Item;
import com.tenure.domain.item.enums.ItemStatus;
import com.tenure.domain.item.repository.ItemRepository;
import com.tenure.domain.purchase.dto.PurchaseOfferCreateRequest;
import com.tenure.domain.purchase.dto.PurchaseOfferCreateResponse;
import com.tenure.domain.purchase.dto.PurchaseOfferDetailResponse;
import com.tenure.domain.purchase.dto.PurchaseOfferDetailResponse.DeliveryDisclosureStatus;
import com.tenure.domain.purchase.dto.PurchaseOfferDetailResponse.ViewerRole;
import com.tenure.domain.purchase.dto.PurchaseOfferReceivedListResponse;
import com.tenure.domain.purchase.dto.PurchaseOfferRejectResponse;
import com.tenure.domain.purchase.dto.PurchaseOfferSentListResponse;
import com.tenure.domain.purchase.entity.PurchaseOffer;
import com.tenure.domain.purchase.enums.PurchaseOfferStatus;
import com.tenure.domain.purchase.exception.PurchaseOfferErrorCode;
import com.tenure.domain.purchase.repository.PurchaseOfferRepository;
import com.tenure.domain.trade.entity.Trade;
import com.tenure.domain.trade.enums.TradeSourceType;
import com.tenure.domain.trade.repository.TradeRepository;
import com.tenure.domain.user.entity.User;
import com.tenure.domain.user.enums.UserGrade;
import com.tenure.domain.user.repository.UserRepository;
import com.tenure.global.exception.CustomException;
import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class PurchaseOfferServiceTest {

    private static final Long OFFER_ID = 123L;
    private static final Long ITEM_ID = 10L;
    private static final Long OWNER_ID = 1L;
    private static final Long PROPOSER_ID = 2L;
    private static final Long ADDRESS_ID = 100L;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private PurchaseOfferRepository purchaseOfferRepository;

    @Mock
    private DeliveryAddressRepository deliveryAddressRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TradeRepository tradeRepository;

    private PurchaseOfferService purchaseOfferService;

    @BeforeEach
    void setUp() {
        purchaseOfferService = new PurchaseOfferService(
                itemRepository,
                purchaseOfferRepository,
                new PurchaseOfferExpirationService(),
                deliveryAddressRepository,
                userRepository,
                tradeRepository
        );
    }

    @Test
    void createPurchaseOffer_createsAuthorizedOfferWithOwnerGradeFeeAndShippingFee() {
        User owner = user(OWNER_ID, UserGrade.BASIC, 5000);
        User proposer = user(PROPOSER_ID, UserGrade.RECORD, null);
        Item item = item(ITEM_ID, owner);
        DeliveryAddress address = address(ADDRESS_ID, proposer);

        givenOfferableItem(item, proposer, Optional.empty());
        when(deliveryAddressRepository.findByIdAndUser_Id(ADDRESS_ID, PROPOSER_ID)).thenReturn(Optional.of(address));
        when(purchaseOfferRepository.save(any(PurchaseOffer.class))).thenAnswer(invocation -> {
            PurchaseOffer offer = invocation.getArgument(0);
            ReflectionTestUtils.setField(offer, "id", OFFER_ID);
            return offer;
        });

        PurchaseOfferCreateResponse response = purchaseOfferService.createPurchaseOffer(
                ITEM_ID,
                PROPOSER_ID,
                request(360000, true)
        );

        assertThat(response.offerId()).isEqualTo(OFFER_ID);
        assertThat(response.status()).isEqualTo(PurchaseOfferStatus.SENT);
        assertThat(response.paymentAuthorizationStatus()).isEqualTo(PaymentAuthorizationStatus.AUTHORIZED);
        assertThat(response.remainingSeconds()).isPositive();
        assertThat(response.amounts().offerAmount()).isEqualTo(360000);
        assertThat(response.amounts().shippingFee()).isEqualTo(5000);
        assertThat(response.amounts().proposerServiceFee()).isEqualTo(21600);
        assertThat(response.amounts().totalPaymentAmount()).isEqualTo(386600);
        assertThat(response.amounts().ownerSettlementAmount()).isEqualTo(365000);

        ArgumentCaptor<PurchaseOffer> captor = ArgumentCaptor.forClass(PurchaseOffer.class);
        verify(purchaseOfferRepository).save(captor.capture());
        PurchaseOffer savedOffer = captor.getValue();
        assertThat(savedOffer.getPaymentAuthorizationId()).startsWith("mock_offer_auth_");
        assertThat(savedOffer.getFeeRateSnapshot()).isEqualByComparingTo("0.0600");
        assertThat(savedOffer.getDeliveryReceiverName()).isEqualTo("Proposer");
    }

    @Test
    void createPurchaseOffer_rejectsWhenOfferPriceIsLowerThanMinimum() {
        assertThatThrownBy(() -> purchaseOfferService.createPurchaseOffer(
                ITEM_ID,
                PROPOSER_ID,
                request(999, true)
        ))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(PurchaseOfferErrorCode.OFFER_PRICE_TOO_LOW);
    }

    @Test
    void createPurchaseOffer_rejectsWhenAgreementIsFalse() {
        assertThatThrownBy(() -> purchaseOfferService.createPurchaseOffer(
                ITEM_ID,
                PROPOSER_ID,
                request(1000, false)
        ))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(PurchaseOfferErrorCode.AGREEMENT_REQUIRED);
    }

    @Test
    void createPurchaseOffer_rejectsSelfOffer() {
        User owner = user(OWNER_ID, UserGrade.BASIC, null);
        Item item = item(ITEM_ID, owner);

        when(itemRepository.findByIdForUpdate(ITEM_ID)).thenReturn(Optional.of(item));
        when(userRepository.findById(OWNER_ID)).thenReturn(Optional.of(owner));

        assertThatThrownBy(() -> purchaseOfferService.createPurchaseOffer(
                ITEM_ID,
                OWNER_ID,
                request(1000, true)
        ))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(PurchaseOfferErrorCode.SELF_OFFER_NOT_ALLOWED);
    }

    @Test
    void createPurchaseOffer_rejectsWhenItemIsNotOwned() {
        User owner = user(OWNER_ID, UserGrade.BASIC, null);
        User proposer = user(PROPOSER_ID, UserGrade.BASIC, null);
        Item item = item(ITEM_ID, owner);
        ReflectionTestUtils.setField(item, "itemStatus", ItemStatus.ON_SALE);

        when(itemRepository.findByIdForUpdate(ITEM_ID)).thenReturn(Optional.of(item));
        when(userRepository.findById(PROPOSER_ID)).thenReturn(Optional.of(proposer));

        assertThatThrownBy(() -> purchaseOfferService.createPurchaseOffer(
                ITEM_ID,
                PROPOSER_ID,
                request(1000, true)
        ))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(PurchaseOfferErrorCode.ITEM_NOT_OWNED);
    }

    @Test
    void createPurchaseOffer_rejectsWhenPurchaseOfferDisabled() {
        User owner = user(OWNER_ID, UserGrade.BASIC, null);
        User proposer = user(PROPOSER_ID, UserGrade.BASIC, null);
        Item item = item(ITEM_ID, owner);
        ReflectionTestUtils.setField(item, "purchaseOfferEnabled", false);

        when(itemRepository.findByIdForUpdate(ITEM_ID)).thenReturn(Optional.of(item));
        when(userRepository.findById(PROPOSER_ID)).thenReturn(Optional.of(proposer));

        assertThatThrownBy(() -> purchaseOfferService.createPurchaseOffer(
                ITEM_ID,
                PROPOSER_ID,
                request(1000, true)
        ))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(PurchaseOfferErrorCode.PURCHASE_OFFER_DISABLED);
    }

    @Test
    void createPurchaseOffer_rejectsWhenOfferAlreadyExistsForSameUserAndItem() {
        User owner = user(OWNER_ID, UserGrade.BASIC, null);
        User proposer = user(PROPOSER_ID, UserGrade.BASIC, null);
        Item item = item(ITEM_ID, owner);
        PurchaseOffer existingOffer = offer(777L, item, proposer, owner, LocalDateTime.now().plusHours(1));

        givenOfferableItem(item, proposer, Optional.of(existingOffer));

        assertThatThrownBy(() -> purchaseOfferService.createPurchaseOffer(
                ITEM_ID,
                PROPOSER_ID,
                request(1000, true)
        ))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(PurchaseOfferErrorCode.PURCHASE_OFFER_ALREADY_USED);
    }

    @Test
    void getPurchaseOfferDetail_returnsProposerViewWithDeliveryAndWithoutOwnerSettlement() {
        User owner = user(OWNER_ID, UserGrade.BASIC, null);
        User proposer = user(PROPOSER_ID, UserGrade.BASIC, null);
        Item item = item(ITEM_ID, owner);
        PurchaseOffer offer = offer(OFFER_ID, item, proposer, owner, LocalDateTime.now().plusHours(2));

        givenOfferDetail(item, offer);

        PurchaseOfferDetailResponse response = purchaseOfferService.getPurchaseOfferDetail(OFFER_ID, PROPOSER_ID);

        assertThat(response.viewerRole()).isEqualTo(ViewerRole.PROPOSER);
        assertThat(response.status()).isEqualTo(PurchaseOfferStatus.SENT);
        assertThat(response.paymentAuthorizationStatus()).isEqualTo(PaymentAuthorizationStatus.AUTHORIZED);
        assertThat(response.remainingSeconds()).isPositive();
        assertThat(response.amounts().offerAmount()).isEqualTo(360000);
        assertThat(response.amounts().ownerSettlementAmount()).isNull();
        assertThat(response.deliveryDisclosureStatus()).isEqualTo(DeliveryDisclosureStatus.VISIBLE);
        assertThat(response.delivery()).isNotNull();
        assertThat(response.delivery().phone()).isEqualTo("010-1234-5678");

        InOrder lockOrder = inOrder(itemRepository, purchaseOfferRepository);
        lockOrder.verify(itemRepository).findByIdForUpdate(ITEM_ID);
        lockOrder.verify(purchaseOfferRepository).findByIdForUpdate(OFFER_ID);
    }

    @Test
    void getPurchaseOfferDetail_returnsOwnerViewWithoutDeliveryBeforeAcceptance() {
        User owner = user(OWNER_ID, UserGrade.BASIC, null);
        User proposer = user(PROPOSER_ID, UserGrade.BASIC, null);
        Item item = item(ITEM_ID, owner);
        PurchaseOffer offer = offer(OFFER_ID, item, proposer, owner, LocalDateTime.now().plusHours(2));

        givenOfferDetail(item, offer);

        PurchaseOfferDetailResponse response = purchaseOfferService.getPurchaseOfferDetail(OFFER_ID, OWNER_ID);

        assertThat(response.viewerRole()).isEqualTo(ViewerRole.OWNER);
        assertThat(response.amounts().ownerSettlementAmount()).isEqualTo(365000);
        assertThat(response.delivery()).isNull();
        assertThat(response.deliveryDisclosureStatus()).isEqualTo(DeliveryDisclosureStatus.AFTER_ACCEPTANCE);
    }

    @Test
    void getPurchaseOfferDetail_expiresSentOfferWhenPastDeadline() {
        User owner = user(OWNER_ID, UserGrade.BASIC, null);
        User proposer = user(PROPOSER_ID, UserGrade.BASIC, null);
        Item item = item(ITEM_ID, owner);
        PurchaseOffer offer = offer(OFFER_ID, item, proposer, owner, LocalDateTime.now().minusMinutes(1));

        givenOfferDetail(item, offer);

        PurchaseOfferDetailResponse response = purchaseOfferService.getPurchaseOfferDetail(OFFER_ID, PROPOSER_ID);

        assertThat(response.status()).isEqualTo(PurchaseOfferStatus.EXPIRED);
        assertThat(response.paymentAuthorizationStatus()).isEqualTo(PaymentAuthorizationStatus.RELEASED);
        assertThat(response.remainingSeconds()).isZero();
        assertThat(offer.getStatus()).isEqualTo(PurchaseOfferStatus.EXPIRED);
        assertThat(offer.getPaymentAuthorizationStatus()).isEqualTo(PaymentAuthorizationStatus.RELEASED);
    }

    @Test
    void getPurchaseOfferDetail_rejectsNonParticipant() {
        User owner = user(OWNER_ID, UserGrade.BASIC, null);
        User proposer = user(PROPOSER_ID, UserGrade.BASIC, null);
        Item item = item(ITEM_ID, owner);
        PurchaseOffer offer = offer(OFFER_ID, item, proposer, owner, LocalDateTime.now().plusHours(2));

        givenOfferDetail(item, offer);

        assertThatThrownBy(() -> purchaseOfferService.getPurchaseOfferDetail(OFFER_ID, 999L))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(PurchaseOfferErrorCode.PURCHASE_OFFER_ACCESS_DENIED);
    }

    @Test
    void getSentPurchaseOffers_returnsSentListWithCursorFields() {
        User owner = user(OWNER_ID, UserGrade.BASIC, null);
        User proposer = user(PROPOSER_ID, UserGrade.BASIC, null);
        Item item = item(ITEM_ID, owner);
        PurchaseOffer offer = offer(OFFER_ID, item, proposer, owner, LocalDateTime.now().plusHours(2));
        ReflectionTestUtils.setField(offer, "createdAt", LocalDateTime.now().minusMinutes(10));

        givenSentList(List.of(), List.of(offer));

        PurchaseOfferSentListResponse response = purchaseOfferService.getSentPurchaseOffers(
                PROPOSER_ID,
                List.of(PurchaseOfferStatus.SENT),
                null,
                null,
                20
        );

        assertThat(response.content()).hasSize(1);
        assertThat(response.content().get(0).offerId()).isEqualTo(OFFER_ID);
        assertThat(response.content().get(0).canCancel()).isTrue();
        assertThat(response.hasNext()).isFalse();
        assertThat(response.nextCursor()).isNull();
    }

    @Test
    void getSentPurchaseOffers_expiresSentBeforeQueryingList() {
        User owner = user(OWNER_ID, UserGrade.BASIC, null);
        User proposer = user(PROPOSER_ID, UserGrade.BASIC, null);
        Item item = item(ITEM_ID, owner);
        PurchaseOffer expiredOffer = offer(OFFER_ID, item, proposer, owner, LocalDateTime.now().minusMinutes(1));
        ReflectionTestUtils.setField(expiredOffer, "createdAt", LocalDateTime.now().minusMinutes(10));

        givenSentList(List.of(expiredOffer), List.of(expiredOffer));

        PurchaseOfferSentListResponse response = purchaseOfferService.getSentPurchaseOffers(
                PROPOSER_ID,
                List.of(PurchaseOfferStatus.EXPIRED),
                null,
                null,
                20
        );

        assertThat(expiredOffer.getStatus()).isEqualTo(PurchaseOfferStatus.EXPIRED);
        assertThat(expiredOffer.getPaymentAuthorizationStatus()).isEqualTo(PaymentAuthorizationStatus.RELEASED);
        assertThat(response.content()).hasSize(1);
        assertThat(response.content().get(0).canCancel()).isFalse();
    }

    @Test
    void getSentPurchaseOffers_mapsTradeIdForAcceptedOffer() {
        User owner = user(OWNER_ID, UserGrade.BASIC, null);
        User proposer = user(PROPOSER_ID, UserGrade.BASIC, null);
        Item item = item(ITEM_ID, owner);
        PurchaseOffer acceptedOffer = offer(OFFER_ID, item, proposer, owner, LocalDateTime.now().plusHours(2));
        ReflectionTestUtils.setField(acceptedOffer, "status", PurchaseOfferStatus.ACCEPTED);
        ReflectionTestUtils.setField(acceptedOffer, "createdAt", LocalDateTime.now().minusMinutes(10));
        Trade trade = trade(900L, acceptedOffer.getId());

        givenSentList(List.of(), List.of(acceptedOffer));
        when(tradeRepository.findAllBySourceTypeAndSourceIdIn(
                TradeSourceType.PURCHASE_OFFER,
                List.of(acceptedOffer.getId())
        )).thenReturn(List.of(trade));

        PurchaseOfferSentListResponse response = purchaseOfferService.getSentPurchaseOffers(
                PROPOSER_ID,
                List.of(PurchaseOfferStatus.ACCEPTED),
                null,
                null,
                20
        );

        assertThat(response.content()).hasSize(1);
        assertThat(response.content().get(0).tradeId()).isEqualTo(900L);
    }

    @Test
    void getReceivedPurchaseOffers_returnsReceivedListWithProposerAndOwnerAmounts() {
        User owner = user(OWNER_ID, UserGrade.BASIC, null);
        User proposer = user(PROPOSER_ID, UserGrade.BASIC, null);
        Item item = item(ITEM_ID, owner);
        PurchaseOffer offer = offer(OFFER_ID, item, proposer, owner, LocalDateTime.now().plusHours(2));
        ReflectionTestUtils.setField(offer, "createdAt", LocalDateTime.now().minusMinutes(10));

        givenReceivedList(List.of(), List.of(offer));

        PurchaseOfferReceivedListResponse response = purchaseOfferService.getReceivedPurchaseOffers(
                OWNER_ID,
                List.of(PurchaseOfferStatus.SENT),
                null,
                null,
                20
        );

        assertThat(response.content()).hasSize(1);
        assertThat(response.content().get(0).offerId()).isEqualTo(OFFER_ID);
        assertThat(response.content().get(0).canAccept()).isTrue();
        assertThat(response.content().get(0).canReject()).isTrue();
        assertThat(response.hasNext()).isFalse();
        assertThat(response.nextCursor()).isNull();
    }

    @Test
    void getReceivedPurchaseOffers_expiresSentBeforeQueryingList() {
        User owner = user(OWNER_ID, UserGrade.BASIC, null);
        User proposer = user(PROPOSER_ID, UserGrade.BASIC, null);
        Item item = item(ITEM_ID, owner);
        PurchaseOffer expiredOffer = offer(OFFER_ID, item, proposer, owner, LocalDateTime.now().minusMinutes(1));
        ReflectionTestUtils.setField(expiredOffer, "createdAt", LocalDateTime.now().minusMinutes(10));

        givenReceivedList(List.of(expiredOffer), List.of(expiredOffer));

        PurchaseOfferReceivedListResponse response = purchaseOfferService.getReceivedPurchaseOffers(
                OWNER_ID,
                List.of(PurchaseOfferStatus.EXPIRED),
                null,
                null,
                20
        );

        assertThat(expiredOffer.getStatus()).isEqualTo(PurchaseOfferStatus.EXPIRED);
        assertThat(expiredOffer.getPaymentAuthorizationStatus()).isEqualTo(PaymentAuthorizationStatus.RELEASED);
        assertThat(response.content()).hasSize(1);
        assertThat(response.content().get(0).canAccept()).isFalse();
        assertThat(response.content().get(0).canReject()).isFalse();
    }

    @Test
    void getReceivedPurchaseOffers_mapsTradeIdForAcceptedOffer() {
        User owner = user(OWNER_ID, UserGrade.BASIC, null);
        User proposer = user(PROPOSER_ID, UserGrade.BASIC, null);
        Item item = item(ITEM_ID, owner);
        PurchaseOffer acceptedOffer = offer(OFFER_ID, item, proposer, owner, LocalDateTime.now().plusHours(2));
        ReflectionTestUtils.setField(acceptedOffer, "status", PurchaseOfferStatus.ACCEPTED);
        ReflectionTestUtils.setField(acceptedOffer, "createdAt", LocalDateTime.now().minusMinutes(10));
        Trade trade = trade(900L, acceptedOffer.getId());

        givenReceivedList(List.of(), List.of(acceptedOffer));
        when(tradeRepository.findAllBySourceTypeAndSourceIdIn(
                TradeSourceType.PURCHASE_OFFER,
                List.of(acceptedOffer.getId())
        )).thenReturn(List.of(trade));

        PurchaseOfferReceivedListResponse response = purchaseOfferService.getReceivedPurchaseOffers(
                OWNER_ID,
                List.of(PurchaseOfferStatus.ACCEPTED),
                null,
                null,
                20
        );

        assertThat(response.content()).hasSize(1);
        assertThat(response.content().get(0).tradeId()).isEqualTo(900L);
    }

    @Test
    void rejectPurchaseOffer_rejectsSentOfferAndReleasesAuthorization() {
        User owner = user(OWNER_ID, UserGrade.BASIC, null);
        User proposer = user(PROPOSER_ID, UserGrade.BASIC, null);
        Item item = item(ITEM_ID, owner);
        PurchaseOffer offer = offer(OFFER_ID, item, proposer, owner, LocalDateTime.now().plusHours(1));

        givenOfferDetail(item, offer);

        PurchaseOfferRejectResponse response = purchaseOfferService.rejectPurchaseOffer(OFFER_ID, OWNER_ID);

        assertThat(response.offerId()).isEqualTo(OFFER_ID);
        assertThat(response.status()).isEqualTo(PurchaseOfferStatus.REJECTED);
        assertThat(response.paymentAuthorizationStatus()).isEqualTo(PaymentAuthorizationStatus.RELEASED);
        assertThat(response.serverTime()).isNotNull();
        assertThat(offer.getStatus()).isEqualTo(PurchaseOfferStatus.REJECTED);
        assertThat(offer.getPaymentAuthorizationStatus()).isEqualTo(PaymentAuthorizationStatus.RELEASED);

        InOrder lockOrder = inOrder(itemRepository, purchaseOfferRepository);
        lockOrder.verify(itemRepository).findByIdForUpdate(ITEM_ID);
        lockOrder.verify(purchaseOfferRepository).findByIdForUpdate(OFFER_ID);
    }

    @Test
    void rejectPurchaseOffer_rejectsNonOwner() {
        User owner = user(OWNER_ID, UserGrade.BASIC, null);
        User proposer = user(PROPOSER_ID, UserGrade.BASIC, null);
        Item item = item(ITEM_ID, owner);
        PurchaseOffer offer = offer(OFFER_ID, item, proposer, owner, LocalDateTime.now().plusHours(1));

        givenOfferDetail(item, offer);

        assertThatThrownBy(() -> purchaseOfferService.rejectPurchaseOffer(OFFER_ID, PROPOSER_ID))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(PurchaseOfferErrorCode.PURCHASE_OFFER_ACCESS_DENIED);
    }

    @Test
    void rejectPurchaseOffer_rejectsNonSentStatus() {
        User owner = user(OWNER_ID, UserGrade.BASIC, null);
        User proposer = user(PROPOSER_ID, UserGrade.BASIC, null);
        Item item = item(ITEM_ID, owner);
        PurchaseOffer offer = offer(OFFER_ID, item, proposer, owner, LocalDateTime.now().plusHours(1));
        ReflectionTestUtils.setField(offer, "status", PurchaseOfferStatus.ACCEPTED);

        givenOfferDetail(item, offer);

        assertThatThrownBy(() -> purchaseOfferService.rejectPurchaseOffer(OFFER_ID, OWNER_ID))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(PurchaseOfferErrorCode.PURCHASE_OFFER_NOT_SENT);
    }

    @Test
    void rejectPurchaseOffer_expiresPastDeadlineSentOfferBeforeRejecting() {
        User owner = user(OWNER_ID, UserGrade.BASIC, null);
        User proposer = user(PROPOSER_ID, UserGrade.BASIC, null);
        Item item = item(ITEM_ID, owner);
        PurchaseOffer offer = offer(OFFER_ID, item, proposer, owner, LocalDateTime.now().minusMinutes(1));

        givenOfferDetail(item, offer);

        assertThatThrownBy(() -> purchaseOfferService.rejectPurchaseOffer(OFFER_ID, OWNER_ID))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(PurchaseOfferErrorCode.PURCHASE_REQUEST_EXPIRED);
        assertThat(offer.getStatus()).isEqualTo(PurchaseOfferStatus.EXPIRED);
        assertThat(offer.getPaymentAuthorizationStatus()).isEqualTo(PaymentAuthorizationStatus.RELEASED);
    }

    private void givenOfferableItem(Item item, User proposer, Optional<PurchaseOffer> existingOffer) {
        when(itemRepository.findByIdForUpdate(ITEM_ID)).thenReturn(Optional.of(item));
        when(userRepository.findById(PROPOSER_ID)).thenReturn(Optional.of(proposer));
        when(purchaseOfferRepository.findByItemIdAndProposerIdForUpdate(ITEM_ID, PROPOSER_ID))
                .thenReturn(existingOffer);
    }

    private void givenOfferDetail(Item item, PurchaseOffer offer) {
        when(purchaseOfferRepository.findItemIdById(OFFER_ID)).thenReturn(Optional.of(ITEM_ID));
        when(itemRepository.findByIdForUpdate(ITEM_ID)).thenReturn(Optional.of(item));
        when(purchaseOfferRepository.findByIdForUpdate(OFFER_ID)).thenReturn(Optional.of(offer));
    }

    private void givenSentList(List<PurchaseOffer> expiredOffers, List<PurchaseOffer> fetchedOffers) {
        when(purchaseOfferRepository.findExpiredSentByProposerIdForUpdate(
                eq(PROPOSER_ID),
                eq(PurchaseOfferStatus.SENT),
                any(LocalDateTime.class)
        )).thenReturn(expiredOffers);
        when(purchaseOfferRepository.findSentListByProposerWithCursor(
                eq(PROPOSER_ID),
                any(),
                isNull(),
                isNull(),
                any(Pageable.class)
        )).thenReturn(fetchedOffers);
    }

    private void givenReceivedList(List<PurchaseOffer> expiredOffers, List<PurchaseOffer> fetchedOffers) {
        when(purchaseOfferRepository.findExpiredSentByOwnerIdForUpdate(
                eq(OWNER_ID),
                eq(PurchaseOfferStatus.SENT),
                any(LocalDateTime.class)
        )).thenReturn(expiredOffers);
        when(purchaseOfferRepository.findReceivedListByOwnerWithCursor(
                eq(OWNER_ID),
                any(),
                isNull(),
                isNull(),
                any(Pageable.class)
        )).thenReturn(fetchedOffers);
    }

    private PurchaseOfferCreateRequest request(Integer offerPrice, Boolean agreement) {
        return new PurchaseOfferCreateRequest(offerPrice, ADDRESS_ID, "MOCK_CARD", agreement);
    }

    private User user(Long id, UserGrade grade, Integer defaultShippingFee) {
        User user = instantiate(User.class);
        ReflectionTestUtils.setField(user, "id", id);
        ReflectionTestUtils.setField(user, "grade", grade);
        ReflectionTestUtils.setField(user, "username", "user" + id);
        ReflectionTestUtils.setField(user, "profileImageUrl", "https://image.url/profile-" + id + ".jpg");
        ReflectionTestUtils.setField(user, "defaultShippingFee", defaultShippingFee);
        return user;
    }

    private Item item(Long id, User owner) {
        Item item = instantiate(Item.class);
        ReflectionTestUtils.setField(item, "id", id);
        ReflectionTestUtils.setField(item, "owner", owner);
        ReflectionTestUtils.setField(item, "brandName", "Levis");
        ReflectionTestUtils.setField(item, "itemName", "LVC 1955 501");
        ReflectionTestUtils.setField(item, "representativeImageUrl", "https://image.url/item.jpg");
        ReflectionTestUtils.setField(item, "itemStatus", ItemStatus.OWNED);
        ReflectionTestUtils.setField(item, "purchaseOfferEnabled", true);
        return item;
    }

    private DeliveryAddress address(Long id, User user) {
        DeliveryAddress address = instantiate(DeliveryAddress.class);
        ReflectionTestUtils.setField(address, "id", id);
        ReflectionTestUtils.setField(address, "user", user);
        ReflectionTestUtils.setField(address, "receiverName", "Proposer");
        ReflectionTestUtils.setField(address, "phone", "010-1234-5678");
        ReflectionTestUtils.setField(address, "addressLine1", "Seoul Gangnam");
        ReflectionTestUtils.setField(address, "addressLine2", "101");
        ReflectionTestUtils.setField(address, "postalCode", "12345");
        ReflectionTestUtils.setField(address, "requestNote", "Leave at door");
        return address;
    }

    private PurchaseOffer offer(
            Long id,
            Item item,
            User proposer,
            User owner,
            LocalDateTime expiresAt
    ) {
        PurchaseOffer offer = PurchaseOffer.create(
                item,
                proposer,
                owner,
                address(ADDRESS_ID, proposer),
                360000,
                5000,
                21600,
                new BigDecimal("0.0600"),
                386600,
                365000,
                "mock_offer_auth_existing",
                "MOCK_CARD",
                expiresAt
        );
        ReflectionTestUtils.setField(offer, "id", id);
        return offer;
    }

    private Trade trade(Long id, Long sourceId) {
        Trade trade = instantiate(Trade.class);
        ReflectionTestUtils.setField(trade, "id", id);
        ReflectionTestUtils.setField(trade, "sourceType", TradeSourceType.PURCHASE_OFFER);
        ReflectionTestUtils.setField(trade, "sourceId", sourceId);
        return trade;
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
