drop all OBJECTS;

create table TABLE_A
(
    FIELD_A         NUMBER        default 0                                                       not null,
    FIELD_B         NUMBER        default 0                                                       not null,
    FIELD_C         VARCHAR(10) default ' '                                                     not null
);

create table TABLE_B
(
    FIELD_A         NUMBER        default 0                                                       not null,
    FIELD_D         NUMBER        default 0                                                       not null,
    FIELD_E         VARCHAR(5)  default ' '                                                     not null
);

create table TABLE_C
(
    FIELD_A         NUMBER        default 0                                                       not null,
    FIELD_B         VARCHAR(10) default ' '                                                    not null,
    FIELD_C         NUMBER        default 0                                                       not null
);

INSERT INTO TABLE_A (FIELD_A, FIELD_B, FIELD_C) VALUES (195, 410, 'DSFGT4510A');
INSERT INTO TABLE_A (FIELD_A, FIELD_B, FIELD_C) VALUES (210, 104, 'DGFHRTNR5A');
INSERT INTO TABLE_A (FIELD_A, FIELD_B, FIELD_C) VALUES (224, 101, '151DDRVTBA');

INSERT INTO TABLE_B (FIELD_A, FIELD_D, FIELD_E) VALUES (195, 41017100, 'HBTVB');
INSERT INTO TABLE_B (FIELD_A, FIELD_D, FIELD_E) VALUES (204, 41414404, 'GSDRB');
INSERT INTO TABLE_B (FIELD_A, FIELD_D, FIELD_E) VALUES (224, 12042107, 'HTYRB');
INSERT INTO TABLE_B (FIELD_A, FIELD_D, FIELD_E) VALUES (414, 21410574, 'KGFHT');
INSERT INTO TABLE_B (FIELD_A, FIELD_D, FIELD_E) VALUES (821, 54334453, 'TDSEV');

INSERT INTO TABLE_C (FIELD_A, FIELD_B, FIELD_C) VALUES (195, 'DSFGT4510A', 410);
INSERT INTO TABLE_C (FIELD_A, FIELD_B, FIELD_C) VALUES (210, 'DGFHRTNR5A', 104);
INSERT INTO TABLE_C (FIELD_A, FIELD_B, FIELD_C) VALUES (224, '151DDRVTBA', 101);
INSERT INTO TABLE_C (FIELD_A, FIELD_B, FIELD_C) VALUES (433, 'FFRVCS78CE', 344);
