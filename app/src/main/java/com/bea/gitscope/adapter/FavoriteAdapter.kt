package com.bea.gitscope.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bea.gitscope.SearchFragment
import com.bea.gitscope.databinding.ItemFavoriteBinding
import com.bumptech.glide.Glide

class FavoriteAdapter :
    ListAdapter<SearchFragment.FavoriteUser, FavoriteAdapter.FavoriteViewHolder>(
        DiffCallback()
    ) {

    inner class FavoriteViewHolder(
        private val binding: ItemFavoriteBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(favorite: SearchFragment.FavoriteUser) {
            binding.favoriteUsername.text = favorite.username
            binding.favoriteBio.text = favorite.bio

            //bild
//            Glide.with(binding.favoriteImage.context)
//                .load(favorite.avatarUrl)
//                .into(binding.favoriteImage)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FavoriteViewHolder {

        val binding = ItemFavoriteBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<SearchFragment.FavoriteUser>() {

        override fun areItemsTheSame(
            oldItem: SearchFragment.FavoriteUser,
            newItem: SearchFragment.FavoriteUser
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: SearchFragment.FavoriteUser,
            newItem: SearchFragment.FavoriteUser
        ): Boolean {
            return oldItem == newItem
        }
    }
}