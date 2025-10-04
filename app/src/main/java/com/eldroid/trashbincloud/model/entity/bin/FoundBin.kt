package com.eldroid.trashbincloud.model.entity.bin

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FoundBin(
    val binId: String = "",
    val name: String = "",
    val location: String = "",
    val isProvisioned: Boolean = false
) : Parcelable
