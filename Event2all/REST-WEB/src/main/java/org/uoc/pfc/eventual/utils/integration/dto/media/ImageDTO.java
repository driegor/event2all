package org.uoc.pfc.eventual.utils.integration.dto.media;

import java.util.ArrayList;
import java.util.Collection;

import org.uoc.pfc.eventual.utils.enums.ImageSize;
import org.uoc.pfc.eventual.utils.integration.dto.generic.JsonDTO;

public class ImageDTO extends JsonDTO {

    private String	      fileName;

    private Collection<FileDTO> files = new ArrayList<FileDTO>();

    public String getFileName() {
	return fileName;
    }

    public void setFileName(String fileName) {
	this.fileName = fileName;
    }

    public Collection<FileDTO> getFiles() {
	return files;
    }

    public void setFiles(Collection<FileDTO> files) {
	this.files = files;
    }

    public class FileDTO extends JsonDTO {

	String    contentType;
	ImageSize imageType;
	String    url;

	public String getContentType() {
	    return contentType;
	}

	public void setContentType(String contentType) {
	    this.contentType = contentType;
	}

	public ImageSize getImageType() {
	    return imageType;
	}

	public void setImageType(ImageSize imageType) {
	    this.imageType = imageType;
	}

	public String getUrl() {
	    return url;
	}

	public void setUrl(String url) {
	    this.url = url;
	}
    }
}