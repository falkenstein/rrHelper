package com.falkenstein.rrweb.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SpringBootSecurityConfiguration {

    private val basicPassword = "rrPass"

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .authorizeHttpRequests { it
                .requestMatchers("/interactive", "/slash", "/webhook/**", "/health").permitAll()
                .anyRequest().authenticated() }
            .httpBasic { }
            .cors { it.configurationSource(corsConfigurationSource()) }
        return http.build()
    }

    @Bean
    fun userDetailsService(): UserDetailsService {
        val userDetails = User
            .withUsername("rr")
            .password(passwordEncoder().encode(basicPassword))
            .authorities("USER")
            .build()
        return InMemoryUserDetailsManager(userDetails)
    }

    @Bean
    fun authenticationProvider(): DaoAuthenticationProvider {
        val authProvider = DaoAuthenticationProvider()
        authProvider.setUserDetailsService(userDetailsService())
        authProvider.setPasswordEncoder(passwordEncoder())
        return authProvider
    }

    @Bean
    @Throws(Exception::class)
    fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration): AuthenticationManager {
        return authenticationConfiguration.authenticationManager
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    private fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.addAllowedOrigin(CorsConfiguration.ALL)
        configuration.addAllowedMethod(CorsConfiguration.ALL)
        configuration.addAllowedHeader(CorsConfiguration.ALL)
        val configSource = UrlBasedCorsConfigurationSource()
        configSource.registerCorsConfiguration("/**", configuration)
        return configSource
    }
}
