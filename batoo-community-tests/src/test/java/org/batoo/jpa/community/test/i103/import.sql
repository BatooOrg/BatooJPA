CREATE TABLE users (user_name VARCHAR(128) NOT NULL, password VARCHAR(128) NOT NULL, theme VARCHAR(25), active CHAR(1) DEFAULT 'Y', active_status_changed_dt TIMESTAMP, created_dt TIMESTAMP DEFAULT CURRENT TIMESTAMP, last_login_dt TIMESTAMP, password_last_changed_dt  TIMESTAMP, CONSTRAINT users_pk PRIMARY KEY ( user_name ) );
ALTER TABLE users ADD COLUMN last_logout_dt TIMESTAMP;
ALTER TABLE users ADD COLUMN logged_in_via_mobile CHAR(1) DEFAULT 'N';
UPDATE users SET logged_in_via_mobile = 'N';

