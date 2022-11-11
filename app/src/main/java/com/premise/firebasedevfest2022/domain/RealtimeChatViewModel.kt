package com.premise.firebasedevfest2022.domain

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow

class RealtimeChatViewModel : ViewModel() {
    private val database get() = FirebaseDatabase.getInstance().reference.child("chatApp")
    val dataState = MutableStateFlow(listOf<ChatMessage>())

    init {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<ChatMessage>()
                for (ds in snapshot.children) {
                    val message = ds.getValue(ChatMessage::class.java)
                    message?.let { list.add(it) }
                }
                dataState.value = list
            }

            override fun onCancelled(error: DatabaseError) {
                FirebaseCrashlytics.getInstance().recordException(Throwable(error.message))
            }
        })
    }

    fun addMessage(message: String) {
        val user = FirebaseAuth.getInstance().currentUser!!
        database.push().setValue(ChatMessage(message, user.displayName ?: user.uid))
    }
}

data class ChatMessage(val message: String = "", val sender: String = "")