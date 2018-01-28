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

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.MainThread
import eu.codetopic.utils.notifications.manager.util.NotifyChannel
import eu.codetopic.utils.notifications.manager.util.NotifyGroup
import eu.codetopic.java.utils.JavaExtensions.alsoIf
import eu.codetopic.utils.AndroidExtensions.OrderedBroadcastResult
import eu.codetopic.utils.AndroidExtensions.sendSuspendOrderedBroadcast
import eu.codetopic.utils.AndroidExtensions.getKotlinSerializable
import eu.codetopic.utils.UtilsBase
import eu.codetopic.utils.UtilsBase.processNameNotifyManager
import eu.codetopic.utils.bundle.SerializableBundleWrapper
import eu.codetopic.utils.notifications.manager.create.MultiNotificationBuilder
import eu.codetopic.utils.notifications.manager.create.NotificationBuilder
import eu.codetopic.utils.notifications.manager.data.NotifyId
import eu.codetopic.utils.notifications.manager.receiver.*
import eu.codetopic.utils.notifications.manager.save.NotifyData

/**
 * @author anty
 */
object NotifyManager {

    internal const val REQUEST_RESULT_UNKNOWN = 1
    internal const val REQUEST_RESULT_FAIL = 0
    internal const val REQUEST_RESULT_OK = -1

    internal const val REQUEST_EXTRA_THROWABLE = "EXTRA_THROWABLE"
    internal const val REQUEST_EXTRA_RESULT = "EXTRA_RESULT"

    private var initialized = false
    private var usable = false

    val isInitialized: Boolean
        get() = initialized

    val isUsable: Boolean
        get() = usable

    fun assertInitialized(context: Context) {
        if (!isInitialized) throw IllegalStateException(
                "NotifyManager is not initialized in this process: " +
                        if (!isOnNotifyManagerProcess(context))
                            "Not running in ':notify' process"
                        else "Not yet initialized"
        )
    }

    fun assertUsable() {
        if (!isUsable) throw IllegalStateException("NotifyManager is not usable in this process")
    }

    fun isOnNotifyManagerProcess(context: Context) =
            context.processNameNotifyManager == UtilsBase.Process.name

    fun assertOnNotifyProcess(context: Context) {
        if (!isOnNotifyManagerProcess(context))
            throw IllegalStateException("Not running in ':notify' process")
    }

    @MainThread
    fun completeInitialization(context: Context) {
        if (usable) throw IllegalStateException(
                "NotifyManager is still initialized in this process"
        )

        usable = true
        if (isOnNotifyManagerProcess(context)) initialized = true

        NotifyData.initialize(context)

        if (isInitialized) {
            cleanup(context)
            refresh(context)
        } else {
            requestRefresh(context, optimise = false)
        }
    }

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
                    .alsoIf({ isInitialized }) { cleanup(context) }

    @MainThread
    fun uninstallChannel(context: Context, channelId: String): NotifyChannel =
            NotifyClassifier.uninstallChannel(context, channelId)
                    .alsoIf({ isInitialized }) { cleanup(context) }

    @MainThread
    fun reinstallGroup(context: Context, groupId: String) =
            NotifyClassifier.reinstallGroup(context, groupId)
                    .alsoIf({ isInitialized }) { refresh(context) }

    @MainThread
    fun reinstallChannel(context: Context, channelId: String) =
            NotifyClassifier.reinstallChannel(context, channelId)
                    .alsoIf({ isInitialized }) { refresh(context) }

    @MainThread
    fun installGroups(context: Context, vararg groups: NotifyGroup) =
            groups.forEach { NotifyClassifier.install(context, it) }

    @MainThread
    fun installChannels(context: Context, vararg channels: NotifyChannel) =
            channels.forEach { NotifyClassifier.install(context, it) }

    @MainThread
    fun reinstallGroups(context: Context, vararg groupIds: String) =
            groupIds.forEach { NotifyClassifier.reinstallGroup(context, it) }
                    .alsoIf({ isInitialized }) { refresh(context) }

    @MainThread
    fun reinstallChannels(context: Context, vararg channelIds: String) =
            channelIds.forEach { NotifyClassifier.reinstallChannel(context, it) }
                    .alsoIf({ isInitialized }) { refresh(context) }

    fun findGroup(groupId: String): NotifyGroup =
            NotifyClassifier.findGroup(groupId)

    fun findChannel(channelId: String): NotifyChannel =
            NotifyClassifier.findChannel(channelId)

    fun hasGroup(groupId: String): Boolean =
            NotifyClassifier.hasGroup(groupId)

    fun hasChannel(channelId: String): Boolean =
            NotifyClassifier.hasChannel(channelId)

    //--------------------------------------------------------------------------

