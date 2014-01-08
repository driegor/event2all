package org.uoc.pfc.eventual.model.generic;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.data.domain.Auditable;

public abstract class AuditEntity extends Entity implements Auditable<ObjectId, ObjectId> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4820550038970159750L;
	private ObjectId createdBy;
	private DateTime createdDate;
	private ObjectId lastModifiedBy;
	private DateTime lastModifiedDate;

	@Override
	public ObjectId getCreatedBy() {
		return createdBy;
	}

	@Override
	public DateTime getCreatedDate() {
		return createdDate;
	}

	@Override
	public void setCreatedDate(DateTime createdDate) {
		this.createdDate = createdDate;
	}

	@Override
	public ObjectId getLastModifiedBy() {
		return lastModifiedBy;
	}

	@Override
	public void setLastModifiedBy(ObjectId lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	@Override
	public DateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

	@Override
	public void setLastModifiedDate(DateTime lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	@Override
	public void setCreatedBy(ObjectId createdBy) {
		this.createdBy = createdBy;
	}

}
