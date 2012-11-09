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

import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.PrimaryKeyJoinColumns;

import org.batoo.common.reflect.ReflectHelper;
import org.batoo.jpa.parser.impl.metadata.PrimaryKeyJoinColumnMetadataImpl;
import org.batoo.jpa.parser.metadata.PrimaryKeyJoinColumnMetadata;
import org.batoo.jpa.parser.metadata.attribute.OneToOneAttributeMetadata;

import com.google.common.collect.Lists;

/**
 * Implementation of {@link OneToOneAttributeMetadata}.
 * 
 * @author hceylan
 * @since $version
 */
public class OneToOneAttributeMetadataImpl extends AssociationAttributeMetadataImpl implements OneToOneAttributeMetadata {

	private final String mappedBy;
	private final boolean removesOprhans;
	private final boolean optional;
	private final String mapsId;
	private final List<PrimaryKeyJoinColumnMetadata> pkJoinColumns = Lists.newArrayList();

	/**
	 * @param member
	 *            the java member of one-to-one attribute
	 * @param metadata
	 *            the metadata definition of the one-to-one attribute
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public OneToOneAttributeMetadataImpl(Member member, OneToOneAttributeMetadata metadata) {
		super(member, metadata);

		this.mappedBy = metadata.getMappedBy();
		this.optional = metadata.isOptional();
		this.removesOprhans = metadata.removesOrphans();
		this.mapsId = metadata.getMapsId();
		this.pkJoinColumns.addAll(Lists.newArrayList(metadata.getPrimaryKeyJoinColumns()));
	}

	/**
	 * @param member
	 *            the java member of attribute
	 * @param name
	 *            the name of the attribute
	 * @param oneToOne
	 *            the annotation
	 * @param parsed
	 *            set of annotations parsed
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public OneToOneAttributeMetadataImpl(Member member, String name, OneToOne oneToOne, Set<Class<? extends Annotation>> parsed) {
		super(member, name, parsed, oneToOne.targetEntity().getName(), oneToOne.fetch(), oneToOne.cascade());

		this.mappedBy = oneToOne.mappedBy();
		this.optional = oneToOne.optional();
		this.removesOprhans = oneToOne.orphanRemoval();
		this.mapsId = this.handleMapsId(member, parsed);

		parsed.add(OneToOne.class);

		final PrimaryKeyJoinColumns pkJoinColumns = ReflectHelper.getAnnotation(member, PrimaryKeyJoinColumns.class);
		final PrimaryKeyJoinColumn pkJoinColumn = ReflectHelper.getAnnotation(member, PrimaryKeyJoinColumn.class);

		if (pkJoinColumns != null) {
			parsed.add(PrimaryKeyJoinColumns.class);

			for (final PrimaryKeyJoinColumn joinColumn : pkJoinColumns.value()) {
				this.pkJoinColumns.add(new PrimaryKeyJoinColumnMetadataImpl(this.getLocator(), joinColumn));
			}
		}
		else {
			if (pkJoinColumn != null) {
				parsed.add(PrimaryKeyJoinColumn.class);

				this.pkJoinColumns.add(new PrimaryKeyJoinColumnMetadataImpl(this.getLocator(), pkJoinColumn));
			}
		}
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
	public String getMapsId() {
		return this.mapsId;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<PrimaryKeyJoinColumnMetadata> getPrimaryKeyJoinColumns() {
		return this.pkJoinColumns;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isOptional() {
		return this.optional;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean removesOrphans() {
		return this.removesOprhans;
	}
}
