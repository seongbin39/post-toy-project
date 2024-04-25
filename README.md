# 게시판 토이 프로젝트
## 프로젝트 URL
- [https://www.seong.store](https://www.seong.store)

## 1. 프로젝트 소개
- 게시판 토이 프로젝트는 Spring Boot와 JPA,QueryDSL 등 을 사용하여 게시판을 구현한 프로젝트입니다.
- Thymeleaf와 Bootstrap을 사용하여 프론트엔드를 구현하였습니다.
- Spring Security를 사용하여 로그인, 회원가입, 권한 처리를 구현하였습니다.
- MariaDB를 사용하여 데이터베이스를 구현하였습니다.
- Docker, Nginx를 사용하여 프로젝트를 배포하였습니다.
- Jenkins를 사용하여 CI/CD를 구현하였습니다.
- GitHub를 사용하여 소스 코드를 관리하였습니다.
- 게시판의 기본적인 기능을 구현하였습니다.
- 게시글과 댓글,대댓글에 대한 CRUD 기능을 구현하였습니다.
- 게시글과 대한 페이징 처리, 검색, 조회수 기능을 구현하였습니다.
- 게시글과 댓글,대댓글에 대한 권한 처리를 구현하였습니다.

## 2. 프로젝트 환경

- Java 17
- Spring Boot 3.2.1
- Gradle 8.5
- Spring Security
- Spring Data JPA
- Querydsl
- MariaDB
- Redis
- Thymeleaf
- Bootstrap
- Lombok

## 3. 프로젝트 구조

```
src
├── main
│   ├── java
│   │   └── com
│   │       └── board
│   │           ├── BoardApplication.java
│   │           ├── config
│   │           │   ├── SecurityConfig.java
│   │           │   └── WebConfig.java
│   │           ├── controller
│   │           │   ├── BoardController.java
│   │           │   └── CommentController.java
│   │           ├── domain
│   │           │   ├── Board.java
│   │           │   ├── Comment.java
│   │           │   └── User.java
│   │           ├── repository
│   │           │   ├── BoardRepository.java
│   │           │   ├── CommentRepository.java
│   │           │   └── UserRepository.java
│   │           └── service
│   │               ├── BoardService.java
│   │               ├── CommentService.java
│   │               └── UserService.java
│   └── resources
│       ├── static
│       │   ├── css
│       │   │   └── style.css
│       │   └── js
│       │       └── script.js
│       ├── templates
│       │   ├── board
│       │   │   ├── boardForm.html
│       │   │   ├── boardList.html
│       │   │   └── boardView.html
│       │   ├── comment
│       │   │   ├── commentForm.html
│       │   │   └── commentList.html
│       │   └── user
│       │       ├── login.html
│       │       └── signup.html
│       ├── application.properties
│       └── data.sql
└── test
    └── java
        └── com
            └── board
                ├── BoardApplicationTests.java
                ├── controller
                │   ├── BoardControllerTests.java
                │   └── CommentControllerTests.java
                ├── repository
                │   ├── BoardRepositoryTests.java
                │   ├── CommentRepositoryTests.java
                │   └── UserRepositoryTests.java
                └── service
                    ├── BoardServiceTests.java
                    ├── CommentServiceTests.java
                    └── UserServiceTests.java
```

## 4. 프로젝트 기능

1. 게시글
    - 게시글 작성, 수정, 삭제, 조회
    - 게시글 첨부 파일 업로드
    - 게시글 목록 페이징 처리
    - 게시글 검색
    - 게시글 조회수
    - 게시글 작성자와 로그인한 사용자가 같은 경우 수정, 삭제 가능
    - 관리자 권한을 가진 사용자는 게시글 수정, 삭제 가능
2. 댓글, 대댓글
    - 댓글 및 대댓글 작성, 수정, 삭제
    - 댓글, 대댓글 작성자와 로그인한 사용자가 같은 경우 수정, 삭제 가능
    - 관리자 권한을 가진 사용자는 댓글, 대댓글 수정, 삭제 가능
3. 회원
   - 회원가입, 로그인, 로그아웃
   - 회원가입 시 이메일 인증 및 비밀번호 암호화
   - 로그인한 사용자만 게시글, 댓글 작성, 댓글 작성 가능
   - 프로필 이미지 업로드
   - 회원 정보 수정, 회원 탈퇴
   - 회원 정보 수정, 회원 탈퇴 시 이메일 인증
4. 관리자
   - 관리자 권한을 가진 사용자는 모든 게시글, 댓글, 대댓글 조회 가능
   - 관리자 권한을 가진 사용자는 사용자 목록 조회 가능
   - 관리자 권한을 가진 사용자는 사용자 정보 수정 가능
   - 관리자 권한을 가진 사용자는 사용자 권한 변경 가능
   - 관리자 권한을 가진 사용자는 사용자 삭제 가능
5. 로그인
    - Spring Security 인증, 인가
    - 소셜 로그인(Google, Naver, Kakao)

## 5. 프로젝트 배포 환경

- Mac OS
- Docker
- Nginx
- Jenkins
- GitHub
![아키텍쳐](https://github.com/seongbin39/ecommerce/assets/85536059/8c2861b0-485e-44cd-bcc5-006917977f22)

## 6. 프로젝트 화면

1. 메인 화면
2. 게시글 목록 화면
3. 게시글 작성 화면
4. 게시글 상세 화면
5. 댓글 작성 화면
6. 회원가입 화면
7. 로그인 화면
8. 회원 정보 수정 화면
9. 회원 탈퇴 화면
10. 사용자 목록 화면
11. 사용자 정보 수정 화면
12. 사용자 권한 변경 화면
13. 사용자 삭제 화면
14. 소셜 로그인 화면
15. 소셜 로그인 성공 화면
16. 소셜 로그인 실패 화면
17. 404 에러 화면
18. 500 에러 화면
19. 로그아웃 화면
20. 이메일 인증 화면
21. 비밀번호 찾기 화면
22. 비밀번호 변경 화면
23. 프로필 이미지 업로드 화면
24. 프로필 이미지 수정 화면
25. 프로필 이미지 삭제 화면
26. 게시글 첨부 파일 다운로드 화면
27. 프로필 이미지 다운로드 화면
28. 게시글 첨부 파일 업로드 화면
29. 게시글 첨부 파일 삭제 화면
30. 게시글 첨부 파일 다운로드 화면

## 7. 프로젝트 기능 추가(예정)

1. 게시글
    - 게시글 좋아요, 싫어요
    - 게시글 추천, 비추천
    - 게시글 신고
2. 댓글, 대댓글
    - 댓글 좋아요, 싫어요
    - 댓글 추천, 비추천
    - 댓글 신고
3. 회원
   - 회원 팔로우, 언팔로우
   - 회원 차단, 차단 해제
   - 회원 신고
4. 관리자
   - 관리자 권한을 가진 사용자는 모든 게시글, 댓글, 대댓글 좋아요, 싫어요, 추천, 비추천, 신고 조회 가능
   - 관리자 권한을 가진 사용자는 사용자 차단, 차단 해제, 신고 조회 가능
   - 관리자 권한을 가진 사용자는 사용자 신고 처리 가능
   - 관리자 권한을 가진 사용자는 사용자 신고 처리 내역 조회 가능
5. 로그인
   - Redis 사용하여 세션을 관리