    @MainThread
    fun refresh(context: Context) = Notifier.refresh(context)

    @MainThread
    fun cleanup(context: Context) = Notifier.cleanup(context)

    @MainThread
    fun notify(context: Context, builder: NotificationBuilder): NotifyId =
            Notifier.notify(context, builder)

    @MainThread
    fun notifyAll(context: Context, builder: MultiNotificationBuilder): Map<NotifyId, Bundle> =
            Notifier.notifyAll(context, builder)

    @MainThread
    fun cancel(context: Context, notifyId: NotifyId): Bundle? =
            Notifier.cancel(context, notifyId)

    @MainThread
    fun cancelAll(context: Context, vararg notifyIds: NotifyId): Map<NotifyId, Bundle> =
            Notifier.cancelAll(context, notifyIds.asList())

    @MainThread
    fun cancelAll(context: Context, notifyIds: Collection<NotifyId>): Map<NotifyId, Bundle> =
            Notifier.cancelAll(context, notifyIds)

    @MainThread
    fun cancelAll(context: Context, groupId: String? = null, channelId: String? = null) =
            Notifier.cancelAll(context, groupId, channelId)

    @MainThread
    fun getDataOf(notifyId: NotifyId): Bundle {
        assertUsable()
        return NotifyData.instance[notifyId]
                ?: throw IllegalArgumentException("Id doesn't exists: $notifyId")
    }

    @MainThread
    fun getAllData(groupId: String? = null, channelId: String? = null): Map<NotifyId, Bundle> {
        assertUsable()
        return NotifyData.instance.getAll(groupId, channelId)
    }

    //--------------------------------------------------------------------------

    @MainThread
    fun requestRefresh(context: Context, optimise: Boolean = true) {
        assertUsable()
        if (optimise && isInitialized) refresh(context)
        else context.sendBroadcast(RqRefreshReceiver.getStartIntent(context))
    }

    @MainThread
    fun requestNotify(context: Context, builder: NotificationBuilder, optimise: Boolean = true) {
        assertUsable()
        if (optimise && isInitialized) notify(context, builder)
        else context.sendBroadcast(
                RqNotifyReceiver.getStartIntent(context, builder)
        )
    }

    @MainThread
    fun requestNotifyAll(context: Context, builder: MultiNotificationBuilder,
                         optimise: Boolean = true) {
        assertUsable()
        if (optimise && isInitialized) notifyAll(context, builder)
        else context.sendBroadcast(
                RqNotifyAllReceiver.getStartIntent(context, builder)
        )
    }

    @MainThread
    fun requestCancel(context: Context, notifyId: NotifyId, optimise: Boolean = true) {
        assertUsable()
        if (optimise && isInitialized) cancel(context, notifyId)
        else context.sendBroadcast(RqCancelReceiver.getStartIntent(context, notifyId))
    }

    @MainThread
    fun requestCancelAll(context: Context, vararg notifyIds: NotifyId, optimise: Boolean = true) =
            requestCancelAll(context, notifyIds.asList(), optimise)

    @MainThread
    fun requestCancelAll(context: Context, notifyIds: Collection<NotifyId>, optimise: Boolean = true) {
        assertUsable()
        if (optimise && isInitialized) cancelAll(context, notifyIds)
        else context.sendBroadcast(RqCancelAllIdsReceiver.getStartIntent(context, notifyIds))
    }

    @MainThread
    fun requestCancelAll(context: Context, groupId: String? = null,
                         channelId: String? = null, optimise: Boolean = true) {
        assertUsable()
        if (optimise && isInitialized) cancelAll(context, groupId, channelId)
        else context.sendBroadcast(
                RqCancelAllReceiver.getStartIntent(context, groupId, channelId)
        )
    }

    //--------------------------------------------------------------------------

    private fun getInitialResult(): OrderedBroadcastResult =
            OrderedBroadcastResult(
                    code = REQUEST_RESULT_UNKNOWN,
                    data = null,
                    extras = null
            )

    @MainThread
    private suspend inline fun <T> sendSuspendRequest(context: Context, name: String, intent: Intent,
                                                      resultExtractor: (result: OrderedBroadcastResult) -> T): T {
        val result = context.sendSuspendOrderedBroadcast(intent, getInitialResult())
        when (result.code) {
            REQUEST_RESULT_OK -> return resultExtractor(result)
            REQUEST_RESULT_FAIL ->
                throw result.extras?.getSerializable(REQUEST_EXTRA_THROWABLE) as? Throwable
                        ?: RuntimeException("Unknown fail result received from $name")
            REQUEST_RESULT_UNKNOWN ->
                throw RuntimeException("Failed to process broadcast by $name")
            else -> throw RuntimeException("Unknown resultCode received from $name: ${result.code}")
        }
    }

