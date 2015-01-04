package uk.q3c.krail.persist.jpa;

import com.google.common.base.Preconditions;
import com.google.inject.BindingAnnotation;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import org.apache.onami.persist.EntityManagerProvider;

import java.lang.annotation.Annotation;

/**
 * Created by David Sowerby on 03/01/15.
 */
public class DefaultGenericJpaDaoProvider implements GenericJpaDaoProvider {

    private Injector injector;

    @Inject
    protected DefaultGenericJpaDaoProvider(Injector injector) {
        this.injector = injector;
    }

    @Override
    public GenericJpaDao getDao(Class<? extends Annotation> annotatedBy) {
        Preconditions.checkArgument(annotatedBy.isAnnotationPresent(BindingAnnotation.class), "Annotation must be a "
                + "@BindingAnnotation");
        Key<EntityManagerProvider> key = Key.get(EntityManagerProvider.class, annotatedBy);
        EntityManagerProvider entityManagerProvider = injector.getInstance(key);
        GenericJpaDao dao = injector.getInstance(GenericJpaDao.class);
        dao.setEntityManagerProvider(entityManagerProvider);
        return dao;
    }


}
