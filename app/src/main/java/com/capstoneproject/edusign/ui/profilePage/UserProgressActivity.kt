package com.capstoneproject.edusign.ui.profilePage

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.capstoneproject.edusign.databinding.ActivityUserProgressBinding
import com.capstoneproject.edusign.ui.challenge.ChallengeViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class UserProgressActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserProgressBinding
    private lateinit var viewModel: UserProgressViewModel
    private val applicationScope = CoroutineScope(SupervisorJob())

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProgressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory = ChallengeViewModelFactory.getInstance(this, applicationScope)
        viewModel = ViewModelProvider(this, factory)[UserProgressViewModel::class.java]

        viewModel.completionPercentageLiveData.observe(this) { completionPercentage ->
            // Update your UI with the completion percentage
            // For example, set a TextView's text
            val roundedPercentage = completionPercentage.toInt()

            binding.progressBar.progress = roundedPercentage
            binding.textViewProgress.text = "$roundedPercentage%"
        }

    }
}