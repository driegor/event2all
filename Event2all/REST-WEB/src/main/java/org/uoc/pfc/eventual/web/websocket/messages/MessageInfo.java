package org.uoc.pfc.eventual.web.websocket.messages;

public class MessageInfo extends PostInfo {

	private final String message;

	public MessageInfo(String userId, String eventId, String postId, String message) {
		super(userId, eventId, postId);
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

}