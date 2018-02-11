/*
 * utils
 * Copyright (C)   2018  anty
 *
 * This program is free  software: you can redistribute it and/or modify
 * it under the terms  of the GNU General Public License as published by
 * the Free Software  Foundation, either version 3 of the License, or
 * (at your option) any  later version.
 *
 * This program is distributed in the hope that it  will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied  warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.   See the
 * GNU General Public License for more details.
 *
 * You  should have received a copy of the GNU General Public License
 * along  with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.codetopic.utils.debug.items.notify

import android.content.Context
import eu.codetopic.java.utils.debug.DebugMode
import eu.codetopic.java.utils.log.Log
import eu.codetopic.utils.R
import eu.codetopic.utils.notifications.manager.NotifyManager
import eu.codetopic.utils.notifications.manager.create.MultiNotificationBuilder
import eu.codetopic.utils.notifications.manager.create.MultiNotificationBuilder.Companion.requestSuspendShowAll
import eu.codetopic.utils.notifications.manager.create.NotificationBuilder
import eu.codetopic.utils.notifications.manager.create.NotificationBuilder.Companion.requestSuspendShow
import eu.codetopic.utils.ui.container.items.custom.CardViewWrapper
import eu.codetopic.utils.ui.container.items.custom.CustomItem
import eu.codetopic.utils.ui.container.items.custom.CustomItemWrapper
import kotlinx.android.synthetic.main.item_debug_notify.*
import org.jetbrains.anko.longToast
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * @author anty
 */
class NotifyManagerDebugItem : CustomItem() {

    companion object {

        private const val LOG_TAG = "NotifyManagerDebugItem"

        fun initialize(context: Context) {
            DebugMode.ifEnabled {
                NotifyManager.installGroup(context, NotifyManagerDebugGroup())
                NotifyManager.installChannel(context, NotifyManagerDebugChannel())
            }
        }

        private fun assertInitialized() {
            if (NotifyManager.hasGroup(NotifyManagerDebugGroup.ID) &&
                    NotifyManager.hasChannel(NotifyManagerDebugChannel.ID)) return

            throw IllegalStateException("$LOG_TAG is not initialized")
        }
    }

    override fun onBindViewHolder(holder: CustomItem.ViewHolder, itemPosition: Int) {
        holder.butRefresh.onClick {
            try {
                assertInitialized()
                NotifyManager.requestSuspendRefresh(holder.context)
                holder.context.longToast(R.string.debug_item_notify_toast_refresh_done)
            } catch (e: Exception) {
                Log.w(LOG_TAG, "butRefresh.onClick()", e)
            }
        }

        holder.butShowOne.onClick {
            try {
                assertInitialized()
                NotificationBuilder.create(
                        groupId = NotifyManagerDebugGroup.ID,
                        channelId = NotifyManagerDebugChannel.ID) {
                    persistent = true
                    refreshable = true
                }.requestSuspendShow(holder.context)
                holder.context.longToast(R.string.debug_item_notify_toast_show_one_done)
            } catch (e: Exception) {
                Log.w(LOG_TAG, "butShowOne.onClick()", e)
            }
        }

        holder.butShowMulti.onClick {
            try {
                assertInitialized()
                MultiNotificationBuilder.create(
                        groupId = NotifyManagerDebugGroup.ID,
                        channelId = NotifyManagerDebugChannel.ID) {
                    persistent = true
                    refreshable = true
                }.requestSuspendShowAll(holder.context)
                holder.context.longToast(R.string.debug_item_notify_toast_show_multi_done)
            } catch (e: Exception) {
                Log.w(LOG_TAG, "butShowMulti.onClick()", e)
            }
        }

        holder.butCancelAll.onClick {
            try {
                assertInitialized()
                NotifyManager.requestSuspendCancelAll(
                        context = holder.context,
                        groupId = NotifyManagerDebugGroup.ID,
                        channelId = NotifyManagerDebugChannel.ID
                )
                holder.context.longToast(R.string.debug_item_notify_toast_cancel_all_done)
            } catch (e: Exception) {
                Log.w(LOG_TAG, "butCancelAll.onClick()", e)
            }
        }
    }

    override fun getItemLayoutResId(context: Context) = R.layout.item_debug_notify

    override fun getWrappers(context: Context): Array<CustomItemWrapper> = CardViewWrapper.WRAPPER
}