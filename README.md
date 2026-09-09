# Mini Board (Spring Boot Community Project)

Java와 Spring Boot 기반의 커뮤니티 게시판 웹 애플리케이션. 
JPA와 Spring Security 같은 고수준 추상화 기술에 의존하지 않고, **JDBC를 이용한 데이터 접근과 세션 기반 인증/인가 로직을 직접 구현**하며 웹 애플리케이션의 핵심 동작 원리를 깊이 있게 학습하고자 기획.

---

## Project Overview

Spring Boot 3.x 환경에서 동작하는 게시글, 댓글, 회원 관리 중심의 커뮤니티 서비스. 
프레임워크의 기본 환경 위에서 아래 영역들을 직접 설계하고 통제하며 백엔드 구조에 대한 이해도 향상에 집중.

* JDBC 기반 SQL 매핑 및 데이터 처리
* `HttpSession`을 활용한 로그인 세션 관리
* `Interceptor` 기반의 역할 단위(Role-based) 접근 제어
* 서버사이드 데이터 및 권한 검증

---

## Service Access

* **URL:** (서비스 중단)
* **ADMIN 계정**
  * ID: `admin1`
  * PW: `admin1`

---

## Tech Stack

### Backend
* Java 17
* Spring Boot 3.x
* Spring MVC
* Spring JDBC (`NamedParameterJdbcTemplate`)
* MySQL 8.0

### Frontend
* Thymeleaf
* Bootstrap 5

---

## Key Features

### 1. 게시판 (Board)
* **CRUD:** 게시글 및 댓글 작성, 조회, 수정, 삭제 
* **동적 검색:** 제목, 내용, 작성자 등 다양한 조건을 조합한 다중 검색 지원
* **페이징 처리:** `LIMIT / OFFSET` 쿼리를 활용해 대량 데이터 조회 시 성능 최적화

### 2. 인증 및 권한 관리 (Auth & RBAC)
* **자체 세션 관리:** Spring Security 필터 대신 `HttpSession`을 이용한 로그인 구현
* **역할 기반 접근 제어:** MVC `Interceptor`를 통한 권한 검증
  * `ADMIN`: 전체 게시글/댓글 및 회원 관리
  * `USER`: 일반 글 작성 및 본인 소유의 데이터 수정/삭제
  * `GUEST`: 읽기 전용 접근
* **보안:** `BCryptPasswordEncoder`를 적용한 비밀번호 단방향 암호화

### 3. 댓글 시스템
* 게시글 종속적인 댓글 작성 및 삭제 기능
* 비정상적인 접근(타인의 댓글 삭제 요청 등)을 방지하기 위한 서버단 검증 로직 적용

---

## Technical Decisions

### JDBC를 활용한 데이터 접근 제어
ORM 기술 대신 Spring JDBC를 채택하여 쿼리 실행 흐름을 직관적으로 파악하고 데이터베이스 접근 비용을 직접 제어.
* `NamedParameterJdbcTemplate` 활용으로 SQL 가독성 향상 및 파라미터 바인딩 오류 방지
* 복잡한 연관 데이터 조회 시 직접 `JOIN` 쿼리를 작성하여 N+1 이슈 원천 차단

### Interceptor 기반의 단순화된 인증·인가
복잡한 Security 필터 체인 대신 Spring MVC의 인터셉터를 활용해 요청 흐름을 단순하고 명확하게 설계.
* `LoginCheckInterceptor`: 전역적인 로그인 상태 검증
* `RoleCheckInterceptor`: URL 패턴(`/admin/**` 등)에 따른 인가 처리 및 권한 부족 시 `403 Forbidden` 응답 제어

### 계층 분리 및 DTO 책임 할당
* Controller, Service, Repository의 명확한 레이어 분리
* Request / Response DTO를 철저히 분리하여 View 계층과 도메인 비즈니스 로직 간의 결합도 최소화

---

## ERD

```mermaid
erDiagram
    USERS ||--o{ POSTS : writes
    USERS ||--o{ COMMENTS : writes
    POSTS ||--o{ COMMENTS : has

    USERS {
        bigint id PK
        varchar login_id UK
        varchar password
        varchar username
        varchar role
    }

    POSTS {
        bigint id PK
        varchar title
        text content
        bigint user_id FK
        bigint like_count
        bigint dislike_count
    }

    COMMENTS {
        bigint id PK
        bigint post_id FK
        bigint user_id FK
        varchar content
    }
