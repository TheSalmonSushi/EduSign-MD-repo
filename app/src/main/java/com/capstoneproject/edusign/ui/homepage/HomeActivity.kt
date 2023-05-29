package com.capstoneproject.edusign.ui.homepage


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.capstoneproject.edusign.databinding.ActivityHomeBinding
import com.capstoneproject.edusign.ml.LandmarkerHelper
import com.capstoneproject.edusign.ui.camera.MainActivity

class HomeActivity : AppCompatActivity() {

    private lateinit var homeActivityBinding: ActivityHomeBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeActivityBinding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(homeActivityBinding.root)

        homeActivityBinding.fab.setOnClickListener {
            val intent = Intent(this@HomeActivity, MainActivity::class.java)
            startActivity(intent)
        }

        homeActivityBinding.bottomNavigationView.background = null // hide abnormal layer in bottom nav




    }


    companion object{
        private const val TAG = "HomeActivity"
    }

}
