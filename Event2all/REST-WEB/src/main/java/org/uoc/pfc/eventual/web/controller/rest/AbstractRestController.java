package org.uoc.pfc.eventual.web.controller.rest;

import java.util.Collection;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.uoc.pfc.eventual.service.IRestService;
import org.uoc.pfc.eventual.web.controller.utils.RequestExtractor;
import org.uoc.pfc.eventual.web.interceptor.security.SkipSecurity;
import org.uoc.pfc.eventual.web.utils.filter.CorsFilter;

public abstract class AbstractRestController<DTO, RS extends IRestService<DTO>> {

    private final Class<DTO> dtoClass;

    protected abstract RS getRestService();

    public AbstractRestController(Class<DTO> dtoClass) {
	this.dtoClass = dtoClass;
    }

    @RequestMapping(method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<DTO> post(@RequestBody DTO bean) {
	HttpHeaders headers = CorsFilter.getAccessControllAllowOrigin();
	ResponseEntity<DTO> entity = new ResponseEntity<DTO>(getRestService().post(bean), headers, HttpStatus.CREATED);
	return entity;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity<DTO> put(@PathVariable("id") String id, @RequestBody DTO bean) {
	HttpHeaders headers = CorsFilter.getAccessControllAllowOrigin();
	ResponseEntity<DTO> entity = new ResponseEntity<DTO>(getRestService().put(bean), headers, HttpStatus.OK);
	return entity;
    }

    @SkipSecurity
    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<Collection<DTO>> get() {

	Collection<DTO> list = getRestService().get();
	HttpHeaders headers = CorsFilter.getAccessControllAllowOrigin();
	ResponseEntity<Collection<DTO>> entity = new ResponseEntity<Collection<DTO>>(list, headers, HttpStatus.OK);
	return entity;
    }

    @RequestMapping(value = "/{id}/exists", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<Boolean> exists(@PathVariable("id") String id) {

	Boolean exists = getRestService().exists(id);
	HttpHeaders headers = CorsFilter.getAccessControllAllowOrigin();
	ResponseEntity<Boolean> entity = new ResponseEntity<Boolean>(exists, headers, HttpStatus.OK);
	return entity;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<DTO> get(@PathVariable("id") String id) {
	DTO dto = getRestService().get(id);
	HttpHeaders headers = CorsFilter.getAccessControllAllowOrigin();
	ResponseEntity<DTO> entity = new ResponseEntity<DTO>(dto, headers, HttpStatus.OK);
	return entity;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity<String> delete(@PathVariable("id") String id) {
	getRestService().delete(id);
	HttpHeaders headers = CorsFilter.getAccessControllAllowOrigin();
	ResponseEntity<String> entity = new ResponseEntity<String>(id, headers, HttpStatus.OK);
	return entity;
    }

    @RequestMapping(value = "/match", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<Collection<DTO>> match(WebRequest request) {
	DTO dto = RequestExtractor.extractPojo(request, dtoClass);
	HttpHeaders headers = CorsFilter.getAccessControllAllowOrigin();
	Collection<DTO> list = getRestService().match(dto);
	ResponseEntity<Collection<DTO>> entity = new ResponseEntity<Collection<DTO>>(list, headers, HttpStatus.OK);
	return entity;
    }

    @RequestMapping(value = "/count", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<Long> count() {
	HttpHeaders headers = CorsFilter.getAccessControllAllowOrigin();
	ResponseEntity<Long> entity = new ResponseEntity<Long>(getRestService().count(), headers, HttpStatus.OK);
	return entity;
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleRemainingExceptions(Exception ex) {
	return new ModelAndView("error");
    }
}
