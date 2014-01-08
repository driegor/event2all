package org.uoc.pfc.eventual.web.websocket.impl.tomcat;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.websocket.StreamInbound;

//este WebSocketServlet, permite intercambiar mensajes entre todos los participantes de un
//evento que estén conectados en ese momento

@WebServlet(urlPatterns = "/api/post/realTime")
public class RealTimePostServlet extends GenericPostServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected StreamInbound createWebSocketInbound(String subProtocol, HttpServletRequest request) {

		final String userId = request.getParameter("userId");
		final String eventId = request.getParameter("eventId");
		return new PostConnection(userId, eventId);
	}

}