package com.eldroid.trashbincloud.model.repository

import com.eldroid.trashbincloud.model.entity.TrashBin
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

/**
 * Repository class for interacting with Trash Bin data in Firebase Realtime Database.
 * Handles CRUD operations and real-time updates for trash bin data.
 */
class TrashBinRepository(
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
) {
    private val binsRef = database.getReference("trash_bins")

    /**
     * Get a specific trash bin by ID
     */
    fun getBin(binId: String, callback: (TrashBin?, String?) -> Unit) {
        binsRef.child(binId).addListenerForSingleValueEvent(object : ValueEventListener {
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
     * Get all trash bins
     */
    fun getAllBins(callback: (List<TrashBin>, String?) -> Unit) {
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
     * Update a trash bin data
     */
    fun updateBin(bin: TrashBin, callback: (Boolean, String?) -> Unit) {
        binsRef.child(bin.binId).setValue(bin)
            .addOnSuccessListener {
                callback(true, null)
            }
            .addOnFailureListener { e ->
                callback(false, e.message)
            }
    }

    /**
     * Add a new trash bin
     */
    fun addBin(bin: TrashBin, callback: (Boolean, String?) -> Unit) {
        val newBinRef = binsRef.push()
        val binWithId = bin.copy(binId = newBinRef.key ?: "")

        newBinRef.setValue(binWithId)
            .addOnSuccessListener {
                callback(true, null)
            }
            .addOnFailureListener { e ->
                callback(false, e.message)
            }
    }

    /**
     * Delete a trash bin
     */
    fun deleteBin(binId: String, callback: (Boolean, String?) -> Unit) {
        binsRef.child(binId).removeValue()
            .addOnSuccessListener {
                callback(true, null)
            }
            .addOnFailureListener { e ->
                callback(false, e.message)
            }
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
