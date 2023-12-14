package com.example.coroutine

import android.os.Bundle
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
        // 这一行很关键，如果不设置 lifecycle owner，所有的 livedata databinding 将不生效。
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
    }
}