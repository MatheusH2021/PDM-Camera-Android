package com.ifpe_4_ads.camif

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ifpe_4_ads.camif.databinding.ActivityEditImageBinding
import java.io.InputStream

class EditImageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditImageBinding
    private var currentBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val photoUriString = intent.getStringExtra("photoUri")
        if (photoUriString != null) {
            loadImage(Uri.parse(photoUriString))
        } else {
            Toast.makeText(this, "Imagem não encontrada", Toast.LENGTH_SHORT).show()
        }

        binding.buttonGrayScale.setOnClickListener {
            currentBitmap?.let {
                currentBitmap = applyGrayScale(it)
                binding.imageViewEdit.setImageBitmap(currentBitmap)
            }
        }

        binding.buttonRotateRight.setOnClickListener {
            currentBitmap?.let {
                currentBitmap = rotateBitmap(it, 90f)
                binding.imageViewEdit.setImageBitmap(currentBitmap)
            }
        }

        binding.buttonRotateLeft.setOnClickListener {
            currentBitmap?.let {
                currentBitmap = rotateBitmap(it, -90f)
                binding.imageViewEdit.setImageBitmap(currentBitmap)
            }
        }

        binding.buttonInvertColors.setOnClickListener {
            currentBitmap?.let {
                currentBitmap = applyInvertColors(it)
                binding.imageViewEdit.setImageBitmap(currentBitmap)
            }
        }

        binding.buttonSaveImage.setOnClickListener {
            currentBitmap?.let {
                if (photoUriString != null) {
                    saveImage(it, Uri.parse(photoUriString))
                }
            }
        }
    }

    private fun loadImage(uri: Uri) {
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()
        if (bitmap != null) {
            currentBitmap = bitmap
            binding.imageViewEdit.setImageBitmap(currentBitmap)
        }
    }

    private fun applyGrayScale(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val grayBitmap = Bitmap.createBitmap(width, height, bitmap.config ?: Bitmap.Config.ARGB_8888)

        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = bitmap.getPixel(x, y)
                val avg = (Color.red(pixel) + Color.green(pixel) + Color.blue(pixel)) / 3
                grayBitmap.setPixel(x, y, Color.rgb(avg, avg, avg))
            }
        }
        return grayBitmap
    }

    private fun applyInvertColors(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val invertedBitmap = Bitmap.createBitmap(width, height, bitmap.config ?: Bitmap.Config.ARGB_8888)

        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = bitmap.getPixel(x, y)
                val red = 255 - Color.red(pixel)
                val green = 255 - Color.green(pixel)
                val blue = 255 - Color.blue(pixel)
                invertedBitmap.setPixel(x, y, Color.rgb(red, green, blue))
            }
        }
        return invertedBitmap
    }

    private fun rotateBitmap(bitmap: Bitmap, angle: Float): Bitmap {
        val matrix = android.graphics.Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun saveImage(bitmap: Bitmap, uri: Uri) {
        val outputStream = contentResolver.openOutputStream(uri)
        if (outputStream != null) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            Toast.makeText(this, "Imagem Salva.", Toast.LENGTH_LONG).show()
            val intent = Intent(this, GalleryActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "Erro ao acessar o arquivo para salvar.", Toast.LENGTH_SHORT).show()
        }
    }
}
