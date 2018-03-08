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

package eu.codetopic.utils.notifications.manager

import android.app.Notification
import android.content.Context
import android.os.Bundle
import android.support.annotation.MainThread
import eu.codetopic.java.utils.alsoIf
import eu.codetopic.java.utils.log.Log
import eu.codetopic.utils.getKSerializable
import eu.codetopic.utils.notifications.manager.create.MultiNotificationBuilder
import eu.codetopic.utils.notifications.manager.create.NotificationBuilder
import eu.codetopic.utils.notifications.manager.data.NotifyId
import eu.codetopic.utils.notifications.manager.data.NotifyIdSerializer
import eu.codetopic.utils.notifications.manager.receiver.*
import eu.codetopic.utils.notifications.manager.save.NotifyData
import eu.codetopic.utils.notifications.manager.util.NotifyChannel
import eu.codetopic.utils.notifications.manager.util.NotifyGroup

/**
 * @author anty
 */
object NotifyManager {

    private const val LOG_TAG = "NotifyManager"

    //--------------------------------------------------------------------------

    @MainThread
    fun installGroup(context: Context, group: NotifyGroup) =
            NotifyClassifier.install(context, group)

    @MainThread
    fun installChannel(context: Context, channel: NotifyChannel) =
            NotifyClassifier.install(context, channel)

    @MainThread
    fun uninstallGroup(context: Context, groupId: String): NotifyGroup =
            NotifyClassifier.uninstallGroup(context, groupId)
                    .alsoIf({ NotifyBase.isInitialized }) { Notifier.cleanup(context) }

    @MainThread
    fun uninstallChannel(context: Context, channelId: String): NotifyChannel =
            NotifyClassifier.uninstallChannel(context, channelId)
                    .alsoIf({ NotifyBase.isInitialized }) { Notifier.cleanup(context) }

    @MainThread
    fun reinstallGroup(context: Context, groupId: String) =
            NotifyClassifier.reinstallGroup(context, groupId)
                    .alsoIf({ NotifyBase.isInitialized }) { Notifier.refresh(context) }

    @MainThread
    fun reinstallChannel(context: Context, channelId: String) =
            NotifyClassifier.reinstallChannel(context, channelId)
                    .alsoIf({ NotifyBase.isInitialized }) { Notifier.refresh(context) }

    @MainThread
    fun replaceGroup(context: Context, group: NotifyGroup) =
            NotifyClassifier.replace(context, group)
                    .alsoIf({ NotifyBase.isInitialized }) { Notifier.refresh(context) }

    @MainThread
    fun replaceChannel(context: Context, channel: NotifyChannel) =
            NotifyClassifier.replace(context, channel)
                    .alsoIf({ NotifyBase.isInitialized }) { Notifier.refresh(context) }

    @MainThread
    fun installGroups(context: Context, vararg groups: NotifyGroup) =
            groups.forEach { NotifyClassifier.install(context, it) }

    @MainThread
    fun installChannels(context: Context, vararg channels: NotifyChannel) =
            channels.forEach { NotifyClassifier.install(context, it) }

    @MainThread
    fun uninstallGroups(context: Context, vararg groupIds: String) =
            groupIds.forEach { NotifyClassifier.uninstallGroup(context, it) }

    @MainThread
    fun uninstallChannels(context: Context, vararg channelIds: String) =
            channelIds.forEach { NotifyClassifier.uninstallChannel(context, it) }

    @MainThread
    fun reinstallGroups(context: Context, vararg groupIds: String) =
            groupIds.forEach { NotifyClassifier.reinstallGroup(context, it) }
                    .alsoIf({ NotifyBase.isInitialized }) { Notifier.refresh(context) }

    @MainThread
    fun reinstallChannels(context: Context, vararg channelIds: String) =
            channelIds.forEach { NotifyClassifier.reinstallChannel(context, it) }
                    .alsoIf({ NotifyBase.isInitialized }) { Notifier.refresh(context) }

    @MainThread
    fun replaceGroups(context: Context, vararg groups: NotifyGroup) =
            groups.forEach { NotifyClassifier.replace(context, it) }
                    .alsoIf({ NotifyBase.isInitialized }) { Notifier.refresh(context) }

    @MainThread
    fun replaceChannels(context: Context, vararg channels: NotifyChannel) =
            channels.forEach { NotifyClassifier.replace(context, it) }
                    .alsoIf({ NotifyBase.isInitialized }) { Notifier.refresh(context) }

    fun findGroup(groupId: String): NotifyGroup =
            NotifyClassifier.findGroup(groupId)

    fun findChannel(channelId: String): NotifyChannel =
            NotifyClassifier.findChannel(channelId)

