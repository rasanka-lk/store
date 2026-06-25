package com.example.store.dto;

import java.util.List;

public record CursorPageResponse<T>(List<T> data, int pageSize, Long nextCursor, boolean hasNext) {}
