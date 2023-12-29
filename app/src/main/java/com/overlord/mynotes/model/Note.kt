package com.overlord.mynotes.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID


@Entity(tableName = "notes")
data class Note (
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    @ColumnInfo(name = "title", defaultValue = "")
    var title: String?,
    @ColumnInfo(name = "description", defaultValue = "")
    var description: String?,
    @ColumnInfo(name = "isSelected", defaultValue = "false")
    var isSelected: Boolean = false,
    @ColumnInfo(name = "creationTimeMillis")
    val creationTimeMillis: Long = System.currentTimeMillis()
)