    fun hasGroup(groupId: String): Boolean =
            NotifyClassifier.hasGroup(groupId)

    fun hasChannel(channelId: String): Boolean =
            NotifyClassifier.hasChannel(channelId)

    fun isChannelEnabled(groupId: String?, channelId: String): Boolean =
            ChannelsEnabler.isChannelEnabled(groupId, channelId)

    fun isChannelEnabledInSystem(context: Context, groupId: String, channelId: String): Boolean =
            ChannelsEnabler.isChannelEnabledInSystem(context, groupId, channelId)

    //--------------------------------------------------------------------------

    fun getOnChangeBroadcastAction(): String {
        NotifyBase.assertUsable()
        return NotifyData.instance.broadcastActionChanged
    }

    fun isMultiProcessBroadcastBlockerDetected(): Boolean {
        NotifyBase.assertUsable()
        return NotifyData.instance.isBroadcastRejectionAtWarnLevel()
    }

    @MainThread
    fun getDataOf(notifyId: NotifyId): Bundle {
        try {
            NotifyBase.assertUsable()
            return NotifyData.instance[notifyId]
                    ?: throw IllegalArgumentException("Id doesn't exists: $notifyId")
        } catch (e: Exception) {
            Log.e(LOG_TAG, "getDataOf(notifyId=$notifyId)", e)
        }
        return Bundle.EMPTY
    }

    @MainThread
    fun getAllData(groupId: String? = null, channelId: String? = null): Map<out NotifyId, Bundle> {
        try {
            NotifyBase.assertUsable()
            return NotifyData.instance.getAll(groupId, channelId)
        } catch (e: Exception) {
            Log.e(LOG_TAG, "getAllData(groupId=$groupId, channelId=$channelId)", e)
        }
        return emptyMap()
    }

    @MainThread
    fun build(context: Context, builder: NotificationBuilder,
              hasTag: Boolean): Pair<NotifyId, Notification> =
            Notifier.build(context, builder, hasTag)

    @MainThread
    fun buildOrNull(context: Context, builder: NotificationBuilder,
                    hasTag: Boolean): Pair<NotifyId, Notification>? {
        try {
            return Notifier.build(context, builder, hasTag)
        } catch (e: Exception) {
            Log.e(LOG_TAG, "buildOrNull(builder=$builder, hasTag=$hasTag)", e)
        }
        return null
    }

    //--------------------------------------------------------------------------

    @MainThread
    fun refresh(context: Context, optimise: Boolean = true) {
        try {
            NotifyBase.assertUsable()
            if (optimise && NotifyBase.isInitialized) Notifier.refresh(context)
            else context.sendBroadcast(RqRefreshReceiver.getStartIntent(context))
        } catch (e: Exception) {
            Log.e(LOG_TAG, "refresh(optimise=$optimise)", e)
        }
    }

    @MainThread
    fun notify(context: Context, builder: NotificationBuilder, optimise: Boolean = true) {
        try {
            NotifyBase.assertUsable()
            if (optimise && NotifyBase.isInitialized) Notifier.notify(context, builder)
            else context.sendBroadcast(
                    RqNotifyReceiver.getStartIntent(context, builder)
            )
        } catch (e: Exception) {
            Log.e(LOG_TAG, "notify(builder=$builder, optimise=$optimise)", e)
        }
    }

    @MainThread
    fun notifyAll(context: Context, builder: MultiNotificationBuilder, optimise: Boolean = true) {
        try {
            NotifyBase.assertUsable()
            if (optimise && NotifyBase.isInitialized) Notifier.notifyAll(context, builder)
            else context.sendBroadcast(
                    RqNotifyAllReceiver.getStartIntent(context, builder)
            )
        } catch (e: Exception) {
            Log.e(LOG_TAG, "notifyAll(builder=$builder, optimise=$optimise)", e)
        }
    }

    @MainThread
    fun cancel(context: Context, notifyId: NotifyId, optimise: Boolean = true) {
        try {
            NotifyBase.assertUsable()
            if (optimise && NotifyBase.isInitialized) Notifier.cancel(context, notifyId)
            else context.sendBroadcast(RqCancelReceiver.getStartIntent(context, notifyId))
        } catch (e: Exception) {
            Log.e(LOG_TAG, "cancel(notifyId=$notifyId, optimise=$optimise)", e)
        }
    }

