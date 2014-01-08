package org.uoc.pfc.eventual.repository.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.beanutils.PropertyUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;
import org.uoc.pfc.eventual.model.generic.Assignment;
import org.uoc.pfc.eventual.repository.IGenericRepository;
import org.uoc.pfc.eventual.repository.exception.MongoDBException;
import org.uoc.pfc.eventual.repository.impl.callback.ReferenceIdCallBack;

import com.mongodb.WriteResult;

public class BaseRepository<E> extends SimpleMongoRepository<E, ObjectId> implements IGenericRepository<E> {

	protected Class<E> entityClass;

	public E update(E entity) {
		return this.save(entity);
	}

	public BaseRepository(MongoRepositoryFactory factory, @Qualifier("mongoTemplate") MongoTemplate mongoTemplate, Class<E> entityClass) {
		super(new MongoRepositoryFactory(mongoTemplate).<E, ObjectId> getEntityInformation(entityClass), mongoTemplate);
		this.entityClass = entityClass;
	}

	@Override
	public Collection<E> executeQuery(Query query) {
		return getMongoOperations().find(query, entityClass);
	}

	@Override
	public E executeQuery(Query query, boolean onlyFirst) {
		return getMongoOperations().findOne(query, entityClass);
	}

	@Override
	public Collection<E> executeQuery(Map<String, Object> searchParams) {

		Query query = new Query();
		for (Entry<String, Object> entrySet : searchParams.entrySet()) {
			if (entrySet.getValue() != null) {
				query.addCriteria(Criteria.where(entrySet.getKey()).is(entrySet.getValue()));
			}
		}

		return getMongoOperations().find(query, entityClass);
	}

	@Override
	public Collection<E> in(String field, ObjectId... ids) {
		Query query = new Query();
		query.addCriteria(Criteria.where(field).in((Object[]) ids));

		return getMongoOperations().find(query, entityClass);
	}

	@Override
	public Collection<E> nin(String field, ObjectId... ids) {
		Query query = new Query();
		query.addCriteria(Criteria.where(field).nin((Object[]) ids));

		return getMongoOperations().find(query, entityClass);
	}

	public Collection<E> like(String like, String fields) {
		return executeQuery("^.*" + like + ".*$", fields);
	}

	public Collection<E> executeQuery(String regexp, String fields) {

		Query query = new Query();
		String[] tokens = fields.split(",");

		Criteria[] orCriterias = new Criteria[tokens.length];

		for (int i = 0; i < tokens.length; i++) {
			orCriterias[i] = Criteria.where(tokens[i]).regex(regexp, "i");
		}

		query.addCriteria(new Criteria().orOperator(orCriterias));
		return getMongoOperations().find(query, entityClass);
	}

	@Override
	public boolean updateFirst(Query query, Update update) {
		WriteResult wr = getMongoOperations().updateFirst(query, update, entityClass);
		return wr.getN() > 0;
	}

	public <E2> boolean addReferenceId(ObjectId id, ObjectId referenceId, String referenceIdField, ReferenceIdCallBack<E2> referenceIdCallBack, Boolean owner) {
		return addReferenceId(id, referenceId, "id", referenceIdField, referenceIdCallBack, null, owner);
	}

	public <E2> boolean addReferenceId(ObjectId id, ObjectId referenceId, String referenceIdField, ObjectId tx) {
		return addReferenceId(id, referenceId, "id", referenceIdField, null, tx, Boolean.FALSE);
	}

	public <E2> boolean addReferenceId(ObjectId id, ObjectId referenceId, String idField, String referenceIdField, ReferenceIdCallBack<E2> referenceIdCallBack,
			ObjectId tx, boolean owner) throws MongoDBException {

		boolean ok = Boolean.FALSE;
		boolean written = Boolean.FALSE;

		// creamos un id de transaccion si no existe una ya previamente creada
		if (tx == null) {
			tx = new ObjectId();
		}

		// creamos la asignacion para el objeto referenciado
		Assignment assignment = new Assignment(tx, referenceId, owner);

		try {

			// comprobamos que no exista ya una asignacion previa de este objeto
			Query existQuery = new Query(Criteria.where(idField).is(id));
			existQuery.addCriteria(Criteria.where(referenceIdField + ".id").in(referenceId));
			Collection<E> result = executeQuery(existQuery);

			if ((result != null) && (result.size() > 0)) {
				throw new MongoDBException("Assignment already exists");
			}

			Query userQuery = new Query(Criteria.where(idField).is(id));
			boolean update = updateFirst(userQuery, new Update().push(referenceIdField, assignment));

			if (!update) {
				// para mantener la integridad, si no se ha podido actualizar los datos, para
				// evitar tener valores en una entidad y en otra no, lanzamos una excepción
				throw new MongoDBException("Error updating data");
			}

			written = Boolean.TRUE;

			if (referenceIdCallBack != null) {
				referenceIdCallBack.addCallBack(referenceId, id, tx);
			}
			ok = Boolean.TRUE;

		} catch (Exception e) {

			// sólo si se ha llegado a escribir desharemos el update para esta transacción
			if (written) {
				Query userQuery = new Query(Criteria.where(idField).is(id));
				updateFirst(userQuery, new Update().pull(referenceIdField, assignment));
			}
			throw new MongoDBException("Error assigning to entity " + id + " of type " + this.entityClass + " referenced entity with id " + id, e);
		}
		return ok;
	}

	public <E2> boolean removeReferenceId(ObjectId id, ObjectId referenceId, String referenceIdField, ObjectId tx) {
		return removeReferenceId(id, referenceId, "id", referenceIdField, tx, null);
	}

	public <E2> boolean removeReferenceId(ObjectId id, ObjectId referenceId, String referenceIdField, ReferenceIdCallBack<E2> referenceIdCallBack) {
		return removeReferenceId(id, referenceId, "id", referenceIdField, null, referenceIdCallBack);
	}

	@SuppressWarnings("unchecked")
	public <E2> boolean removeReferenceId(ObjectId id, ObjectId referenceId, String idField, String referenceIdField, ObjectId tx,
			ReferenceIdCallBack<E2> referenceIdCallBack) {

		boolean ok = Boolean.FALSE;
		boolean written = Boolean.FALSE;

		Assignment assignment = null;

		try {

			// obtenemos el id que queremos eliminar
			E e = findOne(id);

			// obtenemos la entidad a actualizar

			Collection<Assignment> assignments = (Collection<Assignment>) PropertyUtils.getProperty(e, referenceIdField);
			Iterator<Assignment> it = assignments.iterator();

			while (it.hasNext() && !written) {
				assignment = it.next();
				// garantizamos que no vamos a obtener un objeto insertado posteriomente
				if (assignment.getId().equals(referenceId) && ((tx == null) || ((tx != null) && tx.equals(assignment.getTx())))) {
					Query eventQuery = new Query(Criteria.where(idField).is(id));
					updateFirst(eventQuery, new Update().pull(referenceIdField, assignment));
					written = Boolean.TRUE;
					if (referenceIdCallBack != null) {
						referenceIdCallBack.removeCallBack(referenceId, id, assignment.getTx());
					}
					ok = Boolean.TRUE;
				}
			}
		} catch (Exception e) {

			// sólo si se ha llegado a escribir desharemos el update para esta transaccion
			if (written) {
				Query eventQuery = new Query(Criteria.where(idField).is(id));
				updateFirst(eventQuery, new Update().push(referenceIdField, assignment));
			}
			throw new MongoDBException("Error unassigning from entity " + id + " of type " + this.entityClass + " embeddid entity with id " + id, e);
		}
		return ok;
	}
}
