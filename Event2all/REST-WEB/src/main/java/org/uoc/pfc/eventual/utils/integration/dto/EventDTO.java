package org.uoc.pfc.eventual.utils.integration.dto;

import java.util.Collection;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.uoc.pfc.eventual.utils.integration.dto.generic.JsonDTO;
import org.uoc.pfc.eventual.utils.integration.dto.media.ImageDTO;
import org.uoc.pfc.eventual.utils.integration.dto.post.PostDTO;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class EventDTO extends JsonDTO {

	private String name;
	private String description;
	private String date;
	private ImageDTO image;
	private Collection<UserDTO> users;
	private Collection<PostDTO<?>> posts;
	private Collection<ImageDTO> images;
	private String file_source;
	private int postsCount;
	private int imagesCount;
	private String ownerId;
	private boolean shared;
	private String token;

	public int getPostsCount() {
		return postsCount;
	}

	public void setPostsCount(int postsCount) {
		this.postsCount = postsCount;
	}

	public int getImagesCount() {
		return imagesCount;
	}

	public void setImagesCount(int imagesCount) {
		this.imagesCount = imagesCount;
	}

	public void setUsers(Collection<UserDTO> users) {
		this.users = users;
	}

	public void setPosts(Collection<PostDTO<?>> posts) {
		this.posts = posts;
	}

	public void setImages(Collection<ImageDTO> images) {
		this.images = images;
	}

	public Collection<PostDTO<?>> getPosts() {
		return posts;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Collection<UserDTO> getUsers() {
		return users;
	}

	public Collection<ImageDTO> getImages() {
		return images;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public ImageDTO getImage() {
		return image;
	}

	public void setImage(ImageDTO image) {
		this.image = image;
	}

	public String getFile_source() {
		return file_source;
	}

	public void setFile_source(String file_source) {
		this.file_source = file_source;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public boolean isShared() {
		return shared;
	}

	public void setShared(boolean shared) {
		this.shared = shared;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

}
