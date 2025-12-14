package com.eldroid.trashbincloud.model.repository

import com.eldroid.trashbincloud.model.entity.ActivityEvent
import com.eldroid.trashbincloud.model.entity.Commands
import com.eldroid.trashbincloud.model.entity.TrashBin
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

/**
 * Repository class for interacting with Trash Bin data in Firebase Realtime Database.
 * Handles CRUD operations and real-time updates for trash bin data.
 */
class ActivityRepository(
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
) {

    private val binsRef = database.getReference("activities")

    /**
     * Get all bins associated with user UID
     */
    fun getUserBins(userUid: String, binId: String, callback: (List<ActivityEvent>, String?) -> Unit) {
        binsRef.child(userUid).child(binId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val activitiesList = mutableListOf<ActivityEvent>()

                if (snapshot.hasChildren()) {
                    for (activitySnapshot in snapshot.children) {
                        try {
                            // Make sure we're reading an object, not a primitive
                            if (activitySnapshot.value is Map<*, *>) {
                                val activity = activitySnapshot.getValue(ActivityEvent::class.java)
                                activity?.let {
                                    activitiesList.add(it)
                                }
                            }
                        } catch (e: Exception) {
                            // Log the error but continue processing other activities
                            android.util.Log.e("ActivityRepository", "Error parsing activity: ${e.message}")
                        }
                    }
                }

                callback(activitiesList, null)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(emptyList(), error.message)
            }
        })
    }

    // TODO: Kotlin DOCS
    /**
     * Get a specific trash bin by ID
     */
    fun getBin(userUid: String, binId: String, callback: (TrashBin?, String?) -> Unit) {
        binsRef.child(userUid).child(binId)
            .addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val bin = snapshot.getValue(TrashBin::class.java)
                    callback(bin, null)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(null, error.message)
                }
            })
    }

    /**
     * Update a trash bin data
     */
    fun updateBin(userUid: String, bin: TrashBin, callback: (Boolean, String?) -> Unit) {
        binsRef.child(userUid).child(bin.binId ?: "")
            .setValue(bin)
            .addOnSuccessListener { callback(true, null) }
            .addOnFailureListener { e -> callback(false, e.message) }
    }

    /**
     * Update only the commands of the trash bin
     */
    fun updateBinCommand(userUid: String, binId: String, commands: Commands, callback: (Boolean, String?) -> Unit) {
        binsRef.child(userUid).child(binId).child("commands")
            .setValue(commands)
            .addOnSuccessListener { callback(true, null) }
            .addOnFailureListener { e -> callback(false, e.message) }
    }

    /**
     * Add a new trash bin
     * MAY NOT BE USED, since device will be the one who should add trash bin.
     */
    fun addBin(userUid: String, bin: TrashBin, callback: (Boolean, String?) -> Unit) {
        binsRef.child(userUid).child(bin.binId ?: "")
            .setValue(bin)
            .addOnSuccessListener { callback(true, null) }
            .addOnFailureListener { e -> callback(false, e.message) }
    }

    /**
     * Delete a trash bin
     * MAY NOT BE USED, since device will be the one who should delete trash bin.
     */
    fun deleteBin(userUid: String, binId: String, callback: (Boolean, String?) -> Unit) {
        binsRef.child(userUid).child(binId)
            .removeValue()
            .addOnSuccessListener { callback(true, null) }
            .addOnFailureListener { e -> callback(false, e.message) }
    }

    /**
     * Get all trash bins
     */
    fun getAllBins(userUid: String, callback: (List<TrashBin>, String?) -> Unit) {
        binsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val binsList = mutableListOf<TrashBin>()
                for (binSnapshot in snapshot.children) {
                    val bin = binSnapshot.getValue(TrashBin::class.java)
                    bin?.let { binsList.add(it) }
                }
                callback(binsList, null)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(emptyList(), error.message)
            }
        })
    }

    /**
     * Listen for real-time updates on a specific bin
     */
    fun listenForBinUpdates(binId: String, callback: (TrashBin?, String?) -> Unit): ValueEventListener {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val bin = snapshot.getValue(TrashBin::class.java)
                callback(bin, null)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null, error.message)
            }
        }

        binsRef.child(binId).addValueEventListener(listener)
        return listener
    }

    /**
     * Remove a listener when no longer needed
     */
    fun removeListener(binId: String, listener: ValueEventListener) {
        binsRef.child(binId).removeEventListener(listener)
    }
}
