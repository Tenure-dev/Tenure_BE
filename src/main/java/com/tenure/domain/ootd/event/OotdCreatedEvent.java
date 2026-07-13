package com.tenure.domain.ootd.event;

public record OotdCreatedEvent(Long ootdId, Long ownerId, String imageUrl) {
}
