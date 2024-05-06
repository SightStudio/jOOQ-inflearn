# 섹션 3-2. 트랜잭션과 @Transactional, 그리고 spring-start-jOOQ

- Docs
  - update: https://www.jooq.org/doc/latest/manual/sql-building/sql-statements/update-statement
  - delete: https://www.jooq.org/doc/latest/manual/sql-building/sql-statements/delete-statement/

### 1. update 절
#### 1.1 동적으로 필드 업데이트

```mysql
set global general_log = ON;
set global log_output = 'TABLE';
```