package org.uoc.pfc.eventual.web.interceptor.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.uoc.pfc.eventual.config.AppConst;
import org.uoc.pfc.eventual.service.ISecurityService;
import org.uoc.pfc.eventual.utils.integration.security.TokenDTO;
import org.uoc.pfc.eventual.web.controller.rest.AbstractRestController;

public class ValidTokenInterceptor implements HandlerInterceptor {

    @Autowired
    ISecurityService securityService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

	// no nos interesa interceptar estos métodos
	if (!(handler instanceof HandlerMethod)) {
	    return true;
	}

	HandlerMethod method = (HandlerMethod) handler;
	// interceptaremos todos los controladores de la capa rest
	if (AbstractRestController.class.isAssignableFrom(method.getBean().getClass())) {

	    // por defecto todos los métodos necesitará credenciales, a excepción de algunos pocos
	    // como por ejemplo la pantalla de login
	    SkipSecurity methodAnnotation = method.getMethodAnnotation(SkipSecurity.class);
	    if (methodAnnotation != null) {
		return true;
	    }

	    String csrf_token = request.getHeader(AppConst.Security.CSRF_TOKEN);

	    // comprobamos que el usuario exista en el repositorio de tokens
	    TokenDTO tokenDTO = csrf_token != null ? securityService.findToken(csrf_token) : null;

	    if (tokenDTO == null) {

		response.addHeader("Access-Control-Allow-Origin", "*");
		// en caso contrario devolvemos un 401
		response.sendError(HttpStatus.UNAUTHORIZED.value());
		return false;
	    }
	}
	return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }
}