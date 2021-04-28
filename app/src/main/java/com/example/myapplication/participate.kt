package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_participate.*
import kotlinx.android.synthetic.main.content_activity_list.*
import java.util.ArrayList

class participate : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_participate)

        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid
        var array = ArrayList<String>()

        var array_aid= ArrayList<String>()
        var array_name= ArrayList<String>()
        var array_introduction= ArrayList<String>()
        var array_datetime= ArrayList<String>()
        var array_location= ArrayList<String>()
        var array_bonus= ArrayList<String>()
        var array_shop_id= ArrayList<String>()

        //取得activity
        FirebaseDatabase.getInstance().getReference("/activity").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                dataSnapshot.children.forEach{
                    Log.d("text","${it.key}")   //it.key.toString()>activity id
                    val type=it.value   //type.toString()>shop


                    FirebaseDatabase.getInstance().getReference("/activity/${it.key}").addValueEventListener(object :
                        ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            var value = dataSnapshot.getValue(Activity::class.java)
                            array.add("${value?.name}")

                            array_aid.add("${value?.aid}")
                            array_name.add("${value?.name}")
                            array_introduction.add("${value?.introduction}")
                            array_datetime.add("${value?.datetime}")
                            array_location.add("${value?.location}")
                            array_bonus.add("${value?.bonus}")
                            array_shop_id.add("${value?.uid}")

                            val adapter = ArrayAdapter(this@participate,R.layout.list_item, array)
                            val listView: ListView = findViewById(R.id.list)
                            listView.setAdapter(adapter)


                            val list: ListView =findViewById(R.id.list)
                            list.setOnItemClickListener{ adapterView: AdapterView<*>, view1: View, i: Int, l: Long ->
                                val intent = Intent(this@participate,activity_info::class.java)
                                intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK

                                intent.putExtra("aid","${array_aid[i]}")
                                intent.putExtra("name","${array_name[i]}")
                                intent.putExtra("introduction","${array_introduction[i]}")
                                intent.putExtra("datetime","${array_datetime[i]}")
                                intent.putExtra("location","${array_location[i]}")
                                intent.putExtra("bonus","${array_bonus[i]}")
                                intent.putExtra("shop_id","${array_shop_id[i]}")
                                startActivity(intent)
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
