package org.uoc.pfc.eventual.utils.integration.converter;

import org.dozer.CustomConverter;
import org.dozer.MappingException;
import org.uoc.pfc.eventual.model.User;
import org.uoc.pfc.eventual.model.post.Comment;
import org.uoc.pfc.eventual.utils.integration.dto.UserDTO;
import org.uoc.pfc.eventual.utils.integration.dto.media.ImageDTO;
import org.uoc.pfc.eventual.utils.integration.dto.post.CommentDTO;

public class CommentConverter implements CustomConverter {

    @Override
    public Object convert(Object existingDestinationFieldValue, Object sourceFieldValue, Class<?> destinationClass, Class<?> sourceClass) {

	Object dest = null;

	if (sourceFieldValue == null) {
	    return null;
	}

	try {

	    if (sourceFieldValue instanceof Comment) {
		ImageConverter ic = new ImageConverter();
		Comment comment = (Comment) sourceFieldValue;
		CommentDTO commentDTO = new CommentDTO();
		commentDTO.setId(String.valueOf(comment.getId()));
		DateConverter dcc = new DateConverter();
		commentDTO.setPostTime((String) dcc.convert(null, comment.getPostTime(), null, null));
		User user = comment.getUser();
		UserDTO userDTO = new UserDTO();
		if (user != null) {
		    userDTO.setId(String.valueOf(user.getId()));
		    userDTO.setName(user.getName());
		    userDTO.setImage((ImageDTO) ic.convert(null, user.getImage(), null, null));
		}
		commentDTO.setUser(userDTO);
		commentDTO.setPostEnum(comment.getPostEnum());
		commentDTO.setHeader(comment.getHeader());
		commentDTO.setContent(comment.getContent().replace("\\n", "<br>"));
		return commentDTO;
	    }
	} catch (RuntimeException e) {
	    throw new MappingException("Converter " + this.getClass().getName() + " used incorrectly. Arguments passed in were:"
		    + existingDestinationFieldValue + " and " + sourceFieldValue);
	}
	return dest;
    }
}