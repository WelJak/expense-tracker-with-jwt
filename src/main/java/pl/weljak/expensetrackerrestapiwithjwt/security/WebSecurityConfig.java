package pl.weljak.expensetrackerrestapiwithjwt.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import pl.weljak.expensetrackerrestapiwithjwt.domain.user.PostgresEtUserRepository;
import pl.weljak.expensetrackerrestapiwithjwt.security.jwt.JwtAuthTokenFilter;
import pl.weljak.expensetrackerrestapiwithjwt.security.jwt.JwtAuthEntryPoint;
import pl.weljak.expensetrackerrestapiwithjwt.security.jwt.JwtTokenProvider;
import pl.weljak.expensetrackerrestapiwithjwt.security.userdetails.PostgresUserDetailsService;
import pl.weljak.expensetrackerrestapiwithjwt.utils.Endpoints;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        prePostEnabled = true
)
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final PostgresEtUserRepository postgresEtUserRepository;
    private final JwtAuthEntryPoint jwtAuthEntryPoint;
    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public JwtAuthTokenFilter authenticationJwtTokenFilter(){
        return new JwtAuthTokenFilter(userDetailsService(), jwtTokenProvider);
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(){
        return new PostgresUserDetailsService(postgresEtUserRepository);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService()).passwordEncoder(bCryptPasswordEncoder());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .authorizeRequests()
                .antMatchers(Endpoints.AUTH_LOGIN_ENDPOINT).permitAll()
                .antMatchers(Endpoints.AUTH_REGISTER_ENDPOINT).permitAll()
                .anyRequest().authenticated()
                .and()
                .exceptionHandling().authenticationEntryPoint(jwtAuthEntryPoint)
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
    }
}
