package com.example.mynotes.presentation.fragments

import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.mynotes.R
import com.example.mynotes.presentation.adapters.RecyclerViewNotesAdapter
import com.example.mynotes.databinding.FragmentNoteBinding
import com.example.mynotes.domain.entities.Note
import com.example.mynotes.domain.utils.SwipeToDelete
import com.example.mynotes.domain.utils.hideKeyboard
import com.example.mynotes.presentation.viewmodels.NoteActivityViewModel
import com.google.android.material.transition.MaterialElevationScale
import kotlinx.coroutines.launch

class NoteFragment : Fragment(R.layout.fragment_note) {
    private lateinit var noteFragmentBinding: FragmentNoteBinding
    private val noteActivityViewModel: NoteActivityViewModel by activityViewModels()
    private lateinit var recyclerViewAdapter: RecyclerViewNotesAdapter
    private lateinit var alertDialogBuilder: AlertDialog.Builder
    private lateinit var deletedNoteAlertDialog: AlertDialog
    private var noteTemplate: Note = Note(0, "", "", "")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        exitTransition = MaterialElevationScale(true).apply {
            duration = 400
        }

        enterTransition = MaterialElevationScale(true).apply {
            duration = 400
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        noteFragmentBinding = FragmentNoteBinding.bind(view)
        val navController = Navigation.findNavController(view)

        requireView().hideKeyboard()

        noteFragmentBinding.addNote.setOnClickListener {
            noteFragmentBinding.appBarLayout.visibility = View.INVISIBLE
            navController.navigate(NoteFragmentDirections.actionNoteFragmentToSaveAndDeleteFragment())
        }

        noteFragmentBinding.innerFloatingActionButton.setOnClickListener {
            noteFragmentBinding.appBarLayout.visibility = View.INVISIBLE
            navController.navigate(NoteFragmentDirections.actionNoteFragmentToSaveAndDeleteFragment())
        }

        recyclerViewDisplay()
        swipeToDelete(noteFragmentBinding.recyclerViewNote)

        noteFragmentBinding.search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString().isNotEmpty()) {
                    val text = p0.toString()
                    val query = "%$text%"
                    if (query.isNotEmpty()) {
                        viewLifecycleOwner.lifecycleScope.launch {
                            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                                noteActivityViewModel.searchNote(query).collect {
                                    recyclerViewAdapter.submitList(it)
                                }
                            }
                        }
                    } else {
                        observeDataChanges()
                    }
                } else {
                    observeDataChanges()
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

        noteFragmentBinding.search.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                v.clearFocus()
                requireView().hideKeyboard()
            }
            return@setOnEditorActionListener true
        }

        noteFragmentBinding.recyclerViewNote.setOnScrollChangeListener { _, scrollX, scrollY, _, oldScrollY ->
            when {
                scrollY > oldScrollY -> {
                    noteFragmentBinding.chatFloatingActionButtonText.isVisible = false
                }

                scrollX == scrollY -> {
                    noteFragmentBinding.chatFloatingActionButtonText.isVisible = true
                }

                else -> {
                    noteFragmentBinding.chatFloatingActionButtonText.isVisible = true
                }
            }
        }
    }

    private fun swipeToDelete(recyclerViewNote: RecyclerView) {
        val swipeToDeleteCallback = object : SwipeToDelete() {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                recyclerViewAdapter.notifyItemMoved(
                    viewHolder.absoluteAdapterPosition, target.absoluteAdapterPosition
                )
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.absoluteAdapterPosition
                val note = recyclerViewAdapter.currentList[position]
                noteTemplate = note
                noteActivityViewModel.deleteNote(note)
                noteFragmentBinding.search.apply {
                    hideKeyboard()
                    clearFocus()
                }

                if (noteFragmentBinding.search.text.toString().isEmpty()) {
                    observeDataChanges()
                }

                alertDialogBuilder = AlertDialog.Builder(requireContext())
                deletedNoteAlertDialog = alertDialogBuilder
                    .setTitle("Delete the note ?")
                    .setPositiveButton("Yes")
                    { _, _ ->
                        deletedNoteAlertDialog.dismiss()
                    }
                    .setNegativeButton("No")
                    { _, _ ->
                        noteActivityViewModel.saveNote(noteTemplate)
                        deletedNoteAlertDialog.dismiss()
                    }
                    .create()

                deletedNoteAlertDialog.setCancelable(false)
                deletedNoteAlertDialog.show()
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(recyclerViewNote)
    }

    private fun recyclerViewDisplay() {
        when (resources.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> setUpRecyclerView(2)
            Configuration.ORIENTATION_LANDSCAPE -> setUpRecyclerView(3)
            Configuration.ORIENTATION_SQUARE -> setUpRecyclerView(3)
            Configuration.ORIENTATION_UNDEFINED -> setUpRecyclerView(2)
        }
    }

    private fun observeDataChanges() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                noteActivityViewModel.getAllNotes().collect { list ->
                    recyclerViewAdapter.submitList(list)
                }
            }
        }
    }

    private fun setUpRecyclerView(spanCount: Int) {
        noteFragmentBinding.recyclerViewNote.apply {
            layoutManager =
                StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL)
            recyclerViewAdapter = RecyclerViewNotesAdapter()
            recyclerViewAdapter.stateRestorationPolicy =
                RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            adapter = recyclerViewAdapter
            viewTreeObserver.addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }
        }
        observeDataChanges()
    }
}