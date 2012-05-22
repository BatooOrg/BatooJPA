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

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;

import javax.persistence.Access;
import javax.persistence.AccessType;

import org.apache.commons.beanutils.PropertyUtils;
import org.batoo.jpa.common.log.BLogger;
import org.batoo.jpa.common.log.BLoggerFactory;
import org.batoo.jpa.common.log.ToStringBuilder;
import org.batoo.jpa.common.reflect.ReflectHelper;
import org.batoo.jpa.parser.MappingException;
import org.batoo.jpa.parser.impl.annotated.BasicsParser;
import org.batoo.jpa.parser.impl.annotated.EmbeddedIdsParser;
import org.batoo.jpa.parser.impl.annotated.EmbeddedsParser;
import org.batoo.jpa.parser.impl.annotated.IdsParser;
import org.batoo.jpa.parser.impl.annotated.ManyToManiesParser;
import org.batoo.jpa.parser.impl.annotated.ManyToOnesParser;
import org.batoo.jpa.parser.impl.annotated.OneToManiesParser;
import org.batoo.jpa.parser.impl.annotated.OneToOnesParser;
import org.batoo.jpa.parser.impl.annotated.TransientsParser;
import org.batoo.jpa.parser.impl.annotated.VersionsParser;
import org.batoo.jpa.parser.impl.metadata.type.EntityMetadataImpl;
import org.batoo.jpa.parser.metadata.attribute.AttributeMetadata;
import org.batoo.jpa.parser.metadata.attribute.AttributesMetadata;
import org.batoo.jpa.parser.metadata.attribute.BasicAttributeMetadata;
import org.batoo.jpa.parser.metadata.attribute.EmbeddedAttributeMetadata;
import org.batoo.jpa.parser.metadata.attribute.EmbeddedIdAttributeMetadata;
import org.batoo.jpa.parser.metadata.attribute.IdAttributeMetadata;
import org.batoo.jpa.parser.metadata.attribute.ManyToManyAttributeMetadata;
import org.batoo.jpa.parser.metadata.attribute.ManyToOneAttributeMetadata;
import org.batoo.jpa.parser.metadata.attribute.OneToManyAttributeMetadata;
import org.batoo.jpa.parser.metadata.attribute.OneToOneAttributeMetadata;
import org.batoo.jpa.parser.metadata.attribute.TransientAttributeMetadata;
import org.batoo.jpa.parser.metadata.attribute.VersionAttributeMetadata;

import com.google.common.collect.Maps;

/**
 * Implementation of {@link AttributesMetadata}.
 * 
 * @author hceylan
 * @since $version
 */
public class AttributesMetadataImpl implements AttributesMetadata {

	private static final BLogger LOG = BLoggerFactory.getLogger(AttributesMetadataImpl.class);

	private final EntityMetadataImpl parent;
	private final AttributesMetadata ormMetadata;

	private final List<TransientAttributeMetadata> transients;
	private final List<IdAttributeMetadata> ids;
	private final List<EmbeddedIdAttributeMetadata> embeddedIds;
	private final List<BasicAttributeMetadata> basics;
	private final List<VersionAttributeMetadata> versions;
	private final List<OneToOneAttributeMetadata> oneToOnes;
	private final List<OneToManyAttributeMetadata> oneToManies;
	private final List<ManyToOneAttributeMetadata> manyToOnes;
	private final List<ManyToManyAttributeMetadata> manyToManies;

	private final Map<String, AttributeMetadata> ormAttributeMap = Maps.newHashMap();
	private final Map<String, Member> memberMap = Maps.newHashMap();

	private final List<EmbeddedAttributeMetadata> embeddeds;

