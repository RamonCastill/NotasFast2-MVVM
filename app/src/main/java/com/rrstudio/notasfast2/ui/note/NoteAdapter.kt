package com.rrstudio.notasfast2.ui.note

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rrstudio.notasfast2.R
import com.rrstudio.notasfast2.core.BaseViewHolder
import com.rrstudio.notasfast2.data.model.NoteEntity
import com.rrstudio.notasfast2.databinding.FragmentEditNoteBinding
import com.rrstudio.notasfast2.databinding.ItemNoteBinding
import com.rrstudio.notasfast2.repository.onClickListener
import com.rrstudio.notasfast2.ui.editNote.EditNoteFragment

class NoteAdapter(private var notes: MutableList<NoteEntity>, private var listener: onClickListener):
    RecyclerView.Adapter<NoteAdapter.ViewHolder>(){

    private lateinit var mContext: Context
    private var mEditNote: FragmentEditNoteBinding? = null

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemNoteBinding.bind(view)
        fun setListener(noteEntity: NoteEntity) {

            with(binding.root) {

                setOnClickListener { listener.onClick(noteEntity) }
                true

            }



            binding.checkBox.setOnClickListener {
                if (binding.checkBox.isChecked) {
                    listener.onDeleteNote(noteEntity)
                }
            }
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mContext = parent.context

        val view = LayoutInflater.from(mContext).inflate(R.layout.item_note, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int){

        val note = notes.get(position)

        with(holder) {
            setListener(note)

            binding.tvName.text = note.name
            binding.tvDate.text = note.date
            binding.tvDescription.text = note.description
            binding.checkBox.isChecked = false
        }
    }

    override fun getItemCount(): Int = notes.size



    fun setNote(notes: MutableList<NoteEntity>){
        this.notes = notes
        notifyDataSetChanged()
    }

    fun update(noteEntity: NoteEntity){
        val index = notes.indexOf(noteEntity)
        if (index != -1){
            notes.set(index, noteEntity)
            notifyItemChanged(index)
        }
    }

    fun delete(noteEntity: NoteEntity){
        val index = notes.indexOf(noteEntity)
        if (index != -1){
            notes.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    fun add(noteEntity: NoteEntity) {
        if(!notes.contains(noteEntity)) {
            notes.add(noteEntity)
            notifyDataSetChanged()
        }
    }

}