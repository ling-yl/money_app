package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_set_goods_money.*

class user_set_redcash : AppCompatActivity() {

    var uid=FirebaseAuth.getInstance().currentUser?.uid
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_set_redcash)

        submit.setOnClickListener {

            val pay_cash=pay_cash.text.toString()


            if(pay_cash.isEmpty() )
            {
                Toast.makeText(this,"有格子未填寫", Toast.LENGTH_SHORT).show()

            }
            else{
                val uid = FirebaseAuth.getInstance().uid?:""
                val ref = FirebaseDatabase.getInstance().getReference("/user/$uid")
                val user = User(pay_cash.toInt()).toMap()

                ref.updateChildren(user).addOnSuccessListener {
                    Log.d("setmoney","real time database access success")
                }
                val intent = Intent(this,main_page::class.java)
                intent.flags= Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
        }

    }
}
