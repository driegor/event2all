package org.uoc.pfc.eventual.web.websocket.impl.tomcat;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.websocket.StreamInbound;

//este WebSocketServlet, permite intercambiar comentarios de un post concreto

@WebServlet(urlPatterns = "/api/post/comments/realTime")
public class RealTimePostCommentsServlet extends GenericPostServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected StreamInbound createWebSocketInbound(String subProtocol, HttpServletRequest request) {

		final String userId = request.getParameter("userId");
		final String eventId = request.getParameter("eventId");
		final String postId = request.getParameter("postId");
		return new PostConnection(userId, eventId, postId);
	}

}