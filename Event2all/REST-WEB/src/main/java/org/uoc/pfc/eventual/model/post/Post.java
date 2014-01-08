package org.uoc.pfc.eventual.model.post;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.uoc.pfc.eventual.model.User;
import org.uoc.pfc.eventual.model.generic.EmbeddedEntity;
import org.uoc.pfc.eventual.utils.enums.PostEnum;

public class Post<T> extends EmbeddedEntity {

    private DateTime postTime;
    private User     user;
    private T	content;
    private PostEnum postEnum;
    private String   header;

    public Post(String header, PostEnum postEnum) {
	this.header = header;
	this.postEnum = postEnum;
    }

    public Post(String header, T content, PostEnum postEnum) {
	this.header = header;
	this.content = content;
	this.postEnum = postEnum;
    }

    public DateTime getPostTime() {
	return postTime;
    }

    public void setPostTime(DateTime postTime) {
	this.postTime = postTime;
    }

    public void setContent(T content) {
	this.content = content;
    }

    public PostEnum getPostEnum() {
	return postEnum;
    }

    public void setPostEnum(PostEnum postEnum) {
	this.postEnum = postEnum;
    }

    public String getHeader() {
	return header;
    }

    public void setHeader(String header) {
	this.header = header;
    }

    public T getContent() {
	return content;
    }

    public User getUser() {
	return user;
    }

    public void setUser(User user) {
	this.user = user;
    }

    @Override
    public ObjectId getId() {
	return id;
    }

    @Override
    public void setId(ObjectId id) {
	this.id = id;

    }
}