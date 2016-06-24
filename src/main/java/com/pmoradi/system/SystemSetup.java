package com.pmoradi.system;

import com.pmoradi.entities.dao.GroupDao;
import com.pmoradi.entities.dao.URLDao;
import com.pmoradi.entities.dao.UserDao;
import com.pmoradi.security.AuthenticationFilter;
import org.apache.commons.io.IOUtils;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

import javax.inject.Singleton;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Context;
import java.io.InputStream;

@ApplicationPath("/service")
public class SystemSetup extends ResourceConfig {

    public SystemSetup(@Context ServiceLocator locator) {
        packages("com.pmoradi.rest");
        packages("org.glassfish.jersey.jackson");
        register(JacksonFeature.class);
        register(AuthenticationFilter.class);

        final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("hibernate-engine");
        final SessionFactory sessionFactory = entityManagerFactory::createEntityManager;

        Runtime.getRuntime().addShutdownHook(new Thread(entityManagerFactory::close));

        register(new AbstractBinder() {
            @Override
            protected void configure() {
                bindFactory(InjectFactory.getGroupDaoFactory(sessionFactory)).to(GroupDao.class).in(Singleton.class);
                bindFactory(InjectFactory.getURLDaoFactory(sessionFactory)).to(URLDao.class).in(Singleton.class);
                bindFactory(InjectFactory.getUserDaoFactory(sessionFactory)).to(UserDao.class).in(Singleton.class);

                InputStream properties = Thread.currentThread().getContextClassLoader().getResourceAsStream("connection.properties");
                bindFactory(InjectFactory.getApplicationFactory(properties)).to(Application.class).in(Singleton.class);
                IOUtils.closeQuietly(properties);

                Factory<Facade> facadeFactory = InjectFactory.getFacadeFactory(new LockManager());
                Factory<AdminFacade> adminFacadeFactory = InjectFactory.getAdminFacadeFactory();

                locator.inject(facadeFactory);
                locator.inject(adminFacadeFactory);

                bindFactory(facadeFactory).to(Facade.class).in(Singleton.class);
                bindFactory(adminFacadeFactory).to(AdminFacade.class).in(Singleton.class);
            }
        });
    }
}