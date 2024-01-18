package com.example.paging7

import android.content.Context
import androidx.savedstate.SavedStateRegistryOwner
import com.example.paging7.data.GithubRepository
import com.example.paging7.data.GithubService
import com.example.paging7.database.ReposDatabase
import com.example.paging7.ui.GithubReposViewModelFactory

/**
 *
 *
 * @author 高超（gaochao.cc）
 * @since 2024/1/17
 */
object Injection {
    private fun provideGithubRepository(context: Context): GithubRepository {
        return GithubRepository(GithubService.create(), ReposDatabase.getInstance(context))
    }

    fun provideGithubReposViewModelFactory(
        context: Context,
        owner: SavedStateRegistryOwner
    ): GithubReposViewModelFactory {
        return GithubReposViewModelFactory(owner, provideGithubRepository(context))
    }
}