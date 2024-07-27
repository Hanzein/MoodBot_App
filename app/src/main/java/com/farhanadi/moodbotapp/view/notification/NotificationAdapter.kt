package com.farhanadi.moodbotapp.view.notification

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.farhanadi.moodbotapp.R
import com.farhanadi.moodbotapp.databinding.ItemNotificationBinding

class NotificationAdapter(
    private val binding: ItemNotificationBinding,
    private val notifications: MutableList<Notification>,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    inner class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(notification: Notification, position: Int) {
            binding.tvtimeNotif.text = notification.time
            binding.trashIcon.setOnClickListener {
                onDeleteClick(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_notification, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(notifications[position], position)
    }

    override fun getItemCount(): Int = notifications.size

    fun removeItem(position: Int) {
        notifications.removeAt(position)
        notifyItemRemoved(position)
    }
}
