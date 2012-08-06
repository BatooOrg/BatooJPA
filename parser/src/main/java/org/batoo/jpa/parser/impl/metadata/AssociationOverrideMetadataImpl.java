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

import javax.persistence.AssociationOverride;
import javax.persistence.JoinColumn;

import org.batoo.jpa.parser.impl.AbstractLocator;
import org.batoo.jpa.parser.metadata.AssociationMetadata;
import org.batoo.jpa.parser.metadata.AttributeOverrideMetadata;
import org.batoo.jpa.parser.metadata.JoinColumnMetadata;
import org.batoo.jpa.parser.metadata.JoinTableMetadata;

import com.google.common.collect.Lists;

/**
 * Implementation of {@link AttributeOverrideMetadata}.
 * 
 * @author hceylan
 * @since $version
 */
public class AssociationOverrideMetadataImpl implements AssociationMetadata {

	private final AbstractLocator locator;
	private final String name;
	private final List<JoinColumnMetadata> joinColumns = Lists.newArrayList();
	private final JoinTableMetadata joinTable;

	/**
	 * @param locator
	 *            the locator
	 * @param annotation
	 *            the annotation
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AssociationOverrideMetadataImpl(AbstractLocator locator, AssociationOverride annotation) {
		super();

		this.locator = locator;
		this.name = annotation.name();

		if (annotation.joinColumns().length > 0) {
			for (final JoinColumn joinColumn : annotation.joinColumns()) {
				this.joinColumns.add(new JoinColumnMetadataImpl(this.locator, joinColumn));
			}

			this.joinTable = null;
		}
		else {
			this.joinTable = new JoinTableMetadaImpl(locator, annotation.joinTable());
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<JoinColumnMetadata> getJoinColumns() {
		return this.joinColumns;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public JoinTableMetadata getJoinTable() {
		return this.joinTable;
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

}
