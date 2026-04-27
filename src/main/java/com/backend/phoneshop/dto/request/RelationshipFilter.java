package com.backend.phoneshop.dto.request;

import lombok.Builder;

@Builder
public record RelationshipFilter(
        String idKey,
        String entityKey
) {
    public RelationshipFilter {
        if (idKey == null || idKey.isBlank()) {
            idKey = "id";
        }
    }
}
