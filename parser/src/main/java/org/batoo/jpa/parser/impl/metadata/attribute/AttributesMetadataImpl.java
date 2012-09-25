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
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.batoo.jpa.common.log.BLogger;
import org.batoo.jpa.common.log.BLoggerFactory;
import org.batoo.jpa.common.log.ToStringBuilder;
import org.batoo.jpa.common.reflect.PropertyDescriptor;
import org.batoo.jpa.common.reflect.ReflectHelper;
import org.batoo.jpa.parser.MappingException;
import org.batoo.jpa.parser.impl.metadata.type.EntityMetadataImpl;
import org.batoo.jpa.parser.metadata.attribute.AttributeMetadata;
import org.batoo.jpa.parser.metadata.attribute.AttributesMetadata;
import org.batoo.jpa.parser.metadata.attribute.BasicAttributeMetadata;
import org.batoo.jpa.parser.metadata.attribute.ElementCollectionAttributeMetadata;
import org.batoo.jpa.parser.metadata.attribute.EmbeddedAttributeMetadata;
import org.batoo.jpa.parser.metadata.attribute.EmbeddedIdAttributeMetadata;
import org.batoo.jpa.parser.metadata.attribute.IdAttributeMetadata;
import org.batoo.jpa.parser.metadata.attribute.ManyToManyAttributeMetadata;
import org.batoo.jpa.parser.metadata.attribute.ManyToOneAttributeMetadata;
import org.batoo.jpa.parser.metadata.attribute.OneToManyAttributeMetadata;
import org.batoo.jpa.parser.metadata.attribute.OneToOneAttributeMetadata;
import org.batoo.jpa.parser.metadata.attribute.TransientAttributeMetadata;
import org.batoo.jpa.parser.metadata.attribute.VersionAttributeMetadata;
import org.batoo.jpa.parser.metadata.type.ManagedTypeMetadata;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Implementation of {@link AttributesMetadata}.
 * 
 * @author hceylan
 * @since $version
 */
public class AttributesMetadataImpl implements AttributesMetadata {

	/**
	 * Common parser for attributes that parses and merges the optional ORM File provided metadata.
	 * 
	 * @param <A>
	 *            the type of the attribute
	 * 
	 * @author hceylan
	 * @since $version
	 */
	private abstract class AttributesParser<A extends AttributeMetadata> extends ArrayList<A> {

		private final Map<String, Member> memberMap;
		private final List<A> metadatas;
		private final Class<? extends Annotation> indicativeAnnotation;

		/**
		 * @param memberMap
		 *            the map of members
		 * @param metadatas
		 *            the optional ORM Metadata
		 * @param indicativeAnnotation
		 *            the annotation that indicates the type
		 * 
		 * @since $version
		 * @author hceylan
		 */
		@SuppressWarnings("unchecked")
		public AttributesParser(Map<String, Member> memberMap, List<A> metadatas, Class<? extends Annotation> indicativeAnnotation) {
			super();

			this.memberMap = memberMap;
			this.metadatas = (List<A>) (metadatas != null ? metadatas : Lists.newArrayList());
			this.indicativeAnnotation = indicativeAnnotation;

			this.probe();

			this.metadatas.removeAll(this);
			if (this.metadatas.size() > 0) {
				throw new MappingException("The following attributes defined in orm.xml could be located\n" + metadatas);
			}
		}

		/**
		 * Parses the <code>member</code>.
		 * 
		 * @param name
		 *            the name of the attribute
		 * @param member
		 *            the member
		 * @param parseDefault
		 *            true if by default the member should be parsed
		 * @param metadata
		 *            the optional ORM Metadata
		 * @param parsed
		 *            the set of annotations parsed
		 * @return the attribute created
		 * 
		 * @since $version
		 * @author hceylan
		 */
		protected abstract A parseAttribute(String name, Member member, A metadata, Set<Class<? extends Annotation>> parsed);

