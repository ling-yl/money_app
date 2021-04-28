package com.example.myapplication

import android.graphics.Bitmap
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.android.synthetic.main.activity_show__qr_code.*

class show_QR_code : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show__qr_code)
        val is_user = intent.getStringExtra("is_user")
        val uid = intent.getStringExtra("uid")
        val user_name = intent.getStringExtra("user_name")
        leave.setOnClickListener {
            finish()
        }
        val qrcode = QRCodeWriter()
        val code = qrcode.encode("${is_user};${user_name};${uid}",BarcodeFormat.QR_CODE,250,250)
        val width =  code.width
        val height =  code.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if ( code.get(x, y)) Color.BLACK else Color.WHITE)
            }
        }
        show_QR_code.setImageBitmap(bitmap)

    }


}