package eu.epitech.levisse.thierry.todolist

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcel
import android.os.Parcelable
import java.util.*


/**
 * Created by thierry on 29/01/18.
 */

@Entity(tableName = "task")

data class Task(@ColumnInfo(name = "title") var title: String = "",
                @ColumnInfo(name = "description") var description: String = "",
                @ColumnInfo(name = "creationDate") var creationDate: Date = Calendar.getInstance().time,
                @ColumnInfo(name = "deadlineDate") var deadlineDate: Date = Date(0),
                @ColumnInfo(name = "imageLocation") var imageLocation: String = "",
                @ColumnInfo(name = "frequence") var frequence: Int = 0, // 0 = one-time, 1 = daily
                @ColumnInfo(name = "done") var done: Int = 0, // 0 = in progress, 1 = done
                @PrimaryKey(autoGenerate = true) var id: Long = 0): Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            Date(parcel.readLong()),
            Date(parcel.readLong()),
            parcel.readString(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readLong())

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(title)
        dest.writeString(description)
        dest.writeLong(creationDate.time)
        dest.writeLong(deadlineDate.time)
        dest.writeString(imageLocation)
        dest.writeInt(frequence)
        dest.writeInt(done)
        dest.writeLong(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Task> {
        override fun createFromParcel(parcel: Parcel): Task {
            return Task(parcel)
        }

        override fun newArray(size: Int): Array<Task?> {
            return arrayOfNulls(size)
        }
    }

}