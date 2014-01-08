package org.uoc.pfc.eventual.web.websocket.messages;

public class StatusInfo extends PostInfo {

	public enum STATUS {
		CONNECTED, DISCONNECTED
	}

	private final STATUS status;

	public StatusInfo(String userId, String eventId, String postId, STATUS status) {
		super(userId, eventId, postId);
		this.status = status;
	}

	public STATUS getStatus() {
		return status;
	}
}