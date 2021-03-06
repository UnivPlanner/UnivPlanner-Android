package edu.sungshin.univplanner;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;


public class SettingActivity extends AppCompatActivity {
    private FirebaseDatabase myFirebaseDatabase;
    private DatabaseReference myDatabaseReference;
    private ChildEventListener myChildEventListener;
    private FirebaseAuth mAuth;

    final int GET_GALLERY_IMAGE = 200;
    ImageView imageView;
    String imgName = "profile.png";

    TextView set_name;
    TextView set_std_number;
    String name_from_firebase;
    String id_from_firebase;

    TextView nav_name;
    TextView nav_std_number;

    ImageView univLogo;

    Boolean isClick = false;
    TextView contactUs;
    EditText contactUsText;
    ImageView sendBtn;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        contactUs = findViewById(R.id.contactUs);
        contactUsText = findViewById(R.id.contactUsText);
        sendBtn = findViewById(R.id.sendBtn);

        // ???????????? Textview
        contactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ???????????? EditText??? Send?????? ?????????/?????????
                isClick = !isClick;
                if (isClick){
                    contactUsText.setVisibility(View.VISIBLE);
                    sendBtn.setVisibility(View.VISIBLE);
                }else {
                    contactUsText.setVisibility(View.GONE);
                    sendBtn.setVisibility(View.GONE);
                }
            }
        });


        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sendBtn.getVisibility() == View.VISIBLE){
                    String complain = String.valueOf(contactUsText.getText());
                    Log.e("complain", complain);

                    Intent email = new Intent(Intent.ACTION_SEND);
                    email.setType("plain/text");
                    String[] address = {"sswu.univplanner@gmail.com"};
                    email.putExtra(Intent.EXTRA_EMAIL, address);
//                    email.putExtra(Intent.EXTRA_SUBJECT, "test@test");
                    email.putExtra(Intent.EXTRA_TEXT, complain);
                    startActivity(email);

                }
            }
        });

        univLogo = (ImageView) findViewById(R.id.assignment_univLogo);
        univLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        imageView = (ImageView) findViewById(R.id.imageView);

        set_name = (TextView) findViewById(R.id.std_name);
        set_std_number = (TextView) findViewById(R.id.std_num);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        String userInfo = user.getUid();
        Log.e("fb uid", userInfo);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://univp-1db5d-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference myRef2 = database.getReference("User").
                child(userInfo).child("name");

        // navigation bar??? ?????? ?????? ????????????
        myRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                name_from_firebase =  snapshot.getValue(String.class);
                Log.e("nav_std_name", name_from_firebase + "");
                set_name.setText(name_from_firebase);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {  }
        });

        // firebase?????? ?????? ??????(id) ????????????
        DatabaseReference myRef3 = database.getReference("User").
                child(userInfo).child("id");

        // navigation bar??? ?????? ?????? ????????????
        myRef3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                id_from_firebase =  snapshot.getValue(String.class);
                Log.e("nav_std_id", id_from_firebase + "");
                set_std_number.setText(id_from_firebase);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        try {
            String imgpath = getCacheDir() + "/" + imgName;   // ?????? ???????????? ???????????? ?????? ????????? ??????
            Bitmap bm = BitmapFactory.decodeFile(imgpath);
            imageView.setImageBitmap(bm);   // ?????? ???????????? ????????? ???????????? ??????????????? ???
            if(bm == null)
                imageView.setImageResource(R.drawable.profile);
        } catch (Exception e) {
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] versionArray = new String[] { "????????? ????????? ??????", "????????? ????????? ??????"};
                AlertDialog.Builder dlg = new AlertDialog.Builder(SettingActivity.this);
                dlg.setItems(versionArray,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if(which == 0){
                                    try {
                                        File file = getCacheDir();  // ??????????????? ?????? ????????? ????????????
                                        File[] flist = file.listFiles();
                                        for (int i = 0; i < flist.length; i++) {    // ????????? ???????????? ??????
                                            if (flist[i].getName().equals(imgName)) {   // ??????????????? ?????? ????????? ?????? ???????????? ????????? ??????
                                                flist[i].delete();  // ?????? ??????
                                            }
                                        }
                                    } catch (Exception e) {
                                    }
                                    imageView.setImageResource(R.drawable.profile);
                                }
                                else if(which==1){
                                    Intent intent = new Intent();
                                    intent.setType("image/*");
                                    intent.setAction(Intent.ACTION_GET_CONTENT);
                                    startActivityForResult(intent, GET_GALLERY_IMAGE);
                                }
                            }
                        });
                dlg.show();
            }
        });
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //App Bar??? ?????? ????????? Drawer??? Open ?????? ?????? Incon ??????
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_dehaze_24);

        DrawerLayout drawLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawLayout,
                toolbar,
                R.string.open,
                R.string.closed
        );


        drawLayout.addDrawerListener(actionBarDrawerToggle);

        //??????????????? ????????? ????????? ???, ???????????? ?????????
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId())
                {

                    case R.id.lecture:
                        Intent intent0 = new Intent(getApplicationContext(), check_lecture_Activity.class);
                        startActivity(intent0);
                        break;
                    case R.id.assignment:
                        Intent intent3 = new Intent(getApplicationContext(), check_assignment_Activity.class);
                        startActivity(intent3);
                        break;
                    case R.id.my_todo:
                        Intent intent4 = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent4);
                        break;
                    case R.id.setting:
                        Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.logout:
                        Intent intent2 = new Intent(getApplicationContext(), login.class);
                        startActivity(intent2);
                        finish();
                        break;
                }

                DrawerLayout drawer = findViewById(R.id.drawerLayout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        NavigationView navView= (NavigationView) findViewById(R.id.navigationView);
        View nav_view=navView.getHeaderView(0);
        nav_name = (TextView) nav_view.findViewById(R.id.nav_name);
        nav_std_number = (TextView) nav_view.findViewById(R.id.nav_std_number);


        // firebase?????? ?????? ?????? ????????????
        DatabaseReference myRef4 = database.getReference("User").
                child(userInfo).child("name");

        // navigation bar??? ?????? ?????? ????????????
        myRef4.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                name_from_firebase =  snapshot.getValue(String.class);
                Log.e("nav_std_name", name_from_firebase + "");
                nav_name.setText(name_from_firebase);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {  }
        });

        // firebase?????? ?????? ??????(id) ????????????
        DatabaseReference myRef5 = database.getReference("User").
                child(userInfo).child("id");

        // navigation bar??? ?????? ?????? ????????????
        myRef5.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                id_from_firebase =  snapshot.getValue(String.class);
                Log.e("nav_std_id", id_from_firebase + "");
                nav_std_number.setText(id_from_firebase);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_GALLERY_IMAGE) {
            if (resultCode == RESULT_OK) {
                Uri fileUri = data.getData();
                ContentResolver resolver = getContentResolver();
                try {
                    InputStream instream = resolver.openInputStream(fileUri);
                    Bitmap imgBitmap = BitmapFactory.decodeStream(instream);
                    imageView.setImageBitmap(imgBitmap);    // ????????? ????????? ??????????????? ???
                    instream.close();   // ????????? ????????????
                    saveBitmapToJpeg(imgBitmap);    // ?????? ???????????? ??????
                } catch (Exception e) {
                }

            }
        }

    }
    public void saveBitmapToJpeg(Bitmap bitmap) {   // ????????? ????????? ?????? ???????????? ??????
        File tempFile = new File(getCacheDir(), imgName);    // ?????? ????????? ?????? ??????
        try {
            tempFile.createNewFile();   // ???????????? ??? ????????? ????????????
            FileOutputStream out = new FileOutputStream(tempFile);  // ????????? ??? ??? ?????? ???????????? ????????????
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);   // compress ????????? ????????? ???????????? ???????????? ????????????
            out.close();    // ????????? ????????????
        } catch (Exception e) {
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawerLayout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}