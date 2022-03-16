package com.project.conf;


import com.project.service.MemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    static Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    public SecurityConfig()
    {
        SecurityConfig.logger.debug("SecurityConfig");
    }

    @Autowired
    private MemberService memberService;

    @Autowired
   private DataSource dataSource;

    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();

    }

    @Override
    protected void configure(HttpSecurity http) throws Exception
    {

//     인증과 권한
        http
                .authorizeRequests()
                .antMatchers("/user/**").authenticated()
                .antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")
                .anyRequest().permitAll()
                .and()
//     폼 로그인 설정
                .formLogin()
                .loginPage("/Login")
                .loginProcessingUrl("/doLogin")
                .defaultSuccessUrl("/", true)
                .permitAll()
                .and()
//     로그아웃 설정
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/Logout"))
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID", "remember-me")
                .and()
//     remember me 설정
                .rememberMe()
                .key("myWeb")
                .rememberMeParameter("remember-me")
                .tokenValiditySeconds(86400) //1day
                .and()
//     exceptionHandling
                .exceptionHandling()
                .accessDeniedPage("/denied")
                .and()
//     session 관리
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.NEVER)
                .invalidSessionUrl("/Login")
                .and()
//	    csrf
                .csrf().disable();


    }
    @Bean
    public PersistentTokenRepository persistentTokenRepository()
    {
        JdbcTokenRepositoryImpl db = new JdbcTokenRepositoryImpl();
        db.setDataSource(dataSource);
        return db;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        return new CustomAuthenticationProvider(userDetailsService(), passwordEncoder());
    }

    @Bean
    public CustomAuthenticationFilter customAuthenticationFilter() throws Exception{
        CustomAuthenticationFilter filter = new CustomAuthenticationFilter(
                new AntPathRequestMatcher("/Login", HttpMethod.POST.name())
        );
        filter.setAuthenticationManager(authenticationManagerBean());
        filter.setAuthenticationSuccessHandler(new SimpleUrlAuthenticationSuccessHandler("/"));
        filter.setAuthenticationFailureHandler(new SimpleUrlAuthenticationFailureHandler("/Login"));

        return filter;
    }

    //security 기본설정
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception
    {
       auth.userDetailsService(memberService).passwordEncoder(passwordEncoder());
    }

}
