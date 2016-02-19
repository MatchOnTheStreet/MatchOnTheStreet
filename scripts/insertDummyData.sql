/* Account Dummies */
insert into Accounts (uid, name) values ("mable", "Mable Pines");
insert into Accounts (uid, name) values ("dipper", "Dipper Pines");
insert into Accounts (uid, name) values ("rick", "Rick Sanchez");
insert into Accounts (uid, name) values ("morty", "Morty Smith");

/* Event Dummies */
insert into Events (eid, title, longitude, latitude, time, description)
	values (1, "Awesome Death Match", 20, 20, '2016-02-20 22:22:22',
		"This is an awesome event full of nice people.");
insert into Events (eid, title, longitude, latitude, time, description)
	values (2, "Awesome Friend Match", 20, 20, '2016-04-20 14:11:22',
		"This is an awesome event full of evil people.");

/* Attending Dummies */
insert into Attending (uid, eid) values ('mable', 1);
insert into Attending (uid, eid) values ('dipper', 1);
insert into Attending (uid, eid) values ('rick', 2);
insert into Attending (uid, eid) values ('morty', 2);
