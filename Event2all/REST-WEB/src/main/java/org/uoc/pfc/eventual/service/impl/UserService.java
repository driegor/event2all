package org.uoc.pfc.eventual.service.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.uoc.pfc.eventual.model.User;
import org.uoc.pfc.eventual.model.media.Image;
import org.uoc.pfc.eventual.repository.impl.RepositoryConfig;
import org.uoc.pfc.eventual.repository.impl.UserRepository;
import org.uoc.pfc.eventual.service.IFileService;
import org.uoc.pfc.eventual.service.IUserService;
import org.uoc.pfc.eventual.utils.enums.ImageType;
import org.uoc.pfc.eventual.utils.integration.dto.UserDTO;
import org.uoc.pfc.eventual.web.controller.command.UploadMultiFileCommand;

@Service
public class UserService extends RestService<UserDTO, User, UserRepository> implements IUserService {

    @Autowired
    IFileService imageService;

    @Autowired
    public UserService(RepositoryConfig config) {
	super(UserDTO.class, User.class, config.getUserRespository());
    }

    @Override
    public Collection<UserDTO> inEvent(String eventId) {
	return adapter.toBeans(repository.inEvent(new ObjectId(eventId)), UserDTO.class);
    }

    @Override
    public Collection<UserDTO> notInEvent(String eventId) {
	return adapter.toBeans(repository.notInEvent(new ObjectId(eventId)), UserDTO.class);
    }

    @Override
    public void addImages(String userId, UploadMultiFileCommand imageFiles) throws IOException {
	List<Image> images = imageService.storeImageFile(userId, ImageType.USER, imageFiles);
	repository.addImages(new ObjectId(userId), images);

    }

    public UserDTO findByMail(String mail) {
	return adapter.toBean(repository.findByMail(mail), UserDTO.class);
    }
}
