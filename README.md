# Findex

## 프로젝트 소개

- 외부 금융 Open API와 연동하여 사용자에게 금융 지수 데이터를 제공하는 대시보드 서비스입니다.
- 지수 정보 및 지수 데이터의 자동 연동을 통해 최신 데이터를 수집하고, 지수별 성과 분석과 이동 평균선 차트를 제공합니다.
- 프로젝트 기간 : 2026.03.11 ~ 2026.03.20

---

## 팀원 구성



| 👑 **Leader** | 👥 **Member** | 👥 **Member** | 👥 **Member** |
| :-----------: | :-----------: | :-----------: | :-----------: |
| **이승민**<br><sup>[chosi123](https://github.com/chosi123)</sup> | **김현재**<br><sup>[hyunjae3458](https://github.com/hyunjae3458)</sup> | **곽인성**<br><sup>[kwaksss](https://github.com/kwaksss)</sup> | **박정현**<br><sup>[JungH200000](https://github.com/JungH200000)</sup> |
| <img src="https://img.shields.io/badge/Leader-%23F05032?style=for-the-badge&logo=git&logoColor=white" alt="Leader" /> <img src="https://img.shields.io/badge/Backend-007ACC?style=for-the-badge&logo=spring&logoColor=white" alt="Backend" /> | <img src="https://img.shields.io/badge/Backend-007ACC?style=for-the-badge&logo=spring&logoColor=white" alt="Backend" /> | <img src="https://img.shields.io/badge/Backend-007ACC?style=for-the-badge&logo=spring&logoColor=white" alt="Backend" /> | <img src="https://img.shields.io/badge/Backend-007ACC?style=for-the-badge&logo=spring&logoColor=white" alt="Backend" /> |
| 연동 작업 및 자동 연동 설정 API 구현 | 지수 대시보드 API 구현 | 지수 정보 API 구현 | 지수 데이터 API 구현<br>공통 API 처리 기반 구성 |



---

## 기술 스택

### 백엔드

<!-- Spring Boot, Spring Data JPA -->
<img src="https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=Spring%20Boot&logoColor=white" /> <img src="https://img.shields.io/badge/Spring%20Data%20JPA-6DB33F?style=for-the-badge&logo=Spring&logoColor=white" />

### 데이터베이스

<!-- PostgreSQL -->
<img src="https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge&logo=PostgreSQL&logoColor=white" />

### 라이브러리


<img src="https://img.shields.io/badge/QueryDSL-00599C?style=for-the-badge&logo=Gradle&logoColor=white" /> <img src="https://img.shields.io/badge/MapStruct-3178C6?style=for-the-badge&logo=Apache%20Maven&logoColor=white" /> <img src="https://img.shields.io/badge/springdoc--openapi-85EA2D?style=for-the-badge&logo=OpenAPI%20Initiative&logoColor=black" />

### 배포

<!-- Railway.io -->
<a href="https://railway.app">
<img src="https://img.shields.io/badge/Railway-0B0D0E?style=for-the-badge&logo=Railway&logoColor=white" />
</a>

### 협업

<img src="https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=git&logoColor=white" /> <img src="https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white" /> <img src="https://img.shields.io/badge/Discord-5865F2?style=for-the-badge&logo=discord&logoColor=white" /> <img src="https://img.shields.io/badge/Notion-000000?style=for-the-badge&logo=notion&logoColor=white" />

---

## 팀원별 구현 기능 상세

### 곽인성

<img width="2000" height="905" alt="indexinfo" src="https://github.com/user-attachments/assets/27e92a30-7aa0-41f1-a29b-20d841d28bc4" />

- **지수 정보 관리 API**
    - 지수 정보 등록, 수정, 삭제, 단건/목록 조회 기능을 담당하는 API 구현
    - 등록 시 자동 연동 설정이 함께 생성되도록 구현
    - 지수 정보 삭제 시 관련 지수 데이터가 함께 삭제되도록 연관 삭제 처리
- **지수 데이터 검증 로직**
    - `{지수 분류명}`, `{지수명}` 조합으로 중복 등록을 방지하는 검증 로직 구현
    - 기준 시점의 미래 날짜 입력 방지 로직 구현
- **QueryDSL 기반 지수 정보 목록 조회**
    - `{지수 분류명}`, `{지수명}`, `{즐겨찾기}`를 기준으로 지수 정보 목록을 조회할 수 있는 기능 구현
    - 정렬 및 커서 페이지네이션을 적용하여 대용량 데이터에서도 효율적으로 조회할 수 있도록 구현

### 박정현

<img width="2000" height="899" alt="indaxdata" src="https://github.com/user-attachments/assets/cfce47ba-8fb1-4d03-bda6-ae1d17f5bd6b" />

- **지수 데이터 관리 API**
    - 지수 데이터 등록, 수정, 삭제, 목록 조회 기능을 담당하는 API 구현
- **지수 데이터 검증 로직 및 수정 로직**
    - `지수 정보 ID`와 `기준 날짜` 조합의 중복 등록을 방지하는 검증 로직 구현
    - `고가`, `저가`, `시가`, `종가`의 관계를 검증하여 잘못된 데이터 입력 방지
    - 수정 시 변경된 값만 반영하고, 지수와 날짜처럼 수정 불가능한 필드는 제외하도록 처리
- **QueryDSL 기반 지수 데이터 목록 조회 고도화**
    - `지수 정보 ID`와 `날짜 범위`를 기준으로 지수 데이터 목록을 조회할 수 있는 기능 구현
    - 정렬 필드별 커서 기반 페이지네이션을 적용해 대용량 데이터에서도 효율적으로 조회할 수 있도록 구현
    - `LocalDate`, `BigDecimal`, `Long` 타입에 따라 커서 비교 로직을 분리해 조회 구조를 개선
- **공통 API 응답 및 예외 처리 기반 구성**
    - 전역 예외 처리와 `ErrorResponse`를 구성해 잘못된 요청과 조회 실패 상황에서 일관된 오류 응답을 제공
    - QueryDSL 설정과 커서 페이지네이션 공통 응답 구조를 구현해 다른 기능에서도 재사용 가능한 기반 마련

### 김현재

<img width="1378" height="825" alt="image" src="https://github.com/user-attachments/assets/a27b8d4e-0ed8-46d4-bf88-423dee3766f3" />

- **주요 지수 API**
    - `{즐겨찾기}` 된 지수들의 지수별 기준가 대비 기간별 등락률과 대비(Versus) 수치를 계산하여 랭킹화하는 기능 구현
    - `{종가}` 기준으로 성과 데이터를 계산
- **지수 차트 및 분석 API**
    - 슬라이딩 윈도우 알고리즘을 적용한 이동평균선 데이터를 실시간으로 산출하여 월/분기/년 단위 시계열 차트 데이터 조회 기능 구현
    - `{종가}` 기준 5일, 20일 이동평균선 계산 로직 구현
- **관심 지수 성과 분석 랭킹 API**
    - `{종가}` 기준으로 즐겨찾기에 등록한 지수 지수별 기준가 대비 기간별 등락률과 전일/전주/전월 대비 성과 랭킹 구현
- **지수 데이터 CSV Export**
    - In-Memory 기반의 대용량 CSV 파일 다운로드 기능 구현

### 이승민

<img width="1378" height="809" alt="image" src="https://github.com/user-attachments/assets/b7fb5142-0fb5-426c-bacc-32164d071173" />

- **Open API 기반 지수 연동 API**
    - Open API를 활용한 지수 정보 연동 및 지수 데이터 연동 기능 구현
- **연동 작업 이력 관리 API**
    - `{유형}`, `{지수명}`, `{대상 날짜}`, `{작업자}`, `{작업일시}`, `{결과}` 조건 기반 목록 조회 기능 구현
    - 정렬 및 커서 페이지네이션을 적용하여 대용량 데이터에서도 효율적으로 조회할 수 있도록 구현
- **자동 연동 설정 및 배치 자동화**
    - 자동 연동 설정 목록 조회 및 활성화 여부 수정 기능 구현
    - 활성화된 지수를 대상으로 일정 주기마다 지수 데이터 연동을 수행하는 Scheduler 기반 배치 로직 구현

---

## 파일 구조

```text
src/
├── main/
│   ├── java/
│   │   └── org/
│   │       └── codeiteam3/
│   │           └── findex/
│   │               ├── config/          # QueryDSL, OpenAPI 등 공통 설정
│   │               ├── exception/       # 전역 예외 처리
│   │               ├── enums/           # 공통 Enum
│   │               ├── pagination/      # 커서 페이지네이션 공통 응답
│   │               ├── indexinfo/       # 지수 정보 관리
│   │               ├── indexdata/       # 지수 데이터 관리
│   │               ├── syncjob/         # 연동 작업 관리
│   │               └── autosyncconfig/  # 자동 연동 설정 관리
│   └── resources/
│       ├── static/                  # 정적 프론트엔드 파일
│       ├── application.yml          # 공통 설정 파일
│       ├── application-local.yml    # 로컬 개발용 설정 파일
│       ├── application-prod.yml     # 배포용 설정 파일
│       ├── schema.sql               # PostgreSQL Schema
│       └── schema-h2.sql            # h2 Schema
├── .gitignore
├── build.gradle
├── settings.gradle
└── README.md

```

---

## 구현 홈페이지

[[바로가기] Findex 구현 홈페이지](https://hopeful-essence-production-2761.up.railway.app/#/dashboard)

---

## 팀 Notion

[[바로가기] Spring 백엔드 초급 팀 프로젝트 팀 Notion](https://www.notion.so/jungh20000/SB10-3-Codeit-Spring-321f59816c02803aafbdf8a3354cfcdf?source=copy_link)

---

## 프로젝트 회고록

- [[바로가기] Findex 발표 자료]()
- [[바로가기] 프로젝트 회고록](https://www.notion.so/jungh20000/4Ls-Retrospective-327f59816c0280c4b2eae058cf64cf1a?source=copy_link)

---
