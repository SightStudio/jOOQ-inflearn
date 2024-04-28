# 섹션 2-5. EnumConverter 를 통한 Enum Mapping 하기

### 1. FilmPriceSummary 수정

```java
@Getter
public class FilmPriceSummary {

    private Long filmId;
    private String title;
    private PriceCategory priceCategory;
    private BigDecimal rentalRate;
    private Long totalInventory;

    @Getter
    public enum PriceCategory {
        CHEAP("Cheap"),
        MODERATE("Moderate"),
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
```

### 2. EnumConverter 생성
평균 대여 기간이 가장 긴 영화부터 정렬해서 조회한다.

```java
public class PriceCategoryConverter extends EnumConverter<String, FilmPriceSummary.PriceCategory>{

    public PriceCategoryConverter() {
        super(String.class, FilmPriceSummary.PriceCategory.class, FilmPriceSummary.PriceCategory::getCode);
    }
}
```

### 3. EnumConverter 사용

```java

@Repository
public class FilmRepositoryHasA {

    private final DSLContext dslContext;
    
    // ...
    
    public List<FilmPriceSummary> findFilmPriceSummaryByFilmTitleLike(String filmTitle) {
        final JInventory INVENTORY = JInventory.INVENTORY;
        return dslContext.select(
                        FILM.FILM_ID,
                        FILM.TITLE,
                        FILM.RENTAL_RATE,
                        case_()
                                .when(FILM.RENTAL_RATE.le(BigDecimal.valueOf(1.0)), "Cheap")
                                .when(FILM.RENTAL_RATE.le(BigDecimal.valueOf(3.0)), "Moderate")
                                .else_("Expensive").as("price_category").convert(new PriceCategoryConverter()),
                        selectCount().from(INVENTORY).where(INVENTORY.FILM_ID.eq(FILM.FILM_ID)).asField("total_inventory")
                ).from(FILM)
                .where(containsIfNotBlank(FILM.TITLE, filmTitle))
                .fetchInto(FilmPriceSummary.class);
    }
}
```