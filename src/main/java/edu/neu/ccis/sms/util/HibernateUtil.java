package edu.neu.ccis.sms.util;

import java.util.logging.Logger;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * HibernateUtil class initializing hibernate sessionfactory
 * 
 * @author Pramod R. Khare
 * @date 8-May-2015
 * @lastUpdate 10-May-2015
 */
public class HibernateUtil {
    private static final Logger LOGGER = Logger.getLogger(HibernateUtil.class.getName());

    private static final SessionFactory sessionFactory = buildSessionFactory();

    /** Create the SessionFactory from hibernate.cfg.xml */
    private static SessionFactory buildSessionFactory() {
        try {
            return new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            LOGGER.severe("Initial SessionFactory creation failed." + ex.getMessage());
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    /** Close caches and connection pools */
    public static void shutdown() {
        getSessionFactory().close();
    }

}