package com.visualisation.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.sql.DataSource;


@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "entityManagerFactoryDAG",
        transactionManagerRef = "transactionManagerDAG",
        basePackages = {"com.visualisation.repository.dag"}) //设置Repository所在位置
public class JPAConfig {

    @Resource(name = "dag")
    private DataSource DAGDataSource;

    @Autowired
    private JpaProperties jpaProperties;

    @Resource
    private HibernateProperties properties;

    @Bean(name = "entityManagerDAG")
    public EntityManager entityManager(EntityManagerFactoryBuilder builder) {
        return entityManagerFactoryDAG(builder).getObject().createEntityManager();
    }

    @Bean(name = "entityManagerFactoryDAG")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryDAG(EntityManagerFactoryBuilder builder) {

        LocalContainerEntityManagerFactoryBean entityManagerFactory
                = builder
                .dataSource(DAGDataSource)
                //设置实体类所在位置
                .packages("com.visualisation.model.dag")
                .persistenceUnit("DAGPersistenceUnit")
                .properties(properties.determineHibernateProperties(jpaProperties.getProperties(), new
                        HibernateSettings()))
                .build();
        return entityManagerFactory;
    }

    @Bean(name = "transactionManagerDAG")
    public PlatformTransactionManager transactionManagerPrimary(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(entityManagerFactoryDAG(builder).getObject());
    }
}