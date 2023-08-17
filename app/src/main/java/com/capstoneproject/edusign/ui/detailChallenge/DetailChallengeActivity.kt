package com.capstoneproject.edusign.ui.detailChallenge

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.capstoneproject.edusign.data.db.ChallengeWithQuestions
import com.capstoneproject.edusign.databinding.ActivityChallengeDetailBinding
import com.capstoneproject.edusign.ui.cameraChallenge.ChallengeCameraActivity
import com.capstoneproject.edusign.ui.challenge.ChallengeViewModelFactory
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class DetailChallengeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChallengeDetailBinding
    private lateinit var viewModel: DetailChallengeViewModel
    private lateinit var  challengeWithQuestions: ChallengeWithQuestions
    private lateinit var adapter: DetailChallengeAdapter
    private val applicationScope = CoroutineScope(SupervisorJob())

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            if (data != null) {
                validateAnswer(data.getBooleanExtra(RESULT_ANS_EXTRA, false), data.getIntExtra(
                    VALIDATE_ANS_EXTRA, 0))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChallengeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val challengeId = intent.getIntExtra(CHALLENGEID_EXTRA, 0)

        val factory = ChallengeViewModelFactory.getInstance(this, applicationScope)
        viewModel = ViewModelProvider(this, factory)[DetailChallengeViewModel::class.java]

        viewModel.setChallengesId(challengeId)

        viewModel.challengesWithQuestions.observe(this) {
            binding.tvTitleChallenge.text = it.challenge.name

            challengeWithQuestions = it

            val adapter = DetailChallengeAdapter(it.questions.toMutableList(), OnIntentToCamera {question ->
                val intent = Intent(this, ChallengeCameraActivity::class.java)
                intent.putExtra(ChallengeCameraActivity.KATA_EXTRA, question)
                resultLauncher.launch(intent)
            })

            this.adapter = adapter
            binding.viewPager.adapter = adapter

            val storedIndex = viewModel.getCurrentItemIndex()
            binding.viewPager.setCurrentItem(storedIndex, false)

            TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
                tab.text = "Soal ${position + 1}"
            }.attach()
        }
    }

    private fun validateAnswer(isAnsCorrect: Boolean, challengeId: Int) {
        val question = challengeWithQuestions.questions.first { it.id == challengeId }
        val index = challengeWithQuestions.questions.indexOf(question)
        question.isAnswered = isAnsCorrect
        adapter.update(question, index)
        viewModel.updateQuestion(question)

        // Save the current item index
        val currentItemIndex = binding.viewPager.currentItem
        viewModel.setCurrentItemIndex(currentItemIndex)

    }

    companion object {
        private const val TAG = "detailChallenge"
        const val VALIDATE_ANS_EXTRA = "validateAnswerExtra"
        const val RESULT_ANS_EXTRA = "resultAnswerExtra"
        const val CHALLENGEID_EXTRA = "challengeIdExtra"
    }
}