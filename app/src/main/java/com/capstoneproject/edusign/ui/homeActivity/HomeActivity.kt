package com.capstoneproject.edusign.ui.homeActivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.capstoneproject.edusign.R
import com.capstoneproject.edusign.databinding.ActivityHomeBinding
import com.capstoneproject.edusign.ui.camera.MainActivity
import com.capstoneproject.edusign.ui.challenge.ChallengeFragment
import com.capstoneproject.edusign.ui.dictionary.DictionaryFragment

@Suppress("DEPRECATION")
class HomeActivity : AppCompatActivity() {

    private lateinit var homeActivityBinding: ActivityHomeBinding
    private var fragmentChallenge: ChallengeFragment? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeActivityBinding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(homeActivityBinding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        fragmentChallenge = ChallengeFragment()
        fragmentSwitch(fragmentChallenge!!)

        val fragmentDictionary = DictionaryFragment()

        val selectedFragment = intent.getIntExtra("selectedFragment", R.id.navigation_challenge)
        when (selectedFragment) {
            R.id.navigation_challenge -> fragmentSwitch(fragmentChallenge!!)
            R.id.navigation_dictionary -> fragmentSwitch(fragmentDictionary)
        }

        homeActivityBinding.bottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.navigation_challenge -> {
                    fragmentSwitch(fragmentChallenge!!)
                    true
                }
                R.id.navigation_dictionary -> {
                    fragmentSwitch(fragmentDictionary)
                    true
                }
                else -> false
            }

        }

        homeActivityBinding.fab.setOnClickListener {
            val intent = Intent(this@HomeActivity, MainActivity::class.java)
            startActivity(intent)
        }

        homeActivityBinding.bottomNavigationView.background = null


    }

    private fun fragmentSwitch(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }


    companion object{
        private const val TAG = "HomeActivity"
    }

}
