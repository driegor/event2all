package org.uoc.pfc.eventual.utils.integration.converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;

import org.dozer.CustomConverter;
import org.dozer.MappingException;
import org.uoc.pfc.eventual.model.post.Post;
import org.uoc.pfc.eventual.utils.integration.dto.post.PostDTO;

public class PostsConverter implements CustomConverter {

	@SuppressWarnings("unchecked")
	@Override
	public Object convert(Object existingDestinationFieldValue, Object sourceFieldValue, Class<?> destinationClass, Class<?> sourceClass) {

		Object dest = null;

		if (sourceFieldValue == null) {
			return null;
		}

		try {
			if (sourceFieldValue instanceof LinkedHashSet<?>) {

				Collection<Post<?>> posts = ((LinkedHashSet<Post<?>>) sourceFieldValue);
				Collection<PostDTO<?>> dtoPosts = new ArrayList<PostDTO<?>>();
				PostConverter postConverter = new PostConverter();
				for (Post<?> post : posts) {
					dtoPosts.add((PostDTO<?>) postConverter.convert(null, post, null, null));

				}
				return dtoPosts;
			}
		} catch (RuntimeException e) {
			throw new MappingException("Converter " + this.getClass().getName() + " used incorrectly. Arguments passed in were:"
					+ existingDestinationFieldValue + " and " + sourceFieldValue);
		}
		return dest;
	}
}