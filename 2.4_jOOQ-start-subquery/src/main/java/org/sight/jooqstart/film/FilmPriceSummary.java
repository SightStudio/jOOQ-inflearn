package org.sight.jooqstart.film;

import lombok.Getter;

@Getter
public class FilmPriceSummary {

    private Long filmId;
    private String title;
    private PriceCategory priceCategory;

    @Getter
    public enum PriceCategory {
        CHEAP("Cheap"),
        NORMAL("Normal"),
        EXPENSIVE("Expensive")
        ;

        private final String code;

        PriceCategory(String code) {
            this.code = code;
        }

        public static PriceCategory findByCode (String code) {
            for (PriceCategory value : values()) {
                if (value.code.equalsIgnoreCase(code)) {
                    return value;
                }
            }
            return null;
        }
    }
}
