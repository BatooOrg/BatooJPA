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
import java.util.List;
import java.util.Set;

import javax.persistence.EnumType;
import javax.persistence.ManyToMany;
import javax.persistence.TemporalType;

import org.batoo.jpa.parser.metadata.AttributeOverrideMetadata;
import org.batoo.jpa.parser.metadata.ColumnMetadata;
import org.batoo.jpa.parser.metadata.attribute.ManyToManyAttributeMetadata;

import com.google.common.collect.Lists;

/**
 * Implementation of {@link ManyToManyAttributeMetadata}.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class ManyToManyAttributeMetadataImpl extends AssociationAttributeMetadataImpl implements ManyToManyAttributeMetadata {

	private final String mappedBy;
	private final String mapKey;
	private final String mapKeyClassName;
	private final ColumnMetadata mapKeyColumn;
	private final EnumType mapKeyEnumType;
	private final TemporalType mapKeyTemporalType;
	private final String orderBy;
	private final ColumnMetadata orderColumn;
	private final List<AttributeOverrideMetadata> mapKeyAttributeOverrides = Lists.newArrayList();

	/**
	 * @param member
	 *            the java member of one-to-one attribute
	 * @param metadata
	 *            the metadata definition of the one-to-one attribute
	 * 
	 * @since 2.0.0
	 */
	public ManyToManyAttributeMetadataImpl(Member member, ManyToManyAttributeMetadata metadata) {
		super(member, metadata);

		this.mappedBy = metadata.getMappedBy();
		this.mapKey = metadata.getMapKey();
		this.mapKeyClassName = metadata.getMapKeyClassName();
		this.mapKeyColumn = metadata.getMapKeyColumn();
		this.mapKeyEnumType = metadata.getMapKeyEnumType();
		this.mapKeyTemporalType = metadata.getMapKeyTemporalType();
		this.mapKeyAttributeOverrides.addAll(metadata.getMapKeyAttributeOverrides());
		this.orderBy = metadata.getOrderBy();
		this.orderColumn = metadata.getOrderColumn();
	}

	/**
	 * @param member
	 *            the java member of attribute
	 * @param name
	 *            the name of the attribute
	 * @param manyToMany
	 *            the annotation
	 * @param parsed
	 *            set of annotations parsed
	 * 
	 * @since 2.0.0
	 */
	public ManyToManyAttributeMetadataImpl(Member member, String name, ManyToMany manyToMany, Set<Class<? extends Annotation>> parsed) {
		super(member, name, parsed, manyToMany.targetEntity().getName(), manyToMany.fetch(), manyToMany.cascade());

		parsed.add(ManyToMany.class);

		this.mappedBy = manyToMany.mappedBy();
		this.mapKey = this.handleMapKey(member, parsed);
		this.mapKeyClassName = this.handleMapKeyClassName(member, parsed);
		this.mapKeyColumn = this.handleMapKeyColumn(member, parsed);
		this.mapKeyEnumType = this.handleMapKeyEnumType(member, parsed);
		this.mapKeyTemporalType = this.handleMapKeyTemporalType(member, parsed);
		this.orderBy = this.handleOrderBy(member, parsed);
		this.orderColumn = this.handleOrderColumn(member, parsed);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getMapKey() {
		return this.mapKey;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<AttributeOverrideMetadata> getMapKeyAttributeOverrides() {
		return this.mapKeyAttributeOverrides;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getMapKeyClassName() {
		return this.mapKeyClassName;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public ColumnMetadata getMapKeyColumn() {
		return this.mapKeyColumn;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public EnumType getMapKeyEnumType() {
		return this.mapKeyEnumType;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public TemporalType getMapKeyTemporalType() {
		return this.mapKeyTemporalType;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getMappedBy() {
		return this.mappedBy;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getOrderBy() {
		return this.orderBy;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public ColumnMetadata getOrderColumn() {
		return this.orderColumn;
	}
}
