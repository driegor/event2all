package org.uoc.pfc.eventual.web.websocket.impl.tomcat;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;
import org.apache.catalina.websocket.WsOutbound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uoc.pfc.eventual.web.websocket.messages.ConnectionInfo;
import org.uoc.pfc.eventual.web.websocket.messages.MessageInfo;
import org.uoc.pfc.eventual.web.websocket.messages.StatusInfo;

import com.google.gson.Gson;

public abstract class GenericPostServlet extends WebSocketServlet {

    private static final long	 serialVersionUID = 1L;

    private static final Logger       log	      = LoggerFactory.getLogger(GenericPostServlet.class);

    private final Set<PostConnection> connections      = new CopyOnWriteArraySet<PostConnection>();

    @Override
    protected boolean verifyOrigin(String origin) {
	return true;
    }

    @Override
    protected abstract StreamInbound createWebSocketInbound(String subProtocol, HttpServletRequest request);

    class PostConnection extends MessageInbound {

	private final String userId;
	private final String eventId;
	private final String postId;

	private final Gson   jsonProcessor;

	protected PostConnection(String userId, String eventId) {
	    this(userId, eventId, null);
	}

	protected PostConnection(String userId, String eventId, String postId) {
	    this.eventId = eventId;
	    this.userId = userId;
	    this.postId = postId;
	    jsonProcessor = new Gson();
	}

	@Override
	// al conectarnos enviamos un mensaje indicando que nos hemos conectado al resto de usuarios de este websoket
	protected void onOpen(WsOutbound outbound) {
	    sendConnectionInfo(outbound);
	    sendStatusInfoToOtherUsers(new StatusInfo(userId, eventId, postId, StatusInfo.STATUS.CONNECTED));
	    connections.add(this);
	}

	@Override
	protected void onClose(int status) {
	    // al desconectarnos enviamos un menaje indicando que nos hemos desconectado al resto de usuarios de este evento
	    sendStatusInfoToOtherUsers(new StatusInfo(userId, eventId, postId, StatusInfo.STATUS.DISCONNECTED));
	    connections.remove(this);
	}

	@Override
	protected void onBinaryMessage(ByteBuffer byteBuffer) throws IOException {
	    throw new UnsupportedOperationException("Mensajes binarios no soportados");
	}

	@Override
	protected void onTextMessage(CharBuffer charBuffer) throws IOException {
	    final MessageInfo message = jsonProcessor.fromJson(charBuffer.toString(), MessageInfo.class);
	    final Collection<PostConnection> destinationConnection = connections2broadCast(message.getUserId(), message.getEventId(), message.getPostId());
	    if (destinationConnection != null) {
		for (PostConnection conection : destinationConnection) {
		    conection.getWsOutbound().writeTextMessage(CharBuffer.wrap(jsonProcessor.toJson(message)));
		}
	    } else {
		log.warn("Se está intentando enviar un mensaje a un usuario que ya no está conectado");
	    }
	}

	private void sendConnectionInfo(WsOutbound outbound) {
	    final List<String> activeUsers = getActiveUsers();
	    final ConnectionInfo connectionInfoMessage = new ConnectionInfo(userId, eventId, postId, activeUsers);
	    try {
		outbound.writeTextMessage(CharBuffer.wrap(jsonProcessor.toJson(connectionInfoMessage)));
	    } catch (IOException e) {
		log.error("No se ha podido enviar el mensaje", e);
	    }
	}

	// obtenemos la lista de usuarios conectados a este evento
	private List<String> getActiveUsers() {
	    final List<String> activeUsers = new ArrayList<String>();
	    for (PostConnection connection : connections.toArray(new PostConnection[connections.size()])) {
		if (connection.eventId.equals(eventId)) {
		    activeUsers.add(connection.userId);
		}
	    }
	    return activeUsers;
	}

	// enviamos un menaje a todos los usuarios, de un evento, excepto a nosotros mismos
	private void sendStatusInfoToOtherUsers(StatusInfo message) {
	    final Collection<PostConnection> otherUsersConnections = connections2broadCast(message.getUserId(), message.getEventId(), message.getPostId());
	    for (PostConnection connection : otherUsersConnections) {
		try {
		    connection.getWsOutbound().writeTextMessage(CharBuffer.wrap(jsonProcessor.toJson(message)));
		} catch (IOException e) {
		    log.error("No se ha podido enviar el mensaje", e);
		}
	    }
	}

	// obtenemos todas las conexiones de este evento menos la nuestra
	private Collection<PostConnection> connections2broadCast(String userId, String eventId, String postId) {
	    final Collection<PostConnection> to = new ArrayList<GenericPostServlet.PostConnection>();

	    for (PostConnection connection : connections.toArray(new PostConnection[connections.size()])) {

		// no queremos enviarnos un mensaje a nosotros mismos
		if (((connection.eventId != null) && connection.eventId.equals(eventId))
			&& (((postId == null) || (((postId != null) && (connection.postId.equals(postId)))))) && !connection.equals(this)) {
		    to.add(connection);
		}
	    }
	    return to;
	}
    }

}