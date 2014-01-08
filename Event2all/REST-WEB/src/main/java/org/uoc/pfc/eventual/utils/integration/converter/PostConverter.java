package org.uoc.pfc.eventual.utils.integration.converter;

import org.dozer.CustomConverter;
import org.dozer.MappingException;
import org.uoc.pfc.eventual.model.User;
import org.uoc.pfc.eventual.model.post.CommentedPictures;
import org.uoc.pfc.eventual.model.post.Post;
import org.uoc.pfc.eventual.utils.integration.dto.UserDTO;
import org.uoc.pfc.eventual.utils.integration.dto.media.ImageDTO;
import org.uoc.pfc.eventual.utils.integration.dto.post.CommentDTO;
import org.uoc.pfc.eventual.utils.integration.dto.post.CommentedPicturesContentDTO;
import org.uoc.pfc.eventual.utils.integration.dto.post.CommentedPicturesDTO;
import org.uoc.pfc.eventual.utils.integration.dto.post.PostDTO;

public class PostConverter implements CustomConverter {

    @Override
    public Object convert(Object existingDestinationFieldValue, Object sourceFieldValue, Class<?> destinationClass, Class<?> sourceClass) {

	Object dest = null;

	if (sourceFieldValue == null) {
	    return null;
	}

	try {
	    if (sourceFieldValue instanceof Post<?>) {

		PostDTO<?> postDTO = null;

		Post<?> post = (Post<?>) sourceFieldValue;

		switch (((Post<?>) sourceFieldValue).getPostEnum()) {

		    case COMMENTED_PICTURES:
			CommentedPictures cp = ((CommentedPictures) sourceFieldValue);
			postDTO = new CommentedPicturesDTO();
			ContentConverter cntConverter = new ContentConverter();
			((CommentedPicturesDTO) postDTO).setContent((CommentedPicturesContentDTO) cntConverter.convert(null, cp.getContent(), null, null));
			break;

		    case COMMENT:
			CommentConverter converter = new CommentConverter();
			postDTO = (CommentDTO) converter.convert(null, post, null, null);
			break;

		    default:
			break;
		}

		if (postDTO != null) {

		    DateConverter dcc = new DateConverter();
		    ImageConverter ic = new ImageConverter();

		    postDTO.setId(String.valueOf(post.getId()));
		    postDTO.setPostTime((String) dcc.convert(null, post.getPostTime(), null, null));
		    User user = post.getUser();
		    UserDTO userDTO = new UserDTO();
		    if (user != null) {
			userDTO.setId(String.valueOf(user.getId()));
			userDTO.setName(user.getName());
			userDTO.setImage((ImageDTO) ic.convert(null, user.getImage(), null, null));
		    }
		    postDTO.setUser(userDTO);
		    postDTO.setPostEnum(post.getPostEnum());
		    postDTO.setHeader(post.getHeader());
		}
		return postDTO;
	    }

	} catch (RuntimeException e) {
	    throw new MappingException("Converter " + this.getClass().getName() + " used incorrectly. Arguments passed in were:"
		    + existingDestinationFieldValue + " and " + sourceFieldValue);
	}
	return dest;
    }
}