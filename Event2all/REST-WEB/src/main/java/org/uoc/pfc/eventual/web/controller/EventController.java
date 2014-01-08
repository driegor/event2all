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
import org.uoc.pfc.eventual.service.IEventService;
import org.uoc.pfc.eventual.utils.integration.dto.EventDTO;
import org.uoc.pfc.eventual.utils.integration.dto.UserDTO;
import org.uoc.pfc.eventual.utils.integration.dto.generic.IdsDTO;
import org.uoc.pfc.eventual.utils.integration.dto.media.ImageDTO;
import org.uoc.pfc.eventual.utils.integration.dto.post.CommentDTO;
import org.uoc.pfc.eventual.utils.integration.dto.post.CommentedPicturesDTO;
import org.uoc.pfc.eventual.utils.integration.dto.post.PostDTO;
import org.uoc.pfc.eventual.utils.integration.security.TokenDTO;
import org.uoc.pfc.eventual.web.controller.command.PostMultiFileCommand;
import org.uoc.pfc.eventual.web.controller.command.UploadMultiFileCommand;
import org.uoc.pfc.eventual.web.controller.rest.AbstractRestController;
import org.uoc.pfc.eventual.web.utils.filter.CorsFilter;

@Controller
@RequestMapping(value = "/api/event")
public class EventController extends AbstractRestController<EventDTO, IEventService> {

	@Autowired
	IEventService eventService;

	public EventController() {
		super(EventDTO.class);
	}

	// listado de ventos por usuario
	@RequestMapping(value = "/by-user/{userId}", method = RequestMethod.GET)
	public @ResponseBody
	ResponseEntity<Collection<EventDTO>> eventsByUser(@PathVariable("userId") String userId) {
		HttpHeaders headers = CorsFilter.getAccessControllAllowOrigin();
		ResponseEntity<Collection<EventDTO>> entity = new ResponseEntity<Collection<EventDTO>>(getRestService().byUser(userId), headers, HttpStatus.OK);
		return entity;
	}

	// añadimos usuarios nuevos al evento
	@RequestMapping(value = "/{eventId}/users", method = RequestMethod.PUT)
	public @ResponseBody
	ResponseEntity<Boolean> join(@PathVariable("eventId") String eventId, @RequestBody IdsDTO ids) {
		HttpHeaders headers = CorsFilter.getAccessControllAllowOrigin();

		boolean allOks = Boolean.TRUE;
		for (String userId : ids.getIds()) {
			allOks = allOks && getRestService().join(eventId, userId, Boolean.FALSE);
		}
		ResponseEntity<Boolean> entity = new ResponseEntity<Boolean>(allOks, headers, HttpStatus.OK);
		return entity;
	}

	// eliminamos usuarios del evento
	@RequestMapping(value = "/{eventId}/users", method = RequestMethod.DELETE)
	public @ResponseBody
	ResponseEntity<Boolean> unJoin(@PathVariable("eventId") String eventId, @RequestBody IdsDTO ids) {
		HttpHeaders headers = CorsFilter.getAccessControllAllowOrigin();
		boolean allOks = Boolean.TRUE;
		for (String userId : ids.getIds()) {
			allOks = allOks && getRestService().unJoin(eventId, userId);
		}
		ResponseEntity<Boolean> entity = new ResponseEntity<Boolean>(allOks, headers, HttpStatus.OK);
		return entity;
	}

	// listado de los usuarios que participan en este evento
	@RequestMapping(value = "/{eventId}/users", method = RequestMethod.GET)
	public @ResponseBody
	Collection<UserDTO> users(@PathVariable("eventId") String eventId) {
		return getRestService().getUsers(eventId);
	}

	// listado de posts de este evento
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/{eventId}/posts", method = RequestMethod.GET)
	public @ResponseBody
	ResponseEntity<Collection<PostDTO>> posts(@PathVariable("eventId") String eventId) {
		HttpHeaders headers = CorsFilter.getAccessControllAllowOrigin();
		Collection<PostDTO> list = getRestService().getPosts(eventId);
		ResponseEntity<Collection<PostDTO>> entity = new ResponseEntity<Collection<PostDTO>>(list, headers, HttpStatus.OK);
		return entity;
	}

	// obtenemos un post concreto de un evento
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/{eventId}/posts/{postId}", method = RequestMethod.GET)
	public @ResponseBody
	ResponseEntity<PostDTO> posts(@PathVariable("eventId") String eventId, @PathVariable("postId") String postId) {
		HttpHeaders headers = CorsFilter.getAccessControllAllowOrigin();
		PostDTO post = getRestService().getPost(eventId, postId);
		ResponseEntity<PostDTO> entity = new ResponseEntity<PostDTO>(post, headers, HttpStatus.OK);
		return entity;
	}

	// añadimos un nuevo post, del tipo imagen con comentario, al evento
	// especificando el usuario que ha enviado la imagen
	@RequestMapping(value = "/{eventId}/posts/user/{userId}/postImage", method = RequestMethod.POST)
	public @ResponseBody
	ResponseEntity<CommentedPicturesDTO> postImage(@PathVariable("eventId") String eventId, @PathVariable("userId") String userId, PostMultiFileCommand post)
			throws IOException {
		HttpHeaders headers = CorsFilter.getAccessControllAllowOrigin();
		CommentedPicturesDTO cpDTO = getRestService().post(eventId, userId, post);
		ResponseEntity<CommentedPicturesDTO> entity = new ResponseEntity<CommentedPicturesDTO>(cpDTO, headers, HttpStatus.OK);
		return entity;
	}

