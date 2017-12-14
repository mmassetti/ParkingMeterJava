DROP DATABASE IF EXISTS parquimetros;
DROP USER IF EXISTS admin@'localhost';
DROP USER IF EXISTS 'venta'@'%';
DROP USER IF EXISTS 'inspector'@'%';
DROP USER IF EXISTS 'parquimetro'@'%';


# Se crea la base de Datos
CREATE DATABASE parquimetros;

# Se selecciona la base de datos sobre la cual se van a hacer modificaciones
USE parquimetros;

#-------------------------------------------------------------------------
# Se crean las tablas para las entidades y relaciones

CREATE TABLE conductores (
 dni INT(11) UNSIGNED NOT NULL, 
 nombre VARCHAR(45) NOT NULL, 
 apellido VARCHAR(45) NOT NULL, 
 direccion VARCHAR(45) NOT NULL, 
 telefono VARCHAR(45) NOT NULL, 
 registro INT(11) UNSIGNED NOT NULL, 
 
 CONSTRAINT pk_conductores 
 PRIMARY KEY (dni)
 ) ENGINE=InnoDB;

 
#
CREATE TABLE automoviles (
 patente CHAR(6) NOT NULL,
 marca VARCHAR(45) NOT NULL, 
 modelo VARCHAR(45) NOT NULL, 
 color VARCHAR(45) NOT NULL, 
 dni INT(11) UNSIGNED NOT NULL, 
 
 CONSTRAINT pk_automoviles 
 PRIMARY KEY (patente),

 FOREIGN KEY (dni) REFERENCES conductores (dni)
 ON DELETE RESTRICT ON UPDATE CASCADE
 
) ENGINE=InnoDB;

#
CREATE TABLE tipos_tarjeta (
 tipo VARCHAR(45) NOT NULL,
 descuento DECIMAL(3,2) UNSIGNED NOT NULL, 
 
 CONSTRAINT pk_tipos_tarjeta
 PRIMARY KEY (tipo)
 ) ENGINE=InnoDB;

