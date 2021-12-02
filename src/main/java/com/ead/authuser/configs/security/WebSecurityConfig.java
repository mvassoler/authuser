package com.ead.authuser.configs.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true) //configura a instância global do authentication manager
@EnableWebSecurity //desliga todos as implementações default do spring security
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    //Define uma lista com os recursos que não necessitam de autenticação
    private static final String[] AUTH_WHITELIST = {
            "/ead-authuser/auth/**"
    };

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.httpBasic()
                .and()
                .authorizeRequests()
                .antMatchers(AUTH_WHITELIST).permitAll() //aplica a permissão total para a lista de recursos
                .anyRequest().authenticated() //listar os endpoints com autorizações especificas ou endpoints que podem ser acessados sem autenticação
                .and()
                .csrf().disable() //csrf: falsiciação de solicitação entre site é habilitado por default, mas clientes eureka não possuem token válidos
                .formLogin(); //obriga a exibição do formulário de autenticação no dashboard
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()  //do tipo em memória
                .withUser("admin")  //usuario
                .password(passwordEncoder().encode("123456"))  //senha
                .roles("ADMIN"); //regra
    }

    //Procede o encode da senha
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


}
