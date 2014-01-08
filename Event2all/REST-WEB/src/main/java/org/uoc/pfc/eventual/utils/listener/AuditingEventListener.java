package org.uoc.pfc.eventual.utils.listener;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;
import org.uoc.pfc.eventual.model.generic.AuditEntity;
import org.uoc.pfc.eventual.model.generic.EmbeddedEntity;

@Component
public class AuditingEventListener implements ApplicationListener<BeforeConvertEvent<Object>> {

	@Autowired
	private Auditor auditorAware;

	@Autowired
	@Qualifier("dateTimeProvider")
	private DateTimeProvider dateTimeProvider;

	@Override
	public void onApplicationEvent(BeforeConvertEvent<Object> event) {
		Object obj = event.getSource();
		if (AuditEntity.class.isAssignableFrom(obj.getClass())) {
			AuditEntity entity = (AuditEntity) obj;
			if (entity.isNew()) {
				entity.setCreatedBy(auditorAware.getCurrentAuditor());
				entity.setCreatedDate(dateTimeProvider.getDateTime());
			}
			entity.setLastModifiedBy(auditorAware.getCurrentAuditor());
			entity.setLastModifiedDate(dateTimeProvider.getDateTime());
		} else if (EmbeddedEntity.class.isAssignableFrom(obj.getClass())) {
			EmbeddedEntity entity = (EmbeddedEntity) obj;
			if (entity.isNew()) {
				entity.setId(new ObjectId());
			}
		}
	}
}