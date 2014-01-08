package org.uoc.pfc.eventual.model.post;

import java.util.List;

import org.uoc.pfc.eventual.model.media.Image;
import org.uoc.pfc.eventual.utils.enums.PostEnum;

public class CommentedPictures extends Post<CommentedPicturesContent> {

	public CommentedPictures() {
		super("", PostEnum.COMMENTED_PICTURES);
	}

	public CommentedPictures(String header) {
		super(header, PostEnum.COMMENTED_PICTURES);
	}

	public CommentedPictures(String header, String description, List<Image> images) {
		super(header, new CommentedPicturesContent(description, images), PostEnum.COMMENTED_PICTURES);
	}
}
