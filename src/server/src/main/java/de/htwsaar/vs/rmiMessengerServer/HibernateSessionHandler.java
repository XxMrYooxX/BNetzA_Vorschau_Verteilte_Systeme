package de.htwsaar.vs.rmiMessengerServer;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public final class HibernateSessionHandler {

    private static SessionFactory sessionFactory;
    private static Session session;

    private static EntityManagerFactory entityManagerFactory;
    private static EntityManager entityManager;


    private static SessionFactory buildSessionFactory() {
        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().
                configure("hibernate.cfg.xml").build();
        Metadata metadata = new MetadataSources(serviceRegistry).getMetadataBuilder().build();
        SessionFactory sessionFactory = metadata.getSessionFactoryBuilder().build();
        return sessionFactory;
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) sessionFactory = buildSessionFactory();
        return sessionFactory;
    }

    public static Session getSession() {
        if (sessionFactory == null) sessionFactory = buildSessionFactory();
        Session session = sessionFactory.getCurrentSession();
        return session;
    }

    private static EntityManagerFactory buildEntityManagerFactory() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("persistence");
        return entityManagerFactory;
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        if (entityManagerFactory == null) entityManagerFactory = buildEntityManagerFactory();
        return entityManagerFactory;
    }

    public static EntityManager getEntityManager() {
        if (entityManagerFactory == null) entityManagerFactory = buildEntityManagerFactory();
        entityManager = entityManagerFactory.createEntityManager();
        return entityManager;
    }
}
