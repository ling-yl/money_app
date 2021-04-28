package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.zxing.integration.android.IntentIntegrator
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main_page.*
import kotlinx.android.synthetic.main.activity_main_page.user_redcash
import kotlinx.android.synthetic.main.activity_redcash.*
import java.text.SimpleDateFormat
import java.util.*

class redcash : AppCompatActivity() {

    var time = Calendar.getInstance()
    var format = SimpleDateFormat ("yyyy-MM-dd hh:mm:ss")
    var now_time =  format.format(time.time)
    var give_cash=0
    var uid= FirebaseAuth.getInstance().currentUser?.uid
    var have_cash=0
    var current_user_name=""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_redcash)

        val ref = FirebaseDatabase.getInstance().getReference("/user/$uid")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val snap = dataSnapshot.getValue(User::class.java)

                //收紅包(從廠商/從會員)
                user_redcash.setOnClickListener{

                    val intent = Intent(this@redcash,show_QR_code::class.java)
                    intent.putExtra("uid",snap?.uid)
                    intent.putExtra("is_user", snap?.isuser)
                    intent.putExtra("user_name", snap?.username)
                    startActivity(intent)
                }

                //送紅包
                user_send_redcash.setOnClickListener{
                    open_scanner()
                }


            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result.contents == null || result== null ) {return}
        val data=result.contents.split(";")

        val alert = AlertDialog.Builder(this@redcash)


        val ref = FirebaseDatabase.getInstance().getReference("/user/$uid")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val snap1 = dataSnapshot.getValue(User::class.java)

                if (data[0]=="true")//QRCODE的第一格欄位是使用者 代表要送紅包
                {
                    //val alert = AlertDialog.Builder(this@redcash)

                    if(snap1!!.cash.toInt()<snap1!!.give_cash.toInt()){
                        alert.setMessage("您的錢不夠哦")
                        alert.setPositiveButton("確定") { dialog, which ->
                            Toast.makeText(applicationContext, "送紅包失敗", Toast.LENGTH_SHORT).show()
                        }
                        alert.show()
                    }
                    else {
                        val ref = FirebaseDatabase.getInstance().getReference("/user/${data[2]}")

                        ref.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                val snap = dataSnapshot.getValue(User::class.java)

                                alert.setMessage("是否要送給「${snap?.username}」${snap?.give_cash}元的紅包?")
                                alert.setPositiveButton("YES") { dialog, which ->
                                    val key =  FirebaseDatabase.getInstance().getReference("/record").push().key
                                    val cash =snap!!.cash.toInt()
                                    FirebaseDatabase.getInstance().getReference("/user/${uid}/cash").setValue(snap1?.cash.toInt()-snap?.give_cash.toInt())   //送的人
                                    FirebaseDatabase.getInstance().getReference("/user/${data[2]}/cash").setValue(snap!!.cash.toInt()+snap!!.give_cash.toInt())
                                    val record=record(data[2],data[1],uid,snap1?.username,"cash",snap?.give_cash.toInt(),now_time)
                                    FirebaseDatabase.getInstance().getReference("/record/${key}").setValue(record)
                                    FirebaseDatabase.getInstance().getReference("/user/${uid}/record/${key}").setValue("pay")
                                    FirebaseDatabase.getInstance().getReference("/user/${data[2]}/record/${key}").setValue("get")


                                    Toast.makeText(applicationContext, "交易完成", Toast.LENGTH_SHORT).show()

                                    val intent = Intent(this@redcash,main_page::class.java)
                                    startActivity(intent)
                                }
                                alert.show()
                            }


                            override fun onCancelled(databaseError: DatabaseError) {}
                        })


                    }

                }
                else{

                }



            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })

        //會員送給另一個會員紅包

      /*  else//QRCODE的第一格欄位是商家 代表要購買物品
        {
            val ref = FirebaseDatabase.getInstance().getReference("/user/${data[2]}")
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val snap = dataSnapshot.getValue(User::class.java)
                    if (have_cash<snap!!.goods_cash)
                    {
                        alert.setMessage("您的錢不夠哦")
                        alert.setPositiveButton("確定") { dialog, which ->
                            Toast.makeText(applicationContext, "請先充值", Toast.LENGTH_SHORT).show()
                        }
                        alert.show()
                    }
                    else {
                        alert.setMessage("是否要跟${snap.username}購買「${snap.goods_name}」花費${snap.goods_cash}?")
                        alert.setPositiveButton("YES") { dialog, which ->
                            val key =  FirebaseDatabase.getInstance().getReference("/record").push().key
                            val cash = snap.cash.toInt()
                            FirebaseDatabase.getInstance().getReference("/user/${uid}").child("cash").setValue(have_cash- snap.goods_cash)
                            FirebaseDatabase.getInstance().getReference("/user/${data[2]}").child("cash").setValue(cash+ snap.goods_cash)
                            val record=record(data[2],snap.username,uid,current_user_name,"goods", snap.goods_cash,now_time,snap.goods_name)
                            FirebaseDatabase.getInstance().getReference("/record/${key}").setValue(record)
                            FirebaseDatabase.getInstance().getReference("/user/${uid}/record/${key}").setValue("pay")
                            FirebaseDatabase.getInstance().getReference("/user/${data[2]}/record/${key}").setValue("get")
                            Toast.makeText(applicationContext, "交易完成", Toast.LENGTH_SHORT).show()
                        }
                        alert.show()
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }*/
    }

    fun open_scanner(){
        val  integrator= IntentIntegrator(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        integrator.setPrompt("Scan a barcode")
        integrator.setCameraId(0)  // 前鏡頭
        integrator.setBeepEnabled(false) // 拍照聲音
        integrator.setBarcodeImageEnabled(true)
        integrator.setOrientationLocked(false) // 部翻轉
        integrator.initiateScan()

    }

}
