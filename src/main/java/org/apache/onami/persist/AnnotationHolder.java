/*
 * Copyright (c) 2015. David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package org.apache.onami.persist;

import java.lang.annotation.Annotation;

/**
 * Object to hold the annotation of a persistence unit. may contain {@code null}.
 */
class AnnotationHolder {

    /**
     * The annotation under which to bind current persistence unit.
     */
    private final Class<? extends Annotation> annotation;

    /**
     * Constructor.
     *
     * @param annotation
     *         the annotation under which to bind the annotations. May be {@code null}.
     */
    AnnotationHolder(Class<? extends Annotation> annotation) {
        this.annotation = annotation;
    }

    /**
     * @return the annotation for the current persistence unit. May return {@code null}.
     */
    Class<? extends Annotation> getAnnotation() {
        return annotation;
    }

}
