package org.uoc.pfc.eventual.web.websocket.messages;

class PostInfo {

	private final String userId;
	private final String eventId;
	private final String postId;

	public PostInfo(String userId, String eventId, String postId) {
		this.userId = userId;
		this.eventId = eventId;
		this.postId = postId;
	}

	public String getUserId() {
		return userId;
	}

	public String getEventId() {
		return eventId;
	}

	public String getPostId() {
		return postId;
	}

}