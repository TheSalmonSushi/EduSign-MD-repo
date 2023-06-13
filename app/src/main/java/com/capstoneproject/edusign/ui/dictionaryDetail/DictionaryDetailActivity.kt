package com.capstoneproject.edusign.ui.dictionaryDetail

import android.net.Uri
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.capstoneproject.edusign.R
import com.capstoneproject.edusign.databinding.ActivityDetailDictionaryBinding
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

class DictionaryDetailActivity : AppCompatActivity() {

    private lateinit var detailActivityBinding: ActivityDetailDictionaryBinding
    private lateinit var player: SimpleExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        detailActivityBinding = ActivityDetailDictionaryBinding.inflate(layoutInflater)
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        setContentView(detailActivityBinding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        val videoLink = intent.getStringExtra("videoLink")
        val videoUri = Uri.parse(videoLink)

        // Initialize the ExoPlayer
        val trackSelector = DefaultTrackSelector(this)
        val loadControl = DefaultLoadControl()
        player = SimpleExoPlayer.Builder(this)
            .setTrackSelector(trackSelector)
            .setLoadControl(loadControl)
            .build()

        // Prepare the MediaSource
        val userAgent = Util.getUserAgent(this, getString(R.string.app_name))
        val mediaDataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(this, userAgent)
        val mediaSource: MediaSource = ProgressiveMediaSource.Factory(mediaDataSourceFactory)
            .createMediaSource(videoUri)

        // Set the player to the PlayerView
        detailActivityBinding.playerView.player = player

        // Prepare the MediaSource and start playback
        player.setMediaSource(mediaSource)
        player.prepare()
        player.play()
    }

    override fun onStop() {
        super.onStop()
        // Release the player when the activity is stopped
        player.release()
    }
}