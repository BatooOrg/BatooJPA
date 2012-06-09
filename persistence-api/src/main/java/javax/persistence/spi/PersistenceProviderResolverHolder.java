package javax.persistence.spi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.PersistenceException;

/**
 * Holds the global {@link javax.persistence.spi.PersistenceProviderResolver} instance. If no <code>PersistenceProviderResolver</code> is
 * set by the environment, the default </code>PersistenceProviderResolver is used.
 * 
 * Implementations must be thread-safe.
 * 
 * @since Java Persistence 2.0
 */
public class PersistenceProviderResolverHolder {

	/**
	 * Default provider resolver class to use when none is explicitly set.
	 * 
	 * Uses the META-INF/services approach as described in the Java Persistence specification. A getResources() call is made on the current
	 * context classloader to find the service provider files on the classpath. Any service files found are then read to obtain the classes
	 * that implement the persistence provider interface.
	 */
	private static class DefaultPersistenceProviderResolver implements PersistenceProviderResolver {

		/**
		 * A ProviderName captures each provider name found in a service file as well as the URL for the service file it was found in. This
		 * information is only used for diagnostic purposes.
		 */
		private class ProviderName {

			/** Provider name **/
			private final String name;

			/** URL for the service file where the provider name was found **/
			private final URL source;

			public ProviderName(String name, URL sourceURL) {
				this.name = name;
				this.source = sourceURL;
			}

			public String getName() {
				return this.name;
			}

			public URL getSource() {
				return this.source;
			}

			@Override
			public String toString() {
				return this.getName() + " - " + this.getSource();
			}
		}

		/**
		 * Cached list of available providers cached by ClassLoader to ensure there is not potential for provider visibility issues. A weak
		 * map is used
		 */
		private volatile WeakHashMap<ClassLoader, List<PersistenceProvider>> providers = new WeakHashMap<ClassLoader, List<PersistenceProvider>>();

		private static final String LOGGER_SUBSYSTEM = "javax.persistence.spi";

		private Logger logger;

		private static final String SERVICE_PROVIDER_FILE = "META-INF/services/javax.persistence.spi.PersistenceProvider";

		private static final Pattern nonCommentPattern = Pattern.compile("^([^#]+)");

		/**
		 * For each services file look for uncommented provider names on each line.
		 */
		private void addProviderNames(URL url, Collection<ProviderName> providerNames) {
			InputStream in = null;
			try {
				in = url.openStream();
				final BufferedReader reader = new BufferedReader(new InputStreamReader(in));

				String line;
				while ((line = reader.readLine()) != null) {
					line = line.trim();
					final Matcher m = DefaultPersistenceProviderResolver.nonCommentPattern.matcher(line);
					if (m.find()) {
						providerNames.add(new ProviderName(m.group().trim(), url));
					}
				}
			}
			catch (final IOException ioe) {
				throw new PersistenceException("IOException caught reading: " + url, ioe);
			}
			finally {
				if (in != null) {
					try {
						in.close();
					}
					catch (final IOException e) {}
				}
			}
		}

		/**
		 * Clear all cached providers
		 */
		@Override
		public void clearCachedProviders() {
			this.providers.clear();
		}

		@Override
		public List<PersistenceProvider> getPersistenceProviders() {
			final ClassLoader loader = Thread.currentThread().getContextClassLoader();
			List<PersistenceProvider> loadedProviders = this.providers.get(loader);

			if (loadedProviders == null) {
				final Collection<ProviderName> providerNames = this.getProviderNames(loader);
				loadedProviders = new ArrayList<PersistenceProvider>();

				for (final ProviderName providerName : providerNames) {
					try {
						final PersistenceProvider provider = (PersistenceProvider) loader.loadClass(providerName.getName()).newInstance();
						loadedProviders.add(provider);
					}
					catch (final ClassNotFoundException cnfe) {
						this.log(Level.FINEST, cnfe + ": " + providerName);
					}
					catch (final InstantiationException ie) {
						this.log(Level.FINEST, ie + ": " + providerName);
					}
					catch (final IllegalAccessException iae) {
						this.log(Level.FINEST, iae + ": " + providerName);
					}
					catch (final ClassCastException cce) {
						this.log(Level.FINEST, cce + ": " + providerName);
					}
				}

				// If none are found we'll log the provider names for diagnostic purposes.
				if (loadedProviders.isEmpty() && !providerNames.isEmpty()) {
					this.log(Level.WARNING, "No valid providers found using:");
					for (final ProviderName name : providerNames) {
						this.log(Level.WARNING, name.toString());
					}
				}

				this.providers.put(loader, loadedProviders);
			}

			return loadedProviders;
		}

		/**
		 * Locate all JPA provider services files and collect all of the provider names available.
		 */
		private Collection<ProviderName> getProviderNames(ClassLoader loader) {
			Enumeration<URL> resources = null;

			try {
				resources = loader.getResources(DefaultPersistenceProviderResolver.SERVICE_PROVIDER_FILE);
			}
			catch (final IOException ioe) {
				throw new PersistenceException("IOException caught: " + loader + ".getResources("
					+ DefaultPersistenceProviderResolver.SERVICE_PROVIDER_FILE + ")", ioe);
			}

			final Collection<ProviderName> providerNames = new ArrayList<ProviderName>();

			while (resources.hasMoreElements()) {
				final URL url = resources.nextElement();
				this.addProviderNames(url, providerNames);
			}

			return providerNames;
		}

		private void log(Level level, String message) {
			if (this.logger == null) {
				this.logger = Logger.getLogger(DefaultPersistenceProviderResolver.LOGGER_SUBSYSTEM);
			}
			this.logger.log(level, DefaultPersistenceProviderResolver.LOGGER_SUBSYSTEM + "::" + message);
		}
	}

	private static PersistenceProviderResolver singleton = new DefaultPersistenceProviderResolver();

	/**
	 * Returns the current persistence provider resolver.
	 * 
	 * @return the current persistence provider resolver
	 */
	public static PersistenceProviderResolver getPersistenceProviderResolver() {
		return PersistenceProviderResolverHolder.singleton;
	}

	/**
	 * Defines the persistence provider resolver used.
	 * 
	 * @param resolver
	 *            persistence provider resolver to be used.
	 */
	public static void setPersistenceProviderResolver(PersistenceProviderResolver resolver) {
		if (resolver == null) {
			PersistenceProviderResolverHolder.singleton = new DefaultPersistenceProviderResolver();
		}
		else {
			PersistenceProviderResolverHolder.singleton = resolver;
		}
	}
}
