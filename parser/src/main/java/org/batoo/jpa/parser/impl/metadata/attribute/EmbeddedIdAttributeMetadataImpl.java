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

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.EmbeddedId;

import org.batoo.common.reflect.ReflectHelper;
import org.batoo.jpa.parser.impl.metadata.AttributeOverrideMetadataImpl;
import org.batoo.jpa.parser.metadata.AttributeOverrideMetadata;
import org.batoo.jpa.parser.metadata.attribute.EmbeddedIdAttributeMetadata;

import com.google.common.collect.Lists;

/**
 * 
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class EmbeddedIdAttributeMetadataImpl extends AttributeMetadataImpl implements EmbeddedIdAttributeMetadata {

	private final List<AttributeOverrideMetadata> attributeOverrides = Lists.newArrayList();

	/**
	 * @param member
	 *            the java member of attribute
	 * @param metadata
	 *            the metadata definition of the attribute
	 * 
	 * @since 2.0.0
	 * @author hceylan
	 */
	public EmbeddedIdAttributeMetadataImpl(Member member, EmbeddedIdAttributeMetadata metadata) {
		super(member, metadata);

		this.attributeOverrides.addAll(metadata.getAttributeOverrides());
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
	 * @author hceylan
	 */
	public EmbeddedIdAttributeMetadataImpl(Member member, String name, Set<Class<? extends Annotation>> parsed) {
		super(member, name);

		parsed.add(EmbeddedId.class);

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
