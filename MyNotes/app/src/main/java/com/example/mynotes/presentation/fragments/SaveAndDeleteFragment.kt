package com.example.mynotes.presentation.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mynotes.R
import com.example.mynotes.presentation.adapters.ColorBoxesAdapter
import com.example.mynotes.databinding.BottomSheetLayoutBinding
import com.example.mynotes.databinding.FragmentSaveAndDeleteBinding
import com.example.mynotes.domain.entities.Note
import com.example.mynotes.domain.utils.Environment
import com.example.mynotes.domain.utils.hideKeyboard
import com.example.mynotes.presentation.viewmodels.NoteActivityViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Random

class SaveAndDeleteFragment : Fragment(R.layout.fragment_save_and_delete) {

    private lateinit var navController: NavController
    private lateinit var contentBinding: FragmentSaveAndDeleteBinding
    private var note: Note? = null
    private var color = -1
    private lateinit var result: String
    private val formatter = SimpleDateFormat("dd.MM.yyyy 'at' HH:mm:ss", Locale("ru", "RU"))
    private val dateString = formatter.format(Date())
    private val noteActivityViewModel: NoteActivityViewModel by activityViewModels()
    private val args: SaveAndDeleteFragmentArgs by navArgs()
    private lateinit var temporaryColorHEX: String
    private val setOfUniqueColors = mutableSetOf<String>()
    private val random = Random()
    private var temporaryColorARGB = 0
    private var colorArray: IntArray = intArrayOf()
    private val colorHashSet = hashSetOf<Int>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        contentBinding = FragmentSaveAndDeleteBinding.bind(view)
        navController = Navigation.findNavController(view)

        contentBinding.backButton.setOnClickListener {
            requireView().hideKeyboard()
            navController.popBackStack()
        }

        contentBinding.lastEdited.text = getString(R.string.edited_on, dateString)

        contentBinding.saveNote.setOnClickListener {
            saveNote()
        }

        contentBinding.editTextNoteContent.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) contentBinding.floatingActionButtonColorPick.visibility = View.GONE
            else contentBinding.floatingActionButtonColorPick.visibility = View.VISIBLE
        }

        setUpUniqueColors()

        contentBinding.floatingActionButtonColorPick.setOnClickListener {
            setupSelectedColor()
        }
        setUpNote()
    }

    private fun setUpNote() {
        val note = args.note
        val title = contentBinding.editTextTitle
        val content = contentBinding.editTextNoteContent
        val lastEdited = contentBinding.lastEdited

        if (note == null) contentBinding.lastEdited.text = getString(R.string.edited_on, dateString)
        else {
            title.setText(note.title)
            content.setText(note.content)
            lastEdited.text = getString(R.string.edited_on, note.date)
            color = note.color
            contentBinding.apply {
                noteContentFragmentParent.setBackgroundColor(color)
            }
        }
    }

    private fun setUpUniqueColors() {
        for (i in colorArray.indices) {
            temporaryColorARGB =
                Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256))
            temporaryColorHEX = java.lang.String.format("#%06X", 0xFFFFFF and temporaryColorARGB)
            if (!setOfUniqueColors.add(temporaryColorHEX))
                continue
            colorArray[i] = temporaryColorARGB
        }
    }

    private fun setupSelectedColor() {
        val bottomSheetDialog = BottomSheetDialog(
            requireContext(), R.style.BottomSheetDialogTheme
        )

        val bottomSheetView: View = layoutInflater.inflate(
            R.layout.bottom_sheet_layout, null
        )

        with(bottomSheetDialog) {
            setContentView(bottomSheetView)
            show()
        }

        val bottomSheetBinding = BottomSheetLayoutBinding.bind(bottomSheetView)
        while (colorHashSet.size != 60) {
            colorHashSet.add(Random().nextInt(60))
        }

        colorArray = colorHashSet.toIntArray()
        setUpUniqueColors()
        bottomSheetBinding.apply {

            val viewArrayList = arrayListOf<View>()

            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    for (i in 0 until 60) {
                        viewArrayList.add(View(requireContext()))
                    }
                }
            }

            val layoutManager =
                LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
            val adapter = ColorBoxesAdapter(viewArrayList, colorArray) { colorCode ->
                when (colorCode) {
                    Environment.colorCode -> contentBinding.apply {
                        noteContentFragmentParent.setBackgroundColor(Environment.colorState)
                        bottomSheetContainer.setBackgroundColor(Environment.colorState)
                        bottomSheetParent.setCardBackgroundColor(Environment.colorState)
                        bottomSheetParent.setBackgroundColor(Environment.colorState)
                        color = Environment.colorState
                    }
                }
            }
            rvTest.layoutManager = layoutManager
            rvTest.adapter = adapter
            bottomSheetParent.setBackgroundColor(color)
        }
        bottomSheetView.post {
            bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun saveNote() {
        if (contentBinding.editTextNoteContent.text.toString()
                .isEmpty() || contentBinding.editTextTitle.text.toString().isEmpty()
        ) {
            Toast.makeText(activity, "Content or title is empty", Toast.LENGTH_SHORT).show()
        } else {
            note = args.note
            when (note) {
                null -> {
                    noteActivityViewModel.saveNote(
                        Note(
                            0,
                            contentBinding.editTextTitle.text.toString(),
                            contentBinding.editTextNoteContent.text.toString(),
                            dateString,
                            color
                        )
                    )
                    result = "Note saved"
                    setFragmentResult(
                        "key", bundleOf("bundleKey" to result)
                    )
                    navController.navigate(SaveAndDeleteFragmentDirections.actionSaveAndDeleteFragmentToNoteFragment())
                }

                else -> {
                    updateNote()
                    navController.popBackStack()
                }
            }
        }
    }

    private fun updateNote() {
        note?.let {
            noteActivityViewModel.updateNote(
                Note(
                    it.id,
                    contentBinding.editTextTitle.text.toString(),
                    contentBinding.editTextNoteContent.text.toString(),
                    dateString,
                    color
                )
            )
        }
    }
}