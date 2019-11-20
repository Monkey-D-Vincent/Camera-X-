package com.example.camerax.base

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * @Demo class BaseActivity
 * @Description TODO
 * @author libo
 * @date 2019-11-19 11:26
 */
abstract class BaseActivity :AppCompatActivity() {

    lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this;
        onCreateView()
        initView(savedInstanceState)
    }

    abstract fun onCreateView()

    abstract fun initView(savedInstanceState: Bundle?)

}