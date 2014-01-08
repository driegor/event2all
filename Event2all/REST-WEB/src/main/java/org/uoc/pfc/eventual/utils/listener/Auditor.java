package org.uoc.pfc.eventual.utils.listener;

import org.bson.types.ObjectId;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

@Component
public class Auditor implements AuditorAware<ObjectId> {
	@Override
	public ObjectId getCurrentAuditor() {
		// If you are using spring-security, you may get this from SecurityContext.
		return new ObjectId();
	}
}