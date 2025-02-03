package com.ifpe_4_ads.camif

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.ifpe_4_ads.camif.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonTakeImg.setOnClickListener(this)

        binding.buttonListImg.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.button_take_img -> {
                startActivity(Intent(this, CameraActivity::class.java))
            }
            R.id.button_list_img -> {
                startActivity(Intent(this, GalleryActivity::class.java))
            }
        }
    }

}