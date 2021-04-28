package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import java.util.*
import java.text.*
import android.content.Intent
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.zxing.integration.android.IntentIntegrator
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main_page.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
class main_page : AppCompatActivity() {
    var time = Calendar.getInstance()
    var format = SimpleDateFormat ("yyyy-MM-dd hh:mm:ss")
    var now_time =  format.format(time.time)
    var give_cash=0
    var uid=FirebaseAuth.getInstance().currentUser?.uid
    var have_cash=0
    var current_user_name=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)
        if(uid==null)//如果未登入 就回主選單
        {
            val intent= Intent(this,first_choose::class.java)
            intent.flags=Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }//登入狀態判斷是商家還是會員
        else{
            val ref = FirebaseDatabase.getInstance().getReference("/user/$uid")
            ref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    val snap = dataSnapshot.getValue(User::class.java)
                    Picasso.get().load(snap?.profileImgUri).into(user_picture)
                    if (snap?.isuser.toString()=="false"&& snap?.give_cash==null ) // 如果是商家而且沒有設定過價格
                    {
                        val intent = Intent(this@main_page,set_goods_money::class.java)
                        intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                    else if (snap?.isuser.toString()=="false"){  // 如果是商家 顯示內容會有更改
                        give_cash=snap!!.give_cash
                        have_cash= snap!!.cash
                        current_user_name=snap!!.username
                        set_shop(snap!!)
                    }
                    else if(snap?.isuser.toString()=="true"&& snap?.give_cash==null){ // 如果是使用者而且沒有設定過紅包價格
                        val intent = Intent(this@main_page,user_set_redcash::class.java)
                        intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                    else{
                        have_cash=snap!!.cash
                        current_user_name=snap!!.username
                        set_user(snap!!)
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }

        //活動列表
        user_participate.setOnClickListener{
            val intent = Intent(this,participate::class.java)
            startActivity(intent)
        }

        //紀錄
        user_record.setOnClickListener {
            val intent = Intent(this,buy_record::class.java)
            startActivity(intent)
        }

        //登出
        user_logout.setOnClickListener {

            val alert = AlertDialog.Builder(this)
            alert.setMessage("是否登出？")
            alert.setPositiveButton("是") { dialog, which ->

                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this,first_choose::class.java)
                intent.flags=Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)

                Toast.makeText(applicationContext, "成功登出", Toast.LENGTH_SHORT).show()
            }
            alert.show()

        }

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result.contents == null || result== null ) {return}
        val alert = AlertDialog.Builder(this)
        alert.setTitle("交易訊息")
        alert.setNegativeButton("取消"){dialog,which ->
            Toast.makeText(applicationContext,"取消交易",Toast.LENGTH_SHORT).show()
        }
        val data=result.contents.split(";")
        if (data[0]=="true")//QRCODE的第一格欄位是使用者 代表要送紅包
        {
            if (have_cash<give_cash)
            {
                alert.setMessage("您的錢不夠哦")
                alert.setPositiveButton("確定") { dialog, which ->
                    Toast.makeText(applicationContext, "請先充值", Toast.LENGTH_SHORT).show()
                }
                alert.show()
            }
            else {
                val ref = FirebaseDatabase.getInstance().getReference("/user/${data[2]}")
                ref.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val snap = dataSnapshot.getValue(User::class.java)
                        alert.setMessage("是否要送給「${data[1]}」${give_cash}元的紅包?")
                        alert.setPositiveButton("YES") { dialog, which ->
                            val key =  FirebaseDatabase.getInstance().getReference("/record").push().key
                            val cash =snap!!.cash.toInt()
                            FirebaseDatabase.getInstance().getReference("/user/${uid}").child("cash").setValue(have_cash-give_cash)
                            FirebaseDatabase.getInstance().getReference("/user/${data[2]}").child("cash").setValue(cash+give_cash)
                            val record=record(data[2],data[1],uid,current_user_name,"cash",give_cash,now_time)
                            FirebaseDatabase.getInstance().getReference("/record/${key}").setValue(record)
                            FirebaseDatabase.getInstance().getReference("/user/${uid}/record/${key}").setValue("pay")
                            FirebaseDatabase.getInstance().getReference("/user/${data[2]}/record/${key}").setValue("get")
                            Toast.makeText(applicationContext, "交易完成", Toast.LENGTH_SHORT).show()
                        }
                        alert.show()
                    }
                    override fun onCancelled(databaseError: DatabaseError) {}
                })
            }
        }
        else//QRCODE的第一格欄位是商家 代表要購買物品
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
    fun set_shop(snap:User){
        user_name.text = "商家名稱 : "+snap?.username.toString()
        user_cash.text = "商家餘額 : "+snap?.cash.toString()
        user_redcash.text = "送 紅 包"
        user_goods.text = "販 售 商 品"
        user_participate.text="活 動 列 表"

        user_redcash.setOnClickListener {
            open_scanner()
        }
        user_goods.setOnClickListener {
            val intent = Intent(this@main_page,show_QR_code::class.java)
            intent.putExtra("uid",snap.uid)
            intent.putExtra("is_user", snap.isuser)
            intent.putExtra("user_name", snap.username)
            startActivity(intent)
        }
        user_participate.setOnClickListener{
            val intent = Intent(this@main_page,activity_list::class.java)
            //intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }
    fun set_user(snap: User){
        user_name.text = "用戶名稱 : "+snap?.username.toString()
        user_cash.text = "用戶餘額 : "+snap?.cash.toString()

        user_redcash.setOnClickListener {
            /*val intent = Intent(this@main_page,show_QR_code::class.java)
            intent.putExtra("uid",snap.uid)
            intent.putExtra("is_user", snap.isuser)
            intent.putExtra("user_name", snap.username)
            startActivity(intent)*/

            val intent = Intent(this@main_page,redcash::class.java)
            intent.putExtra("uid",snap.uid)
            intent.putExtra("user_name", snap.username)
            startActivity(intent)
        }
        user_goods.setOnClickListener {
            open_scanner()
        }
    }
}

