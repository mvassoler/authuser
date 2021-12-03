package com.ead.authuser.configs.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true) //configura a instância global do authentication manager
@EnableWebSecurity //desliga todos as implementações default do spring security
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    AuthenticationEntryPointImpl authenticationEntryPoint;

    //Define uma lista com os recursos que não necessitam de autenticação
    private static final String[] AUTH_WHITELIST = {
            "/auth/**"
    };

    /*@Override
    public void configure(HttpSecurity http) throws Exception {
        http.httpBasic()
                .authenticationEntryPoint(authenticationEntryPoint)
                .and()
                .authorizeRequests()
                .antMatchers(AUTH_WHITELIST).permitAll() //aplica a permissão total para a lista de recursos
                .antMatchers(HttpMethod.GET, "/users/**").hasRole("STUDENT") //aplica uma permissão específica
                .anyRequest().authenticated() //listar os endpoints com autorizações especificas ou endpoints que podem ser acessados sem autenticação
                .and()
                .csrf().disable(); //csrf: falsificação de solicitação entre site é habilitado por default, mas clientes eureka não possuem token válidos

    }*/
    public void configure(HttpSecurity http) throws Exception {
        http.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint)
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) //definição da politica de sessão
                .and()
                .authorizeRequests()
                .antMatchers(AUTH_WHITELIST).permitAll() //aplica a permissão total para a lista de recursos
                .anyRequest().authenticated() //listar os endpoints com autorizações especificas ou endpoints que podem ser acessados sem autenticação
                .and()
                .csrf().disable(); //csrf: falsificação de solicitação entre site é habilitado por default, mas clientes eureka não possuem token válidos
        http.addFilterBefore(authenticationJwtFilter(), UsernamePasswordAuthenticationFilter.class);
    }

  /*  @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()  //do tipo em memória
                .withUser("admin")  //usuario
                .password(passwordEncoder().encode("123456"))  //senha
                .roles("ADMIN"); //regra
    }*/

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    //Procede o encode da senha
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception{
        return super.authenticationManagerBean();
    }

    @Bean
    public AuthenticationJwtFilter authenticationJwtFilter(){
        return new AuthenticationJwtFilter();
    }

}
