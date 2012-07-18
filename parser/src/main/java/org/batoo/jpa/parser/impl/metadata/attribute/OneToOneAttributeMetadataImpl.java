/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.batoo.jpa.parser.impl.metadata.attribute;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.util.List;
import java.util.Set;

import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.PrimaryKeyJoinColumns;

import org.batoo.jpa.common.reflect.ReflectHelper;
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
