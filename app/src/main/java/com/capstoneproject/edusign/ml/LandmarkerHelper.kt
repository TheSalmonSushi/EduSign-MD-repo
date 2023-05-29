package com.capstoneproject.edusign.ml

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.core.Delegate
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarker
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarkerResult
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import java.util.concurrent.ExecutionException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

class LandmarkerHelper(
    private var minDetectionConfidence: Float,
    private var minTrackingConfidence: Float,
    private var minPresenceConfidence: Float,
    private val context: Context,
) {
    private var handLandmarker: HandLandmarker? = null
    private var poseLandmarker: PoseLandmarker? = null
    private var faceLandmarker: FaceLandmarker? = null

    init {
        setupLandmarker()
    }

    fun clearLandmarker() {
        handLandmarker?.close()
        handLandmarker = null
        poseLandmarker?.close()
        poseLandmarker = null
        faceLandmarker?.close()
        faceLandmarker = null
    }

    private fun setupLandmarker() {
        val executor: ExecutorService = Executors.newFixedThreadPool(3)
        val tasks = mutableListOf<Future<*>>()

        val setupHand = executor.submit<Unit> {
            val handBaseOptionBuilder = BaseOptions.builder()
            handBaseOptionBuilder.setModelAssetPath(MP_HAND_LANDMARKER_TASK)
            handBaseOptionBuilder.setDelegate(Delegate.GPU)

            val handBaseOptions = handBaseOptionBuilder.build()

            val handOptionsBuilder =
                HandLandmarker.HandLandmarkerOptions.builder()
                    .setBaseOptions(handBaseOptions)
                    .setMinHandDetectionConfidence(minDetectionConfidence)
                    .setMinTrackingConfidence(minTrackingConfidence)
                    .setMinHandPresenceConfidence(minPresenceConfidence)
                    .setNumHands(2)
                    .setRunningMode(RunningMode.VIDEO)

            handLandmarker =
                HandLandmarker.createFromOptions(context,handOptionsBuilder.build())
        }

        val setupPose = executor.submit<Unit> {
            val poseBaseOptionBuilder = BaseOptions.builder()
            poseBaseOptionBuilder.setModelAssetPath(MP_POSE_LANDMARKER_LITE)
            poseBaseOptionBuilder.setDelegate(Delegate.GPU)

            val poseBaseOptions = poseBaseOptionBuilder.build()

            val poseOptionsBuilder =
                PoseLandmarker.PoseLandmarkerOptions.builder()
                    .setBaseOptions(poseBaseOptions)
                    .setMinPoseDetectionConfidence(minDetectionConfidence)
                    .setMinTrackingConfidence(minTrackingConfidence)
                    .setMinPosePresenceConfidence(minPresenceConfidence)
                    .setRunningMode(RunningMode.VIDEO)

            poseLandmarker =
                PoseLandmarker.createFromOptions(context, poseOptionsBuilder.build())
        }

        val setupFace = executor.submit<Unit> {
            val faceBaseOptionBuilder = BaseOptions.builder()
            faceBaseOptionBuilder.setModelAssetPath(MP_FACE_LANDMARKER)
            faceBaseOptionBuilder.setDelegate(Delegate.GPU)

            val faceBaseOptions = faceBaseOptionBuilder.build()

            val faceOptionsBuilder =
                FaceLandmarker.FaceLandmarkerOptions.builder()
                    .setBaseOptions(faceBaseOptions)
                    .setMinFaceDetectionConfidence(minDetectionConfidence)
                    .setMinTrackingConfidence(minTrackingConfidence)
                    .setMinFacePresenceConfidence(minPresenceConfidence)
                    .setRunningMode(RunningMode.VIDEO)

            faceLandmarker =
                FaceLandmarker.createFromOptions(context, faceOptionsBuilder.build())
        }

        tasks.add(setupHand)
        tasks.add(setupPose)
        tasks.add(setupFace)

        try {
            tasks.forEach { it.get() } // Waiting task to finish
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        } finally {
            executor.shutdown()
        }
    }

    fun getLandmarks(
        videoUri: Uri,
        inferenceIntervalMs: Long
    ): List<List<Float>>? {
        //Create 3 thread
        val executor: ExecutorService = Executors.newFixedThreadPool(3)

        // Load frames from the video and run the hand landmarker.
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, videoUri)
        val videoLengthMs =
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                ?.toLong()

        // Note: We need to read width/height from frame instead of getting the width/height
        // of the video directly because MediaRetriever returns frames that are smaller than the
        // actual dimension of the video file.
        val firstFrame = retriever.getFrameAtTime(0)
        val width = firstFrame?.width
        val height = firstFrame?.height

        // If the video is invalid, returns a null detection result
        if ((videoLengthMs == null) || (width == null) || (height == null)) return null

        //Get one frame every frameInterval ms, then run detection on these frames.
        val handResultList = mutableListOf<HandLandmarkerResult>()
        val leftHandResultList = mutableListOf<List<Float>>()
        val rightHandResultList = mutableListOf<List<Float>>()
        val poseResultList = mutableListOf<List<Float>>()
        val lipsResultList = mutableListOf<List<Float>>()
        val numberOfFrameToRead = videoLengthMs.div(inferenceIntervalMs)

        val tasks = mutableListOf<Future<*>>()
        var frameCount = 0
        for (i in 0..numberOfFrameToRead) {
            val timestampMs = i * inferenceIntervalMs // ms

            retriever
                .getFrameAtTime(
                    timestampMs * 1000, // convert from ms to micro-s
                    MediaMetadataRetriever.OPTION_CLOSEST
                )
                ?.let { frame ->
                    // Convert the video frame to ARGB_8888 which is required by the MediaPipe
                    val argb8888Frame =
                        if (frame.config == Bitmap.Config.ARGB_8888) frame
                        else frame.copy(Bitmap.Config.ARGB_8888, false)

                    // Convert the input Bitmap object to an MPImage object to run inference
                    val mpImage = BitmapImageBuilder(argb8888Frame).build()

                    // Run landmarker using MediaPipe Landmarker API asynchronous
                    val handTask = executor.submit<Unit> {
                        handLandmarker?.detectForVideo(mpImage, timestampMs)
                            ?.let {
                                synchronized(handResultList) {
                                    val handResult = extractHand(it)
                                    leftHandResultList.add(handResult.left)
                                    rightHandResultList.add(handResult.right)
                                    handResultList.add(it)
                                }
                            }
                    }

                    val poseTask = executor.submit<Unit> {
                        poseLandmarker?.detectForVideo(mpImage, timestampMs)
                            ?.let {
                                synchronized(poseResultList) {
                                    poseResultList.add(extractPose(it))
                                }
                            }
                    }

                    val faceTask = executor.submit<Unit> {
                        faceLandmarker?.detectForVideo(mpImage, timestampMs)
                            ?.let {
                                synchronized(lipsResultList) {
                                    lipsResultList.add(extractLips(it))
                                }
                            }
                    }

                    tasks.add(handTask)
                    tasks.add(poseTask)
                    tasks.add(faceTask)
                }
            frameCount++
            if (frameCount == MAX_FRAME) { break }
        }

        try {
            tasks.forEach { it.get() } // Waiting task to finish
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        } finally {
            retriever.release()
            executor.shutdown()
        }
        return flattenLandmark(
            leftHandResultList,rightHandResultList,
            poseResultList, lipsResultList
        )
    }

    private fun flattenLandmark(
        leftHandLandmark: List<List<Float>>,
        rightHandLandmark: List<List<Float>>,
        poseLandmark: List<List<Float>>,
        lipsLandmark: List<List<Float>>
    ): List<List<Float>> {
        val data = mutableListOf<List<Float>>()
        for (frame in 0 until MAX_FRAME) {
            val landmark = mutableListOf<Float>()
            landmark.addAll(lipsLandmark[frame])
            landmark.addAll(leftHandLandmark[frame])
            landmark.addAll(poseLandmark[frame])
            landmark.addAll(rightHandLandmark[frame])
            data.add(landmark)
        }
        return data
    }

    private fun extractHand(handLandmark: HandLandmarkerResult): Hand {
        val rh = mutableListOf<Float>()
        val lh = mutableListOf<Float>()

        //Check number of hand
        if (handLandmark.handednesses().size == 1) { //One hand detected
            //Left or Right?
            if(handLandmark.handednesses()[0][0].index() == 0) {
                for (landmark in handLandmark.worldLandmarks()[0]) { // Loop to get X, Y, Z coordinates
                    rh.add(landmark.x())
                    lh.add(0.0F)
                    rh.add(landmark.y())
                    lh.add(0.0F)
                    rh.add(landmark.z())
                    lh.add(0.0F)
                }
            }else{
                for (landmark in handLandmark.worldLandmarks()[0]) { // Loop to get X, Y, Z coordinates
                    rh.add(0.0F)
                    lh.add(landmark.x())
                    rh.add(0.0F)
                    lh.add(landmark.y())
                    rh.add(0.0F)
                    lh.add(landmark.z())
                }
            }

        }else if (handLandmark.handednesses().size == 2){ //Two hand detected
            for (landmark in handLandmark.worldLandmarks()[0]){
                rh.add(landmark.x())
                rh.add(landmark.y())
                rh.add(landmark.z())
            }
            for (landmark in handLandmark.worldLandmarks()[1]){
                lh.add(landmark.x())
                lh.add(landmark.y())
                lh.add(landmark.z())
            }
        }
        return Hand(lh, rh)
    }

    private fun extractPose(poseLandmark: PoseLandmarkerResult): List<Float> {
        val pose = mutableListOf<Float>()

        for (landmark in poseLandmark.worldLandmarks()[0]) {
            pose.add(landmark.x())
            pose.add(landmark.y())
            pose.add(landmark.z())
        }
        return pose
    }

    private fun extractLips(faceLandmark: FaceLandmarkerResult): List<Float> {
        val lips = mutableListOf<Float>()
        val idxLips = listOf( // Index of lips
            61, 185, 40, 39, 37, 0, 267, 269, 270, 409, 291,
            146, 91, 181, 84, 17, 314, 405, 321, 375, 291,
            78, 191, 80, 81, 82, 13, 312, 311, 310, 415, 308,
            78, 95, 88, 178, 87, 14, 317, 402, 318, 324, 308
        )

        for (idx in idxLips) {
            lips.add(faceLandmark.faceLandmarks()[0][idx].x())
            lips.add(faceLandmark.faceLandmarks()[0][idx].y())
            lips.add(faceLandmark.faceLandmarks()[0][idx].z())
        }
        return lips
    }

    data class Hand(
        val left: List<Float>,
        val right: List<Float>
    )

    companion object {
        private const val MP_HAND_LANDMARKER_TASK = "hand_landmarker.task"
        private const val MP_POSE_LANDMARKER_LITE = "pose_landmarker_lite.task"
        private const val MP_FACE_LANDMARKER = "face_landmarker.task"
        private const val MAX_FRAME = 30
    }
}
