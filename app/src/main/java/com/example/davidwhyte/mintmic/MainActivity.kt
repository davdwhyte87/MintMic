package com.example.davidwhyte.mintmic

import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.media.MediaFormat
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.*
import android.provider.BaseColumns
import android.support.v7.app.AppCompatActivity
import android.support.v4.app.ActivityCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.example.davidwhyte.mintmic.Model.Record
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*
import java.util.jar.Manifest
import kotlin.collections.ArrayList
import kotlin.concurrent.timer

class MainActivity : AppCompatActivity() {
    var state=0
    private val RECORD_REQUEST_CODE = 101
    private val WRITE_REQUEST_CODE=2930
    val mediaRecorder=MediaRecorder()
    val mediaPlayer=MediaPlayer()
    var FILE_REC=""
    var REC_LINK=""
    var tmSum:Long=0
    var timer:CountDownTimer?=null
    val dir=Environment.getExternalStorageDirectory().absolutePath+"/MintMic"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //list button handler
        val listbtn=findViewById<ImageButton>(R.id.record_list)
        listbtn.setOnClickListener {
            val intent=Intent(this,RecordsActivity::class.java)
            startActivity(intent)
        }
        //record button handler
        val record=findViewById<ImageButton>(R.id.record)
        getData()
        timer=timer()
        if(!File(dir).exists()){
            File(dir).mkdir()
        }
        record.setOnClickListener {
            if(state==0){
                state=1
                tmSum=0
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


    fun timer():CountDownTimer{
        val minute:Long=1000*60
        val future:Long=(minute * 1440) + (minute * 155) + (1000 * 50)
        val interval:Long=1000
        var d_time=""

        val timer_view=findViewById<TextView>(R.id.timer_view)
        return object:CountDownTimer(future,interval){
            override fun onTick(p0: Long) {
                tmSum++
                //check if tsum is up to a minute
                if(tmSum<60){
                    timer_view.text=tmSum.toString()
                }
                else{
                    //get how many minutes
                }
            }
            override fun onFinish() {
                timer_view.text=0.toString()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(WRITE_REQUEST_CODE==requestCode){
            record()
        }
        if(RECORD_REQUEST_CODE==requestCode){
            record()
        }
    }
    fun record(){
        //reset the record button
        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),WRITE_REQUEST_CODE)
        }
        else{
            if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.RECORD_AUDIO)!=PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.RECORD_AUDIO),RECORD_REQUEST_CODE)
            }
            else{
                var n=File(dir).walkTopDown().count()
                var name="record"+n+".aac"
                var mFileName=dir+"/"+name
                FILE_REC=name
                REC_LINK=mFileName
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT)
                mediaRecorder.setOutputFile(mFileName)
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT)
                mediaRecorder.setAudioSamplingRate(2500)
                try {
                    mediaRecorder.prepare()
                    mediaRecorder.start()
                    timer?.start()
                } catch (e:IOException) {
                    Log.e("LOG_TAG", "prepare() failed");
                }
            }
        }

    }
    fun stoprec(){
        try {
            mediaRecorder.stop()
//            mediaRecorder.release()
            timer?.cancel()
            saveData()
            Toast.makeText(this,"Record saved",Toast.LENGTH_SHORT).show()
//            playrec()
        }catch (e:IOException){
            e.printStackTrace()
        }

    }

    fun getData(){
        //this function gets the data for a record ready
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val currentDate = sdf.format(Date())
        val filename=FILE_REC
        val duration=tmSum.toString()
    }

    fun saveData(){
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val currentDate = sdf.format(Date())
        val dbHelper=RecordContract.RecordEntry.RecordDbHelper(this)
        val db=dbHelper.writableDatabase
        val size=File(FILE_REC).length()
        val vals=ContentValues().apply {
            put(RecordContract.RecordEntry.COLUMN_NAME_NAME,FILE_REC)
            put(RecordContract.RecordEntry.COLUMN_NAME_SIZE,size.toString())
            put(RecordContract.RecordEntry.COLUMN_NAME_Date,currentDate)
            put(RecordContract.RecordEntry.COLUMN_NAME_R_LINK,REC_LINK)
        }
        val newRowId=db?.insert(RecordContract.RecordEntry.TABLE_NAME,null,vals)
    }

    fun playrec(){
        try {
            mediaPlayer.setDataSource(dir)
            mediaPlayer.prepare()
            mediaPlayer.start()
        }catch (e:IOException){
            e.printStackTrace()
        }
    }
}
