package uk.q3c.krail.persist.jpa;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import org.apache.onami.persist.EntityManagerProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({JpaModule.class, TestJpaUnitModule.class})
public class DefaultGenericJpaDaoProviderTest {

    @Inject
    DefaultGenericJpaDaoProvider provider;

    @Inject
    @Jpa1
    EntityManagerProvider emp1;

    @Test(expected = IllegalArgumentException.class)
    public void getDao_invalidAnnotation() {
        //given

        //when
        provider.getDao(Before.class);
        //then
    }

    @Test
    public void getDao() {
        //given

        //when
        GenericJpaDao dao = provider.getDao(Jpa1.class);
        //then
        assertThat(dao).isNotNull();

    }
}