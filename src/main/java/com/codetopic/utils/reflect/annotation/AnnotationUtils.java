package com.codetopic.utils.reflect.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Map;

public class AnnotationUtils {

    private static final String LOG_TAG = "AnnotationUtils";

    public static void replaceAnnotation(Class<?> target, Annotation annotation)
            throws NoSuchFieldException, IllegalAccessException {

        Field annotationsField = Class.class.getDeclaredField("annotations");
        annotationsField.setAccessible(true);
        //noinspection unchecked
        ((Map<Class<? extends Annotation>, Annotation>) annotationsField.get(target))
                .put(annotation.annotationType(), annotation);
    }

}
