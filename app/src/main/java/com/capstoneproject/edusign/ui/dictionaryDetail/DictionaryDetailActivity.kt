package com.capstoneproject.edusign.ui.dictionaryDetail

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.capstoneproject.edusign.R
import com.capstoneproject.edusign.databinding.ActivityDetailDictionaryBinding
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

@Suppress("DEPRECATION")
class DictionaryDetailActivity : AppCompatActivity() {

    private lateinit var detailActivityBinding: ActivityDetailDictionaryBinding
    private lateinit var player: SimpleExoPlayer
    private lateinit var videoPlaybackService: VideoPlaybackService
    private var isServiceBound = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as VideoPlaybackService.LocalBinder
            videoPlaybackService = binder.getService()
            isServiceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isServiceBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        detailActivityBinding = ActivityDetailDictionaryBinding.inflate(layoutInflater)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        setContentView(detailActivityBinding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        val videoLink = intent.getStringExtra("videoLink")
        val videoUri = Uri.parse(videoLink)

        val dictionaryWord = intent.getStringExtra("dictWord")

        val trackSelector = DefaultTrackSelector(this)
        player = SimpleExoPlayer.Builder(this)
            .setTrackSelector(trackSelector)
            .build()

        player.repeatMode = Player.REPEAT_MODE_ALL

        val userAgent = Util.getUserAgent(this, getString(R.string.app_name))
        val mediaDataSourceFactory = DefaultDataSourceFactory(this, userAgent)
        val mediaSource = ProgressiveMediaSource.Factory(mediaDataSourceFactory)
            .createMediaSource(videoUri)

        detailActivityBinding.dictionaryWord.text = dictionaryWord

        detailActivityBinding.playerView.player = player

        player.setMediaSource(mediaSource)
        player.prepare()
        player.play()
    }

    override fun onStart() {
        super.onStart()
        val serviceIntent = Intent(this, VideoPlaybackService::class.java)
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()
        if (isServiceBound) {
            unbindService(serviceConnection)
            isServiceBound = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }
}
