package org.uoc.pfc.eventual.service;

import java.io.IOException;
import java.util.Collection;

import org.uoc.pfc.eventual.utils.integration.dto.UserDTO;
import org.uoc.pfc.eventual.web.controller.command.UploadMultiFileCommand;

public interface IUserService extends IRestService<UserDTO> {

    Collection<UserDTO> notInEvent(String eventId);

    Collection<UserDTO> inEvent(String eventId);

    void addImages(String userId, UploadMultiFileCommand images) throws IOException;

    UserDTO findByMail(String mail);

}
