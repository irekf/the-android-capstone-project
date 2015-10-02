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
    CONSTRAINT fk_username FOREIGN KEY (username) REFERENCES users(username));