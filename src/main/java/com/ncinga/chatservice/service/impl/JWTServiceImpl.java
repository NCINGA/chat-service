package com.ncinga.chatservice.service.impl;
import com.ncinga.chatservice.service.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;

@Service
public class JWTServiceImpl implements JWTService {

    private final OAuth2AuthorizedClientManager authorizedClientManager;

    @Autowired
    public JWTServiceImpl(ClientRegistrationRepository clientRegistrationRepository) {
        OAuth2AuthorizedClientProvider authorizedClientProvider = OAuth2AuthorizedClientProviderBuilder.builder()
                .clientCredentials()
                .build();

        OAuth2AuthorizedClientRepository authorizedClientRepository = new HttpSessionOAuth2AuthorizedClientRepository();

        DefaultOAuth2AuthorizedClientManager authorizedClientManager = new DefaultOAuth2AuthorizedClientManager(
                clientRegistrationRepository, authorizedClientRepository);

        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);
        this.authorizedClientManager = authorizedClientManager;
    }

    public String generateJwtToken() {
        OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest
                .withClientRegistrationId("azure")
                .principal("Microsoft Graph API")
                .build();

        OAuth2AccessToken accessToken = this.authorizedClientManager
                .authorize(authorizeRequest)
                .getAccessToken();

        return accessToken.getTokenValue();
    }
}
