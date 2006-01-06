/*
 * Databinder: a simple bridge from Wicket to Hibernate
 * Copyright (C) 2006  Nathan Hamblen nathan@technically.us
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

/*
 * Note: this class contains code adapted from wicket-contrib-database. 
 */

package net.databinder;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;

import wicket.Application;
import wicket.Response;
import wicket.protocol.http.WebRequest;
import wicket.protocol.http.WebRequestCycle;
import wicket.protocol.http.WebSession;

/**
 * Opens Hibernate sessions as required and closes (but does not flush) them at a request's
 * end. Creates a Hibernate session factory in a static initialization block, configuring it with
 * annotatied classes in a DataApplication subclass.
 * @author Nathan Hamblen
 */
public class DataRequestCycle extends WebRequestCycle {
	private Session hibernateSession;
	private static final SessionFactory hibernateSessionFactory;

    static {
        try {
        	AnnotationConfiguration config = new AnnotationConfiguration();
        	DataApplication app = ((DataApplication)Application.get());
        	app.configureHibernate(config);
            hibernateSessionFactory = config.buildSessionFactory();
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }
	
	public DataRequestCycle(final WebSession session, final WebRequest request, final Response response) {
		super(session, request, response);
	}
	
	/**
	 * Will open a session if one is not already open for this request.
	 * @return the open Hibernate session for the current request cycle.
	 */
	public static Session getHibernateSession() {
		return ((DataRequestCycle)get()).getCycleHibernateSession();
	}
	
	/**
	 * @return the open Hibernate session for this request cycle.
	 */
	protected Session getCycleHibernateSession() {
		if(hibernateSession == null)
			hibernateSession = openSession();
		return hibernateSession;
	}
	
	/**
	 * @return a newly opened session
	 */
    private static Session openSession()
            throws HibernateException {
    			return hibernateSessionFactory.openSession();
    }
	
    /**
     * Closes but does not flush a Hibernate session, if one was open for this request.
     */
	@Override
	protected void onEndRequest() {
		try {
			if (hibernateSession != null)
				hibernateSession.close();
		} finally {
			hibernateSession = null;
		}		
	}
}