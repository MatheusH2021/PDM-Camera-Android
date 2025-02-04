package com.ifpe_4_ads.camif

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.IOException

class PhotoViewActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 1001
    }

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
        val buttonEdit: Button = findViewById(R.id.buttonEdit)
        buttonEdit.setOnClickListener {
            val editIntent = Intent(this, EditImageActivity::class.java)
            editIntent.putExtra("photoUri", photoUriString)
            startActivity(editIntent)
        }
    }

    private fun checkPermissions(): Boolean {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermissions() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_MEDIA_IMAGES), REQUEST_CODE_PERMISSIONS)
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_CODE_PERMISSIONS)
        }
    }

    private fun loadImage(imageView: ImageView, photoUriString: String?) {
        if (photoUriString != null) {
            try {
                val uri = Uri.parse(photoUriString)
                val inputStream = contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()

                if (bitmap != null) {
                    val rotatedBitmap = rotateImageIfRequired(bitmap, uri)
                    imageView.setImageBitmap(rotatedBitmap)
                } else {
                    Toast.makeText(this, "Erro ao carregar a imagem.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Erro ao acessar a imagem.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Uri da imagem não encontrada", Toast.LENGTH_SHORT).show()
        }
    }

    private fun rotateImageIfRequired(bitmap: Bitmap, uri: Uri): Bitmap {
        return try {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                val exif = ExifInterface(inputStream)
                val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

                val rotationAngle = when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> 90
                    ExifInterface.ORIENTATION_ROTATE_180 -> 180
                    ExifInterface.ORIENTATION_ROTATE_270 -> 270
                    ExifInterface.ORIENTATION_NORMAL -> 0
                    ExifInterface.ORIENTATION_UNDEFINED -> 0
                    else -> 0
                }

                if (rotationAngle != 0) {
                    val matrix = Matrix()
                    matrix.postRotate(rotationAngle.toFloat())
                    Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                } else {
                    bitmap
                }
            } ?: bitmap
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

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val imageView: ImageView = findViewById(R.id.imageViewPhoto)
                val photoUriString = intent.getStringExtra("photoUri")
                loadImage(imageView, photoUriString)
            } else {
                Toast.makeText(this, "Permissão negada para acessar a imagem", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