	/**
	 * 
	 * @param parent
	 *            the parent managed type
	 * @param ormMetadata
	 *            the optional ORM Metadata
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AttributesMetadataImpl(EntityMetadataImpl parent, AttributesMetadata ormMetadata) {
		super();

		this.parent = parent;
		this.ormMetadata = ormMetadata;

		this.consolidateAttributes();
		this.probeAttributes();

		// transients
		this.transients = new TransientsParser(this, this.memberMap, ormMetadata != null ? ormMetadata.getTransients() : null).getAttributes();

		// id and version stuff
		this.ids = new IdsParser(this, this.memberMap, ormMetadata != null ? ormMetadata.getIds() : null).getAttributes();
		this.embeddedIds = new EmbeddedIdsParser(this, this.memberMap, ormMetadata != null ? ormMetadata.getEmbeddedIds() : null).getAttributes();
		this.versions = new VersionsParser(this, this.memberMap, ormMetadata != null ? ormMetadata.getVersions() : null).getAttributes();

		// embeddeds
		this.embeddeds = new EmbeddedsParser(this, this.memberMap, ormMetadata != null ? ormMetadata.getEmbeddeds() : null).getAttributes();

		// associations
		this.oneToOnes = new OneToOnesParser(this, this.memberMap, ormMetadata != null ? ormMetadata.getOneToOnes() : null).getAttributes();
		this.oneToManies = new OneToManiesParser(this, this.memberMap, ormMetadata != null ? ormMetadata.getOneToManies() : null).getAttributes();
		this.manyToOnes = new ManyToOnesParser(this, this.memberMap, ormMetadata != null ? ormMetadata.getManyToOnes() : null).getAttributes();
		this.manyToManies = new ManyToManiesParser(this, this.memberMap, ormMetadata != null ? ormMetadata.getManyToManies() : null).getAttributes();

		// basics is the last
		this.basics = new BasicsParser(this, this.memberMap, ormMetadata != null ? ormMetadata.getBasics() : null).getAttributes();
	}

	/**
	 * Consolidates all the ORM defined attributes into a single map.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private void consolidateAttributes() {
		/*
		 * NOTE: 21.05.2012 01:19
		 * .
		 * Just watched "the Lake House"...
		 * .
		 * ...for the second time, the first one being in a movie theater and now on TV when I took a break from the project and wanted to
		 * watch a movie.
		 * .
		 * For those haven't seen it yet, it's a love story spanning over two years. Love is all about passion.
		 * .
		 * I have started this project two months ago, and the prototype proved that it could be what I envision it to be.
		 * .
		 * From now on, I'll put notes like this into the sources.
		 * .
		 * Hope two years from now, some who are as passionate about 'developing software' as I am, will be reading them.
		 */

		AttributesMetadataImpl.LOG.debug("Consolidating ORM Attributes of entity {0}", this.parent.getClassName());

		if (this.ormMetadata != null) {
			this.consolidateAttributes(this.ormMetadata.getBasics());
			this.consolidateAttributes(this.ormMetadata.getEmbeddedIds());
			this.consolidateAttributes(this.ormMetadata.getEmbeddeds());
			this.consolidateAttributes(this.ormMetadata.getIds());
			this.consolidateAttributes(this.ormMetadata.getManyToManies());
			this.consolidateAttributes(this.ormMetadata.getManyToOnes());
			this.consolidateAttributes(this.ormMetadata.getOneToManies());
			this.consolidateAttributes(this.ormMetadata.getOneToOnes());
			this.consolidateAttributes(this.ormMetadata.getTransients());
			this.consolidateAttributes(this.ormMetadata.getVersions());
		}

