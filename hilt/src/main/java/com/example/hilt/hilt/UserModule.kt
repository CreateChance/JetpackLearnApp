package com.example.hilt.hilt

import com.example.hilt.models.User
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped

/**
 * User Model
 * 可以通过 @InstallIn 注解自动注入到 hilt 内置的 component 中，以实现对象注入
 * 注意：不同的 component 的 scope 是不一样的，需要协同一致，否则会报错
 *
 * @author 高超（gaochao.cc）
 * @since 2023/12/4
 */
@InstallIn(ActivityRetainedComponent::class)
@Module
class UserModule {

    @ActivityRetainedScoped
    @Provides
    fun provideUser(): User {
        return User()
    }
}