# member-profile-service

클라우드기반 백엔드 설계 과제를 위한 저장소입니다.

## 운영 설정

- 로컬 환경은 `local` profile과 H2 데이터베이스를 사용합니다.
- 운영 환경은 `prod` profile과 MySQL RDS를 사용합니다.
- 운영 DB 접속 정보와 팀 이름은 AWS Systems Manager Parameter Store에서 주입받습니다.

Parameter Store 설정 방법은 [AWS Parameter Store 설정](docs/aws-parameter-store.md)을 참고합니다.
