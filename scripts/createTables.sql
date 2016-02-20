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
	uid int,
	name varchar(50),
	primary key (uid)
);

create table Attending (
	uid int references Accounts(uid),
	eid int references Events(eid)
);

