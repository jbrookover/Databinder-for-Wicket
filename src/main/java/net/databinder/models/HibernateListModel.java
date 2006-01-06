/*
 * Databinder: a simple bridge from Wicket to Hibernate
 * Copyright (C) 2006  Nathan Hamblen nathan@technically.us

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

package net.databinder.models;

import net.databinder.DataRequestCycle;

import org.hibernate.Query;

import wicket.model.LoadableDetachableModel;

/**
 * Model of a Hibernate query generated List. 
 * @author Nathan Hamblen
 */
public class HibernateListModel extends LoadableDetachableModel {
	private String queryString;
	private IQueryBinder queryBinder;
	
	/**
	 * Contructor for a simple query.
	 * @param queryString query with no parameters
	 */
	public HibernateListModel(String queryString) {
		this.queryString = queryString;
	}
	
	/**
	 * Constructor for a parametarized query.
	 * @param queryString Query with parameters
	 * @param queryBinder object that binds the query parameters
	 */
	public HibernateListModel(String queryString, IQueryBinder queryBinder) {
		this(queryString);
		this.queryBinder = queryBinder;
	}
	
	/**
	 * Load the object List through Hibernate, binding query parameters if available.
	 */
	@Override
	protected Object load() {
		Query query = DataRequestCycle.getHibernateSession().createQuery(queryString);
		if (queryBinder != null)
			queryBinder.bind(query);
		return query.list();
	}
}