package com.dws.ActualRetro;

import com.dws.ActualRetro.jwt.AuthEntryPointJwt;
import com.dws.ActualRetro.jwt.AuthTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.security.SecureRandom;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Order(1)
public class RestSecurityConf extends WebSecurityConfigurerAdapter {
    @Autowired
    UserService userService;
    @Autowired
    RepositoryUserDetailService  userDetailService;
    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
    //@Bean ya introducida en SecurityConfiguration
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10, new SecureRandom());
    }
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(passwordEncoder());
    }
    @Override
    protected void configure(HttpSecurity http) throws Exception{
        http.antMatcher("/api/**");
        http.exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests().antMatchers(HttpMethod.POST,"/api/auth/**").permitAll();
        http.authorizeRequests().antMatchers(HttpMethod.POST, "/api/products/consoles/**").hasRole("USER");
        http.authorizeRequests().antMatchers(HttpMethod.PUT, "/api/products/consoles/**").hasRole("USER");
        http.authorizeRequests().antMatchers(HttpMethod.DELETE, "/api/products/consoles/**").hasAnyRole("ADMIN", "USER");
        http.authorizeRequests().antMatchers(HttpMethod.POST, "/api/products/videogames/**").hasRole("USER");
        http.authorizeRequests().antMatchers(HttpMethod.PUT, "/api/products/videogames/**").hasRole("USER");
        http.authorizeRequests().antMatchers(HttpMethod.DELETE, "/api/products/videogames/**").hasAnyRole("ADMIN", "USER");
        //http.authorizeRequests().antMatchers(HttpMethod.POST, "/api/auth/signup", "/api/auth/login").permitAll();
        http.csrf().disable();
        http.authorizeRequests().anyRequest().permitAll();
        http.httpBasic();
        http.formLogin().disable();
        //http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        //http.exceptionHandling().authenticationEntryPoint(unauthorizedHandler);
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
    }
}