    @MainThread
    fun cancelAll(context: Context, notifyIds: Collection<NotifyId>, optimise: Boolean = true) {
        try {
            NotifyBase.assertUsable()
            if (optimise && NotifyBase.isInitialized) Notifier.cancelAll(context, notifyIds)
            else context.sendBroadcast(RqCancelAllIdsReceiver.getStartIntent(context, notifyIds))
        } catch (e: Exception) {
            Log.e(LOG_TAG, "cancelAll(notifyIds=$notifyIds, optimise=$optimise)", e)
        }
    }

    @MainThread
    fun cancelAll(context: Context, groupId: String? = null,
                  channelId: String? = null, optimise: Boolean = true) {
        try {
            NotifyBase.assertUsable()
            if (optimise && NotifyBase.isInitialized) Notifier.cancelAll(context, groupId, channelId)
            else context.sendBroadcast(
                    RqCancelAllReceiver.getStartIntent(context, groupId, channelId)
            )
        } catch (e: Exception) {
            Log.e(LOG_TAG, "cancelAll(groupId=$groupId," +
                    " channelId=$channelId, optimise=$optimise)", e)
        }
    }

    @MainThread
    fun setChannelEnabled(context: Context, groupId: String?, channelId: String,
                          enable: Boolean, optimise: Boolean = true) {
        try {
            NotifyBase.assertUsable()
            if (optimise && NotifyBase.isInitialized)
                ChannelsEnabler.setChannelEnabled(context, groupId, channelId, enable)
            else context.sendBroadcast(
                    RqSetEnabledReceiver.getStartIntent(context, groupId, channelId, enable)
            )
        } catch (e: Exception) {
            Log.e(LOG_TAG, "setChannelEnabled(groupId=$groupId, channelId=$channelId," +
                    " enable=$enable, optimise=$optimise)", e)
        }
    }

    //--------------------------------------------------------------------------

    @MainThread
    suspend fun sRefresh(context: Context, optimise: Boolean = true) {
        try {
            NotifyBase.assertUsable()
            return if (optimise && NotifyBase.isInitialized) Notifier.refresh(context)
            else sendSuspendRequest(
                    context = context,
                    name = "RqRefreshReceiver",
                    intent = RqRefreshReceiver.getStartIntent(context),
                    resultExtractor = { Unit }
            )
        } catch (e: Exception) {
            if (e is BroadcastRejectedException) {
                Log.w(LOG_TAG, "sRefresh(optimise=$optimise) -> Broadcast was rejected", e)
            } else {
                Log.e(LOG_TAG, "sRefresh(optimise=$optimise)", e)
            }
        }
    }

    @MainThread
    suspend fun sNotify(context: Context, builder: NotificationBuilder,
                        optimise: Boolean = true): NotifyId? {
        try {
            NotifyBase.assertUsable()
            return if (optimise && NotifyBase.isInitialized) Notifier.notify(context, builder)
            else sendSuspendRequestNotNull(
                    context = context,
                    name = "RqNotifyReceiver",
                    intent = RqNotifyReceiver.getStartIntent(context, builder),
                    resultExtractor = {
                        it.extras?.getKSerializable(REQUEST_EXTRA_RESULT, NotifyIdSerializer)
                    }
            )
        } catch (e: Exception) {
            if (e is BroadcastRejectedException) {
                Log.w(LOG_TAG, "notify(builder=$builder, optimise=$optimise)" +
                        " -> Broadcast was rejected", e)
            } else {
                Log.e(LOG_TAG, "notify(builder=$builder, optimise=$optimise)", e)
            }
        }
        return null
    }

    @MainThread
    suspend fun sNotifyAll(context: Context, builder: MultiNotificationBuilder,
                           optimise: Boolean = true): Map<out NotifyId, Bundle> {
        try {
            NotifyBase.assertUsable()
            return if (optimise && NotifyBase.isInitialized) Notifier.notifyAll(context, builder)
            else sendSuspendRequestNotNull(
                    context = context,
                    name = "RqNotifyAllReceiver",
                    intent = RqNotifyAllReceiver.getStartIntent(context, builder),
                    resultExtractor = {
                        it.extras
                                ?.getKSerializable(
                                        key = REQUEST_EXTRA_RESULT,
                                        loader = RqNotifyAllReceiver.RESULT_SERIALIZER
                                )
                    }
            )
        } catch (e: Exception) {
            if (e is BroadcastRejectedException) {
                Log.w(LOG_TAG, "notifyAll(builder=$builder, optimise=$optimise)" +
                        " -> Broadcast was rejected", e)
            } else {
                Log.e(LOG_TAG, "notifyAll(builder=$builder, optimise=$optimise)", e)
            }
        }
        return emptyMap()
    }

