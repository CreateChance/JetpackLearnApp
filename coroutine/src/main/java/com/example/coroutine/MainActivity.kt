package com.example.coroutine

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.coroutine.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dataBinding =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        dataBinding.lifecycleOwner = this
        val repository = MyRepository(titleService, getTitleDatabase(applicationContext).titleDao)
        val viewModel =
            ViewModelProvider(this, MyViewModel.FACTORY(repository))[MyViewModel::class.java]
        dataBinding.viewModel = viewModel

        viewModel.snackBar.observe(this) { text ->
            text?.let {
                Snackbar.make(dataBinding.root, text, Snackbar.LENGTH_LONG).show()
            }
        }

        viewModel.taps.observe(this) {
            Log.d("GAOCHAO", "tap 发生变化：$it")
        }
    }
}