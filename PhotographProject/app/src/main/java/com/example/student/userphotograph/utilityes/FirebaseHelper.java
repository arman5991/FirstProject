package com.example.student.userphotograph.utilityes;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.student.userphotograph.models.Pictures;
import com.example.student.userphotograph.models.PostModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import static com.example.student.userphotograph.utilityes.Constants.NAME;
import static com.example.student.userphotograph.utilityes.Constants.PHOTOGRAPHS;
import static com.example.student.userphotograph.utilityes.Constants.POST;
import static com.example.student.userphotograph.utilityes.Constants.USER_ID;

public class FirebaseHelper {

    private static String name;
    private static String uid;
    private static DatabaseReference databaseRef;

    public static void downloadImageAndSetAvatar(final StorageReference ref, final ImageView img) {
        ref.getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                img.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    public static String getFileExtension(Uri uri, Activity activity) {
        ContentResolver contentResolver = activity.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public static void upload(final Context context,
                              final String imageName,
                              final EditText titlePhoto,
                              final DatabaseReference databaseGalleryRef,
                              StorageReference storageGalleryRef,
                              Uri filePath) {
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Uploading");
        progressDialog.show();
        StorageReference sRef = storageGalleryRef.child(imageName);
        sRef.putFile(filePath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        Toast.makeText(context, "File Uploaded ", Toast.LENGTH_LONG).show();

                        @SuppressWarnings("VisibleForTests")

                        Pictures picture = new Pictures(titlePhoto.getText().toString().trim(), taskSnapshot.getDownloadUrl().toString(), imageName);

                        String uploadId = databaseGalleryRef.push().getKey();
                        databaseGalleryRef.child(uploadId).setValue(picture);
                    }
                })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        progressDialog.dismiss();
                        Toast.makeText(context.getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();

                        Toast.makeText(context, "Warning !!!, Error file ", Toast.LENGTH_LONG).show();
                        titlePhoto.setText("");
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        @SuppressWarnings("VisibleForTests")
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                    }
                });
    }

    public static void uploadPost(final Context context,
                                  final String imageName,
                                  final EditText titlePhoto,
                                  StorageReference storagePostRef,
                                  final DatabaseReference databasePostRef,
                                  Uri filePath) {
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Uploading");
        progressDialog.show();
        StorageReference sRef = storagePostRef.child(imageName);
        sRef.putFile(filePath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        Toast.makeText(context, "File Uploaded ", Toast.LENGTH_LONG).show();
                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        FirebaseUser mUser = auth.getCurrentUser();
                        assert mUser != null;
                        databaseRef = FirebaseDatabase.getInstance().getReference().child(PHOTOGRAPHS).child(mUser.getUid());
                        databaseRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                name = dataSnapshot.child(NAME).getValue(String.class);
                                uid = dataSnapshot.child(USER_ID).getValue(String.class);

                                @SuppressWarnings("VisibleForTests")
                                PostModel postModel = new PostModel(taskSnapshot.getDownloadUrl().toString(), titlePhoto.getText().toString().trim(), imageName, name, uid);
                                long data = postModel.getDate();
                                databasePostRef.child(POST).child(String.valueOf(data)).setValue(postModel);
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                            }
                        });
                    }
                })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        progressDialog.dismiss();
                        Toast.makeText(context.getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        Toast.makeText(context, "Warning !!!, Error file ", Toast.LENGTH_LONG).show();
                        titlePhoto.setText("");
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        @SuppressWarnings("VisibleForTests")
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                    }
                });
    }
}
