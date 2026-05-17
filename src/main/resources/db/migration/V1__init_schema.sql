CREATE TABLE `members`
(
    id                CHAR(36)     NOT NULL,
    name              VARCHAR(255) NOT NULL,
    age               INT          NOT NULL,
    mbti              VARCHAR(255) NOT NULL,
    profile_image_key VARCHAR(255) NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
