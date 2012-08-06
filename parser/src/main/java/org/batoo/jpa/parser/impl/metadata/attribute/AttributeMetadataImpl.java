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

import java.lang.reflect.Field;
import java.lang.reflect.Member;

import javax.persistence.AccessType;

import org.batoo.jpa.common.log.ToStringBuilder;
import org.batoo.jpa.parser.impl.AbstractLocator;
import org.batoo.jpa.parser.metadata.attribute.AttributeMetadata;

/**
 * The implementation of {@link AttributeMetadata}.
 * 
 * @author hceylan
 * @since $version
 */
public class AttributeMetadataImpl implements AttributeMetadata {

	private final Member member;
	private final String name;
	private final AccessType access;
	private final AbstractLocator locator;

	/**
	 * @param member
	 *            the java member of attribute
	 * @param metadata
	 *            the metadata definition of the attribute
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AttributeMetadataImpl(Member member, AttributeMetadata metadata) {
		this(member, metadata.getName(), metadata.getAccess(), metadata.getLocator());
	}

	/**
	 * @param member
	 *            the java member of attribute
	 * @param name
	 *            the name of the attribute
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AttributeMetadataImpl(Member member, String name) {
		this(member, name, member instanceof Field ? AccessType.FIELD : AccessType.PROPERTY, null);
	}

	private AttributeMetadataImpl(Member member, String name, AccessType access, AbstractLocator locator) {
		super();

		this.name = name;
		this.member = member;
		this.access = access;
		this.locator = locator;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final AccessType getAccess() {
		return this.access;
	}

	/**
	 * Returns the locator of the attribute.
	 * 
	 * @return the locator of the attribute
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Override
	public AbstractLocator getLocator() {
		return this.locator;
	}

	/**
	 * Returns the java member of the attribute.
	 * 
	 * @return the java member of the attribute
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public final Member getMember() {
		return this.member;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final String getName() {
		return this.name;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return new ToStringBuilder(this)//
		.excludeFieldNames("access") //
		.toString();
	}
}