		@SuppressWarnings("unchecked")
		private void probe() {
			final Set<Class<? extends Annotation>> parsed = Sets.<Class<? extends Annotation>> newHashSet();

			// if the ORM Attributes is null or empty then nothing interesting here
			if (this.metadatas != null) {

				// process the list of attributes defined by ORM Mapping
				for (final AttributeMetadata attribute : this.metadatas) {
					final Member member = this.memberMap.remove(attribute.getName());

					if (member == null) {
						throw new MappingException("The attribute " + attribute.getName() + " cannot be found.", attribute.getLocator());
					}

					// parse the attribute
					this.add(this.parseAttribute(attribute.getName(), member, (A) attribute, parsed));

					// log ignored annotations
					ReflectHelper.warnAnnotations(AttributesMetadataImpl.LOG, member, parsed);
				}
			}

			// probe the rest of the properties only if metadata is not complete
			if (!AttributesMetadataImpl.this.isMetadataComplete()) {

				// enumerate all the members
				final Iterator<Entry<String, Member>> i = this.memberMap.entrySet().iterator();
				while (i.hasNext()) {
					final Entry<String, Member> entry = i.next();

					final String name = entry.getKey();
					final Member member = entry.getValue();

					// if the member has an indicative annotation then probed
					if ((this.indicativeAnnotation == null) || (ReflectHelper.getAnnotation(member, this.indicativeAnnotation) != null)) {

						// remove the member as it is probed
						i.remove();

						// parse the attribute
						this.add(this.parseAttribute(name, member, null, parsed));

						// log ignored annotations
						ReflectHelper.warnAnnotations(AttributesMetadataImpl.LOG, member, parsed);
					}
				}
			}

		}
	}

	private static final BLogger LOG = BLoggerFactory.getLogger(AttributesMetadataImpl.class);

	private final ManagedTypeMetadata parent;
	private final AttributesMetadata metadata;

	private final List<TransientAttributeMetadata> transients;
	private final List<IdAttributeMetadata> ids;
	private final List<EmbeddedIdAttributeMetadata> embeddedIds;
	private final List<BasicAttributeMetadata> basics;
	private final List<VersionAttributeMetadata> versions;

	private final List<EmbeddedAttributeMetadata> embeddeds;

	private final List<ElementCollectionAttributeMetadata> elementCollections;

	private final List<OneToOneAttributeMetadata> oneToOnes;
	private final List<OneToManyAttributeMetadata> oneToManies;
	private final List<ManyToOneAttributeMetadata> manyToOnes;
	private final List<ManyToManyAttributeMetadata> manyToManies;

	private final Map<String, AttributeMetadata> ormAttributeMap = Maps.newHashMap();
	private final Map<String, Member> memberMap = Maps.newHashMap();

