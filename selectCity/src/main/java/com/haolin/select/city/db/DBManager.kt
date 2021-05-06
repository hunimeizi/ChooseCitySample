package com.haolin.select.city.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.os.Environment
import com.haolin.select.city.bean.City
import java.io.*
import java.util.*

class DBManager(private val mContext: Context) {
    private val DB_PATH: String = (File.separator + "data"
            + Environment.getDataDirectory().absolutePath + File.separator
            + mContext.packageName + File.separator + "databases" + File.separator)

    suspend fun copyDBFile() {
        val dir = File(DB_PATH)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val dbFile = File(DB_PATH + DB_NAME)
        if (!dbFile.exists()) {
            val `is`: InputStream
            val os: OutputStream
            try {
                `is` = mContext.resources.assets.open(ASSETS_NAME)
                os = FileOutputStream(dbFile)
                val buffer = ByteArray(BUFFER_SIZE)
                var length: Int
                while (`is`.read(buffer, 0, buffer.size).also { length = it } > 0) {
                    os.write(buffer, 0, length)
                }
                os.flush()
                os.close()
                `is`.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 读取所有城市
     * @return
     */
    suspend fun getAllCities() : List<City>{
        val db = SQLiteDatabase.openOrCreateDatabase(DB_PATH + DB_NAME, null)
        val cursor = db.rawQuery("select * from $TABLE_NAME", null)
        val result: MutableList<City> = ArrayList()
        var city: City
        while (cursor.moveToNext()) {
            val name = cursor.getString(cursor.getColumnIndex(NAME))
            val pinyin = cursor.getString(cursor.getColumnIndex(PINYIN))
            val cityId = cursor.getString(cursor.getColumnIndex(CITYID))
            city = City(name, pinyin, cityId)
            result.add(city)
        }
        cursor.close()
        db.close()
        Collections.sort(result, CityComparator())
        return result
    }
    /**
     * 通过名字或者拼音搜索
     * @param keyword
     * @return
     */
   suspend fun searchCity(keyword: String): List<City> {
        val db = SQLiteDatabase.openOrCreateDatabase(DB_PATH + DB_NAME, null)
        val cursor = db.rawQuery(
            "select * from " + TABLE_NAME + " where name like \"%" + keyword
                    + "%\" or namePinyin like \"%" + keyword + "%\"", null
        )
        val result: MutableList<City> = ArrayList()
        var city: City
        while (cursor.moveToNext()) {
            val name = cursor.getString(cursor.getColumnIndex(NAME))
            val pinyin = cursor.getString(cursor.getColumnIndex(PINYIN))
            val cityId = cursor.getString(cursor.getColumnIndex(CITYID))
            city = City(name, pinyin, cityId)
            result.add(city)
        }
        cursor.close()
        db.close()
        Collections.sort(result, CityComparator())
        return result
    }

    /**
     * a-z排序
     */
    private inner class CityComparator : Comparator<City> {
        override fun compare(lhs: City, rhs: City): Int {
            val a = lhs.namePinyin.substring(0, 1)
            val b = rhs.namePinyin.substring(0, 1)
            return a.compareTo(b)
        }
    }

    companion object {
        private const val ASSETS_NAME = "citysearch_db"
        private const val DB_NAME = "citysearch_db"
        private const val TABLE_NAME = "cityentity"
        private const val NAME = "name"
        private const val PINYIN = "namePinyin"
        private const val CITYID = "baiduCode"
        private const val BUFFER_SIZE = 2048
    }

}