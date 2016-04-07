package cz.codetopic.utils.list.items.multiline;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import cz.codetopic.utils.R;
import cz.codetopic.utils.Utils;

/**
 * Created by anty on 15.2.16.
 *
 * @author anty
 */
public class MultilineItemUtils {

    public static ItemViewHolder generateItemViewHolderFor
            (View view, @LayoutRes Integer layoutResourceId) {
        Object tag = view.getTag();
        ItemViewHolder holder;
        if (tag instanceof ItemViewHolder) {
            holder = (ItemViewHolder) tag;
        } else {
            holder = new ItemViewHolder(view, layoutResourceId);
            view.setTag(holder);
        }
        return holder;
    }

    public static View applyMultilineItemOnView(Context context, ViewGroup parent,
                                                MultilineItem item, @LayoutRes Integer layoutResourceId) {
        View view = LayoutInflater.from(context).inflate(layoutResourceId, parent, false);
        applyMultilineItemOnView(generateItemViewHolderFor(view, layoutResourceId), item);
        return view;
    }

    public static void applyMultilineItemOnView(@NonNull View view, MultilineItem item) {
        applyMultilineItemOnView(generateItemViewHolderFor(view, null), item);
    }

    public static void applyMultilineItemOnView(ItemViewHolder holder, MultilineItem item) {
        if (holder.imgIcon != null) {
            initImageIcon(holder.imgIcon, item, MultilineItem.NO_POSITION);
        }
        if (holder.text1 != null && holder.text2 != null) {
            holder.text1.setText(item.getTitle(holder.text1.getContext(), MultilineItem.NO_POSITION));
            CharSequence text = item.getText(holder.text1.getContext(), MultilineItem.NO_POSITION);
            if (text == null) {
                if (item instanceof MultilinePaddingItem && !((MultilinePaddingItem) item)
                        .usePadding(holder.text1.getContext(), MultilineItem.NO_POSITION)) {
                    Utils.setPadding(holder.text1, 1, 1, 1, 1);
                } else Utils.setPadding(holder.text1, 1, 8, 1, 8);
                holder.text2.setVisibility(View.GONE);
            } else {
                Utils.setPadding(holder.text1, 1, 1, 1, 1);
                holder.text2.setVisibility(View.VISIBLE);
                holder.text2.setText(text);
            }
        } else {
            TextView textView = holder.text1 != null ? holder.text1 : holder.text2;
            if (textView != null) {
                textView.setText(holder.text1 != null ?
                        item.getTitle(textView.getContext(), MultilineItem.NO_POSITION) :
                        item.getText(textView.getContext(), MultilineItem.NO_POSITION));

                if (item instanceof MultilinePaddingItem && !((MultilinePaddingItem) item)
                        .usePadding(textView.getContext(), MultilineItem.NO_POSITION)) {
                    Utils.setPadding(textView, 1, 1, 1, 1);
                } else Utils.setPadding(textView, 1, 8, 1, 8);
            }
        }
    }

    private static void initImageIcon(ImageView imgIcon, MultilineItem item, int position) {
        if (item instanceof MultilineImageItem) {
            Bitmap bitmap = ((MultilineImageItem) item)
                    .getImageBitmap(imgIcon.getContext(), position);
            if (bitmap != null) {
                imgIcon.setVisibility(View.VISIBLE);
                imgIcon.setImageBitmap(bitmap);
                return;
            }
        }
        if (item instanceof MultilineResourceImageItem) {
            imgIcon.setVisibility(View.VISIBLE);
            imgIcon.setImageResource(((MultilineResourceImageItem) item)
                    .getImageResourceId(imgIcon.getContext(), position));
        } else imgIcon.setVisibility(View.GONE);
    }

    public static class ItemViewHolder {

        public final Integer layoutResourceId;
        public final ImageView imgIcon;
        public final TextView text1;
        public final TextView text2;

        ItemViewHolder(View view, @LayoutRes Integer layoutResourceId) {
            imgIcon = (ImageView) view.findViewById(R.id.image_view);
            text1 = (TextView) view.findViewById(R.id.text_view_title);
            text2 = (TextView) view.findViewById(R.id.text_view_text);
            this.layoutResourceId = layoutResourceId;
        }
    }

}
