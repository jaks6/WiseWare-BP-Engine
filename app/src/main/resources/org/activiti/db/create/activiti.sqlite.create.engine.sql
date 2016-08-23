create table ACT_GE_PROPERTY (
    NAME_ TEXT PRIMARY KEY,
    VALUE_ TEXT,
    REV_ INTEGER
);

insert into ACT_GE_PROPERTY
values ('schema.version', '5.21.0.0', 1);


insert into ACT_GE_PROPERTY
values ('schema.history', 'create(5.21.0.0)', 1);

insert into ACT_GE_PROPERTY
values ('next.dbid', '1', 1);

create table ACT_GE_BYTEARRAY (
    ID_ TEXT PRIMARY KEY,
    REV_ INTEGER,
    NAME_ TEXT,
    DEPLOYMENT_ID_ TEXT,
    BYTES_ BLOB,
    GENERATED_ INTEGER
);

create table ACT_RE_DEPLOYMENT (
    ID_ TEXT PRIMARY KEY,
    NAME_ TEXT,
    CATEGORY_ TEXT,
    TENANT_ID_ TEXT DEFAULT '',
    DEPLOY_TIME_ TEXT
);

create table ACT_RE_MODEL (
    ID_ TEXT NOT NULL PRIMARY KEY,
    REV_ INTEGER,
    NAME_ TEXT,
    KEY_ TEXT,
    CATEGORY_ TEXT,
    CREATE_TIME_ TEXT,
    LAST_UPDATE_TIME_ TEXT,
    VERSION_ INTEGER,
    META_INFO_ TEXT,
    DEPLOYMENT_ID_ TEXT,
    EDITOR_SOURCE_VALUE_ID_ TEXT,
    EDITOR_SOURCE_EXTRA_VALUE_ID_ TEXT,
    TENANT_ID_ TEXT DEFAULT ''
);

create table ACT_RU_EXECUTION (
    ID_ TEXT PRIMARY KEY,
    REV_ INTEGER,
    PROC_INST_ID_ TEXT,
    BUSINESS_KEY_ TEXT,
    PARENT_ID_ TEXT,
    PROC_DEF_ID_ TEXT,
    SUPER_EXEC_ TEXT,
    ACT_ID_ TEXT,
    IS_ACTIVE_ INTEGER,
    IS_CONCURRENT_ INTEGER,
    IS_SCOPE_ INTEGER,
    IS_EVENT_SCOPE_ INTEGER,
    SUSPENSION_STATE_ INTEGER,
    CACHED_ENT_STATE_ INTEGER,
    TENANT_ID_ TEXT default '',
    NAME_ TEXT,
    LOCK_TIME_ TEXT
);

create table ACT_RU_JOB (
    ID_ TEXT NOT NULL PRIMARY KEY,
    REV_ INTEGER,
    TYPE_ TEXT NOT NULL,
    LOCK_EXP_TIME_ TEXT,
    LOCK_OWNER_ TEXT,
    EXCLUSIVE_ INTEGER,
    EXECUTION_ID_ TEXT,
    PROCESS_INSTANCE_ID_ TEXT,
    PROC_DEF_ID_ TEXT,
    RETRIES_ INTEGER,
    EXCEPTION_STACK_ID_ TEXT,
    EXCEPTION_MSG_ TEXT,
    DUEDATE_ TEXT,
    REPEAT_ TEXT,
    HANDLER_TYPE_ TEXT,
    HANDLER_CFG_ TEXT,
    TENANT_ID_ TEXT default ''
);

create table ACT_RE_PROCDEF (
    ID_ TEXT NOT NULL PRIMARY KEY,
    REV_ INTEGER,
    CATEGORY_ TEXT,
    NAME_ TEXT,
    KEY_ TEXT NOT NULL,
    VERSION_ INTEGER NOT NULL,
    DEPLOYMENT_ID_ TEXT,
    RESOURCE_NAME_ TEXT,
    DGRM_RESOURCE_NAME_ TEXT,
    DESCRIPTION_ TEXT,
    HAS_START_FORM_KEY_ INTEGER,
    HAS_GRAPHICAL_NOTATION_ INTEGER,
    SUSPENSION_STATE_ INTEGER,
    TENANT_ID_ TEXT default ''
);

