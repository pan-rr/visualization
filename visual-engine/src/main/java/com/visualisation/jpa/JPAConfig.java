package com.visualisation.jpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.util.Objects;


@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "entityManagerFactoryDAG",
        transactionManagerRef = "transactionManagerDAG",
        basePackages = {"com.visualisation.repository.dag", "com.visualisation.repository.file"}) //设置Repository所在位置
public class JPAConfig {

    @Resource(name = "dag")
    private DataSource DAGDataSource;

    @Autowired
    private JpaProperties jpaProperties;

    @Resource
    private HibernateProperties properties;

    @Bean(name = "entityManagerDAG")
    public EntityManager entityManager(EntityManagerFactoryBuilder builder) {
        return Objects.requireNonNull(entityManagerFactoryDAG(builder).getObject()).createEntityManager();
    }

    @Bean(name = "entityManagerFactoryDAG")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryDAG(EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(DAGDataSource)
                //设置实体类所在位置
                .packages("com.visualisation.model.dag", "com.visualisation.model.file")
                .persistenceUnit("DAGPersistenceUnit")
                .properties(properties.determineHibernateProperties(jpaProperties.getProperties(), new
                        HibernateSettings()))
                .build();
    }

    @Bean(name = "transactionManagerDAG")
    public PlatformTransactionManager transactionManagerPrimary(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(Objects.requireNonNull(entityManagerFactoryDAG(builder).getObject()));
    }

    @Bean(name = "dagTransactionTemplate")
    public TransactionTemplate dagTransactionTemplate(@Qualifier("transactionManagerDAG") PlatformTransactionManager transactionManager) {
        return new TransactionTemplate(transactionManager);
    }
}