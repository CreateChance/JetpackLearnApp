package com.example.navigation3

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI

/**
 * 演示采用 navigation 实现 2 个 fragment 之间互相跳转，最基本的使用方式。
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // set toolbar
        setSupportActionBar(findViewById(R.id.my_toolbar))

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        // 在标题栏上显示返回箭头
        NavigationUI.setupActionBarWithNavController(this, navController)
    }

    /**
     * 手动复写这个方法，使得 navigation 接管默认的返回逻辑
     * 如果不复写这个方法的话，toolbar 上的返回按钮将不生效
     */
    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        return navController.navigateUp()
    }
}