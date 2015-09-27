package com.acpcoursera;

import java.io.File;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.acpcoursera.config.OAuth2ServerConfig;
import com.acpcoursera.config.SecurityConfiguration;

/* Automatically inject any dependencies marked with @Autowired */
@EnableAutoConfiguration

/*
 * Enable the DispatcherServlet so that requests can be routed to our
 * Controllers
 */
@EnableWebMvc

/* Scan a package for controllers */
@ComponentScan("com.acpcoursera.controllers")

@Import({SecurityConfiguration.class, OAuth2ServerConfig.class})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /* Use HTTPS in Tomcat */
    @Bean
    EmbeddedServletContainerCustomizer containerCustomizer(@Value("${keystore.file}") String keystoreFile,
            @Value("${keystore.pass}") final String keystorePass) throws Exception {

        final String absoluteKeystoreFile = new File(keystoreFile).getAbsolutePath();

        EmbeddedServletContainerCustomizer customizer = new EmbeddedServletContainerCustomizer() {

            @Override
            public void customize(ConfigurableEmbeddedServletContainer container) {

                TomcatEmbeddedServletContainerFactory tomcat = (TomcatEmbeddedServletContainerFactory) container;

                tomcat.addConnectorCustomizers(new TomcatConnectorCustomizer() {

                    @Override
                    public void customize(Connector connector) {
                        connector.setPort(8443);
                        connector.setSecure(true);
                        connector.setScheme("https");

                        Http11NioProtocol proto = (Http11NioProtocol) connector.getProtocolHandler();
                        proto.setSSLEnabled(true);

                        proto.setKeystoreFile(absoluteKeystoreFile);
                        proto.setKeystorePass(keystorePass);
                        proto.setKeystoreType("JKS");
                        proto.setKeyAlias("tomcat");

                    }
                });

            }

        };

        return customizer;
    }

}