create table reviews
(
    id_review serial
        primary key,
    id_book   integer not null
        references books,
    id_user   integer not null
        references users,
    date      varchar not null,
    review    text    not null,
    rating    integer not null
);

alter table reviews
    owner to postgres;

INSERT INTO public.reviews (id_review, id_book, id_user, date, review, rating) VALUES (4, 21, 1, '29-04-2023', 'Me ha gustado que la protagonista fuera la androide Klara y como narra lo que experimenta', 5);
INSERT INTO public.reviews (id_review, id_book, id_user, date, review, rating) VALUES (5, 1, 1, '29-04-2023', 'Guay', 5);
INSERT INTO public.reviews (id_review, id_book, id_user, date, review, rating) VALUES (7, 23, 1, '29-04-2023', 'Hace gracia', 3);
INSERT INTO public.reviews (id_review, id_book, id_user, date, review, rating) VALUES (8, 5, 1, '29-04-2023', 'Obligatorio leer', 5);
INSERT INTO public.reviews (id_review, id_book, id_user, date, review, rating) VALUES (9, 10, 1, '29-04-2023', 'Protagonista fuerte', 5);
INSERT INTO public.reviews (id_review, id_book, id_user, date, review, rating) VALUES (10, 11, 1, '29-04-2023', '', 5);
INSERT INTO public.reviews (id_review, id_book, id_user, date, review, rating) VALUES (11, 12, 1, '29-04-2023', '', 5);
INSERT INTO public.reviews (id_review, id_book, id_user, date, review, rating) VALUES (12, 6, 1, '29-04-2023', 'Cenicienta cyborg y alienigena', 5);
INSERT INTO public.reviews (id_review, id_book, id_user, date, review, rating) VALUES (13, 7, 1, '29-04-2023', '', 5);
INSERT INTO public.reviews (id_review, id_book, id_user, date, review, rating) VALUES (14, 8, 1, '29-04-2023', '', 5);
INSERT INTO public.reviews (id_review, id_book, id_user, date, review, rating) VALUES (17, 23, 39, '29-04-2023', '', 4);
INSERT INTO public.reviews (id_review, id_book, id_user, date, review, rating) VALUES (19, 22, 1, '30-04-2023', '', 4);
INSERT INTO public.reviews (id_review, id_book, id_user, date, review, rating) VALUES (15, 17, 1, '01-05-2023', 'Un clasico', 4);
