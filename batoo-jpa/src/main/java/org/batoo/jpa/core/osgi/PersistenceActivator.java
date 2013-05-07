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
package org.batoo.jpa.core.osgi;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * Used to discover/resolve JPA providers in an OSGi environment.
 * 
 * @author lburgazzoli
 * @since 2.0.1
 */
@SuppressWarnings("rawtypes")
public class PersistenceActivator implements BundleActivator {

	private static BundleContext context = null;
	private static ServiceRegistration serviceReg = null;

	public PersistenceActivator() {
		super();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		PersistenceActivator.context = context;

		final Hashtable<String, String> props = new Hashtable<String, String>();
		props.put("javax.persistence.provider", org.batoo.jpa.core.BatooPersistenceProvider.class.getName());
		props.put("javax.persistence.spi.PersistenceProvider", org.batoo.jpa.core.BatooPersistenceProvider.class.getName());
		props.put("javax.persistence.PersistenceProvider", org.batoo.jpa.core.BatooPersistenceProvider.class.getName());

		PersistenceActivator.serviceReg = context.registerService("javax.persistence.spi.PersistenceProvider",
			new org.batoo.jpa.core.BatooPersistenceProvider(), props);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		if (PersistenceActivator.serviceReg != null) {
			PersistenceActivator.serviceReg.unregister();
			PersistenceActivator.serviceReg = null;
		}

		PersistenceActivator.context = null;
	}
}
