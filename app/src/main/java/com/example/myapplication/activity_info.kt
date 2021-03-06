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


                if (snap?.isuser.toString()=="false" )  {   //???????????????
                    val btn:Button= findViewById(R.id.activity_attend)
                    btn.text="??????????????????"

                    activity_bonus.setText("???????????????"+get_bonus)


                    //???????????????
                    btn.setOnClickListener{

                        FirebaseDatabase.getInstance().getReference("/activity/${get_aid}").addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                val data2 = dataSnapshot.getValue(Activity::class.java)

                                if(data2!!.bonus.toInt()>snap!!.cash.toInt()){  //??????????????????
                                    Toast.makeText(applicationContext, "???????????????", Toast.LENGTH_SHORT).show()
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


                    //???????????????
                    go_back.setOnClickListener{
                        val intent = Intent(this@activity_info,activity_list::class.java)
                        intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                    }
                }
                else{   //??????????????????

                    //??????????????????????????????
                    val check_join = FirebaseDatabase.getInstance().getReference("/user/$uid/activity")
                    check_join.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val csnap = dataSnapshot.child(get_aid).exists()

                            if(!csnap)   //???????????????
                            {
                                val myref = FirebaseDatabase.getInstance().getReference("/user_join/$uid")
                                //??????????????????
                                activity_attend.setOnClickListener{

                                    alert.setTitle("????????????")
                                    alert.setMessage("????????????????????????")
                                    alert.setPositiveButton("??????") { dialog, which ->

                                        val key =  FirebaseDatabase.getInstance().getReference("/user_join").push().key
                                        val activity_join=Activity(get_aid,uid,get_bonus).toMap()

                                        val record_id=Activity(key.toString()).toMap()

                                        //?????????????????????
                                        FirebaseDatabase.getInstance().getReference("/user_join/${key}").setValue(activity_join)
                                        FirebaseDatabase.getInstance().getReference("/user_join/${key}/get_bonus").setValue("no")

                                        //????????????????????????????????????
                                        FirebaseDatabase.getInstance().getReference("/user/${uid}/activity/$get_aid").setValue(record_id)

                                        activity_bonus.setText("??????????????????")  //????????????????????????
                                        activity_attend.setText("??? ??? ??? ???")

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

                                        Toast.makeText(applicationContext, "????????????", Toast.LENGTH_SHORT).show()

                                    }
                                    alert.show()



                                }

                            }
                            else{   //???????????????

                                //????????????????????????????????????
                                val check_bonus = FirebaseDatabase.getInstance().getReference("/user/$uid/activity/$get_aid")   //????????????id
                                check_bonus.addValueEventListener(object : ValueEventListener {
                                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                                        val bsnap = dataSnapshot.getValue(Activity::class.java)

                                        val check_get_bonus = FirebaseDatabase.getInstance().getReference("/user_join/${bsnap?.record_id}")
                                        check_get_bonus.addValueEventListener(object : ValueEventListener {
                                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                                val gbsnap = dataSnapshot.getValue(Activity2::class.java)

                                                if(gbsnap?.get_bonus=="yes")   //??????????????????
                                                {
                                                    activity_attend.setText("??? ??? ??? ??? ???")
                                                    activity_bonus.setText("??????????????????")
                                                }
                                                else{   //???????????????

                                                    activity_bonus.setText("??????????????????")  //????????????????????????
                                                    activity_attend.setText("??? ??? ??? ???")

                                                    //??????????????????????????????
                                                    val activity_bonus : TextView = findViewById(R.id.activity_bonus)
                                                    activity_bonus.setOnClickListener{
                                                        open_scanner()

                                                    }


                                                    val jref = FirebaseDatabase.getInstance().getReference("/user/$uid/activity/$get_aid")
                                                    jref.addListenerForSingleValueEvent(object : ValueEventListener {
                                                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                                                            val jsnap = dataSnapshot.getValue(Activity::class.java)

                                                            //????????????????????????
                                                            activity_attend.setOnClickListener{

                                                                alert.setTitle("??????????????????")
                                                                alert.setMessage("??????????????????????????????")
                                                                alert.setPositiveButton("??????") { dialog, which ->

                                                                    //??????????????????????????????
                                                                    FirebaseDatabase.getInstance().getReference("/user/$uid/activity/$get_aid").removeValue()

                                                                    //???????????????????????????
                                                                    FirebaseDatabase.getInstance().getReference("/user_join/${jsnap?.record_id}").removeValue()


                                                                    activity_bonus.setText("")  //????????????????????????
                                                                    activity_attend.setText("??? ???")

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


                                                                    Toast.makeText(applicationContext, "??????????????????", Toast.LENGTH_SHORT).show()

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


                    //???????????????
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


    //???????????????????????????
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        super.onActivityResult(requestCode, resultCode, data)

        var uid=FirebaseAuth.getInstance().currentUser?.uid
        val get_shop_id:String = intent.getStringExtra("shop_id")

        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result.contents == null || result== null ) {return}


        val data=result.contents.split(";")

        if (data[0]=="false"){  //QRCODE??????????????????????????? ???????????????????????????

            val ref = FirebaseDatabase.getInstance().getReference("/user/${data[2]}")   //???userid???????????????
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val snap = dataSnapshot.getValue(User::class.java)

                    val get_aid:String = intent.getStringExtra("aid")

                    val aref = FirebaseDatabase.getInstance().getReference("/activity/$get_aid")    //?????????id???????????????
                    aref.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val asnap = dataSnapshot.getValue(Activity::class.java)

                            val get_name:String = intent.getStringExtra("name")

                            val jref = FirebaseDatabase.getInstance().getReference("/user/$uid/activity/$get_aid")  //??????????????????id
                            jref.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    val jsnap = dataSnapshot.getValue(Activity::class.java)

                                    val gref = FirebaseDatabase.getInstance().getReference("/user_join/${jsnap?.record_id}")    //??????user_join/record_id
                                    gref.addListenerForSingleValueEvent(object : ValueEventListener{
                                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                                            val gsnap = dataSnapshot.getValue(Activity2::class.java)

                                            val alert = AlertDialog.Builder(this@activity_info)

                                            if(gsnap?.get_bonus.toString()=="yes"){ //??????????????????
                                                /*alert.setMessage("????????????????????????")
                                                alert.setPositiveButton("??????") { dialog, which ->


                                                }
                                                alert.show()*/

                                            }
                                            else{   //??????????????????
                                                alert.setMessage("??????????????????"+get_name+"?????????????????????${asnap?.bonus}??????")
                                                alert.setPositiveButton("??????") { dialog, which ->

                                                    val wsref = FirebaseDatabase.getInstance().getReference("/user/${gsnap?.uid}")    //???????????????????????????cash
                                                    wsref.addListenerForSingleValueEvent(object : ValueEventListener{
                                                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                                                            val wsnap = dataSnapshot.getValue(User::class.java)

                                                                FirebaseDatabase.getInstance().getReference("/user_join/${jsnap?.record_id}").child("get_bonus").setValue("yes") //???get_bonus???yes????????????????????????
                                                                val new_cash:Int=wsnap!!.cash.toInt().plus(asnap!!.bonus.toInt()) //???????????????????????????
                                                                FirebaseDatabase.getInstance().getReference("/user/${gsnap?.uid}").child("cash").setValue(new_cash)

                                                        }
                                                        override fun onCancelled(databaseError: DatabaseError) {}
                                                    })
                                                    val sref = FirebaseDatabase.getInstance().getReference("/user/${get_shop_id}")    //?????????????????????
                                                    sref.addListenerForSingleValueEvent(object : ValueEventListener{
                                                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                                                            val ssnap = dataSnapshot.getValue(User::class.java)

                                                            val new_shop_cash:Int=ssnap!!.cash.toInt().minus(asnap!!.bonus.toInt())  //????????????????????????
                                                            FirebaseDatabase.getInstance().getReference("/user/${get_shop_id}").child("cash").setValue(new_shop_cash)



                                                            //????????????user/user_id/?????????pay_bonus_user_join_record/${jsnap?.record_id}:"pay_bonus"
                                                            FirebaseDatabase.getInstance().getReference("/user/${get_shop_id}/activity/pay_bonus_record/${jsnap?.record_id}").setValue("pay_bonus")

                                                            activity_attend.setText("??? ??? ??? ??? ???")
                                                            Toast.makeText(applicationContext, "?????????????????????", Toast.LENGTH_SHORT).show()


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
        integrator.setCameraId(0)  // ?????????
        integrator.setBeepEnabled(false) // ????????????
        integrator.setBarcodeImageEnabled(true)
        integrator.setOrientationLocked(false) // ?????????
        integrator.initiateScan()

    }


}
