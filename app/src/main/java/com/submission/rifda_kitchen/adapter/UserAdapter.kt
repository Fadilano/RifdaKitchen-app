package com.submission.rifda_kitchen.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import com.submission.rifda_kitchen.R
import com.submission.rifda_kitchen.databinding.UserItemListBinding
import com.submission.rifda_kitchen.model.UserModel

class UserAdapter(
    private val context: Context,
    private val userList: List<UserModel>,
    private val onRoleChange: (UserModel, String) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    inner class UserViewHolder(private val binding: UserItemListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: UserModel) {
            binding.tvUserName.text = user.name
            binding.tvUserEmail.text = user.email

            val roles = context.resources.getStringArray(R.array.user_roles)
            val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, roles)
            binding.spinnerUserRole.adapter = adapter

            // Set initial role selection
            val roleIndex = roles.indexOfFirst { it.equals(user.role, ignoreCase = true) }
            binding.spinnerUserRole.setSelection(roleIndex)

            // Handle role changes
            binding.spinnerUserRole.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val selectedRole = roles[position]
                    if (selectedRole != user.role) {
                        onRoleChange(user, selectedRole)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // No action needed
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = UserItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(userList[position])
    }

    override fun getItemCount(): Int = userList.size
}
