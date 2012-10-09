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
package org.batoo.jpa.parser.impl.metadata;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.AccessType;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import javax.persistence.PersistenceException;

import org.batoo.jpa.common.log.BLogger;
import org.batoo.jpa.common.log.BLoggerFactory;
import org.batoo.jpa.parser.MappingException;
import org.batoo.jpa.parser.impl.metadata.type.EmbeddableMetadataImpl;
import org.batoo.jpa.parser.impl.metadata.type.EntityMetadataImpl;
import org.batoo.jpa.parser.impl.metadata.type.MappedSuperclassMetadataImpl;
import org.batoo.jpa.parser.metadata.EntityListenerMetadata;
import org.batoo.jpa.parser.metadata.Metadata;
import org.batoo.jpa.parser.metadata.NamedNativeQueryMetadata;
import org.batoo.jpa.parser.metadata.NamedQueryMetadata;
import org.batoo.jpa.parser.metadata.SequenceGeneratorMetadata;
import org.batoo.jpa.parser.metadata.TableGeneratorMetadata;
import org.batoo.jpa.parser.metadata.type.EmbeddableMetadata;
import org.batoo.jpa.parser.metadata.type.EntityMetadata;
import org.batoo.jpa.parser.metadata.type.ManagedTypeMetadata;
import org.batoo.jpa.parser.metadata.type.MappedSuperclassMetadata;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Implementation of {@link Metadata}.
 * 
 * @author hceylan
 * @since $version
 */
public class MetadataImpl implements Metadata {

	private static final BLogger LOG = BLoggerFactory.getLogger(MetadataImpl.class);

	private AccessType accessType;
	private boolean xmlMappingMetadataComplete;
	private String schema;
	private String catalog;

	private final List<SequenceGeneratorMetadata> sequenceGenerators = Lists.newArrayList();
	private final List<TableGeneratorMetadata> tableGenerators = Lists.newArrayList();

	private final List<NamedQueryMetadata> namedQueries = Lists.newArrayList();
	private final List<NamedNativeQueryMetadata> namedNativeQueries = Lists.newArrayList();
	private final List<EntityListenerMetadata> entityListeners = Lists.newArrayList();
	private final Map<String, ManagedTypeMetadata> entityMap = Maps.newHashMap();
	private boolean cascadePersist;

