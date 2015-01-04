package uk.q3c.krail.persist.jpa;

import org.apache.onami.persist.EntityManagerProvider;

import java.lang.annotation.Annotation;

/**
 * Implementation constructs a {@link GenericJpaDao} and initialises it with the {{@link EntityManagerProvider}}
 * identified by {@code annotatedBy}
 * <p>
 * <p>
 * Created by David Sowerby on 03/01/15.
 */
public interface GenericJpaDaoProvider {
    GenericJpaDao getDao(Class<? extends Annotation> annotatedBy);
}
