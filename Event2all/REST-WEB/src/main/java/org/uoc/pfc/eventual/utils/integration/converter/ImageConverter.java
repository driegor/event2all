package org.uoc.pfc.eventual.utils.integration.converter;

import org.dozer.CustomConverter;
import org.dozer.MappingException;
import org.uoc.pfc.eventual.model.media.File;
import org.uoc.pfc.eventual.model.media.Image;
import org.uoc.pfc.eventual.utils.integration.dto.media.ImageDTO;

public class ImageConverter implements CustomConverter {

	@Override
	public Object convert(Object existingDestinationFieldValue, Object sourceFieldValue, Class<?> destinationClass, Class<?> sourceClass) {

		Object dest = null;

		if (sourceFieldValue == null) {
			return null;
		}

		try {
			if (sourceFieldValue instanceof Image) {

				Image img = (Image) sourceFieldValue;
				ImageDTO imgDTO = new ImageDTO();

				imgDTO.setFileName(img.getFileName());
				imgDTO.setId(img.getId().toString());
				for (File file : img.getFiles()) {
					ImageDTO.FileDTO fileDTO = imgDTO.new FileDTO();
					fileDTO.setContentType(file.getContentType());
					fileDTO.setImageType(file.getImageType());
					fileDTO.setId(String.valueOf(file.getId()));
					fileDTO.setUrl("/api/file/show/" + String.valueOf(file.getId()));
					imgDTO.getFiles().add(fileDTO);
				}
				return imgDTO;
			}
		} catch (RuntimeException e) {
			throw new MappingException("Converter " + this.getClass().getName() + " used incorrectly. Arguments passed in were:"
					+ existingDestinationFieldValue + " and " + sourceFieldValue);
		}
		return dest;
	}
}