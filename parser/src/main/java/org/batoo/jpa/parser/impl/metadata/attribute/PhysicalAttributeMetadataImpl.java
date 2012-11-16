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
package org.batoo.jpa.parser.impl.metadata.attribute;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.batoo.common.reflect.ReflectHelper;
import org.batoo.jpa.parser.impl.metadata.ColumnMetadataImpl;
import org.batoo.jpa.parser.metadata.ColumnMetadata;
import org.batoo.jpa.parser.metadata.attribute.PhysicalAttributeMetadata;

/**
 * 
 * The implementation of the {@link PhysicalAttributeMetadata}.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class PhysicalAttributeMetadataImpl extends AttributeMetadataImpl implements PhysicalAttributeMetadata {

	private final ColumnMetadata column;
	private final TemporalType temporalType;

	/**
	 * @param member
	 *            the java member of singular attribute
	 * @param metadata
	 *            the metadata definition of the singular attribute
	 * 
	 * @since 2.0.0
	 */
	public PhysicalAttributeMetadataImpl(Member member, PhysicalAttributeMetadata metadata) {
		super(member, metadata);

		this.column = metadata.getColumn();
		this.temporalType = metadata.getTemporalType();
	}

	/**
	 * @param member
	 *            the java member of singular attribute
	 * @param name
	 *            the name of the singular attribute
	 * @param parsed
	 *            set of annotations parsed
	 * 
	 * @since 2.0.0
	 */
	public PhysicalAttributeMetadataImpl(Member member, String name, Set<Class<? extends Annotation>> parsed) {
		super(member, name);

		final Column column = ReflectHelper.getAnnotation(member, Column.class);
		final Temporal temporal = ReflectHelper.getAnnotation(member, Temporal.class);

		parsed.add(Column.class);
		parsed.add(Temporal.class);

		this.column = column != null ? new ColumnMetadataImpl(this.getLocator(), column) : null;
		this.temporalType = temporal != null ? temporal.value() : null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final ColumnMetadata getColumn() {
		return this.column;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final TemporalType getTemporalType() {
		return this.temporalType;
	}

}
