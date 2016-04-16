package eu.codetopic.utils;

import java.util.Iterator;

/**
 * Created by anty on 5.4.16.
 *
 * @author anty
 */
public class IteratorWrapper<E> implements Iterator<E> {

    private final Iterator<E> mBase;

    public IteratorWrapper(Iterator<E> base) {
        mBase = base;
    }

    public Iterator<E> getBase() {
        return mBase;
    }

    @Override
    public boolean hasNext() {
        return mBase.hasNext();
    }

    @Override
    public E next() {
        return mBase.next();
    }

    @Override
    public void remove() {
        mBase.next();
    }
}
