package com.example.fittubertrial1;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private TextView adminId,adminPassword,notificationText,fileType;
    private String id,password,textMessage;
    private Button loginButton,sendNotification,selectFile,uploadFile;
    private RequestQueue mRequestQueue;
    private String URL = "https://fcm.googleapis.com/fcm/send";
    private FirebaseDatabase firebaseDatabase;
    private Uri fileUri;
    private ProgressDialog progressDialog;
    private boolean uploadedSucess;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //To remove status bar from login activity
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        /*View decorView=getWindow().getDecorView();
        int uiOptions=View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);*/

        //Toolbar variables
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Upload file variables here
        uploadFile=findViewById(R.id.uploadFile);
        selectFile=findViewById(R.id.selectFile);
        fileType=findViewById(R.id.fileType);

        //FireBase variables
        firebaseDatabase=FirebaseDatabase.getInstance();

        //Send Notification work here
        mRequestQueue = Volley.newRequestQueue(this);

        //Admin login variables
        adminId=(TextView) findViewById(R.id.adminLogin);
        adminPassword=(TextView)findViewById(R.id.adminPassword);
        loginButton=(Button)findViewById(R.id.loginButton);

        //Send notification buttons
        notificationText=(TextView)findViewById(R.id.notificationText);
        sendNotification=(Button)findViewById(R.id.sendNotification);

        //Admin Login Starts Here
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id=adminId.getText().toString();
                password=adminPassword.getText().toString();
                performLoginOperation(id,password);
            }
        });
    }
    protected void performLoginOperation(String id,String password)
    {
        if(id.equals("Humanity")&&password.equals("Thankyou@vivek"))
        {
            //To make buttons and text fields visible and unvisible
            adminId.setVisibility(View.INVISIBLE);
            adminPassword.setVisibility(View.INVISIBLE);
            loginButton.setVisibility(View.INVISIBLE);
            notificationText.setVisibility(View.VISIBLE);
            sendNotification.setVisibility(View.VISIBLE);
            uploadFile.setVisibility(View.VISIBLE);
            selectFile.setVisibility(View.VISIBLE);
            fileType.setVisibility(View.VISIBLE);

            //Send Notification work starts here
            sendNotification.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseMessaging.getInstance().subscribeToTopic("news");
                    sendNotification();
                }
            });

            //If user press file upload
            selectFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)
                    {
                        selectFile();
                    }
                    else
                    {
                        ActivityCompat.requestPermissions(LoginActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},9);
                    }
                }
            });

            //Upload button code
            uploadFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(fileUri!=null)
                    {
                        uploadFileToFirebase(fileUri);
                    }
                    else
                    {
                        Toast.makeText(LoginActivity.this, "Please Select File", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
        else
        {
            Toast.makeText(this,"Login id Or Login password is wrong",Toast.LENGTH_SHORT).show();
        }

        //Make User id and password invisible
        adminId.setText("");
        adminPassword.setText("");
    }

    //This method is used to upload file on firebase
    private void uploadFileToFirebase(Uri fileUri) {
        progressDialog=new ProgressDialog(LoginActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("Uploading file...");
        progressDialog.setProgress(0);
        progressDialog.show();
        try {
            InputStream inputStream = getContentResolver().openInputStream(fileUri);
            InputStreamReader isReader = new InputStreamReader(inputStream);
            BufferedReader lineReader = new BufferedReader(isReader);
            String lineText = null;
            lineReader.readLine();
            HashMap<String, Object> map = new HashMap<>();
            uploadedSucess=true;
            while ((lineText = lineReader.readLine()) != null) {
                String[] data = lineText.split(",");
                int n=data.length;
                if(n<3)
                    continue;
                /*if(data[0]==null||data[1]==null||data[2]==null)
                    continue;*/
                String comment=data[0];
                String replyer=data[1];
                String reply=data[2];
                map.put("comment", comment);
                map.put("replyer", replyer);
                map.put("reply", reply);
                firebaseDatabase.getReference().child("Post").push()
                        .setValue(map)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                //Toast.makeText(this, "Please Select File", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                uploadedSucess=false;
                                Toast.makeText(LoginActivity.this,"File Not Successfully Uploaded",Toast.LENGTH_SHORT).show();
                            }
                        });
            }
            if(uploadedSucess) {
                Toast.makeText(LoginActivity.this, "File Successfully Uploaded", Toast.LENGTH_SHORT).show();
                progressDialog.setProgress(100);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    //This method is invoked when permission is given or denied by user(Invoked automatically)
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==9&&grantResults[0]==PackageManager.PERMISSION_GRANTED)
            selectFile();
        else
            Toast.makeText(this,"Unable to select file, please try again",Toast.LENGTH_SHORT).show();
    }

    //Select File button pressed
    private void selectFile()
    {
        Intent intent=new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,86);

    }

    @Override
    //This method is invoked automatically after selectFile()
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 86 && data != null && resultCode == RESULT_OK) {
            fileUri = data.getData();
            fileType.setText("A File is selected:"+data.getData().getLastPathSegment());
        } else {
            Toast.makeText(this, "Please Select File", Toast.LENGTH_SHORT).show();
        }
    }

    //Send Notification work is here
    private void sendNotification() {
        JSONObject mainObj = new JSONObject();
        try {
            mainObj.put("to", "/topics/" + "news");
            JSONObject notificationObj = new JSONObject();
            textMessage = notificationText.getText().toString();
            notificationObj.put("title", "Fit Tuber");
            notificationObj.put("body", textMessage);
            mainObj.put("notification", notificationObj);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL,
                    mainObj,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> header = new HashMap<>();
                    header.put("content-type", "application/json");
                    header.put("authorization", "key=AAAAuo6349Q:APA91bF0OzNAiOZ2ek90bv746newb4iaoQHGUZJJt7L9MBtcfWDVcdoGvfRxFyUWSZKIZ__i4UqOhxeTwXYPhreUaAk5qMxIsHkG7qKeII76LmILfzyA2ZrogW3HQCmXpUCisUsrfNYs");
                    return header;
                }
            };
            mRequestQueue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
