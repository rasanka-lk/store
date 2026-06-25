package com.example.store.util;

import com.example.store.dto.CursorPageResponse;

import java.util.List;
import java.util.function.Function;

public final class CursorPaginationUtil {

    private CursorPaginationUtil() {}

    public static <T, R> CursorPageResponse<R> buildResponse(
            List<T> items, int size, Function<T, Long> cursorExtractor, Function<List<T>, List<R>> mapper) {
        boolean hasNext = items.size() == size;
        Long nextCursor = hasNext ? cursorExtractor.apply(items.get(items.size() - 1)) : null;

        return new CursorPageResponse<>(mapper.apply(items), size, nextCursor, hasNext);
    }
}
