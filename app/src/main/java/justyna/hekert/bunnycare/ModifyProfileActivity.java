package justyna.hekert.bunnycare;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import justyna.hekert.bunnycare.profiles.ProfileListContent;

public class ModifyProfileActivity extends AppCompatActivity {

    private String profilePicPath;
    final Calendar myCalendar = Calendar.getInstance();
    public static final int REQUEST_IMAGE_CAPTURE = 1; // request code for image capture
    public static final int REQUEST_GALLERY_PHOTO = 2;
    private String mCurrentPhotoPath; // String used to save the path of the picture

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);

        TextView camera_msg = findViewById(R.id.camera_msg);
        camera_msg.setVisibility(View.INVISIBLE);

        Intent intent = getIntent();
        if(intent !=null) {
            String oldPic = intent.getStringExtra(MainActivity.PicPathExtra);
            setImgPicPath(oldPic);
        }

        EditText dateTxt= (EditText) findViewById(R.id.modDate);
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        dateTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(ModifyProfileActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        FloatingActionButton fabCam = findViewById(R.id.fabCameraMod);
        fabCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Camera action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                TakePicture();
            }
        });

        FloatingActionButton fabGallery = findViewById(R.id.fabGalleryMod);
        fabGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (ActivityCompat.checkSelfPermission(ModifyProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(ModifyProfileActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_GALLERY_PHOTO);
                    }
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    galleryIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivityForResult(galleryIntent, REQUEST_GALLERY_PHOTO);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void updateLabel() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        EditText dateTxt= (EditText) findViewById(R.id.modDate);
        dateTxt.setText(sdf.format(myCalendar.getTime()));
    }


    public void TakePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            }catch (IOException ex){

            }

            if(photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, getString(R.string.myFileprovider), photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFIleName = ("ItemPic" + ProfileListContent.ITEMS.size()+1 )+ timeStamp + " ";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile( imageFIleName, ".jpg", storageDir );
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public String getRealPathFromUri(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = getContentResolver().query(contentUri, proj, null, null, null);
            assert cursor != null;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        FragmentActivity holdingActivity = this;
        TextView camera_msg = findViewById(R.id.camera_msg);

        if (holdingActivity != null) {
            if (requestCode == REQUEST_GALLERY_PHOTO && resultCode == RESULT_OK){
                Uri selectedImage = data.getData();
                String targetUri = getRealPathFromUri(selectedImage);

                ((ModifyProfileActivity) holdingActivity).setImgPicPath(targetUri);
                camera_msg.setText(R.string.camera_mod_msg);
                camera_msg.setVisibility(View.VISIBLE);
            } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
                ((ModifyProfileActivity) holdingActivity).setImgPicPath(mCurrentPhotoPath);
                camera_msg.setText(R.string.camera_mod_msg);
                camera_msg.setVisibility(View.VISIBLE);
            } else {
                ((ModifyProfileActivity) holdingActivity).PicTakenCancelled();
            }
        }
    }

    public void setImgPicPath (String val){
        profilePicPath = val;
    }

    public void PicTakenCancelled (){
        Intent data = new Intent();
        setResult(RESULT_CANCELED, data);
        finish();
    }

    public void modClick(View view) {
        Intent data = new Intent();

        EditText nameEditTxt = findViewById(R.id.modName);
        EditText dateEditTxt = findViewById(R.id.modDate);
        Spinner sexEditTxt = findViewById(R.id.modSex);
        EditText weightEditTxt = findViewById(R.id.modWeight);
        EditText typeEditTxt = findViewById(R.id.modType);
        EditText specialCharEditTxt = findViewById(R.id.modSpecChar);

        String name = nameEditTxt.getText().toString();
        String date = dateEditTxt.getText().toString();
        String sex = sexEditTxt.getSelectedItem().toString();
        String type = typeEditTxt.getText().toString();
        String specChar = specialCharEditTxt.getText().toString();

        boolean show_msg = false;
        String msg = "";

        //check values
        if(name.isEmpty()) {
            msg = getString(R.string.enter_name);
            show_msg = true;
        }
        else if (date.isEmpty()){
            msg = getString(R.string.enter_date);
            show_msg = true;
        }
        else if(weightEditTxt.getText().toString().isEmpty()) {
            msg = getString(R.string.enter_weight);
            show_msg = true;
        }

        if(show_msg) {
            Snackbar.make(view, msg, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return;
        }

        Double weight = Double.parseDouble(weightEditTxt.getText().toString());

        //default values
        if (type.isEmpty()) {
            type = getString(R.string.default_type);
        }
        if (specChar.isEmpty()) {
            specChar = getString(R.string.default_specChar);
        }

        Intent intent = getIntent();
        if(intent !=null) {
            int pos = intent.getIntExtra(MainActivity.PositionExtra, -1);
            if(pos != -1) {
                ProfileListContent.Profile profile = intent.getParcelableExtra(MainActivity.ProfileExtra);
                profile.name = name;
                profile.dateOfBirth = date;
                profile.weight = weight;
                profile.sex = sex;
                profile.type = type;
                profile.specChar = specChar;
                profile.picPath = profilePicPath;

                long id = ProfileListContent.modItem(pos, profile);

                MyBaseManager MBM = new MyBaseManager(this);
                MBM.modifyProfile(id, name,date, weight, sex, type, specChar, profilePicPath);
            }
        }

        //hide keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        setResult(RESULT_OK, data);
        finish();
    }
}
