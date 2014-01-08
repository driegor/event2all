package org.uoc.pfc.eventual.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.uoc.pfc.eventual.model.generic.Entity;
import org.uoc.pfc.eventual.repository.impl.BaseRepository;
import org.uoc.pfc.eventual.service.IRestService;
import org.uoc.pfc.eventual.utils.integration.Adapter;
import org.uoc.pfc.eventual.utils.integration.dto.generic.JsonDTO;

public class RestService<DTO extends JsonDTO, E extends Entity, R extends BaseRepository<E>> implements IRestService<DTO> {

	@Autowired
	protected Adapter adapter;

	protected R repository;
	private final Class<E> entityClass;
	protected Class<DTO> dtoClass;

	public RestService(Class<DTO> dtoClass, Class<E> entityClass, R repository) {
		this.repository = repository;
		this.dtoClass = dtoClass;
		this.entityClass = entityClass;
	}

	@Override
	public DTO post(DTO bean) {
		E entity = adapter.toBean(bean, entityClass, customPrePostMapping(bean, entityClass));
		return adapter.toBean(repository.save(entity), dtoClass, customPostMapping(entity, dtoClass));
	}

	@Override
	public DTO put(DTO bean) {
		E entity = adapter.toBean(bean, entityClass, customPrePutMapping(bean, entityClass));
		return adapter.toBean(repository.update(entity), dtoClass, customPutMapping(entity, dtoClass));
	}

	@Override
	public Collection<DTO> get() {
		Collection<E> entities = repository.findAll();
		List<E> entityList = new ArrayList<E>(entities);
		Collections.sort(entityList, new Comparator<E>() {
			@Override
			public int compare(E o1, E o2) {
				return o1.getId().compareTo(o2.getId());
			}
		});
		return adapter.toBeans(entityList, dtoClass, customGetMapping(entityList, dtoClass));
	}

	@Override
	public DTO get(String id) {
		E e = repository.findOne(new ObjectId(id));
		return adapter.toBean(e, dtoClass, customGetMapping(e, dtoClass));
	}

	@Override
	public void delete(String id) {
		repository.delete(new ObjectId(id));
	}

	@Override
	public Collection<DTO> match(DTO dto) {

		Collection<E> entities = repository.executeQuery(dto.toMap());
		return adapter.toBeans(entities, dtoClass, customSearchMapping(entities, dtoClass));
	}

	private String customSearchMapping(Collection<E> entities, Class<DTO> dtoClass2) {

		return null;
	}

	@Override
	public boolean exists(String id) {
		return repository.exists(new ObjectId(id));
	}

	@Override
	public Long count() {
		return repository.count();
	}

	@Override
	public Collection<DTO> like(String like, String fields) {
		return adapter.toBeans(repository.like(like, fields), dtoClass);
	}

	protected String customGetMapping(Collection<E> entities, Class<DTO> dtoClass) {
		return null;
	}

	protected String customPostMapping(E entity, Class<DTO> dtoClass) {
		return null;
	}

	protected String customPrePostMapping(DTO bean, Class<E> dtoClass) {
		return null;
	}

	protected String customGetMapping(E e, Class<DTO> dtoClass) {
		return null;
	}

	protected String customPutMapping(E entity, Class<DTO> dtoClass) {
		return null;
	}

	protected String customPrePutMapping(DTO bean, Class<E> dtoClass) {
		return null;
	}

}
