package com.capstoneproject.edusign.ui.dictionaryDetail

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.capstoneproject.edusign.R
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

@Suppress("DEPRECATION")
class VideoPlaybackService : Service() {

    private lateinit var player: SimpleExoPlayer
    private var isForegroundService = false

    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService(): VideoPlaybackService = this@VideoPlaybackService
    }

    override fun onCreate() {
        super.onCreate()

        val trackSelector = DefaultTrackSelector(this)
        player = SimpleExoPlayer.Builder(this)
            .setTrackSelector(trackSelector)
            .build()

        player.repeatMode = Player.REPEAT_MODE_ALL
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val videoLink = intent?.getStringExtra("videoLink")
        val videoUri = Uri.parse(videoLink)

        val userAgent = Util.getUserAgent(this, getString(R.string.app_name))
        val mediaDataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(this, userAgent)
        val mediaSource: MediaSource = ProgressiveMediaSource.Factory(mediaDataSourceFactory)
            .createMediaSource(videoUri)

        player.setMediaSource(mediaSource)
        player.prepare()
        player.play()

        if (!isForegroundService) {
            startForeground(FOREGROUND_SERVICE_ID, createNotification())
            isForegroundService = true
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }

    private fun createNotification(): Notification {
        val notificationIntent = Intent(this@VideoPlaybackService, DictionaryDetailActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Video Playback")
            .setContentText("Playing video in the background")
            .setSmallIcon(R.drawable.baseline_notifications_24)
            .setContentIntent(pendingIntent)
            .build()
    }

    companion object {
        private const val FOREGROUND_SERVICE_ID = 1
        private const val CHANNEL_ID = "VideoPlaybackChannel"
    }
}