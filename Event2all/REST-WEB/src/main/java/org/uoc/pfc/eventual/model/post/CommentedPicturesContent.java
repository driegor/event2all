package org.uoc.pfc.eventual.model.post;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.uoc.pfc.eventual.model.media.Image;

public class CommentedPicturesContent {

	String description;
	List<Comment> comments;
	Collection<Image> images;

	public CommentedPicturesContent() {
		super();
	}

	public CommentedPicturesContent(String description, List<Image> images) {
		this.description = description;
		this.images = images;
	}

	public List<Comment> getComments() {
		if (comments == null) {
			comments = new ArrayList<Comment>();
		}
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	public Collection<Image> getImages() {
		if (images == null) {
			images = new ArrayList<Image>();
		}
		return images;
	}

	public void setImages(Collection<Image> images) {
		this.images = images;
	}

	public String getDescrition() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
