package com.example.paging7.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.example.paging7.Injection
import com.example.paging7.data.RemotePresentationState
import com.example.paging7.data.asRemotePresentationState
import com.example.paging7.databinding.FragmentRepoListBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * 展示 github repo list 的页面
 */
class RepoListFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val viewBinding = FragmentRepoListBinding.inflate(inflater, container, false)

        val viewModel = ViewModelProvider(
            this,
            Injection.provideGithubReposViewModelFactory(requireActivity(), this)
        )[GithubReposViewModel::class.java]

        viewBinding.rcvRepoList.addItemDecoration(
            DividerItemDecoration(
                requireActivity(),
                DividerItemDecoration.VERTICAL
            )
        )

        viewBinding.apply {
            val repoListAdapter = ReposListAdapter()
            // 自定义页眉和页脚，实现加载状态的指示和失败重试按钮
            val headerAdapter = LoadStateAdapter { repoListAdapter.retry() }
            val footerAdapter = LoadStateAdapter { repoListAdapter.retry() }
            rcvRepoList.adapter = repoListAdapter.withLoadStateHeaderAndFooter(
                header = headerAdapter,
                footer = footerAdapter,
            )

            etSearchRepo.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    etSearchRepo.text.trim().let {
                        if (it.isNotEmpty()) {
                            rcvRepoList.scrollToPosition(0)
                            viewModel.uiAction.invoke(UiAction.Search(it.toString()))
                        }
                    }
                    true
                } else {
                    false
                }
            }

            lifecycleScope.launch {
                viewModel.uiState
                    .map { it.query }
                    .distinctUntilChanged()
                    .collect(etSearchRepo::setText)
            }

            // setup scroll listener
            rcvRepoList.addOnScrollListener(object : OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    if (dy != 0) {
                        viewModel.uiAction.invoke(
                            UiAction.Scroll(
                                currentQuery = viewModel.uiState.value.query
                            )
                        )
                    }
                }
            })

            val notLoading = repoListAdapter.loadStateFlow
                .asRemotePresentationState()
                // Only react to cases where REFRESH completes i.e., NotLoading.
                .map { it == RemotePresentationState.PRESENTED }

            val hasNotScrolledForCurrentSearch = viewModel.uiState
                .map { it.hasNotScrolledForCurrentSearch }
                .distinctUntilChanged()

            val shouldScrollToTop = combine(
                notLoading,
                hasNotScrolledForCurrentSearch,
                Boolean::and
            ).distinctUntilChanged()

            lifecycleScope.launch {
                shouldScrollToTop.collect {
                    if (it) rcvRepoList.scrollToPosition(0)
                }
            }

            lifecycleScope.launch {
                viewModel.pagingDataFlow.collectLatest(repoListAdapter::submitData)
            }

            lifecycleScope.launch {
                repoListAdapter.loadStateFlow.collect { loadState ->
                    // 当 CombinedLoadStates refresh 状态为 NotLoading 并且列表数量为 0 的时候
                    // 展示空表提示并且隐藏列表 view
                    val isListEmpty =
                        loadState.refresh is LoadState.NotLoading && repoListAdapter.itemCount == 0
                    emptyList.isVisible = isListEmpty
                    rcvRepoList.isVisible =
                        loadState.source.refresh is LoadState.NotLoading || loadState.mediator?.refresh is LoadState.NotLoading
                    pbRetryProgress.isVisible = loadState.mediator?.refresh is LoadState.Loading
                    btnRetry.isVisible =
                        loadState.mediator?.refresh is LoadState.Error && repoListAdapter.itemCount == 0

                    headerAdapter.loadState =
                        loadState.mediator?.refresh?.takeIf { it is LoadState.Error && repoListAdapter.itemCount == 0 }
                            ?: loadState.prepend

                    // 如果发现任何错误，通过 toast 通知用户
                    val errorState = loadState.source.refresh as? LoadState.Error
                        ?: loadState.source.append as? LoadState.Error
                        ?: loadState.source.prepend as? LoadState.Error
                        ?: loadState.refresh as? LoadState.Error
                        ?: loadState.append as? LoadState.Error
                        ?: loadState.prepend as? LoadState.Error
                    errorState?.let {
                        Toast.makeText(
                            requireActivity(),
                            "\uD83D\uDE28 Wooops ${it.error}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }

            btnRetry.setOnClickListener {
                repoListAdapter.retry()
            }
        }

        return viewBinding.root
    }


}