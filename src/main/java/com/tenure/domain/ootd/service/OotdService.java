package com.tenure.domain.ootd.service;

import com.tenure.domain.ootd.dto.OotdCreateResponse;
import com.tenure.domain.ootd.entity.Ootd;
import com.tenure.domain.ootd.enums.OotdSource;
import com.tenure.domain.ootd.exception.OotdErrorCode;
import com.tenure.domain.ootd.repository.OotdRepository;
import com.tenure.domain.user.entity.User;
import com.tenure.domain.user.repository.UserRepository;
import com.tenure.global.exception.CommonErrorCode;
import com.tenure.global.exception.CustomException;
import com.tenure.global.storage.ImageStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class OotdService {

    private static final String OOTD_IMAGE_DIRECTORY = "ootds";

    private final OotdRepository ootdRepository;
    private final UserRepository userRepository;
    private final ImageStorageService imageStorageService;

    @Transactional
    public OotdCreateResponse createOotd(Long currentUserId, MultipartFile image, String source) {
        validateImage(image);
        OotdSource ootdSource = validateSource(source);

        User owner = userRepository.findById(currentUserId)
                .orElseThrow(() -> new CustomException(CommonErrorCode.UNAUTHORIZED));

        String imageUrl = imageStorageService.store(image, OOTD_IMAGE_DIRECTORY);

        Ootd ootd = Ootd.create(owner, imageUrl, ootdSource);
        ootdRepository.save(ootd);

        // TODO: AI 연동 시 OOTD_CREATED 이벤트 발행으로 전환하여 비동기 태그 분석 트리거
        log.info("OOTD_CREATED - ootdId={}, ownerId={}, imageUrl={}", ootd.getId(), owner.getId(), imageUrl);

        return OotdCreateResponse.of(ootd);
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
