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

package eu.codetopic.utils.notifications.manager.data

import android.content.Context
import android.os.Bundle
import android.support.annotation.MainThread
import eu.codetopic.utils.notifications.manager.NotifyClassifier
import eu.codetopic.utils.notifications.manager.NotifyManager
import eu.codetopic.utils.notifications.manager.util.NotifyChannel
import eu.codetopic.utils.notifications.manager.util.NotifyGroup
import kotlinx.serialization.*
import kotlinx.serialization.internal.PairSerializer
import kotlinx.serialization.internal.StringSerializer
import kotlinx.serialization.json.JSON

/**
 * @author anty
 */

// FIXME: This class should be sealed, but making it sealed causes problems with serialization... Why??
abstract class NotifyId {

    companion object {
         fun forCommon(groupId: String, channelId: String, id: Int,
                       hasTag: Boolean = true): NotifyId =
                 CommonNotifyId(groupId, channelId, id, hasTag)
    }

    // Part of notification identity
    abstract val isSummary: Boolean
    abstract val idGroup: String
    abstract val idChannel: String
    abstract val idNotify: Int
    abstract val hasTag: Boolean

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
        if (hasTag != other.hasTag) return false

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
                    " isRefreshable=$isRefreshable, hasTag=$hasTag, timeWhen=$timeWhen)"
}

@Serializer(forClass = NotifyId::class)
object NotifyIdSerializer : KSerializer<NotifyId> { // TODO: Better way to serialize NotifyId

    private const val TYPE_COMMON = "COMMON"
    private const val TYPE_COMMON_PERSISTENT = "COMMON_PERSISTENT"
    private const val TYPE_SUMMARY = "SUMMARY"

    private val serializer =
            PairSerializer(StringSerializer, StringSerializer)

    override val serialClassDesc: KSerialClassDesc
        get() = serializer.serialClassDesc

    @Suppress("REDUNDANT_ELSE_IN_WHEN")
    override fun save(output: KOutput, obj: NotifyId) = serializer.save(
            output,
            when (obj) {
                is CommonNotifyId -> TYPE_COMMON to JSON.stringify(obj)
                is CommonPersistentNotifyId -> TYPE_COMMON_PERSISTENT to JSON.stringify(obj)
                is SummaryNotifyId -> TYPE_SUMMARY to JSON.stringify(obj)
                else -> throw IllegalArgumentException("Unknown notifyId type: ${obj::class}")
            }
    )

    override fun load(input: KInput): NotifyId = serializer.load(input).let {
        when (it.first) {
            TYPE_COMMON -> JSON.parse<CommonNotifyId>(it.second)
            TYPE_COMMON_PERSISTENT ->
                JSON.parse<CommonPersistentNotifyId>(it.second)
            TYPE_SUMMARY -> JSON.parse<SummaryNotifyId>(it.second)
            else -> throw IllegalArgumentException("Unknown notifyId type: ${it.first}")
        }
    }
}

@Serializable
internal class CommonNotifyId(override val idGroup: String,
                              override val idChannel: String,
                              override val idNotify: Int,
                              override val hasTag: Boolean = true,
                              override val timeWhen: Long = System.currentTimeMillis()) : NotifyId() {

    @Transient
    override val isSummary: Boolean
        get() = false

    @Transient
    override val isPersistent: Boolean
        get() = false

    @Transient
    override val isRefreshable: Boolean
        get() = false
}

@Serializable
internal class CommonPersistentNotifyId(override val idGroup: String,
                                        override val idChannel: String,
                                        override val idNotify: Int,
                                        override val timeWhen: Long = System.currentTimeMillis(),
                                        override val isRefreshable: Boolean = true) : NotifyId() {
    @Transient
    override val isSummary: Boolean
        get() = false

    @Transient
    override val isPersistent: Boolean
        get() = true

    @Transient
    override val hasTag: Boolean
        get() = true
}

@Serializable
internal class SummaryNotifyId(override val idGroup: String,
                               override val idChannel: String,
                               override val timeWhen: Long = System.currentTimeMillis()) : NotifyId() {

    companion object {

        private const val SUMMARY_NOTIFICATION_ID = 1
    }

    @Transient
    override val idNotify: Int
        get() = SUMMARY_NOTIFICATION_ID

    @Transient
    override val isSummary: Boolean
        get() = true

    @Transient
    override val isPersistent: Boolean
        get() = false

    @Transient
    override val isRefreshable: Boolean
        get() = true

    @Transient
    override val hasTag: Boolean
        get() = true
}

val NotifyId.group: NotifyGroup
    get() = NotifyClassifier.findGroup(idGroup)

val NotifyId.channel: NotifyChannel
    get() = NotifyClassifier.findChannel(idChannel)

val NotifyId.idCombined: String
    get() = NotifyChannel.combinedId(idGroup, idChannel)

val NotifyId.tag: String?
    get() =
        if (hasTag)
            "TAG(isSummary=$isSummary, combinedId=$idCombined)"
        else null

@MainThread
fun NotifyId.cancel(context: Context, optimise: Boolean = true) =
        NotifyManager.cancel(context, this, optimise)

@MainThread
suspend fun NotifyId.sCancel(context: Context, optimise: Boolean = true): Bundle? =
        NotifyManager.sCancel(context, this, optimise)

@get:MainThread
val NotifyId.data: Bundle
    get() = NotifyManager.getDataOf(this)