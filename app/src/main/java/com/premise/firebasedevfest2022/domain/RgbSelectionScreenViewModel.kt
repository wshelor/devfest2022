package com.premise.firebasedevfest2022.domain

import androidx.lifecycle.ViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import kotlinx.coroutines.flow.MutableStateFlow

class RgbSelectionScreenViewModel : ViewModel() {
    private val database get() = FirebaseDatabase.getInstance().reference.child("rgbBattle")
    private val firebaseAnalytics = Firebase.analytics
    val dataState = MutableStateFlow(State())

    init {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dataState.value = State(
                    snapshot.child("Red").getValue(Int::class.java) ?: 0,
                    snapshot.child("Green").getValue(Int::class.java) ?: 0,
                    snapshot.child("Blue").getValue(Int::class.java) ?: 0,
                    snapshot.child("Enabled").getValue(Boolean::class.java) ?: false,
                    Firebase.remoteConfig.getString("RgbTeamSelection")
                )
            }

            override fun onCancelled(error: DatabaseError) {
                FirebaseCrashlytics.getInstance().recordException(Throwable(error.message))
            }
        })
    }

    fun addPoint() {
        when (dataState.value.currentTeam) {
            "Red" -> database.child("Red").setValue(dataState.value.redPoints + 1)
            "Green" -> database.child("Green").setValue(dataState.value.greenPoints + 1)
            "Blue" -> database.child("Blue").setValue(dataState.value.bluePoints + 1)
            else -> FirebaseCrashlytics.getInstance().recordException(java.lang.Exception("Attempted to vote for nonexistent team!"))
        }
        firebaseAnalytics.logEvent("color_voted") {
            param("color", dataState.value.currentTeam)
        }
        database.push()
    }

    data class State(val redPoints: Int = 0, val greenPoints: Int = 0, val bluePoints: Int = 0, val enabled: Boolean = false, val currentTeam: String = "Loading")
}

