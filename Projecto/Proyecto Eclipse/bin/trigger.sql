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