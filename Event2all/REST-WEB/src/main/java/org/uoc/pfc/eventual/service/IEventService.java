package org.uoc.pfc.eventual.service;

import java.io.IOException;
import java.util.Collection;

import org.uoc.pfc.eventual.utils.integration.dto.EventDTO;
import org.uoc.pfc.eventual.utils.integration.dto.UserDTO;
import org.uoc.pfc.eventual.utils.integration.dto.media.ImageDTO;
import org.uoc.pfc.eventual.utils.integration.dto.post.CommentDTO;
import org.uoc.pfc.eventual.utils.integration.dto.post.CommentedPicturesDTO;
import org.uoc.pfc.eventual.utils.integration.dto.post.PostDTO;
import org.uoc.pfc.eventual.utils.integration.security.TokenDTO;
import org.uoc.pfc.eventual.web.controller.command.PostMultiFileCommand;
import org.uoc.pfc.eventual.web.controller.command.UploadMultiFileCommand;

public interface IEventService extends IRestService<EventDTO> {

    public boolean join(String eventId, String id, boolean owner);

    public boolean unJoin(String eventId, String id);

    public Collection<UserDTO> getUsers(String eventId);

    public Collection<EventDTO> byUser(String userId);

    public CommentedPicturesDTO post(String eventId, String userId, PostMultiFileCommand post) throws IOException;

    public CommentDTO post(String eventId, String userId, String post);

    public ImageDTO setImage(String eventId, UploadMultiFileCommand images) throws IOException;

    public Collection<CommentedPicturesDTO> getPictures(String eventId);

    @SuppressWarnings("rawtypes")
    public Collection<PostDTO> getPosts(String eventId);

    public CommentDTO addComment(String eventId, String postId, String userId, String comment);

    public CommentDTO getComment(String eventId, String postId, String commentId);

    @SuppressWarnings("rawtypes")
    public PostDTO getPost(String eventId, String postId);

    public TokenDTO generateUniqueToken();

    public String byToken(String token);

}
