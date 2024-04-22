# 섹션 2-5. EnumConverter 를 통해 SELECT 한 컬럼을 Enum 타입으로 매핑하기

### 1. 인라인 뷰 (FROM 절)
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
