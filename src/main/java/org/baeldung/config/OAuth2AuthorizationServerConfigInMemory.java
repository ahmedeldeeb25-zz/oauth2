package org.baeldung.config;

import java.util.Arrays;

import javax.sql.DataSource;

import org.baeldung.config.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
 
 

@Configuration
 
@EnableAuthorizationServer
public class OAuth2AuthorizationServerConfigInMemory extends AuthorizationServerConfigurerAdapter {

    

    @Autowired
    @Qualifier("authenticationManagerBean")
    private AuthenticationManager authenticationManager;
	@Autowired
	private TokenStore tokenStore;
	
	@Autowired
	private CustomUserDetailsService userDetailsService;
	
	
	@Bean
	public WebResponseExceptionTranslator loggingExceptionTranslator() {
		return new DefaultWebResponseExceptionTranslator() {
			@Override
			public ResponseEntity<OAuth2Exception> translate(Exception e) throws Exception {
				// This is the line that prints the stack trace to the log. You can customise
				// this to format the trace etc if you like
				e.printStackTrace();

				// Carry on handling the exception
				ResponseEntity<OAuth2Exception> responseEntity = super.translate(e);
				HttpHeaders headers = new HttpHeaders();
				headers.setAll(responseEntity.getHeaders().toSingleValueMap());
				OAuth2Exception excBody = responseEntity.getBody();
				return new ResponseEntity<>(excBody, headers, responseEntity.getStatusCode());
			}
		};
	}

     

    @Override
    public void configure(final AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
        oauthServer.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");
    }

    @Override
    public void configure(final ClientDetailsServiceConfigurer clients) throws Exception {// @formatter:off
		clients
				.inMemory().withClient("sampleClientId").authorizedGrantTypes("implicit")
				.scopes("read", "write", "foo", "bar").autoApprove(false).accessTokenValiditySeconds(3600)

				.and().withClient("fooClientIdPassword").secret("secret")
				.authorizedGrantTypes("password", "authorization_code", "refresh_token").scopes("foo", "read", "write")
				.accessTokenValiditySeconds(3600) // 1 hour
				.refreshTokenValiditySeconds(2592000) // 30 days

				.and().withClient("barClientIdPassword").secret("secret")
				.authorizedGrantTypes("password", "authorization_code", "refresh_token").scopes("bar", "read", "write")
				.accessTokenValiditySeconds(3600) // 1 hour
				.refreshTokenValiditySeconds(2592000) // 30 days
		;
	} // @formatter:on

    @Override
    public void configure(final AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        // @formatter:off
		final TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
		
		tokenEnhancerChain.setTokenEnhancers(Arrays.asList(tokenEnhancer()));
		endpoints.tokenStore(tokenStore())
		.exceptionTranslator(loggingExceptionTranslator())
				// .accessTokenConverter(accessTokenConverter())
				.tokenEnhancer(tokenEnhancerChain)
				.userDetailsService(userDetailsService)
				.authenticationManager(authenticationManager);
		// @formatter:on
    }
     
    @Bean
	@Primary
	public DefaultTokenServices tokenServices() {
		DefaultTokenServices tokenServices = new DefaultTokenServices();
		tokenServices.setSupportRefreshToken(true);
		tokenServices.setTokenStore(this.tokenStore);
		return tokenServices;
	}

    @Bean
    public TokenEnhancer tokenEnhancer() {
        return new CustomTokenEnhancer();
    }
    
    @Bean
	public TokenStore tokenStore() {
		return new InMemoryTokenStore();
	}


    
    

    

    

}
