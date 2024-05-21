package org.sight.jooqstart.film;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jooq.generated.tables.pojos.Film;
import org.jooq.generated.tables.pojos.Inventory;
import org.jooq.generated.tables.pojos.Rental;

@Getter
@RequiredArgsConstructor
public class FilmInventoryRental {
    private final Film film;
    private final Inventory inventory;
    private final Rental rental;
}