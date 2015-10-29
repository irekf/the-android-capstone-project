package com.acpcoursera.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.builders.InMemoryClientDetailsServiceBuilder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

@Configuration
public class OAuth2ServerConfig {

    @Configuration
    @EnableResourceServer
    protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    	public static final String RESOURCE_ID = "data";

        @Override
        public void configure(HttpSecurity http) throws Exception {

            http
                .authorizeRequests()
                .antMatchers("/oauth/token").anonymous();

            http
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/**")
                .access("#oauth2.hasScope('read')");

            http
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/**")
                .access("#oauth2.hasScope('write')");

        }

        @Override
        public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
             resources.resourceId(RESOURCE_ID);
        }

    }

    @Configuration
    @EnableAuthorizationServer
    protected static class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

        @Autowired
        private AuthenticationManager authenticationManager;

        @Autowired
        protected DataSource dataSource;

        @Bean
        public ClientDetailsService clientDetailsService() throws Exception {
            return new InMemoryClientDetailsServiceBuilder()
                    .withClient("mobile").authorizedGrantTypes("password")
                    .authorities("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT")
                    .scopes("read","write")
                    .resourceIds(ResourceServerConfiguration.RESOURCE_ID)
                    .secret("12345")
                    .accessTokenValiditySeconds(3600).and().build();
        }

        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints)
                throws Exception {
            endpoints.authenticationManager(authenticationManager);
        }

        @Override
        public void configure(ClientDetailsServiceConfigurer clients)
                throws Exception {
            clients.withClientDetails(clientDetailsService());
        }

    }

    @Configuration
    @Order(Ordered.LOWEST_PRECEDENCE - 20)
    protected static class AuthenticationManagerConfiguration extends
            GlobalAuthenticationConfigurerAdapter {

        @Autowired
        private DataSource dataSource;

        private JdbcUserDetailsManager userDetailsManager;

        @Bean
        public JdbcUserDetailsManager userDetailsManager() {
            return userDetailsManager;
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }

        @Override
        public void init(AuthenticationManagerBuilder auth) throws Exception {
            this.userDetailsManager = auth.jdbcAuthentication().dataSource(dataSource)
                    .passwordEncoder(passwordEncoder())
                    .usersByUsernameQuery(
                            "SELECT username, password, enabled FROM users WHERE username=?")
                    .authoritiesByUsernameQuery(
                            "SELECT username, authority FROM authorities WHERE username=?")
                    .getUserDetailsService();
        }

    }

}
