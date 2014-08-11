/*
 * Copyright 2014 BatooJPA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.batoo.jpa.jdbc.ds;

import org.batoo.jpa.jdbc.AbstractDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import javax.sql.rowset.RowSetMetaDataImpl;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Map;
import java.util.Properties;

/**
 * @author lburgazzoli
 */
public abstract class BatooDataSource extends AbstractDataSource {
    private static final Logger LOGGER = LoggerFactory.getLogger(BatooDataSource.class);

    /**
     * The DataSource.
     */
    private DataSource ds;

    /**
     * c-tor
     */
    public BatooDataSource() {
        this.ds = null;
    }

    // *************************************************************************
    //
    // *************************************************************************

    @Override
    public void releaseConnection(Connection connection) {
        try {
            connection.close();
        } catch(Exception e) {
            LOGGER.warn("Exception",e);
        }
    }

    // *************************************************************************
    //
    // *************************************************************************

    @Override
    public Connection getConnection() throws SQLException {
        return this.ds.getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return this.ds.getConnection(username,password);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return this.ds.getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        this.ds.setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        this.ds.setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return this.ds.getLoginTimeout();
    }

    @Override
    public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return this.ds.getParentLogger();
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return DataSource.class.equals(iface)
            || AbstractDataSource.class.isAssignableFrom(iface)
            || BatooDataSource.class.isAssignableFrom(iface);
    }

    @Override
    public <T> T unwrap(Class<T> iface) {
        try {
            RowSetMetaDataImpl impl;
            if (isWrapperFor(iface)) {
                return iface.cast(this);
            } else {
                throw new RuntimeException("Cannot unwrap to requested type [" + iface.getName() + "]");
            }
        } catch(SQLException e) {
            throw new RuntimeException("Cannot unwrap to requested type [" + iface.getName() + "]",e);
        }
    }

    // *************************************************************************
    //
    // *************************************************************************

    /**
     *
     * @param ds
     */
    protected void setWrappedDataSource(DataSource ds) {
        this.ds = ds;
    }

    /**
     *
     */
    protected DataSource getWrappedDataSource() {
        return this.ds;
    }

    /**
     *
     * @param props
     * @param prefix
     * @return
     * @throws Exception
     */
    protected Properties mapToProperties(Map<?,?> props,String prefix) throws Exception {
        Properties cpProps = new Properties();

        for(Object keyo : props.keySet()) {
            String key = (String)keyo;
            if(key.startsWith(prefix)) {
                cpProps.setProperty(
                    key.substring(prefix.length()),
                    (String)props.get(key));
            }
        }

        return cpProps;
    }
}
