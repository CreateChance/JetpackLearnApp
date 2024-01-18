package com.example.paging7.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.paging7.R
import com.example.paging7.databinding.RepoItemBinding
import com.example.paging7.databinding.SeparatorItemBinding

/**
 *
 *
 * @author 高超（gaochao.cc）
 * @since 2024/1/17
 */
class ReposListAdapter :
    PagingDataAdapter<UiModel, RecyclerView.ViewHolder>(UIMODEL_COMPARATOR) {

    class RepoItemViewHolder(val dataBinding: RepoItemBinding) :
        RecyclerView.ViewHolder(dataBinding.root)

    class SepItemViewHolder(val dataBinding: SeparatorItemBinding) :
        RecyclerView.ViewHolder(dataBinding.root)

    companion object {
        private val UIMODEL_COMPARATOR = object : DiffUtil.ItemCallback<UiModel>() {
            override fun areItemsTheSame(oldItem: UiModel, newItem: UiModel): Boolean {
                return (oldItem is UiModel.RepoItem && newItem is UiModel.RepoItem &&
                        oldItem.repo.fullName == newItem.repo.fullName) ||
                        (oldItem is UiModel.SeparatorItem && newItem is UiModel.SeparatorItem &&
                                oldItem.desc == newItem.desc)
            }

            override fun areContentsTheSame(oldItem: UiModel, newItem: UiModel): Boolean =
                oldItem == newItem
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is UiModel.RepoItem -> R.layout.repo_item
            is UiModel.SeparatorItem -> R.layout.separator_item
            else -> throw UnsupportedOperationException("Unknown view")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == R.layout.repo_item) {
            RepoItemViewHolder(
                RepoItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else {
            SepItemViewHolder(
                SeparatorItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val uiModel = getItem(position)) {
            is UiModel.RepoItem -> (holder as RepoItemViewHolder).dataBinding.repo = uiModel.repo
            is UiModel.SeparatorItem -> (holder as SepItemViewHolder).dataBinding.textContent =
                uiModel.desc

            null -> {}
        }
    }
}

