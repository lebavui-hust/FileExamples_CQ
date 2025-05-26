package com.example.fileexamples

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.CalendarContract.Colors
import android.provider.Settings
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.fileexamples.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity() {

    val SETTINGS = "settings"
    val BACKGROUND_COLOR = "background_color"

    lateinit var binding: ActivityMainBinding
    var color: Int = Color.WHITE

    lateinit var db: SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonRed.setOnClickListener {
            color = Color.RED
            binding.main.setBackgroundColor(color)
        }

        binding.buttonGreen.setOnClickListener {
            color = Color.GREEN
            binding.main.setBackgroundColor(color)
        }
        binding.buttonBlue.setOnClickListener {
            color = Color.BLUE
            binding.main.setBackgroundColor(color)
        }

        val prefs = getSharedPreferences(SETTINGS, MODE_PRIVATE)
        color = prefs.getInt(BACKGROUND_COLOR, Color.WHITE)
        binding.main.setBackgroundColor(color)

        binding.buttonReadRaw.setOnClickListener {
            val inputStream = resources.openRawResource(R.raw.test)
            val reader = inputStream.reader()
            val content = reader.readText()
            reader.close()

            Log.v("TAG", content)
        }

        binding.buttonReadInternal.setOnClickListener {
            val reader = openFileInput("test2.txt").reader()
            val content = reader.readText()
            reader.close()
            binding.editText.setText(content)
        }

        binding.buttonWriteInternal.setOnClickListener {
            val writer = openFileOutput("test2.txt", MODE_PRIVATE).writer()
            writer.write(binding.editText.text.toString())
            writer.close()
        }

        binding.buttonRemoveInternal.setOnClickListener {
            val file = File("$filesDir/test2.txt")
            if (file.exists())
                file.delete()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                startActivity(intent)
            }
        }

        binding.buttonWriteExternal.setOnClickListener {
            val path = Environment.getExternalStorageDirectory().path
            val file = File("$path/test3.txt")
            val writer = file.outputStream().writer()
            writer.write(binding.editText.text.toString())
            writer.close()
        }

        binding.buttonReadExternal.setOnClickListener {
            val path = Environment.getExternalStorageDirectory().path
            val file = File("$path/test3.txt")
            val reader = file.inputStream().reader()
            val content = reader.readText()
            reader.close()
            binding.editText.setText(content)
        }

        val rootFile = Environment.getExternalStorageDirectory()
        for (child in rootFile.listFiles()) {
            Log.v("TAG", "${child.name} File: ${child.isFile} Dir: ${child.isDirectory}")
        }


        db = SQLiteDatabase.openDatabase("${filesDir.path}/test_db", null, SQLiteDatabase.CREATE_IF_NECESSARY)
        // createTable()

        // val cs = db.rawQuery("select * from tblAmigo", null)

        val cs = db.query("tblAmigo", arrayOf("recID", "name", "phone"),
            null, null, null, null, null)

        Log.v("TAG", "Num records: ${cs.count}")
        cs.moveToFirst()
        do {
            val recID = cs.getInt(0)
            val name = cs.getString(1)
            val phone = cs.getString(2)
            Log.v("TAG", "$recID - $name - $phone")
        } while (cs.moveToNext())
        cs.close()
    }

    fun createTable() {
        db.beginTransaction()
        try {
            db.execSQL("create table tblAmigo(" +
                    "recID integer primary key autoincrement," +
                    "name text," +
                    "phone text)")
            db.execSQL("insert into tblAmigo(name, phone) values ('AAA', '555-1111')")
            db.execSQL("insert into tblAmigo(name, phone) values ('BBB', '555-2222')")
            db.execSQL("insert into tblAmigo(name, phone) values ('CCC', '555-3333')")
            db.setTransactionSuccessful()
        } catch (ex: Exception) {
            ex.printStackTrace()
        } finally {
            db.endTransaction()
        }
    }

    override fun onStop() {
        super.onStop()

        db.close()

        val prefs = getSharedPreferences(SETTINGS, MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putInt(BACKGROUND_COLOR, color)
        editor.apply()
    }
}