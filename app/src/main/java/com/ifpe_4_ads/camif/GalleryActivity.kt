package com.ifpe_4_ads.camif

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.ifpe_4_ads.camif.adapters.ImagesCustomAdapter
import com.ifpe_4_ads.camif.databinding.ActivityGalleryBinding
import java.io.File

class GalleryActivity : AppCompatActivity() {

    private lateinit var photoFiles: List<File>
    private lateinit var binding: ActivityGalleryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGalleryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadPhotos()
    }

    private fun loadPhotos() {
        val directory = File(Constants.FILE_PATH)

        if (directory.exists() && directory.isDirectory) {
            photoFiles = directory.listFiles()?.filter { it.extension == "jpg" } ?: emptyList()
            val photoNames = photoFiles.map { it.name }

            val recyclerView = binding.imagesRecyclerView
            val gridLayoutManager = GridLayoutManager(this, 2)

            val imagesAdapter = ImagesCustomAdapter(photoNames) { photoName ->

                val photoFile = photoFiles.find { it.name == photoName }
                if (photoFile != null) {
                    val intent = Intent(this, PhotoViewActivity::class.java).apply {
                        putExtra("photoUri", Uri.fromFile(photoFile).toString())
                    }
                    startActivity(intent)
                }

            }

            recyclerView.adapter = imagesAdapter
            recyclerView.layoutManager = gridLayoutManager
//
//
//            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, photoNames)
//            listView.adapter = adapter
//
//            listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
//                val intent = Intent(this, PhotoViewActivity::class.java).apply {
//                    putExtra("photoUri", Uri.fromFile(photoFiles[position]).toString())
//                }
//                startActivity(intent)
//            }

        } else {
            Log.e("GalleryActivity", "Diretório nao encontrado ou não é um diretrio válido")
        }
    }


}
