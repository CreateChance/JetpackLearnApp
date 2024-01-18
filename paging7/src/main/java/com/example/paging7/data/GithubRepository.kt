package com.example.paging7.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.paging7.database.ReposDatabase
import com.example.paging7.model.Repo
import kotlinx.coroutines.flow.Flow

/**
 *
 *
 * @author 高超（gaochao.cc）
 * @since 2024/1/17
 */
class GithubRepository(private val githubService: GithubService, private val reposDatabase: ReposDatabase) {

    @OptIn(ExperimentalPagingApi::class)
    fun getSearchResultFlow(query: String): Flow<PagingData<Repo>> {
        // appending '%' so we can allow other characters to be before and after the query string
        val dbQuery = "%${query.replace(' ', '%')}%"
        val pagingSourceFactory =  { reposDatabase.reposDao().getRepoPagingSourceByName(dbQuery)}
        return Pager(
            config = PagingConfig(
                pageSize = GithubRemoteMediator.GITHUB_REPO_SEARCH_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = pagingSourceFactory,
            remoteMediator = GithubRemoteMediator(
                query,
                githubService,
                reposDatabase
            )
        ).flow
    }
}