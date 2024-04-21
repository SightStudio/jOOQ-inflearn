# 섹션 2-4. 서브쿼리

#### 1. 스칼라 서브쿼리 (SELECT 절)
영화별 대여료가 1.0 이하면 'Cheap', 3.0 이하면 'Moderate', 그 이상이면 'Expensive'로 분류하고, 각 영화의 총 재고 수를 조회한다.

```mysql
SELECT
    film.film_id,
    film.title,
    film.rental_rate,
    CASE
        WHEN rental_rate <= 1.0 THEN 'Cheap'
        WHEN rental_rate <= 3.0 THEN 'Moderate'
        ELSE 'Expensive'
    END AS price_category,
    (SELECT COUNT(*) FROM inventory where film_id = film.film_id) AS total_inventory
FROM FILM;
```

### 2. 인라인 뷰 (FROM 절)
평균 대여 기간이 가장 긴 영화부터 정렬해서 조회한다.

```mysql
SELECT film.film_id,
       film.title,
       rental_duration_info.average_rental_duration
FROM film
JOIN
     (SELECT inventory.film_id, AVG(DATEDIFF(rental.return_date, rental.rental_date)) AS average_rental_duration
      FROM rental JOIN
          inventory ON rental.inventory_id = inventory.inventory_id
      WHERE rental.return_date IS NOT NULL
      GROUP BY inventory.film_id
     ) AS rental_duration_info
    ON film.film_id = rental_duration_info.film_id
ORDER BY rental_duration_info.average_rental_duration DESC;
```

### 3. 서브쿼리 
대여된 기록이 있는 영화가 있는 영화만 조회한다.
```mysql
SELECT
  film.film_id,
  film.title
FROM
  film
WHERE EXISTS (
  SELECT 1
  FROM inventory
  JOIN rental ON inventory.inventory_id = rental.inventory_id
  WHERE inventory.film_id = film.film_id
)
```
