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

import com.jolbox.bonecp.BoneCPConfig;
import com.jolbox.bonecp.BoneCPDataSource;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Properties;

/**
 * @author lburgazzoli
 */
public class BoneCpDataSource extends AbstractConnectionProvider {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(BoneCpDataSource.class);

    public static final String CONFIG_PREFIX = "org.batoo.bonecp.";

    /**
     * c-tor
     */
    public BoneCpDataSource() {
    }

    // *************************************************************************
    //
    // *************************************************************************

    @Override
    public void open(String persistanceUnitName, String hintName, Map<String, Object> props) {
        try {
            LOGGER.debug("Configuring BoneCP");
            setWrappedDataSource( new BoneCPDataSource(loadConfiguration(props)));

        } catch(Exception e) {
            LOGGER.warn("Exception",e);
        }

        LOGGER.debug("BoneCP Configured");
    }

    @Override
    public void close() {
        ((BoneCPDataSource)getWrappedDataSource()).close();
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
    private BoneCPConfig loadConfiguration(Map<?,?> props) throws Exception {
        Properties cpProps = mapToProperties(props,CONFIG_PREFIX);

        if(props.containsKey("javax.persistence.jdbc.driver")) {
            cpProps.setProperty("driverClass",(String)props.get("javax.persistence.jdbc.driver"));
        }
        if(props.containsKey("javax.persistence.jdbc.url")) {
            cpProps.setProperty("jdbcUrl",(String)props.get("javax.persistence.jdbc.url"));
        }
        if(props.containsKey("javax.persistence.jdbc.user")) {
            cpProps.setProperty("username",(String)props.get("javax.persistence.jdbc.user"));
        }
        if(props.containsKey("javax.persistence.jdbc.password")) {
            cpProps.setProperty("password",(String)props.get("javax.persistence.jdbc.password"));
        }

        return new BoneCPConfig(cpProps);
    }
}
