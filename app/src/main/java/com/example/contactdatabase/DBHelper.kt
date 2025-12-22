package com.example.contactdatabase

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context):
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

        companion object {
            private const val DATABASE_NAME = "contacts.db"
            private const val DATABASE_VERSION = 1
            private const val TABLE_NAME = "contacts"

            private const val COLUMN_ID = "id"
            private const val COLUMN_NAME = "name"
            private const val COLUMN_PHONE = "phone"
            private const val COLUMN_EMAIL = "email"
            private const val COLUMN_AVATAR_RES = "avatar_resource_id"
            private const val COLUMN_AVATAR_URI = "avatar_uri"
        }

        override fun onCreate(db: SQLiteDatabase) {
            val createTable = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_PHONE TEXT NOT NULL,
                $COLUMN_EMAIL TEXT,
                $COLUMN_AVATAR_RES INTEGER,
                $COLUMN_AVATAR_URI TEXT
            )
        """.trimIndent()

            db.execSQL(createTable)
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
            onCreate(db)
        }
    // Insert contact
    fun insertContact(contact: Contact): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, contact.name)
            put(COLUMN_PHONE, contact.phone)
            put(COLUMN_EMAIL, contact.email)
            put(COLUMN_AVATAR_RES, contact.avatarResourceId)
            put(COLUMN_AVATAR_URI, contact.avatarUri)
        }
        return db.insert(TABLE_NAME, null, values)
    }

    // Get all contacts
    fun getAllContacts(): List<Contact> {
        val contactList = mutableListOf<Contact>()
        val db = readableDatabase
        val cursor = db.query(TABLE_NAME, null, null, null, null, null, "$COLUMN_NAME ASC")

        with(cursor) {
            while (moveToNext()) {
                val contact = Contact(
                    id = getLong(getColumnIndexOrThrow(COLUMN_ID)),
                    name = getString(getColumnIndexOrThrow(COLUMN_NAME)),
                    phone = getString(getColumnIndexOrThrow(COLUMN_PHONE)),
                    email = getString(getColumnIndexOrThrow(COLUMN_EMAIL)) ?: "",
                    avatarResourceId = getInt(getColumnIndexOrThrow(COLUMN_AVATAR_RES)),
                    avatarUri = getString(getColumnIndexOrThrow(COLUMN_AVATAR_URI))
                )
                contactList.add(contact)
            }
            close()
        }
        return contactList
    }

    // Update contact
    fun updateContact(contact: Contact): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, contact.name)
            put(COLUMN_PHONE, contact.phone)
            put(COLUMN_EMAIL, contact.email)
            put(COLUMN_AVATAR_RES, contact.avatarResourceId)
            put(COLUMN_AVATAR_URI, contact.avatarUri)
        }
        return db.update(TABLE_NAME, values, "$COLUMN_ID = ?", arrayOf(contact.id.toString()))
    }

    // Delete contact
    fun deleteContact(id: Long): Int {
        val db = writableDatabase
        return db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }

    // Get contact by Id
    fun getContactById(id: Long): Contact? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_NAME, null, "$COLUMN_ID = ?", arrayOf(id.toString()),
            null, null, null
        )

        var contact: Contact? = null
        if (cursor.moveToFirst()) {
            contact = Contact(
                id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                phone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)),
                email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)) ?: "",
                avatarResourceId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_AVATAR_RES)),
                avatarUri = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AVATAR_URI))
            )
        }
        cursor.close()
        return contact
    }

    }
