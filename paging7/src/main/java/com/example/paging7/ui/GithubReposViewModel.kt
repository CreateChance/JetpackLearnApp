package com.example.paging7.ui

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.savedstate.SavedStateRegistryOwner
import com.example.paging7.data.GithubRepository
import com.example.paging7.model.RepoSearchResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 *
 *
 * @author 高超（gaochao.cc）
 * @since 2024/1/17
 */
class GithubReposViewModel(private val githubRepository: GithubRepository) : ViewModel() {

    val uiState: LiveData<UiState>

    val uiAction: (UiAction) -> Unit

    init {
        val queryLiveData = MutableLiveData(DEFAULT_QUERY)

        uiState = queryLiveData
            .distinctUntilChanged()
            .switchMap { queryString ->
                liveData {
                    val state = githubRepository.getSearchResultFlow(query = queryString)
                        .map {
                            UiState(
                                queryString,
                                it
                            )
                        }.asLiveData(Dispatchers.Main)
                    emitSource(state)
                }
            }

        uiAction = { action ->
            when (action) {
                is UiAction.Search -> {
                    queryLiveData.postValue(action.query)
                }

                is UiAction.Scroll -> {
                    if (action.shouldFetchMore) {
                        queryLiveData.value?.let { queryString ->
                            viewModelScope.launch {
                                githubRepository.requestMore(queryString)
                            }
                        }
                    }
                }
            }
        }
    }
}

class GithubReposViewModelFactory(
    owner: SavedStateRegistryOwner,
    private val githubRepository: GithubRepository
) : AbstractSavedStateViewModelFactory(owner, null) {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (modelClass.isAssignableFrom(GithubReposViewModel::class.java)) {
            return GithubReposViewModel(githubRepository) as T
        }
        throw IllegalArgumentException("Unknown view model class: $modelClass")
    }
}

data class UiState(
    val query: String,
    val searchResult: RepoSearchResult
)

sealed class UiAction {
    data class Search(val query: String) : UiAction()

    data class Scroll(
        val visibleItemCount: Int,
        val lastVisibleItemPosition: Int,
        val totalItemCount: Int
    ) : UiAction() {
        val shouldFetchMore: Boolean
            get() = visibleItemCount + lastVisibleItemPosition + VISIBLE_THRESHOLD > totalItemCount
    }
}

private const val VISIBLE_THRESHOLD = 5
private const val DEFAULT_QUERY = "Android"
