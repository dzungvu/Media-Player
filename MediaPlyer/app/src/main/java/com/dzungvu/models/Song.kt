package com.dzungvu.models

import android.os.Parcel
import android.os.Parcelable
import io.realm.RealmModel
import io.realm.annotations.RealmClass

/**
 * Use for
 * Created by DzungVu on 1/31/2018.
 */
@RealmClass
open class Song(
        open var id: Int = 0,
        open var uri: String = "",
        open var isSelected: Boolean = false
) : Parcelable, RealmModel {
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString(),
            parcel.readByte() != 0.toByte()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(uri)
        parcel.writeByte(if (isSelected) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Song> {
        override fun createFromParcel(parcel: Parcel): Song {
            return Song(parcel)
        }

        override fun newArray(size: Int): Array<Song?> {
            return arrayOfNulls(size)
        }
    }


}