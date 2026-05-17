# Member Profile Service

## 🚀 프로젝트 소개

이 프로젝트는 스타트업 백엔드 개발자로서 팀원들의 정보를 저장하고 프로필 사진을 업로드하는 API를 구축하고, 이를 AWS 상에서 안전하고 중단 없이 운영하는 것을 목표로 합니다. 아무것도 없는 상태에서 네트워크를 구축하고, DB와 파일 저장소를 분리하여 "서버가 죽어도 데이터가 안전한" Stateless 아키텍처를 완성하는 데 중점을 두었습니다.

### 기술 스택

*   **백엔드**: Spring Boot 3.x, Java 21, Gradle
*   **데이터베이스**: MySQL (RDS), H2 Database (로컬 개발)
*   **클라우드**: AWS (EC2, RDS, S3, Parameter Store, ALB, ASG, CloudFront, Route 53, ACM, IAM, Budgets)
*   **CI/CD**: GitHub Actions, Docker, Docker Hub

## ✨ 주요 기능 (필수 과제)

### LV 0 - 요금 폭탄 방지 AWS Budget 설정

클라우드 실습 중 가장 중요한 비용 관리를 위해 AWS Budgets를 설정했습니다. 월 예산을 $100로 설정하고, 예산의 80% 도달 시 이메일 알림이 오도록 구성했습니다.

*   **설정 요구사항**: 월 예산 $100, 80% 도달 시 이메일 알림
*   **제출 요구사항**: 설정 완료된 AWS Budgets 화면 캡처

### LV 1 - 네트워크 구축 및 핵심 기능 배포

안전한 네트워크 환경을 구축하고, '운영 가능한 상태'의 애플리케이션을 배포하여 외부 접속을 확인합니다.

*   **인프라 구축 (VPC & EC2)**
    *   VPC를 설정하여 Public/Private Subnet 분리
    *   Public Subnet에 EC2 생성
*   **애플리케이션 개발 (API & Actuator)**
    *   팀원 정보 저장 및 조회 API 개발 (`POST /api/members`, `GET /api/members/{id}`)
    *   로컬은 H2, 운영은 MySQL을 사용하도록 `application.yml`을 `local/prod`로 분리
    *   API 요청 시 `INFO` 레벨 로그, 예외 발생 시 `ERROR` 레벨 스택트레이스 로그 남기기
    *   `spring-boot-starter-actuator` 의존성 추가 및 헬스 체크 엔드포인트 노출 (`management.endpoints.web.exposure.include=health`)
*   **배포 및 검증**: EC2에 프로젝트 배포 및 실행, `/actuator/health` 엔드포인트 응답 확인
*   **제출 요구사항**: 설정 완료된 EC2의 퍼블릭 IP

### LV 2 - DB 분리 및 보안 연결

DB 비밀번호를 코드에 직접 노출하지 않고, AWS 관리형 서비스를 이용해 안전하게 배포합니다.

*   **인프라 요구사항**
    *   로컬 접속용 Public Subnet에 MySQL RDS 생성
    *   RDS 보안 그룹(Inbound)에 EC2의 보안 그룹 ID만 허용하는 보안 그룹 체이닝 설정
    *   DB 접속 정보(`url`, `username`, `password`) 및 확인용 파라미터를 Parameter Store에 저장
*   **애플리케이션 요구사항**
    *   Spring Boot 실행 시 Parameter Store 값을 주입받아 RDS에 연결
    *   Parameter Store에 저장한 `team-name` 값이 `/actuator/info` 엔드포인트에서 조회되도록 Actuator Info 확장
*   **검증**: `http://{EC2_Public_IP}:8080/actuator/info` 접속 시 팀 이름 출력 확인
*   **제출 요구사항**
    *   Actuator Info 엔드포인트 URL
    *   RDS 보안 그룹 인바운드 규칙 스크린샷 (EC2 보안 그룹 ID가 등록된 화면)

### LV 3 - 프로필 사진 기능 추가와 권한 관리

팀원 정보에 프로필 사진 기능을 추가하고, 서버 디스크가 아닌 S3를 사용하여 데이터의 안전성을 확보합니다.

*   **인프라 요구사항**
    *   "모든 퍼블릭 액세스 차단" 설정이 켜진 S3 버킷 생성
    *   S3 접근 권한이 있는 IAM Role 생성 및 EC2에 연결 (Access Key 미사용) 또는 IAM Policy 적용
*   **API 요구사항**
    *   `POST /api/members/{id}/profile-image`: MultipartFile로 이미지를 받아 S3에 업로드하고, 이미지 URL을 DB에 업데이트
    *   `GET /api/members/{id}/profile-image`: Presigned URL을 생성하여 반환 (유효기간 7일 설정)
*   **제출 요구사항**
    *   발급받은 Presigned URL 1개와 해당 URL의 만료 시간
    *   (IAM Role 사용 시) 접근 성공 스크린샷

