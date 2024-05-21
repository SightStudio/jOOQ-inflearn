package org.sight.jooqstart.film;

import lombok.Getter;
import org.jooq.generated.tables.pojos.Film;
import org.jooq.generated.tables.pojos.Inventory;
import org.jooq.generated.tables.pojos.Rental;

import java.util.List;
import java.util.Map;

@Getter
public class FilmInventorySummary {

    private final Film film;
    private final List<InventoryRental> inventoryList;

    public FilmInventorySummary(Map.Entry<Film, Map<Inventory, List<Rental>>> entry) {
        this.film = entry.getKey();
        this.inventoryList = entry.getValue().entrySet().stream()
                .map(InventoryRental::new)
                .toList();
    }

    public Long getFilmId() {
        return film.getFilmId();
    }

    @Getter
    public static class InventoryRental {
        private final Inventory inventory;
        private final List<Rental> rentalList;

        public InventoryRental(Map.Entry<Inventory, List<Rental>> entry) {
            this.inventory = entry.getKey();
            this.rentalList = entry.getValue();
        }
    }
}