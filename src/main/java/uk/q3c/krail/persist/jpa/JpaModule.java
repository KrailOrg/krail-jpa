package uk.q3c.krail.persist.jpa;

import com.google.inject.AbstractModule;

/**
 * Created by David Sowerby on 01/01/15.
 */
public class JpaModule extends AbstractModule {
    @Override
    protected void configure() {

        bind(GenericJpaDao.class).to(DefaultGenericJpaDao.class);
        bind(GenericJpaDaoProvider.class).to(DefaultGenericJpaDaoProvider.class);
        bind(JpaContainerProvider.class).to(DefaultJpaContainerProvider.class);
    }


}
