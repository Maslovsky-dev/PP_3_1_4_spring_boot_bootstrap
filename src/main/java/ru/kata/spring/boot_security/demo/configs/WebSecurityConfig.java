package ru.kata.spring.boot_security.demo.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.kata.spring.boot_security.demo.service.UserDetailsService;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final SuccessUserHandler successUserHandler;
    private final UserDetailsService userDetailsService;


    @Autowired
    public WebSecurityConfig(SuccessUserHandler successUserHandler, UserDetailsService userDetailsService) {
        this.successUserHandler = successUserHandler;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void configure (HttpSecurity http) throws Exception {
        //Конфигурируем сам Spring Security
        //Конфигурируем авторизацию
        http.authorizeRequests()
                .antMatchers("/admin").hasRole("ADMIN")
                .antMatchers("/auth/login","/auth/registration","/error").permitAll()
                .anyRequest().hasAnyRole("USER","ADMIN")
                .and()
                .formLogin().loginPage("/auth/login").successHandler(successUserHandler)
                .loginProcessingUrl("/process_login")
//                .defaultSuccessUrl("/hello", true)
                .failureUrl("/auth/login?error")
                .and()
                .logout().logoutUrl("/logout").logoutSuccessUrl("/auth/login");
    }
    //настраивает аутентификацию
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(getPasswordEncoder());
    }
    @Bean
    public PasswordEncoder getPasswordEncoder() {

        return new BCryptPasswordEncoder();
    }
}