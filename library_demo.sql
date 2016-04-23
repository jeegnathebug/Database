INSERT INTO genre (genre_name) VALUES
('horror'),
('scifi'),
('fantasy'),
('biography'),
('childrens');

INSERT INTO author (firstname, lastname) VALUES
('George','Martin'),
('JK','Rowling'),
('Doc','Seuss'),
('Isaac','Asimov');

INSERT INTO book VALUES
(12345,'Harry Potter and some rock','2008-02-01',3),
(12346,'Harry Potter and some cup','2005-03-12',3),
(12347,'Harry Potter and some fugitive','2009-06-16',3),
(12348,'Harry Potter and Jon Snows ultimate crossover','2012-02-14',1),
(12349,'Game of Chairs','2012-02-14',3),
(12350,'Silly Nonsense','2012-02-28',5),
(12351,'My Robot','2014-09-24',2);

INSERT INTO book_authors VALUES
(2,12345),
(2,12346),
(2,12347),
(2,12348),
(1,12348),
(1,12349),
(3,12350),
(4,12351);

INSERT INTO patron (firstname, lastname,fees,email) VALUES
('Joe','Sho',0,'joes@gmail.com'),
('Edna','Carmichael',10,'ednak@gmail.com'),
('Timmytim','Tim',0,'timtimtim@gmail.com');

INSERT INTO book_loan (patron_id,book,due_date,returned) VALUES
(1,12345,'2013-02-14',true),
(1,12346,'2013-03-14',true),
(2,12348,'2015-09-18',false),
(3,12350,'2015-04-11',false);

INSERT INTO top_readers VALUES
(1,2),
(3,1),
(5,3);
