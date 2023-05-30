package com.capstoneproject.edusign.ui.resultPage

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.capstoneproject.edusign.databinding.ActivityResultTranslateBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class ResultTranslateActivity : AppCompatActivity() {

    private lateinit var resultTranslateBinding: ActivityResultTranslateBinding
    private lateinit var resultTranslateViewModel: ResultTranslateViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resultTranslateBinding = ActivityResultTranslateBinding.inflate(layoutInflater)
        setContentView(resultTranslateBinding.root)

        resultTranslateViewModel = ViewModelProvider(this)[ResultTranslateViewModel::class.java]

        setupAction()

        resultTranslateViewModel.predictionLiveData.observe(this) { prediction ->
            // Tangani hasil prediksi di sini
            if (prediction != null) {
                val predictionList = prediction.prediction
                val valueList = prediction.value

                // Tampilkan hasil prediksi di TextView
                val predictionResult = buildPredictionResultText(predictionList, valueList)
                resultTranslateBinding.translateResult.text = predictionResult
            }


        }
        resultTranslateViewModel.errorLiveData.observe(this) { error ->
            Toast.makeText(this, "Error: $error", Toast.LENGTH_SHORT).show()
        }




    }

    private fun uriToFile(uri: Uri): File? {
        val filePathColumn = arrayOf(MediaStore.Video.Media.DATA)
        val cursor = contentResolver.query(uri, filePathColumn, null, null, null)
        cursor?.moveToFirst()

        val columnIndex = cursor?.getColumnIndex(filePathColumn[0])
        val filePath = cursor?.getString(columnIndex ?: -1)
        cursor?.close()

        return filePath?.let { File(it) }
    }


    private fun setupAction() {
        val videoUri: Uri? = intent.getParcelableExtra("uri")
        val videoFile: File? = videoUri?.let { uriToFile(it) }


        if (videoFile != null && videoFile.exists()) {
            val videoRequestBody = videoFile.asRequestBody("video/mp4".toMediaTypeOrNull())
            val videoPart =
                MultipartBody.Part.createFormData("video", videoFile.name, videoRequestBody)
            resultTranslateViewModel.uploadVideo(videoPart)
        } else {
            Toast.makeText(this, "Error: ingfo ini error cuii", Toast.LENGTH_SHORT).show()
        }
    }

    private fun buildPredictionResultText(
        predictionList: List<String>,
        valueList: List<String>
    ): String {
        val sb = StringBuilder()
        for (i in predictionList.indices) {
            val prediction = predictionList[i]
            val value = valueList[i]
            sb.append("$prediction: $value\n")
        }
        return sb.toString()
    }

}