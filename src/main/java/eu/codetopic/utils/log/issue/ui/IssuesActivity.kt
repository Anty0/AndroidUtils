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
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.support.v7.widget.helper.ItemTouchHelper.LEFT
import android.support.v7.widget.helper.ItemTouchHelper.RIGHT
import android.view.Menu
import android.view.MenuItem
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import eu.codetopic.java.utils.to
import eu.codetopic.utils.*
import eu.codetopic.utils.broadcast.LocalBroadcast
import eu.codetopic.utils.log.issue.data.Issue
import eu.codetopic.utils.log.issue.notify.IssuesNotifyChannel
import eu.codetopic.utils.log.issue.notify.IssuesNotifyGroup
import eu.codetopic.utils.notifications.manager.NotifyManager
import eu.codetopic.utils.notifications.manager.data.NotifyId
import eu.codetopic.utils.notifications.manager.data.cancel
import eu.codetopic.utils.ui.activity.modular.module.ToolbarModule
import eu.codetopic.utils.ui.container.adapter.CustomItemAdapter
import eu.codetopic.utils.ui.container.recycler.Recycler
import eu.codetopic.utils.ui.view.holder.loading.LoadingModularActivity
import kotlinx.android.extensions.CacheImplementation
import kotlinx.android.extensions.ContainerOptions
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.coroutines.experimental.asReference
import org.jetbrains.anko.coroutines.experimental.bg
import org.jetbrains.anko.ctx

/**
 * @author anty
 */
@ContainerOptions(CacheImplementation.SPARSE_ARRAY)
class IssuesActivity : LoadingModularActivity(ToolbarModule()) {

    companion object {

        private const val LOG_TAG = "IssuesActivity"

        fun getIntent(context: Context): Intent =
                Intent(context, IssuesActivity::class.java)

    }

    private val updateReceiver = receiver { _, _ -> update() }

    private var issuesList: List<Pair<NotifyId, Issue>>? = null

    private var adapter: CustomItemAdapter<IssueItem>? = null
    private var recyclerManager: Recycler.RecyclerManagerImpl? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adapter = CustomItemAdapter(this)

        recyclerManager = Recycler.inflate().withSwipeToRefresh().withItemDivider().on(this)
                .setEmptyImage(getIconics(GoogleMaterial.Icon.gmd_done).sizeDp(72))
                .setEmptyText(R.string.empty_view_text_no_logged_issues)
                .setSmallEmptyText(R.string.empty_view_text_small_no_logged_issues)
                .setOnRefreshListener { -> updateWithRefreshing() }
                .setAdapter(adapter)
                .setItemTouchHelper(object : ItemTouchHelper.Callback() {

                    fun getItem(viewHolder: RecyclerView.ViewHolder): IssueItem? =
                            viewHolder.adapterPosition.takeIf { it != -1 }
                                    ?.let { adapter?.getItem(it) }
                                    .to<IssueItem>()

                    override fun getMovementFlags(recyclerView: RecyclerView,
                                                  viewHolder: RecyclerView.ViewHolder): Int =
                            makeMovementFlags(0, LEFT or RIGHT)

                    override fun onMove(recyclerView: RecyclerView,
                                        viewHolder: RecyclerView.ViewHolder,
                                        target: RecyclerView.ViewHolder): Boolean {
                        return false
                    }

                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        getItem(viewHolder)?.notifyId?.cancel(ctx)
                    }
                })

        updateWithLoading()
    }

    override fun onStart() {
        super.onStart()

        register()
    }

    override fun onStop() {
        unregister()

        super.onStop()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)

        menuInflater.inflate(R.menu.activity_issues, menu)
        menu.findItem(R.id.item_done_all).icon =
                getIconics(GoogleMaterial.Icon.gmd_done_all).actionBar()

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_done_all -> {
                val self = this.asReference()
                val holder = holder

                launch(UI) {
                    holder.showLoading()

                    NotifyManager.sCancelAll(
                        context = self(),
                        groupId = IssuesNotifyGroup.ID,
                        channelId = IssuesNotifyChannel.ID
                    )

                    self().update().join()

                    holder.hideLoading()
                }
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun register(): Job {
        LocalBroadcast.registerReceiver(
                receiver = updateReceiver,
                filter = intentFilter(
                        NotifyManager.getOnChangeBroadcastAction()
                )
        )

        return update()
    }

    private fun unregister() {
        LocalBroadcast.unregisterReceiver(updateReceiver)
    }

    private fun updateWithRefreshing(): Job {
        val self = this.asReference()
        return launch(UI) {
            self().update().join()

            self().recyclerManager?.setRefreshing(false)
        }
    }

    private fun updateWithLoading(): Job {
        val holder = holder
        val self = this.asReference()
        return launch(UI) {
            holder.showLoading()

            self().update().join()

            holder.hideLoading()
        }
    }

    private fun update(): Job {
        val self = this.asReference()
        return launch(UI) {
            self().issuesList = bg {
                NotifyManager
                        .getAllData(
                                groupId = IssuesNotifyGroup.ID,
                                channelId = IssuesNotifyChannel.ID
                        )
                        .entries
                        .sortedBy { it.key.timeWhen }
                        .mapNotNull map@ {
                            val (id, data) = it
                            return@map IssuesNotifyChannel.readData(data)
                                    ?.let { id to it }
                        }
            }.await()

            self().updateUi()
        }
    }

    private fun updateUi() {
        adapter?.edit {
            clear()

            issuesList
                    ?.map {
                        val (id, issue) = it
                        IssueItem(id, issue)
                    }
                    ?.also { addAll(it) }

            notifyAllItemsChanged()
        }
    }

    override fun onDestroy() {
        recyclerManager = null
        adapter = null
        super.onDestroy()
    }
}