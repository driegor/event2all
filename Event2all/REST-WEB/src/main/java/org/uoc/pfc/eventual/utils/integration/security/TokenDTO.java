package org.uoc.pfc.eventual.utils.integration.security;

public class TokenDTO {

    String token;
    String user;

    public TokenDTO(String token) {
	this.token = token;
    }

    public TokenDTO(String user, String token) {
	this.user = user;
	this.token = token;
    }

    public TokenDTO() {

    }

    public String getUser() {
	return user;
    }

    public void setUser(String user) {
	this.user = user;
    }

    public String getToken() {
	return token;
    }

    public void setToken(String token) {
	this.token = token;
    }

}
