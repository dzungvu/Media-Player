package com.dzungvu.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.dzungvu.adapter.PlaylistAdapter
import com.dzungvu.models.Playlist
import com.dzungvu.models.Song
import com.dzungvu.utils.RealmHelper
import com.dzungvu.utils.RecyclerItemClickListener
import io.realm.Realm


class PlaylistActivity : AppCompatActivity(), RecyclerItemClickListener {

    private lateinit var adapter: PlaylistAdapter
    private lateinit var playlists: ArrayList<Playlist>
    private lateinit var rcvPlaylist: RecyclerView

    private lateinit var realm: Realm
    private lateinit var realmHelper: RealmHelper

    companion object {
        private val SONGS_RECEIVE_CODE = 1
        private val SONGS_RECEIVE_TAG = "song_receive"
        private val NAME_RECEIVE_TAG = "name_receive"
        private val BUNDLE_RECEIVE_TAG = "bundle_receive"

        fun newInstance(name: String, songs: ArrayList<Song>): Intent {
            val intent = Intent()
            val bundle = Bundle()
            bundle.putString(NAME_RECEIVE_TAG, name)
            bundle.putParcelableArrayList(SONGS_RECEIVE_TAG, songs)
            intent.putExtra(BUNDLE_RECEIVE_TAG, bundle)

            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playlist)

        rcvPlaylist = findViewById(R.id.rcv_playlist)
        rcvPlaylist.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        realm = Realm.getDefaultInstance()
        realmHelper = RealmHelper(realm)
        playlists = ArrayList()
    }

    override fun onResume() {
        super.onResume()
        playlists.clear()
        playlists = realmHelper.readAllPlaylist()
        adapter = PlaylistAdapter(this, playlists, this)
        rcvPlaylist.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.playlist_menu, menu)
//        val inflater = menuInflater
//        inflater.inflate(R.menu.default_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.mnu_add -> {
                gotoSongList()
                return true
            }
        }

        return false
    }

    private fun gotoSongList() {
        val intent = Intent(this, SongListActivity::class.java)
        startActivityForResult(intent, SONGS_RECEIVE_CODE)
    }

    override fun OnItemClickListener(view: View, pos: Int) {

        val intent = MainActivity.newInstance(playlists[pos])
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SONGS_RECEIVE_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val bundle = data!!.getBundleExtra(BUNDLE_RECEIVE_TAG)
                val name = bundle.getString(NAME_RECEIVE_TAG)
                val songs: ArrayList<Song> = bundle.getParcelableArrayList(SONGS_RECEIVE_TAG)
                val playlist = Playlist()
                playlist.name = name
                var listId = ""
                var isStarted = false
                songs.forEach {
                    if (isStarted) {
                        listId = listId + "," + it.id
                    } else {
                        listId = it.id.toString()
                        isStarted = true
                    }
                }
                playlist.songs = listId

                realm.beginTransaction()
                realm.insertOrUpdate(playlist)
                realm.commitTransaction()

                playlists.clear()
                playlists = realmHelper.readAllPlaylist()
                adapter.notifyDataSetChanged()
            }
        }
    }

}
