package com.capstoneproject.edusign.ui.challenge

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.capstoneproject.edusign.R
import com.capstoneproject.edusign.databinding.ActivityDetailChallenge2Binding
import com.capstoneproject.edusign.databinding.ActivityDetailChallenge4Binding
import com.capstoneproject.edusign.ui.cameraChallenge.CameraForChallenge

class ActivityDetailChallenge4 : AppCompatActivity() {

    private lateinit var binding: ActivityDetailChallenge4Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailChallenge4Binding.inflate(layoutInflater)
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        setContentView(binding.root)

        binding.peragakanButton.setOnClickListener {
            val intent = Intent(this@ActivityDetailChallenge4, CameraForChallenge::class.java)
            intent.putExtra("challenge", "nenek")
            startActivity(intent)
        }
    }
}