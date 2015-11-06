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
    CONSTRAINT fk_auth_username FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE);
    
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
    CONSTRAINT fk_info_username FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE);
    
CREATE TABLE IF NOT EXISTS `user_gcm` (
    id int(11) NOT NULL AUTO_INCREMENT,
    username varchar(64) NOT NULL,
    token varchar(4096),
    PRIMARY KEY (id),
    UNIQUE KEY uni_username(username),
    KEY fk_username_idx(username),
    CONSTRAINT fk_gcm_username FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE);
    
CREATE TABLE IF NOT EXISTS `check_in_data` (
    id int(11) NOT NULL AUTO_INCREMENT,
    username varchar(64) NOT NULL,
    sugar_level float NOT NULL,
    sugar_level_time varchar(64) NOT NULL,
    meal varchar(4096) NOT NULL,
    meal_time varchar(64) NOT NULL,
    insulin_dosage float NOT NULL,
    insulin_administration_time varchar(64) NOT NULL,
    mood_level int,
    stress_level int,
    energy_level int,
    check_in_timestamp timestamp NOT NULL,
    PRIMARY KEY (id),
    KEY fk_username_idx(username),
    CONSTRAINT fk_check_in_username FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE);