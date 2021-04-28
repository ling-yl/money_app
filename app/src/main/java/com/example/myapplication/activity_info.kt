package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_info.*


class activity_info : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)


        var uid=FirebaseAuth.getInstance().currentUser?.uid
        val user = FirebaseAuth.getInstance().currentUser!!.uid
        var snap = false
        //val myref = FirebaseDatabase.getInstance().getReference("/user_join")
        val alert = AlertDialog.Builder(this)
        
        val get_aid:String = intent.getStringExtra("aid")
        val get_name:String = intent.getStringExtra("name")
        val get_introduction:String = intent.getStringExtra("introduction")
        val get_datetime:String = intent.getStringExtra("datetime")
        val get_location:String = intent.getStringExtra("location")
        val get_bonus:String = intent.getStringExtra("bonus")
        val get_shop_id:String = intent.getStringExtra("shop_id")

        activity_name.setText(get_name)
        activity_introduction.setText(get_introduction)
        activity_date.setText(get_datetime)
        activity_address.setText(get_location)

        val ref = FirebaseDatabase.getInstance().getReference("/user/$uid")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val snap = dataSnapshot.getValue(User::class.java)


                if (snap?.isuser.toString()=="false" )  {   //如果是商家
                    val btn:Button= findViewById(R.id.activity_attend)
                    btn.text="發送參與獎勵"

                    activity_bonus.setText("獎勵金額："+get_bonus)


                    //給活動獎勵
                    btn.setOnClickListener{

                        FirebaseDatabase.getInstance().getReference("/activity/${get_aid}").addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                val data2 = dataSnapshot.getValue(Activity::class.java)

                                if(data2!!.bonus.toInt()>snap!!.cash.toInt()){  //如果餘額不足
                                    Toast.makeText(applicationContext, "餘額不足！", Toast.LENGTH_SHORT).show()
                                }
                                else{
                                    val intent = Intent(this@activity_info,show_QR_code::class.java)
                                    intent.putExtra("uid",snap?.uid)
                                    intent.putExtra("is_user", snap?.isuser)
                                    intent.putExtra("user_name", snap?.username)
                                    startActivity(intent)
                                }

                            }
                            override fun onCancelled(databaseError: DatabaseError) {}

                        })



                    }


                    //回活動列表
                    go_back.setOnClickListener{
                        val intent = Intent(this@activity_info,activity_list::class.java)
                        intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                    }
                }
                else{   //如果是使用者

                    //目的：檢查有沒有報名
                    val check_join = FirebaseDatabase.getInstance().getReference("/user/$uid/activity")
                    check_join.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val csnap = dataSnapshot.child(get_aid).exists()

                            if(!csnap)   //如果沒報名
                            {
                                val myref = FirebaseDatabase.getInstance().getReference("/user_join/$uid")
                                //按下報名之後
                                activity_attend.setOnClickListener{

                                    alert.setTitle("活動報名")
                                    alert.setMessage("確定要報名活動？")
                                    alert.setPositiveButton("確定") { dialog, which ->

                                        val key =  FirebaseDatabase.getInstance().getReference("/user_join").push().key
                                        val activity_join=Activity(get_aid,uid,get_bonus).toMap()

                                        val record_id=Activity(key.toString()).toMap()

                                        //新增至參與紀錄
                                        FirebaseDatabase.getInstance().getReference("/user_join/${key}").setValue(activity_join)
                                        FirebaseDatabase.getInstance().getReference("/user_join/${key}/get_bonus").setValue("no")

                                        //新增至使用者內的參與紀錄
                                        FirebaseDatabase.getInstance().getReference("/user/${uid}/activity/$get_aid").setValue(record_id)

                                        activity_bonus.setText("領取活動獎勵")  //顯示領取活動獎勵
                                        activity_attend.setText("取 消 報 名")

                                        val intent = Intent(this@activity_info,activity_info::class.java)
                                        intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TOP
                                        intent.putExtra("aid",get_aid)
                                        intent.putExtra("name",get_name)
                                        intent.putExtra("introduction",get_introduction)
                                        intent.putExtra("datetime",get_datetime)
                                        intent.putExtra("location",get_location)
                                        intent.putExtra("bonus",get_bonus)
                                        intent.putExtra("shop_id",get_shop_id)
                                        startActivity(intent)

                                        Toast.makeText(applicationContext, "報名成功", Toast.LENGTH_SHORT).show()

                                    }
                                    alert.show()



                                }

                            }
                            else{   //如果有報名

                                //目的：確認有沒有領過獎勵
                                val check_bonus = FirebaseDatabase.getInstance().getReference("/user/$uid/activity/$get_aid")   //取得紀錄id
                                check_bonus.addValueEventListener(object : ValueEventListener {
                                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                                        val bsnap = dataSnapshot.getValue(Activity::class.java)

                                        val check_get_bonus = FirebaseDatabase.getInstance().getReference("/user_join/${bsnap?.record_id}")
                                        check_get_bonus.addValueEventListener(object : ValueEventListener {
                                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                                val gbsnap = dataSnapshot.getValue(Activity2::class.java)

                                                if(gbsnap?.get_bonus=="yes")   //已經領過獎勵
                                                {
                                                    activity_attend.setText("已 完 成 報 到")
                                                    activity_bonus.setText("獎勵已領取！")
                                                }
                                                else{   //未領取獎勵

                                                    activity_bonus.setText("領取活動獎勵")  //顯示領取活動獎勵
                                                    activity_attend.setText("取 消 報 名")

                                                    //按下領取活動獎勵之後
                                                    val activity_bonus : TextView = findViewById(R.id.activity_bonus)
                                                    activity_bonus.setOnClickListener{
                                                        open_scanner()

                                                    }


                                                    val jref = FirebaseDatabase.getInstance().getReference("/user/$uid/activity/$get_aid")
                                                    jref.addListenerForSingleValueEvent(object : ValueEventListener {
                                                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                                                            val jsnap = dataSnapshot.getValue(Activity::class.java)

                                                            //按下取消報名之後
                                                            activity_attend.setOnClickListener{

                                                                alert.setTitle("取消活動報名")
                                                                alert.setMessage("確定要取消報名活動？")
                                                                alert.setPositiveButton("確定") { dialog, which ->

                                                                    //移除使用者的參與紀錄
                                                                    FirebaseDatabase.getInstance().getReference("/user/$uid/activity/$get_aid").removeValue()

                                                                    //移除參與記錄的資料
                                                                    FirebaseDatabase.getInstance().getReference("/user_join/${jsnap?.record_id}").removeValue()


                                                                    activity_bonus.setText("")  //顯示領取活動獎勵
                                                                    activity_attend.setText("報 名")

                                                                    val intent = Intent(this@activity_info,activity_info::class.java)
                                                                    intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TOP
                                                                    intent.putExtra("aid",get_aid)
                                                                    intent.putExtra("name",get_name)
                                                                    intent.putExtra("introduction",get_introduction)
                                                                    intent.putExtra("datetime",get_datetime)
                                                                    intent.putExtra("location",get_location)
                                                                    intent.putExtra("bonus",get_bonus)
                                                                    intent.putExtra("shop_id",get_shop_id)
                                                                    startActivity(intent)


                                                                    Toast.makeText(applicationContext, "取消報名成功", Toast.LENGTH_SHORT).show()

                                                                }
                                                                alert.show()

                                                            }


                                                        }
                                                        override fun onCancelled(databaseError: DatabaseError) {}
                                                    })
                                                }
                                            }
                                            override fun onCancelled(databaseError: DatabaseError) {}
                                        })

                                    }
                                    override fun onCancelled(databaseError: DatabaseError) {}
                                })
                            }
                        }
                        override fun onCancelled(databaseError: DatabaseError) {}
                    })


                    //回活動列表
                    go_back.setOnClickListener{
                        val intent = Intent(this@activity_info,participate::class.java)
                        //intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }

                }


            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })


    }


    //目的：領取參與獎勵
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        super.onActivityResult(requestCode, resultCode, data)

        var uid=FirebaseAuth.getInstance().currentUser?.uid
        val get_shop_id:String = intent.getStringExtra("shop_id")

        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result.contents == null || result== null ) {return}


        val data=result.contents.split(";")

        if (data[0]=="false"){  //QRCODE的第一格欄位是商家 代表要領取參與獎勵

            val ref = FirebaseDatabase.getInstance().getReference("/user/${data[2]}")   //取userid以下的資料
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val snap = dataSnapshot.getValue(User::class.java)

                    val get_aid:String = intent.getStringExtra("aid")

                    val aref = FirebaseDatabase.getInstance().getReference("/activity/$get_aid")    //取活動id以下的資料
                    aref.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val asnap = dataSnapshot.getValue(Activity::class.java)

                            val get_name:String = intent.getStringExtra("name")

                            val jref = FirebaseDatabase.getInstance().getReference("/user/$uid/activity/$get_aid")  //取得參與紀錄id
                            jref.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    val jsnap = dataSnapshot.getValue(Activity::class.java)

                                    val gref = FirebaseDatabase.getInstance().getReference("/user_join/${jsnap?.record_id}")    //取得user_join/record_id
                                    gref.addListenerForSingleValueEvent(object : ValueEventListener{
                                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                                            val gsnap = dataSnapshot.getValue(Activity2::class.java)

                                            val alert = AlertDialog.Builder(this@activity_info)

                                            if(gsnap?.get_bonus.toString()=="yes"){ //代表領過獎勵
                                                /*alert.setMessage("已經領過獎勵了！")
                                                alert.setPositiveButton("確定") { dialog, which ->


                                                }
                                                alert.show()*/

                                            }
                                            else{   //還沒領過獎勵
                                                alert.setMessage("是否要領取「"+get_name+"」的參與獎勵：${asnap?.bonus}元？")
                                                alert.setPositiveButton("確定") { dialog, which ->

                                                    val wsref = FirebaseDatabase.getInstance().getReference("/user/${gsnap?.uid}")    //目的：取得使用者的cash
                                                    wsref.addListenerForSingleValueEvent(object : ValueEventListener{
                                                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                                                            val wsnap = dataSnapshot.getValue(User::class.java)

                                                                FirebaseDatabase.getInstance().getReference("/user_join/${jsnap?.record_id}").child("get_bonus").setValue("yes") //在get_bonus設yes，代表已領過獎勵
                                                                val new_cash:Int=wsnap!!.cash.toInt().plus(asnap!!.bonus.toInt()) //取得使用者新的餘額
                                                                FirebaseDatabase.getInstance().getReference("/user/${gsnap?.uid}").child("cash").setValue(new_cash)

                                                        }
                                                        override fun onCancelled(databaseError: DatabaseError) {}
                                                    })
                                                    val sref = FirebaseDatabase.getInstance().getReference("/user/${get_shop_id}")    //取得廠商的資料
                                                    sref.addListenerForSingleValueEvent(object : ValueEventListener{
                                                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                                                            val ssnap = dataSnapshot.getValue(User::class.java)

                                                            val new_shop_cash:Int=ssnap!!.cash.toInt().minus(asnap!!.bonus.toInt())  //取得商家新的餘額
                                                            FirebaseDatabase.getInstance().getReference("/user/${get_shop_id}").child("cash").setValue(new_shop_cash)



                                                            //在廠商的user/user_id/下新增pay_bonus_user_join_record/${jsnap?.record_id}:"pay_bonus"
                                                            FirebaseDatabase.getInstance().getReference("/user/${get_shop_id}/activity/pay_bonus_record/${jsnap?.record_id}").setValue("pay_bonus")

                                                            activity_attend.setText("已 完 成 報 到")
                                                            Toast.makeText(applicationContext, "成功領取獎勵！", Toast.LENGTH_SHORT).show()


                                                        }
                                                        override fun onCancelled(databaseError: DatabaseError) {}
                                                    })


                                                }
                                                alert.show()
                                            }



                                        }
                                        override fun onCancelled(databaseError: DatabaseError) {}
                                    })








                                }
                                override fun onCancelled(databaseError: DatabaseError) {}
                            })




                        }
                        override fun onCancelled(databaseError: DatabaseError) {}
                    })





                }
                override fun onCancelled(databaseError: DatabaseError) {}
            })

        }
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
