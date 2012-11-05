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
import java.util.Iterator;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.batoo.jpa.core.impl.instance.Enhancer;
import org.codehaus.plexus.util.FileUtils;

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
	 * @parameter default-value="**\/*.class"
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

		final URLClassLoader cl = this.extendRealmClasspath();

		final List<File> classes = this.findEntityClassFiles();
		final int classRootLength = this.classes.toString().length() + 1;

		for (final File classPath : classes) {
			try {
				final String absolutePath = classPath.getAbsolutePath();

				if (absolutePath.endsWith("$Enhanced.class")) {
					continue;
				}

				final String className = absolutePath.substring(classRootLength, absolutePath.length() - 6).replace('\\', '.').replace('/', '.');

				this.getLog().info("Enhancing: " + className);

				final Class<?> clazz = cl.loadClass(className);

				final byte[] byteCode = Enhancer.create(clazz);
				final String outputFile = this.classes.getAbsolutePath() + "/" + clazz.getName().replaceAll("\\.", "/") + Enhancer.SUFFIX_ENHANCED + ".class";
				this.getLog().info("Writing  : " + outputFile);

				final FileOutputStream os = new FileOutputStream(outputFile);
				try {
					os.write(byteCode);
				}
				finally {
					os.close();
				}
			}
			catch (final Exception e) {
				throw new MojoExecutionException("Enhancement failed for " + classPath.getName());
			}
		}
	}

	/**
	 * This will prepare the current ClassLoader and add all jars and local classpaths (e.g. target/classes) needed by the OpenJPA task.
	 * 
	 * @return
	 * 
	 * @throws MojoExecutionException
	 *             on any error inside the mojo
	 */
	protected URLClassLoader extendRealmClasspath() throws MojoExecutionException {
		final List urls = new ArrayList();

		for (final Iterator itor = this.compileClasspathElements.iterator(); itor.hasNext();) {
			final File pathElem = new File((String) itor.next());
			try {
				final URL url = pathElem.toURI().toURL();
				urls.add(url);
				this.getLog().debug("Added classpathElement URL " + url);
			}
			catch (final MalformedURLException e) {
				throw new MojoExecutionException("Error in adding the classpath " + pathElem, e);
			}
		}

		return new URLClassLoader((URL[]) urls.toArray(new URL[urls.size()]), this.getClass().getClassLoader());
	}

	/**
	 * Locates and returns a list of class files found under specified class directory.
	 * 
	 * @return list of class files.
	 * @throws MojoExecutionException
	 *             if there was an error scanning class file resources.
	 */
	private List findEntityClassFiles() throws MojoExecutionException {
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
	 * Returns File location for the persistence classes.
	 * 
	 * @return File location for the persistence classes
	 */
	private File getEntityClasses() {
		return this.classes;
	}

	/**
	 * Returns if the execution should be skipped.
	 * 
	 * @return true if the execution should be skipped, flase otherwise
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private boolean skipMojo() {
		if (this.skip) {
			this.getLog().info("Skiping enhancement execution");

			return true;
		}

		return false;
	}
}
