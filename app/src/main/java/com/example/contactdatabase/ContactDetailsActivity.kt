package com.example.contactdatabase

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.contactdatabase.databinding.ActivityContactDetailsBinding

class ContactDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContactDetailsBinding
    private lateinit var dbHelper: DBHelper
    private var contact: Contact? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DBHelper(this)

        val contactId = intent.getLongExtra("CONTACT_ID", -1)
        if (contactId != -1L) {
            contact = dbHelper.getContactById(contactId)
            displayContactDetails()
        } else {
            Toast.makeText(this, "Contact not found", Toast.LENGTH_SHORT).show()
            finish()
        }

        setupButtons()
    }

    private fun displayContactDetails() {
        contact?.let {
            binding.txtName.text = it.name
            binding.txtPhone.text = it.phone
            binding.txtEmail.text = if (it.email.isEmpty()) "No email" else it.email


            if (it.avatarUri != null) {
                val file = java.io.File(it.avatarUri!!)
                if (file.exists()) {
                    binding.imgAvatar.setImageURI(Uri.fromFile(file))
                } else {
                    binding.imgAvatar.setImageResource(R.drawable.avatar)
                }
            } else {
                binding.imgAvatar.setImageResource(it.avatarResourceId)
            }
        }
    }

    private fun setupButtons() {
        binding.btnEdit.setOnClickListener {
            contact?.let {
                val intent = Intent(this, AddContactActivity::class.java)
                intent.putExtra("CONTACT_ID", it.id)
                startActivity(intent)
            }
        }

        binding.btnDelete.setOnClickListener {
            showDeleteConfirmation()
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun showDeleteConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Delete Contact")
            .setMessage("Are you sure you want to delete ${contact?.name}?")
            .setPositiveButton("Delete") { _, _ ->
                contact?.let {
                    dbHelper.deleteContact(it.id)
                    Toast.makeText(this, "${it.name} deleted", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        val contactId = intent.getLongExtra("CONTACT_ID", -1)
        if (contactId != -1L) {
            contact = dbHelper.getContactById(contactId)
            displayContactDetails()
        }
    }
}