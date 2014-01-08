package org.uoc.pfc.eventual.repository.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory;
import org.uoc.pfc.eventual.model.Event;
import org.uoc.pfc.eventual.model.User;
import org.uoc.pfc.eventual.model.generic.Assignment;
import org.uoc.pfc.eventual.model.media.Image;
import org.uoc.pfc.eventual.model.post.Comment;
import org.uoc.pfc.eventual.model.post.CommentedPictures;
import org.uoc.pfc.eventual.model.post.Post;
import org.uoc.pfc.eventual.repository.impl.callback.ReferenceIdCallBack;
import org.uoc.pfc.eventual.utils.enums.PostEnum;

public class EventRepository extends BaseRepository<Event> implements ReferenceIdCallBack<User> {

	@Autowired
	UserRepository userRepository;

	public EventRepository(MongoRepositoryFactory mongoRepositoryFactory, MongoTemplate mongoTemplate) {
		super(mongoRepositoryFactory, mongoTemplate, Event.class);
	}

	// sobreescribimos el método save del BaseRepository para gestionar
	// si el usuario debe auto asignarse a este evento
	@SuppressWarnings("unchecked")
	@Override
	public Event save(Event event) {
		boolean isNew = event.getId() != null;
		event = super.save(event);
		// una vez creado el evento, asignamos el usuario actual
		if ((event.getOwnerId() != null) && !isNew) {
			join(event.getId(), event.getOwnerId(), Boolean.TRUE);
		}
		return event;
	}

	// sobreescribimos el método update del BaseRepository para gestionar
	// los posts

	@Override
	public Event update(Event event) {
		Event oldEvent = findOne(event.getId());
		oldEvent.setName(event.getName());
		oldEvent.setDescription(event.getDescription());
		oldEvent.setDate(event.getDate());
		oldEvent.setToken(event.getToken());
		return super.save(oldEvent);
	}

	public Collection<Event> byUser(ObjectId... userIds) {

		Collection<Event> events = in("assignment.id", userIds);
		// ampliamos la información de la clase evento para cada item
		for (Event e : events) {
			expand(e);
		}
		return events;
	}

	@Override
	public List<Event> findAll() {
		List<Event> events = super.findAll();

		// ampliamos la información de la clase evento para cada item
		for (Event e : events) {
			expand(e);
		}
		return events;
	}

	private Event expand(Event e) {
		if (e == null) {
			return e;
		}

		List<ObjectId> ids = new ArrayList<ObjectId>();
		// obtenemos los id's de los usuarios de este evento
		for (Assignment ass : e.getAssignment()) {
			ids.add(ass.getId());
		}

		Query query = new Query();
		query.addCriteria(Criteria.where("id").in(ids));
		e.setUsers(getMongoOperations().find(query, User.class));
		return e;
	}

	@Override
	public Page<Event> findAll(Pageable pageable) {
		Page<Event> events = super.findAll(pageable);

		// si el listado de objetos tiene objetos assigment, ampliamos su resultado
		for (Event e : events) {
			expand(e);
		}

		return events;
	}

	@Override
	public Event findOne(ObjectId id) {
		return expand(super.findOne(id));
	}

	public Post<?> post(ObjectId eventId, ObjectId userId, Post<?> post) {

		Event event = findOne(eventId);
		User user = new User();
		user.setId(userId);
		post.setPostTime(new DateTime());
		post.setUser(user);
		event.getPosts().add(post);

		if (post instanceof Comment) {
			event.addPostsCount();
		} else if (post instanceof CommentedPictures) {
			event.addImagesCount();
		}

		List<Post<?>> posts = new ArrayList<Post<?>>(event.getPosts());
		// ordernamos antes de guardar
		Collections.sort(posts, new Comparator<Post<?>>() {
			@Override
			public int compare(Post<?> o1, Post<?> o2) {
				return o2.getPostTime().compareTo(o1.getPostTime());
			}
		});
		event.setPosts(posts);
		save(event);

		if (post.getUser() != null) {
			post.setUser(userRepository.findOne(post.getUser().getId()));
		}

		return post;
	}

	public Collection<User> getUsers(ObjectId eventId) {
		return userRepository.in("assignment.id", eventId);
	}

	public boolean join(ObjectId eventId, ObjectId userId, boolean owner) {
		return addReferenceId(eventId, userId, "assignment", this, owner);
	}

	public boolean unJoin(ObjectId eventId, ObjectId userId) {
		return removeReferenceId(eventId, userId, "assignment", this);
	}

	public Image addImages(ObjectId eventId, List<Image> images) {
		Image image = images.get(0);
		Event event = findOne(eventId);
		event.setImage(image);
		save(event);
		return image;
	}

	public Collection<Post<?>> getPosts(ObjectId eventId, PostEnum postEnum) {
		return getPosts(eventId, null, null, postEnum);
	}

	public Collection<Post<?>> getPosts(ObjectId eventId) {
		return getPosts(eventId, null, null, null);
	}

	public Collection<Post<?>> getPosts(ObjectId eventId, ObjectId userId) {
		return getPosts(eventId, userId, null, null);
	}

