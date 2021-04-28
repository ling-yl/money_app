package com.example.myapplication
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.Toast
import android.util.Log
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.database.FirebaseDatabase
import android.app.Activity
import java.util.*
import android.provider.MediaStore
class register : AppCompatActivity() {
    var isuser = "true"
    var cash = 0
    var select_photo :Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val getIntent = intent.getStringExtra("shop");
        Log.d("intent","intent"+getIntent)
        if (getIntent == "shop") {
            select_picture.setText("請選擇商標")
            register_name.setHint("商家名稱")
            isuser="false"
            cash=10000
        }
        else{
            cash=500
        }

        register_button.setOnClickListener {
            performRegister()
        }
        login_button.setOnClickListener {
            val intent = Intent(this,login::class.java)
            startActivity(intent)
        }
        select_picture.setOnClickListener {
            val intent=Intent(Intent.ACTION_PICK)
            intent.type="image/*"
            startActivityForResult(intent,0)
        }

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==0 && resultCode == Activity.RESULT_OK && data!=null)
        {
            select_photo = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,select_photo)
            select_photo_after.setImageBitmap(bitmap)
            select_picture.alpha = 0f
        }
    }

    private fun performRegister(){
        val username = register_name.text.toString()
        val email = register_mail.text.toString()
        val password = register_password.text.toString()
        if(username.isEmpty() || email.isEmpty())
        {
            Toast.makeText(this,"請輸入email或username",Toast.LENGTH_SHORT).show()
            return
        }
        else if (password.isEmpty())
        {
            Toast.makeText(this,"請輸入password",Toast.LENGTH_SHORT).show()
            return
        }
        else if(select_photo==null) return

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information

                Log.d("create_success", "createUserWithEmail:success")
                uploadImageToFirebase()
            } else {
                // If sign in fails, display a message to the user.
                Log.w("create_failed", "createUserWithEmail:failure", task.exception)
                Toast.makeText(
                    baseContext, "已經有重複的帳號",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


    }
    private fun uploadImageToFirebase(){

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        ref.putFile(select_photo!!).addOnSuccessListener {
            Log.d("register","img${it.metadata?.path}")
            ref.downloadUrl.addOnSuccessListener {
                Log.d("register","file location $it")
                SaveToRealTimeDataBase(it.toString())
            }
        }

    }
    private fun SaveToRealTimeDataBase(profileImgUri:String)
    {
        Log.d("register",isuser.toString()+cash.toString())
        val uid = FirebaseAuth.getInstance().uid?:""
        val ref = FirebaseDatabase.getInstance().getReference("/user/$uid")
        val user = User(profileImgUri,uid,cash,isuser.toString(),register_name.text.toString())
        ref.setValue(user).addOnSuccessListener {
            Log.d("register","real time database access success")
        }
        val intent = Intent(this,main_page::class.java)
        intent.flags=Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)

    }


}
