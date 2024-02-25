package com.example.mynotes.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mynotes.R
import com.example.mynotes.databinding.NoteItemLayoutBinding
import com.example.mynotes.domain.entities.Note
import com.example.mynotes.domain.utils.hideKeyboard
import com.example.mynotes.presentation.fragments.NoteFragmentDirections
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView

class DiffUtilCallback : DiffUtil.ItemCallback<Note>() {
    override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
        return oldItem == newItem
    }
}

class RecyclerViewNotesAdapter :
    ListAdapter<Note, RecyclerViewNotesAdapter.NotesViewHolder>(DiffUtilCallback()) {

    inner class NotesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val contentBinding = NoteItemLayoutBinding.bind(itemView)
        val title: MaterialTextView = contentBinding.noteItemTitle
        val content: TextView = contentBinding.noteContentItem
        val date: MaterialTextView = contentBinding.noteDate
        val parent: MaterialCardView = contentBinding.noteItemLayoutParent
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        return NotesViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.note_item_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        getItem(position).let { note ->
            holder.apply {
                title.text = note.title
                content.text = note.content
                date.text = note.date
                parent.setCardBackgroundColor(note.color)

                itemView.setOnClickListener {
                    val action = NoteFragmentDirections.actionNoteFragmentToSaveAndDeleteFragment()
                        .setNote(note)
                    it.hideKeyboard()
                    Navigation.findNavController(it).navigate(action)
                }

                content.setOnClickListener {
                    val action = NoteFragmentDirections.actionNoteFragmentToSaveAndDeleteFragment()
                        .setNote(note)
                    it.hideKeyboard()
                    Navigation.findNavController(it).navigate(action)
                }
            }
        }
    }
}