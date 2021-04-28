/*
package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class set_activity extends AppCompatActivity {

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_activity);

        Button submit=(Button)findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performAdd();

            }
        });

    }

    public void performAdd(){
        EditText ac_name=(EditText)findViewById(R.id.ac_name);
        EditText ac_location=(EditText)findViewById(R.id.ac_location);
        EditText ac_datetime=(EditText)findViewById(R.id.ac_datetime);
        EditText ac_introduction=(EditText)findViewById(R.id.ac_introduction);
        EditText ac_bonus=(EditText)findViewById(R.id.ac_bonus);

        String name=ac_name.getText().toString();
        String location=ac_location.getText().toString();
        String datetime=ac_datetime.getText().toString();
        String introduction=ac_introduction.getText().toString();
        String bonus=ac_bonus.getText().toString();
        String usera=user.getUid().toString();  //取得userid

        FirebaseDatabase db = FirebaseDatabase.getInstance(); //取得Firebase連結
        DatabaseReference ContactsRef = db.getReference("activity");


        activity get_ac=new activity(name,location,datetime,introduction,bonus,usera);

        ac_bonus.setText(get_ac.toString());
        //ContactsRef.child("1").setValue(get_ac);
    }
}
*/
