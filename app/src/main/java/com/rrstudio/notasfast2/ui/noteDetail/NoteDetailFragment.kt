package com.rrstudio.notasfast2.ui.noteDetail

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.graphics.drawable.toIcon
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.rrstudio.notasfast2.MainActivity
import com.rrstudio.notasfast2.R
import com.rrstudio.notasfast2.data.local.NoteAplication
import com.rrstudio.notasfast2.data.model.NoteEntity
import com.rrstudio.notasfast2.databinding.FragmentNoteBinding
import com.rrstudio.notasfast2.databinding.FragmentNoteDetailBinding
import com.rrstudio.notasfast2.ui.editNote.EditNoteFragment
import com.rrstudio.notasfast2.ui.note.NoteFragmentDirections
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class NoteDetailFragment : Fragment(R.layout.fragment_note_detail) {

    private lateinit var mBinding: FragmentNoteDetailBinding
    private var mNoteEntity: NoteEntity? = null
    private val args by navArgs<NoteDetailFragmentArgs>()
    private var mActivity: MainActivity? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding = FragmentNoteDetailBinding.bind(view)



        mBinding.tvName.text = args.name
        mBinding.tvDate.text = args.date
        mBinding.tvDescription.text = args.description
        Glide.with(requireContext())
                .load(args.image)
                .centerCrop()
                .into(mBinding.imgNote)

        setupActionBar()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when(item.itemId){
            android.R.id.home -> {

                mActivity?.onBackPressed()
                true
            }
            R.id.action_edit ->{

                val action = NoteDetailFragmentDirections.actionNoteDetailFragmentToEditNoteFragment(
                        id = args.id
                )
                findNavController().navigate(action)

                true
            }
            else-> return super.onOptionsItemSelected(item)
        }
    }



    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_edit, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun setupActionBar() {

        mActivity = activity as? MainActivity
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mActivity?.supportActionBar?.title = getString(R.string.note_detail)

        setHasOptionsMenu(true)
    }

    override fun onDestroy() {
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        mActivity?.supportActionBar?.title = getString(R.string.app_name)



        setHasOptionsMenu(false)
        super.onDestroy()
    }



}