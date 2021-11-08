package com.example.cs_makta;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;



public class MainActivity extends AppCompatActivity {

    private final int GET_GALLERY_IMAGE = 200;
    private final int GET_MASK_IMAGE = 300;
    private ImageView imageview;
    private Button urlbutton;
    private Button button;
    private Button segbtn;
    private Button inpaintbtn;
    private Button portraitbtn;
    private Button maskbtn;
    Bitmap bitmap;
    Bitmap mask_bitmap;
    Bitmap res;
    private String base_url;
    private String option;
    JSONObject jsonObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.InitializeView();
        this.SetListener();

    }



    public void InitializeView(){
        urlbutton = (Button) findViewById(R.id.button);
        button = (Button) findViewById(R.id.button2);
        imageview = (ImageView)findViewById(R.id.imageView);
        segbtn = (Button) findViewById(R.id.button3);
        inpaintbtn = (Button) findViewById(R.id.button4);
        portraitbtn = (Button) findViewById(R.id.button5);
        maskbtn = (Button) findViewById(R.id.button6);
    }

    public void SetListener()
    {
        urlbutton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {

                show();
            }
        }) ;

        button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                option = "/posetransfer";
                Log.v("button", "button click");
                try{
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);

                    byte[] image = byteArrayOutputStream.toByteArray();
                    String byteStream = Base64.encodeToString(image, 0);


                    
                    jsonObject = new JSONObject();
                    jsonObject.put("image", byteStream);
                    jsonObject.put("pose", "1");//미리 선정한 pose의 number
                }catch(JSONException e) {
                }


                connect(option);

            }
        }) ;

        segbtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                option = "/segmentation";
                try{
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);

                    byte[] image = byteArrayOutputStream.toByteArray();
                    String byteStream = Base64.encodeToString(image, 0);


                    
                    jsonObject = new JSONObject();
                    jsonObject.put("image", byteStream);
                }catch(JSONException e) {
                }
                connect(option);

            }
        }) ;
        inpaintbtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                option = "/inpainting";
                try{
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                    byte[] image = byteArrayOutputStream.toByteArray();
                    String byteStream = Base64.encodeToString(image, 0);

                    ByteArrayOutputStream mask_byteArrayOutputStream = new ByteArrayOutputStream();
                    mask_bitmap.compress(Bitmap.CompressFormat.PNG, 100, mask_byteArrayOutputStream);

                    byte[] mask_image = mask_byteArrayOutputStream.toByteArray();
                    String mask_byteStream = Base64.encodeToString(mask_image, 0);


                    jsonObject = new JSONObject();
                    jsonObject.put("image", byteStream);
                    jsonObject.put("mask", mask_byteStream);
                }catch(JSONException e) {
                }
                connect(option);

            }
        }) ;
        portraitbtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                option = "/portrait_mode";
                try{
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);

                    byte[] image = byteArrayOutputStream.toByteArray();
                    String byteStream = Base64.encodeToString(image, 0);



                    jsonObject = new JSONObject();
                    jsonObject.put("image", byteStream);
                    jsonObject.put("sigma", "1");//sigma값이 커짐에 따라 blur의 효과가 커짐
                }catch(JSONException e) {
                }
                connect(option);

            }
        }) ;

        imageview.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent. setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, GET_GALLERY_IMAGE);
            }
        });
        maskbtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent. setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, GET_MASK_IMAGE);
            }
        }) ;
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_GALLERY_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri selectedImageUri = data.getData();
            imageview.setImageURI(selectedImageUri);
            BitmapDrawable drawable = (BitmapDrawable) imageview.getDrawable();
            bitmap = drawable.getBitmap();

        }else if(requestCode == GET_MASK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri selectedImageUri = data.getData();
            imageview.setImageURI(selectedImageUri);
            BitmapDrawable drawable = (BitmapDrawable) imageview.getDrawable();
            mask_bitmap = drawable.getBitmap();

        }

    }

    void show()
    {
        final EditText edittext = new EditText(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("AlertDialog Title");
        builder.setMessage("AlertDialog Content");
        builder.setView(edittext);
        builder.setPositiveButton("입력",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        base_url = edittext.getText().toString();
                        Toast.makeText(getApplicationContext(),edittext.getText().toString() ,Toast.LENGTH_LONG).show();

                    }
                });
        builder.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();
    }


    void connect(String option){
        Thread uThread = new Thread() {
            @Override
            public void run() {
                try {
                    Bitmap res_bitmap = null;
                    InputStream response = null; // 요청 결과를 저장할 변수.
                    String lineEnd = "\r\n";
                    String twoHyphens = "--";
                    String boundary = "*****";

                    
                    String data = jsonObject.toString();
                    
                    URL url = new URL(base_url+option);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setDoInput(true); //input 허용
                    con.setDoOutput(true);  // output 허용
                    con.setUseCaches(false);   // cache copy를 허용하지 않는다.
                    con.setRequestMethod("POST");
                    con.setRequestProperty("Connection", "Keep-Alive");
                    con.setRequestProperty("Content-Type", "application/json;charset=UTF-8");

                    // write data
                    OutputStream out = new DataOutputStream(con.getOutputStream());
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
                    writer.write(data);
                    //dos.writeBytes(twoHyphens + boundary + lineEnd);
                    // 파일 전송시 파라메터명은 file1 파일명은 camera.jpg로 설정하여 전송
                    //dos.writeBytes("Content-Disposition: form-data; name=\"image\";filename=\"input_image.jpg\"" +lineEnd);


                    //dos.writeBytes(lineEnd);
                    // Bitmap을 ByteBuffer로 전환
                    byte[] pixels = new byte[bitmap.getWidth() * bitmap.getHeight()];
                    for (int i = 0; i < bitmap.getWidth(); ++i) {
                        for (int j = 0; j < bitmap.getHeight(); ++j) {
                            //we're interested only in the MSB of the first byte,
                            //since the other 3 bytes are identical for B&W images
                            pixels[i + j] = (byte) ((bitmap.getPixel(i, j) & 0x80) >> 7);
                        }
                    }

                    //dos.write(data);
                    //dos.writeBytes(lineEnd);
                    //dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                    writer.flush(); // finish upload...
                    out.close();
                    Log.v("http", "send");
                    //response
                    response = con.getInputStream();
                    res_bitmap = BitmapFactory.decodeStream(response);

                    Log.v("http", "response");
                    //Response stream종료
                    response.close();

                    // connection종료
                    con.disconnect();
                    res = res_bitmap;


                } catch (Exception e) {
                    e.printStackTrace();
                }


                //NetworkTask networkTask = new NetworkTask(url, null);
                //networkTask.execute();
            }
        };
        uThread.start();
        try{

            uThread.join();

            imageview.setImageBitmap(res);
            imageview.invalidate();

        }catch (InterruptedException e){

            e.printStackTrace();

        }
    }

}