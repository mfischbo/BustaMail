insert into Security_User (id, dateCreated, dateModified, deleted, email, firstName, lastName, locked, password, hidden) values (
0x642430F616964205B1145D963E96CCFD, '2014-06-06 01:00:00', '2014-06-06 01:00:00', 0, 'schdahle@art-ignition.de', 'Markus', 'Fischböck', 0, '$2a$04$zfmWwsUXZfRZqTYtoFBjmOx2aIyKv7Gqa1Q1aesD221yn62tcUGQG', 0);

insert into Security_OrgUnit (id, dateCreated, dateModified, deleted, description, name) values (
0xAEF3B4393CA946DCA0E0CDB030F25A0C, '2014-06-06 17:41:04', '2014-06-06 17:41:04', 0, 'Default organizational unit', 'Root');

insert into Security_Actor(id, addToChildren, addToFutureChildren, OrgUnit_id, User_id) values (
0xDED8F0C4D5F042269EFA8DC33337A36E, 1, 1, 0xAEF3B4393CA946DCA0E0CDB030F25A0C, 0x642430F616964205B1145D963E96CCFD);


insert into Security_Actor_Permission(Actor, Permission_id) values (0xDED8F0C4D5F042269EFA8DC33337A36E, 0x3EA312EE637B4EE984A5446ED7CFA061);
insert into Security_Actor_Permission(Actor, Permission_id) values (0xDED8F0C4D5F042269EFA8DC33337A36E, 0x2F2FE0C63B964893B80BDA85ADFFE8FE);

insert into Media_Directory (id, Owner_id, description, name, Parent_id) values (
0x15F812FBC25C45ABAB5B1FDB7D2DCE33,
0xAEF3B4393CA946DCA0E0CDB030F25A0C,
"Root of the directory hierarchy. If you ever see this, something went terribly wrong",
"/", null);
