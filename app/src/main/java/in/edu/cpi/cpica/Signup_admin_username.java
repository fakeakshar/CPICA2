package in.edu.cpi.cpica;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Timer;
import java.util.TimerTask;

public class Signup_admin_username extends AppCompatActivity {

    FloatingActionButton signup_admin_username_next_btn;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference myref;
    DatabaseReference settingsref;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String temp_username;
    String admin_username_int;
    TextView admin_username_box;
    Animation bounce_anim,generating_admin_username_anim;
    Vibrator buzzer;
    ValueEventListener admin_username_listener;
    Timer timer;

    @Override
    public void onBackPressed() {
        editor.remove("email").apply();
        try{
            settingsref.removeEventListener(admin_username_listener);
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_admin_username);

        buzzer = (Vibrator)this.getSystemService(Context.VIBRATOR_SERVICE);
        bounce_anim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_btn_error_bounce);
        generating_admin_username_anim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.generating_admin_username);
        admin_username_box = (TextView)findViewById(R.id.admin_username_box);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        myref = firebaseDatabase.getReference("Users");
        settingsref = firebaseDatabase.getReference("Settings");
        sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.apply();
        timer = new Timer();

        admin_username_box.startAnimation(generating_admin_username_anim);


        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                runOnUiThread(new Runnable()
                {
                    public void run()
                    {
                        Toast.makeText(Signup_admin_username.this,"Your internet connection seems slow, please wait...",Toast.LENGTH_SHORT).show();
                    }
                });

            }
        },5000);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                runOnUiThread(new Runnable()
                {
                    public void run()
                    {
                        Toast.makeText(Signup_admin_username.this,"Taking longer than expected... Trying again.",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Signup_admin_username.this,Signup_admin_username.class));
                        finish();
                    }
                });

            }
        },15000);

        admin_username_listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                admin_username_int = dataSnapshot.child("admin_username_int").getValue().toString();
                editor.putInt("admin_username_int",Integer.parseInt(admin_username_int));

                temp_username="cpi"+admin_username_int;
                admin_username_box.setText(temp_username);
                generating_admin_username_anim.cancel();
                timer.cancel();
                admin_username_box.startAnimation(bounce_anim);
                buzzer.vibrate(75);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        settingsref.addListenerForSingleValueEvent(admin_username_listener);


        signup_admin_username_next_btn = (FloatingActionButton)findViewById(R.id.signup_admin_username_next_btn);

        signup_admin_username_next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(admin_username_box.getText().equals("(generating...)")){
                    Toast.makeText(getApplicationContext(),"Username is being generated. Please wait.",Toast.LENGTH_LONG).show();
                }
                else{
                    editor.putString("username",temp_username.toUpperCase()).apply();
                    Intent i = new Intent(getApplicationContext(),Signup_admin_password.class);
                    startActivity(i);
                    settingsref.removeEventListener(admin_username_listener);
                    buzzer.cancel();
                    finish();
                }

            }
        });

    }


}