create table ACT_RU_TASK (
    ID_ TEXT PRIMARY KEY,
    REV_ INTEGER,
    EXECUTION_ID_ TEXT,
    PROC_INST_ID_ TEXT,
    PROC_DEF_ID_ TEXT,
    NAME_ TEXT,
    PARENT_TASK_ID_ TEXT,
    DESCRIPTION_ TEXT,
    TASK_DEF_KEY_ TEXT,
    OWNER_ TEXT,
    ASSIGNEE_ TEXT,
    DELEGATION_ TEXT,
    PRIORITY_ INTEGER,
    CREATE_TIME_ TEXT,
    DUE_DATE_ TEXT,
    CATEGORY_ TEXT,
    SUSPENSION_STATE_ INTEGER,
    TENANT_ID_ TEXT default '',
    FORM_KEY_ TEXT
);

create table ACT_RU_IDENTITYLINK (
    ID_ TEXT PRIMARY KEY,
    REV_ INTEGER,
    GROUP_ID_ TEXT,
    TYPE_ TEXT,
    USER_ID_ TEXT,
    TASK_ID_ TEXT,
    PROC_INST_ID_ TEXT,
    PROC_DEF_ID_ TEXT
);

create table ACT_RU_VARIABLE (
    ID_ TEXT NOT NULL PRIMARY KEY,
    REV_ INTEGER,
    TYPE_ TEXT NOT NULL,
    NAME_ TEXT NOT NULL,
    EXECUTION_ID_ TEXT,
    PROC_INST_ID_ TEXT,
    TASK_ID_ TEXT,
    BYTEARRAY_ID_ TEXT,
    DOUBLE_ REAL,
    LONG_ INTEGER,
    TEXT_ TEXT,
    TEXT2_ TEXT
);

create table ACT_RU_EVENT_SUBSCR (
    ID_ TEXT NOT NULL PRIMARY KEY,
    REV_ INTEGER,
    EVENT_TYPE_ TEXT NOT NULL,
    EVENT_NAME_ TEXT,
    EXECUTION_ID_ TEXT,
    PROC_INST_ID_ TEXT,
    ACTIVITY_ID_ TEXT,
    CONFIGURATION_ TEXT,
    CREATED_ TEXT NOT NULL,
    PROC_DEF_ID_ TEXT,
    TENANT_ID_ TEXT DEFAULT ''
);

create table ACT_EVT_LOG (
    LOG_NR_ INTEGER PRIMARY KEY autoincrement,
    TYPE_ TEXT,
    PROC_DEF_ID_ TEXT,
    PROC_INST_ID_ TEXT,
    EXECUTION_ID_ TEXT,
    TASK_ID_ TEXT,
    TIME_STAMP_ TEXT not null,
    USER_ID_ TEXT,
    DATA_ LONGBLOB,
    LOCK_OWNER_ TEXT,
    LOCK_TIME_ TEXT,
    IS_PROCESSED_ INTEGER default 0
);

create table ACT_PROCDEF_INFO (
	ID_ TEXT PRIMARY KEY not null,
    PROC_DEF_ID_ TEXT not null,
    REV_ INTEGER,
    INFO_JSON_ID_ TEXT
);

create index ACT_IDX_EXEC_BUSKEY on ACT_RU_EXECUTION(BUSINESS_KEY_);
create index ACT_IDX_TASK_CREATE on ACT_RU_TASK(CREATE_TIME_);
create index ACT_IDX_IDENT_LNK_USER on ACT_RU_IDENTITYLINK(USER_ID_);
create index ACT_IDX_IDENT_LNK_GROUP on ACT_RU_IDENTITYLINK(GROUP_ID_);
create index ACT_IDX_EVENT_SUBSCR_CONFIG_ on ACT_RU_EVENT_SUBSCR(CONFIGURATION_);
create index ACT_IDX_VARIABLE_TASK_ID on ACT_RU_VARIABLE(TASK_ID_);
create index ACT_IDX_ATHRZ_PROCEDEF on ACT_RU_IDENTITYLINK(PROC_DEF_ID_);
create index ACT_IDX_INFO_PROCDEF on ACT_PROCDEF_INFO(PROC_DEF_ID_);

