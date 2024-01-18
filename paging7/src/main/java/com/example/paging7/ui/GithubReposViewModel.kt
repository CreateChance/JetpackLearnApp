package com.example.paging7.ui

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import androidx.savedstate.SavedStateRegistryOwner
import com.example.paging7.data.GithubRepository
import com.example.paging7.model.Repo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 *
 *
 * @author 高超（gaochao.cc）
 * @since 2024/1/17
 */
@OptIn(ExperimentalCoroutinesApi::class)
class GithubReposViewModel(private val githubRepository: GithubRepository) : ViewModel() {

    val uiState: StateFlow<UiState>

    val pagingDataFlow: Flow<PagingData<UiModel>>

    val uiAction: (UiAction) -> Unit

    init {
        val actionStateFlow = MutableSharedFlow<UiAction>()

        val searches = actionStateFlow
            .filterIsInstance<UiAction.Search>()
            .distinctUntilChanged()
            .onStart { emit(UiAction.Search(DEFAULT_QUERY)) }
        val scrolls = actionStateFlow
            .filterIsInstance<UiAction.Scroll>()
            .distinctUntilChanged()
            .shareIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000), // setup timeout
                replay = 1
            )
            .onStart { emit(UiAction.Scroll(DEFAULT_QUERY)) }

        pagingDataFlow = searches
            .flatMapLatest { searchRepo(it.query) }
            .cachedIn(viewModelScope)

        uiState = combine(
            searches,
            scrolls,
            ::Pair
        ).map { (search, scroll) ->
            UiState(
                query = search.query,
                lastQueryScrolled = scroll.currentQuery,
                hasNotScrolledForCurrentSearch = search.query != scroll.currentQuery
            )
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                initialValue = UiState(DEFAULT_QUERY)
            )

        uiAction = { action ->
            viewModelScope.launch {
                actionStateFlow.emit(action)
            }
        }
    }

    private fun searchRepo(query: String): Flow<PagingData<UiModel>> =
        githubRepository.getSearchResultFlow(query)
            .map { pagingData -> pagingData.map { UiModel.RepoItem(it) } }
            .map { pagingData ->
                pagingData.insertSeparators { before, after ->
                    if (after == null) {
                        return@insertSeparators null
                    }
                    if (before == null) {
                        return@insertSeparators UiModel.SeparatorItem("${after.roundedStarCount}0,000+ stars")
                    }
                    // check between 2 items.
                    if (before.roundedStarCount > after.roundedStarCount) {
                        if (after.roundedStarCount >= 1) {
                            UiModel.SeparatorItem("${after.roundedStarCount}0,000 stars")
                        } else {
                            UiModel.SeparatorItem("< 10,000 stars")
                        }
                    } else {
                        null
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
    val lastQueryScrolled: String = DEFAULT_QUERY,
    val hasNotScrolledForCurrentSearch: Boolean = false,
)

sealed class UiAction {
    data class Search(val query: String) : UiAction()

    data class Scroll(val currentQuery: String) : UiAction()
}

sealed class UiModel {
    data class RepoItem(val repo: Repo) : UiModel() {
        val roundedStarCount: Int = repo.stars / 10_000
    }

    data class SeparatorItem(val desc: String) : UiModel()
}

private const val DEFAULT_QUERY = "Android"
