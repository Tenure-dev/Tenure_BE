package com.tenure.domain.ootd.service;

import com.tenure.domain.ootd.dto.OotdCreateResponse;
import com.tenure.domain.ootd.entity.Ootd;
import com.tenure.domain.ootd.enums.OotdPublicationStatus;
import com.tenure.domain.ootd.enums.OotdSource;
import com.tenure.domain.ootd.event.OotdCreatedEvent;
import com.tenure.domain.ootd.exception.OotdErrorCode;
import com.tenure.domain.ootd.repository.OotdRepository;
import com.tenure.domain.user.entity.User;
import com.tenure.domain.user.repository.UserRepository;
import com.tenure.global.exception.CommonErrorCode;
import com.tenure.global.exception.CustomException;
import com.tenure.global.storage.ImageDeletionService;
import com.tenure.global.storage.ImageStorageService;
import com.tenure.global.storage.StoredImage;
import com.tenure.global.storage.validation.ImageValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class OotdService {

    private static final String OOTD_IMAGE_DIRECTORY = "ootds";

    private final OotdRepository ootdRepository;
    private final UserRepository userRepository;
    private final ImageStorageService imageStorageService;
    private final ImageDeletionService imageDeletionService;
    private final ImageValidator imageValidator;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public OotdCreateResponse createAutoTagOotd(
            Long currentUserId,
            MultipartFile image,
            String source
    ) {
        Ootd ootd = createOotdEntity(currentUserId, image, source, false);

        eventPublisher.publishEvent(new OotdCreatedEvent(
                ootd.getId(),
                ootd.getOwner().getId(),
                ootd.getImageUrl(),
                ootd.getImageObjectKey()
        ));

        return OotdCreateResponse.of(ootd);
    }

    // 태그 작성 화면에서 사용자가 박스를 그릴 때마다 별도 분석 API를 호출하는 흐름이라,
    // 게시 시점에는 자동 분석을 트리거하지 않는다.
    // 태그를 다 작성하기 전까지는 다른 사용자에게 노출되면 안 되므로 임시 비공개(ARCHIVED)로 생성하고,
    // 사용자가 POST /ootds/{ootdId}/tags/confirm을 호출해야 비로소 공개(ACTIVE)로 전환된다.
    @Transactional
    public OotdCreateResponse createManualTagOotd(
            Long currentUserId,
            MultipartFile image,
            String source
    ) {
        Ootd ootd = createOotdEntity(currentUserId, image, source, true);

        return OotdCreateResponse.of(ootd);
    }

    private Ootd createOotdEntity(Long currentUserId, MultipartFile image, String source, boolean archived) {
        validateImage(image);
        imageValidator.validateGeneralImage(image);
        OotdSource ootdSource = validateSource(source);

        User owner = userRepository.findById(currentUserId)
                .orElseThrow(() -> new CustomException(CommonErrorCode.UNAUTHORIZED));

        StoredImage storedImage = imageStorageService.storeImage(image, OOTD_IMAGE_DIRECTORY);

        Ootd ootd = archived
                ? Ootd.createArchived(owner, storedImage.url(), storedImage.objectKey(), ootdSource)
                : Ootd.create(owner, storedImage.url(), storedImage.objectKey(), ootdSource);
        ootdRepository.save(ootd);
        return ootd;
    }

    @Transactional
    public void deleteOotd(Long currentUserId, Long ootdId) {
        Ootd ootd = ootdRepository.findVisibleActiveById(ootdId, currentUserId, OotdPublicationStatus.ACTIVE)
                .orElseThrow(() -> new CustomException(OotdErrorCode.OOTD_NOT_FOUND));

        validateOwner(ootd, currentUserId);

        ootd.delete();
        imageDeletionService.deleteAfterCommit(ootd.getImageObjectKey());
    }

    private void validateOwner(Ootd ootd, Long currentUserId) {
        if (!ootd.getOwner().getId().equals(currentUserId)) {
            throw new CustomException(OotdErrorCode.OOTD_OWNER_ONLY);
        }
    }

    private void validateImage(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new CustomException(OotdErrorCode.OOTD_IMAGE_REQUIRED);
        }
    }

    private OotdSource validateSource(String source) {
        if (!OotdSource.CAMERA.name().equalsIgnoreCase(source)) {
            throw new CustomException(OotdErrorCode.OOTD_SOURCE_INVALID);
        }
        return OotdSource.CAMERA;
    }
}
