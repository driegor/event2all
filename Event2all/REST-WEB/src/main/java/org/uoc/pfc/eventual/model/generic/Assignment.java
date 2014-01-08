package org.uoc.pfc.eventual.model.generic;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;

public class Assignment {

	ObjectId id;
	ObjectId tx;
	boolean owner;
	DateTime date;

	public Assignment(ObjectId tx, ObjectId id, boolean owner) {
		this.id = id;
		this.tx = tx;
		this.owner = owner;
		this.date = new DateTime();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public DateTime getDate() {
		return date;
	}

	public void setDate(DateTime date) {
		this.date = date;
	}

	public ObjectId getTx() {
		return tx;
	}

	public void setTx(ObjectId tx) {
		this.tx = tx;
	}

	public boolean isOwner() {
		return owner;
	}

	public void setOwner(boolean owner) {
		this.owner = owner;
	}

}
