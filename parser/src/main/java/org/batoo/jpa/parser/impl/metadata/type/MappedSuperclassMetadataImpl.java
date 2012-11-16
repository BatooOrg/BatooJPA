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

import javax.persistence.AccessType;
import javax.persistence.MappedSuperclass;

import org.batoo.jpa.parser.metadata.type.MappedSuperclassMetadata;

/**
 * Implementation {@link MappedSuperclassMetadata}.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class MappedSuperclassMetadataImpl extends IdentifiableMetadataImpl implements MappedSuperclassMetadata {

	/**
	 * @param clazz
	 *            the represented class
	 * @param metadata
	 *            the metadata
	 * @param parentAccessType
	 *            the parent access type
	 * 
	 * @since 2.0.0
	 * @author hceylan
	 */
	public MappedSuperclassMetadataImpl(Class<?> clazz, MappedSuperclassMetadata metadata, AccessType parentAccessType) {
		super(clazz, null, parentAccessType);

		this.getAnnotationsParsed().add(MappedSuperclass.class);
	}
}
