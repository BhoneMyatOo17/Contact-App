package com.example.contactdatabase

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.contactdatabase.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dbHelper: DBHelper
    private lateinit var adapter: ContactAdapter
    private var allContacts = listOf<Contact>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DBHelper(this)

        setupRecyclerView()
        setupSwipeToDelete()
        loadContacts()
        setupSearchButton()
        setupAddButtons()
    }

    private fun setupRecyclerView() {
        adapter = ContactAdapter(emptyList()) { contact ->
            val intent = Intent(this, ContactDetailsActivity::class.java)
            intent.putExtra("CONTACT_ID", contact.id)
            startActivity(intent)
        }

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun loadContacts() {
        allContacts = dbHelper.getAllContacts()
        adapter.updateContacts(allContacts)

        if (allContacts.isEmpty()) {
            binding.emptyState.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
            binding.fbtnAdd.visibility = View.GONE
        } else {
            binding.emptyState.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
            binding.fbtnAdd.visibility = View.VISIBLE
        }
    }

    private fun setupSearchButton() {
        binding.btnSearch.setOnClickListener {
            val query = binding.txtSearch.text.toString()
            searchContacts(query)
        }
    }

    private fun searchContacts(query: String) {
        if (query.isEmpty()) {
            adapter.updateContacts(allContacts)
        } else {
            val results = allContacts.filter { contact ->
                contact.name.contains(query, ignoreCase = true) ||
                        contact.phone.contains(query, ignoreCase = true)
            }
            adapter.updateContacts(results)

            if (results.isEmpty()) {
                Toast.makeText(this, "No contacts found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupSwipeToDelete() {
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ) = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val contact = allContacts[position]

                dbHelper.deleteContact(contact.id)
                loadContacts()

                Snackbar.make(binding.recyclerView, "${contact.name} deleted", Snackbar.LENGTH_LONG)
                    .setAction("UNDO") {
                        dbHelper.insertContact(contact)
                        loadContacts()
                    }
                    .show()
            }
        })

        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
    }

    private fun setupAddButtons() {
        binding.btnAddContact.setOnClickListener {
            val intent = Intent(this, AddContactActivity::class.java)
            startActivity(intent)
        }

        binding.fbtnAdd.setOnClickListener {
            val intent = Intent(this, AddContactActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        loadContacts()
    }
}