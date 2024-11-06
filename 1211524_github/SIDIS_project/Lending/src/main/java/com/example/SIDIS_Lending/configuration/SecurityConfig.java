/*
 * Copyright (c) 2022-2024 the original author or authors.
 *
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.example.SIDIS_Lending.configuration;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.project.psoft.usermanagement.model.Role;
import com.project.psoft.usermanagement.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import static java.lang.String.format;

/**
 * Check https://www.baeldung.com/security-spring and
 * https://www.toptal.com/spring/spring-security-tutorial
 * <p>
 * Based on https://github.com/Yoh0xFF/java-spring-security-example/
 *
 * @author pagsousa
 *
 */
@EnableWebSecurity
@Configuration
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
@EnableConfigurationProperties
@RequiredArgsConstructor
public class SecurityConfig {

	private final UserRepository userRepo;

	@Value("${jwt.public.key}")
	private RSAPublicKey rsaPublicKey;

	@Value("${jwt.private.key}")
	private RSAPrivateKey rsaPrivateKey;

	@Value("${springdoc.api-docs.path}")
	private String restApiDocPath;

	@Value("${springdoc.swagger-ui.path}")
	private String swaggerPath;

	@Bean
	public AuthenticationManager authenticationManager(final UserDetailsService userDetailsService,
													   final PasswordEncoder passwordEncoder) {
		final DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsService);
		authenticationProvider.setPasswordEncoder(passwordEncoder);

		return new ProviderManager(authenticationProvider);
	}

	@Bean
	public UserDetailsService userDetailsService() {
		return username -> userRepo.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException(format("User: %s, not found", username)));
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		// Enable CORS and disable CSRF
		http = http.cors(Customizer.withDefaults()).csrf(csrf -> csrf.disable());

		// Set session management to stateless
		http = http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		// Set unauthorized requests exception handler
		http = http.exceptionHandling(
				exceptions -> exceptions.authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())
						.accessDeniedHandler(new BearerTokenAccessDeniedHandler()));

		// Set permissions on endpoints
		http.authorizeHttpRequests()
				// Swagger endpoints must be publicly accessible
				.requestMatchers("/").permitAll().requestMatchers(format("%s/**", restApiDocPath)).permitAll()
				.requestMatchers(format("%s/**", swaggerPath)).permitAll()
				// Our public endpoints
				.requestMatchers("/api/login").permitAll() // login as reader, librarian or admin
				.requestMatchers("/api/register").permitAll()
				.requestMatchers("/api/registerReader").permitAll()// register as reader
				//.requestMatchers("/api/librarian/**").permitAll() // read-only librarian
				//.requestMatchers("/api/reader/**").permitAll() // read-only reader
				.requestMatchers(HttpMethod.GET,"/api/book/topgenres").hasRole(Role.LIBRARIAN)
				.requestMatchers(HttpMethod.GET,"/api/book/topbooks").hasRole(Role.LIBRARIAN)
				.requestMatchers(HttpMethod.GET,"/api/book").permitAll()
				.requestMatchers(HttpMethod.GET,"/api/book/{isbn}").permitAll()
				.requestMatchers(HttpMethod.GET,"/api/book/{isbn}/cover").permitAll()
				.requestMatchers(HttpMethod.GET,"/api/book/search").permitAll()

				// Our private endpoints
				.requestMatchers("/api/admin/user/**").hasRole(Role.ADMIN) // user management

				//Authors
				.requestMatchers(HttpMethod.GET, "/api/authors/books/{authorName}").hasRole(Role.READER)
				.requestMatchers(HttpMethod.GET, "/api/authors/TopAuthors").hasRole(Role.READER)
				.requestMatchers(HttpMethod.GET, "/api/authors/coAuthors/{authorName}").hasRole(Role.READER)
				.requestMatchers(HttpMethod.POST,"/api/authors").hasRole(Role.LIBRARIAN) // library management
				.requestMatchers(HttpMethod.PUT,"/api/authors/{id}").hasRole(Role.LIBRARIAN) // library management
				.requestMatchers(HttpMethod.GET, "/api/authors/{id}").hasAnyRole("LIBRARIAN", "READER")
				.requestMatchers(HttpMethod.GET,"/api/authors").hasAnyRole("LIBRARIAN", "READER")
				.requestMatchers(HttpMethod.GET,"/api/authors/{id}/authorPicture").hasRole(Role.LIBRARIAN)
				.requestMatchers(HttpMethod.GET, "/api/authors/top-readers-per-genre").hasRole(Role.LIBRARIAN)
				.requestMatchers(HttpMethod.GET, "/api/reader/search/email").hasRole(Role.LIBRARIAN)

				//Readers
				.requestMatchers(HttpMethod.GET,"/api/reader/getTopReaders").hasRole(Role.LIBRARIAN)
				.requestMatchers(HttpMethod.GET,"/api/reader/search/**").hasRole(Role.LIBRARIAN)
				.requestMatchers(HttpMethod.GET,"/api/reader/getBookSuggestions").hasRole(Role.READER)
				.requestMatchers(HttpMethod.GET,"/api/reader/{year}/{id}/profile").hasRole(Role.LIBRARIAN)
				.requestMatchers(HttpMethod.GET,"/api/reader/{year}/{id}/profilePicture").hasRole(Role.LIBRARIAN)
				.requestMatchers(HttpMethod.PUT,"/api/reader/updateReader").hasRole(Role.READER)

				// book management
				.requestMatchers(HttpMethod.PUT,"/api/book/{isbn}").hasRole(Role.LIBRARIAN)
				.requestMatchers(HttpMethod.PATCH,"/api/book/{isbn}").hasRole(Role.LIBRARIAN)

				//Lendings
				.requestMatchers("/api/lending").hasRole(Role.LIBRARIAN)
				.requestMatchers("/api/lending/report").hasRole(Role.LIBRARIAN)
				.requestMatchers("/api/lending/getMonthlyLendingPerReader").hasRole(Role.LIBRARIAN)
				.requestMatchers(HttpMethod.PATCH, "/api/lending/{isbnOrlYear}/**").hasRole(Role.READER)
				.requestMatchers(HttpMethod.GET, "/api/lending/{isbnOrlYear}/**")
				.hasAnyRole(Role.LIBRARIAN, Role.READER)

				.anyRequest().authenticated()
				// Set up oauth2 resource server
				.and().httpBasic(Customizer.withDefaults()).oauth2ResourceServer().jwt();

		return http.build();
	}

	// Used by JwtAuthenticationProvider to generate JWT tokens
	@Bean
	public JwtEncoder jwtEncoder() {
		final JWK jwk = new RSAKey.Builder(this.rsaPublicKey).privateKey(this.rsaPrivateKey).build();
		final JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
		return new NimbusJwtEncoder(jwks);
	}

	// Used by JwtAuthenticationProvider to decode and validate JWT tokens
	@Bean
	public JwtDecoder jwtDecoder() {
		return NimbusJwtDecoder.withPublicKey(this.rsaPublicKey).build();
	}

	// Extract authorities from the roles claim
	@Bean
	public JwtAuthenticationConverter jwtAuthenticationConverter() {
		final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
		jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName("roles");
		jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

		final JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
		jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
		return jwtAuthenticationConverter;
	}

	// Set password encoding schema
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	// Used by spring security if CORS is enabled.
	@Bean
	public CorsFilter corsFilter() {
		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		final CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);
		config.addAllowedOrigin("*");
		config.addAllowedHeader("*");
		config.addAllowedMethod("*");
		source.registerCorsConfiguration("/**", config);
		return new CorsFilter(source);
	}

}
