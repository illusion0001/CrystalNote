package com.xephorium.crystalnote.ui.settings

import android.os.Bundle

import com.xephorium.crystalnote.R
import com.xephorium.crystalnote.ui.custom.NoteToolbar
import com.xephorium.crystalnote.ui.drawer.DrawerActivity
import kotlinx.android.synthetic.main.toolbar_activity_layout.*


class SettingsActivity : DrawerActivity(), SettingsContract.View {


    /*--- Variable Declarations ---*/

    private lateinit var presenter: SettingsPresenter


    /*--- Lifecycle Methods ---*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setActivityContent(R.layout.settings_activity_layout)

        presenter = SettingsPresenter()

        setupToolbar()
    }

    override fun onResume() {
        super.onResume()
        presenter.attachView(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }


    /*--- View Manipulation Methods ---*/

    override fun showNavigationDrawer() {
        openDrawer()
    }


    /*--- Private Setup Methods ---*/

    private fun setupToolbar() {
        toolbar.isEditMode = false
        toolbar.setTitle(R.string.settings_title)
        toolbar.setLeftButtonImage(R.drawable.icon_menu)
        toolbar.setNoteToolbarListener(object : NoteToolbar.NoteToolbarListener {
            override fun onLeftButtonClick() = presenter.handleMenuButtonClick()
            override fun onRightButtonClick() = Unit
            override fun onTextChange(text: String) = Unit
        })
    }

}
