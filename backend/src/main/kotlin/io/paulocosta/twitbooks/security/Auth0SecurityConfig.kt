package io.paulocosta.twitbooks.security

import com.auth0.spring.security.api.JwtWebSecurityConfigurer
import org.springframework.beans.factory.annotation.Value
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
@EnableWebSecurity(debug = false)
@Profile("prod")
class Auth0SecurityConfig : WebSecurityConfigurerAdapter() {

    @Value("\${auth0.app.client.id}")
    lateinit var appClientId: String

    @Value("\${auth0.audience}")
    lateinit var audience: String

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
        JwtWebSecurityConfigurer
                .forRS256(appClientId, audience)
                .configure(http)
                .authorizeRequests()
                .antMatchers("/api/**").fullyAuthenticated()
                .antMatchers("debug/**").permitAll()

    }

}
