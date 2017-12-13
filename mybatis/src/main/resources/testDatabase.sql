CREATE DATABASE mybatis;

USE mybatis;

CREATE TABLE crm_user(
id BIGINT NOT NULL AUTO_INCREMENT,
first_name VARCHAR(20) NOT NULL DEFAULT "",
last_name VARCHAR(20) NOT NULL DEFAULT "",
age SMALLINT NOT NULL,
sex TINYINT NOT NULL,
key(id)
);