package com.example.paging6

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import com.example.paging6.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * 学习最新 paging3 的基本使用
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewBinding = ActivityMainBinding.inflate(layoutInflater)

        val articleListAdapter = ArticleListAdapter()
        viewBinding.rcvArticleList.adapter = articleListAdapter

        setContentView(viewBinding.root)

        // viewmodel
        val viewModel by viewModels<ArticlesViewModel>(
            factoryProducer = {
                ArticlesViewModelFactory(this, ArticleRepository())
            }
        )

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                articleListAdapter.loadStateFlow.collectLatest {
                    viewBinding.piTop.isVisible = it.source.prepend is LoadState.Loading
                    viewBinding.piBottom.isVisible = it.source.append is LoadState.Loading
                    Log.d("GAOCHAO", "装载状态变化: $it")
                    if (it.source.refresh is LoadState.Loading || it.source.append is LoadState.Loading) {
                        Toast.makeText(this@MainActivity, "正在装载中...", Toast.LENGTH_SHORT)
                            .show()
                    } else if (it.source.refresh !is LoadState.Error && it.source.append !is LoadState.Error) {
                        Toast.makeText(this@MainActivity, "装载成功！", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.articles.collectLatest {
                    Log.d("GAOCHAO", "更新 PagingData：$it")
                    articleListAdapter.submitData(it)
                }
            }
        }

        viewBinding.btnRefresh.setOnClickListener {
            // refresh articles list.
            articleListAdapter.refresh()
        }
    }
}