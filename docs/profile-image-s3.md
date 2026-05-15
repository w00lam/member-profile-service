# 프로필 이미지 S3 연동

프로필 이미지는 S3 비공개 버킷에 저장합니다.

과제 요구사항에서는 이미지 URL을 DB에 저장한다고 표현되어 있지만, 이 구현에서는 만료되는 Presigned URL 자체를 저장하지 않고 S3 object key를 저장합니다. Presigned URL은 유효기간이 있는 임시 접근 URL이므로 DB에 저장하면 만료 후 사용할 수 없습니다. 대신 DB에는 고정 식별자인 object key를 저장하고, 조회 시점에 유효기간 7일인 Presigned URL을 새로 생성해 반환합니다.

## API

### 프로필 이미지 업로드

```http
POST /api/members/{id}/profile-image
Content-Type: multipart/form-data
```

요청 파트:

| 이름 | 설명 |
| --- | --- |
| `image` | 업로드할 이미지 파일 |

S3 object key는 아래 형식으로 생성합니다.

```text
profile-images/{memberId}/{UUID}-{originalFilename}
```

### 프로필 이미지 Presigned URL 조회

```http
GET /api/members/{id}/profile-image
```

응답으로 7일 동안 유효한 Presigned URL을 반환합니다.

## DB 스키마 참고

운영 환경은 `ddl-auto=validate`를 사용하므로 애플리케이션 실행 전에 DB 스키마가 엔티티와 일치해야 합니다.

처음 배포하는 경우에는 엔티티 기준으로 전체 테이블을 생성한 뒤 실행하면 됩니다. 이미 `member` 테이블이 존재하는 운영 DB에 프로필 이미지 기능만 추가 배포하는 경우에는 아래 컬럼이 필요합니다.

```sql
ALTER TABLE member
    ADD COLUMN profile_image_key VARCHAR(255) NULL;
```

SQL 파일은 참고용으로 [lv3-profile-image.sql](sql/lv3-profile-image.sql)에 있습니다.
