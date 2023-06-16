package com.capstoneproject.edusign.ui.challenge

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import com.capstoneproject.edusign.databinding.ActivityDetailChallengeBinding
import com.capstoneproject.edusign.ui.cameraChallenge.CameraForChallenge


class ActivityDetailChallenge : AppCompatActivity() {

    private lateinit var binding: ActivityDetailChallengeBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailChallengeBinding.inflate(layoutInflater)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        setContentView(binding.root)

        binding.peragakanButton.setOnClickListener {
            val intent = Intent(this@ActivityDetailChallenge, CameraForChallenge::class.java)
            intent.putExtra("challenge", "gajah")
            startActivity(intent)
        }


    }


}
