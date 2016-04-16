package eu.codetopic.utils.list.recyclerView.adapter;

import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import eu.codetopic.utils.IteratorWrapper;
import eu.codetopic.utils.Log;
import eu.codetopic.utils.Objects;

/**
 * Created by anty on 21.2.16.
 *
 * @author anty
 */
public abstract class ArrayEditRecyclerAdapter<T, VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> implements Iterable<T> {

    private static final String LOG_TAG = "ArrayEditRecyclerAdapter";

    private final Object mLock = new Object();
    private final ArrayList<T> mData = new ArrayList<>();

    public ArrayEditRecyclerAdapter() {

    }

    public ArrayEditRecyclerAdapter(Collection<? extends T> data) {
        synchronized (mLock) {
            mData.addAll(data);
        }
    }

    @SafeVarargs
    public ArrayEditRecyclerAdapter(T... data) {
        synchronized (mLock) {
            Collections.addAll(mData, data);
        }
    }

    public boolean isEmpty() {
        synchronized (mLock) {
            return mData.isEmpty();
        }
    }

    @Override
    public int getItemCount() {
        synchronized (mLock) {
            return mData.size();
        }
    }

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
    public Iterator<T> iterator() {
        synchronized (mLock) {
            return new IteratorWrapper<T>(mData.iterator()) {
                @Override
                public void remove() {
                    throw new UnsupportedOperationException("Not supported");
                }
            };
        }
    }

    public Editor<T> edit() {
        return new Editor<>(this);
    }

    public void postModifications(Collection<Modification<T>> modifications) {
        postModifications(null, modifications);
    }

    @UiThread
    public void postModifications(@Nullable Object editTag, Collection<Modification<T>> modifications) {
        synchronized (mLock) {
            //noinspection unchecked
            List<T> mDataBackup = (List<T>) mData.clone();
            for (Modification<T> modification : modifications)
                modification.modify(mData);

            for (Iterator<T> iterator = mDataBackup.iterator(); iterator.hasNext(); ) {
                T obj = iterator.next();
                if (!mData.contains(obj)) {
                    notifyItemRemoved(mDataBackup.indexOf(obj));
                    iterator.remove();
                }
            }

            for (int i = 0, mNewDataSize = mData.size(); i < mNewDataSize; i++) {
                T obj = mData.get(i);
                if (!mDataBackup.contains(obj)) {
                    mDataBackup.add(i, obj);
                    notifyItemInserted(i);
                    continue;
                }

                int oldIndex = mDataBackup.indexOf(obj);
                if (oldIndex != i) {
                    notifyItemMoved(oldIndex, i);
                    mDataBackup.remove(obj);
                    mDataBackup.add(i, obj);
                }
            }

            if (!Objects.equals(mDataBackup, mData)) {// TODO: 16.4.16 only if is in debug mode
                Error e = new InternalError("Detected problem in " + LOG_TAG + " while applying changes");
                Log.e(LOG_TAG, "apply", e);
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

        private static final String LOG_TAG = ArrayEditRecyclerAdapter.LOG_TAG + "$Editor";

        private final ArrayEditRecyclerAdapter<T, ?> mAdapter;
        private final ArrayList<Modification<T>> mModifications = new ArrayList<>();
        private Object mTag = null;

        private Editor(ArrayEditRecyclerAdapter<T, ?> adapter) {
            mAdapter = adapter;
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

        @UiThread
        public synchronized void apply() {
            mAdapter.postModifications(getTag(), mModifications);
            mModifications.clear();
        }
    }

}
