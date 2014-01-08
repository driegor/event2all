package org.uoc.pfc.eventual.utils.integration.dto.post;

import java.util.ArrayList;
import java.util.Collection;

import org.uoc.pfc.eventual.utils.integration.dto.media.ImageDTO;

public class CommentedPicturesContentDTO {

	String description;
	Collection<CommentDTO> comments = new ArrayList<CommentDTO>();
	Collection<ImageDTO> images = new ArrayList<ImageDTO>();

	public Collection<CommentDTO> getComments() {
		return comments;
	}

	public Collection<ImageDTO> getImages() {
		return images;
	}

	public void setComments(Collection<CommentDTO> comments) {
		this.comments = comments;
	}

	public void setImages(Collection<ImageDTO> images) {
		this.images = images;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
