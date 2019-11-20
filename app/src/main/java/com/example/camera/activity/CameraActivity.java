package com.example.camera.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.Size;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.camera.R;
import com.example.camera.base.BaseActivity;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraX;
import androidx.camera.core.FlashMode;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageAnalysisConfig;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureConfig;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.lifecycle.LifecycleOwner;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author libo
 * @Demo class CameraActivity
 * @Description TODO
 * @date 2019-11-14 15:33
 */
public class CameraActivity extends BaseActivity {

	private static final String TAG = "CameraActivity";

	@BindView(R.id.tv_camera)
	TextureView tvCamera;
	@BindView(R.id.rl_content)
	RelativeLayout rlContent;
	@BindView(R.id.iv_light)
	ImageView ivLight;
	@BindView(R.id.iv_change)
	ImageView ivChange;
	@BindView(R.id.iv_camera)
	ImageView ivCamera;
	@BindView(R.id.iv_gallery)
	ImageView ivGallery;

	private Preview preview;
	private ViewGroup parent;

	private Matrix matrix = new Matrix();
	private int centerX;
	private int centerY;
	private int rotationDegrees;
	private ImageCapture imageCapture;

	private File file;
	private File fileDir;
	private CameraX.LensFacing lensFacing;

	private boolean isOnLight = false;

	@Override
	protected void onCreateView() {
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_camera);

		file = new File(Environment.getExternalStorageDirectory() + "/相机拍照/" + System.currentTimeMillis() + ".jpg");
		fileDir = new File(Environment.getExternalStorageDirectory() + "/相机拍照/");
		if (!fileDir.exists()) {
			fileDir.mkdirs();
			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	protected void initView(@Nullable Bundle savedInstanceState) {
		PreviewConfig previewConfig = new PreviewConfig.Builder().setTargetResolution(new Size(1080, 2160)).setTargetRotation(getWindowManager().getDefaultDisplay().getRotation()).build();
		preview = new Preview(previewConfig);
		preview.setOnPreviewOutputUpdateListener(output -> {
			parent = (ViewGroup) tvCamera.getParent();
			parent.removeView(tvCamera);
			parent.addView(tvCamera, 0);
			tvCamera.setSurfaceTexture(output.getSurfaceTexture());
			updateTransform();
		});
		ImageCaptureConfig config = new ImageCaptureConfig.Builder().setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY).setTargetResolution(new Size(1280, 720)).build();
		imageCapture = new ImageCapture(config);
		CameraX.bindToLifecycle(this, preview, imageCapture);
	}

	private void updateTransform() {
		centerX = tvCamera.getWidth() / 2;
		centerY = tvCamera.getHeight() / 2;
		switch (getWindowManager().getDefaultDisplay().getRotation()) {
			case Surface.ROTATION_0:
				rotationDegrees = 0;
				break;
			case Surface.ROTATION_90:
				rotationDegrees = 90;
				break;
			case Surface.ROTATION_180:
				rotationDegrees = 180;
				break;
			case Surface.ROTATION_270:
				rotationDegrees = 270;
				break;
		}
		matrix.postRotate(-rotationDegrees, centerX, centerY);
		tvCamera.setTransform(matrix);
	}

	@OnClick({R.id.iv_light, R.id.iv_change, R.id.iv_camera, R.id.iv_gallery})
	public void onViewClicked(View view) {
		switch (view.getId()) {
			case R.id.iv_light:
				isOnLight = !isOnLight;
				ivLight.setImageResource(isOnLight ? R.drawable.icon_light_on : R.drawable.icon_light_off);
				// AUTO 自动闪光   OFF 不闪光   ON 一直闪烁
				imageCapture.setFlashMode(isOnLight ? FlashMode.AUTO : FlashMode.OFF);
				break;
			case R.id.iv_change:
				lensFacing = CameraX.LensFacing.FRONT == lensFacing ? CameraX.LensFacing.BACK : CameraX.LensFacing.FRONT;
				break;
			case R.id.iv_camera:
				startCamera();
				break;
			case R.id.iv_gallery:
				break;
		}
	}

	/**
	 * 拍照
	 */
	private void startCamera() {
		imageCapture.takePicture(file, command -> {

		}, new ImageCapture.OnImageSavedListener() {
			@Override
			public void onImageSaved(@NonNull File file) {
				uodateGallery(file);
				if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
					sendBroadcast(new Intent(Camera.ACTION_NEW_PICTURE, Uri.fromFile(file)));
				}
//				 mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(file.extension);
//				MediaScannerConnection.scanFile(context, file.absolutePath, mimeType, null);
			}

			@Override
			public void onError(@NonNull ImageCapture.ImageCaptureError imageCaptureError, @NonNull String message, @Nullable Throwable cause) {

			}
		});
	}

	private void uodateGallery(File file) {
		ivGallery.post(() -> {
			Glide.with(ivGallery).load(file).apply(RequestOptions.circleCropTransform()).into(ivGallery);
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		CameraX.setErrorListener((error, message) -> {
			Log.e(TAG, message);
		}, null);
		CameraX.unbind(preview, imageCapture);
		preview.removePreviewOutputListener();
	}
}
