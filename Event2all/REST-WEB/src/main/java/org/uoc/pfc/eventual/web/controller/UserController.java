package org.uoc.pfc.eventual.web.controller;

import java.io.IOException;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.uoc.pfc.eventual.service.IEventService;
import org.uoc.pfc.eventual.service.IFileService;
import org.uoc.pfc.eventual.service.IUserService;
import org.uoc.pfc.eventual.utils.integration.dto.EventDTO;
import org.uoc.pfc.eventual.utils.integration.dto.UserDTO;
import org.uoc.pfc.eventual.web.controller.command.UploadMultiFileCommand;
import org.uoc.pfc.eventual.web.controller.rest.AbstractRestController;
import org.uoc.pfc.eventual.web.interceptor.security.SkipSecurity;
import org.uoc.pfc.eventual.web.utils.filter.CorsFilter;

import com.mongodb.util.Base64Codec;

@Controller
@RequestMapping(value = "/api/user")
public class UserController extends AbstractRestController<UserDTO, IUserService> {

    @Autowired
    IEventService eventService;

    @Autowired
    IUserService  userService;

    @Autowired
    IFileService  fileService;

    public UserController() {
	super(UserDTO.class);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST)
    @SkipSecurity
    public @ResponseBody
    ResponseEntity<UserDTO> post(@RequestBody UserDTO user) {
	HttpHeaders headers = CorsFilter.getAccessControllAllowOrigin();
	ResponseEntity<UserDTO> entity = new ResponseEntity<UserDTO>(getRestService().post(user), headers, HttpStatus.CREATED);
	return entity;
    }

    @RequestMapping(value = "/nin/{eventId}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<Collection<UserDTO>> usersnin(@PathVariable("eventId") String eventId, WebRequest request) {
	HttpHeaders headers = CorsFilter.getAccessControllAllowOrigin();
	ResponseEntity<Collection<UserDTO>> entity = new ResponseEntity<Collection<UserDTO>>(getRestService().notInEvent(eventId), headers, HttpStatus.OK);
	return entity;
    }

    @RequestMapping(value = "/{userId}/events", method = RequestMethod.GET)
    public @ResponseBody
    Collection<EventDTO> events(@PathVariable("userId") String userId, WebRequest request) {
	return eventService.byUser(userId);
    }

    @RequestMapping(value = "/find/{q}", method = RequestMethod.GET)
    public @ResponseBody
    Collection<UserDTO> find(@PathVariable("q") String query, WebRequest request) {

	return getRestService().like(query, "firstName,lastName,mail");
    }

    @RequestMapping(value = "/{userId}/images", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<Boolean> addImages(@PathVariable("userId") String userId, UploadMultiFileCommand images) throws IOException {

	HttpHeaders headers = CorsFilter.getAccessControllAllowOrigin();
	getRestService().addImages(userId, images);
	ResponseEntity<Boolean> entity = new ResponseEntity<Boolean>(Boolean.TRUE, headers, HttpStatus.OK);
	return entity;
    }

    @RequestMapping(value = "/checkmail/{mail}", method = RequestMethod.GET)
    @SkipSecurity
    public @ResponseBody
    ResponseEntity<Boolean> checkAvailability(@PathVariable("mail") String mail) {

	Base64Codec decoder = new Base64Codec();
	byte[] mailDecoded = decoder.decode(mail);
	Boolean exists = getRestService().findByMail(new String(mailDecoded)) == null;
	HttpHeaders headers = CorsFilter.getAccessControllAllowOrigin();
	ResponseEntity<Boolean> entity = new ResponseEntity<Boolean>(exists, headers, HttpStatus.OK);
	return entity;
    }

    @Override
    protected IUserService getRestService() {
	return userService;
    }
}
