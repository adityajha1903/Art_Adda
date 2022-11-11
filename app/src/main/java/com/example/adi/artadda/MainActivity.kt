package com.example.adi.artadda

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.media.MediaScannerConnection
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {

    private var drawingView: DrawingView? = null
    private var customProgressDialog: Dialog? = null
    private val openGalleryLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                val imgBackground: ImageView = findViewById(R.id.iv_background)
                imgBackground.setImageURI(result.data?.data)
            }
        }

    private val saveImgLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                saveYourImg(false)
            } else {
                Toast.makeText(this, "Sorry, you just denied the permission", Toast.LENGTH_SHORT).show()

            }
        }

    private val imgPickerResultLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openGalleryPickImg()
            } else {
                Toast.makeText(this, "Sorry, you just denied the permission", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnChooseBrushSize = findViewById<ImageButton>(R.id.btnChooseBrush)
        val btnChooseBrushColor = findViewById<ImageButton>(R.id.btnChooseColor)
        val btnChooseBgImg = findViewById<ImageButton>(R.id.btnChooseBackgroundImage)
        val btnUndo = findViewById<ImageButton>(R.id.btnUndo)
        val btnRedo = findViewById<ImageButton>(R.id.btnRedo)
        val btnSave = findViewById<ImageButton>(R.id.btnSaveImg)
        val btnShare = findViewById<ImageButton>(R.id.btnShareImg)
        drawingView = findViewById(R.id.drawingView)

        drawingView?.setBrushThickness(5.0f)

        btnChooseBrushColor.setOnClickListener {
            showBrushColorChooserDialog()
        }

        btnChooseBrushSize.setOnClickListener {
            showBrushSizeChooserDialog()
        }

        btnChooseBgImg.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showRationaleDialog("Art adda requires the permission to read the external storage in your device")
                } else {
                    if (isReadStorageAllowed()) {
                        openGalleryPickImg()
                    } else {
                        imgPickerResultLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                }
            }
        }

        btnUndo.setOnClickListener {
            drawingView?.onClickUndo()
        }

        btnRedo.setOnClickListener {
            drawingView?.onClickRedo()
        }

        btnSave.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    showRationaleDialog("Art adda requires the permission to Write on the external storage in your device")
                } else {
                    if (isWriteStorageAllowed()) {
                        saveYourImg(false)
                    } else {
                        saveImgLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    }
                }
            }
        }

        btnShare.setOnClickListener {
            saveYourImg(true)
        }
    }

    private fun saveYourImg(wantToShare: Boolean) {
        lifecycleScope.launch {
            val fLDrawingView = findViewById<FrameLayout>(R.id.fl_drawing)
            saveBitmapFile(getBitmapFromView(fLDrawingView), wantToShare)
        }
    }

    private fun showRationaleDialog(message: String) {
        val builder: AlertDialog.Builder =  AlertDialog.Builder(this)
        builder.setTitle("Permission required")
        builder.setMessage(message)
        builder.setIcon(R.drawable.ic_info)
        builder.setPositiveButton("Cancel") { dialogInterface, _ ->
            dialogInterface.dismiss()
        }

        builder.create().show()
    }

    private fun showBrushColorChooserDialog() {
        val colorDialog = Dialog(this)
        colorDialog.setContentView(R.layout.dialog_color_selector)

        val lLBlackBtn = colorDialog.findViewById<LinearLayout>(R.id.chooseBlack)
        val lLBrownBtn = colorDialog.findViewById<LinearLayout>(R.id.chooseBrown)
        val lLRedBtn = colorDialog.findViewById<LinearLayout>(R.id.chooseRed)
        val lLOrangeBtn = colorDialog.findViewById<LinearLayout>(R.id.chooseOrange)
        val lLYellowBtn = colorDialog.findViewById<LinearLayout>(R.id.chooseYellow)
        val lLLightGreenBtn = colorDialog.findViewById<LinearLayout>(R.id.chooseLightGreen)
        val lLDarkGreenBtn = colorDialog.findViewById<LinearLayout>(R.id.chooseDarkGreen)
        val lLSkyBlueBtn = colorDialog.findViewById<LinearLayout>(R.id.chooseSkyBlue)
        val lLBlueBtn = colorDialog.findViewById<LinearLayout>(R.id.chooseBlue)
        val lLDarkBlueBtn = colorDialog.findViewById<LinearLayout>(R.id.chooseDarkBlue)
        val lLPurpleBtn = colorDialog.findViewById<LinearLayout>(R.id.choosePurple)
        val lLDarkPinkBtn = colorDialog.findViewById<LinearLayout>(R.id.chooseDarkPink)
        val lLPinkBtn = colorDialog.findViewById<LinearLayout>(R.id.choosePink)
        val lLGreyBtn = colorDialog.findViewById<LinearLayout>(R.id.chooseGrey)
        val lLWhiteBtn = colorDialog.findViewById<LinearLayout>(R.id.chooseWhite)

        lLBlackBtn.setOnClickListener {
            setColor(lLBlackBtn, colorDialog)
        }
        lLBrownBtn.setOnClickListener {
            setColor(lLBrownBtn, colorDialog)
        }
        lLRedBtn.setOnClickListener {
            setColor(lLRedBtn, colorDialog)
        }
        lLOrangeBtn.setOnClickListener {
            setColor(lLOrangeBtn, colorDialog)
        }
        lLYellowBtn.setOnClickListener {
            setColor(lLYellowBtn, colorDialog)
        }
        lLLightGreenBtn.setOnClickListener {
            setColor(lLLightGreenBtn, colorDialog)
        }
        lLDarkGreenBtn.setOnClickListener {
            setColor(lLDarkGreenBtn, colorDialog)
        }
        lLSkyBlueBtn.setOnClickListener {
            setColor(lLSkyBlueBtn, colorDialog)
        }
        lLBlueBtn.setOnClickListener {
            setColor(lLBlueBtn, colorDialog)
        }
        lLDarkBlueBtn.setOnClickListener {
            setColor(lLDarkBlueBtn, colorDialog)
        }
        lLPurpleBtn.setOnClickListener {
            setColor(lLPurpleBtn, colorDialog)
        }
        lLDarkPinkBtn.setOnClickListener {
            setColor(lLDarkPinkBtn, colorDialog)
        }
        lLPinkBtn.setOnClickListener {
            setColor(lLPinkBtn, colorDialog)
        }
        lLGreyBtn.setOnClickListener {
            setColor(lLGreyBtn, colorDialog)
        }
        lLWhiteBtn.setOnClickListener {
            setColor(lLWhiteBtn, colorDialog)
        }

        colorDialog.show()
    }

    private fun setColor(color: LinearLayout, dialog: Dialog) {
        drawingView?.setColor(color.tag.toString())
        dialog.dismiss()
    }

    private fun openGalleryPickImg() {
        val openGalleryAndGetImg = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        openGalleryLauncher.launch(openGalleryAndGetImg)
    }

    private fun showBrushSizeChooserDialog() {
        val brushDialog = Dialog(this)
        brushDialog.setContentView(R.layout.dialog_brush_size)
        brushDialog.setTitle("Brush size: ")
        val btnSmallBrush = brushDialog.findViewById<ImageButton>(R.id.smallBrush)
        btnSmallBrush.setOnClickListener {
            drawingView?.setBrushThickness(5.0f)
            brushDialog.dismiss()
        }
        val btnMediumBrush = brushDialog.findViewById<ImageButton>(R.id.mediumBrush)
        btnMediumBrush.setOnClickListener{
            drawingView?.setBrushThickness(10.0f)
            brushDialog.dismiss()
        }
        val btnLargeBrush = brushDialog.findViewById<ImageButton>(R.id.largeBrush)
        btnLargeBrush.setOnClickListener {
            drawingView?.setBrushThickness(20.0f)
            brushDialog.dismiss()
        }
        brushDialog.show()
    }

    private fun getBitmapFromView(view: View): Bitmap {
        val retBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(retBitmap)
        val imgBackground = view.background
        if (imgBackground != null) {
            imgBackground.draw(canvas)
        } else {
            canvas.drawColor(Color.WHITE)
        }
        view.draw(canvas)
        return retBitmap
    }

    private suspend fun saveBitmapFile(bitmap: Bitmap?, wantToShare: Boolean): String {
        showProgressDialog()
        var result = ""
        withContext(Dispatchers.IO) {
            if (bitmap != null) {
                try {
                    val bytes = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, bytes)

                    val dir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera")
                    dir.mkdir()
                    val file = File(dir , "${System.currentTimeMillis() / 1000}" + ".png")
                    val fo = FileOutputStream(file)
                    fo.write(bytes.toByteArray())
                    fo.flush()
                    fo.close()
                    result = file.absolutePath

                    runOnUiThread {
                        if (result.isNotEmpty()) {
                            Toast.makeText(this@MainActivity, "File saved successfully: $result", Toast.LENGTH_LONG).show()
                            customProgressDialog?.dismiss()
                            if (wantToShare) {
                                shareYourImg(result)
                            }
                        }
                    }
                } catch (e: Exception) {
                    result = ""
                    e.printStackTrace()
                }
            }
        }
        return result
    }

    private fun isWriteStorageAllowed(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    private fun isReadStorageAllowed(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    private fun showProgressDialog() {
        customProgressDialog = Dialog(this@MainActivity)
        customProgressDialog?.setContentView(R.layout.dialog_progress)
        customProgressDialog?.setCancelable(false)
        customProgressDialog?.show()
    }

    private fun shareYourImg(result: String) {
        MediaScannerConnection.scanFile(this, arrayOf(result), null) {
            _, uri ->
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
            shareIntent.type = "image/png"
            startActivity(Intent.createChooser(shareIntent, "Share"))
        }
    }
}