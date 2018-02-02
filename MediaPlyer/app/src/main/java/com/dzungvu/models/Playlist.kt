package com.dzungvu.models

import io.realm.RealmModel
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

/**
 * Use for
 * Created by DzungVu on 1/31/2018.
 */

@RealmClass
open class Playlist(
        @PrimaryKey
        open var name: String = "",
        open var songs: String = ""
) : RealmModel {

}