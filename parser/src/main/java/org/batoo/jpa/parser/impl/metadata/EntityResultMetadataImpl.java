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

import java.util.List;

import javax.persistence.EntityResult;
import javax.persistence.FieldResult;

import org.batoo.jpa.parser.AbstractLocator;
import org.batoo.jpa.parser.metadata.EntityResultMetadata;
import org.batoo.jpa.parser.metadata.FieldResultMetadata;

import com.google.common.collect.Lists;

/**
 * 
 * @author asimarslan
 * @since $version
 */
public class EntityResultMetadataImpl implements EntityResultMetadata {

	private final AbstractLocator locator;

	private final String entityClass;
	private final String discriminatorColumn;

	private final List<FieldResultMetadata> fields = Lists.newArrayList();

	/**
	 * 
	 * @param locator
	 *            the java locator
	 * @param annotation
	 *            the annotation
	 * @since $version
	 */
	public EntityResultMetadataImpl(AbstractLocator locator, EntityResult annotation) {
		super();

		this.locator = locator;

		this.entityClass = annotation.entityClass().getName();
		this.discriminatorColumn = annotation.discriminatorColumn();

		for (final FieldResult field : annotation.fields()) {
			this.fields.add(new FieldResultMetadataImpl(locator, field));
		}

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getDiscriminatorColumn() {
		return this.discriminatorColumn;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getEntityClass() {
		return this.entityClass;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<FieldResultMetadata> getFields() {
		return this.fields;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public AbstractLocator getLocator() {
		return this.locator;
	}

}
