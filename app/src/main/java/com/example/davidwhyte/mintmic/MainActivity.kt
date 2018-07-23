package com.example.davidwhyte.mintmic

import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.media.MediaFormat
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.*
import android.provider.BaseColumns
import android.support.v7.app.AppCompatActivity
import android.support.v4.app.ActivityCompat
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
import kotlin.concurrent.timer

class MainActivity : AppCompatActivity() {
    var state=0
    private val RECORD_REQUEST_CODE = 101
    private val WRITE_REQUEST_CODE=2930
    val mediaRecorder=MediaRecorder()
    val mediaPlayer=MediaPlayer()
    var FILE_REC=""
    var tmSum:Long=0
    var timer:CountDownTimer?=null
    val dir=Environment.getExternalStorageDirectory().absolutePath+"/MintMic"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val record=findViewById<ImageButton>(R.id.record)
        getData()
        getDB()
        timer=timer()
        if(!File(dir).exists()){
            File(dir).mkdir()
        }
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
    fun record(){

        //reset the record button
        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),WRITE_REQUEST_CODE)
        }
        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.RECORD_AUDIO)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.RECORD_AUDIO),RECORD_REQUEST_CODE)
        }

        else{
            var n=File(dir).walkTopDown().count()
            var mFileName=dir+"/record"+n+".aac"
            FILE_REC=mFileName
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT)
            mediaRecorder.setOutputFile(mFileName);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
            mediaRecorder.setAudioSamplingRate(16000)
            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
                timer?.start()
            } catch (e:IOException) {
                Log.e("LOG_TAG", "prepare() failed");
            }
        }
    }
    fun stoprec(){
        try {
            mediaRecorder.stop()
            timer?.cancel()
            mediaRecorder.release()
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
        val vals=ContentValues().apply {
            put(RecordContract.RecordEntry.COLUMN_NAME_NAME,FILE_REC)
            put(RecordContract.RecordEntry.COLUMN_NAME_DURATION,tmSum)
            put(RecordContract.RecordEntry.COLUMN_NAME_Date,currentDate)
        }
        val newRowId=db?.insert(RecordContract.RecordEntry.TABLE_NAME,null,vals)
    }

    fun getDB(){
        val dbHelper=RecordContract.RecordEntry.RecordDbHelper(this)
        val db=dbHelper.readableDatabase
        val projection= arrayOf(BaseColumns._ID,
                RecordContract.RecordEntry.COLUMN_NAME_NAME,
                RecordContract.RecordEntry.COLUMN_NAME_DURATION,
                RecordContract.RecordEntry.COLUMN_NAME_Date
        )
//        val selection="${RecordContract.RecordEntry.TABLE_NAME}"
        val cursor=db.query(RecordContract.RecordEntry.TABLE_NAME,projection,null,null,null,null,null)
        with(cursor){
            while (moveToNext()){
                val name=getString(getColumnIndex(RecordContract.RecordEntry.COLUMN_NAME_NAME))
                val date=getString(getColumnIndex(RecordContract.RecordEntry.COLUMN_NAME_Date))
                val duration=getString(getColumnIndex(RecordContract.RecordEntry.COLUMN_NAME_DURATION))
                val id=getInt(getColumnIndex(BaseColumns._ID))
                val record=Record()
                record.id=id.toString()
                record.name=name
                record.date=date
                record.duration=duration
                Log.v("note id",record.name.toString())

            }
        }

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
