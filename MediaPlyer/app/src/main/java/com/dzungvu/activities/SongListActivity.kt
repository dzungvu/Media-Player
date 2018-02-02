package com.dzungvu.activities

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.dzungvu.adapter.SongsAdapter
import com.dzungvu.models.Song
import com.dzungvu.utils.RealmHelper
import com.dzungvu.utils.RecyclerItemClickListener
import io.realm.Realm
import java.io.File

class SongListActivity : AppCompatActivity(), RecyclerItemClickListener {

    private val READ_EXTERNAL_CODE = 1

    private lateinit var edtPlaylistName: EditText
    private lateinit var rcvSongs: RecyclerView
    private lateinit var adapter: SongsAdapter
    private lateinit var songs: ArrayList<Song>
    private lateinit var songsSelected: ArrayList<Song>

    private lateinit var realm: Realm
    private lateinit var realmHelper: RealmHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_song_list)

        realm = Realm.getDefaultInstance()
        realmHelper = RealmHelper(realm)

        edtPlaylistName = findViewById(R.id.edt_playlist_name)
        rcvSongs = findViewById(R.id.rcv_songs)
        rcvSongs.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        songsSelected = ArrayList()

        checkPermission()

    }

    override fun OnItemClickListener(view: View, pos: Int) {

    }

    private fun checkPermission() {
        val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        permissions.forEach {
            val permission = ContextCompat.checkSelfPermission(this, it)
            if (permission != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(permissions, READ_EXTERNAL_CODE)
                }
                return
            }
        }

//        songs = getSongs(Environment.getExternalStorageDirectory())
//        adapter = SongsAdapter(this, songs, this)
//        rcvSongs.adapter = adapter

        val task = getSongTask(this)
        task.execute()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.song_list_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item!!.itemId) {
            R.id.mnu_done -> {
                addAllSongSelected()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == READ_EXTERNAL_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                songs = getSongs(Environment.getExternalStorageDirectory())
//                adapter = SongsAdapter(this, songs, this)
//                rcvSongs.adapter = adapter
                val task = getSongTask(this)
                task.execute()
            } else {
                Toast.makeText(this, "Can not access your phone", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    inner class getSongTask(val context: Context) : AsyncTask<Void, Void, ArrayList<Song>>(), RecyclerItemClickListener {
        override fun OnItemClickListener(view: View, pos: Int) {
//            Toast.makeText(context, songs[pos].name, Toast.LENGTH_LONG).show()
            if (songs[pos].isSelected) {
                songs[pos].isSelected = false
                songsSelected.remove(songs[pos])
            } else {
                songs[pos].isSelected = true
                songsSelected.add(songs[pos])
            }
            adapter.notifyDataSetChanged()
        }

        val progressDialog = ProgressDialog(context)

        override fun onPreExecute() {
            super.onPreExecute()
            progressDialog.setMessage(getString(R.string.please_wait))
            progressDialog.setCancelable(false)
            progressDialog.show()
        }

        override fun doInBackground(vararg p0: Void?): ArrayList<Song> {
            return getSongs(Environment.getExternalStorageDirectory())
        }

        override fun onPostExecute(result: ArrayList<Song>?) {
            super.onPostExecute(result)
            progressDialog.dismiss()
            songs = result!!
            realm.beginTransaction()
            songs.forEach {
                realm.insertOrUpdate(it)
            }
            realm.commitTransaction()
            adapter = SongsAdapter(context, songs, this)
            rcvSongs.adapter = adapter
        }

    }

    private fun getSongs(root: File): ArrayList<Song> {
        val all: ArrayList<Song> = ArrayList()
        val files = root.listFiles()
        var i = 0
        for (file: File in files) {
            if (file.isDirectory && !file.isHidden) {
                all.addAll(getSongs(file))
            } else {
                if (file.name.endsWith(".mp3")) {
                    val song = Song()
                    song.id = i
                    i += 1
                    song.uri = file.toString()
                    all.add(song)
                }
            }
        }
        return all
    }

    private fun addAllSongSelected() {
        if (TextUtils.isEmpty(edtPlaylistName.text.toString())){
            Toast.makeText(this, getString(R.string.please_enter_playlist_name), Toast.LENGTH_LONG).show()
        }else if(songsSelected.size == 0){
            Toast.makeText(this, getString(R.string.select_at_list_one_song), Toast.LENGTH_LONG).show()
        }else{
            val intent = PlaylistActivity.newInstance(edtPlaylistName.text.toString(), songsSelected)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }
}
