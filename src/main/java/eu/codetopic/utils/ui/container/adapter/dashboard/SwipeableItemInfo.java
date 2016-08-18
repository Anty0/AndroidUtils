package eu.codetopic.utils.ui.container.adapter.dashboard;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

public interface SwipeableItemInfo {

    int UP = ItemTouchHelper.UP;

    int DOWN = ItemTouchHelper.DOWN;

    int LEFT = ItemTouchHelper.LEFT;

    int RIGHT = ItemTouchHelper.RIGHT;

    int START = ItemTouchHelper.START;

    int END = ItemTouchHelper.END;

    int getSwipeDirections(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder);

    void onSwiped(RecyclerView.ViewHolder viewHolder, int direction);
}
