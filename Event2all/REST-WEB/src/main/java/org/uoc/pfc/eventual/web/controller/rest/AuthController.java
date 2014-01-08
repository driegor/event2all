package org.uoc.pfc.eventual.web.controller.rest;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.uoc.pfc.eventual.config.AppConst;
import org.uoc.pfc.eventual.service.ISecurityService;
import org.uoc.pfc.eventual.utils.integration.dto.UserDTO;
import org.uoc.pfc.eventual.utils.integration.security.TokenDTO;
import org.uoc.pfc.eventual.utils.token.TokenGeneratorUtils;
import org.uoc.pfc.eventual.web.interceptor.security.SkipSecurity;
import org.uoc.pfc.eventual.web.utils.filter.CorsFilter;

@Controller
@RequestMapping(value = "/api/auth")
public class AuthController {

	@Autowired
	ISecurityService securityService;

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody
	ResponseEntity<TokenDTO> isAuth(HttpServletRequest request) {

		HttpHeaders headers = CorsFilter.getAccessControllAllowOrigin();
		ResponseEntity<TokenDTO> response = null;

		// obtenemos el token actual
		String csrf_token = request.getHeader(AppConst.Security.CSRF_TOKEN);

		// comprobamos que el usuario exista en el repositorio de tokens
		TokenDTO tokenDTO = csrf_token != null ? securityService.findToken(csrf_token) : null;

		if (tokenDTO != null) {
			// si existe devolvemos el token actual
			response = new ResponseEntity<TokenDTO>(tokenDTO, headers, HttpStatus.OK);

		} else {
			// sino existe creamos una nueva respuesta solo con el nuevo token
			TokenDTO new_token = new TokenDTO(TokenGeneratorUtils.getRandomToken(32));
			response = new ResponseEntity<TokenDTO>(new_token, headers, HttpStatus.UNAUTHORIZED);
		}
		return response;
	}

	@SkipSecurity
	@RequestMapping(value = "/authenticate", method = RequestMethod.POST)
	public @ResponseBody
	ResponseEntity<TokenDTO> login(@RequestBody UserDTO user, HttpServletRequest request) {

		HttpHeaders headers = CorsFilter.getAccessControllAllowOrigin();
		ResponseEntity<TokenDTO> response = null;

		// obtenemos el token actual
		String csrf_token = request.getHeader(AppConst.Security.CSRF_TOKEN);

		// el usuario ya está logado ??

		TokenDTO token = securityService.findToken(csrf_token);

		if (token != null) {
			response = new ResponseEntity<TokenDTO>(token, headers, HttpStatus.OK);
			return response;
		}

		// el usuario no está logado, las credenciales son correctas ??

		UserDTO authUsr = securityService.getAuthenticated(user);

		if (authUsr != null) {
			// creamos una nueva entrada en el repository de tokens, asociando el token al usuario
			TokenDTO saved_token = securityService.saveToken(authUsr, csrf_token);

			// lo devolvemos como resultado
			response = new ResponseEntity<TokenDTO>(saved_token, headers, HttpStatus.OK);

		} else {
			// el usuario no se ha podido autentificar en la aplicación
			response = new ResponseEntity<TokenDTO>(new TokenDTO(), headers, HttpStatus.UNAUTHORIZED);
		}
		return response;
	}

	@RequestMapping(value = "/logout", method = RequestMethod.DELETE)
	public @ResponseBody
	ResponseEntity<TokenDTO> logout(HttpServletRequest request) {

		HttpHeaders headers = CorsFilter.getAccessControllAllowOrigin();
		ResponseEntity<TokenDTO> response = null;

		// obtenemos el token actual
		String csrf_token = request.getHeader(AppConst.Security.CSRF_TOKEN);

		// borramos el token
		securityService.removeToken(csrf_token);

		// creamos una nueva respuesta solo con el nuevo token
		TokenDTO new_token = new TokenDTO(TokenGeneratorUtils.getRandomToken(32));
		response = new ResponseEntity<TokenDTO>(new_token, headers, HttpStatus.OK);
		return response;
	}
}
