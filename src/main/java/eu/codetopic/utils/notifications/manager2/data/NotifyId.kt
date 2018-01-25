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

package eu.codetopic.utils.notifications.manager2.data

import android.content.Context
import android.os.Bundle
import android.support.annotation.MainThread
import eu.codetopic.utils.notifications.manager2.Notifier
import eu.codetopic.utils.notifications.manager2.NotifyClassifier
import eu.codetopic.utils.notifications.manager2.NotifyManager
import eu.codetopic.utils.notifications.manager2.util.NotifyChannel
import eu.codetopic.utils.notifications.manager2.util.NotifyGroup
import kotlinx.serialization.KSerializer
import kotlinx.serialization.internal.PairSerializer
import kotlinx.serialization.internal.StringSerializer
import kotlinx.serialization.json.JSON
import kotlinx.serialization.serializer

/**
 * @author anty
 */
abstract class NotifyId {

    companion object {

        private const val TYPE_COMMON = "COMMON"
        private const val TYPE_COMMON_PERSISTENT = "COMMON_PERSISTENT"
        private const val TYPE_SUMMARY = "SUMMARY"

        fun NotifyId.stringify(): String = JSON.stringify(
                PairSerializer(StringSerializer, StringSerializer),
                when (this) {
                    is CommonNotifyId -> TYPE_COMMON to JSON.stringify(this)
                    is CommonPersistentNotifyId -> TYPE_COMMON_PERSISTENT to JSON.stringify(this)
                    is SummaryNotifyId -> TYPE_SUMMARY to JSON.stringify(this)
                    else -> throw IllegalArgumentException("Unknown notifyId type: ${this::class}")
                }
        )

        fun parse(str: String): NotifyId = JSON.parse(
                PairSerializer(StringSerializer, StringSerializer), str)
                .let {
                    when (it.first) {
                        TYPE_COMMON -> JSON.parse<CommonNotifyId>(it.second)
                        TYPE_COMMON_PERSISTENT ->
                            JSON.parse<CommonPersistentNotifyId>(it.second)
                        TYPE_SUMMARY -> JSON.parse<SummaryNotifyId>(it.second)
                        else -> throw IllegalArgumentException("Unknown notifyId type: ${it.first}")
                    }
                }

        val NotifyId.group: NotifyGroup
            get() = NotifyClassifier.findGroup(idGroup)

        val NotifyId.channel: NotifyChannel
            get() = NotifyClassifier.findChannel(idChannel)

        val NotifyId.idCombined: String
            get() = NotifyChannel.combinedId(idGroup, idChannel)

        val NotifyId.tag: String
            get() = "TAG(isSummary=$isSummary, combinedId=$idCombined)"

        @MainThread
        fun NotifyId.cancel(context: Context): Bundle? = Notifier.cancel(context, this)

        @MainThread
        fun NotifyId.requestCancel(context: Context, optimise: Boolean = true) =
                NotifyManager.requestCancel(context, this, optimise)

        @MainThread
        fun Collection<NotifyId>.cancelAll(context: Context): Map<NotifyId, Bundle> =
                Notifier.cancelAll(context, this)

        @MainThread
        fun Collection<NotifyId>.requestCancelAll(context: Context, optimise: Boolean = true) =
                NotifyManager.requestCancelAll(context, this, optimise)

        @MainThread
        fun Pair<NotifyGroup, NotifyChannel>.cancelAll(context: Context): Map<NotifyId, Bundle> =
                Notifier.cancelAll(context, first.id, second.id)

        @MainThread
        fun Pair<NotifyGroup, NotifyChannel>.requestCancelAll(context: Context,
                                                              optimise: Boolean = true) =
                NotifyManager.requestCancelAll(context, first.id, second.id)

        @get:MainThread
        val NotifyId.data: Bundle
            get() = NotifyManager.getDataOf(this)
    }

    // Part of notification identity
    abstract val isSummary: Boolean
    abstract val idGroup: String
    abstract val idChannel: String
    abstract val idNotify: Int

    // Metadata
    abstract val isPersistent: Boolean
    abstract val isRefreshable: Boolean
    abstract val timeWhen: Long

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NotifyId

        if (isSummary != other.isSummary) return false
        if (idGroup != other.idGroup) return false
        if (idChannel != other.idChannel) return false
        if (idNotify != other.idNotify) return false

        return true
    }

    override fun hashCode(): Int {
        var result = isSummary.hashCode()
        result = 31 * result + idGroup.hashCode()
        result = 31 * result + idChannel.hashCode()
        result = 31 * result + idNotify
        return result
    }

    override fun toString(): String =
            "NotifyId(isSummary=$isSummary, idGroup='$idGroup', idChannel='$idChannel'," +
                    " idNotify=$idNotify, isPersistent=$isPersistent," +
                    " isRefreshable=$isRefreshable, timeWhen=$timeWhen)"
}