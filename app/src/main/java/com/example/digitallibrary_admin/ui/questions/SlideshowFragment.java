package com.example.digitallibrary_admin.ui.questions;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.digitallibrary_admin.R;
import com.example.digitallibrary_admin.databinding.FragmentSlideshowBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.jetbrains.annotations.NotNull;

import static android.app.Activity.RESULT_OK;

public class SlideshowFragment extends Fragment {

    ImageView pdf, openfile, cancel;
    EditText year, sem;
    TextView submit;
    Uri filepath;


    StorageReference storageReference;
    DatabaseReference databaseReference;

    private FragmentSlideshowBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentSlideshowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("questions");

        pdf = root.findViewById(R.id.pdficonQuestion);
        openfile = root.findViewById(R.id.openquestion);
        cancel = root.findViewById(R.id.cancelquestion);


        year = root.findViewById(R.id.questionYear);
        sem = root.findViewById(R.id.questionSem);


        submit = root.findViewById(R.id.submitquestion);

        pdf.setVisibility(View.INVISIBLE);
        cancel.setVisibility(View.INVISIBLE);


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pdf.setVisibility(View.INVISIBLE);
                cancel.setVisibility(View.INVISIBLE);
                openfile.setVisibility(View.VISIBLE);


            }
        });


        openfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withContext(getContext()).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                Intent intent = new Intent();
                                intent.setType("application/pdf");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(Intent.createChooser(intent, "select a pdf file.."), 69);

                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                permissionToken.continuePermissionRequest();
                            }
                        }).check();
            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processupload(filepath);

            }
        });



        return root;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 69 && resultCode == RESULT_OK) {
            filepath = data.getData();

            pdf.setVisibility(View.VISIBLE);
            cancel.setVisibility(View.VISIBLE);
            openfile.setVisibility(View.INVISIBLE);


        } else {
            Toast.makeText(getContext(), "pdf not selected", Toast.LENGTH_LONG).show();
        }


    }


    private void processupload(Uri filepath) {


        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("File uploading....");
        progressDialog.show();

        StorageReference reference = storageReference.child("notes/" + System.currentTimeMillis() + ".pdf");


        reference.putFile(filepath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        questionmodel obj = new questionmodel(uri.toString(),sem.getText().toString(),year.getText().toString());
                        databaseReference.child(databaseReference.push().getKey()).setValue(obj);
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "File Uploaded", Toast.LENGTH_LONG).show();

                        pdf.setVisibility(View.INVISIBLE);
                        cancel.setVisibility(View.INVISIBLE);
                        openfile.setVisibility(View.VISIBLE);
                        sem.setText("");
                        year.setText("");
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull @NotNull UploadTask.TaskSnapshot snapshot) {
                float per = (100 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                progressDialog.setMessage("Uploading:" + (int) per + "%");

            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}