package com.example.davidwhyte.mintmic

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.BaseColumns
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.example.davidwhyte.mintmic.Model.Record



class RecordsActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_records)
        displayRecords()
    }

    fun displayRecords(){
        viewManager= LinearLayoutManager(this)
        val recordList=getDB()
        viewAdapter=RecordAdapter(recordList,this)
        recyclerView=findViewById<RecyclerView>(R.id.record_rv).apply {
            layoutManager=viewManager
            adapter=viewAdapter
        }
    }

    fun getDB():ArrayList<Record>{
        var records=ArrayList<Record>()
        val dbHelper=RecordContract.RecordEntry.RecordDbHelper(this)
        val db=dbHelper.readableDatabase
        val projection= arrayOf(BaseColumns._ID,
                RecordContract.RecordEntry.COLUMN_NAME_NAME,
                RecordContract.RecordEntry.COLUMN_NAME_SIZE,
                RecordContract.RecordEntry.COLUMN_NAME_Date,
                RecordContract.RecordEntry.COLUMN_NAME_R_LINK
        )
//        val selection="${RecordContract.RecordEntry.TABLE_NAME}"
        val cursor=db.query(RecordContract.RecordEntry.TABLE_NAME,projection,null,null,null,null,null)
        with(cursor){
            while (moveToNext()){
                val name=getString(getColumnIndex(RecordContract.RecordEntry.COLUMN_NAME_NAME))
                val date=getString(getColumnIndex(RecordContract.RecordEntry.COLUMN_NAME_Date))
                val size=getString(getColumnIndex(RecordContract.RecordEntry.COLUMN_NAME_SIZE))
                val r_link=getString(getColumnIndex(RecordContract.RecordEntry.COLUMN_NAME_R_LINK))
                val id=getInt(getColumnIndex(BaseColumns._ID))
                val record= Record()
                record.id=id.toString()
                record.name=name
                record.date=date
                record.size=size
                record.r_link=r_link
                records.add(record)
                Log.v("note id",record.name.toString())

            }
        }
        return records
    }



}
