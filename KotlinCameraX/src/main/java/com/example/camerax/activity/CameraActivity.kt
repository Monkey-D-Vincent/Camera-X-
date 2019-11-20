package com.example.camerax.activity

import android.content.Intent
import android.hardware.Camera
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.*
import android.view.ViewGroup
import androidx.camera.core.*
import com.bumptech.glide.Glide
import com.example.camerax.R
import com.example.camerax.base.BaseActivity
import com.example.camerax.tools.LuminosityAnalyzer
import kotlinx.android.synthetic.main.activity_camera.*
import java.io.File
import java.util.concurrent.Executor

/**
 * @Demo class CameraActivity
 * @Description TODO
 * @author libo
 * @date 2019-11-19 11:31
 */
class CameraActivity : BaseActivity() {

    private lateinit var imageCapture: ImageCapture
    private lateinit var file: File
    private var lensFacing = CameraX.LensFacing.BACK
    private var flashMode = FlashMode.AUTO

    override fun onCreateView() {
        setContentView(R.layout.activity_camera)
    }

    override fun initView(savedInstanceState: Bundle?) {
        ttVCamera.post { initPreview() }
    }

    fun initPreview() {
        CameraX.unbindAll()
        val metrics = DisplayMetrics().also { ttVCamera.display.getRealMetrics(it) }
        val screenSize = Size(metrics.widthPixels, metrics.heightPixels)
        //预览
        val previewConfig = PreviewConfig.Builder()
                .setLensFacing(lensFacing)
                .setTargetResolution(screenSize)
                .build()

        val preview = Preview(previewConfig)

        preview.setOnPreviewOutputUpdateListener {
            val parent = ttVCamera.parent as ViewGroup
            parent.removeView(ttVCamera)
            parent.addView(ttVCamera, 0)
            ttVCamera.surfaceTexture = it.surfaceTexture
        }

        //图像分析
        val analyzerConfig = ImageAnalysisConfig.Builder().apply {
            setLensFacing(lensFacing)
            setImageReaderMode(ImageAnalysis.ImageReaderMode.ACQUIRE_LATEST_IMAGE)
        }.build()

        //拍照
        val captureConfig = ImageCaptureConfig.Builder()
                .setLensFacing(lensFacing)
                .setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY)
                .setTargetRotation(ttVCamera.display.rotation)
                .build()

        imageCapture = ImageCapture(captureConfig)
        imageCapture.flashMode = flashMode

        val analyzerUseCase = ImageAnalysis(analyzerConfig).apply {
            setAnalyzer(Executor {
                Log.i("CameraActivity", "aaaaaaaaaaaaa  图片美化")
            }, LuminosityAnalyzer())
        }
        //绑定生命周期
        CameraX.bindToLifecycle(this, preview, analyzerUseCase, imageCapture)
        setListener()
    }

    private fun setListener() {
        ivCamera.setOnClickListener {
            file = File(Environment.getExternalStorageDirectory().toString() + "/相机拍照/" + System.currentTimeMillis() + ".jpg")
            imageCapture.takePicture(file, {
                Log.i("CameraActivity", "aaaaaaaaaaaaa  图片压缩")
            }, object : ImageCapture.OnImageSavedListener {
                override fun onError(imageCaptureError: ImageCapture.ImageCaptureError, message: String, exc: Throwable?) {

                }

                override fun onImageSaved(file: File) {
                    Glide.with(context).load(file).into(ivGallery)
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                        sendBroadcast(Intent(Camera.ACTION_NEW_PICTURE, Uri.fromFile(file)))
                    }
                }
            })
        }

        ivLight.setOnClickListener {
            // AUTO 自动闪光   OFF 不闪光   ON 一直闪烁
            flashMode = if (flashMode == FlashMode.OFF) FlashMode.AUTO else FlashMode.OFF
            ivLight.setImageResource(if (flashMode == FlashMode.AUTO) R.drawable.icon_light_on else R.drawable.icon_light_off)
            initPreview()
        }

        ivChange.setOnClickListener {
            lensFacing = if(CameraX.LensFacing.FRONT == lensFacing) CameraX.LensFacing.BACK else CameraX.LensFacing.FRONT
            initPreview()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        CameraX.unbindAll()
    }
}