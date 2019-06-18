package io.paulocosta.twitbooks.security

import com.auth0.spring.security.api.JwtWebSecurityConfigurer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import java.util.*

@Configuration
@EnableWebSecurity(debug = true)
class SecurityConfig : WebSecurityConfigurerAdapter() {

    @Value("\${auth0.api.audience}")
    lateinit var apiAudience: String

    @Value("\${auth0.issuer}")
    lateinit var issuer: String

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val config = CorsConfiguration()
        val source = UrlBasedCorsConfigurationSource()
        config.allowedOrigins = Arrays.asList("http://localhost:3000", "https://twitbooks.io")
        config.allowedMethods = Arrays.asList("GET","POST")
        config.allowCredentials = true
        config.addAllowedHeader("Authorization")
        config.addAllowedHeader("Content-Type")
        source.registerCorsConfiguration("/**", config)
        return source
    }

    override fun configure(http: HttpSecurity) {
        http.cors()
        JwtWebSecurityConfigurer
                .forRS256(apiAudience, issuer)
                .configure(http)
                .authorizeRequests()
                .antMatchers("/api/**").fullyAuthenticated()
                .antMatchers("debug/**").permitAll()

    }

}