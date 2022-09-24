package com.example.cloudservice.security;

import com.example.cloudservice.repository.UserRepository;
import com.example.cloudservice.service.LoggedOutTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    public static final String AUTH_TOKEN = "auth-token";

    private final UserRepository userRepository;
    private final MessageSourceAccessor messages;
    private final KeyProvider keyProvider;
    private final LoggedOutTokenService loggedOutTokenService;

    @Value("${app.security.cors.allowed-origins}")
    private List<String> allowedOrigins;
    @Value("${app.security.cors.allowed-methods}")
    private List<String> allowedMethods;
    @Value("${app.security.cors.allowed-headers}")
    private List<String> allowedHeaders;
    @Value("${app.security.cors.allow-credentials}")
    private boolean allowCredentials;
    @Value("${app.security.jwt.signature-algorithm}")
    private String algorithm;
    @Value("${app.security.jwt.issuer}")
    private String issuer;

    public SecurityConfig(UserRepository userRepository,
                          MessageSourceAccessor messages,
                          KeyProvider keyProvider,
                          LoggedOutTokenService loggedOutTokenService) {
        this.userRepository = userRepository;
        this.messages = messages;
        this.keyProvider = keyProvider;
        this.loggedOutTokenService = loggedOutTokenService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors()
                .and()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests(configurer ->
                        configurer
                                .antMatchers("/login")
                                .permitAll()
                                .anyRequest()
                                .authenticated()
                )
                .logout()
                .addLogoutHandler(new TokenLogoutHandler(loggedOutTokenService))
                .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK))
                .and()
                .oauth2ResourceServer()
                .authenticationEntryPoint(new MessagingAuthenticationEntryPoint(messages))
                .jwt();
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findById(username)
                .orElseThrow(() -> new UsernameNotFoundException(messages.getMessage("user.not.found")));
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(allowedMethods);
        configuration.setAllowedHeaders(allowedHeaders);
        configuration.setAllowCredentials(allowCredentials);
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    KeyPair keyPair() {
        return keyProvider.provideKeys();
    }

    @Bean
    JwtDecoder jwtDecoder(KeyPair keyPair, LoggedOutTokenValidator loggedOutTokenValidator) {
        final NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder
                .withPublicKey((RSAPublicKey) keyPair.getPublic())
                .signatureAlgorithm(SignatureAlgorithm.from(algorithm))
                .build();

        final OAuth2TokenValidator<Jwt> customValidator = new DelegatingOAuth2TokenValidator<>(
                JwtValidators.createDefaultWithIssuer(issuer),
                loggedOutTokenValidator);

        jwtDecoder.setJwtValidator(customValidator);
        return jwtDecoder;
    }

    @Bean
    BearerTokenResolver bearerTokenResolver() {
        final DefaultBearerTokenResolver bearerTokenResolver = new DefaultBearerTokenResolver();
        bearerTokenResolver.setBearerTokenHeaderName(AUTH_TOKEN);
        return bearerTokenResolver;
    }
}
