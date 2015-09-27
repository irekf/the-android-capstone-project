package com.acpcoursera.config;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.builders.InMemoryClientDetailsServiceBuilder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

@Configuration
public class OAuth2ServerConfig {

    @Configuration
    @EnableResourceServer
    protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

        @Override
        public void configure(HttpSecurity http) throws Exception {

            http
                .authorizeRequests()
                .antMatchers("/oauth/token").anonymous();

            http
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/**")
                .access("#oauth2.hasScope('read')")
            .and()
                .authorizeRequests()
                .antMatchers("/**")
                .access("#oauth2.hasScope('write')");

        }

    }

    @Configuration
    @EnableAuthorizationServer
    @Order(Ordered.LOWEST_PRECEDENCE - 100)
    protected static class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

        private ProviderManager authenticationManager;

        @Autowired
        protected DataSource dataSource;

        private JdbcUserDetailsManager userDetailsManager;

        @Autowired
        public void configAuthentication(AuthenticationManagerBuilder auth) throws Exception {
            this.userDetailsManager = auth.jdbcAuthentication().dataSource(dataSource)
                .withDefaultSchema().getUserDetailsService();
        }

        public AuthorizationServerConfiguration() throws Exception {

            DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
            // TODO: password encoding and salt

            List<AuthenticationProvider> providers = new ArrayList<>();
            providers.add(provider);

            authenticationManager = new ProviderManager(providers);

        }

        @Bean
        public ClientDetailsService clientDetailsService() throws Exception {
            return new InMemoryClientDetailsServiceBuilder()
                    .withClient("mobile").authorizedGrantTypes("password")
                    .authorities("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT")
                    .scopes("read","write").resourceIds("data")
                    .accessTokenValiditySeconds(3600).and().build();
        }

        @Bean(name = "userDetailsManager")
        public JdbcUserDetailsManager userDetailsManager() {
            return userDetailsManager;
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

}