    @MainThread
    private suspend inline fun <T> sendSuspendRequestNotNull(context: Context, name: String, intent: Intent,
                                                             resultExtractor: (result: OrderedBroadcastResult) -> T?): T =
            sendSuspendRequest(context, name, intent) {
                resultExtractor(it)
                        ?: throw RuntimeException("Failed to extract result of $name")
            }

    @MainThread
    suspend fun requestSuspendRefresh(context: Context, optimise: Boolean = true) {
        assertUsable()
        return if (optimise && isInitialized) refresh(context)
        else sendSuspendRequest(
                context = context,
                name = "RqRefreshReceiver",
                intent = RqRefreshReceiver.getStartIntent(context),
                resultExtractor = { Unit }
        )
    }

    @MainThread
    suspend fun requestSuspendNotify(context: Context, builder: NotificationBuilder,
                                     optimise: Boolean = true): NotifyId {
        assertUsable()
        return if (optimise && isInitialized) notify(context, builder)
        else sendSuspendRequestNotNull(
                context = context,
                name = "RqNotifyReceiver",
                intent = RqNotifyReceiver.getStartIntent(context, builder),
                resultExtractor = {
                    it.extras?.getString(REQUEST_EXTRA_RESULT)?.let { NotifyId.parse(it) }
                }
        )
    }

    @MainThread
    suspend fun requestSuspendNotifyAll(context: Context, builder: MultiNotificationBuilder,
                                        optimise: Boolean = true): Map<NotifyId, Bundle> {
        assertUsable()
        return if (optimise && isInitialized) notifyAll(context, builder)
        else sendSuspendRequestNotNull(
                context = context,
                name = "RqNotifyAllReceiver",
                intent = RqNotifyAllReceiver.getStartIntent(context, builder),
                resultExtractor = {
                    it.extras
                            ?.getKotlinSerializable(
                                    name = REQUEST_EXTRA_RESULT,
                                    loader = RqNotifyAllReceiver.RESULT_SERIALIZER
                            )
                            ?.map {
                                NotifyId.parse(it.first) to it.second.bundle
                            }
                            ?.toMap()
                }
        )
    }

    @MainThread
    suspend fun requestSuspendCancel(context: Context, notifyId: NotifyId,
                                     optimise: Boolean = true): Bundle? {
        assertUsable()
        return if (optimise && isInitialized) cancel(context, notifyId)
        else sendSuspendRequest(
                context = context,
                name = "RqCancelReceiver",
                intent = RqCancelReceiver.getStartIntent(context, notifyId),
                resultExtractor = {
                    it.extras?.getKotlinSerializable<SerializableBundleWrapper>(
                            name = REQUEST_EXTRA_RESULT
                    )?.bundle
                }
        )
    }

    @MainThread
    suspend fun requestSuspendCancelAll(context: Context, vararg notifyIds: NotifyId,
                                        optimise: Boolean = true): Map<NotifyId, Bundle> =
            requestSuspendCancelAll(context, notifyIds.asList(), optimise)

    @MainThread
    suspend fun requestSuspendCancelAll(context: Context, notifyIds: Collection<NotifyId>,
                                        optimise: Boolean = true): Map<NotifyId, Bundle> {
        assertUsable()
        return if (optimise && isInitialized) cancelAll(context, notifyIds)
        else sendSuspendRequestNotNull(
                context = context,
                name = "RqCancelAllIdsReceiver",
                intent = RqCancelAllIdsReceiver.getStartIntent(context, notifyIds),
                resultExtractor = {
                    it.extras
                            ?.getKotlinSerializable(
                                    name = REQUEST_EXTRA_RESULT,
                                    loader = RqCancelAllIdsReceiver.RESULT_SERIALIZER
                            )
                            ?.map {
                                NotifyId.parse(it.first) to it.second.bundle
                            }
                            ?.toMap()
                }
        )
    }

    @MainThread
    suspend fun requestSuspendCancelAll(context: Context, groupId: String? = null,
                                        channelId: String? = null, optimise: Boolean = true): Map<NotifyId, Bundle> {
        assertUsable()
        return if (optimise && isInitialized) cancelAll(context, groupId, channelId)
        else sendSuspendRequestNotNull(
                context = context,
                name = "RqCancelAllReceiver",
                intent = RqCancelAllReceiver.getStartIntent(context, groupId, channelId),
                resultExtractor = {
                    it.extras
                            ?.getKotlinSerializable(
                                    name = REQUEST_EXTRA_RESULT,
                                    loader = RqCancelAllReceiver.RESULT_SERIALIZER
                            )
                            ?.map {
                                NotifyId.parse(it.first) to it.second.bundle
                            }
                            ?.toMap()
                }
        )
    }
}