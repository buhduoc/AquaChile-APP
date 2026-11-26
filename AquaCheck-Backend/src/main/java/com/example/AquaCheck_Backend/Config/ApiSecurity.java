package com.example.AquaCheck_Backend.Config;
import com.example.AquaCheck_Backend.Repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.http.HttpMethod;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import javax.crypto.spec.SecretKeySpec;
import java.util.*;
import java.time.Instant;
import java.io.IOException;
@Configuration
public class ApiSecurity {
 private final SecretKeySpec key;
 public ApiSecurity(@Value("${AQUA_JWT_SECRET}") String secret) {
  byte[] bytes=Base64.getDecoder().decode(secret);
  if(bytes.length<32) throw new IllegalArgumentException("JWT secret must contain 32 bytes");
  key=new SecretKeySpec(bytes,"HmacSHA256");
 }
 public String token(String usuario) {
  Instant now=Instant.now();
  var claims=JwtClaimsSet.builder().issuer("aquacheck").subject(usuario).issuedAt(now).expiresAt(now.plusSeconds(86400)).build();
  return new NimbusJwtEncoder(new com.nimbusds.jose.jwk.source.ImmutableSecret<>(key)).encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(),claims)).getTokenValue();
 }
 @Bean SecurityFilterChain api(HttpSecurity http, UsuarioRepository repo) throws Exception {
  var decoder=NimbusJwtDecoder.withSecretKey(key).macAlgorithm(MacAlgorithm.HS256).build();
  decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer("aquacheck"));
  var filter=new OncePerRequestFilter(){
   @Override protected void doFilterInternal(HttpServletRequest req,HttpServletResponse res,FilterChain chain) throws ServletException,IOException {
    String h=req.getHeader("Authorization");
    if(h!=null && h.startsWith("Bearer ")) {
     try {
      var jwt=decoder.decode(h.substring(7));
      var u=repo.findByUsuarioIgnoreCase(jwt.getSubject()).orElseThrow();
      if(Boolean.TRUE.equals(u.getAprobada())) {
       String role="Supervisor".equalsIgnoreCase(u.getRol())?"SUPERVISOR":"BUZO";
       SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(u.getUsuario(),null,List.of(new SimpleGrantedAuthority("ROLE_"+role))));
      }
     } catch(JwtException | NoSuchElementException ex) { SecurityContextHolder.clearContext(); }
    }
    chain.doFilter(req,res);
   }
  };
  return http.csrf(c->c.disable()).sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
          .exceptionHandling(e -> e
                  .authenticationEntryPoint((q, p, x) -> p.setStatus(401))
                  .accessDeniedHandler((q, p, x) -> p.setStatus(403)))
   .authorizeHttpRequests(a->a.requestMatchers("/api/auth/login","/swagger-ui/**","/swagger-ui.html","/v3/api-docs/**").permitAll()
    .requestMatchers(HttpMethod.POST,"/api/usuarios").permitAll()
    .requestMatchers("/api/usuarios/**").hasRole("SUPERVISOR")
    .requestMatchers(HttpMethod.DELETE,"/api/**").hasRole("SUPERVISOR")
    .requestMatchers(HttpMethod.PATCH,"/api/**").hasRole("SUPERVISOR")
    .requestMatchers("/api/**").hasAnyRole("BUZO","SUPERVISOR").anyRequest().denyAll())
   .addFilterBefore(filter,UsernamePasswordAuthenticationFilter.class).build();
 }
}
