package org.uoc.pfc.eventual.repository.impl.callback;

import org.bson.types.ObjectId;

public interface ReferenceIdCallBack<E2> {

	public void removeCallBack(ObjectId id, ObjectId referenceId, ObjectId tx);

	public void addCallBack(ObjectId id, ObjectId referenceId, ObjectId tx);

}
