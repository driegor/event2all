package org.uoc.pfc.eventual.model.media;

import org.bson.types.ObjectId;
import org.uoc.pfc.eventual.model.generic.EmbeddedEntity;
import org.uoc.pfc.eventual.utils.enums.ImageSize;

public class File extends EmbeddedEntity {

	String contentType; 
	ImageSize imageType;

	public ImageSize getImageType() {
		return imageType;
	}

	public void setImageType(ImageSize imageType) {
		this.imageType = imageType;
	}

	public String getContentType() {

		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	@Override
	public ObjectId getId() {
		return id;
	}

	@Override
	public void setId(ObjectId id) {
		this.id = id;
	}

}
