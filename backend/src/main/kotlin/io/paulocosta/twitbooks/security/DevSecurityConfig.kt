package io.paulocosta.twitbooks.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity(debug = true)
@Profile("dev")
class DevSecurityConfig : WebSecurityConfigurerAdapter() {

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val config = CorsConfiguration()
        val source = UrlBasedCorsConfigurationSource()
        config.allowedOrigins = listOf("http://localhost:3000", "https://twitbooks.io", "https://www.twitbooks.io")
        config.allowedMethods = listOf("GET", "POST", "PUT")
        config.allowCredentials = true
        config.addAllowedHeader("Authorization")
        config.addAllowedHeader("Content-Type")
        source.registerCorsConfiguration("/**", config)
        return source
    }

    override fun configure(http: HttpSecurity) {
        http.cors()
        http.authorizeRequests()
                .antMatchers("*/**")
                .permitAll()
    }

}
