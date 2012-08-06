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
package org.batoo.jpa.parser.impl.metadata.type;

import java.lang.annotation.Annotation;
import java.util.Set;

import javax.persistence.Access;
import javax.persistence.AccessType;

import org.batoo.jpa.parser.impl.AbstractLocator;
import org.batoo.jpa.parser.impl.metadata.JavaLocator;
import org.batoo.jpa.parser.impl.metadata.attribute.AttributesMetadataImpl;
import org.batoo.jpa.parser.metadata.attribute.AttributesMetadata;
import org.batoo.jpa.parser.metadata.type.ManagedTypeMetadata;

import com.google.common.collect.Sets;

/**
 * Implementation of {@link ManagedTypeMetadatImpl}.
 * 
 * @author hceylan
 * @since $version
 */
public abstract class ManagedTypeMetadatImpl implements ManagedTypeMetadata {

	private final JavaLocator locator;
	private final Class<?> clazz;
	private final AccessType accessType;
	private final AttributesMetadataImpl attributes;
	private final Set<Class<? extends Annotation>> parsed = Sets.newHashSet();

	/**
	 * @param clazz
	 *            the represented class
	 * @param metadata
	 *            the metadata
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ManagedTypeMetadatImpl(Class<?> clazz, ManagedTypeMetadata metadata) {
		super();

		this.clazz = clazz;
		this.locator = new JavaLocator(clazz);
		this.accessType = this.getAccessType(metadata);

		// handle attributes
		this.attributes = new AttributesMetadataImpl(this, clazz, metadata != null ? metadata.getAttributes() : null);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final AccessType getAccessType() {
		return this.accessType;
	}

	/**
	 * Returns the access type.
	 * <p>
	 * if metadata exists and it specifies the access type then it is returned.
	 * <p>
	 * then is class has {@link Access} annotation then it is returned.
	 * <p>
	 * finally default {@link AccessType#FIELD} is returned.
	 * 
	 * @param metadata
	 *            the metadata
	 * @return the access type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private AccessType getAccessType(ManagedTypeMetadata metadata) {
		if ((metadata != null) && (metadata.getAccessType() != null)) {
			return metadata.getAccessType();
		}

		final Access access = this.clazz.getAnnotation(Access.class);
		if ((access != null) && (access.value() != null)) {
			return access.value();
		}

		return AccessType.FIELD;
	}

	/**
	 * Returns the set of annotations parsed.
	 * 
	 * @return the set of annotations parsed
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected Set<Class<? extends Annotation>> getAnnotationsParsed() {
		return this.parsed;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public AttributesMetadata getAttributes() {
		return this.attributes;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final String getClassName() {
		return this.clazz.getName();
	}

	/**
	 * Returns the clazz of the managed type.
	 * 
	 * @return the clazz of the managed type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Class<?> getClazz() {
		return this.clazz;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final AbstractLocator getLocator() {
		return this.locator;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isMetadataComplete() {
		return false; // N/A
	}
}
