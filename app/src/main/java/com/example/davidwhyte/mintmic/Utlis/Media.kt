package com.example.davidwhyte.mintmic.Utlis

import android.media.MediaPlayer
import java.io.IOException

class Media {
    val mediaPlayer= MediaPlayer()

    fun playrec(dir:String){
        try {

            if(mediaPlayer.isPlaying){
                mediaPlayer.stop()
            }
            mediaPlayer.setDataSource(dir)
            mediaPlayer.prepare()
            mediaPlayer.start()
            System.out.print(dir)
        }catch (e: IOException){
            e.printStackTrace()
        }
    }
}