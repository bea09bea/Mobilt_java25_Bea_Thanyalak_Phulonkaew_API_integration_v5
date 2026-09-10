package com.bea.gitscope

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.bea.gitscope.adapter.FavoriteAdapter
import com.bea.gitscope.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import androidx.recyclerview.widget.LinearLayoutManager


private var _binding: FragmentHomeBinding? = null
private val binding get() = _binding!!
private lateinit var auth: FirebaseAuth
private lateinit var favoriteAdapter: FavoriteAdapter
class HomeFragment : Fragment(R.layout.fragment_home) {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        val user = auth.currentUser

        binding.welcome.text = "Welcome " + user?.displayName

        //hämta inloggad användares favorit lista från real time database
        favoriteAdapter = FavoriteAdapter()

        binding.favoritesRecyclerView.apply {
            adapter = favoriteAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        val database = FirebaseDatabase.getInstance(
            "https://gitscope-49803-default-rtdb.europe-west1.firebasedatabase.app"
        ).reference

        val favoritesRef = database
            .child("users")
            .child(user!!.uid)
            .child("favorites")

        favoritesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val favorites = mutableListOf<SearchFragment.FavoriteUser>()

                for (favoritesSnapshot in snapshot.children) {
                    val favorite = favoritesSnapshot.getValue(
                        SearchFragment.FavoriteUser::class.java)

                    if (favorite != null) {
                        favorites.add(favorite)
                    }
                }
                favoriteAdapter.submitList(favorites)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HomeFragment", "Firebase error: ${error.message}")
            }
        })

        binding.logoutBtn.setOnClickListener {
            auth.signOut()

            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
