CREATE TABLE greetings (
    id INT PRIMARY KEY AUTO_INCREMENT, 
    receiver VARCHAR(255), 
    sender VARCHAR(255) 
);
insert into greetings (receiver, sender) values('Foo', 'Bar');
insert into greetings (receiver, sender) values('Bar', 'Foo');