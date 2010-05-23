
    drop schema COMPANY if exists;
    
    drop schema ITEM if exists;
    
    drop schema PEOPLE if exists;
    
    alter table COMPANY.COMPANY 
        drop constraint FK_COMPANY_MAINEMAIL if exists;

    alter table COMPANY.COMPANY 
        drop constraint FK_COMPANY_MAINPHONE if exists;

    alter table COMPANY.COMPANY 
        drop constraint FK_COMPANY_MAINADDR if exists;

    alter table COMPANY.CORPORATE_CLIENT 
        drop constraint FK_CORPCLIENT_INVOICEADDR if exists;

    alter table COMPANY.CORPORATE_CLIENT 
        drop constraint FK_CORPCLIENT_COMPANY if exists;

    alter table COMPANY.CORPORATE_CLIENT 
        drop constraint FK_CORPCLIENT_DELIVERYADDR if exists;

    alter table COMPANY.CORPORATE_CLIENT_REPRESENTATIVE 
        drop constraint FK_CORPCLIENT_REPCORPCLIENT if exists;

    alter table COMPANY.CORPORATE_CLIENT_REPRESENTATIVE 
        drop constraint FK_CORPCLIENT_REPWORKEMAIL if exists;

    alter table COMPANY.CORPORATE_CLIENT_REPRESENTATIVE 
        drop constraint FK_CORPCLIENT_REPPARENTPERSON if exists;

    alter table COMPANY.CORPORATE_CLIENT_REPRESENTATIVE 
        drop constraint FK_CORPCLIENT_REPWORKADDR if exists;

    alter table COMPANY.CORPORATE_CLIENT_REPRESENTATIVE 
        drop constraint FK_CORPCLIENT_REPWORKPHONE if exists;

    alter table COMPANY.ITEM_SUPPLIER 
        drop constraint FK_ITEMSUPPLIER_COMPANY if exists;

    alter table COMPANY.ITEM_SUPPLIER_REPRESENTATIVE 
        drop constraint FK_ITEMSUPPLIER_REPWORKEMAIL if exists;

    alter table COMPANY.ITEM_SUPPLIER_REPRESENTATIVE 
        drop constraint FK_ITEMSUPPLIER_REPITEMSUPPLIER if exists;

    alter table COMPANY.ITEM_SUPPLIER_REPRESENTATIVE 
        drop constraint FK_ITEMSUPPLIER_REPPARENTPERSON if exists;

    alter table COMPANY.ITEM_SUPPLIER_REPRESENTATIVE 
        drop constraint FK_ITEMSUPPLIER_REPWORKADDR if exists;

    alter table COMPANY.ITEM_SUPPLIER_REPRESENTATIVE 
        drop constraint FK_ITEMSUPPLIER_REPWORKPHONE if exists;

    alter table ITEM.ITEM 
        drop constraint FK_ITEM_ITEMSUPPLIER if exists;

    alter table ITEM.ITEM_OPTION_SPECIFICATION 
        drop constraint FK_ITEMOPTIONSPEC_ITEMOPTION if exists;

    alter table ITEM.ITEM_OPTION_SPECIFICATION 
        drop constraint FK_ITEMOPTIONSPEC_ITEMSPEC if exists;

    alter table ITEM.ITEM_SPECIFICATION 
        drop constraint FK_ITEMSPEC_ITEM if exists;

    alter table ITEM.OFFER 
        drop constraint FK_OFFER_ITEM if exists;

    alter table ITEM.PROMOTION 
        drop constraint FK_PROMOTION_OFFER if exists;

    alter table ITEM.ITEM_ITEM_OPTION_ASSOC 
        drop constraint FK_ITEMOPTION_ITEM if exists;

    alter table ITEM.ITEM_ITEM_OPTION_ASSOC 
        drop constraint FK_ITEM_ITEMOPTION if exists;

    alter table PEOPLE.EMPLOYEE 
        drop constraint FK_EMPLOYEE_WORKEMAILADDR if exists;

    alter table PEOPLE.EMPLOYEE 
        drop constraint FK_EMPLOYEE_PERSON if exists;

    alter table PEOPLE.PERSON 
        drop constraint FK_PERSON_HOMEADDR if exists;

    alter table PEOPLE.PERSON 
        drop constraint FK_PERSON_PRVMOBPHONE if exists;

    alter table PEOPLE.PERSON 
        drop constraint FK_PERSON_PRVRESPHONE if exists;

    alter table PEOPLE.PERSON 
        drop constraint FK_PERSON_PRVFAX if exists;

    alter table PEOPLE.PERSON_ACCOUNT 
        drop constraint FK_ACCOUNT_PERSON if exists;

    alter table PEOPLE.PRIVATE_CUSTOMER 
        drop constraint FK_PRIVATECUST_SHIPPINGADDR if exists;

    alter table PEOPLE.PRIVATE_CUSTOMER 
        drop constraint FK_PRIVATECUST_INVOICEADDR if exists;

    alter table PEOPLE.PRIVATE_CUSTOMER 
        drop constraint FK_PRIVATECUST_PERSON if exists;

    drop table COMPANY.COMPANY if exists;

    drop table COMPANY.CORPORATE_CLIENT if exists;

    drop table COMPANY.CORPORATE_CLIENT_REPRESENTATIVE if exists;

    drop table COMPANY.ITEM_SUPPLIER if exists;

    drop table COMPANY.ITEM_SUPPLIER_REPRESENTATIVE if exists;

    drop table ITEM.ITEM if exists;

    drop table ITEM.ITEM_OPTION if exists;

    drop table ITEM.ITEM_OPTION_SPECIFICATION if exists;

    drop table ITEM.ITEM_SPECIFICATION if exists;

    drop table ITEM.OFFER if exists;

    drop table ITEM.PROMOTION if exists;

    drop table ITEM.ITEM_ITEM_OPTION_ASSOC if exists;

    drop table PEOPLE.EMAIL_ADDRESS if exists;

    drop table PEOPLE.EMPLOYEE if exists;

    drop table PEOPLE.PERSON if exists;

    drop table PEOPLE.PERSON_ACCOUNT if exists;

    drop table PEOPLE.POSTAL_ADDRESS if exists;

    drop table PEOPLE.PRIVATE_CUSTOMER if exists;

    drop table PEOPLE.TELEPHONE_NUMBER if exists;

    drop sequence PEOPLE.ID_SEQ_ACCOUNT if exists;

    drop sequence COMPANY.ID_SEQ_COMPANY if exists;

    drop sequence PEOPLE.ID_SEQ_CONTACT_CHANNEL if exists;

    drop sequence COMPANY.ID_SEQ_CORPORATE_CLIENT if exists;

    drop sequence COMPANY.ID_SEQ_CORP_CLIENT_REP if exists;

    drop sequence PEOPLE.ID_SEQ_EMPLOYEE if exists;

    drop sequence ITEM.ID_SEQ_ITEM if exists;

    drop sequence ITEM.ID_SEQ_ITEM_OPTION if exists;

    drop sequence ITEM.ID_SEQ_ITEM_OPTION_SPEC if exists;

    drop sequence ITEM.ID_SEQ_ITEM_SPEC if exists;

    drop sequence COMPANY.ID_SEQ_ITEM_SUPPLIER if exists;

    drop sequence COMPANY.ID_SEQ_ITEM_SUPPLIER_REP if exists;

    drop sequence ITEM.ID_SEQ_OFFER if exists;

    drop sequence PEOPLE.ID_SEQ_PEOPLE if exists;

    drop sequence PEOPLE.ID_SEQ_PRIVATE_CUSTOMER if exists;

    drop sequence ITEM.ID_SEQ_PROMOTION if exists;
    
    create schema PEOPLE if not exists;
    
    create schema COMPANY if not exists;
    
    create schema ITEM if not exists;

    create table COMPANY.COMPANY (
        id bigint not null,
        META_VERSION integer,
        AUDIT_CREATED_BY varchar(15) not null,
        AUDIT_CREATED_ON timestamp not null,
        AUDIT_LAST_UPDATED_BY varchar(15) not null,
        AUDIT_LAST_UPDATED_ON timestamp not null,
        COMPANY_NUMBER varchar(30) not null unique,
        NAME varchar(60) not null unique,
        ID_MAIN_EMAIL_ADDRESS bigint not null unique,
        ID_MAIN_POSTAL_ADDRESS bigint not null unique,
        ID_MAIN_PHONE_NUMBER bigint not null unique,
        primary key (id)
    );

    create table COMPANY.CORPORATE_CLIENT (
        id bigint not null,
        META_VERSION integer,
        AUDIT_CREATED_BY varchar(15) not null,
        AUDIT_CREATED_ON timestamp not null,
        AUDIT_LAST_UPDATED_BY varchar(15) not null,
        AUDIT_LAST_UPDATED_ON timestamp not null,
        CORPORATE_CLIENT_NUMBER varchar(30) not null unique,
        ID_DELIVERY_ADDRESS bigint not null unique,
        ID_COMPANY bigint not null unique,
        ID_INVOICE_ADDRESS bigint not null unique,
        primary key (id)
    );

    create table COMPANY.CORPORATE_CLIENT_REPRESENTATIVE (
        id bigint not null,
        META_VERSION integer,
        AUDIT_CREATED_BY varchar(15) not null,
        AUDIT_CREATED_ON timestamp not null,
        AUDIT_LAST_UPDATED_BY varchar(15) not null,
        AUDIT_LAST_UPDATED_ON timestamp not null,
        CORPORATE_CLIENT_REP_NUMBER varchar(30) not null unique,
        POSITION varchar(20) not null,
        ID_WORK_PHONE_NUMBER bigint,
        ID_WORK_ADDRESS bigint,
        ID_WORK_EMAIL_ADDRESS bigint not null,
        ID_CORPORATE_CLIENT bigint,
        ID_PARENT_PERSON bigint not null unique,
        primary key (id)
    );

    create table COMPANY.ITEM_SUPPLIER (
        id bigint not null,
        META_VERSION integer,
        AUDIT_CREATED_BY varchar(15) not null,
        AUDIT_CREATED_ON timestamp not null,
        AUDIT_LAST_UPDATED_BY varchar(15) not null,
        AUDIT_LAST_UPDATED_ON timestamp not null,
        ITEM_SUPPLIER_NUMBER varchar(30) not null unique,
        ID_COMPANY bigint not null unique,
        primary key (id)
    );

    create table COMPANY.ITEM_SUPPLIER_REPRESENTATIVE (
        id bigint not null,
        META_VERSION integer,
        AUDIT_CREATED_BY varchar(15) not null,
        AUDIT_CREATED_ON timestamp not null,
        AUDIT_LAST_UPDATED_BY varchar(15) not null,
        AUDIT_LAST_UPDATED_ON timestamp not null,
        ITEM_SUPPLIER_REP_NUMBER varchar(30) not null unique,
        POSITION varchar(20) not null,
        ID_WORK_EMAIL_ADDRESS bigint not null,
        ID_WORK_PHONE_NUMBER bigint,
        ID_PARENT_PERSON bigint not null unique,
        ID_WORK_ADDRESS bigint,
        ID_ITEM_SUPPLIER bigint,
        primary key (id)
    );

    create table ITEM.ITEM (
        id bigint not null,
        META_VERSION integer,
        AUDIT_CREATED_BY varchar(15) not null,
        AUDIT_CREATED_ON timestamp not null,
        AUDIT_LAST_UPDATED_BY varchar(15) not null,
        AUDIT_LAST_UPDATED_ON timestamp not null,
        DESCRIPTION varchar(2000) not null,
        ITEM_NUMBER varchar(30) not null unique,
        NAME varchar(60) not null unique,
        ID_ITEM_SUPPLIER bigint not null,
        primary key (id)
    );

    create table ITEM.ITEM_OPTION (
        id bigint not null,
        META_VERSION integer,
        DESCRIPTION varchar(200) not null,
        NAME varchar(60) not null unique,
        TYPE varchar(30) not null,
        primary key (id)
    );

    create table ITEM.ITEM_OPTION_SPECIFICATION (
        id bigint not null,
        META_VERSION integer,
        NAME varchar(200) not null,
        ID_ITEM_OPTION bigint not null,
        ID_ITEM_SPECIFICATION bigint not null,
        primary key (id)
    );

    create table ITEM.ITEM_SPECIFICATION (
        id bigint not null,
        META_VERSION integer,
        AUDIT_CREATED_BY varchar(15) not null,
        AUDIT_CREATED_ON timestamp not null,
        AUDIT_LAST_UPDATED_BY varchar(15) not null,
        AUDIT_LAST_UPDATED_ON timestamp not null,
        ITEM_SPECIFICATION_NUMBER varchar(30) not null unique,
        ID_ITEM bigint not null,
        primary key (id)
    );

    create table ITEM.OFFER (
        id bigint not null,
        META_VERSION integer,
        AUDIT_CREATED_BY varchar(15) not null,
        AUDIT_CREATED_ON timestamp not null,
        AUDIT_LAST_UPDATED_BY varchar(15) not null,
        AUDIT_LAST_UPDATED_ON timestamp not null,
        END_DATE date,
        NAME varchar(60) not null unique,
        PRICE_AMOUNT numeric,
        PRICE_CURRENCY varchar(255),
        START_DATE date not null,
        ID_ITEM bigint not null,
        primary key (id)
    );

    create table ITEM.PROMOTION (
        id bigint not null,
        META_VERSION integer,
        AUDIT_CREATED_BY varchar(15) not null,
        AUDIT_CREATED_ON timestamp not null,
        AUDIT_LAST_UPDATED_BY varchar(15) not null,
        AUDIT_LAST_UPDATED_ON timestamp not null,
        END_DATE date,
        NAME varchar(60) not null unique,
        PROMOTIONAL_TEXT varchar(2000) not null,
        START_DATE date not null,
        ID_OFFER bigint not null unique,
        primary key (id)
    );

    create table ITEM.ITEM_ITEM_OPTION_ASSOC (
        ID_ITEM bigint not null,
        ID_ITEM_OPTION bigint not null,
        primary key (ID_ITEM, ID_ITEM_OPTION)
    );

    create table PEOPLE.EMAIL_ADDRESS (
        id bigint not null,
        META_VERSION integer,
        AUDIT_CREATED_BY varchar(15) not null,
        AUDIT_CREATED_ON timestamp not null,
        AUDIT_LAST_UPDATED_BY varchar(15) not null,
        AUDIT_LAST_UPDATED_ON timestamp not null,
        COMMENT varchar(200),
        NAME varchar(30) not null,
        EMAIL_ADDRESS varchar(70) not null unique,
        primary key (id),
        unique (EMAIL_ADDRESS)
    );

    create table PEOPLE.EMPLOYEE (
        id bigint not null,
        META_VERSION integer,
        AUDIT_CREATED_BY varchar(15) not null,
        AUDIT_CREATED_ON timestamp not null,
        AUDIT_LAST_UPDATED_BY varchar(15) not null,
        AUDIT_LAST_UPDATED_ON timestamp not null,
        DEPARTMENT varchar(30) not null,
        EMPLOYEE_NUMBER varchar(30) not null unique,
        ID_WORK_EMAIL_ADDRESS bigint not null,
        ID_PERSON bigint not null unique,
        primary key (id)
    );

    create table PEOPLE.PERSON (
        id bigint not null,
        META_VERSION integer,
        AUDIT_CREATED_BY varchar(15) not null,
        AUDIT_CREATED_ON timestamp not null,
        AUDIT_LAST_UPDATED_BY varchar(15) not null,
        AUDIT_LAST_UPDATED_ON timestamp not null,
        DATE_OF_BIRTH date not null check (DATE_OF_BIRTH < current_date),
        FIRST_NAME varchar(40) not null,
        GENDER varchar(10) not null,
        LAST_NAME varchar(40) not null,
        MIDDLE_NAMES varchar(60),
        SALUTATION varchar(10) not null,
        TITLE varchar(30),
        ID_PRIVATE_RESIDENTIAL_PHONE bigint,
        ID_HOME_ADDRESS bigint not null,
        ID_PRIVATE_MOBILE_PHONE bigint,
        ID_PRIVATE_FAX bigint,
        primary key (id)
    );

    create table PEOPLE.PERSON_ACCOUNT (
        id bigint not null,
        META_VERSION integer,
        AUDIT_CREATED_BY varchar(15) not null,
        AUDIT_CREATED_ON timestamp not null,
        AUDIT_LAST_UPDATED_BY varchar(15) not null,
        AUDIT_LAST_UPDATED_ON timestamp not null,
        ACCOUNT_EXPIRES_ON date,
        PASSWORD varchar(30) not null,
        PASSWORD_LAST_CHANGED_ON timestamp not null,
        SECRET_ANSWER varchar(100),
        SECRET_QUESTION varchar(200),
        USERNAME varchar(20) not null unique,
        ID_PERSON bigint not null unique,
        primary key (id)
    );

    create table PEOPLE.POSTAL_ADDRESS (
        id bigint not null,
        META_VERSION integer,
        AUDIT_CREATED_BY varchar(15) not null,
        AUDIT_CREATED_ON timestamp not null,
        AUDIT_LAST_UPDATED_BY varchar(15) not null,
        AUDIT_LAST_UPDATED_ON timestamp not null,
        COMMENT varchar(200),
        NAME varchar(30) not null,
        CITY varchar(40) not null,
        POSTAL_CODE varchar(10) not null,
        STREET varchar(50) not null,
        STREET_NUMBER varchar(10) not null,
        primary key (id),
        unique (STREET, STREET_NUMBER, POSTAL_CODE)
    );

    create table PEOPLE.PRIVATE_CUSTOMER (
        id bigint not null,
        META_VERSION integer,
        AUDIT_CREATED_BY varchar(15) not null,
        AUDIT_CREATED_ON timestamp not null,
        AUDIT_LAST_UPDATED_BY varchar(15) not null,
        AUDIT_LAST_UPDATED_ON timestamp not null,
        CUSTOMER_NUMBER varchar(30) not null unique,
        ID_INVOICE_ADDRESS bigint,
        ID_SHIPPING_ADDRESS bigint,
        ID_PERSON bigint not null unique,
        primary key (id)
    );

    create table PEOPLE.TELEPHONE_NUMBER (
        id bigint not null,
        META_VERSION integer,
        AUDIT_CREATED_BY varchar(15) not null,
        AUDIT_CREATED_ON timestamp not null,
        AUDIT_LAST_UPDATED_BY varchar(15) not null,
        AUDIT_LAST_UPDATED_ON timestamp not null,
        COMMENT varchar(200),
        NAME varchar(30) not null,
        PHONE_NUMBER varchar(30) not null unique,
        PHONE_TYPE varchar(20) not null,
        primary key (id),
        unique (PHONE_NUMBER)
    );

    alter table COMPANY.COMPANY 
        add constraint FK_COMPANY_MAINEMAIL 
        foreign key (ID_MAIN_EMAIL_ADDRESS) 
        references PEOPLE.EMAIL_ADDRESS;

    alter table COMPANY.COMPANY 
        add constraint FK_COMPANY_MAINPHONE 
        foreign key (ID_MAIN_PHONE_NUMBER) 
        references PEOPLE.TELEPHONE_NUMBER;

    alter table COMPANY.COMPANY 
        add constraint FK_COMPANY_MAINADDR 
        foreign key (ID_MAIN_POSTAL_ADDRESS) 
        references PEOPLE.POSTAL_ADDRESS;

    alter table COMPANY.CORPORATE_CLIENT 
        add constraint FK_CORPCLIENT_INVOICEADDR 
        foreign key (ID_INVOICE_ADDRESS) 
        references PEOPLE.POSTAL_ADDRESS;

    alter table COMPANY.CORPORATE_CLIENT 
        add constraint FK_CORPCLIENT_COMPANY 
        foreign key (ID_COMPANY) 
        references COMPANY.COMPANY;

    alter table COMPANY.CORPORATE_CLIENT 
        add constraint FK_CORPCLIENT_DELIVERYADDR 
        foreign key (ID_DELIVERY_ADDRESS) 
        references PEOPLE.POSTAL_ADDRESS;

    alter table COMPANY.CORPORATE_CLIENT_REPRESENTATIVE 
        add constraint FK_CORPCLIENT_REPCORPCLIENT 
        foreign key (ID_CORPORATE_CLIENT) 
        references COMPANY.CORPORATE_CLIENT 
        on delete cascade;

    alter table COMPANY.CORPORATE_CLIENT_REPRESENTATIVE 
        add constraint FK_CORPCLIENT_REPWORKEMAIL 
        foreign key (ID_WORK_EMAIL_ADDRESS) 
        references PEOPLE.EMAIL_ADDRESS;

    alter table COMPANY.CORPORATE_CLIENT_REPRESENTATIVE 
        add constraint FK_CORPCLIENT_REPPARENTPERSON 
        foreign key (ID_PARENT_PERSON) 
        references PEOPLE.PERSON;

    alter table COMPANY.CORPORATE_CLIENT_REPRESENTATIVE 
        add constraint FK_CORPCLIENT_REPWORKADDR 
        foreign key (ID_WORK_ADDRESS) 
        references PEOPLE.POSTAL_ADDRESS;

    alter table COMPANY.CORPORATE_CLIENT_REPRESENTATIVE 
        add constraint FK_CORPCLIENT_REPWORKPHONE 
        foreign key (ID_WORK_PHONE_NUMBER) 
        references PEOPLE.TELEPHONE_NUMBER;

    alter table COMPANY.ITEM_SUPPLIER 
        add constraint FK_ITEMSUPPLIER_COMPANY 
        foreign key (ID_COMPANY) 
        references COMPANY.COMPANY;

    alter table COMPANY.ITEM_SUPPLIER_REPRESENTATIVE 
        add constraint FK_ITEMSUPPLIER_REPWORKEMAIL 
        foreign key (ID_WORK_EMAIL_ADDRESS) 
        references PEOPLE.EMAIL_ADDRESS;

    alter table COMPANY.ITEM_SUPPLIER_REPRESENTATIVE 
        add constraint FK_ITEMSUPPLIER_REPITEMSUPPLIER 
        foreign key (ID_ITEM_SUPPLIER) 
        references COMPANY.ITEM_SUPPLIER 
        on delete cascade;

    alter table COMPANY.ITEM_SUPPLIER_REPRESENTATIVE 
        add constraint FK_ITEMSUPPLIER_REPPARENTPERSON 
        foreign key (ID_PARENT_PERSON) 
        references PEOPLE.PERSON;

    alter table COMPANY.ITEM_SUPPLIER_REPRESENTATIVE 
        add constraint FK_ITEMSUPPLIER_REPWORKADDR 
        foreign key (ID_WORK_ADDRESS) 
        references PEOPLE.POSTAL_ADDRESS;

    alter table COMPANY.ITEM_SUPPLIER_REPRESENTATIVE 
        add constraint FK_ITEMSUPPLIER_REPWORKPHONE 
        foreign key (ID_WORK_PHONE_NUMBER) 
        references PEOPLE.TELEPHONE_NUMBER;

    alter table ITEM.ITEM 
        add constraint FK_ITEM_ITEMSUPPLIER 
        foreign key (ID_ITEM_SUPPLIER) 
        references COMPANY.ITEM_SUPPLIER;

    alter table ITEM.ITEM_OPTION_SPECIFICATION 
        add constraint FK_ITEMOPTIONSPEC_ITEMOPTION 
        foreign key (ID_ITEM_OPTION) 
        references ITEM.ITEM_OPTION;

    alter table ITEM.ITEM_OPTION_SPECIFICATION 
        add constraint FK_ITEMOPTIONSPEC_ITEMSPEC 
        foreign key (ID_ITEM_SPECIFICATION) 
        references ITEM.ITEM_SPECIFICATION 
        on delete cascade;

    alter table ITEM.ITEM_SPECIFICATION 
        add constraint FK_ITEMSPEC_ITEM 
        foreign key (ID_ITEM) 
        references ITEM.ITEM 
        on delete cascade;

    alter table ITEM.OFFER 
        add constraint FK_OFFER_ITEM 
        foreign key (ID_ITEM) 
        references ITEM.ITEM;

    alter table ITEM.PROMOTION 
        add constraint FK_PROMOTION_OFFER 
        foreign key (ID_OFFER) 
        references ITEM.OFFER;

    alter table ITEM.ITEM_ITEM_OPTION_ASSOC 
        add constraint FK_ITEMOPTION_ITEM 
        foreign key (ID_ITEM_OPTION) 
        references ITEM.ITEM_OPTION;

    alter table ITEM.ITEM_ITEM_OPTION_ASSOC 
        add constraint FK_ITEM_ITEMOPTION 
        foreign key (ID_ITEM) 
        references ITEM.ITEM;

    alter table PEOPLE.EMPLOYEE 
        add constraint FK_EMPLOYEE_WORKEMAILADDR 
        foreign key (ID_WORK_EMAIL_ADDRESS) 
        references PEOPLE.EMAIL_ADDRESS;

    alter table PEOPLE.EMPLOYEE 
        add constraint FK_EMPLOYEE_PERSON 
        foreign key (ID_PERSON) 
        references PEOPLE.PERSON;

    alter table PEOPLE.PERSON 
        add constraint FK_PERSON_HOMEADDR 
        foreign key (ID_HOME_ADDRESS) 
        references PEOPLE.POSTAL_ADDRESS;

    alter table PEOPLE.PERSON 
        add constraint FK_PERSON_PRVMOBPHONE 
        foreign key (ID_PRIVATE_MOBILE_PHONE) 
        references PEOPLE.TELEPHONE_NUMBER;

    alter table PEOPLE.PERSON 
        add constraint FK_PERSON_PRVRESPHONE 
        foreign key (ID_PRIVATE_RESIDENTIAL_PHONE) 
        references PEOPLE.TELEPHONE_NUMBER;

    alter table PEOPLE.PERSON 
        add constraint FK_PERSON_PRVFAX 
        foreign key (ID_PRIVATE_FAX) 
        references PEOPLE.TELEPHONE_NUMBER;

    alter table PEOPLE.PERSON_ACCOUNT 
        add constraint FK_ACCOUNT_PERSON 
        foreign key (ID_PERSON) 
        references PEOPLE.PERSON;

    alter table PEOPLE.PRIVATE_CUSTOMER 
        add constraint FK_PRIVATECUST_SHIPPINGADDR 
        foreign key (ID_SHIPPING_ADDRESS) 
        references PEOPLE.POSTAL_ADDRESS;

    alter table PEOPLE.PRIVATE_CUSTOMER 
        add constraint FK_PRIVATECUST_INVOICEADDR 
        foreign key (ID_INVOICE_ADDRESS) 
        references PEOPLE.POSTAL_ADDRESS;

    alter table PEOPLE.PRIVATE_CUSTOMER 
        add constraint FK_PRIVATECUST_PERSON 
        foreign key (ID_PERSON) 
        references PEOPLE.PERSON;

    create sequence PEOPLE.ID_SEQ_ACCOUNT;

    create sequence COMPANY.ID_SEQ_COMPANY;

    create sequence PEOPLE.ID_SEQ_CONTACT_CHANNEL;

    create sequence COMPANY.ID_SEQ_CORPORATE_CLIENT;

    create sequence COMPANY.ID_SEQ_CORP_CLIENT_REP;

    create sequence PEOPLE.ID_SEQ_EMPLOYEE;

    create sequence ITEM.ID_SEQ_ITEM;

    create sequence ITEM.ID_SEQ_ITEM_OPTION;

    create sequence ITEM.ID_SEQ_ITEM_OPTION_SPEC;

    create sequence ITEM.ID_SEQ_ITEM_SPEC;

    create sequence COMPANY.ID_SEQ_ITEM_SUPPLIER;

    create sequence COMPANY.ID_SEQ_ITEM_SUPPLIER_REP;

    create sequence ITEM.ID_SEQ_OFFER;

    create sequence PEOPLE.ID_SEQ_PEOPLE;

    create sequence PEOPLE.ID_SEQ_PRIVATE_CUSTOMER;

    create sequence ITEM.ID_SEQ_PROMOTION;
