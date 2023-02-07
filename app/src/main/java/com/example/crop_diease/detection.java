package com.example.crop_diease;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.crop_diease.ml.Model;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;


public class detection extends AppCompatActivity {

    Button camera, gallery,send;
    ImageView imageView;
    TextView result;
    int imageSize = 32;
    String MY_PREFS_NAME = "MyPrefsFile";
    String address, weather;
    String[] classes;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detection);


//        send = findViewById(R.id.button4);
//        send.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//            }
//        });
//
//
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//                NotificationChannel channel = new NotificationChannel("My Notification","My Notification", NotificationManager.IMPORTANCE_DEFAULT);
//                NotificationManager manager = getSystemService(NotificationManager.class);
//                manager.createNotificationChannel(channel);
//
//            }

        gallery = findViewById(R.id.button2);

        result = findViewById(R.id.result);
        imageView = findViewById(R.id.imageView);

        camera = findViewById(R.id.button3);

        SharedPreferences sh = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        address = sh.getString("address", "default");
        Toast.makeText(getApplicationContext(), address, Toast.LENGTH_LONG).show();

        SharedPreferences sh1 = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        weather = sh1.getString("weather", "default");
        Toast.makeText(this, weather.toString(), Toast.LENGTH_SHORT).show();

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        Intent cameraintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraintent, 3);

                    } else {
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);

                    }
                }
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(cameraIntent, 1);
            }
        });

    }

    //
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 3) {
            Bitmap image = (Bitmap) data.getExtras().get("data");
            int dimension = Math.min(image.getWidth(), image.getHeight());
            image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
            imageView.setImageBitmap(image);

            image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
            classifyImage(image);
        } else {
            Uri dat = data.getData();
            Bitmap image = null;
            try {
                image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), dat);
            } catch (IOException e) {
                e.printStackTrace();
            }
            imageView.setImageBitmap(image);

            image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
            classifyImage(image);
        }
    }

    private void classifyImage(Bitmap image) {

        SharedPreferences sh = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        address = sh.getString("address", "default");
        Toast.makeText(getApplicationContext(), address, Toast.LENGTH_LONG).show();

        SharedPreferences sh1 = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        weather = sh1.getString("weather", "default");
        Toast.makeText(this, weather.toString(), Toast.LENGTH_SHORT).show();

        try {
            Model model = Model.newInstance(getApplicationContext());

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 32, 32, 3}, DataType.FLOAT32);


            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());

            int[] intValues = new int[imageSize * imageSize];
            image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
            int pixel = 0;
            //iterate over each pixel and extract R, G, and B values. Add those values individually to the byte buffer.
            for (int i = 0; i < imageSize; i++) {
                for (int j = 0; j < imageSize; j++) {
                    int val = intValues[pixel++]; // RGB
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 1));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 1));
                    byteBuffer.putFloat((val & 0xFF) * (1.f / 1));
                }
            }
            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            Model.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] confidences = outputFeature0.getFloatArray();
            // find the index of the class with the biggest confidence.
            int maxPos = 0;
            float maxConfidence = 0;
            for (int i = 0; i < confidences.length; i++) {
                if (confidences[i] > maxConfidence) {
                    maxConfidence = confidences[i];
                    maxPos = i;
                }
            }
            String[] classes = {"Disease - "+"Apple_scab \n " + "Fungicides:" +"\n" + "Myclobutanil,Benzimidazole. \n " + address ,
                    "Disease - "+"Cercospora_leaf_spot \n  " + "Treatment:"  +"\n" + "chlorothalonil, myclobutanil. \n "  + address ,
                    "Disease - "+"Esca_(Black_Measles) \n"  + "Fungicides:" +"\n"+ "Fenarimol, furmetamide. \n "  + address,
                    "Disease - "+"Leaf_blight_(Isariopsis_Leaf_Spot) \n"  +  "Fungicides:"  +"\n"+ "Mancozeb,Ziziphus.\" \n "  + address,
                    "Disease - "+"Bacterial_spot \n" + "Treatment:" +"\n"+ "sulfur sprays or copper-based. \n "  + address,
                    "Disease - "+"Pepper,_bell___Bacterial_spot \n" + "Treatment:"   +"\n"+ "copper fungicide \n "  + address,
                    "Disease - "+"Potato___Early_blight \n" + "Treatment:"  +"\n"+ "Mancozeb and chlorothalonil \n "  + address,
                    "Disease - "+"Tomato___Early_blight \n" +"Treatment:" +"\n"+ "Mancozeb and chlorothalonil  \n "  + address,
                    "Disease - "+"Tomato_mosaic_virus \n" + "Fungicides - "  +"\n"+ "Mancozeb and chlorothalonil \n "  + address};

            result.setText(classes[maxPos]);


            notification(classes[maxPos]);


            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            // TODO Handle the exception
        }

    }

    private void notification(String data) {

//        NotificationCompat.Builder builder = new NotificationCompat.Builder(detection.this,"My Notification");
//        builder.setContentTitle(address);
//        builder.setContentText(data);
//        builder.setSmallIcon(R.drawable.ic_launcher_background);
//        builder.setAutoCancel(true);
//
//        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(detection.this);
//
//        managerCompat.notify(1,builder.build());
//
//        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
//        r.play();

        SmsManager sms=SmsManager.getDefault();
        sms.sendTextMessage("+919527586442", null, data, null,null);

        SmsManager sms1=SmsManager.getDefault();
        sms1.sendTextMessage("+917757942066", null, data, null,null);

        SmsManager sms2=SmsManager.getDefault();
        sms2.sendTextMessage("+917218232383", null, data, null,null);



    }


}
