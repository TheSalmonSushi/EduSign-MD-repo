package com.capstoneproject.edusign.ui.challenge

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstoneproject.edusign.R
import com.capstoneproject.edusign.data.model.ChallengePicture
import com.capstoneproject.edusign.databinding.FragmentChallengeBinding


class ChallengeFragment : Fragment(), ChallengeAdapter.OnItemClickCallback {

    private var _binding: FragmentChallengeBinding? = null
    private val binding get() = _binding!!
    private lateinit var challengeAdapter: ChallengeAdapter

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
        challengeAdapter = ChallengeAdapter(ArrayList())
        challengeAdapter.setOnitemClickCallback(this)
        binding.rvMain.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMain.adapter = challengeAdapter

        // Prepare the data for the adapter and set it
        val challengeList = getChallengeList()
        challengeAdapter.setChallengeData(challengeList)

    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    override fun onItemClicked(data: ChallengePicture) {
        val intent: Intent
        when (data.name) {
            "Hewan" -> {
                intent = Intent(requireContext(), ActivityDetailChallenge::class.java)
            }
            "Anggota \nTubuh" -> {
                intent = Intent(requireContext(), ActivityDetailChallenge2::class.java)
            }
            "Warna" -> {
                intent = Intent(requireContext(), ActivityDetailChallenge3::class.java)
            }
            "Keluarga" -> {
                intent = Intent(requireContext(), ActivityDetailChallenge4::class.java)
            }
            else -> return
        }
        startActivity(intent)
    }

    private fun getChallengeList(): ArrayList<ChallengePicture> {
        val challengeList = ArrayList<ChallengePicture>()
        val photoArray = resources.obtainTypedArray(R.array.data_photo)
        val nameArray = resources.getStringArray(R.array.data_name)

        for (i in 0 until nameArray.size) {
            val photoResId = photoArray.getResourceId(i, 0)
            val challenge = ChallengePicture(nameArray[i], photoResId)
            challengeList.add(challenge)
        }

        photoArray.recycle()

        return challengeList
    }
}