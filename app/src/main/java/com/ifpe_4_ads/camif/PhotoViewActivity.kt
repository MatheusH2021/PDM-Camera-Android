package com.ifpe_4_ads.camif

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.IOException

class PhotoViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_view)

        val imageView: ImageView = findViewById(R.id.imageViewPhoto)
        val photoUriString = intent.getStringExtra("photoUri")

        if (checkPermissions()) {
            loadImage(imageView, photoUriString)
        } else {
            requestPermissions()
        }
    }

    private fun checkPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            REQUEST_CODE_PERMISSIONS
        )
    }

    private fun loadImage(imageView: ImageView, photoUriString: String?) {
        if (photoUriString != null) {
            val bitmap = BitmapFactory.decodeFile(photoUriString)
            if (bitmap != null) {
                val rotatedBitmap = rotateImageIfRequired(bitmap, photoUriString)
                imageView.setImageBitmap(rotatedBitmap)
            } else {
                Toast.makeText(this, "Erro ao carregar a imagem.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Uri da imagem não encontrada", Toast.LENGTH_SHORT).show()
        }
    }

    private fun rotateImageIfRequired(bitmap: Bitmap, imagePath: String): Bitmap {
        return try {
            val exif = ExifInterface(imagePath)
            val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

            val rotationAngle = when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90
                ExifInterface.ORIENTATION_ROTATE_180 -> 180
                ExifInterface.ORIENTATION_ROTATE_270 -> 270
                else -> 0
            }

            if (rotationAngle != 0) {
                val matrix = Matrix()
                matrix.postRotate(rotationAngle.toFloat())
                Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            } else {
                bitmap
            }
        } catch (e: IOException) {
            e.printStackTrace()
            bitmap
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            val imageView: ImageView = findViewById(R.id.imageViewPhoto)
            val photoUriString = intent.getStringExtra("photoUri")
            loadImage(imageView, photoUriString)
        } else {
            Toast.makeText(this, "Permissão negada para acessar a imagem", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 1001
    }
}
