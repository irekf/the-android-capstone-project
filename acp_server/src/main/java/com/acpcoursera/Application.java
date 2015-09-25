package com.acpcoursera;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/* Automatically inject any dependencies marked with @Autowired */
@EnableAutoConfiguration

/*
 * Enable the DispatcherServlet so that requests can be routed to our
 * Controllers
 */
@EnableWebMvc

/* Scan a package for controllers */
@ComponentScan("com.acpcoursera.controllers")

public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
