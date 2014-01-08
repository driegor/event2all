package org.uoc.pfc.eventual.repository.exception;

import com.mongodb.MongoException;

public class MongoDBException extends MongoException {

	public MongoDBException(String msg, Throwable t) {
		super(msg, t);
	}

	public MongoDBException(String msg) {
		super(msg);
	}

	private static final long serialVersionUID = 1L;

}
