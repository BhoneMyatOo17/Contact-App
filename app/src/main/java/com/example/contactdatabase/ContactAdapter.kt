package com.example.contactdatabase

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.contactdatabase.databinding.ItemContactBinding

class ContactAdapter(
    private var contacts: List<Contact>,
    private val onItemClick: (Contact) -> Unit
) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val binding = ItemContactBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ContactViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bind(contacts[position])
    }

    override fun getItemCount(): Int = contacts.size

    fun updateContacts(newContacts: List<Contact>) {
        contacts = newContacts
        notifyDataSetChanged()
    }

    inner class ContactViewHolder(
        private val binding: ItemContactBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(contact: Contact) {
            binding.txtName.text = contact.name
            binding.txtPhone.text = contact.phone
            binding.txtEmail.text = contact.email


            if (contact.avatarUri != null) {
                val file = java.io.File(contact.avatarUri!!)
                if (file.exists()) {
                    binding.imgAvatar.setImageURI(Uri.fromFile(file))
                } else {
                    binding.imgAvatar.setImageResource(R.drawable.avatar)
                }
            } else {
                binding.imgAvatar.setImageResource(contact.avatarResourceId)
            }

            // Click listener
            binding.root.setOnClickListener {
                onItemClick(contact)
            }
        }
    }
}