package org.uoc.pfc.eventual.model.generic;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

public abstract class Entity {

	@Id
	protected ObjectId id;

	public abstract ObjectId getId();

	public abstract void setId(ObjectId id);

	public boolean isNew() {
		return id == null;
	}
}