#
CREATE TABLE tarjetas (
 id_tarjeta INT(11) UNSIGNED AUTO_INCREMENT NOT NULL, 
 saldo DECIMAL(5,2) NOT NULL, # MIRAR ESTO para poner solo dos digitos
 tipo VARCHAR(45) NOT NULL,
 patente CHAR(6) NOT NULL,

 CONSTRAINT pk_tarjetas	
 PRIMARY KEY (id_tarjeta),

 FOREIGN KEY (tipo) REFERENCES tipos_tarjeta (tipo)
 ON DELETE RESTRICT ON UPDATE CASCADE,
 FOREIGN KEY (patente) REFERENCES automoviles (patente)
 ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

#
CREATE TABLE inspectores ( 
 legajo INT (11) UNSIGNED NOT NULL,
 dni INT(11) UNSIGNED NOT NULL, 
 nombre VARCHAR(45) NOT NULL, 
 apellido VARCHAR(45) NOT NULL, 
 password VARCHAR(32) NOT NULL, #agregar hash md5

 CONSTRAINT pk_inspectores	
 PRIMARY KEY (legajo)
) ENGINE=InnoDB;

#
CREATE TABLE ubicaciones (
 calle VARCHAR(45) NOT NULL,
 altura INT(11) UNSIGNED NOT NULL,  
 tarifa DECIMAL(5,2) UNSIGNED NOT NULL,

 CONSTRAINT pk_ubicaciones	
 PRIMARY KEY (calle,altura)
) ENGINE=InnoDB;


#
CREATE TABLE parquimetros (
 id_parq INT(11) UNSIGNED NOT NULL,
 numero INT(11) UNSIGNED NOT NULL,
 calle VARCHAR(45) NOT NULL,
 altura INT(11) UNSIGNED NOT NULL,

 CONSTRAINT pk_parquimetros	
 PRIMARY KEY (id_parq),

  FOREIGN KEY (calle,altura) REFERENCES ubicaciones (calle,altura)
  ON DELETE RESTRICT ON UPDATE CASCADE
  
) ENGINE=InnoDB;

#
CREATE TABLE accede(
 legajo INT (11) UNSIGNED NOT NULL,
 id_parq INT(11) UNSIGNED NOT NULL,
 fecha DATE NOT NULL,
 hora TIME(2) NOT NULL,

 CONSTRAINT pk_accede	
  PRIMARY KEY (id_parq,fecha,hora),
 
  FOREIGN KEY (legajo) REFERENCES inspectores (legajo)
  ON DELETE RESTRICT ON UPDATE CASCADE,
  FOREIGN KEY (id_parq) REFERENCES parquimetros (id_parq)
  ON DELETE RESTRICT ON UPDATE CASCADE,
 
  KEY (legajo)
) ENGINE=InnoDB;


#
CREATE TABLE estacionamientos (
 id_tarjeta INT(11) UNSIGNED AUTO_INCREMENT NOT NULL, 
 id_parq INT(11) UNSIGNED NOT NULL,
 fecha_ent DATE NOT NULL,
 hora_ent TIME(2) NOT NULL,
 fecha_sal DATE NULL,
 hora_sal TIME(2) NULL,

 CONSTRAINT pk_estacionamientos	
 PRIMARY KEY (id_parq,fecha_ent,hora_ent),

 FOREIGN KEY (id_tarjeta) REFERENCES tarjetas (id_tarjeta) 
 ON DELETE RESTRICT ON UPDATE CASCADE,
 FOREIGN KEY (id_parq) REFERENCES parquimetros (id_parq)
 ON DELETE RESTRICT ON UPDATE CASCADE

) ENGINE=InnoDB;


#
CREATE TABLE asociado_con(
 id_asociado_con INT(11) UNSIGNED AUTO_INCREMENT NOT NULL, 
 legajo INT (11) UNSIGNED NOT NULL,
 calle VARCHAR(45) NOT NULL,
 altura INT(11) UNSIGNED NOT NULL,  
 dia CHAR(2) NOT NULL,
 turno CHAR(1) NOT NULL,
 

  CONSTRAINT pk_asociado_con	
  PRIMARY KEY (id_asociado_con),
  
  FOREIGN KEY (legajo) REFERENCES inspectores (legajo)
  ON DELETE RESTRICT ON UPDATE CASCADE,
  FOREIGN KEY (calle,altura) REFERENCES ubicaciones (calle,altura)
  ON DELETE RESTRICT ON UPDATE CASCADE
 
) ENGINE=InnoDB;


#
CREATE TABLE multa(
 numero INT(11) UNSIGNED AUTO_INCREMENT NOT NULL,
 fecha DATE NOT NULL,
 hora TIME(2) NOT NULL,
 patente CHAR(6) NOT NULL,
 id_asociado_con INT(11) UNSIGNED NOT NULL,
 
 CONSTRAINT pk_multa
  PRIMARY KEY (numero),
 
  FOREIGN KEY (patente) REFERENCES automoviles (patente)
  ON DELETE RESTRICT ON UPDATE CASCADE,
  FOREIGN KEY (id_asociado_con) REFERENCES asociado_con (id_asociado_con)
  ON DELETE RESTRICT ON UPDATE CASCADE

) ENGINE=InnoDB;

#
#
CREATE TABLE ventas(
  id_tarjeta INT UNSIGNED NOT NULL,
  tipo_tarjeta VARCHAR(45) NOT NULL,
  saldo DECIMAL(5,2) NOT NULL,
  fecha DATE NOT NULL,
  hora TIME(2) NOT NULL,

  CONSTRAINT pk_ventas
  PRIMARY KEY (id_tarjeta),

  FOREIGN KEY (id_tarjeta) REFERENCES tarjetas(id_tarjeta)
  ON DELETE RESTRICT ON UPDATE CASCADE,
  FOREIGN KEY (tipo_tarjeta) REFERENCES tarjetas (tipo)
  ON DELETE RESTRICT ON UPDATE CASCADE

) ENGINE=InnoDB;



#-------------------------------------------------------------------------
# Se crean las vistas

CREATE VIEW estacionados AS 
SELECT p.calle,p.altura,t.patente 
FROM (parquimetros as p NATURAL JOIN estacionamientos as e) NATURAL JOIN tarjetas as t 
WHERE e.fecha_sal is NULL AND e.hora_sal is NULL;


# Se crean los usuarios y se otorgan sus privilegios
# Usuario admin:
CREATE USER 'admin'@'localhost' IDENTIFIED BY 'admin';
GRANT ALL PRIVILEGES ON parquimetros.* TO 'admin'@'localhost' WITH GRANT OPTION;


#Creacion de stored procedures
delimiter !
CREATE PROCEDURE conectar (IN id_tarjeta INTEGER, IN id_parq INTEGER)
BEGIN
DECLARE saldo DECIMAL(5,2);
DECLARE tarifa DECIMAL(5, 2);
DECLARE descuento DECIMAL(3,2);
DECLARE minutos DECIMAL(20,2);
DECLARE parq_actual int;
DECLARE saldo_nuevo int;

# Declaro variables locales para recuperar los errores 
DECLARE codigo_SQL  CHAR(5) DEFAULT '00000';	 
DECLARE codigo_MYSQL INT DEFAULT 0;
DECLARE mensaje_error TEXT;

DECLARE EXIT HANDLER FOR SQLEXCEPTION 	 	 
BEGIN #En caso de una excepción SQLEXCEPTION retrocede la transacción y devuelve el código de error especifico de MYSQL (MYSQL_ERRNO), el código de error SQL  (SQLSTATE) y el mensaje de error  
	GET DIAGNOSTICS CONDITION 1 codigo_MYSQL= MYSQL_ERRNO, codigo_SQL= RETURNED_SQLSTATE, mensaje_error= MESSAGE_TEXT;
	SELECT 'SQLEXCEPTION!, Transaccion abortada' AS Resultado, codigo_MySQL, codigo_SQL,  mensaje_error;		
	ROLLBACK;
END;

Start TRANSACTION;
	#Verificar que exita el parquimetro o la tarjeta
	IF EXISTS (SELECT p.id_parq FROM parquimetros as p WHERE id_parq=p.id_parq) THEN
		BEGIN
			IF EXISTS (SELECT t.id_tarjeta FROM tarjetas as t WHERE id_tarjeta=t.id_tarjeta) THEN
				BEGIN
					/* desc   = */ SELECT c.descuento INTO descuento FROM tarjetas t NATURAL JOIN tipos_tarjeta as c WHERE id_tarjeta=t.id_tarjeta LOCK IN SHARE MODE;	
					
					IF EXISTS (SELECT * FROM estacionamientos as e WHERE id_tarjeta=e.id_tarjeta AND e.fecha_sal is NULL AND e.hora_sal is NULL ORDER BY fecha_ent,hora_ent DESC LIMIT 1 FOR UPDATE) THEN
						BEGIN #el cliente se va del estacionamiento
							
							#obtenemos el saldo, parq_actual, tarifa, minutos que estuvo y su nuevo saldo
							/* saldo       = */ SELECT t.saldo INTO saldo FROM tarjetas as t WHERE id_tarjeta=t.id_tarjeta FOR UPDATE;
							/* parq_actual = */ SELECT e.id_parq INTO parq_actual FROM estacionamientos as e WHERE e.id_tarjeta = id_tarjeta AND e.fecha_sal IS NULL AND e.hora_sal IS NULL LOCK IN SHARE MODE;
							/* tarifa      = */ SELECT u.tarifa INTO tarifa FROM ubicaciones as u NATURAL JOIN parquimetros as p WHERE parq_actual=p.id_parq;
							/* minutos     = */ SELECT TRUNCATE((TIME_TO_SEC(TIMEDIFF(now(),CONCAT(e.fecha_ent,' ',e.hora_ent)))/60), 2) INTO minutos FROM estacionamientos as e WHERE id_tarjeta=e.id_tarjeta AND parq_actual=e.id_parq AND e.fecha_sal is NULL AND e.hora_sal is NULL;
							/* saldo_nuevo = */	SET saldo_nuevo = TRUNCATE((saldo-(minutos*tarifa*(1-descuento))),2);					
							
							#actualizar para cerra estacionamiento, primero agrego salida, despues modifico el saldo
							UPDATE estacionamientos as e SET e.fecha_sal=now(), e.hora_sal=now() WHERE id_tarjeta=e.id_tarjeta AND parq_actual=e.id_parq AND e.fecha_sal is NULL AND e.hora_sal is NULL; 
							
							#Resultado: verificar que el saldo sea mayor a '-999' y actulizar saldo en tarjeta
							IF (saldo_nuevo<-999.99) THEN
								BEGIN
									UPDATE tarjetas as t SET t.saldo = '-999.99' WHERE t.id_tarjeta=id_tarjeta;
									SELECT 'Cierre' as Operacion, TRUNCATE (minutos,2) as 'Tiempo Transcurrido (min.)', '-999.99' as 'Saldo Actualizado';
								END;
							ELSE	
								BEGIN
									UPDATE tarjetas AS t SET t.saldo=saldo_nuevo WHERE t.id_tarjeta=id_tarjeta;
									SELECT 'Cierre' AS Operacion, TRUNCATE (minutos,2) as 'Tiempo Transcurrido (MIN)', saldo_nuevo as 'Saldo Actualizado';
								END;
							END IF;	
						END;
					ELSE #el cliete entra al estacionamiento
						BEGIN
							#obetenemos el saldo de la tarjeta
							/* saldo =  */ SELECT t.saldo INTO saldo FROM tarjetas as t WHERE id_tarjeta=t.id_tarjeta;
							/* tarifa = */ SELECT u.tarifa INTO tarifa FROM ubicaciones as u NATURAL JOIN parquimetros p WHERE id_parq = p.id_parq LOCK IN SHARE MODE;

							IF (saldo>0) THEN
								BEGIN
									INSERT INTO estacionamientos (id_tarjeta,id_parq,fecha_ent,hora_ent) VALUES (id_tarjeta, id_parq, now(), now());
									SELECT 'Apertura' as Operacion, 'Exito' as Resultado, TRUNCATE ((saldo/(tarifa*(1-descuento))),2) AS 'Tiempo disponible (min.)';	
								END;
							ELSE
								BEGIN
									SELECT 'Apertura' as Operacion, 'Error' as Resultado, 'Saldo insuficiente' as Motivo;
								END;
							END IF;
							
						END;
					END IF;	
				END;
			ELSE
				BEGIN
					SELECT 'Error' as Resultado, 'id_tarjeta inexistente' as Motivo;
				END;				
			END IF;
		END;
	ELSE
		BEGIN
			SELECT 'Error' as Resultado, 'id_parq inexistente' as Motivo;
		END;
	END IF;
COMMIT;
END;!

delimiter ;


# Usuario venta:
CREATE USER 'venta'@'%' IDENTIFIED BY 'venta';
GRANT INSERT ON parquimetros.tarjetas TO 'venta'; 
GRANT SELECT ON parquimetros.tipos_tarjeta TO 'venta';
GRANT SELECT ON parquimetros.automoviles TO 'venta';

# Usuario inspector:
CREATE USER 'inspector'@'%' IDENTIFIED BY 'inspector';
GRANT SELECT ON parquimetros.estacionados TO 'inspector';
GRANT SELECT ON parquimetros.inspectores TO 'inspector';
GRANT SELECT ON parquimetros.parquimetros TO 'inspector';
GRANT SELECT ON parquimetros.asociado_con TO 'inspector';
GRANT SELECT ON parquimetros.accede TO 'inspector'; 
GRANT INSERT ON parquimetros.accede TO 'inspector'; 
GRANT SELECT ON parquimetros.automoviles TO 'inspector';
GRANT SELECT ON parquimetros.multa to 'inspector';
GRANT INSERT ON parquimetros.multa to 'inspector'; 

#Usuario parquimetro:
CREATE USER 'parquimetro'@'%' IDENTIFIED BY 'parq';
GRANT SELECT ON parquimetros.parquimetros TO 'parquimetro'@'%';
GRANT SELECT ON parquimetros.estacionamientos TO 'parquimetro'@'%';
GRANT SELECT ON parquimetros.tarjetas TO 'parquimetro'@'%';
GRANT INSERT ON parquimetros.parquimetros TO 'parquimetro'@'%';
GRANT INSERT ON parquimetros.estacionamientos TO 'parquimetro'@'%';
GRANT EXECUTE ON PROCEDURE parquimetros.conectar TO 'parquimetro'@'%';
GRANT SELECT ON mysql.proc TO 'parquimetro'@'%';


/*Implemente un trigger para llevar una traza de todas las ventas de tarjetas realizadas. Dicho
#trigger se activara cuando se inserta una nueva tarjeta y guardara en la tabla ventas la siguiente
#informacion: el id de la tarjeta, el tipo de la tarjeta, el saldo inicial, la fecha y hora actual. La
#sentencia de creacion de dicho trigger debera anadirse al archivo \parquimetros.sql".*/

delimiter !

CREATE TRIGGER triggerVentas AFTER INSERT ON tarjetas FOR EACH ROW

BEGIN

	INSERT INTO ventas VALUES (NEW.id_tarjeta,NEW.tipo,NEW.saldo,now(),now());

END; !

delimiter ;
