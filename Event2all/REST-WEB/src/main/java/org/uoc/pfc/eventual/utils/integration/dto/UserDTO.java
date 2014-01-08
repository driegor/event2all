package org.uoc.pfc.eventual.utils.integration.dto;

import java.util.Collection;
import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.uoc.pfc.eventual.utils.integration.dto.generic.JsonDTO;
import org.uoc.pfc.eventual.utils.integration.dto.media.ImageDTO;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class UserDTO extends JsonDTO {

	String name;
	String mail;
	String password;
	String status;
	String participates;
	Boolean owner;
	Boolean present;
	ImageDTO image;
	Collection<EventDTO> events;

	public void setEvents(Collection<EventDTO> events) {
		this.events = events;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

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

	public Collection<EventDTO> getEvents() {
		return events;
	}

	public void setEvents(List<EventDTO> events) {
		this.events = events;
	}

	public ImageDTO getImage() {
		return image;
	}

	public void setImage(ImageDTO image) {
		this.image = image;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getParticipates() {
		return participates;
	}

	public void setParticipates(String participates) {
		this.participates = participates;
	}

	@Override
	public boolean equals(Object obj) {

		if (obj == null) {
			return Boolean.FALSE;
		}

		if (obj instanceof UserDTO) {
			UserDTO other = (UserDTO) obj;
			return getMail().equals(other.getMail()) && getPassword().equals(other.getPassword());
		}
		return Boolean.FALSE;
	}

	public boolean equalsCredentials(UserDTO foundDTO) {
		// TODO Auto-generated method stub
		return false;
	}
}
