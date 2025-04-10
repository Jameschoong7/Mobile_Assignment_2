package com.cet3014n.assignment1

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun fromDietaryList(dietary: List<String>?): String? {
        return dietary?.joinToString(",")
    }

    @TypeConverter
    fun toDietaryList(dietaryString: String?): List<String>? {
        return dietaryString?.split(",")?.map { it.trim() }
    }
}