	private Collection<Post<?>> getPosts(ObjectId eventId, ObjectId userId, ObjectId postId, PostEnum postEnum) {

		Collection<Post<?>> posts = new ArrayList<Post<?>>();
		Event event = findOne(eventId);

		for (Post<?> post : event.getPosts()) {
			boolean add = Boolean.TRUE;
			// filtramos por usuario ??
			if ((userId != null) && (post.getUser() != null) && !post.getUser().getId().equals(userId)) {
				add = Boolean.FALSE;
			}
			// filtramos por tipo ??
			if ((postEnum != null) && !post.getPostEnum().equals(postEnum)) {
				add = Boolean.FALSE;
			}

			// filtramos por id ??
			if ((postId != null) && !post.getId().equals(postId)) {
				add = Boolean.FALSE;
			}

			if (add) {
				if (post.getUser() != null) {
					post.setUser(userRepository.findOne(post.getUser().getId()));
				}
				if (post.getPostEnum().equals(PostEnum.COMMENTED_PICTURES)) {
					CommentedPictures cpPost = (CommentedPictures) post;
					Iterator<Comment> commentIt = cpPost.getContent().getComments().iterator();
					while (commentIt.hasNext()) {
						Comment comment = commentIt.next();
						if (comment.getUser() != null) {
							comment.setUser(userRepository.findOne(comment.getUser().getId()));
						}
					}
					posts.add(cpPost);
				} else {
					posts.add(post);
				}
			}
		}
		return posts;
	}

	public Collection<Post<?>> getPictures(PageRequest pageRequest, ObjectId eventId) {
		return getPosts(eventId, PostEnum.COMMENTED_PICTURES);
	}

	public Collection<Post<?>> getPictures(ObjectId eventId) {
		return getPosts(eventId, PostEnum.COMMENTED_PICTURES);
	}

	public Collection<Post<?>> getPicturesByUser(PageRequest pageRequest, ObjectId eventId, ObjectId userId) {
		return getPosts(eventId, userId, null, PostEnum.COMMENTED_PICTURES);
	}

	public Collection<Post<?>> getPicturesByUser(ObjectId eventId) {
		return getPosts(eventId, PostEnum.COMMENTED_PICTURES);
	}

	public Collection<Post<?>> getComments(PageRequest pageRequest, ObjectId eventId) {
		return getPosts(eventId, PostEnum.COMMENT);
	}

	public Collection<Post<?>> getComments(ObjectId eventId) {
		return getPosts(eventId, PostEnum.COMMENT);
	}

	public Collection<Post<?>> getCommentsByUser(PageRequest pageRequest, ObjectId eventId, ObjectId userId) {
		return getPosts(eventId, userId, null, PostEnum.COMMENT);
	}

	public Collection<Post<?>> getCommentsByUser(ObjectId eventId, ObjectId userId) {
		return getPosts(eventId, userId, null, PostEnum.COMMENT);
	}

	public Post<?> getPost(ObjectId eventId, ObjectId postId) {

		Collection<Post<?>> posts = getPosts(eventId, null, postId, null);

		if ((posts != null) && (posts.size() > 0)) {
			return posts.iterator().next();
		}
		return null;
	}

	public Comment addComment(ObjectId eventId, ObjectId postId, ObjectId userId, Comment comment) {

		User user = new User();
		user.setId(userId);

		comment.setPostTime(new DateTime());
		comment.setUser(user);

		Event event = findOne(eventId);

		Iterator<Post<?>> it = event.getPosts().iterator();

		boolean found = Boolean.FALSE;
		while (it.hasNext()) {
			Post<?> post = it.next();
			if (post.getId().equals(postId) && post.getPostEnum().equals(PostEnum.COMMENTED_PICTURES)) {

				CommentedPictures cp = (CommentedPictures) post;
				cp.getContent().getComments().add(comment);

				List<Comment> comments = new ArrayList<Comment>(cp.getContent().getComments());

				// ordenamos antes de guardar
				Collections.sort(comments, new Comparator<Comment>() {
					@Override
					public int compare(Comment o1, Comment o2) {
						return o2.getPostTime().compareTo(o1.getPostTime());
					}
				});
				cp.getContent().setComments(comments);
				found = Boolean.TRUE;
			}
		}
		if (found) {
			save(event);
		}

		if (comment.getUser() != null) {
			comment.setUser(userRepository.findOne(comment.getUser().getId()));
		}

		return comment;
	}

	public Comment getComment(ObjectId eventId, ObjectId postId, ObjectId commentId) {

		Event event = findOne(eventId);
		Comment foundComment = null;
		Iterator<Post<?>> it = event.getPosts().iterator();

		boolean found = Boolean.FALSE;
		while (it.hasNext() && !found) {

			Post<?> post = it.next();
			if (post.getId().equals(postId) && post.getPostEnum().equals(PostEnum.COMMENTED_PICTURES)) {

				Iterator<Comment> commentIt = ((CommentedPictures) post).getContent().getComments().iterator();

				while (commentIt.hasNext() && !found) {
					Comment comment = commentIt.next();
					if (comment.getId().equals(commentId)) {
						foundComment = comment;
						if (comment.getUser() != null) {
							comment.setUser(userRepository.findOne(comment.getUser().getId()));
						}
						found = Boolean.TRUE;
					}
				}
			}
		}
		return foundComment;
	}

	// tx callback //

	@Override
	public void removeCallBack(ObjectId id, ObjectId referenceId, ObjectId tx) {
		userRepository.removeReferenceId(id, referenceId, "assignment", tx);

	}

	@Override
	public void addCallBack(ObjectId id, ObjectId referenceId, ObjectId tx) {
		userRepository.addReferenceId(id, referenceId, "assignment", tx);

	}

	public boolean existsToken(String token) {

		Query query = new Query();
		query.addCriteria(Criteria.where("token").in(token));
		Collection<Event> match = executeQuery(query);
		return (match != null) && (match.size() > 0);
	}

	public String byToken(String token) {
		Query query = new Query();
		query.addCriteria(Criteria.where("token").in(token));
		Event match = executeQuery(query, Boolean.TRUE);
		return match != null ? match.getId().toString() : null;
	}
}
