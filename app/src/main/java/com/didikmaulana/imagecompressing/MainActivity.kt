package com.didikmaulana.imagecompressing

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private var base64Img: String = ""

    companion object {
        const val GALLERY = 1
        const val CAMERA = 2
        const val PERMISSION_ALLOW = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // button picker click
        btn_picker.setOnClickListener {
            showPictureDialog()

            // check permission android
            permissionGranted()
        }
    }

    private fun permissionGranted() {
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
        } else {
            permissionAllowed()
        }
    }

    private fun permissionAllowed() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.INTERNET) &&
            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE) &&
            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, arrayOf(
                    Manifest.permission.INTERNET,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                ), PERMISSION_ALLOW)
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(
                    Manifest.permission.INTERNET,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                ), PERMISSION_ALLOW)
        }
    }

    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(this)
        pictureDialog.setTitle("Choose Image")
        val pictureDialogItems = arrayOf("From Gallery", "From Camera")
        pictureDialog.setItems(pictureDialogItems)
        { _, which ->
            when (which) {
                0 -> choosePhotoFromGallery()
                1 -> takePhotoFromCamera()
            }
        }
        pictureDialog.show()
    }

    // take from gallery
    private fun choosePhotoFromGallery() {
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(galleryIntent, GALLERY)
    }

    // take from camera
    private fun takePhotoFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_ALLOW) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                onBackPressed()
            }
        }
    }

    // activity result
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GALLERY) {
            if (data != null) {
                val contentURI = data.data
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                    img.setImageBitmap(bitmap)

                    val width   = bitmap.width
                    val height  = bitmap.height
                    if(width>height) {
                        txt_orientation.text = getString(R.string.landscape)
                    } else {
                        txt_orientation.text = getString(R.string.potrait)
                    }
                    // resized bitmap accoding orientation
                    // val resizedBitmap = bitmap.getOrientation(bitmap, width, height)

                    // convert bitmap to base64 encryption
                    // val base64String = resizedBitmap.toBase64()
                    // base64Img = base64String

                    // show file path
                    val fout = bitmap.savePath()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fout)
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Load Failed !", Toast.LENGTH_SHORT).show()
                }
            }
        } else if(requestCode== CAMERA) {
            if (data != null) {
                val thumbnail = data.extras?.get("data") as Bitmap
                img.setImageBitmap(thumbnail)

                val width   = thumbnail.width
                val height  = thumbnail.height
                if(width>height) {
                    txt_orientation.text = getString(R.string.landscape)
                } else {
                    txt_orientation.text = getString(R.string.potrait)
                }
                // resized bitmap according orientation
                // val resizedBitmap = thumbnail.getOrientation(thumbnail, width, height)

                // convert bitmap to base64 encryption
                // val base64String = resizedBitmap.toBase64()
                // base64Img = base64String

                // show file path
                val fout = thumbnail.savePath()
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, fout)
            }
        }
    }
}