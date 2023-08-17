package com.capstoneproject.edusign.ui.challenge

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstoneproject.edusign.R
import com.capstoneproject.edusign.data.model.ChallengePicture
import com.capstoneproject.edusign.databinding.FragmentChallengeBinding
import com.capstoneproject.edusign.ui.detailChallenge.DetailChallengeActivity
import com.capstoneproject.edusign.ui.profilePage.UserProgressActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob


class ChallengeFragment : Fragment() {

    private var _binding: FragmentChallengeBinding? = null
    private val binding get() = _binding!!
    private lateinit var challengeAdapter: ChallengeAdapter

    private lateinit var challengeViewModel: ChallengeViewModel
    private val applicationScope = CoroutineScope(SupervisorJob())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentChallengeBinding.inflate(inflater, container, false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up the RecyclerView and adapter
        challengeAdapter = ChallengeAdapter(ArrayList(), ArrayList())
        binding.rvMain.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMain.adapter = challengeAdapter

        // Prepare the data for the adapter and set it
        val challengeList = getChallengeList()


        val factory = ChallengeViewModelFactory.getInstance(requireContext(), applicationScope)
        challengeViewModel = ViewModelProvider(this, factory)[ChallengeViewModel::class.java]

        challengeViewModel.challengeList.observe(viewLifecycleOwner) {
            //Log.d("DB", "$it")

            challengeAdapter.setChallengeData(challengeList, it)
            challengeAdapter.setOnitemClickCallback(object: ChallengeAdapter.OnItemClickCallback{
                override fun onItemClicked(data: Int) {
                    val intent = Intent(requireContext(), DetailChallengeActivity::class.java)
                    intent.putExtra(DetailChallengeActivity.CHALLENGEID_EXTRA, data)
                    startActivity(intent)
                }

            })
        }

        binding.welcomeImage.setOnClickListener{
            val intent = Intent(requireContext(), UserProgressActivity::class.java)
            startActivity(intent)

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getChallengeList(): ArrayList<ChallengePicture> {
        val challengeList = ArrayList<ChallengePicture>()
        val photoArray = resources.obtainTypedArray(R.array.data_photo)


        for (i in 0 until photoArray.length()) {
            val photoResId = photoArray.getResourceId(i, 0)
            val challenge = ChallengePicture(photoResId)
            challengeList.add(challenge)
        }

        photoArray.recycle()

        return challengeList
    }
}