package org.uoc.pfc.eventual.utils.integration.converter;

import org.dozer.CustomConverter;
import org.dozer.MappingException;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DateConverter implements CustomConverter {

    @Override
    public Object convert(Object existingDestinationFieldValue, Object sourceFieldValue, Class<?> destinationClass, Class<?> sourceClass) {

	Object dest = null;

	if (sourceFieldValue == null) {
	    return null;
	}

	try {
	    if (sourceFieldValue instanceof DateTime) {

		DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy");
		dest = formatter.print((DateTime) sourceFieldValue);

	    } else if (sourceFieldValue instanceof String) {
		String source = (String) sourceFieldValue;

		if ("".equals(source)) {
		    return null;
		}
		DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy");
		dest = formatter.parseDateTime(source);

	    }

	    return dest;

	} catch (RuntimeException e) {
	    throw new MappingException("Converter " + this.getClass().getName() + " used incorrectly. Arguments passed in were:"
		    + existingDestinationFieldValue + " and " + sourceFieldValue);
	}
    }
}