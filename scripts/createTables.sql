create table Events (
	eid int not null auto_increment,
	title varchar(100),
	longitude decimal(5, 2),
	latitude decimal(5, 2),
	time datetime,
	duration int,
	timecreated datetime,
	description varchar(1000),
	primary key (eid)
);

create table Accounts (
	uid varchar(20),
	name varchar(50),
	primary key (uid)
);

create table Attending (
	uid varchar(20) references Accounts(uid),
	eid int references Events(eid)
);

