package gew.server.websocket.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;


@Configuration
public class WebSocketSecurityConfig extends WebSecurityConfigurerAdapter
{

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception
    {
        auth
            // Defines three users with their passwords and roles
            .inMemoryAuthentication()
            .withUser("User1").password("Password").roles("USER")
            .and()
            .withUser("User2").password("Password").roles("USER")
            .and()
            .withUser("Jason").password("PASSWORD").roles("USER");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception
    {
        http
            // Disable CSRF protection
            .csrf().disable()
            // Set default configurations from Spring Security
            .authorizeRequests()
            .anyRequest().authenticated()
            .and()
            .formLogin()
            .and()
            .httpBasic();
    }
}
