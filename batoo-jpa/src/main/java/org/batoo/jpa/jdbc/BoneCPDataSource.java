/*
 * Copyright (c) 2012-2013, Batu Alp Ceylan
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

package org.batoo.jpa.jdbc;

import com.google.common.base.Function;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.CacheBuilder;
import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;
import com.jolbox.bonecp.PoolUtil;
import com.jolbox.bonecp.UsernamePassword;
import org.batoo.common.util.FinalWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;
import javax.sql.DataSource;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * DataSource for use with LazyConnection Provider etc.
 * 
 * @author wallacew
 */
public class BoneCPDataSource extends BoneCPConfig implements DataSource, ObjectFactory {

	/** Serialization UID. */
	private static final long serialVersionUID = -1561804548443209469L;

	/** Config setting. */
	private transient PrintWriter logWriter = null;

	/** Pool handle. */
	private FinalWrapper<BoneCP> pool;

	/** JDBC driver to use. */
	private String driverClass;

	/** Class logger. */
	private static final Logger logger = LoggerFactory.getLogger(BoneCPDataSource.class);

	/**
	 * Constructs (and caches) a datasource on the fly based on the given username/password.
	 */
    private transient final LoadingCache<UsernamePassword, BoneCPDataSource> multiDataSource =
        CacheBuilder.newBuilder().build(new CacheLoader<UsernamePassword, BoneCPDataSource>() {
            @Override
            public BoneCPDataSource load(UsernamePassword key) {
                BoneCPDataSource ds = new BoneCPDataSource(BoneCPDataSource.this.getConfig());
                ds.setUsername(key.getUsername());
                ds.setPassword(key.getPassword());

                return ds;
            }
        });

	/**
	 * Default empty constructor.
	 * 
	 */
	public BoneCPDataSource() {
		// default constructor
	}

	/**
	 * 
	 * @param config
	 *            the configuration
	 */
	public BoneCPDataSource(BoneCPConfig config) {
		final Field[] fields = BoneCPConfig.class.getDeclaredFields();
		for (final Field field : fields) {
			try {
				field.setAccessible(true);
				field.set(this, field.get(config));
			}
			catch (final Exception e) {
				// should never happen
			}
		}
	}

	/**
	 * Close the datasource.
	 * 
	 */
	public void close() {
		if (this.pool != null) {
			this.pool.value.shutdown();

			this.pool = null;
		}
	}

	/**
	 * Returns a configuration object built during initialization of the connection pool.
	 * 
	 * @return the config
	 */
	public BoneCPConfig getConfig() {
		return this;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see javax.sql.DataSource#getConnection()
	 */
	@Override
	public Connection getConnection() throws SQLException {
		FinalWrapper<BoneCP> wrapper = this.pool;

		if (wrapper == null) {
			synchronized (this) {
				if (this.pool == null) {
					this.maybeInit();
				}

				wrapper = this.pool;
			}
		}

		return wrapper.value.getConnection();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see javax.sql.DataSource#getConnection(java.lang.String, java.lang.String)
	 */
	@Override
	public Connection getConnection(String username, String password) throws SQLException {
            try { 
                return this.multiDataSource.get(new UsernamePassword(username, password)).getConnection();
            } catch(ExecutionException e) {
                throw new SQLException(e);
            }
	}

	/**
	 * Gets driver class set in config.
	 * 
	 * @return Driver class set in config
	 */
	public String getDriverClass() {
		return this.driverClass;
	}

	/**
	 * Gets the maximum time in seconds that this data source can wait while attempting to connect to a database. A value of zero means that
	 * the timeout is the default system timeout if there is one; otherwise, it means that there is no timeout. When a DataSource object is
	 * created, the login timeout is initially zero.
	 * 
	 */
	@Override
	public int getLoginTimeout() throws SQLException {
		throw new UnsupportedOperationException("getLoginTimeout is unsupported.");
	}

	/**
	 * Retrieves the log writer for this DataSource object.
	 * 
	 */
	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return this.logWriter;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Object getObjectInstance(Object object, Name name, Context context, Hashtable<?, ?> table) throws Exception {

		final Reference ref = (Reference) object;
		final Enumeration<RefAddr> addrs = ref.getAll();
		final Properties props = new Properties();
		while (addrs.hasMoreElements()) {
			final RefAddr addr = addrs.nextElement();
			if (addr.getType().equals("driverClassName")) {
				Class.forName((String) addr.getContent());
			}
			else {
				props.put(addr.getType(), addr.getContent());
			}
		}
		final BoneCPConfig config = new BoneCPConfig(props);

		return new BoneCPDataSource(config);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return null;
	}

	/**
	 * Returns the total leased connections.
	 * 
	 * @return total leased connections
	 */
	public int getTotalLeased() {
		return this.pool.value.getTotalLeased();
	}

	/**
	 * Returns true if this either implements the interface argument or is directly or indirectly a wrapper for an object that does.
	 * 
	 * @param arg0
	 *            class
	 * @return t/f
	 * @throws SQLException
	 *             on error
	 * 
	 */
	@Override
	public boolean isWrapperFor(Class<?> arg0) throws SQLException {
		return false;
	}

	/**
	 * @throws SQLException
	 * 
	 */
	private void maybeInit() throws SQLException {
		try {
			if (this.getDriverClass() != null) {
				this.loadClass(this.getDriverClass());
			}
		}
		catch (final ClassNotFoundException e) {
			throw new SQLException(PoolUtil.stringifyException(e));
		}

		BoneCPDataSource.logger.debug(this.toString());

		this.pool = new FinalWrapper<BoneCP>(new BoneCP(this));
	}

	/**
	 * Sets driver to use (called via reflection).
	 * 
	 * @param driverClass
	 *            Driver to use
	 */
	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}

	/**
	 * Sets the maximum time in seconds that this data source will wait while attempting to connect to a database. A value of zero specifies
	 * that the timeout is the default system timeout if there is one; otherwise, it specifies that there is no timeout. When a DataSource
	 * object is created, the login timeout is initially zero.
	 */
	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
		throw new UnsupportedOperationException("setLoginTimeout is unsupported.");
	}

	/**
	 * Sets the log writer for this DataSource object to the given java.io.PrintWriter object.
	 */
	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
		this.logWriter = out;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return null;
	}
}
