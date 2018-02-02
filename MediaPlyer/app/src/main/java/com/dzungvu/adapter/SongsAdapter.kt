package com.dzungvu.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.dzungvu.activities.R
import com.dzungvu.models.Song
import com.dzungvu.utils.RecyclerItemClickListener
import java.io.File

/**
 * Use for
 * Created by DzungVu on 1/29/2018.
 */
class SongsAdapter(val context: Context, val songs: ArrayList<Song>, val itemClickListener: RecyclerItemClickListener) :
RecyclerView.Adapter<SongsAdapter.MyViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MyViewHolder {
        val v: View
        if (viewType == 0){
            v = LayoutInflater.from(context).inflate(R.layout.item_song, parent, false)
        }else{
            v = LayoutInflater.from(context).inflate(R.layout.item_song_selected, parent, false)
        }
        return MyViewHolder(v, itemClickListener)
    }

    override fun onBindViewHolder(holder: MyViewHolder?, position: Int) {
        holder!!.tvSongName.text = songs[position].uri
    }

    override fun getItemViewType(position: Int): Int {
        if (!songs[position].isSelected){
            return 0
        }else{
            return 1
        }
    }

    override fun getItemCount(): Int {
        return songs.size
    }


    class MyViewHolder(itemView: View, val itemClickListener: RecyclerItemClickListener): RecyclerView.ViewHolder(itemView), View.OnClickListener {

        val tvSongName: TextView = itemView.findViewById(R.id.tv_song_name)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            itemClickListener.OnItemClickListener(p0!!, adapterPosition)
        }

    }
}