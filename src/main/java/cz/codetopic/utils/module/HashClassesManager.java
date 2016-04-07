package cz.codetopic.utils.module;

import java.util.Collection;
import java.util.HashMap;

/**
 * Created by anty on 13.3.16.
 *
 * @author anty
 */
public class HashClassesManager<T> {

    private final HashMap<Class, T> mData = new HashMap<>();

    @SafeVarargs
    public HashClassesManager(T... data) {
        for (T t : data) mData.put(t.getClass(), t);
    }

    public Collection<T> get() {
        return mData.values();
    }

    public <D extends T> D find(Class<D> clazz) {
        //noinspection unchecked
        return (D) mData.get(clazz);
    }
}
