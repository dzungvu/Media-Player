package com.dzungvu.activities

import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import java.io.Serializable
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private val GET_PLAY_LIST_CODE = 1

    private lateinit var btnPlaylist: ImageButton
    private lateinit var btnPrev: ImageButton
    private lateinit var btnNext: ImageButton
    private lateinit var btnBackward: ImageButton
    private lateinit var btnForward: ImageButton
    private lateinit var btnPlay: ImageButton
    private lateinit var btnRepeat: ImageButton
    private lateinit var btnShuffle: ImageButton
    private lateinit var sbProgress: SeekBar
    private lateinit var tvSongTitle: TextView
    private lateinit var tvCurrentDuration: TextView
    private lateinit var tvTotalDuration: TextView

    private lateinit var mediaPlayer: MediaPlayer
    private var isMPInit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnPlaylist = findViewById(R.id.btn_playlist)
        btnPlaylist.setOnClickListener(this)
        btnPlay = findViewById(R.id.btn_play)
        btnPlay.setOnClickListener(this)
        btnBackward = findViewById(R.id.btn_backward)
        btnBackward.setOnClickListener(this)
        btnForward = findViewById(R.id.btn_forward)
        btnForward.setOnClickListener(this)
        btnPrev = findViewById(R.id.btn_prev)
        btnPrev.setOnClickListener(this)
        btnNext = findViewById(R.id.btn_next)
        btnNext.setOnClickListener(this)
        btnRepeat = findViewById(R.id.btn_repeat)
        btnNext.setOnClickListener(this)
        btnShuffle = findViewById(R.id.btn_shuffle)
        btnShuffle.setOnClickListener(this)
        sbProgress = findViewById(R.id.sb_song_progress)
        tvSongTitle = findViewById(R.id.tv_song_title)
        tvCurrentDuration = findViewById(R.id.tv_song_current_duration_label)
        tvTotalDuration = findViewById(R.id.tv_song_total_duration_label)
        tvSongTitle.isSelected = true

    }


    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.btn_playlist -> doPlaylist()
            R.id.btn_prev -> doPrevious()
            R.id.btn_backward -> doBackward()
            R.id.btn_play -> doPlay()
            R.id.btn_forward -> doForward()
            R.id.btn_next -> doNext()
            R.id.btn_repeat -> doRepeat()
            R.id.btn_shuffle -> doShuffle()
        }
    }

    private fun doPlaylist() {

        val intent = Intent(this, SongListActivity::class.java)
        startActivityForResult(intent, GET_PLAY_LIST_CODE)

    }

    private fun doPrevious() {

    }

    private fun doBackward() {
        try {
            mediaPlayer.seekTo(mediaPlayer.currentPosition - 5000)
        } catch (e: UninitializedPropertyAccessException) {
            e.printStackTrace()
            Toast.makeText(this, "Please select a song to play", Toast.LENGTH_LONG).show()
        }
    }

    private fun doForward() {
        try {
            mediaPlayer.seekTo(mediaPlayer.currentPosition + 5000)
        } catch (e: UninitializedPropertyAccessException) {
            e.printStackTrace()
            Toast.makeText(this, "Please select a song to play", Toast.LENGTH_LONG).show()
        }
    }

    private fun doPlay() {

        try {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
                btnPlay.setImageResource(R.drawable.control_play)
            } else {
                mediaPlayer.start()
                btnPlay.setImageResource(R.drawable.control_pause)
            }
        } catch (e: UninitializedPropertyAccessException) {
            e.printStackTrace()
            Toast.makeText(this, "Please select a song to play", Toast.LENGTH_LONG).show()
        }

    }

    private fun doNext() {

    }

    private fun doRepeat() {

    }

    private fun doShuffle() {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GET_PLAY_LIST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val song = data!!.getSerializableExtra("song")
                playSong(song)
            }
        }
    }

    private fun playSong(song: Serializable) {
        val songNames = song.toString().split("/")
        val songName = songNames[songNames.size - 1]

        val uri = Uri.parse(song.toString())
        if (isMPInit) {
            mediaPlayer.stop()
            mediaPlayer.release()
        }
        mediaPlayer = MediaPlayer.create(this, uri)
        isMPInit = true
        mediaPlayer.start()
        btnPlay.setImageResource(R.drawable.control_pause)
        tvSongTitle.text = songName.replace(".mp3", "")

        val time = TimeUnit.MILLISECONDS.toMinutes(mediaPlayer.duration.toLong()).toString() + ":" + (TimeUnit.MILLISECONDS.toSeconds(mediaPlayer.duration.toLong()) % 60).toString()
        tvTotalDuration.text = time
        thread()
    }

    private fun thread(): Thread {

        val thread = object : Thread() {
            override fun run() {
                val totalDuration = mediaPlayer.duration
                var currentPosition = 0
                sbProgress.max = totalDuration
                while (currentPosition < totalDuration) {
                    try {
                        Thread.sleep(500)
                        currentPosition = mediaPlayer.currentPosition
                        sbProgress.progress = currentPosition
                        this@MainActivity.runOnUiThread {
                            Runnable { tvCurrentDuration.text = TimeUnit.MINUTES.toMinutes(currentPosition.toLong()).toString() }
                        }
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
            }
        }
        thread.start()
        return thread
    }


}
