INSERT INTO conductores(dni,nombre,apellido,direccion,telefono,registro) VALUES (38000001,'Juan','Perez','Calle 1001','15245875',10001);
INSERT INTO conductores(dni,nombre,apellido,direccion,telefono,registro) VALUES (38000002,'Manuel','Safe','Calle 1002','15245875',10002);
INSERT INTO conductores(dni,nombre,apellido,direccion,telefono,registro) VALUES (38000003,'Adrian','Hernandez','Calle 1003','15245875',10002);
INSERT INTO conductores(dni,nombre,apellido,direccion,telefono,registro) VALUES (38000004,'Ana','Garcia','Calle 1004','15245875',10004);


INSERT INTO automoviles(patente,marca,modelo,color,dni) VALUES ('AAA111','Fiat 1',2001,'Rojo',38000001);
INSERT INTO automoviles(patente,marca,modelo,color,dni) VALUES ('BBB222','Ford 2',2002,'Verde',38000002);
INSERT INTO automoviles(patente,marca,modelo,color,dni) VALUES ('CCC333','Chevrolet 3',2003,'Gris',38000003);
INSERT INTO automoviles(patente,marca,modelo,color,dni) VALUES ('DDD444','Ferrari 4',2004,'Azul',38000004);


INSERT INTO tipos_tarjeta(tipo,descuento) VALUES ('Oro',0.50);
INSERT INTO tipos_tarjeta(tipo,descuento) VALUES ('Plata',0.30);
INSERT INTO tipos_tarjeta(tipo,descuento) VALUES ('Bronce',0.10);
INSERT INTO tipos_tarjeta(tipo,descuento) VALUES ('Comun',0.00);


INSERT INTO tarjetas(id_tarjeta,saldo,tipo,patente) VALUES (101,100.00,'Comun','AAA111');
INSERT INTO tarjetas(id_tarjeta,saldo,tipo,patente) VALUES (102,50.40,'Oro','AAA111');
INSERT INTO tarjetas(id_tarjeta,saldo,tipo,patente) VALUES (201,500.15,'Plata','BBB222');
INSERT INTO tarjetas(id_tarjeta,saldo,tipo,patente) VALUES (301,600.54,'Oro','CCC333');
INSERT INTO tarjetas(id_tarjeta,saldo,tipo,patente) VALUES (401,800.10,'Bronce','DDD444');


INSERT INTO ubicaciones(calle,altura,tarifa) VALUES ('Av. Alem',200,0.40);
INSERT INTO ubicaciones(calle,altura,tarifa) VALUES ('Av. Colon',100,0.10);
INSERT INTO ubicaciones(calle,altura,tarifa) VALUES ('9 de julio',2015,0.10);
INSERT INTO ubicaciones(calle,altura,tarifa) VALUES ('Av. Alem',500,0.40);


INSERT INTO parquimetros(id_parq,numero,calle,altura) VALUES (1,10001,'9 de julio',2015);
INSERT INTO parquimetros(id_parq,numero,calle,altura) VALUES (2,10002,'Av. Alem',200);
INSERT INTO parquimetros(id_parq,numero,calle,altura) VALUES (3,10003,'Av. Alem',500);
INSERT INTO parquimetros(id_parq,numero,calle,altura) VALUES (4,10004,'Av. Colon',100);

INSERT INTO estacionamientos(id_tarjeta,id_parq,fecha_ent,hora_ent) VALUES (101,1,now(),now());
INSERT INTO estacionamientos(id_tarjeta,id_parq,fecha_ent,hora_ent) VALUES (102,3,now(),now());
INSERT INTO estacionamientos(id_tarjeta,id_parq,fecha_ent,hora_ent) VALUES (201,4,now(),now());
INSERT INTO estacionamientos(id_tarjeta,id_parq,fecha_ent,hora_ent) VALUES (401,2,now(),now());

INSERT INTO inspectores(legajo,dni,nombre,apellido,password) VALUES (10000,20000001,'Armando','Lopez',MD5('452145'));
INSERT INTO inspectores(legajo,dni,nombre,apellido,password) VALUES (20000,20000002,'Pablo Armando','Hernandez',MD5('452145'));
INSERT INTO inspectores(legajo,dni,nombre,apellido,password) VALUES (30000,20000003,'Agustin','Garcia',MD5('452145'));


INSERT INTO asociado_con(legajo,calle,altura,dia,turno) VALUES (10000,'Av. Alem',200,'Lu','M');
INSERT INTO asociado_con(legajo,calle,altura,dia,turno) VALUES (10000,'9 de julio',2015,'Mi','T');
INSERT INTO asociado_con(legajo,calle,altura,dia,turno) VALUES (10000,'9 de julio',2015,'Ma','T');
INSERT INTO asociado_con(legajo,calle,altura,dia,turno) VALUES (20000,'Av. Colon',100,'Sa','M');
INSERT INTO asociado_con(legajo,calle,altura,dia,turno) VALUES (20000,'Av. Alem',200,'Ma','T');
INSERT INTO asociado_con(legajo,calle,altura,dia,turno) VALUES (30000,'Av. Alem',500,'Ju','M');


INSERT INTO accede(legajo,id_parq,fecha,hora) VALUES (10000,2,'2017-09-12','10:16:25');
INSERT INTO accede(legajo,id_parq,fecha,hora) VALUES (10000,1,'2017-09-12','19:25:25');
INSERT INTO accede(legajo,id_parq,fecha,hora) VALUES (20000,4,'2017-09-12','10:16:25');
INSERT INTO accede(legajo,id_parq,fecha,hora) VALUES (20000,2,'2017-09-12','17:16:25');
INSERT INTO accede(legajo,id_parq,fecha,hora) VALUES (30000,3,'2017-09-12','10:16:25');


INSERT INTO multa(fecha,hora,patente,id_asociado_con) VALUES ('2017-09-12','10:23:45','AAA111',1);
INSERT INTO multa(fecha,hora,patente,id_asociado_con) VALUES ('2017-09-12','19:31:45','BBB222',2);
INSERT INTO multa(fecha,hora,patente,id_asociado_con) VALUES ('2017-09-12','10:25:45','DDD444',3);