    @MainThread
    suspend fun sCancel(context: Context, notifyId: NotifyId,
                        optimise: Boolean = true): Bundle? {
        try {
            NotifyBase.assertUsable()
            return if (optimise && NotifyBase.isInitialized) Notifier.cancel(context, notifyId)
            else sendSuspendRequest(
                    context = context,
                    name = "RqCancelReceiver",
                    intent = RqCancelReceiver.getStartIntent(context, notifyId),
                    resultExtractor = {
                        it.extras?.getBundle(REQUEST_EXTRA_RESULT)
                    }
            )
        } catch (e: Exception) {
            if (e is BroadcastRejectedException) {
                Log.w(LOG_TAG, "sCancel(notifyId=$notifyId, optimise=$optimise)" +
                        " -> Broadcast was rejected", e)
            } else {
                Log.e(LOG_TAG, "sCancel(notifyId=$notifyId, optimise=$optimise)", e)
            }
        }
        return null
    }

    @MainThread
    suspend fun sCancelAll(context: Context, vararg notifyIds: NotifyId,
                           optimise: Boolean = true): Map<out NotifyId, Bundle> =
            sCancelAll(context, notifyIds.asList(), optimise)

    @MainThread
    suspend fun sCancelAll(context: Context, notifyIds: Collection<NotifyId>,
                           optimise: Boolean = true): Map<out NotifyId, Bundle> {
        try {
            NotifyBase.assertUsable()
            return if (optimise && NotifyBase.isInitialized) Notifier.cancelAll(context, notifyIds)
            else sendSuspendRequestNotNull(
                    context = context,
                    name = "RqCancelAllIdsReceiver",
                    intent = RqCancelAllIdsReceiver.getStartIntent(context, notifyIds),
                    resultExtractor = {
                        it.extras
                                ?.getKSerializable(
                                        key = REQUEST_EXTRA_RESULT,
                                        loader = RqCancelAllIdsReceiver.RESULT_SERIALIZER
                                )
                    }
            )
        } catch (e: Exception) {
            if (e is BroadcastRejectedException) {
                Log.w(LOG_TAG, "sCancelAll(notifyIds=$notifyIds, optimise=$optimise)" +
                        " -> Broadcast was rejected", e)
            } else {
                Log.e(LOG_TAG, "sCancelAll(notifyIds=$notifyIds, optimise=$optimise)", e)
            }
        }
        return emptyMap()
    }

    @MainThread
    suspend fun sCancelAll(context: Context, groupId: String? = null, channelId: String? = null,
                           optimise: Boolean = true): Map<out NotifyId, Bundle> {
        try {
            NotifyBase.assertUsable()
            return if (optimise && NotifyBase.isInitialized)
                Notifier.cancelAll(context, groupId, channelId)
            else sendSuspendRequestNotNull(
                    context = context,
                    name = "RqCancelAllReceiver",
                    intent = RqCancelAllReceiver.getStartIntent(context, groupId, channelId),
                    resultExtractor = {
                        it.extras
                                ?.getKSerializable(
                                        key = REQUEST_EXTRA_RESULT,
                                        loader = RqCancelAllReceiver.RESULT_SERIALIZER
                                )
                    }
            )
        } catch (e: Exception) {
            if (e is BroadcastRejectedException) {
                Log.w(LOG_TAG, "sCancelAll(groupId=$groupId, channelId=$channelId," +
                        " optimise=$optimise) -> Broadcast was rejected", e)
            } else {
                Log.e(LOG_TAG, "sCancelAll(groupId=$groupId," +
                        " channelId=$channelId, optimise=$optimise)", e)
            }
        }
        return emptyMap()
    }

    @MainThread
    suspend fun sSetChannelEnabled(context: Context, groupId: String?, channelId: String,
                                   enable: Boolean, optimise: Boolean = true) {
        try {
            NotifyBase.assertUsable()
            return if (optimise && NotifyBase.isInitialized)
                ChannelsEnabler.setChannelEnabled(context, groupId, channelId, enable)
            else sendSuspendRequest(
                    context = context,
                    name = "RqSetEnabledReceiver",
                    intent = RqSetEnabledReceiver.getStartIntent(context, groupId, channelId, enable),
                    resultExtractor = { Unit }
            )
        } catch (e: Exception) {
            if (e is BroadcastRejectedException) {
                Log.w(LOG_TAG, "sSetChannelEnabled(groupId=$groupId, channelId=$channelId," +
                        " enable=$enable, optimise=$optimise) -> Broadcast was rejected", e)
            } else {
                Log.e(LOG_TAG, "sSetChannelEnabled(groupId=$groupId, channelId=$channelId," +
                        " enable=$enable, optimise=$optimise)", e)
            }
        }
    }
}