package com.example.camera.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import com.example.camera.R;
import com.example.camera.base.BaseActivity;
import com.tbruyelle.rxpermissions2.RxPermissions;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

	@BindView(R.id.btn_camera)
	Button btnCamera;
	@BindView(R.id.iv_photo)
	ImageView ivPhoto;

	@Override
	protected void onCreateView() {
		setContentView(R.layout.activity_main);
	}

	@Override
	protected void initView(@Nullable Bundle savedInstanceState) {

	}

	@OnClick(R.id.btn_camera)
	public void onViewClicked() {
		RxPermissions rxPermissions = new RxPermissions(this);
		rxPermissions.requestEach(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE).subscribe(permission -> {
			if (permission.granted) {
				// 用户已经同意该权限
				startActivityForResult(new Intent(context, CameraActivity.class), 100);
			} else if (permission.shouldShowRequestPermissionRationale) {
				// 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
				finish();
			} else {
				// 用户拒绝了该权限，并且选中『不再询问』
				finish();
			}
		});
	}


}