	// añadimos un nuevo post al evento
	// especificando el usuario que ha enviado la imagen
	@RequestMapping(value = "/{eventId}/posts/user/{userId}", method = RequestMethod.POST)
	public @ResponseBody
	ResponseEntity<CommentDTO> post(@PathVariable("eventId") String eventId, @PathVariable("userId") String userId, @RequestBody String post)
			throws IOException {
		HttpHeaders headers = CorsFilter.getAccessControllAllowOrigin();
		CommentDTO commentDTO = getRestService().post(eventId, userId, post);
		ResponseEntity<CommentDTO> entity = new ResponseEntity<CommentDTO>(commentDTO, headers, HttpStatus.OK);
		return entity;
	}

	// añadimos un comentario a un post determinado
	@RequestMapping(value = "/{eventId}/posts/{postId}/comments/{commentId}", method = RequestMethod.GET)
	public @ResponseBody
	ResponseEntity<CommentDTO> getComment(@PathVariable("eventId") String eventId, @PathVariable("postId") String postId,
			@PathVariable("commentId") String commentId, @RequestBody String comment) throws IOException {
		HttpHeaders headers = CorsFilter.getAccessControllAllowOrigin();
		CommentDTO commentDTO = getRestService().getComment(eventId, postId, commentId);
		ResponseEntity<CommentDTO> entity = new ResponseEntity<CommentDTO>(commentDTO, headers, HttpStatus.OK);
		return entity;
	}

	// obtenemos
	@RequestMapping(value = "/{eventId}/posts/{postId}/user/{userId}/comment", method = RequestMethod.POST)
	public @ResponseBody
	ResponseEntity<CommentDTO> comment(@PathVariable("eventId") String eventId, @PathVariable("postId") String postId, @PathVariable("userId") String userId,
			@RequestBody String comment) throws IOException {
		HttpHeaders headers = CorsFilter.getAccessControllAllowOrigin();
		CommentDTO commentDTO = getRestService().addComment(eventId, postId, userId, comment);
		ResponseEntity<CommentDTO> entity = new ResponseEntity<CommentDTO>(commentDTO, headers, HttpStatus.OK);
		return entity;
	}

	// añadimos una imagen al evento,esta imagen no es parte de un post, sino la imagen priincipal del evento que se
	// utilizrá en los distinos listados
	@RequestMapping(value = "/{eventId}/images", method = RequestMethod.POST)
	public @ResponseBody
	ResponseEntity<ImageDTO> addImage(@PathVariable("eventId") String eventId, UploadMultiFileCommand images) throws IOException {

		HttpHeaders headers = CorsFilter.getAccessControllAllowOrigin();
		ImageDTO imageDTO = getRestService().setImage(eventId, images);
		ResponseEntity<ImageDTO> entity = new ResponseEntity<ImageDTO>(imageDTO, headers, HttpStatus.OK);
		return entity;
	}

	// listado de imágenes posteadas del evento
	@RequestMapping(value = "/{eventId}/images", method = RequestMethod.GET)
	public @ResponseBody
	ResponseEntity<Collection<CommentedPicturesDTO>> pictures(@PathVariable("eventId") String eventId) {
		HttpHeaders headers = CorsFilter.getAccessControllAllowOrigin();

		Collection<CommentedPicturesDTO> list = getRestService().getPictures(eventId);
		ResponseEntity<Collection<CommentedPicturesDTO>> entity = new ResponseEntity<Collection<CommentedPicturesDTO>>(list, headers, HttpStatus.OK);

		return entity;
	}

	@RequestMapping(value = "/find/{q}", method = RequestMethod.GET)
	public @ResponseBody
	ResponseEntity<Collection<EventDTO>> find(@PathVariable("q") String query) {

		HttpHeaders headers = CorsFilter.getAccessControllAllowOrigin();
		ResponseEntity<Collection<EventDTO>> entity = new ResponseEntity<Collection<EventDTO>>((getRestService().like(query, "name,description")), headers,
				HttpStatus.OK);
		return entity;
	}

	// generamos el token del evento, que servirá para invitar a participantes de forma
	// masiva a este evento
	@RequestMapping(value = "/token", method = RequestMethod.GET)
	public @ResponseBody
	ResponseEntity<TokenDTO> generateToken() {

		HttpHeaders headers = CorsFilter.getAccessControllAllowOrigin();
		ResponseEntity<TokenDTO> entity = new ResponseEntity<TokenDTO>((getRestService().generateUniqueToken()), headers, HttpStatus.OK);
		return entity;
	}

	// añadimos usuarios nuevos al evento
	@RequestMapping(value = "/token/{token}", method = RequestMethod.PUT)
	public @ResponseBody
	ResponseEntity<Boolean> addByToken(@PathVariable String token, @RequestBody IdsDTO ids) {
		HttpHeaders headers = CorsFilter.getAccessControllAllowOrigin();
		ResponseEntity<Boolean> entity = null;
		// buscamos el evento asociado al token

		String eventId = eventService.byToken(token);

		if (eventId == null) {
			return new ResponseEntity<Boolean>(Boolean.FALSE, headers, HttpStatus.OK);
		}
		boolean allOks = Boolean.TRUE;
		try {
			for (String userId : ids.getIds()) {
				allOks = allOks && getRestService().join(eventId, userId, Boolean.FALSE);
			}
			entity = new ResponseEntity<Boolean>(allOks, headers, HttpStatus.OK);
		} catch (Exception e) {
			entity = new ResponseEntity<Boolean>(Boolean.FALSE, headers, HttpStatus.OK);
		}

		return entity;
	}

	@Override
	protected IEventService getRestService() {
		return eventService;
	}

}
