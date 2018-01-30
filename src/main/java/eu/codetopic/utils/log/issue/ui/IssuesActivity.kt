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
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import eu.codetopic.utils.AndroidExtensions.getIconics
import eu.codetopic.utils.AndroidExtensions.edit
import eu.codetopic.utils.R
import eu.codetopic.utils.log.issue.notify.IssuesNotifyChannel
import eu.codetopic.utils.log.issue.notify.IssuesNotifyGroup
import eu.codetopic.utils.notifications.manager.NotifyManager
import eu.codetopic.utils.ui.activity.modular.module.ToolbarModule
import eu.codetopic.utils.ui.container.adapter.CustomItemAdapter
import eu.codetopic.utils.ui.container.recycler.Recycler
import eu.codetopic.utils.ui.view.holder.loading.LoadingModularActivity
import kotlinx.android.extensions.CacheImplementation
import kotlinx.android.extensions.ContainerOptions
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.coroutines.experimental.asReference

/**
 * @author anty
 */
@ContainerOptions(CacheImplementation.SPARSE_ARRAY)
class IssuesActivity : LoadingModularActivity(ToolbarModule()) {

    companion object {

        private const val LOG_TAG = "IssuesActivity"

        fun start(context: Context) {
            context.startActivity(
                    Intent(context, IssuesActivity::class.java)
            )
        }
    }

    private var adapter: CustomItemAdapter<IssueItem>? = null
    private var recyclerManager: Recycler.RecyclerManagerImpl? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adapter = CustomItemAdapter(this)

        recyclerManager = Recycler.inflate().withSwipeToRefresh().on(this)
                .setEmptyImage(getIconics(GoogleMaterial.Icon.gmd_warning).sizeDp(72))
                .setEmptyText(R.string.empty_view_text_no_logged_issues)
                .setSmallEmptyText(R.string.empty_view_text_small_no_logged_issues)
                .setOnRefreshListener(::update)
                .setAdapter(adapter)
                .apply {
                    val layoutManager = LinearLayoutManager(this@IssuesActivity)
                    setLayoutManager(layoutManager)
                    recyclerView.addItemDecoration(DividerItemDecoration(
                            this@IssuesActivity,
                            layoutManager.orientation
                    ))
                }
    }

    override fun onStart() {
        super.onStart()
        update()
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
                val thisRef = this.asReference()
                val holderRef = holder.asReference()

                launch(UI) {
                    holderRef().showLoading()

                    NotifyManager.requestSuspendCancelAll(
                        context = thisRef(),
                        groupId = IssuesNotifyGroup.ID,
                        channelId = IssuesNotifyChannel.ID
                    )
                    thisRef().update()

                    holderRef().hideLoading()
                }
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun update() {
        adapter?.edit {
            clear()

            addAll(
                    NotifyManager
                            .getAllData(
                                    groupId = IssuesNotifyGroup.ID,
                                    channelId = IssuesNotifyChannel.ID
                            )
                            .mapNotNull {
                                val (id, data) = it
                                val issue = IssuesNotifyChannel.readData(data)
                                        ?: return@mapNotNull null

                                return@mapNotNull IssueItem(id, issue)
                            }
                            .sortedBy {
                                it.notifyId?.timeWhen
                                        ?: System.currentTimeMillis()
                            }
            )

            notifyAllItemsChanged()
        }

        recyclerManager?.setRefreshing(false)
    }

    override fun onDestroy() {
        recyclerManager = null
        adapter = null
        super.onDestroy()
    }
}