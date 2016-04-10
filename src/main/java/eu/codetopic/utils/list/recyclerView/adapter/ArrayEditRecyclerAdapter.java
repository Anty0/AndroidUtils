package eu.codetopic.utils.list.recyclerView.adapter;

import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import eu.codetopic.utils.Log;
import eu.codetopic.utils.Objects;
import eu.codetopic.utils.exceptions.NonUiThreadUsedException;
import eu.codetopic.utils.thread.JobUtils;

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
    private Editor<T> mEditorInstance = null;

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
            final Iterator<T> base = mData.iterator();
            return new Iterator<T>() {
                @Override
                public boolean hasNext() {
                    return base.hasNext();
                }

                @Override
                public T next() {
                    return base.next();
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException("Not supported");
                }
            };
        }
    }

    public Editor<T> edit() {
        synchronized (mLock) {
            if (mEditorInstance != null) {
                throw new IllegalStateException("edit() called on " + LOG_TAG +
                        " before cancel() or apply() called on previous editor");
            }
            mEditorInstance = new Editor<>(this);
            return mEditorInstance;
        }
    }

    protected void onDataEdited(Object editorTag) {

    }

    @UiThread
    public static class Editor<T> {

        private static final String LOG_TAG = ArrayEditRecyclerAdapter.LOG_TAG + "$Editor";

        private final ArrayEditRecyclerAdapter<T, ?> mAdapter;
        private final ArrayList<T> mDataBackup;
        private final ArrayList<T> mNewData;
        private final Object mLock;
        private Object mTag = null;

        @SuppressWarnings("unchecked")
        private Editor(ArrayEditRecyclerAdapter<T, ?> adapter) {
            mAdapter = adapter;
            mLock = mAdapter.mLock;
            synchronized (mLock) {
                mNewData = (ArrayList<T>) mAdapter.mData.clone();
                mDataBackup = (ArrayList<T>) mAdapter.mData.clone();
            }
        }

        public Editor<T> setTag(Object tag) {
            this.mTag = tag;
            return this;
        }

        public Editor<T> add(T object) {
            synchronized (mLock) {
                mNewData.add(object);
            }
            return this;
        }

        public Editor<T> add(int index, T object) {
            synchronized (mLock) {
                mNewData.add(index, object);
            }
            return this;
        }

        public Editor<T> addAll(Collection<? extends T> collection) {
            synchronized (mLock) {
                mNewData.addAll(collection);
            }
            return this;
        }

        public Editor<T> addAll(int index, Collection<? extends T> collection) {
            synchronized (mLock) {
                mNewData.addAll(index, collection);
            }
            return this;
        }

        public T get(int index) {
            synchronized (mLock) {
                return mNewData.get(index);
            }
        }

        public Editor<T> clear() {
            synchronized (mLock) {
                mNewData.clear();
            }
            return this;
        }

        public int size() {
            synchronized (mLock) {
                return mNewData.size();
            }
        }

        public boolean isEmpty() {
            synchronized (mLock) {
                return mNewData.isEmpty();
            }
        }

        public boolean contains(Object object) {
            synchronized (mLock) {
                //noinspection SuspiciousMethodCalls
                return mNewData.contains(object);
            }
        }

        public int indexOf(Object object) {
            synchronized (mLock) {
                //noinspection SuspiciousMethodCalls
                return mNewData.indexOf(object);
            }
        }

        public Editor<T> remove(int index) {
            synchronized (mLock) {
                mNewData.remove(index);
            }
            return this;
        }

        public Editor<T> remove(Object object) {
            synchronized (mLock) {
                //noinspection SuspiciousMethodCalls
                mNewData.remove(object);
            }
            return this;
        }

        public Editor<T> set(int index, T object) {
            synchronized (mLock) {
                mNewData.set(index, object);
            }
            return this;
        }

        public Object[] toArray() {
            synchronized (mLock) {
                return mNewData.toArray();
            }
        }

        public <E> E[] toArray(E[] contents) {
            synchronized (mLock) {
                //noinspection SuspiciousToArrayCall
                return mNewData.toArray(contents);
            }
        }

        public boolean containsAll(Collection<?> collection) {
            synchronized (mLock) {
                return mNewData.containsAll(collection);
            }
        }

        public boolean removeAll(Collection<?> collection) {
            synchronized (mLock) {
                //noinspection SuspiciousMethodCalls
                return mNewData.removeAll(collection);
            }
        }

        public boolean retainAll(Collection<?> collection) {
            synchronized (mLock) {
                return mNewData.retainAll(collection);
            }
        }

        public boolean apply() {
            synchronized (mLock) {
                if (!JobUtils.isOnMainThread())
                    throw new NonUiThreadUsedException("apply() must be called on ui thread");
                if (!cancel()) return false;

                mAdapter.mData.clear();

                for (Iterator<T> iterator = mDataBackup.iterator(); iterator.hasNext(); ) {
                    T obj = iterator.next();
                    if (!mNewData.contains(obj)) {
                        mAdapter.notifyItemRemoved(mDataBackup.indexOf(obj));
                        iterator.remove();
                    }
                }

                for (int i = 0, mNewDataSize = mNewData.size(); i < mNewDataSize; i++) {
                    T obj = mNewData.get(i);
                    if (!mDataBackup.contains(obj)) {
                        mDataBackup.add(i, obj);
                        mAdapter.notifyItemInserted(i);
                        continue;
                    }

                    int oldIndex = mDataBackup.indexOf(obj);
                    if (oldIndex != i) {
                        mAdapter.notifyItemMoved(oldIndex, i);
                        mDataBackup.remove(obj);
                        mDataBackup.add(i, obj);
                    }
                }

                if (!Objects.equals(mDataBackup, mNewData)) {
                    Error e = new InternalError("Detected problem in " + LOG_TAG + " while applying changes");
                    Log.e(LOG_TAG, "apply", e);
                }
                mAdapter.mData.addAll(mNewData);
                mAdapter.onDataEdited(mTag);
                return true;
            }
        }

        public boolean cancel() {
            synchronized (mLock) {
                if (mAdapter.mEditorInstance != this) {
                    Exception e = new IllegalStateException("cancel() called on canceled editor");
                    Log.e(LOG_TAG, "cancel", e);
                    return false;
                }
                mAdapter.mEditorInstance = null;
                return true;
            }
        }
    }

}
