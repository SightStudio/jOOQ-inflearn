package org.sight.jooqstart.film;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class FilmPriceSummary {
    private Long filmId;
    private String title;
    private String priceCategory;
    private BigDecimal rentalRate;
    private Long totalInventory;
}
