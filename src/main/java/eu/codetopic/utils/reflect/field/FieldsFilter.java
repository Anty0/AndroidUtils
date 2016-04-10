package eu.codetopic.utils.reflect.field;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.reflect.Field;

/**
 * Created by anty on 5.4.16.
 *
 * @author anty
 */
public interface FieldsFilter {

    Class<?> getStartClass();

    Class<?> getStopSuperClass();

    boolean addField(@Nullable Field field, @NonNull Class<?> fieldType);

    boolean searchFieldsInFieldClass(@Nullable Field field, @NonNull Class<?> fieldType);
}
