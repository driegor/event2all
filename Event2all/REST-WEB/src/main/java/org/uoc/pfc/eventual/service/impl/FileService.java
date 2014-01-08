package org.uoc.pfc.eventual.service.impl;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.uoc.pfc.eventual.model.media.File;
import org.uoc.pfc.eventual.model.media.Image;
import org.uoc.pfc.eventual.repository.impl.FileStorageRepository;
import org.uoc.pfc.eventual.service.IFileService;
import org.uoc.pfc.eventual.utils.enums.ImageSize;
import org.uoc.pfc.eventual.utils.enums.ImageType;
import org.uoc.pfc.eventual.utils.multimedia.Transformer;
import org.uoc.pfc.eventual.web.controller.command.UploadMultiFileCommand;

@Service
public class FileService implements IFileService {

    @Autowired
    FileStorageRepository fileStorage;

    @Override
    public List<Image> storeImageFile(String ownerId, ImageType imgType, UploadMultiFileCommand files) throws IOException {

	Map<String, String> metaData = new HashMap<String, String>();
	metaData.put("ownerId", ownerId);
	metaData.put("imageType", imgType.name());
	List<Image> images = new ArrayList<Image>();

	for (MultipartFile mpf : files.getFileData()) {

	    String contentType = mpf.getContentType();
	    String fileName = mpf.getName();
	    Image image = new Image();
	    image.setFileName(fileName);

	    // modificamos la imagen original y la convertimos en otra de un tamaño inferior
	    BufferedImage originalImage = ImageIO.read(mpf.getInputStream());

	    // si es una imagen de un usuario , o la imagen principal de un evento, solo crearemos una versión
	    // thumbnail, y otra mas grande, si es un post, crearemos otra en hd, para visualizarla en la galería WEB

	    if (imgType.equals(ImageType.EVENT) || imgType.equals(ImageType.USER)) {
		image.getFiles().add(store(originalImage, ImageSize.THUMBNAIL, contentType, Color.BLACK, fileName, metaData));
		image.getFiles().add(store(originalImage, ImageSize.MEDIUM_MOBILE, contentType, null, fileName, metaData));
	    } else if (imgType.equals(ImageType.POST)) {
		image.getFiles().add(store(originalImage, ImageSize.THUMBNAIL, contentType, null, fileName, metaData));
		image.getFiles().add(store(originalImage, ImageSize.MEDIUM_MOBILE, contentType, Color.BLACK, fileName, metaData));
		image.getFiles().add(store(originalImage, ImageSize.HD, contentType, null, fileName, metaData));
	    }
	    images.add(image);
	}

	return images;
    }

    private File store(BufferedImage originalImage, ImageSize imgSize, String contentType, Color color, String fileName, Map<String, String> metaData)
	    throws IOException {

	// generamos una imagen "recortada" a partir de la imagen original
	BufferedImage resizedImage = Transformer.resizeImage(originalImage, imgSize, color);

	// copiamos el contenido del buffer a un outputstream, y lo almacenamos en base de datos
	ByteArrayOutputStream rszOutputStream = new ByteArrayOutputStream();
	ImageIO.write(resizedImage, "JPG", rszOutputStream);
	InputStream rszInputStream = new ByteArrayInputStream(rszOutputStream.toByteArray());

	String idFile = fileStorage.save(rszInputStream, contentType, fileName, metaData);

	File file = new File();
	file.setContentType(contentType);
	file.setId(new ObjectId(idFile));
	file.setImageType(imgSize);

	return file;
    }

    @Override
    public byte[] getFileContent(String imageId) throws IOException {
	return fileStorage.getContent(imageId);
    }

}
