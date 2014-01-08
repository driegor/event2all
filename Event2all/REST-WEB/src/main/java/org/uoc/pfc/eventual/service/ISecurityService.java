package org.uoc.pfc.eventual.service;

import org.uoc.pfc.eventual.utils.integration.dto.UserDTO;
import org.uoc.pfc.eventual.utils.integration.security.TokenDTO;

public interface ISecurityService {

    TokenDTO findToken(String token);

    UserDTO getAuthenticated(UserDTO user);

    TokenDTO saveToken(UserDTO user, String token);

    void purgeTokens();

    void removeToken(String token);

}
