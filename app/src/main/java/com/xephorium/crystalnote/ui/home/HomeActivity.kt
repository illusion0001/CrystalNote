package com.xephorium.crystalnote.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.View

import com.xephorium.crystalnote.R
import com.xephorium.crystalnote.data.NoteRepository
import com.xephorium.crystalnote.data.model.Note
import com.xephorium.crystalnote.ui.drawer.DrawerActivity
import com.xephorium.crystalnote.ui.update.UpdateActivity
import com.xephorium.crystalnote.ui.update.UpdateActivity.Companion.KEY_NOTE_NAME
import com.xephorium.crystalnote.ui.custom.NoteListView
import com.xephorium.crystalnote.ui.custom.NoteToolbar

import kotlinx.android.synthetic.main.home_activity_layout.*
import kotlinx.android.synthetic.main.toolbar_activity_layout.*

class HomeActivity : DrawerActivity(), HomeContract.View {


    /*--- Variable Declarations ---*/

    lateinit var presenter: HomePresenter


    /*--- Lifecycle Methods ---*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setActivityContent(R.layout.home_activity_layout)

        presenter = HomePresenter()
        presenter.noteRepository = NoteRepository(this)

        setupToolbar()
        setupClickListeners()
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

    override fun populateNoteList(notes: List<Note>) {
        listHomeNotes.visibility = View.VISIBLE
        textHomeEmpty.visibility = View.GONE
        listHomeNotes.populateNoteList(notes)
    }

    override fun showEmptyNotesList() {
        listHomeNotes.visibility = View.GONE
        textHomeEmpty.visibility = View.VISIBLE
    }

    override fun showNavigationDrawer() {
        openDrawer()
    }

    override fun navigateToEditNote(name: String) {
        val intent = Intent(this, UpdateActivity::class.java)
        intent.putExtra(KEY_NOTE_NAME, name)
        startActivity(intent)
    }

    override fun navigateToNewNote() {
        val intent = Intent(this, UpdateActivity::class.java)
        startActivity(intent)
    }


    /*--- Private Setup Methods ---*/

    private fun setupToolbar() {
        toolbar.isEditMode = false
        toolbar.setTitle(R.string.homeTitle)
        toolbar.setLeftButtonImage(R.drawable.icon_menu)
        toolbar.setNoteToolbarListener(object : NoteToolbar.NoteToolbarListener {
            override fun onLeftButtonClick() = presenter.handleMenuButtonClick()
            override fun onRightButtonClick() = Unit
            override fun onTextChange(text: String) = Unit
        })
    }

    private fun setupClickListeners() {
        floatingActionButtonHome.setOnClickListener { presenter.handleNewNoteButtonClick() }
        listHomeNotes.noteListViewListener = object : NoteListView.NoteListViewListener {
            override fun onNoteClick(note: Note) = presenter.handleNoteClick(note)
            override fun onNoteLongClick(note: Note) = presenter.handleNoteLongClick(note)
            override fun onNoteListRefresh() = presenter.handleNoteListRefresh()
        }
    }
}
