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

package eu.codetopic.utils.log.issue

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import eu.codetopic.java.utils.log.Log

import eu.codetopic.java.utils.log.base.LogLine
import eu.codetopic.java.utils.log.base.Priority
import eu.codetopic.utils.R
import eu.codetopic.utils.notifications.manager.NotificationsManager
import eu.codetopic.utils.notifications.manager.data.NotificationId
import kotlinx.android.extensions.CacheImplementation
import kotlinx.android.extensions.ContainerOptions
import kotlinx.android.synthetic.main.activity_error_info.*
import kotlinx.serialization.json.JSON

@ContainerOptions(CacheImplementation.SPARSE_ARRAY)
class IssueInfoActivity : AppCompatActivity() {

    companion object {

        private const val LOG_TAG = "IssueInfoActivity"

        private const val EXTRA_NOTIFY_ID = "EXTRA_NOTIFY_ID"
        private const val EXTRA_ISSUE = "EXTRA_ISSUE"

        fun start(context: Context, id: NotificationId?, issue: Issue) {
            context.startActivity(
                    Intent(context, IssueInfoActivity::class.java)
                            .putExtra(EXTRA_NOTIFY_ID, id?.let { JSON.stringify(it) })
                            .putExtra(EXTRA_ISSUE, JSON.stringify(issue))
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val notifyId = intent?.getStringExtra(EXTRA_NOTIFY_ID)?.let { JSON.parse<NotificationId>(it) }
        val issue = intent?.getStringExtra(EXTRA_ISSUE)?.let { JSON.parse<Issue>(it) }
                ?: run {
                    Log.w(LOG_TAG, "No Issue received in intent")
                    return finish()
                }

        val title = getText(
                if (Priority.ERROR == issue.priority)
                    R.string.activity_label_error_info
                else R.string.activity_label_warn_info
        )

        setFinishOnTouchOutside(false)
        setTitle(title)

        setContentView(R.layout.activity_error_info)

        butExit.setOnClickListener { finish() }

        butDone.isEnabled = notifyId != null
        butDone.setOnClickListener {
            notifyId?.let { NotificationsManager.requestCancel(this, it) }
            finish()
        }

        txtTitle.text = title

        txtMessage.text = issue.toString()
    }
}
