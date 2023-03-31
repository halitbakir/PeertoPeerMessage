package com.halitbakir.peertopeermessage;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    //GLOBAL VARİABLE:
    private CircleImageView main_button_telephone;


    //remember password
    private TextView buttonForgotPassword;


    //Linkedin URL
    private static final String linkedinUrl = "https://tr.linkedin.com";
    private CircleImageView socialLinkedinId;


    //email- password
    private EditText main_editText_email;
    private EditText main_editText_password;


    //login
    private Button main_button_login;


    //register
    private TextView main_button_register;


    //user
    private String userEmailAddress, userPassword;


    //Firebase user
    private FirebaseUser firebaseUser;


    //Firebase giriş
    private FirebaseAuth firebaseAuth;


    //Firebase kullanıcısının giriş/çıkış işleminde kullanılır
    private FirebaseAuth.AuthStateListener authStateListener;

    //firebaseAuth'a kullanıcı eklemek
    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    //FirebaseAuth kullanıcı çıkarmak
    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(authStateListener);
    }

    //Google sign in
    private GoogleSignInOptions gso;
    private GoogleSignInClient gsc;
    private CircleImageView main_button_googlesign;

    //sign in method
    private void signInMethod() {
        Intent googleSignIntent = gsc.getSignInIntent();
        //request code
        startActivityForResult(googleSignIntent, 2344);
    }

    //eğer google giriş başarılı olursa AdminActivitye gitsin
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //start code
        if (requestCode == 2344) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                task.getResult(ApiException.class);
                navigateToAdminActivity();
            } catch (ApiException apiException) {
                Toast.makeText(this, "Google Sign In went wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Google giriş navigateToAdminActivity
    private void navigateToAdminActivity() {
        finish();
        Intent intent = new Intent(MainActivity.this, AdminActivity.class);
        startActivity(intent);
    }

    //Validation Email
    private Boolean validateEmail(String value) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (value.isEmpty()) {
            main_editText_email.setError("Mail Boş geçilemez");
            return false;
        } else if (!value.matches(emailPattern)) {
            main_editText_email.setError("Mail Uygun formatta girelim");
            return false;
        } else {
            main_editText_email.setError(null);
            return true;
        }
    } //end validateEmail

    //Validation Password
    //Hb123456@
    private Boolean validatePassword(String value) {
        String passwordVal = "^" +
                "(?=.*[0-9])" +          // En az 1 tane sayı
                "(?=.*[a-z])" +          // en az 1 tane küçük harf
                "(?=.*[A-Z])" +          // en az 1 tane büyük harf
                "(?=.*[@#$%^&+=])" +     // en az 1 özel karakter
                "(?=\\S+$)" +            // boşluk yok
                ".{4,}" +                // en az 4 karakter
                "$";
        if (value.isEmpty()) {
            main_editText_password.setError("Şifre Boş geçilemez");
            return false;
        } else if (!value.matches(passwordVal)) {
            main_editText_password.setError("Şifre büyük harf, küçük harf, özel karakter ve sayı içermeli.");
            return false;
        } else {
            main_editText_password.setError(null);
            return true;
        }
    } //end validatePassword

    //ONCREATE
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //start codes

        //ID almak:
        main_editText_email = findViewById(R.id.main_editText_email);
        main_editText_password = findViewById(R.id.main_editText_password);
        main_button_login = findViewById(R.id.main_button_login);
        main_button_register = findViewById(R.id.main_button_register);

        //Firebase Instance
        firebaseAuth = FirebaseAuth.getInstance();


        //full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Kullanıcı sisteme giriş yapmış mı?
        //eğer sistemde kullanıcı giriş yapmışsa Admin sayfasında yönlendirsin
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                //kullanıcı bilgileri getirmek
                firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) { //kullanıcı varsa
                    Intent adminIntent = new Intent(getApplicationContext(), AdminActivity.class); //admin activitye yönlendirme yapar
                    //Toast'a String bir ifade göndermek istersek
                    Toast.makeText(MainActivity.this, getString(R.string.admin_redirect), Toast.LENGTH_SHORT).show();
                    startActivity(adminIntent);
                }//end if
            } //end onAuthStateChanged
        }; // end authStateListener


        //Login butona tıkladığında:
        main_button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //login butonuna tıklayınca Email ve passwordu alalım:
                userEmailAddress = main_editText_email.getText().toString();
                userPassword = main_editText_password.getText().toString();

                //input validation
                if (userPassword.isEmpty() || userPassword.equals("") || userPassword == null) {
                    Toast.makeText(MainActivity.this, "Lütfen password boş geçmeyiniz", Toast.LENGTH_SHORT).show();
                }
                if (userEmailAddress.isEmpty() || userEmailAddress.equals("") || userEmailAddress == null) {
                    Toast.makeText(MainActivity.this, "Lütfen email boş geçmeyiniz", Toast.LENGTH_SHORT).show();
                }
                if (!validatePassword(userPassword) || !validateEmail(userEmailAddress)) {
                    return;
                }
                //sisteme giriş dinliyoruz.
                firebaseAuth.signInWithEmailAndPassword(userEmailAddress, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    //eğer sisteme giriş başarılıysa admin sayfasına yönlendir.
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (firebaseUser != null) {
                            Intent adminIntent = new Intent(getApplicationContext(), AdminActivity.class);
                            //Toast'a String bir ifade göndermek istersek
                            Toast.makeText(MainActivity.this, getString(R.string.admin_redirect), Toast.LENGTH_SHORT).show();
                            startActivity(adminIntent);
                        } else {
                            Toast.makeText(MainActivity.this, "Kullanıcı bulunamadı!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    //eğer siteme giriş yaparken herhangi bir hata alırsam. örneğin: internet olmayabilir. sistemde kullanıcı olmayabilir
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, getString(R.string.admin_faile), Toast.LENGTH_SHORT).show();
                    }// end  onFailure
                }); // end addOnFailureListener
            }// end onClick
        });// end setOnClickListener


        //Register Button (kayıt olma)
        main_button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Google sayfasına gitmesi için
                Intent registerIntent = new Intent(getApplicationContext(), RegisterActivity.class);
                Toast.makeText(MainActivity.this, getString(R.string.register_redirect), Toast.LENGTH_SHORT).show();
                startActivity(registerIntent);
            } //end onClick
        }); //end main_button_register

        //Google Login
        main_button_googlesign = findViewById(R.id.main_button_googlesign);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);

        //eğer google ile giriş yapılmışsa tekrar giriş yapılmasını engelleme
        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (googleSignInAccount != null) {
            navigateToAdminActivity();
        }

        //main_button_googlesign setOnClick Listener
        main_button_googlesign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInMethod();
            }//end click
        }); //end setOnClickListener

        //telephone validation
        main_button_telephone = findViewById(R.id.main_button_telephone);
        main_button_telephone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Telephone Validation
                Intent registerIntent = new Intent(getApplicationContext(), TelephoneValidation.class);
                Toast.makeText(MainActivity.this, "Telephone", Toast.LENGTH_SHORT).show();
                startActivity(registerIntent);
            } //end onClick
        }); //end main_button_register

        //Linkedin Giriş
        socialLinkedinId = findViewById(R.id.socialLinkedinId);
        socialLinkedinId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Alert Dialog");
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Application Out");
                builder.setMessage("Uygulama dışına çıkılcaktır. İzin veriyor musunuz?");
                builder.setNegativeButton("Hayır", null);
                builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent browserRedirect = new Intent(Intent.ACTION_VIEW, Uri.parse(linkedinUrl));
                        startActivity(browserRedirect);
                    }
                });
                builder.show();
            }
        });

        //forgot password
        buttonForgotPassword = findViewById(R.id.buttonForgotPassword);
        buttonForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Mail gönderimini sağlayacak kodlar
                EditText resetMail = new EditText(view.getContext());
                AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(view.getContext());
                passwordResetDialog.setTitle("Şifreyi gerçekten değiştirmek istiyor musunuz? ");
                passwordResetDialog.setMessage("Mail adresinizi giriniz ");
                passwordResetDialog.setView(resetMail);

                //Evete tıklanırsa
                passwordResetDialog.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String mail = resetMail.getText().toString();
                        firebaseAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(MainActivity.this, "Mailiniz Gönderildi.", Toast.LENGTH_SHORT).show();
                            }//end onSuccess
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, "Mailiniz gönderilemedi !", Toast.LENGTH_SHORT).show();
                            }
                        }); // end addOnFailureListener
                    } // end onClick
                }); //end passwordResetDialog.setPositiveButton

                //Hayıra tıklanırsa
                passwordResetDialog.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(MainActivity.this, "Hayıra tıklandı vazgeçildi.", Toast.LENGTH_SHORT).show();
                    }//end onClick
                }); //end setNegativeButton
                passwordResetDialog.create().show();
            }//end onClick
        }); //end  buttonForgotPassword.setOnClickListener


    }//end codes
}//class MainActivity