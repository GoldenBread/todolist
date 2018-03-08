package eu.epitech.levisse.thierry.todolist

import android.arch.persistence.room.TypeConverter
import java.util.*

/**
 * Created by thierry on 31/01/18.
 */

class DateConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return if (value == null) null else Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}
