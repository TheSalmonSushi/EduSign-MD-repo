package com.capstoneproject.edusign.ui.homeActivity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
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
        installSplashScreen()
        homeActivityBinding = ActivityHomeBinding.inflate(layoutInflater)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        setContentView(homeActivityBinding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        fragmentChallenge = ChallengeFragment()
        fragmentSwitch(fragmentChallenge!!)

        val fragmentDictionary = DictionaryFragment()

        when (intent.getIntExtra("selectedFragment", R.id.navigation_challenge)) {
            R.id.navigation_challenge -> fragmentSwitch(fragmentChallenge!!)
            R.id.navigation_dictionary -> fragmentSwitch(fragmentDictionary)
        }

        homeActivityBinding.bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
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

        homeActivityBinding.bottomAppBar.setOnApplyWindowInsetsListener { _, insets ->
            val layoutParams =
                homeActivityBinding.bottomAppBar.layoutParams as CoordinatorLayout.LayoutParams
            layoutParams.bottomMargin = insets.systemWindowInsetBottom
            insets
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            hideSystemBars()
        }
    }

    @SuppressLint("WrongConstant")
    @RequiresApi(Build.VERSION_CODES.R)
    private fun hideSystemBars() {
        val controller = window.insetsController
        controller?.systemBarsBehavior =
            WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE or
                    WindowInsetsController.BEHAVIOR_SHOW_BARS_BY_SWIPE
        controller?.hide(WindowInsets.Type.navigationBars())
    }

    private fun fragmentSwitch(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }

}
