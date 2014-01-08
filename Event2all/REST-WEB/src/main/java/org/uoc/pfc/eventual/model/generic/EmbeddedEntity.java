package org.uoc.pfc.eventual.model.generic;

import org.bson.types.ObjectId;

public abstract class EmbeddedEntity extends Entity {

	public EmbeddedEntity() {
		super();
		id = new ObjectId();
	}
}