package com.dzungvu.activities

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Toast
import com.dzungvu.adapter.SongsAdapter
import com.dzungvu.utils.RecyclerItemClickListener
import java.io.File

class SongListActivity : AppCompatActivity(), RecyclerItemClickListener {

    private val READ_EXTERNAL_CODE = 1

    private lateinit var rcvSongs: RecyclerView
    private lateinit var adapter: SongsAdapter
    private lateinit var songs: ArrayList<File>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_song_list)

        rcvSongs = findViewById(R.id.rcv_songs)
        rcvSongs.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        checkPermission()

    }

    override fun OnItemClickListener(view: View, pos: Int) {
        Toast.makeText(this, songs[pos].name, Toast.LENGTH_LONG).show()
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

    inner class getSongTask (val context: Context) : AsyncTask<Void, Void, ArrayList<File>>(), RecyclerItemClickListener {
        override fun OnItemClickListener(view: View, pos: Int) {
//            Toast.makeText(context, songs[pos].name, Toast.LENGTH_LONG).show()
            val intent = Intent()
            intent.putExtra("song", songs[pos])
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        val progressDialog = ProgressDialog(context)

        override fun onPreExecute() {
            super.onPreExecute()
            progressDialog.setMessage("Please wait")
            progressDialog.setCancelable(false)
            progressDialog.show()
        }

        override fun doInBackground(vararg p0: Void?): ArrayList<File> {
            return getSongs(Environment.getExternalStorageDirectory())
        }

        override fun onPostExecute(result: ArrayList<File>?) {
            super.onPostExecute(result)
            progressDialog.dismiss()
            songs = result!!
            adapter = SongsAdapter(context, songs, this)
            rcvSongs.adapter = adapter
        }

    }

    private fun getSongs(root: File): ArrayList<File> {
        val all: ArrayList<File> = ArrayList()
        val files = root.listFiles()
        for (file: File in files) {
            if (file.isDirectory && !file.isHidden) {
                all.addAll(getSongs(file))
            } else {
                if (file.name.endsWith(".mp3")) {
                    all.add(file)
                }
            }
        }
        return all
    }
}
