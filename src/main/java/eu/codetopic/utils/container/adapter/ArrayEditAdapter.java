package eu.codetopic.utils.container.adapter;

import android.support.annotation.Nullable;
import android.support.annotation.UiThread;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import eu.codetopic.utils.IteratorWrapper;
import eu.codetopic.utils.Log;
import eu.codetopic.utils.Objects;

public abstract class ArrayEditAdapter<T, VH extends UniversalAdapter.ViewHolder>
        extends UniversalAdapter<VH> implements Iterable<T> {

    private static final String LOG_TAG = "ArrayEditAdapter";

    private final Object mLock = new Object();
    private final ArrayList<T> mData = new ArrayList<>();

    public ArrayEditAdapter() {
    }

    public ArrayEditAdapter(Collection<? extends T> data) {
        synchronized (mLock) {
            mData.addAll(data);
        }
    }

    @SafeVarargs
    public ArrayEditAdapter(T... data) {
        synchronized (mLock) {
            Collections.addAll(mData, data);
        }
    }

    @Override
    public int getItemCount() {
        synchronized (mLock) {
            return mData.size();
        }
    }

    @Override
    public T getItem(int position) {
        synchronized (mLock) {
            return mData.get(position);
        }
    }

    public T[] getItems(T[] contents) {
        synchronized (mLock) {
            return mData.toArray(contents);
        }
    }

    public ArrayList<T> getItems() {
        synchronized (mLock) {
            //noinspection unchecked
            return (ArrayList<T>) mData.clone();
        }
    }

    public int getItemPosition(T item) {
        synchronized (mLock) {
            return mData.indexOf(item);
        }
    }

    @Override
    public boolean isEmpty() {
        synchronized (mLock) {
            return mData.isEmpty();
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new IteratorWrapper<T>(getItems().iterator()) {
            @Override
            public void remove() {
                throw new UnsupportedOperationException("Not supported");
            }
        };
    }

    public Editor<T> edit() {
        return new Editor<>(this);
    }

    @UiThread
    public void postModifications(Collection<Modification<T>> modifications,
                                  @Nullable Collection<T> contentModifiedItems) {

        postModifications(null, modifications, contentModifiedItems);
    }

    @UiThread
    public void postModifications(@Nullable Object editTag, Collection<Modification<T>> modifications,
                                  @Nullable Collection<T> contentModifiedItems) {

        Base base = getBase();
        synchronized (mLock) {
            if (base.hasOnlySimpleDataChangedReporting()) {
                for (Modification<T> modification : modifications)
                    modification.modify(mData);

                base.notifyDataSetChanged();
            } else {
                //noinspection unchecked
                List<T> dataBackup = (List<T>) mData.clone();
                for (Modification<T> modification : modifications)
                    modification.modify(mData);

                for (Iterator<T> iterator = dataBackup.iterator(); iterator.hasNext(); ) {
                    T obj = iterator.next();
                    if (!mData.contains(obj)) {
                        base.notifyItemRemoved(dataBackup.indexOf(obj));
                        iterator.remove();
                    }
                }

                for (int i = 0, size = mData.size(); i < size; i++) {
                    T obj = mData.get(i);
                    if (!dataBackup.contains(obj)) {
                        dataBackup.add(i, obj);
                        base.notifyItemInserted(i);
                        continue;
                    }

                    int oldIndex = dataBackup.indexOf(obj);
                    if (oldIndex != i) {
                        base.notifyItemMoved(oldIndex, i);
                        dataBackup.remove(obj);
                        dataBackup.add(i, obj);
                    }
                }

                if (contentModifiedItems != null) {
                    for (T item : contentModifiedItems) {
                        int index = mData.indexOf(item);
                        if (index != -1) base.notifyItemChanged(index);
                    }
                } else base.notifyItemRangeChanged(0, mData.size());

                if (!Objects.equals(dataBackup, mData)) Log.e(LOG_TAG, "apply",
                        new InternalError("Detected problem in " + LOG_TAG + " while applying changes" +
                                " -> !dataBackup.equals(newData)"));
            }

            onDataEdited(editTag);
        }
    }

    protected void onDataEdited(@Nullable Object editTag) {

    }

    public interface Modification<T> {
        void modify(List<T> toModify);
    }

    public static class Editor<T> {

        private static final String LOG_TAG = ArrayEditAdapter.LOG_TAG + "$Editor";

        private final WeakReference<? extends ArrayEditAdapter<T, ?>> mAdapterReference;
        private final ArrayList<Modification<T>> mModifications = new ArrayList<>();
        private final ArrayList<T> mChangedItems = new ArrayList<>();
        private boolean mAllItemsChanged = false;
        private Object mTag = null;

        protected Editor(ArrayEditAdapter<T, ?> adapter) {
            mAdapterReference = new WeakReference<>(adapter);
        }

        @UiThread
        @Nullable
        public <A extends ArrayEditAdapter<T, ?>> A getAdapter() {
            //noinspection unchecked
            return (A) mAdapterReference.get();
        }

        public synchronized Object getTag() {
            return mTag;
        }

        public synchronized Editor<T> setTag(@Nullable Object tag) {
            this.mTag = tag;
            return this;
        }

        public synchronized Editor<T> post(Modification<T> modification) {
            mModifications.add(modification);
            return this;
        }

        public Editor<T> add(final T object) {
            return post(new Modification<T>() {
                @Override
                public void modify(List<T> toModify) {
                    toModify.add(object);
                }
            });
        }

        public Editor<T> add(final int index, final T object) {
            return post(new Modification<T>() {
                @Override
                public void modify(List<T> toModify) {
                    toModify.add(index, object);
                }
            });
        }

        public Editor<T> addAll(final Collection<? extends T> collection) {
            return post(new Modification<T>() {
                @Override
                public void modify(List<T> toModify) {
                    toModify.addAll(collection);
                }
            });
        }

        public Editor<T> addAll(final int index, final Collection<? extends T> collection) {
            return post(new Modification<T>() {
                @Override
                public void modify(List<T> toModify) {
                    toModify.addAll(index, collection);
                }
            });
        }

        public Editor<T> clear() {
            return post(new Modification<T>() {
                @Override
                public void modify(List<T> toModify) {
                    toModify.clear();
                }
            });
        }

        public Editor<T> remove(final int index) {
            return post(new Modification<T>() {
                @Override
                public void modify(List<T> toModify) {
                    toModify.remove(index);
                }
            });
        }

        public Editor<T> remove(final Object object) {
            return post(new Modification<T>() {
                @Override
                public void modify(List<T> toModify) {
                    //noinspection SuspiciousMethodCalls
                    toModify.remove(object);
                }
            });
        }

        public Editor<T> set(final int index, final T object) {
            return post(new Modification<T>() {
                @Override
                public void modify(List<T> toModify) {
                    toModify.set(index, object);
                }
            });
        }

        public Editor<T> removeAll(final Collection<?> collection) {
            return post(new Modification<T>() {
                @Override
                public void modify(List<T> toModify) {
                    //noinspection SuspiciousMethodCalls
                    toModify.removeAll(collection);
                }
            });
        }

        public Editor<T> retainAll(final Collection<?> collection) {
            return post(new Modification<T>() {
                @Override
                public void modify(List<T> toModify) {
                    toModify.retainAll(collection);
                }
            });
        }

        @SafeVarargs
        public synchronized final Editor<T> notifyItemsChanged(T... items) {
            Collections.addAll(mChangedItems, items);
            return this;
        }

        public synchronized Editor<T> notifyItemsChanged(Collection<T> items) {
            mChangedItems.addAll(items);
            return this;
        }

        public synchronized Editor<T> notifyAllItemsChanged() {
            mAllItemsChanged = true;
            return this;
        }

        @UiThread
        public synchronized boolean apply() {
            ArrayEditAdapter<T, ?> adapter = getAdapter();
            if (adapter != null) {
                adapter.postModifications(getTag(), mModifications,
                        mAllItemsChanged ? null : mChangedItems);
                mModifications.clear();
                mChangedItems.clear();
                mAllItemsChanged = false;
                return true;
            }
            return false;
        }
    }
}
