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

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Properties;

/**
 * @author lburgazzoli
 */
public class HikariCpDataSource extends AbstractConnectionProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(HikariCpDataSource.class);

    public static final String CONFIG_PREFIX = "org.batoo.hikaricp.";

    /**
     * c-tor
     */
    public HikariCpDataSource() {
    }

    // *************************************************************************
    //
    // *************************************************************************

    @Override
    public void open(String persistanceUnitName, String hintName, Map<String, Object> props) {
        try {
            LOGGER.debug("Configuring HikariCP");
            setWrappedDataSource(new HikariDataSource(loadConfiguration(props)));
        } catch(Exception e) {
            LOGGER.warn("Exception",e);
        }

        LOGGER.debug("HikariCP Configured");
    }

    @Override
    public void close() {
        ((HikariDataSource)getWrappedDataSource()).shutdown();
        setWrappedDataSource(null);
    }

    // *************************************************************************
    //
    // *************************************************************************

    /**
     *
     * @param props
     * @return
     */
    private HikariConfig loadConfiguration(Map<?,?> props) throws Exception {
        Properties cpProps = mapToProperties(props,CONFIG_PREFIX);
        return new HikariConfig(cpProps);
    }
}
