package org.uoc.pfc.eventual.utils.integration.dto.post;

import org.uoc.pfc.eventual.utils.enums.PostEnum;
import org.uoc.pfc.eventual.utils.integration.dto.UserDTO;
import org.uoc.pfc.eventual.utils.integration.dto.generic.JsonDTO;

public class PostDTO<T> extends JsonDTO {

    private String   postTime;
    private UserDTO  user;
    private PostEnum postEnum;
    private String   header;
    private T	content;

    public void setPostTime(String postTime) {
	this.postTime = postTime;
    }

    public void setUser(UserDTO user) {
	this.user = user;
    }

    public void setPostEnum(PostEnum postEnum) {
	this.postEnum = postEnum;
    }

    public void setHeader(String header) {
	this.header = header;
    }

    public String getPostTime() {
	return postTime;
    }

    public UserDTO getUser() {
	return user;
    }

    public PostEnum getPostEnum() {
	return postEnum;
    }

    public String getHeader() {
	return header;
    }

    public T getContent() {
	return content;
    }

    public void setContent(T content) {
	this.content = content;
    }

}