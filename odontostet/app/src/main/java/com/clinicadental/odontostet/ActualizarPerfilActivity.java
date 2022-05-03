package com.clinicadental.odontostet;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.OnProgressListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ActualizarPerfilActivity extends AppCompatActivity {

    private ActivityResultLauncher<Intent> photoPickerActivity;
    private ActivityResultLauncher<Intent> cameraActivity;

    private MaterialDialog progressDialog;

    private Button btnRegresar;
    private ImageView profileImg;
    private FloatingActionButton updateImg;
    private EditText txtNombres;
    private EditText txtApellidos;
    private EditText txtEmail;
    private EditText txtContra;
    private Button btnActualizar;

    private String userId;

    private String names;
    private String lastnames;
    private String password;
    private String email;

    private Long fecha;

    private String currentImgUrl;

    private final int READ_STORAGE_PERMISSION = 1;
    private final int CAMERA_PERMISSION = 2;
    private final int WRITE_STORAGE_PERMISSION = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        userId = FirebaseAuth.getInstance().getUid();

        getPhotoUrl();

        initUI();

        createIntents();

        cargarPerfil();
    }

    private void createIntents() {
        photoPickerActivity = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        processImage(result.getData());
                    }
                }
        );

        cameraActivity = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            uploadPhoto(result.getData().getData());
                        }
                    }
                }
        );
    }

    private void initUI() {
        btnRegresar = findViewById(R.id.btnRegresar);
        btnRegresar.setOnClickListener(view -> onBackPressed());

        profileImg = findViewById(R.id.profileImg);

        updateImg = findViewById(R.id.updateImg);
        updateImg.setOnClickListener(view -> seleccionarImagen());

        txtNombres = findViewById(R.id.txtNombres);
        txtApellidos = findViewById(R.id.txtApellidos);
        txtEmail = findViewById(R.id.txtEmail);
        txtContra = findViewById(R.id.txtContra);

        btnActualizar = findViewById(R.id.btnActualizar);
        btnActualizar.setOnClickListener(view -> actualizarPerfil());
    }

    private void cargarPerfil() {
        FirebaseFirestore.getInstance().collection("Personas").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful() && task.getResult().exists()) {
                    names = task.getResult().getString("Nombres");
                    lastnames = task.getResult().getString("Apellidos");
                    email = task.getResult().getString("Correo ");
                    fecha = task.getResult().getLong("Fecha");

                    password = task.getResult().getString("Contra");

                    setValues();
                }
            }
        });
    }

    public void showProgress(String title, String message) {
        try {
            progressDialog = new MaterialDialog.Builder(this)
                    .backgroundColor(ContextCompat.getColor(this, R.color.blanco))
                    .title(title)
                    .content(message)
                    .progress(true, 0)
                    .progressIndeterminateStyle(false)
                    .build();
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hideProgress() {
        if (progressDialog != null)
            progressDialog.dismiss();
    }

    private void seleccionarImagen() {
        openSelector();
    }

    private void actualizarPerfil() {
        showProgress("Actualizando", "Espere por favor.");
        if (!TextUtils.isEmpty(txtNombres.getText().toString().trim())) {
            if (!TextUtils.isEmpty(txtApellidos.getText().toString().trim())) {
                if (!TextUtils.isEmpty(txtContra.getText().toString().trim())) {

                    Map<String, Object> map = new HashMap<>();
                    String newNames = txtNombres.getText().toString().trim();
                    String newLastnames = txtApellidos.getText().toString().trim();
                    String newPassword = txtContra.getText().toString().trim();

                    map.put("Nombres", newNames);
                    map.put("Apellidos", newLastnames);
                    map.put("Contra", newPassword);


                    FirebaseFirestore.getInstance().collection("Personas").document(userId).update(map).addOnCompleteListener(task -> showAlert("Actualizado", "Perfil actualizado correctamente."));
                }
                else {
                    showAlert("Campo vacío", "Debe ingresar una contraseña");
                }
            }
            else {
                showAlert("Campo vacío", "Debe ingresar un apellido");
            }
        }
        else {
            showAlert("Campo vacío", "Debe ingresar un nombre");
        }
    }

    private void setValues() {
        txtNombres.setText(names);
        txtApellidos.setText(lastnames);
        txtEmail.setText(email);
        txtContra.setText(password);

        if (!TextUtils.isEmpty(currentImgUrl)) {
            Glide.with(this).load(currentImgUrl).into(profileImg);
        }
    }

    private void showAlert(String title, String message) {
        hideProgress();
        new MaterialDialog.Builder(this)
                .backgroundColor(ContextCompat.getColor(this, R.color.blanco))
                .title(title)
                .content(message)
                .positiveText("Aceptar")
                .show();
    }

    private void openSelector() {
        new MaterialDialog.Builder(this)
                .backgroundColor(ContextCompat.getColor(this, R.color.white))
                .title("Selecciona")
                .content("¿De donde quieres elegir la imagen?")
                .positiveText("Cámara")
                .positiveColor(ContextCompat.getColor(this, R.color.camera))
                .onPositive((dialog, which) -> openCamera())
                .negativeText("Galería")
                .negativeColor(ContextCompat.getColor(this, R.color.gallery))
                .onNegative((dialog, which) -> openGallery())
                .neutralText("Cancelar")
                .neutralColor(ContextCompat.getColor(this, R.color.cancel))
                .show();
    }

    private void openCamera() {
        if (hasPermission(CAMERA_PERMISSION)) {
            goToCamera();
        }
        else {
            requestPermission(CAMERA_PERMISSION);
        }
    }

    private void openGallery() {
        if (hasPermission(READ_STORAGE_PERMISSION)) {
            Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            //pickIntent.setType("image/*");
            photoPickerActivity.launch(pickIntent);
        }
        else {
            requestPermission(READ_STORAGE_PERMISSION);
        }
    }

    public boolean hasPermission(int permissionType) {
        String permission = "";
        switch (permissionType) {
            case READ_STORAGE_PERMISSION:
                permission = Manifest.permission.READ_EXTERNAL_STORAGE;
                break;

            case WRITE_STORAGE_PERMISSION:
                permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
                break;

            case CAMERA_PERMISSION:
                permission = Manifest.permission.CAMERA;
                break;
        }

        return !TextUtils.isEmpty(permission) && ContextCompat.checkSelfPermission(getApplicationContext(), permission) == PackageManager.PERMISSION_GRANTED;
    }

    private void processImage(Intent data) {
        if (data != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            if (selectedImage != null) {
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                if (cursor != null) {
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);

                    profileImg.setImageBitmap(BitmapFactory.decodeFile(picturePath));

                    cursor.close();

                    uploadPhoto(selectedImage);
                }
                else {

                }
            }
            else {

            }
        }
    }

    private void requestPermission(int permission) {
        String permissionName = "";
        int requestCode = 0;

        switch (permission) {
            case READ_STORAGE_PERMISSION:
                requestCode = READ_STORAGE_PERMISSION;
                permissionName = Manifest.permission.READ_EXTERNAL_STORAGE;
                break;

            case CAMERA_PERMISSION:
                requestCode = CAMERA_PERMISSION;
                permissionName = Manifest.permission.CAMERA;
                break;

            case WRITE_STORAGE_PERMISSION:
                requestCode = WRITE_STORAGE_PERMISSION;
                permissionName = Manifest.permission.WRITE_EXTERNAL_STORAGE;
                break;
        }

        if (!TextUtils.isEmpty(permissionName)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{permissionName}, requestCode);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        boolean isPermissionGranted = (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED);

        switch (requestCode) {
            case READ_STORAGE_PERMISSION:
                if (isPermissionGranted) {
                    openGallery();
                }
                else {
                    boolean showRationale;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        showRationale = shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE);

                        showPermissionRequestExplain(READ_STORAGE_PERMISSION, !showRationale);
                    }
                }
                break;

            case CAMERA_PERMISSION:
                if (isPermissionGranted) {
                    goToCamera();
                }
                else {
                    boolean showRationale;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        showRationale = shouldShowRequestPermissionRationale(Manifest.permission.CAMERA);

                        showPermissionRequestExplain(CAMERA_PERMISSION, !showRationale);
                    }
                }
                break;

            /*
            case WRITE_STORAGE_PERMISSION:
                if (isPermissionGranted) {
                    saveImage();
                }
                else {

                }
                break;

            */
        }
    }

    private void showPermissionRequestExplain(int permissionType, boolean isRejected) {
        String message = "";

        MaterialDialog.SingleButtonCallback callback = null;

        switch (permissionType) {
            case READ_STORAGE_PERMISSION:
                message = getString(R.string.read_permission_explain);

            case CAMERA_PERMISSION:
                message = getString(R.string.camera_permission_explain);
                break;
        }

        callback = (dialog, which) -> {
            if (isRejected) {
                openSettings();
            }
            else {
                requestPermission(permissionType);
            }
        };

        new MaterialDialog.Builder(this)
                .backgroundColor(ContextCompat.getColor(this, R.color.white))
                .title("Aviso")
                .content(message)
                .contentColor(ContextCompat.getColor(this, R.color.negro))
                .positiveText("Aceptar")
                .positiveColor(ContextCompat.getColor(this, R.color.accept))
                .onPositive(callback)
                .negativeText("Cancelar")
                .negativeColor(ContextCompat.getColor(this, R.color.cancel))
                .show();

    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", getPackageName(), null));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void uploadPhoto(Uri uri) {
        showProgress("Actualizando Foto", "Espere por favor");
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();

        StorageReference ref = storageReference.child("images/" + userId + ".jpg");

        ref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String photoUrl = uri.toString();

                        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                .setPhotoUri(uri)
                                .build();

                        FirebaseAuth.getInstance().getCurrentUser().updateProfile(profileUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    showAlert("Completado", "Foto actualizada correctamente");
                                    refreshPhoto();
                                }
                                else {
                                    showAlert("Error", "Error al actualizar la foto. Favor de reintentar.");
                                }
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showAlert("Error", "Error al actualizar la foto. Favor de reintentar.");
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showAlert("Error", "Error al actualizar la foto. Favor de reintentar.");
            }
        });
    }

    private void goToCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri imageUri = getImageUri();
        //cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        cameraActivity.launch(cameraIntent);

        /*
        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            //pickIntent.setType("image/*");
            photoPickerActivity.launch(pickIntent);
         */
    }

    private Uri getImageUri(){
        Uri m_imgUri = null;
        File m_file;
        try {
            SimpleDateFormat m_sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String m_curentDateandTime = m_sdf.format(new Date());
            String m_imagePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + m_curentDateandTime + ".jpg";
            m_file = new File(m_imagePath);
            m_imgUri = Uri.fromFile(m_file);
        } catch (Exception p_e) {

        }
        return m_imgUri;
    }

    private void getPhotoUrl() {
        Uri photoUri = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl();

        if (photoUri != null) {
            currentImgUrl = photoUri.toString();
        }
    }

    private void refreshPhoto() {
        getPhotoUrl();

        if (!TextUtils.isEmpty(currentImgUrl)) {
            Glide.with(this).load(currentImgUrl).into(profileImg);
        }
    }
}
