package org.uoc.pfc.eventual.model;

import java.util.ArrayList;
import java.util.Collection;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.uoc.pfc.eventual.model.generic.Assignment;
import org.uoc.pfc.eventual.model.generic.AuditEntity;
import org.uoc.pfc.eventual.model.media.Image;
import org.uoc.pfc.eventual.model.post.Post;

@Document
public class Event extends AuditEntity {

    /** 
     * 
     */
    private static final long      serialVersionUID = -6353655972364951189L;
    private DateTime	       date;
    private String		 description;
    private Image		  image;
    private String		 name;
    private Collection<Post<?>>    posts	    = new ArrayList<Post<?>>();
    private Collection<Assignment> assignment       = new ArrayList<Assignment>();
    private String		 token;

    // estos datos no se guardan en BD, por eso añadimos una anotación Transient, el valor se calculará
    // a partir de los datos del capo assignment una vez obtenidos.
    @Transient
    private Collection<User>       users	    = new ArrayList<User>();

    private ObjectId	       ownerId;

    private int		    postsCount;
    private int		    imagesCount;

    public DateTime getDate() {
	return date;
    }

    public String getDescription() {
	return description;
    }

    @Override
    public ObjectId getId() {
	return id;
    }

    public String getName() {
	return name;
    }

    public void setDate(DateTime date) {
	this.date = date;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    @Override
    public void setId(ObjectId id) {
	this.id = id;
    }

    public void setName(String name) {
	this.name = name;
    }

    public void setPosts(Collection<Post<?>> posts) {
	this.posts = posts;
    }

    public Collection<Post<?>> getPosts() {
	return posts;
    }

    public Collection<Assignment> getAssignment() {
	return assignment;
    }

    public void setAssignment(Collection<Assignment> assignment) {
	this.assignment = assignment;
    }

    public Collection<User> getUsers() {
	return users;
    }

    public void setUsers(Collection<User> users) {
	this.users = users;
    }

    public Image getImage() {
	return image;
    }

    public void setImage(Image image) {
	this.image = image;
    }

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

    public void addPostsCount() {
	postsCount++;
    }

    public void addImagesCount() {
	imagesCount++;
    }

    public ObjectId getOwnerId() {
	return ownerId;
    }

    public void setOwnerId(ObjectId ownerId) {
	this.ownerId = ownerId;
    }

    public static long getSerialversionuid() {
	return serialVersionUID;
    }

    public String getToken() {
	return token;
    }

    public void setToken(String token) {
	this.token = token;
    }

}