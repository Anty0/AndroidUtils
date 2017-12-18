/*
 * utils
 * Copyright (C)   2017  anty
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

package eu.codetopic.utils.log

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

import eu.codetopic.java.utils.log.base.LogLine
import eu.codetopic.java.utils.log.base.Priority
import eu.codetopic.utils.AndroidUtils
import eu.codetopic.utils.R
import kotlinx.android.extensions.CacheImplementation
import kotlinx.android.extensions.ContainerOptions
import kotlinx.android.synthetic.main.activity_error_info.*

@ContainerOptions(CacheImplementation.SPARSE_ARRAY)
class ErrorInfoActivity : AppCompatActivity() {

    companion object {

        private const val LOG_TAG = "ErrorInfoActivity"

        private const val EXTRA_LOG_LINE = "EXTRA_LOG_LINE"

        fun start(context: Context, logLine: LogLine) {
            context.startActivity(
                    Intent(context, ErrorInfoActivity::class.java)
                            .putExtra(EXTRA_LOG_LINE, logLine)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val logLine = intent.getSerializableExtra(EXTRA_LOG_LINE) as LogLine?
                ?: return finish()

        val title = getText(
                if (Priority.ERROR == logLine.priority)
                    R.string.activity_label_error_info
                else R.string.activity_label_warn_info
        )

        setFinishOnTouchOutside(false)
        setTitle(title)

        setContentView(R.layout.activity_error_info)

        imgIcon.setImageDrawable(AndroidUtils.getActivityIcon(this, componentName))
        imgIcon.setOnClickListener { finish() }

        txtTitle.text = title

        txtMessage.text = logLine.toString()
    }
}
