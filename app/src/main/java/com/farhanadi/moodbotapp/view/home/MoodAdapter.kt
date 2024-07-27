package com.farhanadi.moodbotapp.view.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.farhanadi.moodbotapp.databinding.ItemRiwayatBinding
import com.farhanadi.moodbotapp.view.other.MoodEntry

class MoodAdapter(private val moodEntries: List<MoodEntry>) : RecyclerView.Adapter<MoodAdapter.MoodViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodViewHolder {
        val binding = ItemRiwayatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MoodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MoodViewHolder, position: Int) {
        val moodEntry = moodEntries[position]
        holder.bind(moodEntry)
    }

    override fun getItemCount(): Int = moodEntries.size

    class MoodViewHolder(private val binding: ItemRiwayatBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(moodEntry: MoodEntry) {
            binding.tvdatehome.text = moodEntry.date
            binding.tvemotionStatus.text = moodEntry.emotion
            binding.tvdeskripsi.text = moodEntry.description
            // Set the emotion icon based on the emotion (you may need to handle this)
        }
    }
}
