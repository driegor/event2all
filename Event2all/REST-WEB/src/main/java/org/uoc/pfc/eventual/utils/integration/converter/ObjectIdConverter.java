package org.uoc.pfc.eventual.utils.integration.converter;

import org.bson.types.ObjectId;
import org.dozer.CustomConverter;
import org.dozer.MappingException;

public class ObjectIdConverter implements CustomConverter {

	@Override
	public Object convert(Object existingDestinationFieldValue, Object sourceFieldValue, Class<?> destinationClass, Class<?> sourceClass) {

		Object dest = null;

		if (sourceFieldValue == null) {
			return null;
		}

		try {
			if (sourceFieldValue instanceof ObjectId) {

				dest = ((ObjectId) sourceFieldValue).toString();

			} else if (sourceFieldValue instanceof String) {

				dest = new ObjectId((String) sourceFieldValue);

			}

			return dest;

		} catch (RuntimeException e) {
			throw new MappingException("Converter " + this.getClass().getName() + " used incorrectly. Arguments passed in were:"
					+ existingDestinationFieldValue + " and " + sourceFieldValue);
		}
	}
}