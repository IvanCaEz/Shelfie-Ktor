create table book_loans
(
    id_user    integer not null
        references users,
    id_book    integer not null
        references books,
    start_date varchar not null,
    end_date   varchar not null
);

alter table book_loans
    owner to postgres;

INSERT INTO public.book_loans (id_user, id_book, start_date, end_date) VALUES (1, 18, '25-04-2023', '24-06-2023');
INSERT INTO public.book_loans (id_user, id_book, start_date, end_date) VALUES (39, 9, '29-04-2023', '29-05-2023');
INSERT INTO public.book_loans (id_user, id_book, start_date, end_date) VALUES (1, 2, '30-04-2023', '30-05-2023');
