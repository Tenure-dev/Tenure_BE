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
import com.tenure.global.storage.ImageStorageService;
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
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public OotdCreateResponse createAutoTagOotd(
            Long currentUserId,
            MultipartFile image,
            String source
    ) {
        Ootd ootd = createOotdEntity(currentUserId, image, source);

        eventPublisher.publishEvent(new OotdCreatedEvent(ootd.getId(), ootd.getOwner().getId(), ootd.getImageUrl()));

        return OotdCreateResponse.of(ootd);
    }

    // 태그 작성 화면에서 사용자가 박스를 그릴 때마다 별도 분석 API를 호출하는 흐름이라,
    // 게시 시점에는 자동 분석을 트리거하지 않는다.
    @Transactional
    public OotdCreateResponse createManualTagOotd(
            Long currentUserId,
            MultipartFile image,
            String source
    ) {
        Ootd ootd = createOotdEntity(currentUserId, image, source);

        return OotdCreateResponse.of(ootd);
    }

    private Ootd createOotdEntity(Long currentUserId, MultipartFile image, String source) {
        validateImage(image);
        OotdSource ootdSource = validateSource(source);

        User owner = userRepository.findById(currentUserId)
                .orElseThrow(() -> new CustomException(CommonErrorCode.UNAUTHORIZED));

        String imageUrl = imageStorageService.store(image, OOTD_IMAGE_DIRECTORY);

        Ootd ootd = Ootd.create(owner, imageUrl, ootdSource);
        ootdRepository.save(ootd);
        return ootd;
    }

    @Transactional
    public void deleteOotd(Long currentUserId, Long ootdId) {
        Ootd ootd = ootdRepository.findVisibleActiveById(ootdId, currentUserId, OotdPublicationStatus.ACTIVE)
                .orElseThrow(() -> new CustomException(OotdErrorCode.OOTD_NOT_FOUND));

        validateOwner(ootd, currentUserId);

        ootd.delete();
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
