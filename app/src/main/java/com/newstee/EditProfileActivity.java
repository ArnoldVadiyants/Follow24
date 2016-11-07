package com.newstee;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.newstee.helper.InternetHelper;
import com.newstee.helper.SQLiteHandler;
import com.newstee.model.data.DataUpdateUser;
import com.newstee.model.data.User;
import com.newstee.model.data.UserLab;
import com.newstee.network.FactoryApi;
import com.newstee.utils.DisplayImageLoaderOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Arnold on 07.04.2016.
 */
public class EditProfileActivity extends AppCompatActivity{
    private static final int SELECT_PICTURE = 1;
private static final String TAG = "EditProfileActivity";
    private String selectedAvatarPath="";
    private String imgPath;
    private ImageType imageType = ImageType.Avatar;
    private byte[]bytes;
    private SQLiteHandler db;
    ImageLoader imageLoader = ImageLoader.getInstance();
CircleImageView avatarImgView;
    ImageView backgroundImgView;
    EditText nameEditText;
    Button saveBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.edit_profile);
        setContentView(R.layout.edit_profile_layout);
        db = new SQLiteHandler(this);
        avatarImgView = (CircleImageView) findViewById(R.id.edit_profile_avatar_imgView);
        avatarImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageType = ImageType.Avatar;
                selectImage();
                /*final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, setImageUri());
                startActivityForResult(intent, CAPTURE_IMAGE);*/
            }
        });


        backgroundImgView =(ImageView)findViewById(R.id.edit_profile_back_imgView);
       // backgroundImgView.setVisibility(View.VISIBLE);
        backgroundImgView.setOnClickListener(new View.OnClickListener() {

            @Override
                public void onClick(View v) {
                imageType = ImageType.Background;
                selectImage();
      /*              final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, setImageUri());
                    startActivityForResult(intent, CAPTURE_IMAGE);*/
             /*   Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("image*//*");
                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image*//*");
                Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});
                startActivityForResult(chooserIntent, PICK_IMAGE);*/
            }
        });
        /*avatarImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image*//*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), SELECT_PICTURE);
            }
        });*/
        Bitmap back = getImageFromStorage(this);
        if(back != null)
        {
            backgroundImgView.setImageBitmap(back);
        }
        nameEditText = (EditText)findViewById(R.id.edit_profile_name_editText);
        nameEditText.setText( UserLab.getInstance().getUser().getUserLogin());
        String avatar = UserLab.getInstance().getUser().getAvatar();
        if(avatar != null)
        {
            avatar = InternetHelper.toCorrectLink(avatar);
            imageLoader.displayImage(avatar, avatarImgView, DisplayImageLoaderOptions.getInstance());

        }
        saveBtn = (Button)findViewById(R.id.edit_profile_save_button);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

updateUser(nameEditText.getText().toString(),null);
                // create RequestBody instance from file


            }
        });

    }
    final private int PICK_IMAGE = 1;
    final private int CAPTURE_IMAGE = 2;

    public Uri setImageUri() {
        // Store image in dcim
        File file = new File(Environment.getExternalStorageDirectory() + "/DCIM/", "image" + new Date().getTime() + ".png");
        Uri imgUri = Uri.fromFile(file);
        this.imgPath = file.getAbsolutePath();
        return imgUri;
    }


    public String getImagePath() {
        return imgPath;
    }

