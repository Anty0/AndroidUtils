/*
 * ApplicationPurkynka
 * Copyright (C)  2017  anty
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
