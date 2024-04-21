CREATE DATABASE WDCOverflow;
USE WDCOverflow;

CREATE TABLE Users (
    id INT AUTO_INCREMENT,
    handle VARCHAR(63) UNIQUE,
    pass VARCHAR(255),
    avatar VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    display_name VARCHAR(255),
    PRIMARY KEY (id)
);

SET FOREIGN_KEY_CHECKS=0;

CREATE TABLE Posts (
    id INT AUTO_INCREMENT,
    created DATETIME,
    content TEXT,
    author INT,
    title VARCHAR(255),
    answer INT,
    PRIMARY KEY (id),
    FOREIGN KEY (author) REFERENCES Users(id) ON DELETE SET NULL,
    FOREIGN KEY (answer) REFERENCES Comments(id) ON DELETE SET NULL
);

CREATE TABLE Comments (
    id INT AUTO_INCREMENT,
    created DATETIME,
    content TEXT,
    author INT,
    post_id INT NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (author) REFERENCES Users(id) ON DELETE SET NULL,
    FOREIGN KEY (post_id) REFERENCES Posts(id) ON DELETE CASCADE
);

SET FOREIGN_KEY_CHECKS=1;

CREATE TABLE Votes (
    user_id INT,
    post_id INT NOT NULL,
    up_or_down INT,
    PRIMARY KEY (user_id, post_id),
    FOREIGN KEY (user_id) REFERENCES Users(id),
    FOREIGN KEY (post_id) REFERENCES Posts(id) ON DELETE CASCADE
);

CREATE TABLE CTags (
    comment_id INT NOT NULL,
    tagname VARCHAR(63),
    PRIMARY KEY (comment_id, tagname),
    FOREIGN KEY (comment_id) REFERENCES Comments(id) ON DELETE CASCADE
);

CREATE TABLE PTags (
    post_id INT NOT NULL,
    tagname VARCHAR(63),
    PRIMARY KEY (post_id, tagname),
    FOREIGN KEY (post_id) REFERENCES Posts(id) ON DELETE CASCADE
);