package com.dws.ActualRetro;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.SecureRandom;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Order(2)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    UserService userDetailsService;


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10, new SecureRandom());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
// Public pages
        http.authorizeRequests().antMatchers("/", "/js/*", "/css/*").permitAll();
        http.authorizeRequests().antMatchers("/login").permitAll();
        http.authorizeRequests().antMatchers("/loginerror").permitAll();
        http.authorizeRequests().antMatchers("/logout").permitAll();
        http.authorizeRequests().antMatchers("/products/consoles").permitAll();
        http.authorizeRequests().antMatchers("/products/videogames").permitAll();
        http.authorizeRequests().antMatchers("/register").permitAll();
        http.authorizeRequests().antMatchers(HttpMethod.POST,"/api/auth/signup").permitAll();
        http.authorizeRequests().antMatchers(HttpMethod.POST,"/api/auth/login").permitAll();
// Private pages (all other pages)
        http.authorizeRequests().anyRequest().authenticated();
// Login form
        http.formLogin().loginPage("/login");
        http.formLogin().usernameParameter("username");
        http.formLogin().passwordParameter("password");
        http.formLogin().defaultSuccessUrl("/");
        http.formLogin().failureUrl("/loginerror");
// Logout
        http.logout().logoutUrl("/logout");
        http.logout().logoutSuccessUrl("/");
        //http.csrf().disable();

    }
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        auth.inMemoryAuthentication().withUser("admin").password(encoder.encode("adminpass")).roles("USER","ADMIN");
}



}