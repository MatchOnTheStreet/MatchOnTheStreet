create table Events (
	eid int not null auto_increment,
	title varchar(100),
	longitude decimal(10, 7),
	latitude decimal(10, 7),
	start_time bigint,
	duration int,
	time_created bigint,
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

