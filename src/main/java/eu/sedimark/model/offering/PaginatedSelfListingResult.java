package eu.sedimark.model.offering;

import lombok.Getter;

@Getter
public class PaginatedSelfListingResult<T> {
    private final T data;
    private final int totalCount;

    public PaginatedSelfListingResult(T data, int totalCount) {
        this.data = data;
        this.totalCount = totalCount;
    }

}