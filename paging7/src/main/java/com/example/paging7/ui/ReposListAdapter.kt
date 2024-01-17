package com.example.paging7.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.paging7.databinding.RepoItemBinding
import com.example.paging7.model.Repo

/**
 *
 *
 * @author 高超（gaochao.cc）
 * @since 2024/1/17
 */
class ReposListAdapter : ListAdapter<Repo, ReposListAdapter.ViewHolder>(REPO_COMPARATOR) {

    class ViewHolder(val dataBinding: RepoItemBinding) : RecyclerView.ViewHolder(dataBinding.root)

    companion object {
        private val REPO_COMPARATOR = object : DiffUtil.ItemCallback<Repo>() {
            override fun areItemsTheSame(oldItem: Repo, newItem: Repo): Boolean =
                oldItem.fullName == newItem.fullName

            override fun areContentsTheSame(oldItem: Repo, newItem: Repo): Boolean =
                oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            RepoItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.dataBinding.repo = getItem(position)
    }
}

