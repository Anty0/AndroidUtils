package eu.codetopic.utils.container.recyclerView.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collection;

import eu.codetopic.utils.R;
import eu.codetopic.utils.container.items.cardview.CardItem;

/**
 * Created by anty on 21.2.16.
 *
 * @author anty
 */
public class CardRecyclerAdapter<T extends CardItem> extends RecyclerAdapter<T,
        CardRecyclerAdapter.CardViewViewHolder<T>> {

    private static final int CARD_VIEW_LAYOUT_ID = R.layout.card_view_base;

    private final Context mContext;

    public CardRecyclerAdapter(Context context) {
        super(CARD_VIEW_LAYOUT_ID);
        mContext = context;
    }

    public CardRecyclerAdapter(Context context, Collection<? extends T> data) {
        super(CARD_VIEW_LAYOUT_ID, data);
        mContext = context;
    }

    @SafeVarargs
    public CardRecyclerAdapter(Context context, T... data) {
        super(CARD_VIEW_LAYOUT_ID, data);
        mContext = context;
    }

    public Context getContext() {
        return mContext;
    }

    @Override
    protected CardViewViewHolder<T> getViewHolderInstance(View view) {
        return new CardViewViewHolder<>(mContext, view);
    }

    protected static class CardViewViewHolder<T extends CardItem> extends RecyclerAdapter.ItemViewHolder<T> {

        private static final String LOG_TAG = "CardViewViewHolder";

        private final Context mContext;

        public CardViewViewHolder(Context context, View itemView) {
            super(itemView);
            mContext = context;
        }

        public Context getContext() {
            return mContext;
        }

        @Override
        protected void onBindViewHolder(final T item, final int position) {
            ViewGroup parent = (ViewGroup) itemView.findViewById(R.id.card_view);

            Object tag = parent.getTag();
            int layoutId = item.getLayoutResId(mContext, position);
            View view = null;
            if (tag != null && layoutId != CardItem.NO_LAYOUT_RES_ID && (int) tag == layoutId)
                view = parent.getChildAt(0);

            parent.removeAllViews();
            view = item.getViewBase(mContext, parent, view, position);
            parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    item.onClick(mContext, v, position);
                }
            });
            parent.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return item.onLongClick(mContext, v, position);
                }
            });

            // TODO: 25.3.16 find way to check if item gives true layout id
            /*if (layoutId != CardItem.NO_LAYOUT_RES_ID && view.getId() != layoutId) {
                Exception e = new WrongIdException(item.getClass().getName() + " provides wrong layout id!");
                Log.e(LOG_TAG, "onBindViewHolder", e);
            }*/
            parent.addView(view);
        }
    }
}
