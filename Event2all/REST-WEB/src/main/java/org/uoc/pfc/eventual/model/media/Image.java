package org.uoc.pfc.eventual.model.media;

import java.util.ArrayList;
import java.util.Collection;

import org.bson.types.ObjectId;
import org.uoc.pfc.eventual.model.generic.EmbeddedEntity;

public class Image extends EmbeddedEntity {

	private String fileName;

	private Collection<File> files = new ArrayList<File>();

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Collection<File> getFiles() {
		return files;
	}

	public void setFiles(Collection<File> files) {
		this.files = files;
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