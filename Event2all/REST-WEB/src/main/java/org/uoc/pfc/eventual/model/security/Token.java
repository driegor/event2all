package org.uoc.pfc.eventual.model.security;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.uoc.pfc.eventual.model.generic.AuditEntity;

@Document
public class Token extends AuditEntity {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    String		    user;
    String		    token;

    public Token(String user, String token) {
	this.user = user;
	this.token = token;
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

    @Override
    public ObjectId getId() {
	return id;
    }

    @Override
    public void setId(ObjectId id) {
	this.id = id;

    }
}