	/**
	 * @param classes
	 *            the explicit classes obtained from the Persistence XML
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public MetadataImpl(List<String> classes) {
		super();

		for (final String clazz : classes) {
			this.entityMap.put(clazz, null);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean cascadePersists() {
		return this.cascadePersist;
	}

	private void findClasses(Set<Class<?>> classes, URLClassLoader classPath) {
		try {
			final Enumeration<URL> resources = classPath.getResources("");
			while (resources.hasMoreElements()) {
				String root = resources.nextElement().getFile();
				if (root.endsWith("WEB-INF/")) {
					root = root + "classes/";
				}

				if (!root.endsWith("/")) {
					root = root + "/";
				}

				this.findClasses(classPath, classes, root.length(), new File(root));
			}
		}
		catch (final Exception e) {
			throw new PersistenceException("Cannot scan the classpath", e);
		}
	}

	private void findClasses(URLClassLoader classPath, Set<Class<?>> classes, int rootLength, File file) throws IOException {
		if (file.isDirectory()) {
			for (final String child : file.list()) {
				this.findClasses(classPath, classes, rootLength, new File(file.getCanonicalPath() + "/" + child));
			}
		}
		else {
			String path = file.getPath();

			if (path.endsWith(".class")) {
				path = path.substring(rootLength - 1, path.length() - 6).replace("/", ".").replace("\\", ".");
				try {
					final Class<?> clazz = classPath.loadClass(path);
					if ((clazz.getAnnotation(Embeddable.class) != null) || //
						(clazz.getAnnotation(MappedSuperclass.class) != null) || //
						(clazz.getAnnotation(Entity.class) != null)) {
						classes.add(clazz);
					}
				}
				catch (final Exception e) {
					MetadataImpl.LOG.warn("Cannot load class {0} in {1}", path, file.getPath());
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public AccessType getAccessType() {
		return this.accessType;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getCatalog() {
		return this.catalog;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<EntityListenerMetadata> getEntityListeners() {
		return this.entityListeners;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<ManagedTypeMetadata> getEntityMappings() {
		return Lists.newArrayList(this.entityMap.values());
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<NamedNativeQueryMetadata> getNamedNativeQueries() {
		return this.namedNativeQueries;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<NamedQueryMetadata> getNamedQueries() {
		return this.namedQueries;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getSchema() {
		return this.schema;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<SequenceGeneratorMetadata> getSequenceGenerators() {
		return this.sequenceGenerators;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<TableGeneratorMetadata> getTableGenerators() {
		return this.tableGenerators;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isXmlMappingMetadataComplete() {
		return this.xmlMappingMetadataComplete;
	}

	/**
	 * Merges the ORM XML based metadata.
	 * 
	 * @param metadata
	 *            the metadata to merge
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void merge(Metadata metadata) {
		this.accessType = metadata.getAccessType();
		this.catalog = metadata.getCatalog();
		this.schema = metadata.getSchema();

		this.xmlMappingMetadataComplete = metadata.isXmlMappingMetadataComplete();
		this.cascadePersist = metadata.cascadePersists();

		this.sequenceGenerators.addAll(metadata.getSequenceGenerators());
		this.tableGenerators.addAll(metadata.getTableGenerators());

		this.entityListeners.addAll(metadata.getEntityListeners());
		this.namedQueries.addAll(metadata.getNamedQueries());
		this.namedNativeQueries.addAll(metadata.getNamedNativeQueries());

		for (final ManagedTypeMetadata managedType : metadata.getEntityMappings()) {
			final ManagedTypeMetadata existing = this.entityMap.put(managedType.getClassName(), managedType);

			if (existing != null) {
				if (existing != null) {
					throw new MappingException("Duplicate definitions for " + managedType.getClassName(), managedType.getLocator(), existing.getLocator());
				}
			}
		}
	}

	/**
	 * Parses the types in the metamodel.
	 * 
	 * @param classloader
	 *            the class loader
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void parse(ClassLoader classloader) {
		for (final Entry<String, ManagedTypeMetadata> entry : this.entityMap.entrySet()) {
			final String className = entry.getKey();
			final ManagedTypeMetadata metadata = entry.getValue();

			try {
				final Class<?> clazz = classloader.loadClass(className);

				if (metadata == null) {
					if (clazz.getAnnotation(Entity.class) != null) {
						final EntityMetadataImpl entityMetadata = new EntityMetadataImpl(clazz, (EntityMetadata) metadata);
						this.entityMap.put(className, entityMetadata);

						this.namedQueries.addAll(entityMetadata.getNamedQueries());
					}
					else if (clazz.getAnnotation(MappedSuperclass.class) != null) {
						this.entityMap.put(className, new MappedSuperclassMetadataImpl(clazz, (MappedSuperclassMetadata) metadata));
					}
					else if (clazz.getAnnotation(Embeddable.class) != null) {
						this.entityMap.put(className, new EmbeddableMetadataImpl(clazz, (EmbeddableMetadata) metadata));
					}
					else {
						throw new MappingException("Cannot determine type of class " + className);
					}
				}
				else {
					if (metadata instanceof EntityMetadata) {
						this.entityMap.put(className, new EntityMetadataImpl(clazz, (EntityMetadata) metadata));

						this.namedQueries.addAll(((EntityMetadata) metadata).getNamedQueries());
					}
					else if (metadata instanceof MappedSuperclassMetadata) {
						this.entityMap.put(className, new MappedSuperclassMetadataImpl(clazz, (MappedSuperclassMetadata) metadata));
					}
				}
			}
			catch (final ClassNotFoundException e) { // class could not be found
				throw new MappingException("Class " + className + " cound not be found.", metadata.getLocator());
			}
		}
	}

	/**
	 * Parses the types in the metamodel and in the jar files.
	 * 
	 * @param jarFiles
	 *            the optional jar files
	 * @param classloader
	 *            the class loader
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void parse(List<URL> jarFiles, ClassLoader classloader) {
		final URLClassLoader classPath = new URLClassLoader(jarFiles.toArray(new URL[jarFiles.size()]), classloader);

		final Set<Class<?>> classes = Sets.newHashSet();

		this.findClasses(classes, classPath);

		for (final Class<?> clazz : classes) {
			if (!this.entityMap.containsKey(clazz.getName()) && !this.entityMap.containsKey(clazz.getSimpleName())) {
				this.entityMap.put(clazz.getName(), null);
			}
		}

		this.parse(classloader);
	}
}
