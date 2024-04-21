package org.sight.jooqstart.config.converter;

import org.jooq.impl.EnumConverter;
import org.sight.jooqstart.film.FilmSummary;

import java.util.function.Function;

public class PriceCategoryConverter extends EnumConverter<String, FilmSummary.PriceCategory>{

    public PriceCategoryConverter() {
        super(String.class, FilmSummary.PriceCategory.class, FilmSummary.PriceCategory::getCode);
    }
}
