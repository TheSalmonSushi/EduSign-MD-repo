package com.capstoneproject.edusign.ui.resultPage

import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.IBinder
import android.util.Log
import android.widget.VideoView

class ResultTranslateVideoService : Service() {

    private lateinit var videoView: VideoView

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val videoUriString = intent?.getStringExtra("videoUri")
        if (videoUriString != null) {
            val videoUri = Uri.parse(videoUriString)
            videoView = VideoView(this)
            videoView.setVideoURI(videoUri)
            videoView.start()
            videoView.setOnCompletionListener { mediaPlayer ->
                mediaPlayer.start()
                mediaPlayer.isLooping = true
            }
        } else {
            // Handle the case where videoUriString is null
            Log.e(TAG, "Video Uri String is null.")
        }
        return START_NOT_STICKY

    }


    override fun onDestroy() {
        videoView.stopPlayback()
        super.onDestroy()
    }

    companion object {
        private const val TAG = "ResultTranslateService"
    }
}