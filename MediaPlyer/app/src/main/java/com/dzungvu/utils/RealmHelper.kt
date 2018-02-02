package com.dzungvu.utils

import com.dzungvu.models.Playlist
import com.dzungvu.models.Song
import io.realm.Realm
import io.realm.RealmResults
import io.realm.kotlin.where

/**
 * Use for
 * Created by DzungVu on 1/31/2018.
 */
class RealmHelper(val realm: Realm){
    public fun readAllPlaylist(): ArrayList<Playlist>{
        val all = ArrayList<Playlist>()

        val allPlaylist = realm.where(Playlist::class.java).findAll()
        allPlaylist.forEach {
            all.add(it)
        }
        return all
    }
    public fun readAllSonglist(): ArrayList<Song>{
        val all = ArrayList<Song>()

        val allPlaylist = realm.where(Song::class.java).findAll()
        allPlaylist.forEach {
            all.add(it)
        }
        return all
    }

    public fun findAllSongInList(list: List<String>): ArrayList<Song>{
        val arrSong = ArrayList<Song>()
        list.forEach {
            arrSong.add(findSongById(it.toInt()))
        }
        return arrSong
    }

    public fun findSongById(id: Int): Song{
        val song = realm.where(Song::class.java).equalTo("id", id).findFirst()
        return song!!
    }

}