		AttributesMetadataImpl.LOG.debug("{0} ORM Attribute(s) obtained for entity {1}", this.ormAttributeMap.size(),
			this.parent.getClassName());
	}

	/**
	 * Consolidates a partition of attributes.
	 * 
	 * @param attributes
	 *            the list of attributes to consolidate
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private void consolidateAttributes(List<? extends AttributeMetadata> attributes) {
		for (final AttributeMetadata attribute : attributes) {
			final AttributeMetadata existing = this.ormAttributeMap.get(attribute.getName());

			if (existing != null) {
				throw new MappingException("Duplicate attribute names.", existing.getLocation(), attribute.getLocation());
			}

			this.ormAttributeMap.put(attribute.getName(), attribute);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<BasicAttributeMetadata> getBasics() {
		return this.basics;
	}

	/**
	 * Returns the java class of the managed type.
	 * 
	 * @return the java class of the managed type
	 * 
	 * @see EntityMetadataImpl#getClazz()
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Class<?> getClazz() {
		return this.parent.getClazz();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<EmbeddedIdAttributeMetadata> getEmbeddedIds() {
		return this.embeddedIds;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<EmbeddedAttributeMetadata> getEmbeddeds() {
		return this.embeddeds;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<IdAttributeMetadata> getIds() {
		return this.ids;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<ManyToManyAttributeMetadata> getManyToManies() {
		return this.manyToManies;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<ManyToOneAttributeMetadata> getManyToOnes() {
		return this.manyToOnes;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<OneToManyAttributeMetadata> getOneToManies() {
		return this.oneToManies;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<OneToOneAttributeMetadata> getOneToOnes() {
		return this.oneToOnes;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<TransientAttributeMetadata> getTransients() {
		return this.transients;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<VersionAttributeMetadata> getVersions() {
		return this.versions;
	}

	/**
	 * Returns if the member should be parsed based on its access type specification.
	 * <p>
	 * Gets the access type of the <code>member</code>.
	 * <p>
	 * if the <code>ormMetada</code> is present, and <code>ormMetadata</code>'s access type is present, then its value returned. Otherwise,
	 * if exists, the <code>member</code> has {@link Access} annotation then its value is inspected.
	 * <p>
	 * Finally inferred access type is compared with the required access type and if equals returns true or false otherwise.
	 * 
	 * @param member
	 *            the member
	 * @param ormMetadata
	 *            the optional ORM Metadata
	 * @return true if should parse, false otherwise
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private boolean isApplicableToAccessType(Member member, AttributeMetadata ormMetadata) {
		AccessType effective;

		// if metadata is complete, and ormMetadata is null then we should skip the member
		if (this.parent.isMetadataComplete() && (ormMetadata == null)) {
			return false;
		}

		// if ORM Metadata sets an access type the that is effective
		if ((ormMetadata != null) && (ormMetadata.getAccess() != null)) {
			effective = ormMetadata.getAccess();
		}
		// inspect the members optional @Access annotation
		else {
			final Access access = ReflectHelper.getAnnotation(member, Access.class);

			// if access is present then infer the effective value from it
			if (access != null) {

				// if access doesn't set the preference then infer the effective value by type of the member
				if (access.value() == null) {
					if (member instanceof Field) {
						effective = AccessType.FIELD;
					}
					else {
						effective = AccessType.PROPERTY;
					}
				}
				// if access sets the preference then use it as effective
				else {
					effective = access.value();

					// check validity
					if ((member instanceof Field) && (effective == AccessType.PROPERTY)) {
						throw new MappingException("Illegal @Access(AccessType.PROPERTY) on field " + member);
					}

					if ((member instanceof Method) && (effective == AccessType.FIELD)) {
						throw new MappingException("Illegal @Access(AccessType.FIELD) on property " + member);
					}
				}
			}
			// use context access type
			else {
				effective = this.parent.getAccessType();
			}
		}

		// finally check if member is applicable to the effective access type
		switch (effective) {
			case FIELD:
				return member instanceof Field;
			default:
				return member instanceof Method;
		}
	}

	/**
	 * Returns if the managed type's metadata is complete.
	 * 
	 * @return true if the managed type's metadata is complete
	 * 
	 * @see EntityMetadataImpl#isMetadataComplete()
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public boolean isMetadataComplete() {
		return this.parent.isMetadataComplete();
	}

	/**
	 * Default entry point for a managed type to parse fields and properties of the class.
	 * <p>
	 * Based on the {@link AccessType} of the context, fields or properties is given priority.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private void probeAttributes() {
		final Class<?> clazz = this.parent.getClazz();
		final AccessType accessType = this.parent.getAccessType();

		// based on the access type preference give priority to fields or properties
		switch (accessType) {
			case FIELD:
				this.probeFields(clazz, accessType);
				this.probeProperties(clazz, accessType);

				break;
			case PROPERTY:
				this.probeProperties(clazz, accessType);
				this.probeFields(clazz, accessType);

				break;
		}
	}

	/**
	 * Entry point to probe the fields.
	 * <p>
	 * Enumerates the fields of the class and inspects each.
	 * 
	 * @param clazz
	 *            the java class
	 * @param accessType
	 *            the default {@link AccessType} of the context
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private void probeFields(final Class<?> clazz, AccessType accessType) {
		for (final Field field : clazz.getDeclaredFields()) {
			final String name = field.getName();

			// if the field is transient then skip it
			if (Modifier.isTransient(field.getModifiers())) {
				continue;
			}

			// locate the ORM Metadata attribute
			final AttributeMetadata ormMetadata = AttributesMetadataImpl.this.ormAttributeMap.get(name);

			// find out if it is applicable
			final boolean applicable = AttributesMetadataImpl.this.isApplicableToAccessType(field, ormMetadata);

			// if applicable then add to the member map
			if (applicable) {
				AttributesMetadataImpl.this.memberMap.put(name, field);
			}
		}

	}

	/**
	 * Entry point to parse the properties.
	 * <p>
	 * Enumerates the getters of the class and inspects each.
	 * 
	 * @param clazz
	 *            the java class
	 * @param accessType
	 *            the default {@link AccessType} of the context
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private void probeProperties(Class<?> clazz, AccessType accessType) {
		// get the properties of the class
		final PropertyDescriptor[] properties = PropertyUtils.getPropertyDescriptors(clazz);

		// for each of the properties if there is a no arg getter then inspect it
		for (final PropertyDescriptor property : properties) {

			final Method getter = property.getReadMethod();

			// check if the property has non-indexed getter
			if ((getter != null) && (getter.getParameterTypes().length == 0)) {
				final Method method = property.getReadMethod();

				final String name = property.getName();

				// locate the ORM Metadata attribute
				final AttributeMetadata ormMetadata = this.ormAttributeMap.get(name);

				// find out if it is applicable
				final boolean applicable = this.isApplicableToAccessType(method, ormMetadata);

				// if applicable then add to the member map
				if (applicable) {
					this.memberMap.put(name, method);
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return new ToStringBuilder(this)//
		.excludeFieldNames("parent", "ormMetadata", "ormAttributeMap", "memberMap") //
		.toString();
	}
}
