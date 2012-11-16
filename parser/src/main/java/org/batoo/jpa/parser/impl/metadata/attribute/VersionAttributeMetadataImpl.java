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

import javax.persistence.Version;

import org.batoo.jpa.parser.metadata.attribute.VersionAttributeMetadata;

/**
 * The implementation of the {@link VersionAttributeMetadata}.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class VersionAttributeMetadataImpl extends PhysicalAttributeMetadataImpl implements VersionAttributeMetadata {

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
	public VersionAttributeMetadataImpl(Member member, String name, Set<Class<? extends Annotation>> parsed) {
		super(member, name, parsed);

		parsed.add(Version.class);
	}

	/**
	 * @param member
	 *            the java member of version attribute
	 * @param metadata
	 *            the metadata definition of the version attribute
	 * 
	 * @since 2.0.0
	 * @author hceylan
	 */
	public VersionAttributeMetadataImpl(Member member, VersionAttributeMetadata metadata) {
		super(member, metadata);
	}
}