	/**
	 * 
	 * @param clazz
	 *            the entity's represented class
	 * @param parent
	 *            the parent managed type
	 * @param metadata
	 *            the metadata
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AttributesMetadataImpl(ManagedTypeMetadata parent, Class<?> clazz, AttributesMetadata metadata) {
		super();

		this.parent = parent;
		this.metadata = metadata;

		this.consolidateAttributes();
		this.probeAttributes(clazz);

		// transients
		this.transients = this.handleTransients();
		this.versions = this.handleVersions();

		// id and version stuff
		this.ids = this.handleIds();
		this.embeddedIds = this.handleEmbeddedIds();

		// embeddeds
		this.embeddeds = this.handleEmbeddeds();

		// element collections
		this.elementCollections = this.handleElementCollections();

		// associations
		this.oneToOnes = this.handleOneToOnes();
		this.manyToOnes = this.handleManyToOnes();
		this.oneToManies = this.handleOneToManies();
		this.manyToManies = this.handleManyToManies();

		// basics is the last
		this.basics = this.handleBasics();
	}

	/**
	 * Consolidates all the ORM defined attributes into a single map.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private void consolidateAttributes() {

		if (this.metadata != null) {
			AttributesMetadataImpl.LOG.debug("Consolidating ORM Attributes of entity {0}", this.parent.getClassName());

			this.consolidateAttributes(this.metadata.getBasics());
			this.consolidateAttributes(this.metadata.getEmbeddedIds());
			this.consolidateAttributes(this.metadata.getEmbeddeds());
			this.consolidateAttributes(this.metadata.getIds());
			this.consolidateAttributes(this.metadata.getManyToManies());
			this.consolidateAttributes(this.metadata.getManyToOnes());
			this.consolidateAttributes(this.metadata.getOneToManies());
			this.consolidateAttributes(this.metadata.getOneToOnes());
			this.consolidateAttributes(this.metadata.getTransients());
			this.consolidateAttributes(this.metadata.getVersions());

			AttributesMetadataImpl.LOG.debug("{0} ORM Attribute(s) obtained for entity {1}", this.ormAttributeMap.size(), this.parent.getClassName());
		}
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
				throw new MappingException("Duplicate attribute names.", existing.getLocator(), attribute.getLocator());
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
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<ElementCollectionAttributeMetadata> getElementCollections() {
		return this.elementCollections;
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
	 * Handles the basic attributes.
	 * 
	 * @return the list of basic attributes
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private List<BasicAttributeMetadata> handleBasics() {
		final List<BasicAttributeMetadata> list = this.metadata != null ? this.metadata.getBasics() : null;

		return new AttributesParser<BasicAttributeMetadata>(this.memberMap, list, null) {

			@Override
			protected BasicAttributeMetadata
				parseAttribute(String name, Member member, BasicAttributeMetadata metadata, Set<Class<? extends Annotation>> parsed) {
				if (metadata != null) {
					return new BasicAttributeMetadataImpl(member, metadata);
				}
				else {
					return new BasicAttributeMetadataImpl(member, name, parsed);
				}
			}
		};
	}

	/**
	 * Handles the element collection attributes.
	 * 
	 * @return list of element collections attributes.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private List<ElementCollectionAttributeMetadata> handleElementCollections() {
		final List<ElementCollectionAttributeMetadata> list = this.metadata != null ? this.metadata.getElementCollections() : null;

		return new AttributesParser<ElementCollectionAttributeMetadata>(this.memberMap, list, ElementCollection.class) {

			@Override
			protected ElementCollectionAttributeMetadata parseAttribute(String name, Member member, ElementCollectionAttributeMetadata metadata,
				Set<Class<? extends Annotation>> parsed) {
				if (metadata != null) {
					return new ElementCollectionAttributeMetadataImpl(member, metadata);
				}
				else {
					final ElementCollection elementCollection = ReflectHelper.getAnnotation(member, ElementCollection.class);

					return new ElementCollectionAttributeMetadataImpl(member, name, elementCollection, parsed);
				}
			}
		};
	}

	/**
	 * Handles the embedded id attributes.
	 * 
	 * @return list of embedded ids.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private List<EmbeddedIdAttributeMetadata> handleEmbeddedIds() {
		final List<EmbeddedIdAttributeMetadata> list = this.metadata != null ? this.metadata.getEmbeddedIds() : null;

		return new AttributesParser<EmbeddedIdAttributeMetadata>(this.memberMap, list, EmbeddedId.class) {

			@Override
			protected EmbeddedIdAttributeMetadata parseAttribute(String name, Member member, EmbeddedIdAttributeMetadata metadata,
				Set<Class<? extends Annotation>> parsed) {
				if (metadata != null) {
					return new EmbeddedIdAttributeMetadataImpl(member, metadata);
				}
				else {
					return new EmbeddedIdAttributeMetadataImpl(member, name, parsed);
				}
			}
		};
	}

	/**
	 * Handles the embedded attributes.
	 * 
	 * @return list of embedded attributes.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private List<EmbeddedAttributeMetadata> handleEmbeddeds() {
		final List<EmbeddedAttributeMetadata> list = this.metadata != null ? this.metadata.getEmbeddeds() : null;

		return new AttributesParser<EmbeddedAttributeMetadata>(this.memberMap, list, Embedded.class) {

			@Override
			protected EmbeddedAttributeMetadata parseAttribute(String name, Member member, EmbeddedAttributeMetadata metadata,
				Set<Class<? extends Annotation>> parsed) {
				if (metadata != null) {
					return new EmbeddedAttributeMetadataImpl(member, metadata);
				}
				else {
					return new EmbeddedAttributeMetadataImpl(member, name, parsed);
				}
			}
		};
	}

	/**
	 * Handles the id attributes.
	 * 
	 * @return list of id attributes
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private List<IdAttributeMetadata> handleIds() {
		final List<IdAttributeMetadata> list = this.metadata != null ? this.metadata.getIds() : null;

		return new AttributesParser<IdAttributeMetadata>(this.memberMap, list, Id.class) {

			@Override
			protected IdAttributeMetadata parseAttribute(String name, Member member, IdAttributeMetadata metadata, Set<Class<? extends Annotation>> parsed) {
				if (metadata != null) {
					return new IdAttributeMetadataImpl(member, metadata);
				}
				else {
					return new IdAttributeMetadataImpl(member, name, parsed);
				}
			}
		};
	}

	/**
	 * Handles the many-to-many attributes.
	 * 
	 * @return list of many-to-many attributes.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private List<ManyToManyAttributeMetadata> handleManyToManies() {
		final List<ManyToManyAttributeMetadata> list = this.metadata != null ? this.metadata.getManyToManies() : null;

		return new AttributesParser<ManyToManyAttributeMetadata>(this.memberMap, list, ManyToMany.class) {

			@Override
			protected ManyToManyAttributeMetadata parseAttribute(String name, Member member, ManyToManyAttributeMetadata metadata,
				Set<Class<? extends Annotation>> parsed) {
				if (metadata != null) {
					return new ManyToManyAttributeMetadataImpl(member, metadata);
				}
				else {
					final ManyToMany manyToMany = ReflectHelper.getAnnotation(member, ManyToMany.class);

					return new ManyToManyAttributeMetadataImpl(member, name, manyToMany, parsed);
				}
			}
		};
	}

	/**
	 * Handles the many-to-one attributes.
	 * 
	 * @return list of many-to-one attributes.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private List<ManyToOneAttributeMetadata> handleManyToOnes() {
		final List<ManyToOneAttributeMetadata> list = this.metadata != null ? this.metadata.getManyToOnes() : null;

		return new AttributesParser<ManyToOneAttributeMetadata>(this.memberMap, list, ManyToOne.class) {

			@Override
			protected ManyToOneAttributeMetadata parseAttribute(String name, Member member, ManyToOneAttributeMetadata metadata,
				Set<Class<? extends Annotation>> parsed) {
				if (metadata != null) {
					return new ManyToOneAttributeMetadataImpl(member, metadata);
				}
				else {
					final ManyToOne manyToOne = ReflectHelper.getAnnotation(member, ManyToOne.class);

					return new ManyToOneAttributeMetadataImpl(member, name, manyToOne, parsed);
				}
			}
		};
	}

	/**
	 * Handles the one-to-many attributes.
	 * 
	 * @return list of one-to-many attributes.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private List<OneToManyAttributeMetadata> handleOneToManies() {
		final List<OneToManyAttributeMetadata> list = this.metadata != null ? this.metadata.getOneToManies() : null;

		return new AttributesParser<OneToManyAttributeMetadata>(this.memberMap, list, OneToMany.class) {

			@Override
			protected OneToManyAttributeMetadata parseAttribute(String name, Member member, OneToManyAttributeMetadata metadata,
				Set<Class<? extends Annotation>> parsed) {
				if (metadata != null) {
					return new OneToManyAttributeMetadataImpl(member, metadata);
				}
				else {
					final OneToMany oneToMany = ReflectHelper.getAnnotation(member, OneToMany.class);

					return new OneToManyAttributeMetadataImpl(member, name, oneToMany, parsed);
				}
			}
		};
	}

	/**
	 * Handles the one-to-one attributes.
	 * 
	 * @return list of one-to-one attributes.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private List<OneToOneAttributeMetadata> handleOneToOnes() {
		final List<OneToOneAttributeMetadata> list = this.metadata != null ? this.metadata.getOneToOnes() : null;

		return new AttributesParser<OneToOneAttributeMetadata>(this.memberMap, list, OneToOne.class) {

			@Override
			protected OneToOneAttributeMetadata parseAttribute(String name, Member member, OneToOneAttributeMetadata metadata,
				Set<Class<? extends Annotation>> parsed) {
				if (metadata != null) {
					return new OneToOneAttributeMetadataImpl(member, metadata);
				}
				else {
					final OneToOne oneToOne = ReflectHelper.getAnnotation(member, OneToOne.class);

					return new OneToOneAttributeMetadataImpl(member, name, oneToOne, parsed);
				}
			}
		};
	}

	/**
	 * Handles the id attributes.
	 * 
	 * @return the list of transient attributes
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private List<TransientAttributeMetadata> handleTransients() {
		final List<TransientAttributeMetadata> list = this.metadata != null ? this.metadata.getTransients() : null;

		return new AttributesParser<TransientAttributeMetadata>(this.memberMap, list, Transient.class) {

			@Override
			protected TransientAttributeMetadata parseAttribute(String name, Member member, TransientAttributeMetadata metadata,
				Set<Class<? extends Annotation>> parsed) {
				if (metadata != null) {
					return new TransientAttributeMetadataImpl(member, metadata);
				}
				else {
					return new TransientAttributeMetadataImpl(member, name);
				}
			}
		};
	}

	/**
	 * Handles the version attributes
	 * 
	 * @return the list of version attributes
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private List<VersionAttributeMetadata> handleVersions() {
		final List<VersionAttributeMetadata> list = this.metadata != null ? this.metadata.getVersions() : null;

		return new AttributesParser<VersionAttributeMetadata>(this.memberMap, list, Version.class) {

			@Override
			protected VersionAttributeMetadata parseAttribute(String name, Member member, VersionAttributeMetadata metadata,
				Set<Class<? extends Annotation>> parsed) {
				if (metadata != null) {
					return new VersionAttributeMetadataImpl(member, metadata);
				}
				else {
					return new VersionAttributeMetadataImpl(member, name, parsed);
				}
			}
		};
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
	private void probeAttributes(Class<?> clazz) {
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
			if (Modifier.isTransient(field.getModifiers()) || Modifier.isStatic(field.getModifiers())) {
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
		final PropertyDescriptor[] properties = ReflectHelper.getProperties(clazz);

		// for each of the properties if there is a no arg getter then inspect it
		for (final PropertyDescriptor property : properties) {

			final Method method = property.getReader();

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
