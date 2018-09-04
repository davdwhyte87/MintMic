package com.example.davidwhyte.mintmic

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.example.davidwhyte.mintmic.Model.Record
import com.example.davidwhyte.mintmic.Utlis.Media
import kotlinx.android.synthetic.main.record_list.view.*

class RecordAdapter(val items:ArrayList<Record>, val context:Context ):RecyclerView.Adapter<RecordAdapter.ViewHolder>(){
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view=LayoutInflater.from(p0.context).inflate(R.layout.record_list,p0,false) as LinearLayout
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
       return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.view.record_name.text=items[position].name
        holder.view.size.text=items[position].name
//        holder.view.record_date.text=items[position].date
        //playbtn onclick listener
        holder.view.play_btn.setOnClickListener {
            var mmedia=Media()
            mmedia.playrec(items[position].r_link)
            val kk=it
            kk.play_btn.setImageResource(R.drawable.stop)
            if (mmedia.mediaPlayer.isPlaying){
                kk.play_btn.setImageResource(R.drawable.play)
            }
            mmedia.mediaPlayer.setOnCompletionListener {
                kk.play_btn.setImageResource(R.drawable.play)
            }

        }
    }
    class ViewHolder(val view:View) : RecyclerView.ViewHolder(view)
}