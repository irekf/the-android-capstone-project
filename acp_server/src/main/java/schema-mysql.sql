CREATE TABLE IF NOT EXISTS `users` (
    `username` varchar(64) NOT NULL,
    `password` varchar(64) NOT NULL,
    `enabled` tinyint(1) NOT NULL,
    PRIMARY KEY (`username`));

CREATE TABLE IF NOT EXISTS `authorities` (
    user_authority_id int(11) NOT NULL AUTO_INCREMENT,
    username varchar(64) NOT NULL,
    authority varchar(64) NOT NULL,
    PRIMARY KEY (user_authority_id),
    UNIQUE KEY uni_username_authority(authority, username),
    KEY fk_username_idx(username),
    CONSTRAINT fk_auth_username FOREIGN KEY (username) REFERENCES users(username));
    
CREATE TABLE IF NOT EXISTS `user_info` (
    id int(11) NOT NULL AUTO_INCREMENT,
    username varchar(64) NOT NULL,
    user_type varchar(64) NOT NULL,
    first_name varchar(64) NOT NULL,
    second_name varchar(64) NOT NULL,
    birth_date varchar(64) NOT NULL,
    medical_record_number varchar(64),
    email varchar(64) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uni_username(username),
    KEY fk_username_idx(username),
    CONSTRAINT fk_info_username FOREIGN KEY (username) REFERENCES users(username));