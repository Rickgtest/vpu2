package com.example.vpu2.appUi

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.vpu2.data.Converters
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "u_arch")
@TypeConverters(Converters::class)
data class UArch(
    @PrimaryKey
    val name: String,
    val description: String?,
    val arch: String?,
    val date: Int?,
    val images: List<String>?,
    val content: String? = null
)
