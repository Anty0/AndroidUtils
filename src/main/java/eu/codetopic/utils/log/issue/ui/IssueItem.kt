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
import android.support.v4.content.ContextCompat
import eu.codetopic.java.utils.log.base.Priority
import eu.codetopic.utils.getFormattedText
import eu.codetopic.utils.R
import eu.codetopic.utils.log.issue.data.Issue
import eu.codetopic.utils.notifications.manager.data.NotifyId
import eu.codetopic.utils.ui.container.items.custom.CustomItem
import kotlinx.android.synthetic.main.item_issue.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author anty
 */
class IssueItem(val notifyId: NotifyId?, val issue: Issue) : CustomItem() {

    companion object {

        private const val LOG_TAG = "IssueItem"

        private val DATE_FORMAT = SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.getDefault())
    }

    override fun onBindViewHolder(holder: ViewHolder, itemPosition: Int) {
        holder.txtPriority.apply {
            setTextColor(ContextCompat.getColor(
                    holder.context,
                    when (issue.priority) {
                        Priority.ERROR -> R.color.materialRed
                        Priority.WARN -> R.color.materialOrange
                        else -> R.color.materialYellow
                    }
            ))
            text = issue.priority.displayID.toString()
        }

        holder.txtMessage.text = issue.message ?: issue.throwableName

        holder.txtTag.text = holder.context.getFormattedText(
                R.string.issue_item_time_tag_format,
                notifyId?.timeWhen?.let { DATE_FORMAT.format(it) }
                        ?: holder.context.getString(R.string.issue_item_time_unknown),
                issue.tag
        )

        if (itemPosition != NO_POSITION) { // detects usage in header
            holder.boxClickTarget.setOnClickListener {
                /*val context = holder.context
                val options = context.baseActivity?.let {
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                            it,
                            holder.boxColoredBackground,
                            context.getString(R.string.id_transition_issue_item)
                    )
                }

                if (options == null) Log.w(LOG_TAG, "Can't start IssueInfoActivity " +
                        "with transition: Cannot find Activity in context hierarchy")

                ContextCompat.startActivity(
                        context,
                        IssueInfoActivity.getStartIntent(context, this),
                        options?.toBundle()
                )*/

                IssueInfoActivity.start(holder.context, notifyId, issue)
            }
        }
    }

    override fun getItemLayoutResId(context: Context): Int = R.layout.item_issue

    //override fun getWrappers(context: Context): Array<CustomItemWrapper> = CardViewWrapper.WRAPPER

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as IssueItem

        if (notifyId != other.notifyId) return false
        if (issue != other.issue) return false

        return true
    }

    override fun hashCode(): Int {
        var result = notifyId?.hashCode() ?: 0
        result = 31 * result + issue.hashCode()
        return result
    }

    override fun toString(): String {
        return "IssueItem(notifyId=$notifyId, issue=$issue)"
    }
}