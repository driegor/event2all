package org.uoc.pfc.eventual.web.utils.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.web.filter.OncePerRequestFilter;

public class CorsFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

	if ((request.getHeader("Access-Control-Request-Method") != null) && "OPTIONS".equals(request.getMethod())) {
	    // CORS "pre-flight" request
	    response.addHeader("Access-Control-Allow-Origin", "*");
	    response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
	    response.addHeader("Access-Control-Allow-Headers", "Content-Type,X-CSRF-Token");
	    response.addHeader("Access-Control-Max-Age", "1728000");
	}
	filterChain.doFilter(request, response);
    }

    public static HttpHeaders getAccessControllAllowOrigin() {
	HttpHeaders headers = new HttpHeaders();
	headers.add("Access-Control-Allow-Origin", "*");
	return headers;
    }
}