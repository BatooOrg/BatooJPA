/*
 * Copyright (c) 2012 - Batoo Software ve Consultancy Ltd.
 * 
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.batoo.jpa.parser.impl.metadata;

import java.util.Map;

import javax.persistence.NamedNativeQuery;
import javax.persistence.QueryHint;

import org.batoo.jpa.parser.AbstractLocator;
import org.batoo.jpa.parser.metadata.NamedNativeQueryMetadata;
import org.batoo.jpa.parser.metadata.NamedQueryMetadata;

import com.google.common.collect.Maps;

/**
 * Implementation of {@link NamedQueryMetadata}.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class NamedNativeQueryMetadataImpl implements NamedNativeQueryMetadata {

	private final AbstractLocator locator;
	private final String query;
	private final String name;
	private final Map<String, Object> hints = Maps.newHashMap();
	private final String resultClass;
	private final String resultSetMapping;

	/**
	 * @param locator
	 *            the locator
	 * @param annotation
	 *            the annotation
	 * 
	 * @since 2.0.0
	 */
	public NamedNativeQueryMetadataImpl(AbstractLocator locator, NamedNativeQuery annotation) {
		super();

		this.locator = locator;
		this.name = annotation.name();
		this.query = annotation.query();
		this.resultClass = annotation.resultClass().getName();
		this.resultSetMapping = annotation.resultSetMapping();

		for (final QueryHint hint : annotation.hints()) {
			this.hints.put(hint.name(), hint.value());
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Map<String, Object> getHints() {
		return this.hints;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public AbstractLocator getLocator() {
		return this.locator;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getQuery() {
		return this.query;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getResultClass() {
		return this.resultClass;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getResultSetMapping() {
		return this.resultSetMapping;
	}
}
