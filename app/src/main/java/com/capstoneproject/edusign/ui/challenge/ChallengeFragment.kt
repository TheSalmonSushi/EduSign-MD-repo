package com.capstoneproject.edusign.ui.challenge

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import com.capstoneproject.edusign.R
import com.capstoneproject.edusign.databinding.FragmentChallengeBinding


class ChallengeFragment : Fragment() {

    private var _binding: FragmentChallengeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChallengeBinding.inflate(inflater, container, false)
        return binding.root
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindButtons()
    }


    private fun bindButtons() {
        binding.buttonCat1.setOnClickListener {
            val intent = Intent(activity, ActivityDetailChallenge::class.java)
            startActivity(intent)
        }

        binding.buttonCat2.setOnClickListener {
            val intent = Intent(activity, ActivityDetailChallenge2::class.java)
            startActivity(intent)
        }

        binding.buttonCat3.setOnClickListener {
            val intent = Intent(activity, ActivityDetailChallenge3::class.java)
            startActivity(intent)
        }

        binding.buttonCat4.setOnClickListener {
            val intent = Intent(activity, ActivityDetailChallenge4::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}