package org.uoc.pfc.eventual.utils.token;

import org.apache.commons.lang.RandomStringUtils;

public class TokenGeneratorUtils {

	// a mayor numero menor posibilidad de colision
	public static final int DEFAULT_LENGHT = 16;

	// este método genera una clave aleatoría que se utilizará como token para asignar de forma automática un usuario a un evento
	// sin la necesidad de que el administrador del mismo lo haga de forma manual
	// Este método no garantiza que el token sea unico, pueden haber colisiones, con lo que se deberá comprobar que este token
	// no exista previamente asignado a otro usuario

	public static String getRandomToken(int length) {
		return RandomStringUtils.random(length, Boolean.TRUE, Boolean.TRUE);
	}

}