## 💡 도전 과제

### LV 4 - Docker & CI/CD 파이프라인 구축

애플리케이션을 컨테이너 환경으로 포장하고, 코드 푸시 한 번으로 배포까지 완료되는 자동화를 구축합니다.

*   **Docker 도입**: `Dockerfile`을 작성하여 애플리케이션을 이미지로 빌드
*   **Github Actions CI/CD**
    *   `.github/workflows/deploy.yml` 작성
    *   **CI**: Main 브랜치 푸시 시 Build & Test 수행
    *   **CD**: 빌드된 이미지를 Docker Hub에 Push, EC2에서 `docker pull` 명령어로 이미지 받아 실행
*   **검증**: 코드를 수정하여 Github에 Push 했을 때, EC2에 자동으로 반영되는지 확인
*   **제출 요구사항**
    *   Github Actions 성공 이미지 (초록색 체크 표시)
    *   EC2 터미널 이미지 (`sudo docker ps` 명령어로 실행 중인 컨테이너 목록 확인)

### LV 5 - 고가용성 아키텍처와 보안 도메인 연결 (ALB + ASG + HTTPS)

트래픽 급증에 대비한 확장성(Auto Scaling)과 보안(HTTPS)을 확보하고, 외우기 쉬운 도메인을 연결합니다.

*   **NAT Gateway 생성**: Public Subnet에 NAT Gateway 생성, Private Subnet 라우팅 테이블 수정
*   **RDS & EC2 이사**: Public Subnet에 있던 RDS & EC2를 Private Subnet 환경으로 재구성
*   **도메인 구입 및 인증서 발급**: AWS Route 53에서 도메인 구입 및 호스팅 영역 생성, AWS ACM에서 SSL 인증서 발급
*   **로드 밸런서(ALB) 및 Auto Scaling 구성**
    *   **ALB**: EC2 앞단에 배치, HTTPS(443) 리스너에 인증서 적용, HTTP(80) 요청 HTTPS로 리다이렉트
    *   **ASG**: 시작 템플릿 작성, ALB와 연결된 Auto Scaling Group 생성 (트래픽/CPU 사용량에 따라 EC2 자동 생성/삭제)
*   **도메인 연결**: AWS Route 53에서 A 레코드를 생성하여 도메인 주소가 ALB DNS 주소를 가리키도록 설정
*   **제출 요구사항**
    *   HTTPS 적용된 도메인 URL
    *   Target Group(대상 그룹) 이미지 (Registered targets에 인스턴스가 등록되어 있고 Healthy 상태인 화면)

### LV 6 - 글로벌 성능 최적화 (CloudFront CDN)

전 세계 어디서든 프로필 사진을 빠르게 볼 수 있도록 CDN을 적용합니다.

*   **CloudFront 구축**: S3 버킷을 원본으로 하는 CloudFront 배포 생성
*   **검증**: CloudFront 도메인을 통해 이미지가 정상적으로 로딩되는지 확인
*   **제출 요구사항**
    *   CloudFront 이미지 URL (`https://dxxxxxxx.cloudfront.net/...` 형식)

## 🛠️ 트러블슈팅 및 학습 경험

프로젝트를 진행하면서 마주했던 주요 문제 상황과 해결 과정, 그리고 이를 통해 얻은 학습 경험을 공유합니다.

### 1. UUID를 사용한 이유와 저장 방식에 대한 고민

일반적인 `Long` Auto Increment ID 대신 `UUID` 기반으로 회원 ID를 구현했습니다. 이는 클라우드 기반의 확장 가능한 환경을 고려한 선택이었습니다. UUID는 애플리케이션 레벨에서 고유 ID를 생성하여 여러 서버 인스턴스에서 ID 충돌 가능성을 낮추고, 데이터 생성 순서 노출 및 리소스 ID 예측 가능성 문제를 방지합니다.

저장 방식으로는 `BINARY(16)` 대신 `CHAR(36)` 기반 문자열 형태를 선택했습니다. `BINARY(16)`이 저장 공간 및 인덱스 효율 측면에서 유리하지만, 이번 프로젝트는 대규모 트래픽 최적화보다는 클라우드 인프라 구성, 운영 환경 경험, 장애 분석 및 디버깅에 초점이 맞춰져 있었기 때문에, DB 조회 시 가독성, 로그 분석 편의성, 운영 중 디버깅 용이성을 우선했습니다.

### 2. 에러 코드를 도메인별로 분리한 이유

이전 프로젝트에서 모든 에러 코드를 하나의 `ErrorCode`에서 관리했을 때 발생했던 Git 충돌, 파일 크기 증가, 도메인 경계 모호화 등의 문제를 해결하기 위해 에러 코드를 도메인별로 분리했습니다. 현재는 `MemberErrorCode`를 별도로 관리하며, 향후 `AuthErrorCode`, `StorageErrorCode` 등으로 확장할 계획입니다. 이를 통해 Git 충돌 감소, 도메인 책임 분리, 에러 관리 가독성 향상, 유지보수성 개선 효과를 기대합니다.

