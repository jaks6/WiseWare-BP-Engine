create table ACT_ID_GROUP (
    ID_ TEXT,
    REV_ integer,
    NAME_ TEXT,
    TYPE_ TEXT,
    primary key (ID_)
);

create table ACT_ID_MEMBERSHIP (
    USER_ID_ TEXT,
    GROUP_ID_ TEXT,
    primary key (USER_ID_, GROUP_ID_)
);

create table ACT_ID_USER (
    ID_ TEXT,
    REV_ integer,
    FIRST_ TEXT,
    LAST_ TEXT,
    EMAIL_ TEXT,
    PWD_ TEXT,
    PICTURE_ID_ TEXT,
    primary key (ID_)
);

create table ACT_ID_INFO (
    ID_ TEXT,
    REV_ integer,
    USER_ID_ TEXT,
    TYPE_ TEXT,
    KEY_ TEXT,
    VALUE_ TEXT,
    PASSWORD_ longvarbinary,
    PARENT_ID_ TEXT,
    primary key (ID_)
);

--alter table ACT_ID_MEMBERSHIP
--    add constraint ACT_FK_MEMB_GROUP
--    foreign key (GROUP_ID_)
--    references ACT_ID_GROUP;
--
--alter table ACT_ID_MEMBERSHIP
--    add constraint ACT_FK_MEMB_USER
--    foreign key (USER_ID_)
--    references ACT_ID_USER;
