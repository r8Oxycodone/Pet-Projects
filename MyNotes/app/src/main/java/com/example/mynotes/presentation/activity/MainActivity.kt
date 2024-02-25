package com.example.mynotes.presentation.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.ViewModelProvider
import com.example.mynotes.databinding.ActivityMainBinding
import com.example.mynotes.domain.db.NoteDatabase
import com.example.mynotes.data.repository.NoteRepository
import com.example.mynotes.presentation.viewmodels.NoteActivityViewModel
import com.example.mynotes.presentation.viewmodels.NoteActivityViewModelFactory

class MainActivity : AppCompatActivity() {
    private lateinit var noteActivityViewModel: NoteActivityViewModel
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        WindowCompat.setDecorFitsSystemWindows(window, false)
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())

        supportActionBar?.hide()
        binding = ActivityMainBinding.inflate(layoutInflater)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemGestures())
            // Apply the insets as padding to the view. Here we're setting all of the
            // dimensions, but apply as appropriate to your layout. You could also
            // update the views margin if more appropriate.
            view.updatePadding(insets.left, insets.top, insets.right, insets.bottom)

            // Return CONSUMED if we don't want the window insets to keep being passed
            // down to descendant views.
            WindowInsetsCompat.CONSUMED
        }

        setContentView(binding.root)

        val noteRepository = NoteRepository(NoteDatabase.createDatabase(this))
        val noteActivityViewModelFactory = NoteActivityViewModelFactory(noteRepository)

        noteActivityViewModel = ViewModelProvider(
            this, noteActivityViewModelFactory
        )[NoteActivityViewModel::class.java]
    }
}