package org.uoc.pfc.eventual.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.uoc.pfc.eventual.config.AppConst;
import org.uoc.pfc.eventual.model.security.Token;
import org.uoc.pfc.eventual.repository.TokenRepository;
import org.uoc.pfc.eventual.service.ISecurityService;
import org.uoc.pfc.eventual.utils.integration.Adapter;
import org.uoc.pfc.eventual.utils.integration.dto.UserDTO;
import org.uoc.pfc.eventual.utils.integration.security.TokenDTO;

@Service
public class SecurityService implements ISecurityService {

    @Autowired
    protected Adapter adapter;

    @Autowired
    UserService       userService;

    @Autowired
    TokenRepository   tokenRepository;

    @Override
    @Cacheable(value = "token", key = "#p0")
    public TokenDTO findToken(String token) {
	return adapter.toBean(tokenRepository.find(token), TokenDTO.class);
    }

    @Override
    public UserDTO getAuthenticated(UserDTO user) {
	UserDTO foundDTO = userService.findByMail((user.getMail()));
	return (user.equals(foundDTO) ? foundDTO : null);
    }

    @Override
    @CachePut(value = "token", key = "#token")
    public TokenDTO saveToken(UserDTO user, String token) {
	return adapter.toBean(tokenRepository.save(new Token(user.getId(), token)), TokenDTO.class);
    }

    // cada x minutos eliminaremos los tokens antiguos, forzando a la aplicación a volver a autenticarse
    @Override
    @Scheduled(fixedDelay = AppConst.Security.PURGE_TOKEN_INTERVAL)
    @CacheEvict(value = "token", allEntries = true)
    public void purgeTokens() {
	tokenRepository.purge();
    }

    @Override
    @CacheEvict(value = "token", key = "#token")
    public void removeToken(String token) {
	tokenRepository.purge(token);
    }
}
