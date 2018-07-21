package com.example.davidwhyte.mintmic

import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.media.MediaFormat
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.widget.ImageButton
import java.io.IOException
import java.util.*
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {
    var state=0
    private val RECORD_REQUEST_CODE = 101
    private val WRITE_REQUEST_CODE=2930
    val mediaRecorder=MediaRecorder()
    val mediaPlayer=MediaPlayer()
    var FILE_REC=Environment.getExternalStorageDirectory().absolutePath+"/recorder.aac"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val record=findViewById<ImageButton>(R.id.record)
        record.setOnClickListener {
            if(state==0){
                state=1
                record.setImageResource(R.drawable.stop)
                record()
            }
            else{
                state=0
                record.setImageResource(R.drawable.record)
                stoprec()
            }
        }
    }
    fun record(){
        Handler().postDelayed({
          Log.v("timer","did it man")
        },3000)
        //reset the record button
        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),WRITE_REQUEST_CODE)
        }
        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.RECORD_AUDIO)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.RECORD_AUDIO),RECORD_REQUEST_CODE)
        }
        else{
            var mFileName="filearma"

            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT)
            mediaRecorder.setOutputFile(FILE_REC);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
            mediaRecorder.setAudioSamplingRate(16000)
            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
            } catch (e:IOException) {
                Log.e("LOG_TAG", "prepare() failed");
            }
        }
    }
    fun stoprec(){
        try { 
            mediaRecorder.stop()
            mediaRecorder.release()
            playrec()
        }catch (e:IOException){
            e.printStackTrace()
        }

    }

    fun playrec(){
        try {
            mediaPlayer.setDataSource(FILE_REC)
            mediaPlayer.prepare()
            mediaPlayer.start()
        }catch (e:IOException){
            e.printStackTrace()
        }
    }
}