### 3. 로컬에서는 정상인데 운영 환경(RDS)에서만 실패했던 UUID 문제

회원 생성 API가 로컬(H2)에서는 정상 동작했지만, AWS RDS(MySQL) 환경에서 `Incorrect string value` 에러가 발생했습니다. 이는 Hibernate 7의 UUID 저장 전략과 MySQL 컬럼 타입 불일치(DB는 `CHAR(36)`을 기대했지만 Hibernate는 `BINARY(16)`으로 처리) 때문이었습니다. 이 경험을 통해 Hibernate 기본 매핑 전략, DB Dialect 차이, 실제 운영 DB 동작 방식까지 함께 고려해야 함을 배웠고, 운영 환경과 최대한 유사한 환경에서 테스트하는 중요성을 깨달았습니다.

### 4. Gradle Daemon 때문에 발생했던 Java 21 인식 문제

EC2에서 Spring Boot 프로젝트 빌드 중 `Cannot find a Java installation on your machine matching: {languageVersion=21}` 에러가 발생했습니다. Java 21이 설치되어 있었음에도 불구하고 발생한 이 문제는 Gradle Daemon이 이전 JDK 환경 정보를 캐싱하고 있었기 때문이었습니다. `./gradlew --stop` 명령어로 Daemon을 재시작하여 해결했으며, `java -version` 뿐만 아니라 `javac -version`까지 함께 확인해야 함을 배웠습니다.

### 5. Docker는 “어디서든 실행된다”가 아니라 아키텍처도 맞아야 한다는 점

GitHub Actions 기반 CI/CD 구축 후 Docker 이미지는 정상 배포되었으나, EC2에서 컨테이너가 `exec format error`와 함께 계속 재시작되는 문제가 있었습니다. 원인은 GitHub Actions 빌드 환경(`linux/amd64`)과 EC2 실행 환경(`ARM64(aarch64)`) 간의 아키텍처 불일치였습니다. GitHub Actions에서 `platforms: linux/arm64`로 빌드 플랫폼을 명시하여 해결했으며, CI/CD 구축 시 실행 환경의 CPU 아키텍처까지 고려해야 함을 체감했습니다.

### 6. ALB Target Group이 Unhealthy였던 원인

ALB Health Check가 계속 실패하여 Spring Boot 설정, Actuator 설정, 컨테이너 문제 등을 의심했습니다. 하지만 실제 원인은 EC2와 RDS 간의 연결 실패, 즉 RDS Security Group 설정 오류였습니다. `Private EC2 SG → RDS SG : 3306 허용` 구조가 되어야 했으나, RDS가 다른 Security Group을 허용하고 있어 Spring Boot 부팅 실패, `/actuator/health` 실패로 이어졌습니다. RDS 인바운드 규칙을 EC2 Security Group 기준으로 수정한 뒤 해결되었으며, 네트워크 구조, Security Group 체이닝, 애플리케이션 부팅 의존성까지 함께 고려해야 함을 배웠습니다.

### 7. 파일 업로드 API를 구현하면서 HTTP Multipart 구조를 이해하게 된 점

프로필 이미지 업로드 API 구현 시 `@RequestParam MultipartFile`만 고려했으나, 파일 업로드가 `multipart/form-data` 구조이며 요청 내부가 여러 개의 part로 나뉜다는 점을 이해하게 되었습니다. 이후 `@RequestPart("image") MultipartFile image` 형태로 명확하게 처리했으며, JSON과 파일 동시 업로드 구조를 고려할 때 `@RequestPart`가 의미 전달, 유지보수성, 요청 구조 명확성 측면에서 더 적절하다고 판단했습니다.

## 📝 과제 제출 요구사항 (README.md 포함 내용)

과제 제출 시 `README.md`에 반드시 포함되어야 하는 정보는 다음과 같습니다.

*   **LV 0**: 설정 완료된 AWS Budgets 화면 캡처
*   **LV 1**: 설정 완료된 EC2의 퍼블릭 IP
*   **LV 2**: Actuator Info 엔드포인트 URL, RDS 보안 그룹 인바운드 규칙 스크린샷
*   **LV 3**: 발급받은 Presigned URL 1개와 해당 URL의 만료 시간 (IAM Role 사용 시 접근 성공 스크린샷)
*   **LV 4**: Github Actions 성공 이미지 (초록색 체크 표시), EC2 터미널 이미지 (`sudo docker ps` 결과)
*   **LV 5**: HTTPS 적용된 도메인 URL, Target Group(대상 그룹) 이미지
*   **LV 6**: CloudFront 이미지 URL

---
