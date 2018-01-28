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

package eu.codetopic.utils.notifications.manager2.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import eu.codetopic.java.utils.JavaExtensions.kSerializer
import eu.codetopic.java.utils.log.Log
import eu.codetopic.utils.bundle.SerializableBundleWrapper.Companion.asSerializable
import eu.codetopic.utils.AndroidExtensions.putKotlinSerializableExtra
import eu.codetopic.utils.AndroidExtensions.getKotlinSerializableExtra
import eu.codetopic.utils.bundle.SerializableBundleWrapper
import eu.codetopic.utils.notifications.manager2.Notifier
import eu.codetopic.utils.notifications.manager2.NotifyManager
import eu.codetopic.utils.notifications.manager2.create.MultiNotificationBuilder
import eu.codetopic.utils.notifications.manager2.data.NotifyId.Companion.stringify
import kotlinx.serialization.internal.PairSerializer
import kotlinx.serialization.internal.StringSerializer
import kotlinx.serialization.json.JSON
import kotlinx.serialization.list
import org.jetbrains.anko.bundleOf

/**
 * @author anty
 */
class RqNotifyAllReceiver : BroadcastReceiver() {

    companion object {

        private const val LOG_TAG = "RqNotifyAllReceiver"
        private const val NAME = "eu.codetopic.utils.notifications.manager2.receiver.$LOG_TAG"
        private const val EXTRA_BUILDER = "$NAME.BUILDER"

        internal val RESULT_SERIALIZER =
                PairSerializer<String, SerializableBundleWrapper>(
                        StringSerializer,
                        kSerializer()
                ).list

        internal fun getStartIntent(context: Context, builder: MultiNotificationBuilder): Intent {
            return Intent(context, RqNotifyAllReceiver::class.java)
                    .putKotlinSerializableExtra(EXTRA_BUILDER, builder)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        try {
            NotifyManager.assertInitialized(context)

            val builder = intent.getKotlinSerializableExtra<MultiNotificationBuilder>(EXTRA_BUILDER)
                    ?: throw IllegalArgumentException("No notification builder received by intent")

            val result = Notifier.notifyAll(context, builder)

            if (isOrderedBroadcast) {
                setResult(NotifyManager.REQUEST_RESULT_OK, null, bundleOf(
                        NotifyManager.REQUEST_EXTRA_RESULT to JSON.stringify(
                                RESULT_SERIALIZER,
                                result.map {
                                    it.key.stringify() to it.value.asSerializable()
                                }
                        )
                ))
            }
        } catch (e: Exception) {
            Log.e(LOG_TAG, "onReceive()", e)

            if (isOrderedBroadcast) {
                setResult(NotifyManager.REQUEST_RESULT_FAIL, null, bundleOf(
                        NotifyManager.REQUEST_EXTRA_THROWABLE to e
                ))
            }
        }
    }
}