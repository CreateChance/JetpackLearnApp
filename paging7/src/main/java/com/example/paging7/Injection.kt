package com.example.paging7

import androidx.savedstate.SavedStateRegistryOwner
import com.example.paging7.data.GithubRepository
import com.example.paging7.data.GithubService
import com.example.paging7.ui.GithubReposViewModelFactory

/**
 *
 *
 * @author 高超（gaochao.cc）
 * @since 2024/1/17
 */
object Injection {
    private fun provideGithubRepository(): GithubRepository {
        return GithubRepository(GithubService.create())
    }

    fun provideGithubReposViewModelFactory(owner: SavedStateRegistryOwner): GithubReposViewModelFactory {
        return GithubReposViewModelFactory(owner, provideGithubRepository())
    }
}