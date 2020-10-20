package net.saamankhali.portfoliosite.controllers

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter

@Configuration
class SiteSecurityConfig : WebSecurityConfigurerAdapter()
{
    override fun configure(http: HttpSecurity?) {
        if(http != null)
        {
            http
                .authorizeRequests()
                    .antMatchers("/admin/**").authenticated()
                    .anyRequest().permitAll()
                .and()
                    .httpBasic()
                .and()
                    .formLogin().disable()
                    .csrf().disable()
        }
    }
}