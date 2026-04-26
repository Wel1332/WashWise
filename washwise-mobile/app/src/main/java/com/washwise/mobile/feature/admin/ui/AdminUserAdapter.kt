package com.washwise.mobile.feature.admin.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.washwise.mobile.R
import com.washwise.mobile.databinding.ItemAdminUserBinding
import com.washwise.mobile.feature.admin.data.AdminUser
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Renders a user row in the Admin Users tab. The current user gets a "You" badge
 * and no role-change button (admins shouldn't demote themselves accidentally).
 */
class AdminUserAdapter(
    private val onChangeRole: (AdminUser) -> Unit
) : ListAdapter<AdminUserAdapter.Item, AdminUserAdapter.UserViewHolder>(UserDiff()) {

    data class Item(val user: AdminUser, val isCurrentUser: Boolean)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemAdminUserBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class UserViewHolder(
        private val binding: ItemAdminUserBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Item) {
            val user = item.user
            val name = user.fullName ?: "Unknown"
            binding.tvUserName.text = name
            binding.tvUserEmail.text = user.email ?: "—"
            binding.tvUserInitials.text = initialsOf(name)
            binding.tvUserJoined.text = user.createdAt
                ?.let { "Joined ${formatJoined(it)}" } ?: "—"

            applyRoleBadge(user.role)

            if (item.isCurrentUser) {
                binding.btnChangeRole.visibility = View.GONE
                binding.tvCurrentUserBadge.visibility = View.VISIBLE
            } else {
                binding.btnChangeRole.visibility = View.VISIBLE
                binding.tvCurrentUserBadge.visibility = View.GONE
                binding.btnChangeRole.setOnClickListener { onChangeRole(user) }
            }
        }

        private fun applyRoleBadge(role: String?) {
            val (label, bgRes, textColor) = when ((role ?: "").uppercase()) {
                "ADMIN" -> Triple("Admin", R.drawable.bg_pill_grey_soft, "#111827")
                "STAFF" -> Triple("Staff", R.drawable.bg_pill_purple, "#7E22CE")
                else -> Triple("Customer", R.drawable.bg_badge_blue, "#1D4ED8")
            }
            binding.tvUserRoleBadge.text = label
            binding.tvUserRoleBadge.setBackgroundResource(bgRes)
            binding.tvUserRoleBadge.setTextColor(Color.parseColor(textColor))
        }

        private fun initialsOf(name: String): String {
            val parts = name.trim().split(" ").filter { it.isNotBlank() }
            return when {
                parts.isEmpty() -> "U"
                parts.size == 1 -> parts[0].take(2).uppercase()
                else -> "${parts[0].first()}${parts[1].first()}".uppercase()
            }
        }

        private fun formatJoined(raw: String): String = try {
            val parsed = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).parse(raw)
            SimpleDateFormat("MMM d, yyyy", Locale.US).format(parsed!!)
        } catch (_: Exception) {
            raw.substringBefore('T')
        }
    }

    class UserDiff : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean =
            oldItem.user.id == newItem.user.id

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean =
            oldItem == newItem
    }
}
