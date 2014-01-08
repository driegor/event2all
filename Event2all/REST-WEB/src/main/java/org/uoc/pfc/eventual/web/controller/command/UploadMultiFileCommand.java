package org.uoc.pfc.eventual.web.controller.command;

import org.springframework.web.multipart.MultipartFile;

public class UploadMultiFileCommand {

	protected MultipartFile[] fileData;

	public MultipartFile[] getFileData() {
		return fileData;
	}

	public void setFileData(MultipartFile[] fileData) {
		this.fileData = fileData;
	}

}
