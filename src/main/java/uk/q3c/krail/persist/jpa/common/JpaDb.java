/*
 *
 *  * Copyright (c) 2016. David Sowerby
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 *  * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 *  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 *  * specific language governing permissions and limitations under the License.
 *
 */

package uk.q3c.krail.persist.jpa.common;

import org.eclipse.persistence.config.TargetDatabase;

/**
 * Created by David Sowerby on 05/04/15.
 */
public enum JpaDb {
    //formatter:off
    DERBY_EMBEDDED("org.apache.derby.jdbc.EmbeddedDriver", TargetDatabase.Derby, "jdbc:derby:"),
    DERBY_CLIENT_SERVER("org.apache.derby.jdbc.ClientDriver", TargetDatabase.Derby, "jdbc:derby:"),
    HSQLDB("org.hsqldb.jdbcDriver", TargetDatabase.HSQL, "jdbc:hsqldb:"),
    MYSQL("com.mysql.jdbc.Driver", TargetDatabase.MySQL, "jdbc:mysql:"),
    MYSQL4("com.mysql.jdbc.Driver", TargetDatabase.MySQL4, "jdbc:mysql:");

    //formatter:on
    private String driver;
    private String targetDatabase;
    private String urlPrefix;

    JpaDb(String driver, String targetDatabase, String urlPrefix) {
        this.driver = driver;
        this.targetDatabase = targetDatabase;
        this.urlPrefix = urlPrefix;
    }

    public String getUrlPrefix() {
        return urlPrefix;
    }

    public String getTargetDatabase() {
        return targetDatabase;
    }

    public String getDriver() {
        return driver;
    }
}
