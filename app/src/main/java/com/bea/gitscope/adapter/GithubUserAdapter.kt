package com.bea.gitscope.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bea.gitscope.R
import com.bea.gitscope.model.GitHubSearchUser
import com.bea.gitscope.model.GitHubUser
import com.bumptech.glide.Glide
import com.bea.gitscope.databinding.UserCardBinding

class GitHubUserAdapter(
    private var users: List<GitHubUser>,
    private val onFavoriteClick: (GitHubUser, Boolean) -> Unit
) : RecyclerView.Adapter<GitHubUserAdapter.UserViewHolder>() {

    //Håller layout för varje användarkort
    class UserViewHolder(
        val binding: UserCardBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {

        //Hämtar layouten från user_card.xml
        val binding = UserCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        //hämta användare för RecyclerView
        val user = users[position]

        //fyller layout med sökande users data
        holder.binding.username.text = user.login
        holder.binding.bio.text = user.bio ?: "Ingen bio"
        //holder.binding.repos.text = "Repositories: ${user.public_repos}"
        holder.binding.followers.text = "Followers: ${user.followers}"
        //holder.binding.createdAt.text = "Skapad: ${user.created_at}"

        // Hjärtikon
        holder.binding.favoriteButton.setImageResource(
            if (user.isFavorite) {
                R.drawable.heart
            } else {
                R.drawable.heart_empty
            }
        )

        holder.binding.favoriteButton.setOnClickListener {
            user.isFavorite = !user.isFavorite

            //Uppdaterar hjärtikon när användare klickar
            holder.binding.favoriteButton.setImageResource(
                if (user.isFavorite) {
                    R.drawable.heart
                } else {
                    R.drawable.heart_empty
                }
            )

            onFavoriteClick(user, user.isFavorite)
        }
    }

    //Räkna hur många kort som ska visas
    override fun getItemCount(): Int {
        return users.size
    }

    //Uppdaterar listan med nya användare
    fun updateUsers(newUsers: List<GitHubUser>) {
        users = newUsers
        notifyDataSetChanged()
    }
}