create table book_ratings
(
    id_book integer          not null
        references books,
    rating  double precision not null
);

alter table book_ratings
    owner to postgres;

INSERT INTO public.book_ratings (id_book, rating) VALUES (13, 0);
INSERT INTO public.book_ratings (id_book, rating) VALUES (14, 0);
INSERT INTO public.book_ratings (id_book, rating) VALUES (15, 0);
INSERT INTO public.book_ratings (id_book, rating) VALUES (16, 0);
INSERT INTO public.book_ratings (id_book, rating) VALUES (9, 0);
INSERT INTO public.book_ratings (id_book, rating) VALUES (18, 0);
INSERT INTO public.book_ratings (id_book, rating) VALUES (19, 0);
INSERT INTO public.book_ratings (id_book, rating) VALUES (20, 0);
INSERT INTO public.book_ratings (id_book, rating) VALUES (24, 0);
INSERT INTO public.book_ratings (id_book, rating) VALUES (25, 0);
INSERT INTO public.book_ratings (id_book, rating) VALUES (26, 0);
INSERT INTO public.book_ratings (id_book, rating) VALUES (27, 0);
INSERT INTO public.book_ratings (id_book, rating) VALUES (2, 0);
INSERT INTO public.book_ratings (id_book, rating) VALUES (21, 5);
INSERT INTO public.book_ratings (id_book, rating) VALUES (1, 5);
INSERT INTO public.book_ratings (id_book, rating) VALUES (5, 5);
INSERT INTO public.book_ratings (id_book, rating) VALUES (10, 5);
INSERT INTO public.book_ratings (id_book, rating) VALUES (11, 5);
INSERT INTO public.book_ratings (id_book, rating) VALUES (12, 5);
INSERT INTO public.book_ratings (id_book, rating) VALUES (6, 5);
INSERT INTO public.book_ratings (id_book, rating) VALUES (7, 5);
INSERT INTO public.book_ratings (id_book, rating) VALUES (8, 5);
INSERT INTO public.book_ratings (id_book, rating) VALUES (23, 3.5);
INSERT INTO public.book_ratings (id_book, rating) VALUES (22, 3);
INSERT INTO public.book_ratings (id_book, rating) VALUES (17, 4);
