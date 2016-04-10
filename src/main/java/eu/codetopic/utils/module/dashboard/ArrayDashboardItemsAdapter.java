package eu.codetopic.utils.module.dashboard;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by anty on 17.3.16.
 *
 * @author anty
 */
public abstract class ArrayDashboardItemsAdapter<T extends DashboardItem> extends DashboardItemsAdapter implements List<T> {

    private final ArrayList<T> mItems = new ArrayList<>();

    public ArrayDashboardItemsAdapter(Context context) {
        super(context);
    }

    @Override
    protected DashboardItem[] getItems() {
        return this.toArray(new DashboardItem[this.size()]);
    }

    @Override
    public void add(int location, T object) {
        mItems.add(location, object);
    }

    @Override
    public boolean add(T object) {
        return mItems.add(object);
    }

    @Override
    public boolean addAll(int location, @NonNull Collection<? extends T> collection) {
        return mItems.addAll(location, collection);
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends T> collection) {
        return mItems.addAll(collection);
    }

    @Override
    public void clear() {
        mItems.clear();
    }

    @Override
    public boolean contains(Object object) {
        return mItems.contains(object);
    }

    @Override
    public boolean containsAll(@NonNull Collection<?> collection) {
        return mItems.containsAll(collection);
    }

    @Override
    public T get(int location) {
        return mItems.get(location);
    }

    @Override
    public int indexOf(Object object) {
        return mItems.indexOf(object);
    }

    @Override
    public boolean isEmpty() {
        return mItems.isEmpty();
    }

    @NonNull
    @Override
    public Iterator<T> iterator() {
        return mItems.iterator();
    }

    @Override
    public int lastIndexOf(Object object) {
        return mItems.lastIndexOf(object);
    }

    @Override
    public ListIterator<T> listIterator() {
        return mItems.listIterator();
    }

    @NonNull
    @Override
    public ListIterator<T> listIterator(int location) {
        return mItems.listIterator(location);
    }

    @Override
    public T remove(int location) {
        return mItems.remove(location);
    }

    @Override
    public boolean remove(Object object) {
        return mItems.remove(object);
    }

    @Override
    public boolean removeAll(@NonNull Collection<?> collection) {
        return mItems.removeAll(collection);
    }

    @Override
    public boolean retainAll(@NonNull Collection<?> collection) {
        return mItems.retainAll(collection);
    }

    @Override
    public T set(int location, T object) {
        return mItems.set(location, object);
    }

    @Override
    public int size() {
        return mItems.size();
    }

    @NonNull
    @Override
    public List<T> subList(int start, int end) {
        return mItems.subList(start, end);
    }

    @NonNull
    @Override
    public Object[] toArray() {
        return mItems.toArray();
    }

    @NonNull
    @Override
    @SuppressWarnings("SuspiciousToArrayCall")
    public <T1> T1[] toArray(@NonNull T1[] array) {
        return mItems.toArray(array);
    }
}
