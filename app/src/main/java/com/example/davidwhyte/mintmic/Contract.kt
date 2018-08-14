package com.example.davidwhyte.mintmic

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

object RecordContract {
    // Table contents are grouped together in an anonymous object.
    object RecordEntry : BaseColumns {
        const val TABLE_NAME = "records"
        const val COLUMN_NAME_NAME = "name"
        const val COLUMN_NAME_Date = "date"
        const val COLUMN_NAME_SIZE="duration"
        const val COLUMN_NAME_R_LINK="r_link"
        private const val SQL_CREATE_ENTRIES =
                "CREATE TABLE ${RecordContract.RecordEntry.TABLE_NAME} (" +
                        "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                        "${RecordContract.RecordEntry.COLUMN_NAME_NAME} TEXT," +
                        "${RecordContract.RecordEntry.COLUMN_NAME_Date} TEXT," +
                        "${RecordContract.RecordEntry.COLUMN_NAME_R_LINK} TEXT," +
                        "${RecordContract.RecordEntry.COLUMN_NAME_SIZE} TEXT)"

        private const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${RecordContract.RecordEntry.TABLE_NAME}"

        class RecordDbHelper(context:Context):SQLiteOpenHelper(context, Database_Name,null,1){
            override fun onCreate(p0: SQLiteDatabase?) {
                p0?.execSQL(SQL_CREATE_ENTRIES)
            }

            override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {

            }

            companion object {
                const val Database_Name="mintmic"
                const val Database_Version=1
            }
        }
    }

}
