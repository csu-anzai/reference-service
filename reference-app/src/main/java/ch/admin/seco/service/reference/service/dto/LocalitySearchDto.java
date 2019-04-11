package ch.admin.seco.service.reference.service.dto;

public class LocalitySearchDto {

    private final String query;
    private final int size;
    private final boolean distinctByLocalityCity;

    public LocalitySearchDto(String query, int size, boolean distinctByLocalityCity) {
        this.query = query;
        this.size = size;
        this.distinctByLocalityCity = distinctByLocalityCity;
    }

    public String getQuery() {
        return query;
    }

    public int getSize() {
        return size;
    }

    public boolean isDistinctByLocalityCity() {
        return distinctByLocalityCity;
    }
}
