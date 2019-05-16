package com.xephorium.crystalnote.ui.drawer

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.ViewGroup

import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager

import com.xephorium.crystalnote.R
import com.xephorium.crystalnote.data.SharedPreferencesRepository
import com.xephorium.crystalnote.ui.about.AboutActivity
import com.xephorium.crystalnote.ui.base.BaseActivity
import com.xephorium.crystalnote.ui.utility.DisplayUtils
import com.xephorium.crystalnote.ui.drawer.DrawerItem.Companion.DrawerItemType.*
import com.xephorium.crystalnote.ui.home.HomeActivity
import com.xephorium.crystalnote.ui.settings.SettingsActivity
import com.xephorium.crystalnote.ui.widget.WidgetActivity
import kotlinx.android.synthetic.main.drawer_activity_layout.*
import kotlinx.android.synthetic.main.drawer_layout.*
import com.xephorium.crystalnote.ui.drawer.DrawerItem.Companion.DrawerButton.*
import com.xephorium.crystalnote.ui.drawer.DrawerItem.Companion.DrawerButton

@SuppressLint("Registered")
open class DrawerActivity : BaseActivity(), DrawerContract.View {


    /*--- Variable Declarations ---*/

    private lateinit var presenter: DrawerPresenter

    private lateinit var drawerToggle: ActionBarDrawerToggle
    private var drawerOpen: Boolean = false
    private var drawerAnimating: Boolean = false


    /*--- Lifecycle Methods ---*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.drawer_activity_layout)

        presenter = DrawerPresenter()
        presenter.sharedPreferencesRepository = SharedPreferencesRepository(this)

        setupViewFields()
        setupStatusBar()
        setupToolbar()
        setupNavDrawerItems()
    }

    override fun onResume() {
        super.onResume()
        presenter.attachView(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    override fun onBackPressed() {
        if (drawerAnimating)
            return

        if (drawerOpen) {
            layoutDrawer.closeDrawers()
            drawerAnimating = true
        } else
            super.onBackPressed()
    }


    /*--- Child Activity Methods ---*/

    fun setActivityContent(layoutResource: Int) {
        val view = layoutInflater.inflate(layoutResource, null)
        view.layoutParams =
                ViewGroup.LayoutParams(layoutActivityContent.width, getActivityContentHeight())
        layoutActivityContent.addView(view)
    }

    fun openDrawer() {
        layoutDrawer.openDrawer(GravityCompat.START)
        drawerAnimating = true
    }

    fun closeDrawer() {
        layoutDrawer.closeDrawers()
        drawerAnimating = true
    }


    /*--- View Manipulation Methods ---*/

    override fun closeDrawerAfterDelay() {
        Handler().postDelayed({
            closeDrawer()
        }, RIPPLE_DELAY)
    }

    override fun navigateToHome() {
        navigateToActivity(Intent(this@DrawerActivity, HomeActivity::class.java))
    }

    override fun navigateToWidget() {
        navigateToActivity(Intent(this@DrawerActivity, WidgetActivity::class.java))
    }

    override fun navigateToSettings() {
        navigateToActivity(Intent(this@DrawerActivity, SettingsActivity::class.java))
    }

    override fun navigateToAbout() {
        navigateToActivity(Intent(this@DrawerActivity, AboutActivity::class.java))
    }

    override fun setSelectedMenuButton(button: DrawerButton) {
        (listDrawer.adapter as? DrawerAdapter)?.items?.forEachIndexed { index, item ->
            if (item.type == BUTTON) {
                val holder = listDrawer.findViewHolderForLayoutPosition(index)
                        as? DrawerAdapter.ViewHolder
                holder?.let {
                    if (item.text == button.name) {
                        it.layout.setBackgroundColor(ContextCompat
                                .getColor(this, R.color.drawerItemSelected))
                        it.text.setTextColor(ContextCompat
                                .getColor(this, R.color.drawerItemSelectedText))
                        it.icon.setColorFilter(
                                ContextCompat.getColor(this, R.color.drawerItemSelectedIcon),
                                PorterDuff.Mode.SRC_IN
                        )
                    } else {
                        it.text.setTextColor(ContextCompat
                                .getColor(this, R.color.drawerItemText)
                        )
                        it.icon.setColorFilter(
                                ContextCompat.getColor(this, R.color.drawerItemText),
                                PorterDuff.Mode.SRC_IN
                        )
                    }
                }
            }
        }
    }


    /*--- Private Setup Methods ---*/

    private fun setupStatusBar() {
        layoutDrawerActivityStatusBar.minimumHeight = DisplayUtils.getStatusBarHeight(this)
        layoutDrawerActivityStatusBar.elevation = resources.getDimension(R.dimen.toolbarElevation)
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun setupViewFields() {
        drawerOpen = false
        drawerAnimating = false

        drawerToggle = getActionBarDrawerToggle()
        drawerToggle.isDrawerIndicatorEnabled = false
        layoutDrawer.addDrawerListener(drawerToggle)
    }

    private fun setupNavDrawerItems() {
        listDrawer.layoutManager = LinearLayoutManager(this)
        listDrawer.adapter = DrawerAdapter(this, getItems())
    }

    private fun getActionBarDrawerToggle() = object : ActionBarDrawerToggle(
            this,
            layoutDrawer,
            R.string.navigationDrawerOpened,
            R.string.navigationDrawerClosed) {

        override fun onDrawerClosed(drawerView: View) {
            super.onDrawerClosed(drawerView)
            drawerOpen = false
            drawerAnimating = false
        }

        override fun onDrawerOpened(drawerView: View) {
            super.onDrawerOpened(drawerView)
            drawerOpen = true
            drawerAnimating = false
        }
    }

    private fun getItems() = listOf(
            DrawerItem(R.drawable.icon_note, NOTES.name, object : DrawerItem.ClickListener {
                override fun onClick() = presenter.handleHomeClick()
            }, BUTTON
            ),
            DrawerItem(R.drawable.icon_sliders, SETTINGS.name, object : DrawerItem.ClickListener {
                override fun onClick() = presenter.handleSettingsClick()
            }, BUTTON
            ),
            DrawerItem(R.drawable.icon_widget, WIDGET.name, object : DrawerItem.ClickListener {
                override fun onClick() = presenter.handleWidgetClick()
            }, BUTTON
            ),
            DrawerItem(0, "", object : DrawerItem.ClickListener {
                override fun onClick() = Unit
            }, DIVIDER
            ),
            DrawerItem(R.drawable.icon_about, ABOUT.name, object : DrawerItem.ClickListener {
                override fun onClick() = presenter.handleAboutClick()
            }, BUTTON
            )
    )

    private fun navigateToActivity(intent: Intent) {
        Handler().postDelayed({
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            overridePendingTransition(0, 0)
        }, RIPPLE_DELAY + DRAWER_CLOSE_TIME)
    }

    private fun getActivityContentHeight(): Int {
        val displayHeight = DisplayUtils.getDisplayHeight(this)
        val statusBarHeight = DisplayUtils.getStatusBarHeight(this)
        val toolbarHeight = resources.getDimensionPixelSize(R.dimen.toolbarHeight)
        return displayHeight - (statusBarHeight + toolbarHeight)
    }


    /*--- Constants ---*/

    companion object {
        private const val DRAWER_CLOSE_TIME: Long = 300
        private const val RIPPLE_DELAY: Long = 100
    }
}
