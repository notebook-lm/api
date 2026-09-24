package vn.edu.fsoftacademy.api.shared.pagination;

import java.util.List;

public record PageResult<T>(
                List<T> items,
                int page,
                int size,
                long totalItems,
                int totalPages,
                boolean hasNext,
                boolean hasPrevious) {
}
