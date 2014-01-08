package org.uoc.pfc.eventual.service;

import java.util.Collection;

public interface IRestService<DTO> {

    public boolean exists(String id);

    public DTO get(String id);

    public Collection<DTO> get();

    public void delete(String id);

    public DTO post(DTO entity);

    public DTO put(DTO entity);

    public Collection<DTO> match(DTO dto);

    Collection<DTO> like(String like, String fields);

    public Long count();
}
