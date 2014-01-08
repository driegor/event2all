package org.uoc.pfc.eventual.model;

import java.util.ArrayList;
import java.util.Collection;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.uoc.pfc.eventual.model.generic.Assignment;
import org.uoc.pfc.eventual.model.generic.AuditEntity;
import org.uoc.pfc.eventual.model.media.Image;

@Document
public class User extends AuditEntity {

	/**
     * 
     */
	private static final long serialVersionUID = 5630702693705203357L;
	private String name;
	private String password;
	private String mail;
	private String status;
	private Image image;
	private Collection<Assignment> assignment = new ArrayList<Assignment>();
	// estos datos no se guardan en BD, por eso añadimos una anotación Transient, el valor se calculará
	// a partir de los datos del capo assignment una vez obtenidos.

	@Transient
	private Collection<Event> events = new ArrayList<Event>();

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	@Override
	public ObjectId getId() {
		return id;
	}

	@Override
	public void setId(ObjectId id) {
		this.id = id;
	}

	public Collection<Assignment> getAssignment() {
		return assignment;
	}

	public void setAssignment(Collection<Assignment> assignment) {
		this.assignment = assignment;
	}

	public Collection<Event> getEvents() {
		return events;
	}

	public void setEvents(Collection<Event> events) {
		this.events = events;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
