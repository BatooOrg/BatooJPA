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

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;

import org.batoo.jpa.common.reflect.ReflectHelper;
import org.batoo.jpa.parser.impl.metadata.AssociationOverrideMetadataImpl;
import org.batoo.jpa.parser.impl.metadata.AttributeOverrideMetadataImpl;
import org.batoo.jpa.parser.metadata.AssociationMetadata;
import org.batoo.jpa.parser.metadata.AttributeOverrideMetadata;
import org.batoo.jpa.parser.metadata.attribute.EmbeddedAttributeMetadata;

import com.google.common.collect.Lists;

/**
 * The implementation of the {@link EmbeddedAttributeMetadata}.
 * 
 * @author hceylan
 * @since $version
 */
public class EmbeddedAttributeMetadataImpl extends AttributeMetadataImpl implements EmbeddedAttributeMetadata {

	private final List<AttributeOverrideMetadata> attributeOverrides = Lists.newArrayList();
	private final List<AssociationMetadata> associationOverrides = Lists.newArrayList();

	/**
	 * @param member
	 *            the java member of attribute
	 * @param metadata
	 *            the metadata definition of the attribute
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public EmbeddedAttributeMetadataImpl(Member member, EmbeddedAttributeMetadata metadata) {
		super(member, metadata);

		this.attributeOverrides.addAll(metadata.getAttributeOverrides());
		this.associationOverrides.addAll(metadata.getAssociationOverrides());
	}

	/**
	 * @param member
	 *            the java member of attribute
	 * @param name
	 *            the name of the attribute
	 * @param parsed
	 *            set of annotations parsed
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public EmbeddedAttributeMetadataImpl(Member member, String name, Set<Class<? extends Annotation>> parsed) {
		super(member, name);

		// if there is AttributesOverrides annotation use it
		final AttributeOverrides attributeOverrides = ReflectHelper.getAnnotation(member, AttributeOverrides.class);
		final AttributeOverride attributeOverride = ReflectHelper.getAnnotation(member, AttributeOverride.class);

		if ((attributeOverrides != null) && (attributeOverrides.value() != null) && (attributeOverrides.value().length > 0)) {
			parsed.add(AttributeOverrides.class);

			for (final AttributeOverride a : attributeOverrides.value()) {
				this.attributeOverrides.add(new AttributeOverrideMetadataImpl(this.getLocator(), a));
			}
		}
		else if (attributeOverride != null) {
			parsed.add(AttributeOverride.class);

			this.attributeOverrides.add(new AttributeOverrideMetadataImpl(this.getLocator(), attributeOverride));
		}

		// if there is AssociationsOverrides annotation use it
		final AssociationOverrides associationOverrides = ReflectHelper.getAnnotation(member, AssociationOverrides.class);
		final AssociationOverride associationOverride = ReflectHelper.getAnnotation(member, AssociationOverride.class);

		if ((associationOverrides != null) && (associationOverrides.value() != null) && (associationOverrides.value().length > 0)) {
			parsed.add(AssociationOverrides.class);

			for (final AssociationOverride a : associationOverrides.value()) {
				this.associationOverrides.add(new AssociationOverrideMetadataImpl(this.getLocator(), a));
			}
		}
		else if (associationOverride != null) {
			parsed.add(AssociationOverride.class);

			this.associationOverrides.add(new AssociationOverrideMetadataImpl(this.getLocator(), associationOverride));
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<AssociationMetadata> getAssociationOverrides() {
		return this.associationOverrides;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<AttributeOverrideMetadata> getAttributeOverrides() {
		return this.attributeOverrides;
	}

}
