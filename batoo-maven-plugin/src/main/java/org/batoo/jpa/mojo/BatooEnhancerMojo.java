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
package org.batoo.jpa.mojo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.batoo.jpa.core.impl.instance.Enhancer;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Generates the enhanced classes.
 * 
 * @goal enhance
 * @phase process-classes
 * @requiresDependencyResolution compile
 * 
 * @author hceylan
 * @since $version
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class BatooEnhancerMojo extends AbstractMojo {

	/**
	 * File location for the persistence classes.
	 * 
	 * @parameter expression="${batoojpa.classes}" default-value="${project.build.outputDirectory}"
	 * @required
	 */
	protected File classes;

	/**
	 * Classpath elements to use for enhancement.
	 * 
	 * @parameter default-value="${project.compileClasspathElements}"
	 * @required
	 * @readonly
	 */
	protected List compileClasspathElements;

	/**
	 * Comma seperated representation of excludes.
	 * 
	 * @parameter default-value="";
	 */
	private String excludes;

	/**
	 * Comma seperated representation of includes.
	 * 
	 * @parameter default-value=""
	 */
	private String includes;

	/**
	 * The maven project.
	 * 
	 * @parameter default-value="${project}"
	 * @required
	 * @readonly
	 */
	protected MavenProject project;

	/**
	 * Skip the exeution.
	 * 
	 * @parameter default-value="false"
	 */
	private boolean skip;

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.apache.maven.plugin.Mojo#execute()
	 */
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		if (this.skipMojo()) {
			return;
		}

		final ArrayList<String> includeList = Lists.newArrayList();
		final ArrayList<String> excludeList = Lists.newArrayList();

		if (StringUtils.isNotBlank(this.includes)) {
			for (final String string : Splitter.on(",").trimResults().split(this.includes)) {
				includeList.add(string);
			}
		}

		if (StringUtils.isNotBlank(this.excludes)) {
			for (final String string : Splitter.on(",").trimResults().split(this.excludes)) {
				excludeList.add(string);
			}
		}

		final URLClassLoader cl = this.getExtendedClasspath();

		final Set<Class<?>> classes = Sets.newHashSet();
		this.findClasses(cl, classes, includeList, excludeList);

		for (final Class<?> clazz : classes) {
			try {
				System.out.println("Enhancing: " + clazz.getName());
				final byte[] byteCode = Enhancer.create(clazz);
				final String outputFile = this.classes.getAbsolutePath() + "/" + clazz.getName().replaceAll("\\.", "/") + Enhancer.SUFFIX_ENHANCED + ".class";
				System.out.println("Writing enhanced class: " + outputFile);

				final FileOutputStream os = new FileOutputStream(outputFile);
				try {
					os.write(byteCode);
				}
				finally {
					os.close();
				}
			}
			catch (final Exception e) {
				throw new MojoExecutionException("Enhancement failed for " + clazz.getName());
			}
		}
	}

	private void findClasses(URLClassLoader classPath, Set<Class<?>> classes, List<String> includeList, List<String> excludeList) throws MojoExecutionException {
		try {
			final Enumeration<URL> resources = classPath.getResources("");
			while (resources.hasMoreElements()) {
				String root = resources.nextElement().getFile();
				if (!root.endsWith(File.separator) && !root.endsWith("/")) {
					root = root + File.separator;
				}

				this.findClasses(classPath, classes, includeList, excludeList, root.length(), new File(root));
			}
		}
		catch (final MojoExecutionException e) {
			throw e;
		}
		catch (final Exception e) {
			throw new MojoExecutionException("Cannot scan the classpath", e);
		}
	}

	private void findClasses(URLClassLoader classPath, Set<Class<?>> classes, List<String> includeList, List<String> excludeList, int rootLength, File file)
		throws IOException, MojoExecutionException {
		if (file.isDirectory()) {
			for (final String child : file.list()) {
				this.findClasses(classPath, classes, includeList, excludeList, rootLength, new File(file.getCanonicalPath() + "/" + child));
			}
		}
		else {
			String path = file.getPath();

			if (path.endsWith(".class") && !path.endsWith("$Enhanced.class")) {
				path = path.substring(rootLength, path.length() - 6).replace("/", ".");
				try {
					final Class<?> clazz = classPath.loadClass(path);
					if (!excludeList.isEmpty()) {
						for (final String exclude : excludeList) {
							if (clazz.getName().startsWith(exclude)) {
								return;
							}
						}
					}

					if (includeList.isEmpty()) {
						classes.add(clazz);
					}
					else {
						for (final String include : includeList) {
							if (clazz.getName().startsWith(include)) {
								classes.add(clazz);

								break;
							}
						}
					}
				}
				catch (final Exception e) {
					throw new MojoExecutionException("Cannot load class: " + path);
				}
			}
		}
	}

	/**
	 * Locates and returns a list of class files found under specified class directory.
	 * 
	 * @return list of class files.
	 * @throws MojoExecutionException
	 *             if there was an error scanning class file resources.
	 */
	protected List findEntityClassFiles() throws MojoExecutionException {
		List files = new ArrayList();

		try {

			files = FileUtils.getFiles(this.getEntityClasses(), this.includes, this.excludes);
		}
		catch (final IOException e) {
			throw new MojoExecutionException("Error while scanning for '" + this.includes + "' in " + "'" + this.getEntityClasses().getAbsolutePath() + "'.", e);
		}

		return files;
	}

	/**
	 * Returns the list of classpath elements for the mojo.
	 * 
	 * @return the list of classpath elements for the mojo
	 */
	protected List getClasspathElements() {
		return this.compileClasspathElements;
	}

	/**
	 * Returns File location for the persistence classes.
	 * 
	 * @return File location for the persistence classes
	 */
	protected File getEntityClasses() {
		return this.classes;
	}

	/**
	 * Returns the extended class loader for the realm.
	 * 
	 * @return the extended class loader
	 * 
	 * @throws MojoExecutionException
	 *             thrown if execution fails.
	 */
	private URLClassLoader getExtendedClasspath() throws MojoExecutionException {
		final List urls = new ArrayList();

		for (final Object path : this.getClasspathElements()) {
			final File pathElement = new File((String) path);
			try {
				final URL url = pathElement.toURI().toURL();
				urls.add(url);
				this.getLog().debug("Added URL " + url);
			}
			catch (final MalformedURLException e) {
				throw new MojoExecutionException("Cannot extend classpath realm: " + pathElement, e);
			}
		}

		return new URLClassLoader((URL[]) urls.toArray(new URL[urls.size()]));
	}

	/**
	 * Returns the converted file paths as array.
	 * 
	 * @param files
	 *            List of files
	 * @return the converted file paths as array
	 */
	protected String[] getFilePaths(List files) {
		final String[] args = new String[files.size()];

		for (int i = 0; i < files.size(); i++) {
			final File file = (File) files.get(i);

			args[i] = file.getAbsolutePath();
		}

		return args;
	}

	/**
	 * Returns if the execution should be skipped.
	 * 
	 * @return true if the execution should be skipped, flase otherwise
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected boolean skipMojo() {
		if (this.skip) {
			this.getLog().info("Skiping enhancement execution");

			return true;
		}

		return false;
	}
}
