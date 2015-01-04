package uk.q3c.krail.persist.jpa;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class JpaConfigTest {

    JpaConfig config;

    @Before
    public void setup() {
        config = new JpaConfig();
    }

    @Test
    public void db() {
        //given

        //when
        config.db(JpaConfig.Db.DERBY_EMBEDDED);
        //then
        assertThat(config.get(PersistenceUnitProperties.JDBC_DRIVER)).isEqualTo("org.apache.derby.jdbc.EmbeddedDriver");
        assertThat(config.get(PersistenceUnitProperties.TARGET_DATABASE)).isEqualTo("Derby");
    }

    @Test
    public void url() {
        //given

        //when
        config.url("jdbc:derby:/home/david/temp/derbyDb", true);
        //then
        assertThat(config.get(PersistenceUnitProperties.JDBC_URL)).isEqualTo("jdbc:derby:/home/david/temp/derbyDb;" +
                "create=true");

        //when
        config.url("jdbc:derby:/home/david/temp/derbyDb", false);
        //then
        assertThat(config.get(PersistenceUnitProperties.JDBC_URL)).isEqualTo("jdbc:derby:/home/david/temp/derbyDb;" +
                "create=false");
    }

    @Test
    public void ddl_gen() {
        //given

        //when
        config.ddlGeneration(JpaConfig.Ddl.CREATE_ONLY);
        //then
        assertThat(config.get(PersistenceUnitProperties.DDL_GENERATION)).isEqualTo("create-tables");

        //when
        config.ddlGeneration(JpaConfig.Ddl.DROP_AND_CREATE);
        //then
        assertThat(config.get(PersistenceUnitProperties.DDL_GENERATION)).isEqualTo("drop-and-create-tables");
    }

    @Test
    public void urlAfterDb() {
        //given

        //when
        config.db(JpaConfig.Db.DERBY_EMBEDDED);
        config.url("/home/david/temp/derbyDb", true);
        //then
        assertThat(config.get(PersistenceUnitProperties.JDBC_URL)).isEqualTo("jdbc:derby:/home/david/temp/derbyDb;" +
                "create=true");
    }

    @Test
    public void dbAfterUr() {
        //given

        //when
        config.url("/home/david/temp/derbyDb", true);
        config.db(JpaConfig.Db.DERBY_EMBEDDED);
        //then
        assertThat(config.get(PersistenceUnitProperties.JDBC_URL)).isEqualTo("jdbc:derby:/home/david/temp/derbyDb;" +
                "create=true");
    }
}