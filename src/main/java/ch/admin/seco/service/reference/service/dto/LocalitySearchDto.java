package ch.admin.seco.service.reference.service.dto;

public class LocalitySearchDto {

    private final String query;
    private final int size;
    private final boolean distinctLocalities;

    public LocalitySearchDto(String query, int size, boolean distinctLocalities) {
        this.query = query;
        this.size = size;
        this.distinctLocalities = distinctLocalities;
    }

    public String getQuery() {
        return query;
    }

    public int getSize() {
        return size;
    }

    public boolean isDistinctLocalities() {
        return distinctLocalities;
    }
}
