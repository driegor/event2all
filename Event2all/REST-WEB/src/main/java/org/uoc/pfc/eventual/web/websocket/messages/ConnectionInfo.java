package org.uoc.pfc.eventual.web.websocket.messages;

import java.util.List;

public class ConnectionInfo extends PostInfo {

	private final List<String> activeUsers;

	public ConnectionInfo(String userId, String eventId, String postId, List<String> activeUsers) {
		super(userId, eventId, postId);
		this.activeUsers = activeUsers;
	}

	public List<String> getActiveUsers() {
		return activeUsers;
	}

}