package com.example.compose00.model

import android.os.Parcel
import android.os.Parcelable

/**
 *
 *
 * @author 高超（gaochao.cc）
 * @since 2024/2/2
 */
data class Message(
    val author: String,

    val body: String,
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
    )

    fun isMe(): Boolean {
        return author == "CreateChance"
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(author)
        dest.writeString(body)
    }

    companion object CREATOR : Parcelable.Creator<Message> {
        override fun createFromParcel(parcel: Parcel): Message {
            return Message(parcel)
        }

        override fun newArray(size: Int): Array<Message?> {
            return arrayOfNulls(size)
        }
    }
}