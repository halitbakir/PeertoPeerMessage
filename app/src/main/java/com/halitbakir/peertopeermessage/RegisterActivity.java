package com.halitbakir.peertopeermessage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {
    //Global Variable
    private EditText register_editText_EmailAddress;
    private EditText register_editText_Password;
    private Button register_button_submit;

    //Firebase işlemleri
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;


    //Email-password
    private String userEmailAddress,userPassword;


    //Validation Email- mail doğrulama
    private Boolean validateEmail(String val){
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (val.isEmpty()){
            register_editText_EmailAddress.setError("Email boş geçilemez");
            return false;
        } else if (val.matches(emailPattern)) {
            register_editText_EmailAddress.setError("Emaili uygun formatta yazmadınız");
            return false;
        }else
            register_editText_EmailAddress.setError(null);
        return true;
    }

    //Validation Password- şifre doğrulama
    private Boolean validatePassword(String val){
        String passwordVal="^"+
                "(?=.*[0-9])"+ //en az 1 sayı girilmeli
                "(?=.*[a-z])"+ //en az 1 harf girilmeli
                "(?=.*[@#$%+=&])"+ // en az 1 tane özel karakter
                "(?=\\S+$)"+ // no white spaces- boşluk olmalı
                ".{4,}"+ // en az 4 karakter ve üstünde olmalı
                "$"
                ;
        if (val.isEmpty()){
            register_editText_Password.setError("Şifre boş geçilemez");
            return false;
        } else if (val.matches(passwordVal)) {
            register_editText_Password.setError("Şifreyi uygun formatta yazmadınız");
            return false;
        }else
            register_editText_Password.setError(null);
        return true;
    }



    //ONCREATE
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //Start

        //ID almak
        register_editText_EmailAddress=findViewById(R.id.register_editText_EmailAddress);
        register_editText_Password=findViewById(R.id.register_editText_Password);
        register_button_submit=findViewById(R.id.register_button_submit);

        //Firebase Instance
        firebaseAuth=FirebaseAuth.getInstance();

        //SıgnIn button onClick
        register_button_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //kullanıcının girdiği email ve passwordu almak
                userEmailAddress= register_editText_EmailAddress.getText().toString();
                userPassword= register_editText_Password.getText().toString();
                firebaseAuth.createUserWithEmailAndPassword(userEmailAddress,userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    //onComplete-1
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Toast.makeText(RegisterActivity.this, getString(R.string.login_account), Toast.LENGTH_SHORT).show();
                        firebaseAuth.signInWithEmailAndPassword(userEmailAddress,userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            //onComplete-2
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Intent adminIntent=new Intent(getApplicationContext(),AdminActivity.class);
                                Toast.makeText(RegisterActivity.this, getString(R.string.admin_redirect), Toast.LENGTH_SHORT).show();
                                startActivity(adminIntent);
                            }//end onComplete
                        }).addOnFailureListener(new OnFailureListener() { //giriş yapmaya çalışırken hata alınamsı
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(RegisterActivity.this, getString(R.string.login_faile), Toast.LENGTH_SHORT).show();
                            }//end onFailure
                        }); //end onFailure

                    }//end onComplete
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterActivity.this, getString(R.string.not_register), Toast.LENGTH_SHORT).show();
                    }
                }); //end createUserWithEmailAndPassword
            }//end onClick
        });


    }//End onCreate
} //End RegisterActivity