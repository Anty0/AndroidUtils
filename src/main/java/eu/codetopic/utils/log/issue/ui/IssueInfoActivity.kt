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

package eu.codetopic.utils.log.issue.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import eu.codetopic.java.utils.log.Log

import eu.codetopic.java.utils.log.base.Priority.*
import eu.codetopic.utils.R
import eu.codetopic.utils.putKSerializableExtra
import eu.codetopic.utils.getKSerializableExtra
import eu.codetopic.utils.log.issue.data.Issue
import eu.codetopic.utils.notifications.manager.data.NotifyId
import eu.codetopic.utils.notifications.manager.data.NotifyId.Companion.requestCancel
import eu.codetopic.utils.notifications.manager.data.NotifyIdSerializer
import kotlinx.android.extensions.CacheImplementation
import kotlinx.android.extensions.ContainerOptions
import kotlinx.android.synthetic.main.activity_issue_info.*
import kotlinx.serialization.internal.NullableSerializer

@ContainerOptions(CacheImplementation.SPARSE_ARRAY)
class IssueInfoActivity : AppCompatActivity() {

    companion object {

        private const val LOG_TAG = "IssueInfoActivity"

        private const val EXTRA_NOTIFY_ID = "EXTRA_NOTIFY_ID"
        private const val EXTRA_ISSUE = "EXTRA_ISSUE"

        private val EXTRA_NOTIFY_ID_SERIALIZER = NullableSerializer(NotifyIdSerializer)

        fun start(context: Context, notifyId: NotifyId?, issue: Issue) {
            context.startActivity(
                    Intent(context, IssueInfoActivity::class.java)
                            .putKSerializableExtra(EXTRA_NOTIFY_ID, notifyId,
                                    EXTRA_NOTIFY_ID_SERIALIZER)
                            .putKSerializableExtra(EXTRA_ISSUE, issue)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val notifyId = intent?.getKSerializableExtra(EXTRA_NOTIFY_ID, EXTRA_NOTIFY_ID_SERIALIZER)
        val issue = intent?.getKSerializableExtra<Issue>(EXTRA_ISSUE)
                ?: run {
                    Log.e(LOG_TAG, "No Issue received by intent")
                    return finish()
                }

        val title = getText(
                when (issue.priority) {
                    ERROR -> R.string.activity_label_error_info
                    WARN -> R.string.activity_label_warn_info
                    BREAK_EVENT -> R.string.activity_label_break_event_info
                    else -> R.string.activity_label_issue_info
                }
        )

        setFinishOnTouchOutside(false)
        setTitle(title)

        setContentView(R.layout.activity_issue_info)

        butExit.setOnClickListener { finish() }

        butDone.isEnabled = notifyId != null
        butDone.setOnClickListener {
            notifyId?.requestCancel(this)
            finish()
        }

        txtTitle.text = title

        txtMessage.text = issue.toString()
    }
}
