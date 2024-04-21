INSERT INTO Users (
    handle,
    pass,
    avatar,
    email,
    display_name
) VALUES (
    'pixelpenguin',
    'password',
    '/path/to/mypic.jpg',
    'pixel@penguin.com',
    'Pixel Penguin'
);


INSERT INTO Posts (
    created,
    content,
    author,
    title
) VALUES (
    CURRENT_TIMESTAMP(),
    'This is my post',
    1,
    'Pixel penguin''s first post'
);

INSERT INTO Comments (
    created,
    content,
    author,
    post_id
) VALUES (
    CURRENT_TIMESTAMP(),
    'This is my comment',
    1,
    2
);