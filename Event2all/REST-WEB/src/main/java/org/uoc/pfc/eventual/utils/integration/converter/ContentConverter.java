package org.uoc.pfc.eventual.utils.integration.converter;

import org.dozer.CustomConverter;
import org.dozer.MappingException;
import org.uoc.pfc.eventual.model.media.Image;
import org.uoc.pfc.eventual.model.post.Comment;
import org.uoc.pfc.eventual.model.post.CommentedPicturesContent;
import org.uoc.pfc.eventual.utils.integration.dto.media.ImageDTO;
import org.uoc.pfc.eventual.utils.integration.dto.post.CommentDTO;
import org.uoc.pfc.eventual.utils.integration.dto.post.CommentedPicturesContentDTO;

public class ContentConverter implements CustomConverter {

    @Override
    public Object convert(Object existingDestinationFieldValue, Object sourceFieldValue, Class<?> destinationClass, Class<?> sourceClass) {

	Object dest = null;

	if (sourceFieldValue == null) {
	    return null;
	}

	try {
	    if (sourceFieldValue instanceof CommentedPicturesContent) {

		CommentedPicturesContent content = (CommentedPicturesContent) sourceFieldValue;
		CommentedPicturesContentDTO cntDTO = new CommentedPicturesContentDTO();

		cntDTO.setDescription(content.getDescrition().replace("\\n", "<br>"));

		if (content.getComments() != null) {
		    CommentConverter commentConverter = new CommentConverter();
		    for (Comment comment : content.getComments()) {
			cntDTO.getComments().add((CommentDTO) commentConverter.convert(null, comment, null, null));
		    }
		}

		if (content.getImages() != null) {
		    ImageConverter imgConverter = new ImageConverter();
		    for (Image img : content.getImages()) {
			cntDTO.getImages().add((ImageDTO) imgConverter.convert(null, img, null, null));
		    }
		}

		return cntDTO;
	    }
	} catch (RuntimeException e) {
	    throw new MappingException("Converter " + this.getClass().getName() + " used incorrectly. Arguments passed in were:"
		    + existingDestinationFieldValue + " and " + sourceFieldValue);
	}
	return dest;
    }
}