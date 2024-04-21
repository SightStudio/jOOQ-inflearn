package org.sight.jooqstart.config.converter;

import org.jooq.impl.EnumConverter;
import org.sight.jooqstart.film.FilmPriceSummary;

public class PriceCategoryConverter extends EnumConverter<String, FilmPriceSummary.PriceCategory>{

    public PriceCategoryConverter() {
        super(String.class, FilmPriceSummary.PriceCategory.class, FilmPriceSummary.PriceCategory::getCode);
    }
}
