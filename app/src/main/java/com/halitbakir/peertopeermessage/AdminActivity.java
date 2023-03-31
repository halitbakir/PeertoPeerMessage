package com.halitbakir.peertopeermessage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AdminActivity extends AppCompatActivity {

    //Global Variable
    private Toolbar myToolBarId;

    private String addPersonEmail;

    private Handler mHandler;

    //Google SignIn
    private GoogleSignInOptions gso;
    private GoogleSignInClient gsc;
    private TextView nameGoogleLoginID, emailGoogleLoginId;
    private Button signOutButtonId;


    //Firebase user
    private FirebaseUser firebaseUser;


    //Firebase giriş
    private FirebaseAuth firebaseAuth;


    //Storage Resim
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private DatabaseReference userReferances;
    private DatabaseReference mailReferances;
    private DatabaseReference imageReferances;
    private TextView userEmailAddressId;
    private final static int PICTURE_CONST = 44;

    //Firebase kullanıcısının giriş/çıkış işleminde kullanılır
    private FirebaseAuth.AuthStateListener authStateListener;

    //firebaseAuth'a kullanıcı eklemek
    @Override
    protected void onStart() {
        super.onStart();
        //firebaseAuth.addAuthStateListener(authStateListener);
    }

    //FirebaseAuth kullanıcı çıkarmak
    @Override
    protected void onStop() {
        super.onStop();
        //firebaseAuth.removeAuthStateListener(authStateListener);
    }

    //Google SignIn
    private void signOutMethod() {
        gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                finish();
                //güvenli çıkış sonrası ana menüye git
                startActivity(new Intent(AdminActivity.this, MainActivity.class));
            }
        });
    }

    //Resim için
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICTURE_CONST && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            StorageReference picturePath = storageReference.child("pictures").child(firebaseAuth.getCurrentUser().getEmail());
            picturePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(AdminActivity.this, "Resminiz Firebase'e eklendi.", Toast.LENGTH_SHORT).show();
                    Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageUrl = uri.toString();
                            imageReferances.setValue(imageUrl);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AdminActivity.this, "Resim yüklenirken sorun oluştu", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    //Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //Menu Item
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //sistemde kullanıcı var mı
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        int menuChooiseItem = item.getItemId();
        switch (menuChooiseItem) {

            case R.id.adminMenuRefleshId:
                if (firebaseUser != null) {
                    Toast.makeText(this, "Sayfa yenileniyor...", Toast.LENGTH_SHORT).show();
                    Intent intentReflesh = new Intent(AdminActivity.this, AdminActivity.class);
                    startActivity(intentReflesh);
                }
                break;


            case R.id.adminMenuPictureId:
                Toast.makeText(this, "Resim ekleme tıklandı", Toast.LENGTH_SHORT).show();
                Intent allPictures = new Intent(Intent.ACTION_PICK);
                allPictures.setType("image/*");
                startActivityForResult(allPictures, PICTURE_CONST);

                //resim yüklendikten sonra database kaydetsin.
                userReferances = databaseReference.child(firebaseAuth.getCurrentUser().getUid().toString());
                //UID
                mailReferances = userReferances.child("mail adresim");
                mailReferances.setValue(firebaseAuth.getCurrentUser().getEmail());
                imageReferances = userReferances.child("resimim");
                break;

            case R.id.adminMenuPersonId:
                Toast.makeText(this, "Kişi ekle tıklandı", Toast.LENGTH_SHORT).show();
                //Alert Dialog
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

                //Custom Dialog
                View view = getLayoutInflater().inflate(R.layout.add_person, null);
                alertDialogBuilder.setView(view);
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

                //add person.xml
                EditText editTextAddPersonMailId = view.findViewById(R.id.editTextAddPersonMailId);
                Button buttonAddPerson = view.findViewById(R.id.buttonAddPerson);

                //Custom Dialog Button Clicked
                buttonAddPerson.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (editTextAddPersonMailId.getText().toString().isEmpty()) {
                            addPersonEmail = editTextAddPersonMailId.getText().toString();
                            editTextAddPersonMailId.setText("");
                            alertDialog.hide();
                            //sorgu Firebase select
                        }
                    }
                });
                break;


            case R.id.adminMenuChronometreId:
                Toast.makeText(this, "Kronometre tıklandı", Toast.LENGTH_SHORT).show();
                Intent chronometreIntent = new Intent(getApplicationContext(), Chronometre.class); //admin activitye yönlendirme yapar
                //Toast'a String bir ifade göndermek istersek
                Toast.makeText(AdminActivity.this, "Kronometre", Toast.LENGTH_SHORT).show();
                startActivity(chronometreIntent);
                break;

            case R.id.adminMenuVkiId:
                Toast.makeText(this, "VKI tıklandı", Toast.LENGTH_SHORT).show();
                Intent vkiIntent = new Intent(getApplicationContext(), VkiActivity.class); //admin activitye yönlendirme yapar
                //Toast'a String bir ifade göndermek istersek
                Toast.makeText(AdminActivity.this, "VKI", Toast.LENGTH_SHORT).show();
                startActivity(vkiIntent);
                break;

            case R.id.adminMenuInfoId:
                Toast.makeText(this, "Info tıklandı", Toast.LENGTH_SHORT).show();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder
                        .append(" Model: " + Build.MODEL) //Telefon model gösterme
                        .append(" Üretici Firma: " + Build.MANUFACTURER) //Telefon üretici gösterme
                        .append(" Mac Address: " + Build.VERSION.SDK_INT);  //Telefon Mac adresi gösterme
                String data = stringBuilder.toString();
                Toast.makeText(this, "Info: " + data, Toast.LENGTH_LONG).show();
                break;

            case R.id.adminMenuMailSendId:
                Toast.makeText(this, "Mail alanı tıklandı", Toast.LENGTH_SHORT).show();
                Intent emailIntent = new Intent(AdminActivity.this, EmailSendActivity.class);
                startActivity(emailIntent);
                break;

            case R.id.adminMenuLogoutId:
                Toast.makeText(this, "Çıkış yapılıyor...", Toast.LENGTH_SHORT).show();
                if (firebaseUser != null) {
                    firebaseAuth.signOut();
                    Toast.makeText(this, "Çıkış yapıldı", Toast.LENGTH_SHORT).show();
                    Intent intentHome = new Intent(AdminActivity.this, MainActivity.class);
                    startActivity(intentHome);
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    //ONCREATE
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        //start
        myToolBarId = findViewById(R.id.myToolBarId);
        myToolBarId.setTitle("Admin Page");
        myToolBarId.setLogo(R.drawable.logo);
        setSupportActionBar(myToolBarId);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        userEmailAddressId = findViewById(R.id.userEmailAddressId);

        //anasayfada kullanıcı mailini göstermek
        if (firebaseUser != null) {
            String email = firebaseAuth.getCurrentUser().getEmail();
            String name = firebaseAuth.getCurrentUser().getDisplayName();
            userEmailAddressId.setText(email);
        }

        storageReference = FirebaseStorage.getInstance().getReference();

        //Full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Google Sign In
        signOutButtonId = findViewById(R.id.signOutButtonId);
        emailGoogleLoginId = findViewById(R.id.userEmailAddressId);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);
        signOutButtonId.setVisibility(View.INVISIBLE);
        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (googleSignInAccount != null) {
            String email = googleSignInAccount.getEmail();
            emailGoogleLoginId.setText(email);
            signOutButtonId.setVisibility(View.VISIBLE);
        }
        signOutButtonId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(AdminActivity.this, "Google Çıkışı yapıldı", Toast.LENGTH_SHORT).show();
                signOutMethod();
            }
        });

    }//end
}//end AdminActivity