package com.genius.appdesign2;

import static android.content.ContentValues.TAG;
//import static com.genius.appdesign2.LyoActivity.arrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

//import com.genius.appdesign2.data.DataJob;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.firestore.DocumentReference;
//import com.google.firebase.firestore.auth.User;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
import android.net.Uri;
//import android.os.AsyncTask;
import android.os.Bundle;
import android.os.FileUtils;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
//import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
//import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
//import android.widget.TextView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.os.Handler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class Activity_profile extends AppCompatActivity {

    private EditText tvUsename, tvMob, tvPassword,tvPassword2,tvemail,tvfname,tvlname;
    private ImageView tvUrl;
    private Button button;
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private static final int SELECT_PICTURE = 1;
    String mob,username,password,email,fname,lname,id,url2,password_match,userid_match,get_url;
   Toast tst;
    private static final int PICK_IMAGE = 100;
    private StorageReference mChatPhotosStorageReference;
    private FirebaseStorage mFirebaseStorage;
    private ProgressDialog progressDialog;
    private ProgressBar progressBar;
    Uri imageUri;
    Handler handle;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser =   firebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null){
            reload();
        }
    }

    @Override
    public void onBackPressed(){
        Intent returnIntent = new Intent();
        Intent i =  new Intent(Activity_profile.this, AccountActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().hide();


        String email_id=getIntent().getStringExtra("email_id");

        tvMob =findViewById(R.id.activity_profile_tvMob);
        tvUsename = findViewById(R.id.activity_profile_tvUsername);
        tvPassword = findViewById(R.id.activity_profile_tvPassword);
        tvPassword2 = findViewById(R.id.activity_profile_tvPassword);
        tvemail = findViewById(R.id.activity_profile_tvemail);
        tvfname = findViewById(R.id.activity_profile_f_name);
        tvlname = findViewById(R.id.activity_profile_l_name);
        tvlname.setMovementMethod(new ScrollingMovementMethod());
        tvemail.setEnabled(false);
        progressDialog = new ProgressDialog(this);
        progressBar = new ProgressBar (this);
        //reference = FirebaseDatabase.getInstance().getReference("users");
        mob = tvMob.getText().toString();
        username = tvUsename.getText().toString();
        password = tvPassword.getText().toString();
        email = tvemail.getText().toString();
        fname = tvfname.getText().toString();
        lname = tvlname.getText().toString();
        tvUrl = (ImageView) findViewById(R.id.activity_profile_tvimage);
        button = findViewById(R.id.activity_profile_bUpdate);
        final ImageView iv = (ImageView) findViewById(R.id.activity_profile_tvimage);
        ImageView imageView1 = findViewById(R.id.activity_profile_imageview1);
        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                    Intent returnIntent = new Intent();
                     Intent i =  new Intent(Activity_profile.this, AccountActivity.class);
                     i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                     startActivity(i);
                     finish();
            }
        });
        ImageView img_change = findViewById(R.id.activity_profile_tvChange_image);
        img_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //openGallery();
                requestStoragePermission();
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        mFirebaseStorage = FirebaseStorage.getInstance();
        mChatPhotosStorageReference = mFirebaseStorage.getReference().child("users").child(email_id); // users is the folder name in firebase consol

        if (firebaseAuth.getCurrentUser() != null) {
            firestore.collection("users")
                    .whereEqualTo("email", firebaseAuth.getCurrentUser().getEmail())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if (document.getData().get("email") != null && document.getData().get("username") != null) {
                                        //tvEmail.setText("Email ID : " + document.getData().get("email").toString());
                                        // tvUsename.setText("Username : " + document.getData().get("username").toString());
                                        tvMob.setText(document.getData().get("phone").toString());
                                        id =document.getId();
                                        tvUsename.setText(document.getData().get("username").toString());
                                        tvemail.setText(document.getData().get("email").toString());
                                        tvfname.setText(document.getData().get("firstName").toString());
                                        tvlname.setText(document.getData().get("lastName").toString());
                                        tvPassword.setText(document.getData().get("password").toString());
                                        password_match=document.getData().get("password").toString();
                                        userid_match=document.getData().get("username").toString();
                                        url2 = document.getData().get("url").toString();
                                        //new DownLoadImageTask(iv).execute(url2);
                                        Picasso.get().load(url2).into(iv);

                                    } else {

                                        Log.e("Account", "Missing Parameters");
                                    }
                                }
                            } else {
                                Log.e("HomeActivity", "Error");
                            }
                        }
                    });
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              // String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&-+=()])(?=\\S+$).{8,20}$";
                if (firebaseAuth.getCurrentUser() != null) {

                    if (tvUsename.getText().toString().trim().length() <= 0) {
                        Toast.makeText(Activity_profile.this,"Username can not be empty",Toast.LENGTH_SHORT).show();
                    }
                   else if (tvMob.getText().toString().trim().length() < 10) {
                        Toast.makeText(Activity_profile.this,"Mobile No should be 10 digits",Toast.LENGTH_SHORT).show();
                    }

                   else if (tvPassword.getText().toString().trim().length() < 6) {
                        Toast.makeText(Activity_profile.this,"Password should be 6 digits",Toast.LENGTH_SHORT).show();
                    }
                    else if (tvfname.getText().toString().trim().length() <= 0) {
                        Toast.makeText(Activity_profile.this,"First name can not be empty",Toast.LENGTH_SHORT).show();
                    }
                   else if (tvlname.getText().toString().trim().length() <= 0) {
                        Toast.makeText(Activity_profile.this,"Last name can not be empty",Toast.LENGTH_SHORT).show();
                    }
                    else if (!tvPassword.getText().toString().matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$")) {
                        Toast.makeText(Activity_profile.this,"Password Must contain 1 Capital,1 small, 1 number, 1 special, atlest 8",Toast.LENGTH_SHORT).show();
                    }
                    /* else
                    {
                       if (!tvUsename.getText().toString().trim().equals(userid_match)) {

                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful()){

                                        List<String> currentUserNamePresent = new ArrayList<>();
                                        for(QueryDocumentSnapshot singleDocument: task.getResult()){
                                            currentUserNamePresent.add((String) singleDocument.getData().get("username"));
                                        }

                                        if (!currentUserNamePresent.contains(tvUsename.getText().toString())){
                                            Toast.makeText(getApplicationContext(), "Not contains", Toast.LENGTH_SHORT).show();
                                        }
                                    }else{
                                        Log.e("Error: ",task.getException().toString());
                                    }
                                }
                            });
                        }
                        else{
                        Toast.makeText(Activity_profile.this,"Same users",Toast.LENGTH_SHORT).show();
                    }



                    }*/

                    else {

                        if (tvPassword.getText().toString().trim().equals(password_match)) {
                            firestore.collection("users").document(id).update(
                                    "phone", tvMob.getText().toString().trim(),
                                    "firstName", tvfname.getText().toString().trim(), "lastName", tvlname.getText().toString().trim())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            checkusername();
                                            tst = Toast.makeText(Activity_profile.this, "Data Updated Successfully", Toast.LENGTH_LONG);
                                            tst.show();
                                            //Toast.makeText(Activity_profile.this, "Data Updated Successfully", Toast.LENGTH_SHORT).show();
                                        }

                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(Activity_profile.this, "Error writing document", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }

                        else
                        {

                                            firebaseAuth.getInstance().getCurrentUser().updatePassword(tvPassword.getText().toString().trim())
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {


                                                            if (task.isSuccessful()) {
                                                                FirebaseUser user = firebaseAuth.getInstance().getCurrentUser();
                                                                updateUI(user);

                                                                firestore.collection("users").document(id).update("password", tvPassword.getText().toString().trim(),
                                                                        "phone", tvMob.getText().toString().trim(),
                                                                        "firstName", tvfname.getText().toString().trim(),"lastName", tvlname.getText().toString().trim())
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {
                                                                                checkusername();
                                                                                Toast.makeText(Activity_profile.this, "Password updated Successfully", Toast.LENGTH_SHORT).show();
                                                                            }

                                                        })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(Activity_profile.this, "Error writing document", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });

                                                            }
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    //Toast.makeText(Activity_profile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    new AlertDialog.Builder(Activity_profile.this)
                                                            .setTitle("Confirm")
                                                            .setMessage("You need to Re- Login Your account. Are you ready to Logout Now")
                                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                                                public void onClick(DialogInterface dialog, int whichButton) {
                                                                    firebaseAuth.signOut();
                                                                    finish();

                                                }})
                                                    .setNegativeButton(android.R.string.no,new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog,int id) {
                                                            // if this button is clicked, just close
                                                            // the dialog box and do nothing
                                                            dialog.cancel();
                                                        }
                                                    }).show();
                                                }
                                            });

                        }
                       /* firestore.collection("users").document(id).update("password", tvPassword.getText().toString().trim(),
                                "username", tvUsename.getText().toString().trim(),
                                "phone", tvMob.getText().toString().trim())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        firebaseAuth.getInstance().getCurrentUser().updatePassword(tvPassword.getText().toString().trim())
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        if (task.isSuccessful()) {
                                                            FirebaseUser user = firebaseAuth.getInstance().getCurrentUser();
                                                            updateUI(user);
                                                            Toast.makeText(Activity_profile.this, "Data has been updated", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(Activity_profile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                Log.d("ERROR", e.getMessage());
                                            }
                                        });
                                    }

                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(Activity_profile.this, "Error writing document", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    */
                    }

                }
            }
        });
    }
  /*  private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }*/


    public void checkusername()
    {
      if (!tvUsename.getText().toString().trim().equals(userid_match)) {

          FirebaseFirestore db = FirebaseFirestore.getInstance();
          db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
              @Override
              public void onComplete(@NonNull Task<QuerySnapshot> task) {
                  if (task.isSuccessful()) {

                      List<String> currentUserNamePresent = new ArrayList<>();
                      for (QueryDocumentSnapshot singleDocument : task.getResult()) {
                          currentUserNamePresent.add((String) singleDocument.getData().get("username"));
                      }

                      if (!currentUserNamePresent.contains(tvUsename.getText().toString())) {

                          firestore.collection("users").document(id).update(
                                  "username", tvUsename.getText().toString().trim())
                                  .addOnSuccessListener(new OnSuccessListener<Void>() {
                                      @Override
                                      public void onSuccess(Void aVoid) {
                                          //Toast.makeText(Activity_profile.this, "Data Updated Successfully", Toast.LENGTH_SHORT).show();
                                      }

                                  })
                                  .addOnFailureListener(new OnFailureListener() {
                                      @Override
                                      public void onFailure(@NonNull Exception e) {
                                          Toast.makeText(Activity_profile.this, "Error writing document", Toast.LENGTH_SHORT).show();
                                      }
                                  });


                         // Toast.makeText(getApplicationContext(), "Data Updated Successfully", Toast.LENGTH_SHORT).show();
                      }
                  else {
                        tst.cancel();
                      Toast.makeText(Activity_profile.this, "Username Already Exists", Toast.LENGTH_SHORT).show();
                  }}
              }
          });
      }
    }


    @TargetApi(16)
    private void requestStoragePermission() {
        Dexter.withActivity(Activity_profile.this)
                .withPermissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {


//                            Intent intent = new Intent();
//                            intent.setType("image/jpeg");
//                            intent.setAction(Intent.ACTION_GET_CONTENT);
//                            Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
//                            startActivityForResult(gallery, PICK_IMAGE);
                            Intent gallery = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(gallery, PICK_IMAGE);

                           // startActivityForResult(Intent.createChooser(gallery, "Select Picture"), SELECT_PICTURE);
                        }
                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            showSettingsDialog();
                        }
                    }
                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Error occurred! " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    private void showSettingsDialog() {
        android.app.AlertDialog.Builder builder = new AlertDialog.Builder(Activity_profile.this);
        builder.setTitle("Need Permissions");
        builder.setMessage(R.string.permission_upload);
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                //openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {


            new AlertDialog.Builder(this)
                    .setTitle("Confirm")
                    .setMessage("Do you really want to update your Profile Picture?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                           // Toast.makeText(Activity_profile.this, "yes", Toast.LENGTH_SHORT).show();
                            //.show();
                            //progressDialog.setMax(100);
                            //progressDialog.setMessage("Uploading Profile....");
                            //progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                            //progressDialog.setIndeterminate(true);
//                            progressBar.setVisibility(View.VISIBLE);
                           progressDialog.setTitle("Uploading...");
                           progressDialog.show();


                           /* progressDialog.setMessage("Downloading Music");
                            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                            progressDialog.setIndeterminate(true);
                            progressDialog.setProgress(0);
                            progressDialog.show();

                            final int totalProgressTime = 100;
                            final Thread t = new Thread() {
                                @Override
                                public void run() {
                                    int jumpTime = 0;

                                    while(jumpTime < totalProgressTime) {
                                        try {
                                            sleep(200);
                                            jumpTime += 5;
                                            progressDialog.setProgress(jumpTime);
                                        } catch (InterruptedException e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            };
                            t.start();
*/

                            imageUri = data.getData();
                            tvUrl.setImageURI(imageUri);
                            Uri uri=data.getData();
                            progressDialog.incrementProgressBy(1);
                            StorageReference filepath=mChatPhotosStorageReference.child(uri.getLastPathSegment());



                            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    //get_url = filepath.getDownloadUrl().toString();
                                    Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                                    while(!uri.isComplete());
                                    Uri ur = uri.getResult();

                                    firestore.collection("users").document(id).update("url",ur.toString().trim())
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(Activity_profile.this, "Image updated Successfully", Toast.LENGTH_SHORT).show();
                                                   progressDialog.dismiss();

                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(Activity_profile.this, "Error uploading image", Toast.LENGTH_SHORT).show();
                                                    //Log.w(TAG, "Error writing document", e);
                                                }
                                            });
                                }
                            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                    double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                                    progressDialog.setMessage("Uploaded " + (int) progress + "%");


                                }

                            });


                        }

                    })

                    .setNegativeButton(android.R.string.no,new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            dialog.cancel();

                        }
                    }).show();

        }
    }
    private void reload() { }
    private void updateUI(FirebaseUser user) {

    }
   /* private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }*/

}

