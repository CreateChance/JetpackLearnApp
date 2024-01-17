package com.example.paging7.data

import com.example.paging7.model.Repo
import com.example.paging7.model.RepoSearchResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

/**
 *
 *
 * @author 高超（gaochao.cc）
 * @since 2024/1/17
 */
class GithubRepository(private val githubService: GithubService) {

    companion object {
        private const val GITHUB_REPO_SEARCH_STARTING_PAGE_KEY = 1
        private const val GITHUB_REPO_SEARCH_PAGE_SIZE = 30
    }

    private val imMemoryCache = mutableListOf<Repo>()

    private val searchResults = MutableSharedFlow<RepoSearchResult>(replay = 1)

    private var lastPageKey = GITHUB_REPO_SEARCH_STARTING_PAGE_KEY

    private var isRequestInProgress = false

    suspend fun getSearchResultFlow(query: String): Flow<RepoSearchResult> {
        // clear old status.
        lastPageKey = GITHUB_REPO_SEARCH_STARTING_PAGE_KEY
        imMemoryCache.clear()
        requestAndSaveData(query)

        return searchResults
    }

    suspend fun requestMore(query: String) {
        if (isRequestInProgress) return
        val successful = requestAndSaveData(query)
        if (successful) {
            lastPageKey++
        }
    }

    private suspend fun requestAndSaveData(query: String): Boolean {
        isRequestInProgress = true
        var successful = false

        try {
            val githubRepoSearchResponse =
                githubService.searchRepos(query, lastPageKey, GITHUB_REPO_SEARCH_PAGE_SIZE)

            val repos = githubRepoSearchResponse.items

            imMemoryCache.addAll(repos)

            searchResults.emit(RepoSearchResult.Success(getReposByNameFromCache(query)))
            successful = true
        } catch (e: Exception) {
            searchResults.emit(RepoSearchResult.Error(e))
        } finally {
            isRequestInProgress = false
        }
        return successful
    }

    private fun getReposByNameFromCache(query: String): List<Repo> {
        return imMemoryCache
            .filter {
                it.name.contains(query, false) ||
                        (it.description?.contains(query, false) ?: false)
            }
            .sortedWith(compareByDescending<Repo> { it.stars }.thenBy { it.name })
    }
}