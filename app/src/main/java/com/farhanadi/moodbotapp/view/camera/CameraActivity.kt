package com.farhanadi.moodbotapp.view.camera

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.farhanadi.moodbotapp.R
import com.farhanadi.moodbotapp.view.chat.ChatBotActivity
import com.farhanadi.moodbotapp.view.main.MainActivity
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

class CameraActivity : AppCompatActivity() {

    private lateinit var viewFinder: PreviewView
    private lateinit var imageCapture: ImageCapture
    private lateinit var textWait: TextView
    private lateinit var captureButton: ImageView
    private lateinit var switchButton: ImageView
    private var cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
    private var remainingAttempts = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        remainingAttempts =3 //reset attempts
        viewFinder = findViewById(R.id.viewFinder)
        textWait=findViewById(R.id.textWait)
        captureButton=findViewById(R.id.captureImage)
        switchButton=findViewById(R.id.switchCamera)
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
        findViewById<ImageView>(R.id.btn_back).setOnClickListener(){
            val intent = Intent(this@CameraActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        captureButton.setOnClickListener {
            takePhoto()
        }

        switchButton.setOnClickListener {
            switchCamera()
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(viewFinder.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder().build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun switchCamera() {
        cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA) {
            CameraSelector.DEFAULT_BACK_CAMERA
        } else {
            CameraSelector.DEFAULT_FRONT_CAMERA
        }
        startCamera()
    }

    private fun takePhoto() {
        val photoFile = File(
            getOutputDirectory(),
            SimpleDateFormat(
                FILENAME_FORMAT, Locale.getDefault()
            ).format(System.currentTimeMillis()) + ".jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        captureButton.visibility=View.GONE
        textWait.visibility = View.VISIBLE
        imageCapture.takePicture(
            outputOptions, ContextCompat.getMainExecutor(this), object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e("Photo Failed", "Photo capture failed: ${exc.message}", exc)
                    captureButton.visibility=View.VISIBLE
                    textWait.visibility = View.GONE
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    sendImageToApi(photoFile)
//                    savePhotoToGallery(photoFile)
                }
            })
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists()) mediaDir else filesDir
    }

    private fun sendImageToApi(photoFile: File) {
        imageCapture
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("image", photoFile.name, photoFile.asRequestBody("image/jpeg".toMediaType()))
            .build()

        val request = Request.Builder()
            .url("https://moodbotapps-2bfesfcx6q-as.a.run.app/api/emopredict")
            .post(requestBody)
            .build()

        val client = OkHttpClient.Builder()
            .connectTimeout(50, TimeUnit.SECONDS)
            .writeTimeout(50, TimeUnit.SECONDS)
            .readTimeout(50, TimeUnit.SECONDS)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("Failed Send to API", "Failed to send image to API", e)
                runOnUiThread {
                    Toast.makeText(this@CameraActivity, "Failed to send image to API", Toast.LENGTH_SHORT).show()
                    captureButton.visibility=View.VISIBLE
                    textWait.visibility = View.GONE
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let { responseBody ->
                    val jsonResponse = JSONObject(responseBody)
                    val apiResponse = jsonResponse.getString("prediction")
                    val status = jsonResponse.getString("status")

                    runOnUiThread {
                        if (status == "100" && remainingAttempts>0) {
                            remainingAttempts--
                            Toast.makeText(this@CameraActivity, "Attempts left: $remainingAttempts", Toast.LENGTH_SHORT).show()
                            captureButton.visibility=View.VISIBLE
                            textWait.visibility = View.GONE
                        } else {
                            handleEmotionResponse(apiResponse)
                        }
                    }
                }
            }
        })
    }


    private fun handleEmotionResponse(emotionResponse: String) {
        // Assuming emotionResponse is one of: "happy", "sad", "angry"
        val intent = Intent(this@CameraActivity, CameraConfirmActivity::class.java).apply {
            putExtra("emotion", emotionResponse)
        }
        startActivity(intent)
    }



    companion object {
        private const val TAG = "CameraActivity"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA, Manifest.permission.INTERNET)
    }
}