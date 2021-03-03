package com.rrstudio.notasfast2.ui.note

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.rrstudio.notasfast2.MainActivity
import com.rrstudio.notasfast2.R
import com.rrstudio.notasfast2.data.local.NoteAplication
import com.rrstudio.notasfast2.data.model.NoteEntity
import com.rrstudio.notasfast2.databinding.FragmentNoteBinding
import com.rrstudio.notasfast2.repository.onClickListener
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class NoteFragment : Fragment(R.layout.fragment_note), onClickListener {

    private lateinit var mBinding: FragmentNoteBinding
    private lateinit var mAdapter: NoteAdapter
    private lateinit var mGridLayout: GridLayoutManager

    private var mActivity: MainActivity? = null



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding = FragmentNoteBinding.bind(view)

        mBinding.fab.setOnClickListener{
            launchEditFragment()
        }

        setupRecyclerView()


    }

    private fun launchEditFragment() {

        val action = NoteFragmentDirections.actionNoteFragmentToEditNoteFragment(
                id = 0
        )
        findNavController().navigate(action)

    }

    private fun setupRecyclerView() {
        mAdapter = NoteAdapter(mutableListOf(), this)
        mGridLayout = GridLayoutManager(this.context, 2)
        getNotes()

        mBinding.rvNotes.apply {
            setHasFixedSize(true)
            layoutManager = mGridLayout
            adapter = mAdapter
        }
    }

    private fun getNotes() {
        doAsync {
            val notes = NoteAplication.database.noteDao().getAllNotes()
            uiThread {
                mAdapter.setNote(notes)

            }
        }
    }


    override fun onClick(noteEntity: NoteEntity) {
        val action = NoteFragmentDirections.actionNoteFragmentToNoteDetailFragment(
            name = noteEntity.name,
            date = noteEntity.date,
            description = noteEntity.description,
            id = noteEntity.id,
                image = noteEntity.image
        )
        findNavController().navigate(action)
    }

    override fun onDeleteNote(noteEntity: NoteEntity) {
        MaterialAlertDialogBuilder(this.requireContext())
                .setTitle(R.string.dialog_delete_title)
                .setPositiveButton(R.string.dialog_delete_confirm,  { _, _ ->
                    doAsync {
                        NoteAplication.database.noteDao().deleteNote(noteEntity)
                        uiThread {
                            mAdapter.delete(noteEntity)
                        }
                    }
                })
                .setNegativeButton(R.string.dialog_delete_cancel, null)
                .show()
    }



    override fun addNote(noteEntity: NoteEntity) {
        mAdapter.add(noteEntity)
    }

    override fun updateNote(noteEntity: NoteEntity) {
        mAdapter.update(noteEntity)
    }

    override fun hideFab(isVisible: Boolean) {
        if (isVisible) mBinding.fab.show()
        else mBinding.fab.hide()
    }
}