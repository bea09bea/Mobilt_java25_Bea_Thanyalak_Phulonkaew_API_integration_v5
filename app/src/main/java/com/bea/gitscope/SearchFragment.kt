package com.bea.gitscope

import android.R.attr.query
import android.os.Bundle
import retrofit2.Call
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.core.os.postDelayed
import com.bea.gitscope.databinding.FragmentSearchBinding
import com.bea.gitscope.model.GitHubSearchResponse
import com.bea.gitscope.network.GitHubClient
import com.bumptech.glide.Glide
import retrofit2.Response
import retrofit2.Callback
import com.bea.gitscope.model.GitHubUser
import android.os.Looper
import android.os.Handler
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bea.gitscope.adapter.GitHubUserAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SearchFragment : Fragment(R.layout.fragment_search) {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: GitHubUserAdapter
    private val handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        Log.d("GitHub", "Searching for: $query")

        //kopplar fragment_search.xml till kotlin kod
        _binding = FragmentSearchBinding.inflate(inflater,container,false)

        //skapa adapter med tom lista först
        adapter = GitHubUserAdapter(emptyList()) { user, isFavorite ->

            if (isFavorite) {
                saveFavorite(user)
            } else {
                removeFavorite(user)
            }
        }

        //koppa adapter till RecyclerView
        binding.userRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.userRecyclerView.adapter = adapter

        //lyssna vad användaren gör i sökfält
        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            //sökbar + enter -> API anrop
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrBlank()) {
                    searchGithubUsers(query)
                }
                return true
            }

            // Måste finnas, men vi gör ingenting när användaren skriver
            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //Skicka API GET request med endpoint
    private fun searchGithubUsers(query: String) {

        GitHubClient.api.searchUsers(query)
            //API svarar
            .enqueue(object : Callback<GitHubSearchResponse> {

                override fun onResponse(
                    call: Call<GitHubSearchResponse>,
                    response: Response<GitHubSearchResponse>
                ) {
                    Log.d("GitHub", "Code: ${response.code()}")
                    Log.d("GitHub", "Body: ${response.body()}")

                    //Om requesten lyckades
                    if (response.isSuccessful) {

                        //hittar användarna
                        val searchUsers = response.body()?.items ?: emptyList()

                        //fullständig lista av användare med all data som jag vill ha
                        val fullUsers = mutableListOf<GitHubUser>()

                        //hämta mer information
                        searchUsers.forEach { searchUser ->

                            GitHubClient.api.getUser(searchUser.login)
                                .enqueue(object : Callback<GitHubUser> {

                                    override fun onResponse(call: Call<GitHubUser>, response: Response<GitHubUser>) {
                                        val user = response.body()

                                        if (response.isSuccessful && user != null) {

                                            checkIfFavorite(user) { isFavorite ->

                                                user.isFavorite = isFavorite

                                                fullUsers.add(user)
                                                adapter.updateUsers(fullUsers.toList())
                                            }
                                        }
                                    }

                                    override fun onFailure(call: Call<GitHubUser>, t: Throwable) {
                                        Log.e("DETAIL", "Kunde inte hämta ${searchUser.login}", t)
                                    }
                                })
                        }

                    } else if (response.code() == 429) {
                        Log.e("GitHub", "Search limit")
                        Toast.makeText(
                            requireContext(),
                            "Too many search request. Try again soon.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "GitHub error: ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<GitHubSearchResponse>, t: Throwable) {
                    Log.e("GitHub", "Error", t)
                    Toast.makeText(
                        requireContext(),
                        t.message ?: "Network error",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }

    data class FavoriteUser(
        val id: Long = 0,
        val username: String = "",
        val bio: String = ""
        //, val avatarUrl: String = ""
    )

    private fun saveFavorite(user: GitHubUser) {

        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
            ?: return

        val database = FirebaseDatabase.getInstance(
            "https://gitscope-49803-default-rtdb.europe-west1.firebasedatabase.app"
        ).reference

        val favoriteUser = FavoriteUser(
            id = user.id,
            username = user.name ?: "",
            bio = user.bio ?: ""
        )

        database
            .child("users")
            .child(currentUserId)
            .child("favorites")
            .child(user.id.toString())
            .setValue(favoriteUser)
            .addOnSuccessListener {
                Log.d("Firebase", "Favorit sparad: ${user.login}")
            }
            .addOnFailureListener { exception ->
                Log.e("Firebase", "Kunde inte spara favorit", exception)
            }
    }

    private fun removeFavorite(user: GitHubUser) {

        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
            ?: return

        val database = FirebaseDatabase.getInstance(
            "https://gitscope-49803-default-rtdb.europe-west1.firebasedatabase.app"
        ).reference

        database
            .child("users")
            .child(currentUserId)
            .child("favorites")
            .child(user.id.toString())
            .removeValue()
            .addOnSuccessListener {
                Log.d("Firebase", "Favorit borttagen: ${user.login}")
            }
            .addOnFailureListener { exception ->
                Log.e("Firebase", "Kunde inte ta bort favorit", exception)
            }
    }

    private fun checkIfFavorite(user: GitHubUser, onResult: (Boolean) -> Unit) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
            ?: return

        val database = FirebaseDatabase.getInstance(
            "https://gitscope-49803-default-rtdb.europe-west1.firebasedatabase.app"
        ).reference

        database
            .child("users")
            .child(currentUserId)
            .child("favorites")
            .child(user.id.toString())
            .get()
            .addOnSuccessListener { snapshot ->

                Log.d(
                    "FavoriteCheck",
                    "user=${user.login}, id=${user.id}, exists=${snapshot.exists()}"
                )

                onResult(snapshot.exists())
            }
            .addOnFailureListener { exception ->
                Log.e("FavoriteCheck", "Kunde inte läsa favorit", exception)
                onResult(false)
            }
    }
}

