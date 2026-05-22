package com.example.data

import androidx.room.TypeConverter

class GameConverters {
    @TypeConverter
    fun fromGenre(value: MovieGenre): String {
        return value.name
    }

    @TypeConverter
    fun toGenre(value: String): MovieGenre {
        return try {
            MovieGenre.valueOf(value)
        } catch (e: Exception) {
            MovieGenre.DRAMA
        }
    }

    @TypeConverter
    fun fromStage(value: ActiveProjectStage): String {
        return value.name
    }

    @TypeConverter
    fun toStage(value: String): ActiveProjectStage {
        return try {
            ActiveProjectStage.valueOf(value)
        } catch (e: Exception) {
            ActiveProjectStage.NONE
        }
    }
}
