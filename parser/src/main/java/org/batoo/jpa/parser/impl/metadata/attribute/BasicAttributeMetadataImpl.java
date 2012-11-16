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

import javax.persistence.Basic;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Lob;

import org.batoo.common.reflect.ReflectHelper;
import org.batoo.jpa.annotations.ColumnTransformer;
import org.batoo.jpa.annotations.Index;
import org.batoo.jpa.parser.impl.metadata.ColumnTransformerMetadataImpl;
import org.batoo.jpa.parser.impl.metadata.IndexMetadataImpl;
import org.batoo.jpa.parser.metadata.ColumnTransformerMetadata;
import org.batoo.jpa.parser.metadata.IndexMetadata;
import org.batoo.jpa.parser.metadata.attribute.BasicAttributeMetadata;

/**
 * The implementation of the {@link BasicAttributeMetadata}.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class BasicAttributeMetadataImpl extends PhysicalAttributeMetadataImpl implements BasicAttributeMetadata {

	private final boolean lob;
	private final EnumType enumType;
	private final boolean optional;
	private final FetchType fetchType;
	private final IndexMetadata index;
	private final ColumnTransformerMetadata columnTransformer;

	/**
	 * @param member
	 *            the java member of basic attribute
	 * @param metadata
	 *            the metadata definition of the basic attribute
	 * 
	 * @since 2.0.0
	 */
	public BasicAttributeMetadataImpl(Member member, BasicAttributeMetadata metadata) {
		super(member, metadata);

		this.lob = metadata.isLob();
		this.enumType = metadata.getEnumType();
		this.optional = metadata.isOptional();
		this.fetchType = metadata.getFetchType();
		this.index = metadata.getIndex();
		this.columnTransformer = metadata.getColumnTransformer();
	}

	/**
	 * @param member
	 *            the java member of attribute
	 * @param name
	 *            the name of the attribute
	 * @param parsed
	 *            set of annotations parsed
	 * 
	 * @since 2.0.0
	 */
	public BasicAttributeMetadataImpl(Member member, String name, Set<Class<? extends Annotation>> parsed) {
		super(member, name, parsed);

		final Basic basic = ReflectHelper.getAnnotation(member, Basic.class);
		final Lob lob = ReflectHelper.getAnnotation(member, Lob.class);
		final Enumerated enumerated = ReflectHelper.getAnnotation(member, Enumerated.class);
		final Index index = ReflectHelper.getAnnotation(member, Index.class);
		final ColumnTransformer columnTransformer = ReflectHelper.getAnnotation(member, ColumnTransformer.class);

		parsed.add(Lob.class);
		parsed.add(Basic.class);
		parsed.add(Enumerated.class);
		parsed.add(Basic.class);
		parsed.add(Index.class);
		parsed.add(ColumnTransformer.class);

		this.optional = basic != null ? basic.optional() : true;
		this.fetchType = basic != null ? basic.fetch() : FetchType.EAGER;
		this.lob = lob != null;
		this.enumType = enumerated != null ? enumerated.value() : null;
		this.index = index != null ? new IndexMetadataImpl(this.getLocator(), index, this.getName()) : null;
		this.columnTransformer = columnTransformer != null ? new ColumnTransformerMetadataImpl(this.getLocator(), columnTransformer) : null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public ColumnTransformerMetadata getColumnTransformer() {
		return this.columnTransformer;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public EnumType getEnumType() {
		return this.enumType;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public FetchType getFetchType() {
		return this.fetchType;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public IndexMetadata getIndex() {
		return this.index;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isLob() {
		return this.lob;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isOptional() {
		return this.optional;
	}
}
