package org.uoc.pfc.eventual.repository.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory;
import org.uoc.pfc.eventual.model.Event;
import org.uoc.pfc.eventual.model.User;
import org.uoc.pfc.eventual.model.generic.Assignment;
import org.uoc.pfc.eventual.model.media.Image;

public class UserRepository extends BaseRepository<User> {

    public UserRepository(MongoRepositoryFactory mongoRepositoryFactory, MongoTemplate mongoTemplate) {
	super(mongoRepositoryFactory, mongoTemplate, User.class);
    }

    public Collection<User> inEvent(ObjectId eventId) {
	return in("assignment.id", eventId);
    }

    public Collection<User> notInEvent(ObjectId eventId) {
	return nin("assignment.id", eventId);
    }

    // sobreescribimos el método salvar del repositorio Restful para gestionar
    // los eventos asociados

    @Override
    public User update(User user) {
	User oldUser = findOne(user.getId());
	// solo actualizamos los datos principales
	oldUser.setName(user.getName());
	oldUser.setMail(user.getMail());
	return save(oldUser);
    }

    @Override
    public List<User> findAll() {
	List<User> users = super.findAll();

	// ampliamos la información de la clase usuario para cada item
	for (User u : users) {
	    expand(u);
	}

	return users;
    }

    private User expand(User e) {

	if (e == null) {
	    return e;
	}

	List<ObjectId> ids = new ArrayList<ObjectId>();

	// obtenemos los id's de los eventos donde participa este usuario
	for (Assignment ass : e.getAssignment()) {
	    ids.add(ass.getId());
	}

	Query query = new Query();
	query.addCriteria(Criteria.where("id").in(ids));
	e.setEvents(getMongoOperations().find(query, Event.class));
	return e;
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
	Page<User> users = super.findAll(pageable);
	// si el listado de objetos tiene objetos assigment, ampliamos su resultado
	for (User u : users) {
	    expand(u);
	}
	return users;
    }

    @Override
    public User findOne(ObjectId id) {
	return expand(super.findOne(id));
    }

    public void addImages(ObjectId userId, List<Image> images) {
	User user = findOne(userId);
	user.setImage(images.get(0));
	save(user);
    }

    public User findByMail(String mail) {

	Query query = new Query();
	query.addCriteria(Criteria.where("mail").is(mail));

	return executeQuery(query, Boolean.TRUE);
    }
}
