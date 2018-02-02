package com.dzungvu.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.dzungvu.activities.R
import com.dzungvu.models.Playlist
import com.dzungvu.utils.RecyclerItemClickListener

/**
 * Use for
 * Created by DzungVu on 1/31/2018.
 */
class PlaylistAdapter(val context: Context, val playlists: ArrayList<Playlist>, val itemClickListener: RecyclerItemClickListener): RecyclerView.Adapter<PlaylistAdapter.MyViewHolder>() {
    override fun onBindViewHolder(holder: MyViewHolder?, position: Int) {
        val playlist = playlists[position]
        holder!!.tvPlaylistName.text = playlist.name
        holder.tvPlatlistSize.text = ((playlist.songs.length / 2 + 1).toString() + "Song(s)")
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.item_playlist, parent, false)

        return MyViewHolder(v, itemClickListener)
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    class MyViewHolder(itemView: View, val itemClickListener: RecyclerItemClickListener) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val tvPlaylistName: TextView = itemView.findViewById(R.id.tv_play_list_name)
        val tvPlatlistSize: TextView = itemView.findViewById(R.id.tv_play_list_size)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            itemClickListener.OnItemClickListener(p0!!, adapterPosition)
        }
    }

}