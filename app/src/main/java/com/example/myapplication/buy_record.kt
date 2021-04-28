package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

class buy_record : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buy_record)

        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid
        var array = ArrayList<String>()

        FirebaseDatabase.getInstance().getReference("/user/${uid}/record").addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                dataSnapshot.children.forEach {
                    Log.d("text","${it.key}")
                    val type=it.value

                    FirebaseDatabase.getInstance().getReference("/record/${it.key}").addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {

                            Log.d("data","${dataSnapshot}")
                            val data = dataSnapshot.getValue(record::class.java)
                            Log.d("data","${data}")

                            if (type=="get")
                            {
                                if(data?.states=="cash")
                                {
                                    Log.d("data1","${data?.money}")
                                    array.add("從「${data?.payname}」獲得${data?.money}元紅包")

                                }
                                else{
                                    Log.d("data2","${data?.money}")
                                    array.add("販賣「${data?.good_name}」給「${data?.payname}」，獲得${data?.money}元")
                                }
                            }
                            else
                            {
                                if(data?.states=="cash")
                                {
                                    Log.d("data3","${data?.money}")
                                    array.add("付給「${data?.getname}」${data?.money}元紅包")
                                }
                                else{
                                    Log.d("data4","${data?.money}")
                                    array.add("購買「${data?.good_name}」，付給「${data?.getname}」${data?.money}元")
                                }
                            }

                            val adapter = ArrayAdapter(this@buy_record,R.layout.list_item, array)

                            val listView:ListView = findViewById(R.id.list)
                            listView.setAdapter(adapter)
                        }



                        override fun onCancelled(databaseError: DatabaseError) {}

                    })
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {}

        })


        //取得活動交易紀錄
        //目的：找出會員領參與獎金的紀錄
        FirebaseDatabase.getInstance().getReference("/user/${uid}/activity").addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                dataSnapshot.children.forEach {
                    Log.d("text","${it.key}")
                    val type=it.value

                    if(type!=="shop"){  //如果是使用者

                        //取得活動id
                        FirebaseDatabase.getInstance().getReference("/activity/${it.key}").addValueEventListener(object : ValueEventListener {

                            override fun onDataChange(dataSnapshot: DataSnapshot) {

                                Log.d("text","${it.key}")
                                Log.d("data","${dataSnapshot}")
                                val data = dataSnapshot.getValue(Activity::class.java)
                                Log.d("data","${data}")

                                //取得record_id
                                FirebaseDatabase.getInstance().getReference("/user/${uid}/activity/${data?.aid}").addValueEventListener(object : ValueEventListener {

                                    override fun onDataChange(dataSnapshot: DataSnapshot) {

                                        Log.d("data","${dataSnapshot}")
                                        val data2 = dataSnapshot.getValue(Activity::class.java)
                                        Log.d("data","${data}")


                                        //去user_join
                                        FirebaseDatabase.getInstance().getReference("/user_join/${data2?.record_id}").addValueEventListener(object : ValueEventListener {

                                            override fun onDataChange(dataSnapshot: DataSnapshot) {

                                                Log.d("data","${dataSnapshot}")
                                                val data3 = dataSnapshot.getValue(Activity2::class.java)
                                                Log.d("data","${data}")

                                                //如果有領取獎勵了
                                                if(data3?.get_bonus=="yes"){
                                                    Log.d("data1","${data?.name}")
                                                    array.add("參加「${data?.name}」獲得${data?.bonus}元參與獎勵")
                                                }

                                                val adapter = ArrayAdapter(this@buy_record,R.layout.list_item, array)

                                                val listView:ListView = findViewById(R.id.list)
                                                listView.setAdapter(adapter)

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


                    /*if(type=="shop"){  //如果是商家

                    }*/

                }
            }
            override fun onCancelled(databaseError: DatabaseError) {}

        })



        //取得活動交易紀錄
        //目的：找出廠商發送獎金的紀錄

        FirebaseDatabase.getInstance().getReference("/user/${uid}").addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = dataSnapshot.getValue(User::class.java)


                if(data?.isuser=="false"){  //如果是商家

                    FirebaseDatabase.getInstance().getReference("/user/${uid}/activity/pay_bonus_record").addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {

                            dataSnapshot.children.forEach {
                                Log.d("text","${it.key}")
                                val type=it.value

                                //去user_join取得aid與uid
                                FirebaseDatabase.getInstance().getReference("/user_join/${it.key}").addValueEventListener(object : ValueEventListener {
                                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                                        val data2 = dataSnapshot.getValue(Activity2::class.java)
                                        val data3 = dataSnapshot.getValue(Activity::class.java)
                                        if(data2?.get_bonus=="yes"){ //如果會員已領取獎勵


                                            //去activity取得bonus
                                            FirebaseDatabase.getInstance().getReference("/activity/${data3?.aid}").addValueEventListener(object : ValueEventListener {
                                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                                    val data4 = dataSnapshot.getValue(Activity::class.java)

                                                    Log.d("data","${data4?.record_id}")
                                                    array.add("支付給「${data?.username}」參與「${data4?.name}」的獎勵${data4?.bonus}元")
                                                }
                                                override fun onCancelled(databaseError: DatabaseError) {}

                                            })

                                        }
                                        val adapter = ArrayAdapter(this@buy_record,R.layout.list_item, array)

                                        val listView:ListView = findViewById(R.id.list)
                                        listView.setAdapter(adapter)
                                    }
                                    override fun onCancelled(databaseError: DatabaseError) {}

                                })

                            }
                        }
                        override fun onCancelled(databaseError: DatabaseError) {}

                    })
                }


            }
            override fun onCancelled(databaseError: DatabaseError) {}

        })


    }
}
