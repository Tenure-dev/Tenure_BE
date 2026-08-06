package com.tenure.global.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageDeletionService {

    private final ImageStorageService imageStorageService;

    public void deleteAfterCommit(String objectKey) {
        if (objectKey == null || objectKey.isBlank()) {
            return;
        }
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            deleteNow(objectKey);
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                deleteNow(objectKey);
            }
        });
    }

    public void deleteNow(String objectKey) {
        if (objectKey == null || objectKey.isBlank()) {
            return;
        }
        try {
            imageStorageService.delete(objectKey);
        } catch (RuntimeException e) {
            log.warn("이미지 원본 삭제 실패 - objectKey={}", objectKey, e);
        }
    }
}
