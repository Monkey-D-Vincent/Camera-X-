package com.example.camerax.activity

import android.Manifest.permission
import android.content.Intent
import android.os.Bundle
import com.example.camerax.R
import com.example.camerax.base.BaseActivity
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    override fun onCreateView() {
        setContentView(R.layout.activity_main)
    }

    override fun initView(savedInstanceState: Bundle?) {
        var rxPermissions = RxPermissions(this);
        rxPermissions.requestEach(permission.CAMERA, permission.WRITE_EXTERNAL_STORAGE, permission.READ_EXTERNAL_STORAGE).subscribe({ permission ->
            if (permission.granted) {
                // 用户已经同意该权限
                btnCamera.setOnClickListener {
                    startActivity(Intent(context, CameraActivity::class.java))
                }
            } else if (permission.shouldShowRequestPermissionRationale) {
                // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                finish()
            } else {
                // 用户拒绝了该权限，并且选中『不再询问』
                finish()
            }
        })
    }

}
