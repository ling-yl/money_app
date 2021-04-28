
package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_set_activity.*
import kotlinx.android.synthetic.main.activity_set_goods_money.*
import kotlinx.android.synthetic.main.activity_set_goods_money.submit
import java.util.*

class set_activity : AppCompatActivity() {

    var uid=FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_activity)


        //設定活動資訊
        submit.setOnClickListener {
            val aname = ac_name.text.toString()
            val alocation = ac_location.text.toString()
            val adatetime = ac_datetime.text.toString()
            val aintroduction = ac_introduction.text.toString()
            val abonus = ac_bonus.text.toString()


            //隨機產生字串
            /*val str = "abcdefghigklmnopkrstuvwxyzABCDEFGHIGKLMNOPQRSTUVWXYZ0123456789"
            val random = Random()
            var sf = StringBuffer()
            for (i in 0..5) {
                var number = random.nextInt(62) //0~61
                sf.append(str[number])
            }*/

            val userid = uid.toString();
            val rannum = (Math.random() * 99).toInt()
            //val activityid = sf.toString() + userid + rannum //活動id


            if (aname.isEmpty() || alocation.isEmpty() || adatetime.isEmpty() || aintroduction.isEmpty() || abonus.isEmpty()) {
                Toast.makeText(this, "有格子未填寫", Toast.LENGTH_SHORT).show()
            } else {

                val uid = FirebaseAuth.getInstance().uid ?: ""
                //val ref = FirebaseDatabase.getInstance().getReference("/activity/$activityid")
                val activityid =  FirebaseDatabase.getInstance().getReference("/activity").push().key

                val activity = Activity(
                    activityid,
                    aname,
                    alocation,
                    adatetime,
                    abonus.toInt(),
                    aintroduction,
                    uid.toString()
                ).toMap()

                FirebaseDatabase.getInstance().getReference("/activity//${activityid}").setValue(activity)
                Toast.makeText(applicationContext, "新增成功", Toast.LENGTH_SHORT).show()

                /*ref.updateChildren(activity).addOnSuccessListener {
                    Log.d("setactivity", "real time database access success")


                    //var key =  FirebaseDatabase.getInstance().getReference("/record").push().key
                    //FirebaseDatabase.getInstance().getReference("/record/${key}").setValue(activity)


                    // Write a message to the database
                    //val database = FirebaseDatabase.getInstance() //取得Firebase連結
                    //val myRef = database.getReference("activity") //Firebase入面邊個目錄
                    //myRef.child(userid).setValue(activity)


                    //FirebaseDatabase.getInstance().reference.child("activity").setValue(activity)

                    *//*myRef.child(userid).setValue(activity).addOnSuccessListener {
                    Log.d("add activity","real time database access success")
                }*//*


                    //FirebaseDatabase.getInstance().getReference("/record").setValue(activity)

                    Toast.makeText(applicationContext, "新增成功", Toast.LENGTH_SHORT).show()
                }
*/
                //在user資料表也加入活動id user/activity/活動id


                //val myref = FirebaseDatabase.getInstance().getReference("/user/$uid/activity/${activityid}")

                //val shop=Activity("shop").toMap()
                FirebaseDatabase.getInstance().getReference("/user/$uid/activity/${activityid}").setValue("shop")


                /*myref.updateChildren(shop).addOnSuccessListener{
                    Log.d("setuseractivity", "real time database access success")
                }*/

                val intent = Intent(this@set_activity,activity_list::class.java)
                intent.flags= Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)


            }
        }

    }
}
