package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_first_choose.*

class first_choose : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_choose)


        choose_user.setOnClickListener {
            val intent = Intent(this,register::class.java)
            startActivity(intent)
        }
        choose_shop.setOnClickListener {
            val intent = Intent(this,register::class.java)
            intent.putExtra("shop","shop")
            startActivity(intent)
        }
    }
}
