package uk.q3c.krail.persist.jpa;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.config.TargetDatabase;
import org.eclipse.persistence.jpa.PersistenceProvider;

import java.util.HashMap;

/**
 * Helper class to populate the equivalent of persistence.xml properties.  A persistence.xml file is still needed but
 * can be minimal (persistent unit declarations and
 * Created by David Sowerby on 01/01/15.
 */
public class JpaConfig extends HashMap<String, Object> {


    public enum Db {
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

        private Db(String driver, String targetDatabase, String urlPrefix) {
            this.driver = driver;
            this.targetDatabase = targetDatabase;
            this.urlPrefix = urlPrefix;
        }

        public String getTargetDatabase() {
            return targetDatabase;
        }

        public String getDriver() {
            return driver;
        }
    }

    public enum Ddl {
        CREATE_ONLY("create-tables"), DROP_AND_CREATE("drop-and-create-tables");

        private String action;

        private Ddl(String action) {
            this.action = action;
        }

        public String getAction() {
            return action;
        }
    }

    public enum TransactionType {JTA, RESOURCE_LOCAL}

    public enum LoggingLevel {OFF, SEVERE, WARNING, INFO, CONFIG, FINE, FINER, FINEST, ALL}

    private boolean create;

    private Db db;
    private String url;


    public JpaConfig() {
        //not sure why this is necessary but assigning properties doesn't work without it
        provider(PersistenceProvider.class);
    }

    public JpaConfig provider(Class<?> providerClazz) {
        put("javax.persistence.provider", providerClazz);
        return this;
    }

    public JpaConfig db(Db db) {
        this.db = db;
        put(PersistenceUnitProperties.JDBC_DRIVER, db.getDriver());
        put(PersistenceUnitProperties.TARGET_DATABASE, db.getTargetDatabase());
        if (url != null) {
            url(url, create);
        }
        return this;
    }

    public JpaConfig url(String url, boolean create) {
        this.url = url;
        this.create = create;
        String prefix = (db == null) ? "" : db.urlPrefix;
        put(PersistenceUnitProperties.JDBC_URL, prefix + url + ";create=" + create);
        return this;
    }

    public JpaConfig user(String user) {
        put(PersistenceUnitProperties.JDBC_USER, user);
        return this;
    }

    public JpaConfig password(String password) {
        put(PersistenceUnitProperties.JDBC_PASSWORD, password);
        return this;
    }

    public JpaConfig ddlGeneration(Ddl ddl) {
        put(PersistenceUnitProperties.DDL_GENERATION, ddl.getAction());
        return this;
    }

    public JpaConfig transactionType(TransactionType transactionType) {
        put(PersistenceUnitProperties.TRANSACTION_TYPE, transactionType.name());
        return this;
    }

    /**
     * Also sets property "eclipselink.logging.level.sql", see http://wiki.eclipse.org/EclipseLink/Examples/JPA/Logging
     *
     * @param level
     *
     * @return
     */
    public JpaConfig loggingLevel(LoggingLevel level) {
        put(PersistenceUnitProperties.LOGGING_LEVEL, level.name());
        put("eclipselink.logging.level.sql", level.name());
        return this;
    }
}
