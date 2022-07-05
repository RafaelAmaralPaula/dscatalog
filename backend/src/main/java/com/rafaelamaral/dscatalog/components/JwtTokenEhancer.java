package com.rafaelamaral.dscatalog.components;

import com.rafaelamaral.dscatalog.entities.User;
import com.rafaelamaral.dscatalog.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenEhancer implements TokenEnhancer {

    @Autowired
    private UserRepository userRepository;

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken oAuth2AccessToken, OAuth2Authentication oAuth2Authentication) {

        User user = userRepository.findByEmail(oAuth2Authentication.getName());

        Map<String , Object> map = new HashMap<>();
        map.put("userFirstName" , user.getFirstName());
        map.put("userLastName" , user.getLastName());
        map.put("userId" , user.getId());

        DefaultOAuth2AccessToken defaultOAuth2AccessToken = (DefaultOAuth2AccessToken) oAuth2AccessToken;
        defaultOAuth2AccessToken.setAdditionalInformation(map);

        return oAuth2AccessToken;
    }
}
