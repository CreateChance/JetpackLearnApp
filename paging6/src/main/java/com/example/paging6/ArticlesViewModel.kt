package com.example.paging6

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.savedstate.SavedStateRegistryOwner
import kotlinx.coroutines.flow.Flow

private const val ITEMS_PER_PAGE = 50

/**
 *
 *
 * @author 高超（gaochao.cc）
 * @since 2024/1/16
 */
class ArticlesViewModel(repository: ArticleRepository) : ViewModel() {

    val articles: Flow<PagingData<Article>> = Pager(
        config = PagingConfig(pageSize = ITEMS_PER_PAGE, enablePlaceholders = false),
        pagingSourceFactory = { repository.articlePagingSource() }
    ).flow.cachedIn(viewModelScope)
}

class ArticlesViewModelFactory(
    owner: SavedStateRegistryOwner,
    private val repository: ArticleRepository
) : AbstractSavedStateViewModelFactory(owner, null) {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (modelClass.isAssignableFrom(ArticlesViewModel::class.java)) {
            return ArticlesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
    }
}
