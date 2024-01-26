package com.example.paging7.data

import android.util.Log
import androidx.paging.CombinedLoadStates
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.paging7.database.RemoteKeys
import com.example.paging7.database.ReposDatabase
import com.example.paging7.model.Repo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.scan

/**
 *
 *
 * @author 高超（gaochao.cc）
 * @since 2024/1/18
 */
@OptIn(ExperimentalPagingApi::class)
class GithubRemoteMediator(
    private val query: String,
    private val githubService: GithubService,
    private val reposDatabase: ReposDatabase
) : RemoteMediator<Int, Repo>() {

    companion object {
        private const val GITHUB_REPO_SEARCH_STARTING_PAGE_KEY = 1
        const val GITHUB_REPO_SEARCH_PAGE_SIZE = 30
    }

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Repo>): MediatorResult {
        Log.d("GAOCHAO", "Load type: $loadType")
        val pageKey = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeysClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: GITHUB_REPO_SEARCH_STARTING_PAGE_KEY
            }

            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                Log.d("GAOCHAO", "Load PREPEND, remote key: ${remoteKeys}, prevKeys: ${remoteKeys?.prevKey}")
                val prevKeys = remoteKeys?.prevKey ?: return MediatorResult.Success(
                    endOfPaginationReached = remoteKeys != null
                )
                prevKeys
            }

            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        try {
            val repoSearchResponse =
                githubService.searchRepos(query, pageKey, state.config.pageSize)

            val repos = repoSearchResponse.items
            val endOfPaginationReached = repos.isEmpty()

            reposDatabase.withTransaction {
                // clear all tables in database if REFRESH
                if (loadType == LoadType.REFRESH) {
                    reposDatabase.remoteKeysDao().clearAll()
                    reposDatabase.reposDao().clearRepos()
                }

                val prevPageKey =
                    if (pageKey == GITHUB_REPO_SEARCH_STARTING_PAGE_KEY) null else pageKey - 1
                val nextPageKey = if (endOfPaginationReached) null else pageKey + 1
                val keys = repos.map {
                    RemoteKeys(repoId = it.id, prevKey = prevPageKey, nextKey = nextPageKey)
                }
                reposDatabase.remoteKeysDao().insertAll(keys)
                reposDatabase.reposDao().insertAll(repos)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeysClosestToCurrentPosition(state: PagingState<Int, Repo>): RemoteKeys? {
        return state.anchorPosition?.let {
            state.closestItemToPosition(it)?.id?.let {
                reposDatabase.remoteKeysDao().getRemoteKeyByRepoId(it)
            }
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, Repo>): RemoteKeys? {
        return state.pages.firstOrNull() { it.data.isNotEmpty() }?.data?.firstOrNull()?.let {
            reposDatabase.remoteKeysDao().getRemoteKeyByRepoId(it.id)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, Repo>): RemoteKeys? {
        return state.pages.lastOrNull() { it.data.isNotEmpty() }?.data?.lastOrNull()?.let {
            reposDatabase.remoteKeysDao().getRemoteKeyByRepoId(it.id)
        }
    }
}

enum class RemotePresentationState {
    INITIAL, REMOTE_LOADING, SOURCE_LOADING, PRESENTED
}

fun Flow<CombinedLoadStates>.asRemotePresentationState(): Flow<RemotePresentationState> =
    scan(RemotePresentationState.INITIAL) { state, loadState ->
        when (state) {
            RemotePresentationState.PRESENTED -> when (loadState.mediator?.refresh) {
                is LoadState.Loading -> RemotePresentationState.REMOTE_LOADING
                else -> state
            }

            RemotePresentationState.INITIAL -> when (loadState.mediator?.refresh) {
                is LoadState.Loading -> RemotePresentationState.REMOTE_LOADING
                else -> state
            }

            RemotePresentationState.REMOTE_LOADING -> when (loadState.source.refresh) {
                is LoadState.Loading -> RemotePresentationState.SOURCE_LOADING
                else -> state
            }

            RemotePresentationState.SOURCE_LOADING -> when (loadState.source.refresh) {
                is LoadState.NotLoading -> RemotePresentationState.PRESENTED
                else -> state
            }
        }
    }.distinctUntilChanged()

