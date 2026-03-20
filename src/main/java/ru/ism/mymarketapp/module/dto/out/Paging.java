package ru.ism.mymarketapp.module.dto.out;

public record Paging(int pageSize,
                     int pageNumber,
                     boolean hasPrevious,
                     boolean hasNext) {
}
