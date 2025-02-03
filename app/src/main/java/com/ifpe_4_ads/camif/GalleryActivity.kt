package com.ifpe_4_ads.camif

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class GalleryActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var photoFiles: List<File>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        listView = findViewById(R.id.listViewPhotos)
        loadPhotos()
    }

    private fun loadPhotos() {
        val directory = File("/storage/emulated/0/Android/media/com.ifpe_4_ads.camif/CamIF/")

        if (directory.exists() && directory.isDirectory) {
            photoFiles = directory.listFiles()?.filter { it.extension == "jpg" } ?: emptyList()

            val photoNames = photoFiles.map { it.name }
            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, photoNames)
            listView.adapter = adapter

            listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                val intent = Intent(this, PhotoViewActivity::class.java).apply {
                    putExtra("photoUri", photoFiles[position].absolutePath)
                }
                startActivity(intent)
            }
        } else {
            Log.e("GalleryActivity", "Diretório nao encontrado ou não é um diretrio válido")
        }
    }
}