public boolean updateUser(@Nullable final String name,@Nullable final String email)
{
    final boolean[] result = {false};
    if((name == null ||name.isEmpty())&&(email == null ||email.isEmpty())&&(selectedAvatarPath == null ||selectedAvatarPath.isEmpty()) )
    {
        return false;
    }
    RequestBody fbody = null;
    if(!selectedAvatarPath.isEmpty())
    {
        File file = new File(selectedAvatarPath);
        fbody = RequestBody.create(MediaType.parse("image/*"), file);
    }
    // fbody = RequestBody.create(MediaType.parse("image/*"),bytes );
    RequestBody username = RequestBody.create(MediaType.parse("text/plain"),name);
    Call<DataUpdateUser> call = FactoryApi.getInstance(getApplicationContext()).update_user(username,null,fbody);
    call.enqueue(new Callback<DataUpdateUser>() {
        @Override
        public void onResponse(Call<DataUpdateUser> call, Response<DataUpdateUser> response) {

            if(response.body().getResult().equals(Constants.RESULT_SUCCESS))
            {
               User u =  UserLab.getInstance().getUser();
                u.setAvatar(response.body().getData().getAvatar());
                u.setUserEmail(response.body().getData().getEmail());
                u.setUserLogin(response.body().getData().getUsername());
                db.updateUser(u.getId(), name, email);
                Toast.makeText(getApplicationContext(), R.string.update_data_success, Toast.LENGTH_LONG).show();
                result[0] =  true;

            }
            else
            {
                Toast.makeText(getApplicationContext(),response.body().getMessage(), Toast.LENGTH_LONG).show();

                result[0] =  false;
                Log.d(TAG, "@@@@@@ Message " + response.body().getMessage());
            }
        }

        @Override
        public void onFailure(Call<DataUpdateUser> call, Throwable t) {
            t.printStackTrace();
            Toast.makeText(getApplicationContext(), R.string.update_data_failure, Toast.LENGTH_LONG).show();
            result[0] =  false;
        }
    });
    return result[0];
}


   @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_CANCELED) {
            Bitmap yourSelectedImage = null;
            String selectedImagePath = "";
            if (requestCode == PICK_IMAGE) {
                if(data == null)
                {
                    return;
                }
                if(data.getData() == null)
                {
                    return;
                }
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(
                            selectedImage, filePathColumn, null, null, null);
                if(cursor == null)
                {
                    return;
                }
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                selectedImagePath = cursor.getString(columnIndex);
                 //   String filePath = cursor.getString(columnIndex);
                    cursor.close();


                   yourSelectedImage = BitmapFactory.decodeFile(selectedImagePath);
             //   backgroundImgView.setImageBitmap(yourSelectedImage);
           //     Bitmap bitmap;
            //    Uri uri = data.getData();
              //  String strUri = getAbsolutePath(uri);
/*File f = new File(strUri);
                Uri uri = */
                //DebugDialog.DebugLog("STR URI:%s", strUri);
               /* if (strUri.contains("document") || strUri.contains("mediaKey") || strUri.contains("content://") ||
                        //if(sourcePath.startsWith("/document") || sourcePath.startsWith("/mediaKey") ||
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {*/
               /*     int read;
                    byte[] buffer;
                    InputStream fileInputStream;
                    ByteArrayOutputStream byteArrayOutputStream;

                    try {
                        fileInputStream = getApplicationContext().getContentResolver().openInputStream(uri);
                        byteArrayOutputStream = new ByteArrayOutputStream();

                        buffer = new byte[2048];
                        while(true)
                        {
                            read = fileInputStream.read(buffer);
                            if(read < 0)
                                break;

                            byteArrayOutputStream.write(buffer, 0, read);

                        }
                        bytes = byteArrayOutputStream.toByteArray();
                         bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        avatarImgView.setImageBitmap(bitmap);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

*/

              //  }
            }else if (requestCode == CAPTURE_IMAGE) {
                selectedImagePath = getImagePath();
            //    avatarImgView.setImageBitmap(decodeFile(selectedImagePath));
                 yourSelectedImage = decodeFile(selectedImagePath);
              //  backgroundImgView.setImageBitmap(yourSelectedImage);
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
            if(yourSelectedImage == null)
            {
                return;
            }

            if(imageType.equals(ImageType.Avatar))
            {
                selectedAvatarPath = selectedImagePath;
                Bitmap profileBitmap  = Bitmap.createScaledBitmap(yourSelectedImage, 120, 120, false);
                avatarImgView.setImageBitmap(profileBitmap);
            }
            else  if(imageType.equals(ImageType.Background))
            {
                backgroundImgView.setImageBitmap(yourSelectedImage);
                saveToInternalStorage(yourSelectedImage);
            }
        }

    }
    private void takePhoto()
    {
        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, setImageUri());
        startActivityForResult(intent, CAPTURE_IMAGE);
    }
    private void chooseFromGallery()
    {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, getString(R.string.btn_choose_from_gallery));
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        startActivityForResult(chooserIntent, PICK_IMAGE);
       /*
        Intent intent = new Intent();
        intent.setType("image*//*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.btn_choose_from_gallery)), PICK_IMAGE);*/
    }

    private void selectImage()
    {
        final CharSequence[] items = {getString(R.string.btn_choose_from_gallery), getString(R.string.btn_take_photo)};
        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
        builder.setTitle(getString(R.string.title_photo));
        builder.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
               // boolean result= Utility.checkPermission(MainActivity.this);
               if(item == 0) {
                   chooseFromGallery();
               }
                else if(item == 1) {
                   takePhoto();
               }
                else {
               }

            }
        });
        builder.show();
    }
    public static Bitmap getImageFromStorage(Context context)
    {

        try {
            ContextWrapper cw = new ContextWrapper(context);
            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
            File f=new File(directory, "profile.jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            return b;
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            return null;

        }

    }
    private String saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,"profile.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    public Bitmap decodeFile(String path) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, o);
            // The new size we want to scale to
            final int REQUIRED_SIZE = 70;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE && o.outHeight / scale / 2 >= REQUIRED_SIZE)
                scale *= 2;

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeFile(path, o2);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;

    }

    public String getAbsolutePath(Uri uri) {
        String[] projection = { MediaStore.MediaColumns.DATA };
    @SuppressWarnings("deprecation")
    Cursor cursor = managedQuery(uri, projection, null, null, null);
    if (cursor != null) {
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    } else
            return null;
}
    /* public void onActivityResult(int requestCode, int resultCode, Intent data) {
         if (resultCode == RESULT_OK) {
             if (requestCode == SELECT_PICTURE) {
                 Uri selectedImageUri = data.getData();
                 selectedImagePath = getPath(selectedImageUri);
                 System.out.println("Image Path : " + selectedImagePath);
                 File file = new File(selectedImagePath);
                 Call<DataPost>call = FactoryApi.getInstance(this).update_user(null,null,file);
                 call.enqueue(new Callback<DataPost>() {
                     @Override
                     public void onResponse(Call<DataPost> call, Response<DataPost> response) {
                         Toast.makeText(getApplicationContext(),response.body().getResult(),Toast.LENGTH_LONG);
                     }

                     @Override
                     public void onFailure(Call<DataPost> call, Throwable t) {

                     }
                 });
             }
         }
     }*/
   /* public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
               finish();
                return(true);
        }

        return(super.onOptionsItemSelected(item));
    }
enum ImageType {Avatar, Background}
}
