package com.tenure.domain.chat.service;

import com.tenure.domain.chat.dto.response.ChatMessageCursorResponse;
import com.tenure.domain.chat.dto.response.ChatReadEvent;
import com.tenure.domain.chat.dto.response.ChatRoomListCursorResponse;
import com.tenure.domain.chat.dto.response.ChatRoomResponse;
import com.tenure.domain.chat.entity.ChatMessage;
import com.tenure.domain.chat.entity.ChatRoom;
import com.tenure.domain.chat.entity.ChatRoomMember;
import com.tenure.domain.chat.enums.ChatRoomFilterType;
import com.tenure.domain.chat.exception.ChatErrorCode;
import com.tenure.domain.chat.repository.ChatMessageRepository;
import com.tenure.domain.chat.repository.ChatRoomMemberRepository;
import com.tenure.domain.chat.repository.ChatRoomRepository;
import com.tenure.domain.item.entity.Item;
import com.tenure.domain.item.exception.ItemErrorCode;
import com.tenure.domain.item.repository.ItemRepository;
import com.tenure.domain.notification.entity.Notification;
import com.tenure.domain.notification.enums.NotificationType;
import com.tenure.domain.notification.repository.NotificationRepository;
import com.tenure.domain.product.entity.Product;
import com.tenure.domain.product.exception.ProductErrorCode;
import com.tenure.domain.product.repository.ProductRepository;
import com.tenure.domain.purchase.enums.PurchaseIntentStatus;
import com.tenure.domain.purchase.enums.PurchaseOfferStatus;
import com.tenure.domain.purchase.repository.PurchaseIntentRepository;
import com.tenure.domain.purchase.repository.PurchaseOfferRepository;
import com.tenure.domain.trade.entity.Trade;
import com.tenure.domain.trade.repository.TradeRepository;
import com.tenure.domain.user.entity.User;
import com.tenure.domain.user.entity.UserBlock;
import com.tenure.domain.user.exception.UserErrorCode;
import com.tenure.domain.user.repository.UserBlockRepository;
import com.tenure.domain.user.repository.UserRepository;
import com.tenure.global.exception.CustomException;
import com.tenure.global.storage.ImageStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static com.tenure.domain.product.enums.ProductStatus.*;
import static com.tenure.domain.purchase.enums.PurchaseOfferStatus.*;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final UserBlockRepository userBlockRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final TradeRepository tradeRepository;
    private final PurchaseIntentRepository purchaseIntentRepository;
    private final PurchaseOfferRepository purchaseOfferRepository;
    private final ImageStorageService localImageStoreService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final NotificationRepository notificationRepository;

    // 채팅 이미지 전송시 허용되는 형식
    private static final List<String> ALLOWED_IMAGE_TYPES = List.of(
            "image/jpeg", "image/png", "image/gif", "image/webp", "image/heic"
    );

    // 채팅방 조회 / 생성
    @Transactional
    public ChatRoomResponse findOrCreateChatRoom(Long buyerId, Long itemId) {

        log.info("[채팅방 생성/조회] buyerId = {}, itemId = {}", buyerId, itemId);

        //아이템 + 주인 한 번에 조회
        Item item = itemRepository.findByIdWithOwner(itemId)
                .orElseThrow(() -> {
                    log.warn("[채팅방 생성/조회] 해당 아이템은 존재하지 않습니다. itemId = {}", itemId);
                    return new CustomException(ItemErrorCode.ITEM_NOT_FOUND);
                });

        User owner = item.getOwner();

        //구매자(사용자) 조회
        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> {
                    log.warn("[채팅방 생성/조회] 해당 구매자는 존재하지 않습니다. buyerId = {}", buyerId);
                    return new CustomException(UserErrorCode.USER_NOT_FOUND);
                });

        if(buyerId.equals(owner.getId())) {
            log.warn("[채팅방 생성 / 조회] 본인의 아이템에는 채팅방을 생성 할 수없습니다. buyerId = {}, ownerId = {}", buyerId, owner.getId());
            throw new CustomException(ChatErrorCode.CHAT_CREATION_NOT_ALLOWED);
        }

        // 양방향 차단 한 번에 조회
        List<UserBlock> blocks = userBlockRepository.findBlocksBetween(buyerId, owner.getId());
        boolean buyerBlockedOwner = blocks.stream()
                .anyMatch(b -> b.getBlocker().getId().equals(buyerId));

        boolean ownerBlockedBuyer = blocks.stream()
                .anyMatch(b -> b.getBlocker().getId().equals(owner.getId()));

        if (buyerBlockedOwner) {
            log.warn("[채팅방 생성 / 조회] 차단된 사용자와는 채팅 할 수 없습니다.");
            throw new CustomException(ChatErrorCode.CHAT_BLOCKED);
        }


        //product 중에서 해당 아이템이 판매중이거나 거래중인 항목을 찾음
        Product product = productRepository.findByItemIdAndProductStatusIn(itemId, List.of(ON_SALE, TRADING))
                .orElseGet(() -> {

                    // 미판매 or 판매완료 상품인 경우
                    // 미판매 상품 + 구매제안 = 채팅방 생성 가능
                    boolean hasSentOffer = purchaseOfferRepository
                            .findIdByProposerIdAndOwnerIdAndItemIdAndStatus(buyerId, owner.getId(), itemId, SENT)
                            .isPresent();

                    if(!hasSentOffer) {
                        log.warn("[채팅방 생성/조회] 해당 상품은 미판매 상품입니다. itemId = {}", itemId);
                        throw new CustomException(ProductErrorCode.PRODUCT_NOT_ON_SALE);
                    }
                    Product offerSentProduct = productRepository.findByItemId(itemId)
                            .orElseThrow(() -> {
                                log.warn("[채팅방 생성/조회] 상품을 찾을 수 없습니다. itemId = {}", itemId);
                                return new CustomException(ProductErrorCode.PRODUCT_NOT_FOUND);
                            });

                    // 상품이 판매 완료 된 경우
                    if(offerSentProduct.getProductStatus() == SOLD) {
                        log.warn("[채팅방 생성/조회] 해당 상품은 판매 완료된 상품입니다. itemId = {}", itemId);
                        throw new CustomException(ProductErrorCode.PRODUCT_NOT_ON_SALE);
                    }

                    // 미판매 + 구매제안
                    return offerSentProduct;
                });

        ChatRoom  chatRoom;

        // 사용자가 동시에 채팅방 생성을 할 경우 방지
        try {
            //체팅방을 조회(닫히지 않은 채팅방) / 없으면 새로 만든 후 저장
            chatRoom = chatRoomRepository.findByItemIdAndSellerIdAndBuyerIdAndIsClosedFalse(itemId, owner.getId(), buyerId)
                    .orElseGet(() -> createChatRoom(item, buyer, owner));
        } catch (DataIntegrityViolationException e) {
            chatRoom = chatRoomRepository.findByItemIdAndSellerIdAndBuyerIdAndIsClosedFalse(itemId, owner.getId(), buyerId)
                    .orElseThrow(() -> {
                        log.warn("[채팅방 생성/조회] 채팅방을 찾을 수 없습니다. itemId = {}, ownerId = {}, buyerId = {}", itemId, owner.getId(), buyerId );
                        return new CustomException(ChatErrorCode.CHAT_ROOM_NOT_FOUND);
                    });
        }



        Long tradeId = tradeRepository.findByItemId(itemId).map(Trade::getId).orElse(null);

        Long purchaseIntentId = purchaseIntentRepository
                .findIdByBuyerIdAndSellerIdAndProductIdAndStatus(buyerId, owner.getId(), product.getId(), PurchaseIntentStatus.SENT)
                .orElse(null);

        Long purchaseOfferId = purchaseOfferRepository
                .findIdByProposerIdAndOwnerIdAndItemIdAndStatus(buyerId, owner.getId(), itemId, SENT)
                .orElse(null);

        // 아이템 상세에서 바로 들어온 경우 처음엔 isOpponentExited false 고정
        return ChatRoomResponse
                .from(chatRoom, item, product, buyerId, tradeId, purchaseIntentId, purchaseOfferId, ownerBlockedBuyer, false);
    }

    // 채팅방 목록 조회
    public ChatRoomListCursorResponse chatRoomList(Long currentUserId, ChatRoomFilterType type,
                             LocalDateTime cursor, LocalDateTime createdAtCursor, Long cursorId, int size)
    {

        // 연락을 한번도 안한 채팅방이 cursor의 경계에 걸리게 된 경우
        if(cursor == null && cursorId == null) {
            cursor = LocalDateTime.now();
            cursorId = Long.MAX_VALUE;
        } else if(cursorId == null) { //cursor는 그냥 null로 지정
            cursorId = Long.MAX_VALUE;
        }

        // null lastMessageAt 영역 커서: 없으면 now() (진입 전 = 전체 포함)
        if(createdAtCursor == null) createdAtCursor = LocalDateTime.now();

        log.info("[채팅방 목록 조회] currentUserId = {}, type = {}, cursor = {}, createdAtCursor = {}, cursorId = {}, size = {}", currentUserId, type, cursor, createdAtCursor, cursorId, size);

        PageRequest pageRequest = PageRequest.of(0, size);

        Slice<ChatRoomMember> chatRooms = switch (type) {
            case BUYING -> chatRoomMemberRepository.findBuyingChatRooms(currentUserId, cursor, createdAtCursor, cursorId, pageRequest);
            case SELLING -> chatRoomMemberRepository.findSellingChatRooms(currentUserId, cursor, createdAtCursor, cursorId, pageRequest);
            case UNREAD -> chatRoomMemberRepository.findUnreadChatRooms(currentUserId, cursor, createdAtCursor, cursorId, pageRequest);
            default -> chatRoomMemberRepository.findAllChatRooms(currentUserId, cursor, createdAtCursor, cursorId, pageRequest); //기본 전체 조회
        };

        log.info("[채팅방 목록 조회] 조회 결과 = {}건, hasNext = {}", chatRooms.getContent().size(), chatRooms.hasNext());
        return ChatRoomListCursorResponse.from(chatRooms, currentUserId);
    }

    //채팅방 목록에서 채팅방 접속
    @Transactional
    public ChatRoomResponse enterChatroom(Long currentUserId, Long chatRoomId) {

        log.info("[채팅방 진입] currentUserId = {}, chatRoomId = {}", currentUserId, chatRoomId);

        // 채팅방 존재 확인
        ChatRoom chatRoom = chatRoomRepository.findByIdWithItem(chatRoomId)
                .orElseThrow(() -> {
                    log.warn("[채팅방 조회] 채팅방을 찾을 수 없습니다. chatRoomId = {}", chatRoomId);
                    return new CustomException(ChatErrorCode.CHAT_ROOM_NOT_FOUND);
                });

        // 채팅방 권한 체크
        if(!chatRoomMemberRepository.existsByUserIdAndChatRoomIdAndIsExitedFalse(currentUserId, chatRoomId)) {
            log.warn("[채팅방 조회] 채팅방 접근 권한이 없습니다. currentUserId = {}, chatRoomId = {}", currentUserId, chatRoomId);
            throw  new CustomException(ChatErrorCode.CHAT_FORBIDDEN);
        }

        Item item = chatRoom.getItem();

        //해당 아이템의 product 조회
        Product product = productRepository.findByItemId(item.getId())
                .orElseThrow(() -> {
                    log.warn("[채팅방 조회] 상품을 찾을 수 없습니다. itemId = {}", item.getId());
                    return new CustomException(ProductErrorCode.PRODUCT_NOT_FOUND);
                });

        //아이템에 대한 거래가 성사됐는지 판단
        Long tradeId = tradeRepository
                .findByItemId(item.getId()).map(Trade::getId).orElse(null);

        Long buyerId = chatRoom.getBuyer().getId();
        Long sellerId = chatRoom.getSeller().getId();
        Long opponentId = currentUserId.equals(buyerId) ? sellerId : buyerId;

        // 해당 상품에 대해 거래 의사를 보낸 적이 있는가
        Long purchaseIntentId = purchaseIntentRepository
                .findIdByBuyerIdAndSellerIdAndProductIdAndStatus(buyerId, sellerId, product.getId(), PurchaseIntentStatus.SENT)
                .orElse(null);

        // 해당 아이템에 대해 구매 제안을 보낸 적이 있는가
        Long purchaseOfferId = purchaseOfferRepository
                .findIdByProposerIdAndOwnerIdAndItemIdAndStatus(buyerId, sellerId, item.getId(), SENT)
                .orElse(null);

        // 상대방이 나를 차단했는지 여부
        boolean isBlocked = userBlockRepository.isBlocked(opponentId, currentUserId);

        // 상대방이 채팅방을 나갔는지 여부
        boolean isOpponentExited = chatRoomMemberRepository.findByUserIdAndChatRoomId(opponentId, chatRoomId)
                .map(ChatRoomMember::isExited)
                .orElse(false);

        // 채팅방 접속 시 앓림이 있다면 읽음 처리, 아니면 넘어감
        notificationRepository.findByReceiverIdAndTargetIdAndType(currentUserId, chatRoomId, NotificationType.CHAT_MESSAGE_CREATED)
                .ifPresent(Notification::markRead);

        return ChatRoomResponse
                .from(chatRoom, item, product, currentUserId, tradeId, purchaseIntentId, purchaseOfferId, isBlocked, isOpponentExited);
    }

    //채팅방 접속 시 unreadCount 업데이트
    @Transactional
    public void updateRead(Long currentUserId, Long chatRoomId) {

        log.info("[읽음 처리] currentUserId = {}, chatRoomId = {}", currentUserId, chatRoomId);

        //채팅방이 없으면
        if (!chatRoomRepository.existsById(chatRoomId)) {
            log.warn("[읽음 처리] 채팅방을 찾을 수 없습니다. chatRoomId = {}", chatRoomId);
            throw new CustomException(ChatErrorCode.CHAT_ROOM_NOT_FOUND);
        }

        // 해당 채팅방 멤버가 아니면
        ChatRoomMember chatRoomMember = chatRoomMemberRepository.findByUserIdAndChatRoomId(currentUserId, chatRoomId)
                .orElseThrow(() -> {
                    log.warn("[읽음 처리] 채팅방 접근 권한이 없습니다. currentUserId = {}, chatRoomId = {}", currentUserId, chatRoomId);
                    return new CustomException(ChatErrorCode.CHAT_FORBIDDEN);
                });

        // 해당 채팅방의 가장 최근 메시지 조회
        ChatMessage chatMessage = chatMessageRepository.findByRecentMessage(chatRoomId)
                .orElse(null);

        chatRoomMember.updateLastRead(chatMessage);

        // 트랜잭션 커밋 이후에만 broadcast (커밋 전 전송 시 수신자가 조회해도 DB 미반영 문제 방지)
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                simpMessagingTemplate.convertAndSend(
                        "/sub/chats/" + chatRoomId,
                        new ChatReadEvent(currentUserId)
                );
            }
        });
    }

    // 채팅 내역 조회
    public ChatMessageCursorResponse getMessages(Long currentUserId, Long chatRoomId
            , LocalDateTime cursor, Long cursorId, int size) {

        if(cursor == null) cursor = LocalDateTime.now();
        if(cursorId == null) cursorId = Long.MAX_VALUE;

        log.info("[채팅 내역 조회] currentUserId = {}, chatRoomId = {}", currentUserId, chatRoomId);

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> {
                    log.warn("[채팅 내역 조회] 채팅방을 찾을 수 없습니다. chatRoomId = {}", chatRoomId);
                    return new CustomException(ChatErrorCode.CHAT_ROOM_NOT_FOUND);
                });

        // 현재 사용자가 해당 채팅방을 나가지 않았는가
        if (!chatRoomMemberRepository.existsByUserIdAndChatRoomIdAndIsExitedFalse(currentUserId, chatRoomId)) {
            log.warn("[채팅 내역 조회] 해당 채팅방에 접근 권한이 없습니다. currentUserId = {}, chatRoomId = {}", currentUserId, chatRoomId);
            throw new CustomException(ChatErrorCode.CHAT_FORBIDDEN);
        }

        Long opponentId = Objects.equals(chatRoom.getSeller().getId(), currentUserId) ?
                chatRoom.getBuyer().getId() : chatRoom.getSeller().getId();

        ChatRoomMember opponentMember = chatRoomMemberRepository.findByUserIdAndChatRoomId(opponentId, chatRoomId)
                .orElseThrow(() -> new CustomException(ChatErrorCode.CHAT_FORBIDDEN));

        // 상대방이 마지막으로 읽은 메시지 Id
        long opponentLastReadMessageId = opponentMember.getLastReadMessage() != null ?
                opponentMember.getLastReadMessage().getId() : 0L;

        PageRequest request = PageRequest.of(0, size);

        Slice<ChatMessage> chatMessages = chatMessageRepository
                .findByChatMessages(chatRoomId, cursor, cursorId, request);

        log.info("[채팅 내역 조회] 채팅 내역 조회 성공");
        return ChatMessageCursorResponse.from(chatMessages, currentUserId, opponentLastReadMessageId);
    }

    //채팅방 생성 매서드
    private ChatRoom createChatRoom(Item item, User buyer, User owner) {
        ChatRoom newRoom = chatRoomRepository.save(ChatRoom.of(item, buyer, owner));
        chatRoomMemberRepository.save(ChatRoomMember.of(newRoom, buyer));
        chatRoomMemberRepository.save(ChatRoomMember.of(newRoom, owner));
        return newRoom;
    }


    //채팅 이미지 업로드
    public List<String> uploadImage(Long currentUserId, Long chatRoomId, List<MultipartFile> images) {

        log.info("[채팅 이미지 업로드] currentUserId = {}, chatRoomId = {}, 이미지 수 = {}", currentUserId, chatRoomId, images.size());

        if(!chatRoomRepository.existsById(chatRoomId)) {
            log.warn("[채팅 이미지 업로드] 채팅방을 찾을 수 없습니다. chatRoomId = {}", chatRoomId);
            throw new CustomException(ChatErrorCode.CHAT_ROOM_NOT_FOUND);
        }

        if(!chatRoomMemberRepository.existsByUserIdAndChatRoomIdAndIsExitedFalse(currentUserId, chatRoomId)) {
            log.warn("[채팅 이미지 업로드] 채팅방 접근 권한이 없습니다. currentUserId = {}, chatRoomId = {}", currentUserId, chatRoomId);
            throw new CustomException(ChatErrorCode.CHAT_FORBIDDEN);
        }

        images.forEach(image -> {
            if(!ALLOWED_IMAGE_TYPES.contains(image.getContentType())) {
                log.warn("[채팅 이미지 업로드] 지원하지 않는 이미지 형식입니다. contentType = {}", image.getContentType());
                throw new CustomException(ChatErrorCode.INVALID_IMAGE_TYPE);
            }
        });

        // 반환 url: /files/chat/{chatRoomId}/{UUID}.확장자
        return images.stream().map(image ->
                localImageStoreService.store(image, "chat/" + chatRoomId)).toList();

    }

    // 채팅방 나가기
    @Transactional
    public void exitChatRoom(Long currentUserId, Long chatRoomId) {

        log.info("[채팅방 나가기] currentUserId = {}, chatRoomId = {}", currentUserId, chatRoomId);

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> {
                    log.warn("[채팅방 나가기] 해당 채팅방을 찾을 수 없습니다. chatRoomId = {}", chatRoomId);
                    return new CustomException(ChatErrorCode.CHAT_ROOM_NOT_FOUND);
                });


        ChatRoomMember chatRoomMember = chatRoomMemberRepository.findByUserIdAndChatRoomId(currentUserId, chatRoomId)
                .orElseThrow(() -> {
                    log.warn("[채팅방 나가기] 해당 채팅방에 접근 할 수 없습니다. chatRoomId = {}, currentUserId = {}", chatRoomId, currentUserId);
                    return new CustomException(ChatErrorCode.CHAT_FORBIDDEN);
                });

        // 이미 채팅방을 나간 상태라면
        if (chatRoomMember.isExited()) {
            return;
        }

        // 마지막 읽은 메시지 업데이트
        chatMessageRepository.findByRecentMessage(chatRoomId)
                .ifPresent(chatRoomMember :: updateLastRead);

        // 채팅방 나감 처리
        chatRoomMember.exit();
        chatRoom.close();

        log.info("[채팅방 나가기 완료] currentUserId = {}, chatRoomId = {}", currentUserId, chatRoomId);
    }
}
