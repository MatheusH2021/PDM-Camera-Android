package com.ifpe_4_ads.camif

import android.Manifest

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ifpe_4_ads.camif.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var preview: Preview

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions()
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            bindPreview()
        }, ContextCompat.getMainExecutor(this))
    }

    private fun bindPreview() {
        preview = Preview.Builder().build()
        preview.setSurfaceProvider(binding.previewView.surfaceProvider) // Usando o PreviewView

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        cameraProvider.bindToLifecycle(this, cameraSelector, preview)
    }

    private fun allPermissionsGranted(): Boolean {
        return REQUIRED_PERMISSIONS.all { permission: String ->
            ContextCompat.checkSelfPermission(baseContext, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}