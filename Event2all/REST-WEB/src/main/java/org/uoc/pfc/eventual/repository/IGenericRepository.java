package org.uoc.pfc.eventual.repository;

import java.util.Collection;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface IGenericRepository<E> extends PagingAndSortingRepository<E, ObjectId> {

	public Collection<E> executeQuery(Map<String, Object> searchParams);

	public Collection<E> executeQuery(Query query);

	public E executeQuery(Query query, boolean onlyFirst);

	public Collection<E> in(String field, ObjectId... ids);

	public Collection<E> nin(String field, ObjectId... ids);

	public boolean updateFirst(Query query, Update update);

}