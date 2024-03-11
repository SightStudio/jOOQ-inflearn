# Docker-Compose 를 사용하여 Sakila 데이터베이스 구축

### 1. 도커 볼륨 설정

도커 데스크탑 설치가 완료된 상태에서

```
docker volume create sakila_volume
```

### 2. docker-compose.yml 작성
```
version: '3.8'
services:
  sakila-mysql:
    image: mysql:8.0.35
    
    # 애플 실리콘 맥을 사용하는 경우 아래 platform 을 주석처리 해제
    # platform: linux/amd64
    volumes:
      - sakila_volume:/data
    ports:
      - '3306:3306'
    environment:
      MYSQL_ROOT_PASSWORD: passwd
      MYSQL_DATABASE: sakila
    command:
      [ 'mysqld', '--character-set-server=utf8mb4', '--collation-server=utf8mb4_unicode_ci', '--lower_case_table_names=1' ]

volumes:
  sakila_volume:
```

### 3. docker-compose 실행
docker-compose.yml 파일이 있는 곳에서 아래의 명령어 실행하여 컨테이너 실행

```
docker-compose up -d
```

### 4. 접속 확인

```
localhost:3306 
ID: root
PW: passwd
```

### 5. 컨테이너 종료
```
docker-compose down
```