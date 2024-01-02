package com.spring.config;

import org.hibernate.SessionFactory;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import com.spring.loginterceptor.LoggingFilter;
import java.util.Properties;

import javax.sql.DataSource;

@Configuration
@EnableAutoConfiguration
@EnableTransactionManagement
@EnableWebSecurity
@EnableJpaRepositories(basePackages = "com.spring.repository")
@EntityScan(basePackages = "com.spring.model")
public class WebSecurityConfigs  {

  @Autowired
  private JwtAuthenticationEntryPoint unauthorizedHandler;

  @Bean
  @SuppressWarnings("csrf_disabled")
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Disabling CSRF protection: Ensure robust alternative security measures are implemented.

      http
          .cors().and()
          .csrf().disable()
          .authorizeHttpRequests()
          .antMatchers("/cart/*", "/signup").permitAll()
          .anyRequest().authenticated()
          .and()
          .exceptionHandling().authenticationEntryPoint(unauthorizedHandler)
          .and()
          .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

      http.addFilterBefore(loggingFilterBean(), UsernamePasswordAuthenticationFilter.class);

      return http.build();
  }
    @Autowired
    private DataSource dataSource;

    @Bean(name = "sessionFactory")
    public LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setPackagesToScan("com.spring.model");
        sessionFactory.setHibernateProperties(additionalProperties());
        return sessionFactory;
    }

    private Properties additionalProperties() {
        Properties properties = new Properties();
        properties.put("hibernate.show_sql", "true");
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        return properties;
    }
  @Bean
  public DataSource dataSource() {
    return DataSourceBuilder.create()
        .driverClassName("com.mysql.jdbc.Driver")
        .username("root")
        .password("password123")
        .url("jdbc:mysql://localhost:3306/lms_cartservice?allowPublicKeyRetrieval=true&useSSL=false")
        .build();
  }

  @Autowired
  @Bean(name = "transactionManager")
  public HibernateTransactionManager getTransactionManager(SessionFactory sessionFactory) {
    HibernateTransactionManager transactionManager = new HibernateTransactionManager(sessionFactory);
    return transactionManager;
  }



  @Primary
  @Bean
  public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
    LocalContainerEntityManagerFactoryBean em1 = new LocalContainerEntityManagerFactoryBean();
    em1.setDataSource(dataSource());
    em1.setPackagesToScan("com.spring.model");

    JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
    em1.setJpaVendorAdapter(vendorAdapter);
    em1.setJpaProperties(additionalProperties());

    return em1;
  }

  @Bean
  public LoggingFilter loggingFilterBean() throws Exception {
    return new LoggingFilter();
  }

}