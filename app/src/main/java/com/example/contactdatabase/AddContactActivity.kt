package com.example.contactdatabase

import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.contactdatabase.databinding.ActivityAddContactBinding

class AddContactActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddContactBinding
    private lateinit var dbHelper: DBHelper
    private var selectedAvatarResId: Int = R.drawable.avatar
    private var selectedAvatarUri: String? = null
    private var contactToEdit: Contact? = null

    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val savedUri = copyImageToInternalStorage(it)
            if (savedUri != null) {
                selectedAvatarUri = savedUri
                selectedAvatarResId = R.drawable.avatar
                binding.imgSelectedAvatar.setImageURI(Uri.parse(savedUri))
            }
        }
    }

    private fun copyImageToInternalStorage(uri: Uri): String? {
        try {
            val inputStream = contentResolver.openInputStream(uri)
            val fileName = "avatar_${System.currentTimeMillis()}.jpg"
            val file = java.io.File(filesDir, fileName)

            inputStream?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            return file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show()
            return null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddContactBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DBHelper(this)

        val contactId = intent.getLongExtra("CONTACT_ID", -1)
        if (contactId != -1L) {
            contactToEdit = dbHelper.getContactById(contactId)
            loadContactData()
        }

        setupButtons()
    }

    private fun loadContactData() {
        contactToEdit?.let { contact ->
            binding.txtTitle.text = "Edit Contact"
            binding.txtName.setText(contact.name)
            binding.txtPhone.setText(contact.phone)
            binding.txtEmail.setText(contact.email)

            selectedAvatarResId = contact.avatarResourceId
            selectedAvatarUri = contact.avatarUri

            if (contact.avatarUri != null) {
                binding.imgSelectedAvatar.setImageURI(android.net.Uri.parse(contact.avatarUri))
            } else {
                binding.imgSelectedAvatar.setImageResource(contact.avatarResourceId)
            }
        }
    }

    private fun setupButtons() {
        binding.btnChooseAvatar.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        binding.btnSave.setOnClickListener {
            saveContact()
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }
    }

    private fun saveContact() {
        val name = binding.txtName.text.toString().trim()
        val phone = binding.txtPhone.text.toString().trim()
        val email = binding.txtEmail.text.toString().trim()

        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show()
            return
        }

        if (phone.isEmpty()) {
            Toast.makeText(this, "Please enter a phone number", Toast.LENGTH_SHORT).show()
            return
        }

        if (contactToEdit != null) {

            val updatedContact = Contact(
                id = contactToEdit!!.id,
                name = name,
                phone = phone,
                email = email,
                avatarResourceId = selectedAvatarResId,
                avatarUri = selectedAvatarUri
            )
            dbHelper.updateContact(updatedContact)
            Toast.makeText(this, "Contact updated", Toast.LENGTH_SHORT).show()
        } else {
            val newContact = Contact(
                name = name,
                phone = phone,
                email = email,
                avatarResourceId = selectedAvatarResId,
                avatarUri = selectedAvatarUri
            )
            dbHelper.insertContact(newContact)
            Toast.makeText(this, "Contact saved", Toast.LENGTH_SHORT).show()
        }

        finish()
    }
}