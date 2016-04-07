package cz.codetopic.utils.reflect.field;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

import cz.codetopic.utils.IteratorWrapper;
import cz.codetopic.utils.Utils;

/**
 * Created by anty on 5.4.16.
 *
 * @author anty
 */
public class FoundField implements Iterable<FoundField> {

    private final boolean hideFromIterator;
    private final Field field;
    private final Class<?> genericFieldType;
    private final FoundField[] contentFields;
    private FoundField parent;

    FoundField(boolean hideFromIterator, @Nullable Field field, @NonNull Class<?> genericFieldType,
               @NonNull FoundField[] contentFields) {

        this.hideFromIterator = hideFromIterator;
        this.field = field;
        if (this.field != null) this.field.setAccessible(true);
        this.genericFieldType = genericFieldType;
        this.contentFields = contentFields;
        for (FoundField contentField : this.contentFields)
            contentField.parent = this;
    }

    public boolean isHidden() {
        return hideFromIterator;
    }

    public String getName() {
        Field field = getField();
        return (isHidden() ? "hidden" : "visible") + " " + (field == null ? "UnknownField" : field.getDeclaringClass().getName()
                + " -> " + field.getName()) + " -> " + getGenericFieldType().getName();
    }

    @Nullable
    public FoundField getParent() {
        return parent;
    }

    @Nullable
    public Field getField() {
        return field;
    }

    @NonNull
    public Class<?> getGenericFieldType() {
        return genericFieldType;
    }

    public boolean containsDeclaringClass(Class<?> toCheck) {
        Field field = getField();
        FoundField parentField = getParent();
        return (field != null && field.getDeclaringClass().equals(toCheck)) ||
                (parentField != null && parentField.containsDeclaringClass(toCheck));
    }

    public Object getFieldObjectFrom(@NonNull Object baseParentObject)
            throws ClassNotFoundException, IllegalAccessException {
        Field field = getField();
        if (field == null) throw new ClassNotFoundException();

        if (!field.getDeclaringClass().isAssignableFrom(baseParentObject.getClass())) {
            FoundField parent = getParent();
            if (parent == null) throw new ClassNotFoundException();
            baseParentObject = parent.getFieldObjectFrom(baseParentObject);
        }

        return resolveArray(field, baseParentObject);
    }

    public String getFieldObjectAsString(@NonNull Object baseParentObject) {
        try {
            return getFieldObjectFrom(baseParentObject).toString();
        } catch (Throwable t) {
            return "unknown";
        }
    }

    private Object resolveArray(Field field, Object toResolve) throws IllegalAccessException, ClassNotFoundException {
        Class toResolveClass = toResolve.getClass();
        if (!toResolveClass.isArray()) return field.get(toResolve);
        Object toReturn = Array.newInstance(field.getType(), Array.getLength(toResolve));
        for (int i = 0, len = Array.getLength(toReturn); i < len; i++) {
            Array.set(toReturn, i, getFieldObjectFrom(Array.get(toResolve, i)));
        }
        return toReturn;
    }

    public FoundField[] getContentFields() {
        return contentFields;
    }

    public String hierarchyToString() {
        return hierarchyToString(true, null);
    }

    public String hierarchyToString(boolean showHidden) {
        return hierarchyToString(showHidden, null);
    }

    public String hierarchyToString(@Nullable Object fieldsObject) {
        return hierarchyToString(true, fieldsObject);
    }

    public String hierarchyToString(boolean showHidden, @Nullable Object fieldsObject) {
        StringBuilder sb = new StringBuilder(getName());
        if (fieldsObject != null) sb.append(" -> ").append(getFieldObjectAsString(fieldsObject));

        FoundField[] content = getContentFields();
        if (content.length > 0) {
            sb.append(" {");
            StringBuilder csb = new StringBuilder();
            for (FoundField contentField : content)
                if (showHidden || !contentField.isHidden() || contentField.getContentFields().length > 0)
                    csb.append("\n").append(contentField.hierarchyToString(fieldsObject));
            sb.append(Utils.addBeforeEveryLine(csb.toString(), "    "));
            sb.append("\n}");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        Field field = getField();
        StringBuilder sb = new StringBuilder(field == null ? "UnknownField" : field.getName());

        FoundField[] content = getContentFields();
        if (content.length > 0) sb.append(": ").append(Arrays.toString(content));
        return sb.toString();
    }

    @Override
    public Iterator<FoundField> iterator() {
        return new HideSupportFoundsFieldsIterator(Arrays
                .asList(getContentFields()).iterator());
    }

    private static class HideSupportFoundsFieldsIterator extends FoundFieldsIterator {

        private FoundField nextBak = null;

        public HideSupportFoundsFieldsIterator(Iterator<FoundField> base) {
            super(base);
            nextBak = getNextUsable();
        }

        @Override
        public boolean hasNext() {
            return nextBak != null;
        }

        @Override
        public FoundField next() {
            if (nextBak == null) throw new NoSuchElementException();
            FoundField next = nextBak;
            nextBak = getNextUsable();
            return next;
        }

        private FoundField getNextUsable() {
            while (super.hasNext()) {
                FoundField next = super.next();
                if (!next.isHidden()) return next;
            }
            return null;
        }
    }

    private static class FoundFieldsIterator extends IteratorWrapper<FoundField> {

        private Iterator<FoundField> parent = null;

        public FoundFieldsIterator(Iterator<FoundField> base) {
            super(base);
        }

        @Override
        public boolean hasNext() {
            if (parent != null) {
                if (parent.hasNext()) return true;
                parent = null;
            }
            return super.hasNext();
        }

        @Override
        public FoundField next() {
            if (parent != null) {
                if (parent.hasNext()) return parent.next();
                parent = null;
            }

            FoundField next = super.next();
            parent = next.iterator();
            return next;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