--alter table ACT_GE_BYTEARRAY
--    add constraint ACT_FK_BYTEARR_DEPL
--    foreign key (DEPLOYMENT_ID_)
--    references ACT_RE_DEPLOYMENT;
--
--alter table ACT_RE_PROCDEF
--    add constraint ACT_UNIQ_PROCDEF
--    unique (KEY_,VERSION_);
--    
--alter table ACT_RU_EXECUTION
--    add constraint ACT_FK_EXE_PROCINST
--    foreign key (PROC_INST_ID_)
--    references ACT_RU_EXECUTION;
--
--alter table ACT_RU_EXECUTION
--    add constraint ACT_FK_EXE_PARENT
--    foreign key (PARENT_ID_)
--    references ACT_RU_EXECUTION;
--    
--alter table ACT_RU_EXECUTION
--    add constraint ACT_FK_EXE_SUPER 
--    foreign key (SUPER_EXEC_) 
--    references ACT_RU_EXECUTION;
--    
--alter table ACT_RU_EXECUTION
--    add constraint ACT_FK_EXE_PROCDEF 
--    foreign key (PROC_DEF_ID_) 
--    references ACT_RE_PROCDEF (ID_);    
--    
--alter table ACT_RU_EXECUTION
--    add constraint ACT_UNIQ_RU_BUS_KEY
--    unique(PROC_DEF_ID_, BUSINESS_KEY_);
--    
--alter table ACT_RU_IDENTITYLINK
--    add constraint ACT_FK_TSKASS_TASK
--    foreign key (TASK_ID_)
--    references ACT_RU_TASK;
--
--alter table ACT_RU_IDENTITYLINK
--    add constraint ACT_FK_ATHRZ_PROCEDEF
--    foreign key (PROC_DEF_ID_)
--    references ACT_RE_PROCDEF;
--    
--alter table ACT_RU_IDENTITYLINK
--    add constraint ACT_FK_IDL_PROCINST
--    foreign key (PROC_INST_ID_) 
--    references ACT_RU_EXECUTION (ID_);       
--
--alter table ACT_RU_TASK
--    add constraint ACT_FK_TASK_EXE
--    foreign key (EXECUTION_ID_)
--    references ACT_RU_EXECUTION;
--
--alter table ACT_RU_TASK
--    add constraint ACT_FK_TASK_PROCINST
--    foreign key (PROC_INST_ID_)
--    references ACT_RU_EXECUTION;
--
--alter table ACT_RU_TASK
--  add constraint ACT_FK_TASK_PROCDEF
--  foreign key (PROC_DEF_ID_)
--  references ACT_RE_PROCDEF;
--
--alter table ACT_RU_VARIABLE
--    add constraint ACT_FK_VAR_EXE
--    foreign key (EXECUTION_ID_)
--    references ACT_RU_EXECUTION;
--
--alter table ACT_RU_VARIABLE
--    add constraint ACT_FK_VAR_PROCINST
--    foreign key (PROC_INST_ID_)
--    references ACT_RU_EXECUTION;
--
--alter table ACT_RU_VARIABLE
--    add constraint ACT_FK_VAR_BYTEARRAY
--    foreign key (BYTEARRAY_ID_)
--    references ACT_GE_BYTEARRAY;
--
--alter table ACT_RU_JOB
--    add constraint ACT_FK_JOB_EXCEPTION
--    foreign key (EXCEPTION_STACK_ID_)
--    references ACT_GE_BYTEARRAY;
--
--alter table ACT_RU_EVENT_SUBSCR
--    add constraint ACT_FK_EVENT_EXEC
--    foreign key (EXECUTION_ID_)
--    references ACT_RU_EXECUTION;
--
--alter table ACT_RE_MODEL 
--    add constraint ACT_FK_MODEL_SOURCE 
--    foreign key (EDITOR_SOURCE_VALUE_ID_) 
--    references ACT_GE_BYTEARRAY (ID_);
--
--alter table ACT_RE_MODEL 
--    add constraint ACT_FK_MODEL_SOURCE_EXTRA 
--    foreign key (EDITOR_SOURCE_EXTRA_VALUE_ID_) 
--    references ACT_GE_BYTEARRAY (ID_);
--    
--alter table ACT_RE_MODEL 
--    add constraint ACT_FK_MODEL_DEPLOYMENT 
--    foreign key (DEPLOYMENT_ID_) 
--    references ACT_RE_DEPLOYMENT (ID_);        
