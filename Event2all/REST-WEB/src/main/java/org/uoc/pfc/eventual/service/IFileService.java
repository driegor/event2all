package org.uoc.pfc.eventual.service;

import java.io.IOException;
import java.util.List;

import org.uoc.pfc.eventual.model.media.Image;
import org.uoc.pfc.eventual.utils.enums.ImageType;
import org.uoc.pfc.eventual.web.controller.command.UploadMultiFileCommand;

public interface IFileService {

    byte[] getFileContent(String imageId) throws IOException;

    List<Image> storeImageFile(String ownerId, ImageType typeImage, UploadMultiFileCommand files) throws IOException;
}
