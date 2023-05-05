create table users
(
    id_user                serial
        primary key,
    name                   varchar   not null,
    email                  varchar   not null,
    password               varchar   not null,
    user_name              varchar   not null,
    description            varchar   not null,
    user_type              varchar   not null,
    borrowed_books_counter integer   not null,
    book_history           integer[] not null,
    banned                 boolean   not null,
    user_image             varchar   not null
);

alter table users
    owner to postgres;

INSERT INTO public.users (id_user, name, email, password, user_name, description, user_type, borrowed_books_counter, book_history, banned, user_image) VALUES (1, 'Ivancho', 'ivan.martinez.7e6@itb.cat', 'a97b6eac20701a30d06e8e2a947dd548ffa59763fa2f972670dfbb69db68dc59', 'theReader', 'I like to read', 'ADMIN', 0, '{21,1,23,5,10,11,12,6,7,8,17,22}', false, 'kgIaclN.jpg');
INSERT INTO public.users (id_user, name, email, password, user_name, description, user_type, borrowed_books_counter, book_history, banned, user_image) VALUES (39, 'Shrek', 'shrek@cienaga.com', 'a61511667debd7e100f77cb0e67d748468535ffd062a50646ae4a4f8f7154e51', 'shrek', 'Que haces en mi cienaga', 'NORMAL', 0, '{23}', false, 'shrek-forever-after-1587549453.jpg');
INSERT INTO public.users (id_user, name, email, password, user_name, description, user_type, borrowed_books_counter, book_history, banned, user_image) VALUES (42, 'persona', 'person@a.com', '5e815286bca594454b291f3b0350ec22aab6de20b6d9efeec67d604f6bce65ee', 'persona', 'persona', 'NORMAL', 0, '', false, 'placeholder.png');
INSERT INTO public.users (id_user, name, email, password, user_name, description, user_type, borrowed_books_counter, book_history, banned, user_image) VALUES (43, 'gato', 'con@botas.com', 'f8e2611287fe201ceaf6db054ece7bf3c9395454e11437aac039836b232365f8', 'gato', 'gato', 'NORMAL', 0, '', false, 'Puss-In-Boots-Ending-Explained-2011-Animated-Action-Film.jpg');
INSERT INTO public.users (id_user, name, email, password, user_name, description, user_type, borrowed_books_counter, book_history, banned, user_image) VALUES (41, 'nuevo', 'nuevo@gmail.com', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'nuevo', 'nuevo', 'NORMAL', 0, '', false, 'Puss-In-Boots-Ending-Explained-2011-Animated-Action-Film.jpg');
INSERT INTO public.users (id_user, name, email, password, user_name, description, user_type, borrowed_books_counter, book_history, banned, user_image) VALUES (44, 'libro ssssaaa', 'nuevo@mi.com', '931927a3d62b8ec859a6fe88dea25e145e7a5e630f683069ec49a6e2e1426cad', 'libro', 'libro', 'NORMAL', 0, '', false, 'Puss-In-Boots-Ending-Explained-2011-Animated-Action-Film.jpg');
INSERT INTO public.users (id_user, name, email, password, user_name, description, user_type, borrowed_books_counter, book_history, banned, user_image) VALUES (45, 'pruebafinal', 'prueba@final.com', '655e786674d9d3e77bc05ed1de37b4b6bc89f788829f9f3c679e7687b410c89b', 'pruebafinal', 'pruebafinal', 'NORMAL', 0, '', false, 'placeholder.png');
