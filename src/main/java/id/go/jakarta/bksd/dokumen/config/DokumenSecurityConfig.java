package id.go.jakarta.bksd.dokumen.config;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import id.go.jakarta.bksd.dokumen.domain.EntityAuditorAware;
import id.go.jakarta.bksd.dokumen.service.PemakaiService;

@Configuration
@EnableWebSecurity
public class DokumenSecurityConfig {
    // @Autowired
    // private PemakaiService pemakaiService;

    @Value("${server.ssl.redirect-port}")
    private int redirectPort;

    @Value("${server.port}")
    private int port;

    @Autowired
    @Qualifier("customAuthenticationEntryPoint")
    AuthenticationEntryPoint authEntryPoint;

    @Autowired
    CustomSuccessHandler customSuccessHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
            AuthenticationSuccessHandler customAuthenticationSuccessHandler) throws Exception {

        http
                .csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                .httpBasic(basic -> basic.authenticationEntryPoint(authEntryPoint))
                .exceptionHandling(Customizer.withDefaults())

                .authorizeHttpRequests(
                        configurer -> configurer
                                .requestMatchers(
                                        "/",
                                        "/home",
                                        "/agendaKota",
                                        "/captcha",
                                        "/captcha/getCaptcha",
                                        "/logmein",
                                        "/kolaborator",
                                        "/kolaborator/daftar",
                                        "/register",
                                        "registerUser",
                                        "/forgotPassword",
                                        "/resetPassword",
                                        "resetPassword2",
                                        "resetPassword3",
                                        "/processForgotPassword",
                                        "/testSendMail",
                                        "/error", "/css/**", "/static/css/**", "/static/css/fonts/**")
                                .permitAll()

                                .requestMatchers("/fonts/**", "/static/fonts/**")
                                .permitAll()

                                .requestMatchers("/js/**", "/static/js/**")
                                .permitAll()

                                .requestMatchers("/assets/**", "/static/assets/**",
                                        "/static/assets/demo/**",
                                        "/static/assets/fonts/metropolis/**",
                                        "/static/assets/img/**",
                                        "/static/assets/img/illustrations/**",
                                        "/static/assets/img/illustrations/profiles/**")
                                .permitAll()

                                .requestMatchers("/admin/**")
                                .hasRole("ADMIN")

                                .anyRequest().authenticated())
                // .rememberMe()
                //.addFilterBefore(customAuthenticationFilter(authenticationManagerBean(http)), CustomLoginFilter2.class)
                .addFilterBefore(customLoginFilter2(), UsernamePasswordAuthenticationFilter.class)
                .formLogin(form -> form.loginPage("/showLoginPage")
                        .loginProcessingUrl("/authenticateUser")
                        .successHandler(customSuccessHandler)
                        .usernameParameter("email")
                        .permitAll())
                .logout(logout -> logout
                        .permitAll()
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
                        .addLogoutHandler(new CustomLogoutHandler())
                        .invalidateHttpSession(true)
                        .logoutSuccessUrl("/showLoginPage?logout"))
                .requiresChannel(channel -> channel.anyRequest().requiresSecure());

        SecurityFilterChain chain = http.build();

        return chain;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(PemakaiService pemakaiService) {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(pemakaiService);
        auth.setPasswordEncoder(passwordEncoder());
        return auth;
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        String hierarchy = "ROLE_ADMIN > ROLE_STAFF \n ROLE_STAFF > ROLE_USER";
        roleHierarchy.setHierarchy(hierarchy);
        return roleHierarchy;
    }

    @Bean
    public DefaultWebSecurityExpressionHandler customWebSecurityExpressionHandler() {
        DefaultWebSecurityExpressionHandler expressionHandler = new DefaultWebSecurityExpressionHandler();
        expressionHandler.setRoleHierarchy(roleHierarchy());
        return expressionHandler;
    }

    // https://www.javainuse.com/boot3/sec/ssl/1
    // @Bean
    // public ServletWebServerFactory servletContainer() {
    // ServletWebServerFactory factory = new TomcatServletWebServerFactory() {

    // @Override
    // protected void postProcessContext(Context context) {
    // SecurityConstraint securityConstraint = new SecurityConstraint();
    // securityConstraint.setUserConstraint("CONFIDENTIAL");
    // SecurityCollection collection = new SecurityCollection();
    // collection.addPattern("/*");
    // securityConstraint.addCollection(collection);
    // context.addConstraint(securityConstraint);
    // }

    // };

    // ((TomcatServletWebServerFactory)
    // factory).addAdditionalTomcatConnectors(redirectConnector());

    // return factory;
    // }

    // https://www.javainuse.com/boot3/sec/ssl/1
    // private Connector redirectConnector() {
    // Connector connector = new
    // Connector("org.apache.coyote.http11.Http11NioProtocol");
    // connector.setScheme("http");
    // connector.setPort(port);
    // connector.setSecure(false);
    // connector.setRedirectPort(redirectPort);

    // return connector;
    // }

    /**
     * - recaptcha:
     * https://www.youtube.com/watch?v=xiK9H-tEiH8, menit ke 12:51 (screenshot)
     * https://www.doanduyhai.com/blog/?p=1610
     * https://www.youtube.com/watch?v=dyA_Pbtbn_E
     * https://stackoverflow.com/questions/71281032/spring-security-exposing-authenticationmanager-without-websecurityconfigureradap
     * https://infobrisk.com/tech-insights/how-to-integrate-google-recaptcha-v3-into-a-spring-boot-applications-login-page/
     * https://stackoverflow.com/questions/55308499/i-got-timeout-or-duplicate-error-using-recaptcha-v3
     * 
     * @return
     * @throws Exception
     **/
    @Bean
    public CustomLoginFilter2 customLoginFilter2() throws Exception {
        CustomLoginFilter2 filter = new CustomLoginFilter2();
        return filter;

    }

    // @Bean
    // public CustomAuthenticationFilter customAuthenticationFilter(AuthenticationManager authenticationManager) throws Exception {
    //     CustomAuthenticationFilter filter = new CustomAuthenticationFilter(authenticationManager);
    //     return filter;

    // }


    @Bean
    public AuditorAware<String> auditorAware() {
        return new EntityAuditorAware();
    }


    // @Autowired
    // public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    //     // Configure global authentication manager with user details service and password encoder
    //     //auth.userDetailsService(pemakaiService).passwordEncoder(passwordEncoder());
    //     auth.authenticationProvider(authenticationProvider(pemakaiService));
    // }

    // @Bean
    // public AuthenticationManager authenticationManagerBean(HttpSecurity http) throws Exception {
    //     return http.getSharedObject(AuthenticationManagerBuilder.class).build();
    // }

}
