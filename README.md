# 📋 Mini Board (Spring Boot Community Project) 
![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-Green?style=for-the-badge&logo=thymeleaf&logoColor=white)

> **"Convenience hides complexity."**
> 
> 편리함 뒤에 숨겨진 원리를 이해하기 위해, **JPA와 Spring Security 없이** 순수 기술로 구현한 커뮤니티 서비스입니다.

## 📖 Project Overview
Spring Boot와 JDBC를 활용하여 개발한 **사용자 참여형 커뮤니티 게시판**입니다.
프레임워크가 제공하는 추상화된 기능(JPA, Security)에 전적으로 의존하기보다, 웹 애플리케이션의 핵심 메커니즘(세션 기반 인증, 역할 기반 인가, SQL 핸들링)을 직접 구현하며 백엔드 개발의 기반을 다지는 데 주력했습니다.

---
접속 주소: http://3.35.209.240:8080

## 🛠 Tech Stack

### Backend
- **Core**: Java 17, Spring Boot 3.x
- **Web**: Spring MVC (RESTful API & SSR)
- **Data Access**: Spring JDBC (NamedParameterJdbcTemplate) - *No ORM*
- **Database**: MySQL 8.0

### Frontend
- **Template Engine**: Thymeleaf
- **Styling**: Bootstrap 5 (Responsive Design)

---

## 💡 Key Features

### 1. 게시글 관리 (Board)
- **CRUD 구현**: 게시글 작성, 조회, 수정, 삭제 기능
- **동적 검색 (Dynamic Search)**: 제목, 내용, 작성자 등 다양한 조건 조합을 `StringBuilder`로 처리하여 검색 최적화
- **페이징 (Pagination)**: 대량의 데이터 처리를 고려하여 `LIMIT`, `OFFSET` 기반의 DB 페이징 구현

### 2. 회원 시스템 및 권한 관리 (Auth & RBAC)
- **자체 인증/인가 구현**: `Spring Security` 없이 `Interceptor`와 `HttpSession`을 활용하여 보안 로직 설계
- **역할 기반 접근 제어 (RBAC)**: 사용자 유형을 3단계로 분류하여 철저한 권한 관리 적용
    - **`ADMIN`**: 전체 게시글/댓글 관리 및 회원 관리 권한 (시스템 관리자)
    - **`USER`**: 게시글 작성, 본인 게시글 수정/삭제, 댓글 작성 권한 (일반 회원)
    - **`GUEST`**: 게시글/댓글 단순 조회만 가능 (비로그인 사용자)
- **보안**: `BCryptPasswordEncoder`를 활용한 비밀번호 단방향 암호화

### 3. 상호작용 기능 (Interaction)
- **계층형 댓글**: 게시글에 대한 댓글 작성 및 삭제 기능
- **작성자 검증**: 서버단에서 세션 정보와 작성자를 대조하여 불법적인 수정/삭제 요청 차단

---

## 🔍 Technical Deep Dive

### 1. Why JDBC, Not JPA? (SQL 주도 개발)
ORM의 편리함보다는 **SQL 작성 능력과 데이터베이스 접근 비용에 대한 이해**를 우선시했습니다.
- **명시적 쿼리 제어**: `NamedParameterJdbcTemplate`을 사용하여 SQL 가독성을 높이고, 파라미터 바인딩을 명확히 했습니다.
- **N+1 문제 원천 차단**: 연관 데이터 조회 시 `JOIN` 쿼리를 직접 작성하여 불필요한 네트워크 통신을 최소화했습니다.

### 2. Spring Security 없는 권한 관리 (Role-Based Authorization)
블랙박스처럼 동작하는 Security 필터 체인 대신, Spring MVC의 인터셉터를 활용하여 권한 체크 로직을 직접 구축했습니다.
- **Role 분리 전략**: DB 내 `role` 컬럼(`ADMIN`, `USER`, `GUEST`)을 기준으로 요청 권한을 식별합니다.
- **Interceptor 활용**:
    - `LoginCheckInterceptor`: 인증 여부(세션 존재 유무) 확인
    - `RoleCheckInterceptor`: 특정 URL(예: `/admin/**`) 접근 시 사용자 Role을 검증하여 권한이 없으면 접근 거부(`403 Forbidden`) 처리

### 3. 견고한 아키텍처 설계
- **계층 분리**: `Controller` (요청 처리) → `Service` (비즈니스 로직) → `Repository` (DB 접근)의 역할 분리.
- **DTO 패턴**: Entity가 View에 직접 노출되는 것을 방지하기 위해 `RequestDTO`, `ResponseDTO`를 철저히 분리하여 데이터 무결성을 보장했습니다.

---

## 📊 ERD (Entity Relationship Diagram)

```mermaid
erDiagram
    USERS ||--o{ POSTS : writes
    USERS ||--o{ COMMENTS : writes
    POSTS ||--o{ COMMENTS : has

    USERS {
        bigint id PK
        varchar login_id UK "Unique Login ID"
        varchar password
        varchar username
        varchar role "ADMIN, USER, GUEST"
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
