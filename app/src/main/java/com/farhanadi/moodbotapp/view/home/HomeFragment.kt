package com.farhanadi.moodbotapp.view.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.farhanadi.moodbotapp.databinding.FragmentHomeBinding
import com.farhanadi.moodbotapp.view.camera.CameraActivity
import com.farhanadi.moodbotapp.view.notification.NotificationActivity
import com.farhanadi.moodbotapp.view.other.MoodEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val db = FirebaseFirestore.getInstance()
    private val moodEntries = mutableListOf<MoodEntry>()
    private lateinit var moodAdapter: MoodAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        setupAction()
        setupRecyclerView()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchAndDisplayData()
    }

    private fun setupRecyclerView() {
        moodAdapter = MoodAdapter(moodEntries)
        binding.rvRiwayatchatseminggu.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRiwayatchatseminggu.adapter = moodAdapter
    }

    private fun fetchAndDisplayData() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Log.e("Firestore", "User ID is null. User might not be authenticated.")
            return
        }
        Log.d("Firestore", "User ID: $userId")

        db.collection("users").document(userId).collection("chatHistory")
            .orderBy("timestamp")  // Optionally order by timestamp
            .get()
            .addOnSuccessListener { result ->
                moodEntries.clear()  // Clear existing data
                if (!result.isEmpty) {
                    for (document in result.documents) {
                        val emotion = document.getString("emotion") ?: "Unknown"
                        val date = document.getDate("timestamp")?.toLocaleString() ?: "Unknown"
                        val description = document.getString("description") ?: ""
                        moodEntries.add(MoodEntry(date, emotion, description))
                    }
                    moodAdapter.notifyDataSetChanged()  // Notify adapter of data change
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error fetching data", exception)
            }
    }

    private fun setupAction() {
        binding.notifIconHome.setOnClickListener {
            startActivity(Intent(requireActivity(), NotificationActivity::class.java))
        }
        binding.btnTochatbot.setOnClickListener {
            startActivity(Intent(requireActivity(), CameraActivity::class.java))
        }
    }
}
