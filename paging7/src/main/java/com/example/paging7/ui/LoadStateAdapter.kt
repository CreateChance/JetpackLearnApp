package com.example.paging7.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.paging7.databinding.FooterItemBinding

/**
 * 加载装载适配器
 *
 * @author 高超（gaochao.cc）
 * @since 2024/1/18
 */
class LoadStateAdapter(private val retry: () -> Unit) :
    LoadStateAdapter<com.example.paging7.ui.LoadStateAdapter.ViewHolder>() {

    class ViewHolder(private val binding: FooterItemBinding, retry: () -> Unit) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.btnRetry.setOnClickListener { retry.invoke() }
        }

        fun bind(loadState: LoadState) {
            if (loadState is LoadState.Error) {
                binding.tvErrorMsg.text = loadState.error.localizedMessage
            }
            binding.pbProgress.isVisible = loadState is LoadState.Loading
            binding.btnRetry.isVisible = loadState is LoadState.Error
            binding.tvErrorMsg.isVisible = loadState is LoadState.Error
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): ViewHolder {
        return ViewHolder(
            FooterItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), retry
        )
    }
}