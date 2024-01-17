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
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.map
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.example.paging7.Injection
import com.example.paging7.databinding.FragmentRepoListBinding
import com.example.paging7.model.RepoSearchResult

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
            Injection.provideGithubReposViewModelFactory(this)
        )[GithubReposViewModel::class.java]

        viewBinding.rcvRepoList.addItemDecoration(
            DividerItemDecoration(
                requireActivity(),
                DividerItemDecoration.VERTICAL
            )
        )

        viewBinding.apply {
            val repoListAdapter = ReposListAdapter()
            rcvRepoList.adapter = repoListAdapter

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

            viewModel.uiState
                .map(UiState::searchResult)
                .distinctUntilChanged()
                .observe(requireActivity()) { result ->
                    when (result) {
                        is RepoSearchResult.Success -> {
                            emptyList.isVisible = result.data.isEmpty()
                            rcvRepoList.isVisible = result.data.isNotEmpty()
                            repoListAdapter.submitList(result.data)
                        }

                        is RepoSearchResult.Error -> {
                            Toast.makeText(
                                requireActivity(),
                                "\uD83D\uDE28 Wooops $result.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                }

            // setup scroll listener
            val repoListLayoutManager = rcvRepoList.layoutManager as LinearLayoutManager
            rcvRepoList.addOnScrollListener(object : OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val totalItemCount = repoListLayoutManager.itemCount
                    val visibleItemCount = repoListLayoutManager.childCount
                    val lastVisibleItemPosition =
                        repoListLayoutManager.findLastVisibleItemPosition()
                    viewModel.uiAction.invoke(
                        UiAction.Scroll(
                            visibleItemCount = visibleItemCount,
                            lastVisibleItemPosition = lastVisibleItemPosition,
                            totalItemCount = totalItemCount
                        )
                    )
                }
            })

            viewModel.uiState
                .map(UiState::query)
                .distinctUntilChanged()
                .observe(requireActivity(), etSearchRepo::setText)
        }

        return viewBinding.root
    }


}