package com.zserg.notepad.security


import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator
import org.springframework.security.oauth2.core.OAuth2TokenValidator
import org.springframework.security.oauth2.jwt.*
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
open class SecurityConfig {
    @Value("\${allowed.origins}")
    private lateinit var allowedOrigins: String

    @Value("\${auth0.audience}")
    private val audience: String = String()

    @Value("\${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private val issuer: String = String()

    @Bean
    fun jwtDecoder(): JwtDecoder {
        val jwtDecoder = JwtDecoders.fromOidcIssuerLocation(issuer) as NimbusJwtDecoder
        val audienceValidator: OAuth2TokenValidator<Jwt> = AudienceValidator(audience)
        val withIssuer: OAuth2TokenValidator<Jwt> = JwtValidators.createDefaultWithIssuer(issuer)
        val withAudience: OAuth2TokenValidator<Jwt> = DelegatingOAuth2TokenValidator(withIssuer, audienceValidator)
        jwtDecoder.setJwtValidator(withAudience)
        return jwtDecoder
    }
    @Bean
    open fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            cors { }
            authorizeRequests {
                authorize(anyRequest, authenticated)
            }
            oauth2ResourceServer {
                jwt { }
            }
        }
        return http.build()
    }
    @Bean
    open fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = allowedOrigins.split(',')
        configuration.allowedMethods = listOf("GET", "POST")
        // NOTE: setAllowCredentials(true) is important,
        // otherwise, the value of the 'Access-Control-Allow-Origin' header in the response
        // must not be the wildcard '*' when the request's credentials mode is 'include'.
        configuration.allowCredentials = true

        // NOTE: setAllowedHeaders is important!
        // Without it, OPTIONS preflight request will fail with 403 Invalid CORS request
        configuration.allowedHeaders = listOf(
            "Authorization",
            "Accept",
            "Cache-Control",
            "Content-Type",
            "Origin",
            "x-csrf-token",
            "x-requested-with"
        )
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

    @Bean
    fun jwtAuthenticationConverter(): JwtAuthenticationConverter? {
        val converter = JwtGrantedAuthoritiesConverter()
        converter.setAuthoritiesClaimName("permissions")
        converter.setAuthorityPrefix("")
        val jwtConverter = JwtAuthenticationConverter()
        jwtConverter.setJwtGrantedAuthoritiesConverter(converter)
        return jwtConverter
    }

}