package org.uoc.pfc.eventual.service.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.uoc.pfc.eventual.model.Event;
import org.uoc.pfc.eventual.model.media.Image;
import org.uoc.pfc.eventual.model.post.Comment;
import org.uoc.pfc.eventual.model.post.CommentedPictures;
import org.uoc.pfc.eventual.repository.impl.EventRepository;
import org.uoc.pfc.eventual.repository.impl.RepositoryConfig;
import org.uoc.pfc.eventual.service.IEventService;
import org.uoc.pfc.eventual.service.IFileService;
import org.uoc.pfc.eventual.utils.enums.ImageType;
import org.uoc.pfc.eventual.utils.integration.dto.EventDTO;
import org.uoc.pfc.eventual.utils.integration.dto.UserDTO;
import org.uoc.pfc.eventual.utils.integration.dto.media.ImageDTO;
import org.uoc.pfc.eventual.utils.integration.dto.post.CommentDTO;
import org.uoc.pfc.eventual.utils.integration.dto.post.CommentedPicturesDTO;
import org.uoc.pfc.eventual.utils.integration.dto.post.PostDTO;
import org.uoc.pfc.eventual.utils.integration.security.TokenDTO;
import org.uoc.pfc.eventual.utils.token.TokenGeneratorUtils;
import org.uoc.pfc.eventual.web.controller.command.PostMultiFileCommand;
import org.uoc.pfc.eventual.web.controller.command.UploadMultiFileCommand;

@Service
public class EventService extends RestService<EventDTO, Event, EventRepository> implements IEventService {

	@Autowired
	IFileService imageService;

	@Autowired
	public EventService(RepositoryConfig config) {
		super(EventDTO.class, Event.class, config.getEventRespository());
	}

	@Override
	public boolean join(String eventId, String userId, boolean owner) {
		return repository.join(new ObjectId(eventId), new ObjectId(userId), owner);
	}

	@Override
	public boolean unJoin(String eventId, String id) {
		return repository.unJoin(new ObjectId(eventId), new ObjectId(id));
	}

	@Override
	public Collection<UserDTO> getUsers(String eventId) {
		return adapter.toBeans(repository.getUsers(new ObjectId(eventId)), UserDTO.class);
	}

	@Override
	public CommentedPicturesDTO post(String eventId, String userId, PostMultiFileCommand post) throws IOException {

		List<Image> images = imageService.storeImageFile(eventId, ImageType.POST, post);
		CommentedPictures cp = new CommentedPictures("", post.getComments(), images);
		repository.post(new ObjectId(eventId), new ObjectId(userId), cp);
		return adapter.toBean(cp, CommentedPicturesDTO.class);
	}

	@Override
	public CommentDTO post(String eventId, String userId, String post) {
		return adapter.toBean(repository.post(new ObjectId(eventId), new ObjectId(userId), new Comment("", post)), CommentDTO.class);

	}

	@Override
	public Collection<EventDTO> byUser(String userId) {
		return adapter.toBeans(repository.byUser(new ObjectId(userId)), EventDTO.class);
	}

	@Override
	public ImageDTO setImage(String eventId, UploadMultiFileCommand imageFiles) throws IOException {

		List<Image> images = imageService.storeImageFile(eventId, ImageType.EVENT, imageFiles);
		return adapter.toBean(repository.addImages(new ObjectId(eventId), images), ImageDTO.class);
	}

	@Override
	public Collection<CommentedPicturesDTO> getPictures(String eventId) {
		return adapter.toBeans(repository.getPictures(new ObjectId(eventId)), CommentedPicturesDTO.class);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Collection<PostDTO> getPosts(String eventId) {
		return adapter.toBeans(repository.getPosts(new ObjectId(eventId)), PostDTO.class, "");
	}

	@Override
	public CommentDTO addComment(String eventId, String postId, String userId, String comment) {

		Comment c = new Comment("", comment);
		return adapter.toBean(repository.addComment(new ObjectId(eventId), new ObjectId(postId), new ObjectId(userId), c), CommentDTO.class);
	}

	@Override
	public CommentDTO getComment(String eventId, String postId, String commentId) {
		return adapter.toBean(repository.getComment(new ObjectId(eventId), new ObjectId(postId), new ObjectId(commentId)), CommentDTO.class);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public PostDTO getPost(String eventId, String postId) {
		return adapter.toBean(repository.getPost(new ObjectId(eventId), new ObjectId(postId)), PostDTO.class);
	}

	@Override
	public TokenDTO generateUniqueToken() {
		// creamos un token aleatorio para usarlo como invitación a un evento

		String token = TokenGeneratorUtils.getRandomToken(TokenGeneratorUtils.DEFAULT_LENGHT);

		TokenDTO tkDTO = new TokenDTO();
		tkDTO.setToken(TokenGeneratorUtils.getRandomToken(TokenGeneratorUtils.DEFAULT_LENGHT));

		// el token generado no garantiza que no se use en un evento actual, asi que mientras no sea
		// unico se irá generando uno nuevo para evitar colisiones.

		while (repository.existsToken(token)) {
			tkDTO.setToken(TokenGeneratorUtils.getRandomToken(TokenGeneratorUtils.DEFAULT_LENGHT));
		}
		return tkDTO;
	}

	@Override
	public String byToken(String token) {
		return repository.byToken(token);
	}

}
