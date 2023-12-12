package com.example.databinding4

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.DataBindingUtil
import com.example.databinding4.databinding.ActivityMainBinding

/**
 * 演示通过 BaseObservable 来实现基本的双向绑定操作
 * 两个 EditText 组件中的数据同步修改更新效果。
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        viewModel.userViewModel = UserViewModel(User("Tom"))
    }
}

/**
 * 测试数据类
 */
class User(var name: String) {
}

/**
 * 模拟的双向绑定 view mode，并不是真的 view model
 * 注意，要想实现双向绑定效果，必须有一对 set & get，同时 set 和 get 后面的内容必须完全一样
 * 猜想：编译生成的代码，会根据固定的范式来调用这里的 set & get 方法。
 */
class UserViewModel(private val user: User) : BaseObservable() {

    /**
     * @Bindable 注解是用来生成 BR 类中的绑定字段的。
     * 同时，这个注解必须用在 get 方法中，否则双向绑定会失效
     */
    @Bindable
    fun getUserName(): String {
        return user.name
    }

    fun setUserName(name: String) {
        if (user.name != name) {
            Log.d("GAOCHAO", "Updating user name: $name")
            user.name = name
            notifyPropertyChanged(BR.userName)
        }
    }
}

