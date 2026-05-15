# AWS Parameter Store 설정

운영 환경(`prod`)에서는 AWS Systems Manager Parameter Store에서 DB 접속 정보와 팀 이름을 읽어옵니다.

## Parameter Store 경로

애플리케이션은 다음 경로를 읽습니다.

```text
/member-profile-service/prod/
```

아래 파라미터를 생성해야 합니다.

| 이름 | 용도 |
| --- | --- |
| `/member-profile-service/prod/DB_URL` | RDS JDBC URL |
| `/member-profile-service/prod/DB_USERNAME` | RDS 사용자명 |
| `/member-profile-service/prod/DB_PASSWORD` | RDS 비밀번호 |
| `/member-profile-service/prod/TEAM_NAME` | `/actuator/info` 확인용 팀 이름 |

이 값들은 `application-prod.yaml`에서 아래 설정으로 참조합니다.

```yaml
spring:
  config:
    import: aws-parameterstore:/member-profile-service/prod/
  datasource:
    url: ${DB_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}

info:
  team-name: ${TEAM_NAME}
```

## AWS CLI 예시

```bash
aws ssm put-parameter \
  --name "/member-profile-service/prod/DB_URL" \
  --type "String" \
  --value "jdbc:mysql://<rds-endpoint>:3306/<database-name>" \
  --overwrite

aws ssm put-parameter \
  --name "/member-profile-service/prod/DB_USERNAME" \
  --type "String" \
  --value "<username>" \
  --overwrite

aws ssm put-parameter \
  --name "/member-profile-service/prod/DB_PASSWORD" \
  --type "SecureString" \
  --value "<password>" \
  --overwrite

aws ssm put-parameter \
  --name "/member-profile-service/prod/TEAM_NAME" \
  --type "String" \
  --value "<team-name>" \
  --overwrite
```

## EC2 IAM 권한

EC2 인스턴스에 연결된 IAM Role에는 최소한 아래 권한이 필요합니다.

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "ssm:GetParameter",
        "ssm:GetParameters",
        "ssm:GetParametersByPath"
      ],
      "Resource": "arn:aws:ssm:ap-northeast-2:*:parameter/member-profile-service/prod/*"
    }
  ]
}
```

`DB_PASSWORD`를 `SecureString`으로 저장한 경우 KMS 키 정책에 따라 `kms:Decrypt` 권한이 추가로 필요할 수 있습니다.

## 운영 실행

EC2에서는 `prod` profile로 애플리케이션을 실행합니다.

```bash
java -jar app.jar --spring.profiles.active=prod
```

정상 적용 여부는 아래 URL에서 확인합니다.

```text
http://<EC2_PUBLIC_IP>:8080/actuator/info
```

응답에 Parameter Store에 저장한 `TEAM_NAME` 값이 포함되어야 합니다.
