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

import javax.persistence.ManyToOne;

import org.batoo.jpa.parser.metadata.attribute.ManyToOneAttributeMetadata;

/**
 * Implementation of {@link ManyToOneAttributeMetadata}.
 * 
 * @author hceylan
 * @since $version
 */
public class ManyToOneAttributeMetadataImpl extends AssociationAttributeMetadataImpl implements ManyToOneAttributeMetadata {

	private final boolean optional;
	private final String mapsId;

	/**
	 * @param member
	 *            the java member of one-to-one attribute
	 * @param metadata
	 *            the metadata definition of the one-to-one attribute
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ManyToOneAttributeMetadataImpl(Member member, ManyToOneAttributeMetadata metadata) {
		super(member, metadata);

		this.optional = metadata.isOptional();
		this.mapsId = metadata.getMapsId();
	}

	/**
	 * @param member
	 *            the java member of attribute
	 * @param name
	 *            the name of the attribute
	 * @param manyToOne
	 *            the annotation
	 * @param parsed
	 *            set of annotations parsed
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ManyToOneAttributeMetadataImpl(Member member, String name, ManyToOne manyToOne, Set<Class<? extends Annotation>> parsed) {
		super(member, name, parsed, manyToOne.targetEntity().getName(), manyToOne.fetch(), manyToOne.cascade());

		parsed.add(ManyToOne.class);

		this.optional = manyToOne.optional();
		this.mapsId = this.handleMapsId(member, parsed);
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
	public boolean isOptional() {
		return this.optional;
	}
}
