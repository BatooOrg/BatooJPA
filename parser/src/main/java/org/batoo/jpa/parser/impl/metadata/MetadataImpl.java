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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

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
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public MetadataImpl() {
		super();
	}

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

	private void findClasses(ClassLoader cl, Set<Class<?>> classes, int rootLength, File file) throws IOException {
		if (file.isDirectory()) {
			for (final String child : file.list()) {
				this.findClasses(cl, classes, rootLength, new File(file.getCanonicalPath() + "/" + child));
			}
		}
		else {
			String path = file.getPath();

			if (path.endsWith(".class")) {
				// Windows compatibility
				if (System.getProperty("os.name").toUpperCase(Locale.ENGLISH).startsWith("WINDOWS")) {
					rootLength--;
				}

				path = path.substring(rootLength, path.length() - 6).replace("/", ".").replace("\\", ".");
				try {
					final Class<?> clazz = this.isPersistentClass(cl, path);
					if (clazz != null) {
						classes.add(clazz);
					}
				}
				catch (final Exception e) {
					MetadataImpl.LOG.warn("Cannot load class {0} in {1}", path, file.getPath());
				}
			}
		}
	}

	private void findClasses(Set<Class<?>> classes, ClassLoader classPath) {
		try {
			final Enumeration<URL> resources = classPath.getResources("");
			while (resources.hasMoreElements()) {
				String root = resources.nextElement().getFile();
				if (root.endsWith("WEB-INF/")) {
					root = root + "classes/";
				}

				if (!root.endsWith(File.separator) && !root.endsWith("/")) {
					root = root + File.separator;
				}

				this.findClasses(classPath, classes, root.length(), new File(root));
			}
		}
		catch (final Exception e) {
			throw new PersistenceException("Cannot scan the classpath", e);
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

	private Class<?> isPersistentClass(ClassLoader classPath, String path) {
		try {
			final Class<?> clazz = classPath.loadClass(path);

			if ((clazz.getAnnotation(Embeddable.class) != null) || //
				(clazz.getAnnotation(MappedSuperclass.class) != null) || //
				(clazz.getAnnotation(Entity.class) != null)) {
				return clazz;
			}
		}
		catch (final Throwable e) {
			// nasty eclipse JUnit fragment spits bogus class loading errors
			if (!path.startsWith("org.eclipse.jdt")) {
				MetadataImpl.LOG.debug(e, "Unable to read class: {0}" + path);
			}
		}

		return null;
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
				throw new MappingException("Duplicate definitions for " + managedType.getClassName(), managedType.getLocator(), existing.getLocator());
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
	public void parse(final ClassLoader classloader) {
		// sort the emanage classes by inheritence
		final ArrayList<String> managedClasses = Lists.newArrayList(this.entityMap.keySet());
		Collections.sort(managedClasses, new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				Class<?> c1 = null, c2 = null;
				try {
					c1 = classloader.loadClass(o1);
					c2 = classloader.loadClass(o2);
				}
				catch (final ClassNotFoundException e) {}

				if (c1.isAssignableFrom(c2)) {
					return -1;
				}

				if (c2.isAssignableFrom(c1)) {
					return 1;
				}

				return 0;
			}
		});

		for (final String className : managedClasses) {
			final ManagedTypeMetadata metadata = this.entityMap.get(className);

			try {
				final Class<?> clazz = classloader.loadClass(className);
				ManagedTypeMetadata parentMetadata = null;

				Class<?> parentClass = clazz.getSuperclass();
				while ((parentMetadata == null) && (parentClass != Object.class)) {
					parentMetadata = this.entityMap.get(parentClass.getName());

					parentClass = parentClass.getSuperclass();
				}

				final AccessType parentAccessType = parentMetadata != null ? parentMetadata.getAccessType() : null;

				if (metadata == null) {
					if (clazz.getAnnotation(Entity.class) != null) {
						final EntityMetadataImpl entityMetadata = new EntityMetadataImpl(clazz, null, parentAccessType);
						this.entityMap.put(className, entityMetadata);

						this.namedQueries.addAll(entityMetadata.getNamedQueries());
					}
					else if (clazz.getAnnotation(MappedSuperclass.class) != null) {
						this.entityMap.put(className, new MappedSuperclassMetadataImpl(clazz, (MappedSuperclassMetadata) metadata, parentAccessType));
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
						this.entityMap.put(className, new EntityMetadataImpl(clazz, (EntityMetadata) metadata, parentAccessType));

						this.namedQueries.addAll(((EntityMetadata) metadata).getNamedQueries());
					}
					else if (metadata instanceof MappedSuperclassMetadata) {
						this.entityMap.put(className, new MappedSuperclassMetadataImpl(clazz, (MappedSuperclassMetadata) metadata, parentAccessType));
					}
				}
			}
			catch (final ClassNotFoundException e) { // class could not be found
				throw new MappingException("Class " + className + " cound not be found.", metadata != null ? metadata.getLocator() : null);
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
	 * @param managedClassNames
	 *            the list of explicist managed class names
	 * @param excludeUnlistedClasses
	 *            if unlisted classes should be excluded
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void parse(List<URL> jarFiles, ClassLoader classloader, List<String> managedClassNames, boolean excludeUnlistedClasses) {
		final Set<Class<?>> classes = Sets.newHashSet();

		this.visitJars(jarFiles, classloader, classes);

		for (final String className : managedClassNames) {
			try {
				classes.add(classloader.loadClass(className));
			}
			catch (final ClassNotFoundException e) {
				throw new PersistenceException("Unable to load listed managed class " + className);
			}
		}

		if (!excludeUnlistedClasses) {
			this.findClasses(classes, classloader);
		}

		for (final Class<?> clazz : classes) {
			if (!this.entityMap.containsKey(clazz.getName()) && !this.entityMap.containsKey(clazz.getSimpleName())) {
				this.entityMap.put(clazz.getName(), null);
			}
		}

		this.parse(classloader);
	}

	/**
	 * @param jarFiles
	 * @param classloader
	 * @param classes
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private void visitJars(List<URL> jarFiles, ClassLoader classloader, Set<Class<?>> classes) {
		for (final URL jarUrl : jarFiles) {
			try {
				final JarFile jarFile = new JarFile(jarUrl.getFile());

				final Enumeration<JarEntry> entries = jarFile.entries();
				while (entries.hasMoreElements()) {
					final JarEntry entry = entries.nextElement();

					if (entry.isDirectory()) {
						continue;
					}

					final String className = entry.getName().replace('/', '.').replace('\\', '.');

					if (className.endsWith(".class")) {
						final Class<?> clazz = this.isPersistentClass(classloader, className.substring(0, className.length() - 6));
						if (clazz != null) {
							classes.add(clazz);
						}
					}
				}
			}
			catch (final IOException e) {
				MetadataImpl.LOG.warn(e, "unable to read jar file: {0}", jarUrl);
			}
		}
	}
}
