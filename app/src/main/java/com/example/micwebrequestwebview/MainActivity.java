package com.example.micwebrequestwebview;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TextView textView, textView1, textView2;
    EditText speechText, editText2;
    ImageButton speechButton;
    String userText, userIpText, targetText, respondText, SpeechWord, SpeechWord2;
    RequestQueue queue;
    Button button1, button2;
    WebView webView;
    String nodeJs_Ip = "http://140.125.32.138:3000";
    //    String carBot_Ip = "http://140.125.32.128:5000/carbot";
    String carBot_Ip = "http://140.125.32.145:5000/carbot";
    TextToSpeech textToSpeech;
    String nowDate;

    private static final int RECOGNIZER_RESULT = 1;
//    private static final String TAG = "MyAppTag";
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private String AudioSavaPath = null;
    private Button start,stop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        textView = findViewById(R.id.textView);
        textView1 = findViewById(R.id.textView4);
        textView2 = findViewById(R.id.textView5);
        editText2 = findViewById(R.id.editText2);
//        speechText = findViewById(R.id.editText);
        speechButton = findViewById(R.id.imageButton);
        button1 = findViewById(R.id.button1);
//        button2 = findViewById(R.id.button2);
        queue = Volley.newRequestQueue(this);
        webView = findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.loadUrl(nodeJs_Ip + "/Metaverse_RoboMaster1");

        start=(Button)findViewById(R.id.start);
        stop=(Button)findViewById(R.id.button1);

        start.setOnClickListener(new View.OnClickListener(){
            //執行背景作業
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(MainActivity.this, RecordingService.class);
                startService(intent);
            }});
        stop.setOnClickListener(new View.OnClickListener(){

            //停止背景作業
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(MainActivity.this, RecordingService.class);
                stopService(intent);
            }});

        speechButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                speechIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speech to text");
                startActivityForResult(speechIntent, RECOGNIZER_RESULT);

                nowDate = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
                System.out.println(nowDate);

                if (checkPermissions() == true) {

//                    AudioSavaPath = Environment.getExternalStorageDirectory().getAbsolutePath()
//                            +"/"+"recordingAudio.wav";

                    mediaRecorder = new MediaRecorder();
                    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                    mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                    mediaRecorder.setOutputFile(getFilePath());

                    try {
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                        Toast.makeText(MainActivity.this, "Recording started", Toast.LENGTH_SHORT).show();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else {

                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{
                            Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },1);
                }


            }
        });

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) {
                    int language = textToSpeech.setLanguage(Locale.ENGLISH);
                }
            }
        });

        int speech = textToSpeech.speak("Where do you want to go?", textToSpeech.QUEUE_FLUSH, null);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Debug", nowDate);
                // Request a string response from the provided URL.
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, carBot_Ip,
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                // Display the first 500 characters of the response string.
                                System.out.println(response);
//                                Log.d("brad", response);
                                String res = response.toString();
                                try {
                                    JSONObject resObject = new JSONObject(res);
//                                    userIpText = resObject.getString("user_ip");
                                    respondText = resObject.getString("respond");
                                    targetText = resObject.getString("target");
                                    editText2.setText("");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
//                                Toast.makeText(MainActivity.this, targetText, Toast.LENGTH_SHORT).show();
//                                Toast.makeText(MainActivity.this, userIpText, Toast.LENGTH_SHORT).show();
//                                textView.setText(targetText);
                                textView1.setText(respondText);
                                int speech = textToSpeech.speak(respondText, textToSpeech.QUEUE_FLUSH, null);

                                //POST_TO_NODEJS--------------------------------------------------------------------

                                StringRequest stringRequest_nodejs = new StringRequest(Request.Method.POST, nodeJs_Ip + "/posttest",
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                System.out.println(response);
                                            }
                                        }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        System.out.println(error);
                                        Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                }){
                                    @Override
                                    public byte[] getBody() throws AuthFailureError {
                                        JSONObject jsonBody2 = new JSONObject();
                                        try {
                                            jsonBody2.put("target", targetText);
                                            jsonBody2.put("user_ip", userIpText);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        String requestBody2 = jsonBody2.toString();
                                        return  requestBody2.getBytes(StandardCharsets.UTF_8);
                                    }
                                };
                                queue.add(stringRequest_nodejs);

                                //POST_TO_NODEJS--------------------------------------------------------------------

                                //POST_TO_Jetson_Xavier-------------------------------------------------------------

//                                JsonObjectRequest stringRequest_jsonXavier = new JsonObjectRequest(Request.Method.POST, userIpText,null,
//                                        new Response.Listener<JSONObject>() {
//                                            @Override
//                                            public void onResponse(JSONObject response) {
//                                                System.out.println(response);
//                                            }
//                                        }, new Response.ErrorListener() {
//                                    @Override
//                                    public void onErrorResponse(VolleyError error) {
//                                        System.out.println(error);
//                                    }
//                                }){
//                                    @Override
//                                    public byte[] getBody() {
//                                        JSONObject jsonBody3 = new JSONObject();
//                                        try {
//                                            jsonBody3.put("target", targetText);
////                                        jsonBody3.put("user", "bot01");
//                                        } catch (JSONException e) {
//                                            e.printStackTrace();
//                                        }
//                                        String requestBody2 = jsonBody3.toString();
//                                        return  requestBody2.getBytes(StandardCharsets.UTF_8);
//                                    }
//                                };
//
//                                queue.add(stringRequest_jsonXavier);

                                //POST_TO_Jetson_Xavier-------------------------------------------------------------

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error);
//                        textView.setText("That didn't work!");
                    }
                })
                {
                    @Override
                    public byte[] getBody() {
                        JSONObject jsonBody = new JSONObject();
                        try {
                            System.out.println(editText2.getText().toString());
                            jsonBody.put("text", editText2.getText().toString());
                            jsonBody.put("user","bot01");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        String requestBody = jsonBody.toString();
                        return requestBody.getBytes(StandardCharsets.UTF_8);
                    }
                };
                // Add the request to the RequestQueue.
                queue.add(jsonObjectRequest);
            }
        });

//        button1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // Request a string response from the provided URL.
//                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, carBot_Ip,
//                        null,
//                        new Response.Listener<JSONObject>() {
//                            @Override
//                            public void onResponse(JSONObject response) {
//                                // Display the first 500 characters of the response string.
//                                System.out.println(response);
////                                Log.d("brad", response);
//                                String res = response.toString();
//                                try {
//                                    JSONObject resObject = new JSONObject(res);
////                                    userIpText = resObject.getString("user_ip");
//                                    respondText = resObject.getString("respond");
//                                    targetText = resObject.getString("target");
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
////                                Toast.makeText(MainActivity.this, targetText, Toast.LENGTH_SHORT).show();
////                                Toast.makeText(MainActivity.this, userIpText, Toast.LENGTH_SHORT).show();
////                                textView.setText(targetText);
//                                textView1.setText(respondText);
//
//                                //POST_TO_NODEJS--------------------------------------------------------------------
//
//                                StringRequest stringRequest_nodejs = new StringRequest(Request.Method.POST, nodeJs_Ip + "/posttest",
//                                        new Response.Listener<String>() {
//                                            @Override
//                                            public void onResponse(String response) {
//                                                System.out.println(response);
//                                            }
//                                        }, new Response.ErrorListener() {
//                                    @Override
//                                    public void onErrorResponse(VolleyError error) {
//                                        System.out.println(error);
//                                        Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
//                                    }
//                                }){
//                                    @Override
//                                    public byte[] getBody() throws AuthFailureError {
//                                        JSONObject jsonBody2 = new JSONObject();
//                                        try {
//                                            jsonBody2.put("target", targetText);
//                                            jsonBody2.put("user_ip", userIpText);
//                                        } catch (JSONException e) {
//                                            e.printStackTrace();
//                                        }
//                                        String requestBody2 = jsonBody2.toString();
//                                        return  requestBody2.getBytes(StandardCharsets.UTF_8);
//                                    }
//                                };
//                                queue.add(stringRequest_nodejs);
//
//                                //POST_TO_NODEJS--------------------------------------------------------------------
//
//                                //POST_TO_Jetson_Xavier-------------------------------------------------------------
//
////                                JsonObjectRequest stringRequest_jsonXavier = new JsonObjectRequest(Request.Method.POST, userIpText,null,
////                                        new Response.Listener<JSONObject>() {
////                                            @Override
////                                            public void onResponse(JSONObject response) {
////                                                System.out.println(response);
////                                            }
////                                        }, new Response.ErrorListener() {
////                                    @Override
////                                    public void onErrorResponse(VolleyError error) {
////                                        System.out.println(error);
////                                    }
////                                }){
////                                    @Override
////                                    public byte[] getBody() {
////                                        JSONObject jsonBody3 = new JSONObject();
////                                        try {
////                                            jsonBody3.put("target", targetText);
//////                                        jsonBody3.put("user", "bot01");
////                                        } catch (JSONException e) {
////                                            e.printStackTrace();
////                                        }
////                                        String requestBody2 = jsonBody3.toString();
////                                        return  requestBody2.getBytes(StandardCharsets.UTF_8);
////                                    }
////                                };
////
////                                queue.add(stringRequest_jsonXavier);
//
//                                //POST_TO_Jetson_Xavier-------------------------------------------------------------
//
//                            }
//                        }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        System.out.println(error);
//                        textView.setText("That didn't work!");
//                    }
//                })
//                {
//                    @Override
//                    public byte[] getBody() {
//                        JSONObject jsonBody = new JSONObject();
//                        try {
//                            jsonBody.put("text", "go to the park");
//                            jsonBody.put("user","bot01");
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                        String requestBody = jsonBody.toString();
//                        return requestBody.getBytes(StandardCharsets.UTF_8);
//                    }
//                };
//                // Add the request to the RequestQueue.
//                queue.add(jsonObjectRequest);
//            }
//        });
//
//
//        button2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // Request a string response from the provided URL.
//                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, carBot_Ip,
//                        null,
//                        new Response.Listener<JSONObject>() {
//                            @Override
//                            public void onResponse(JSONObject response) {
//                                // Display the first 500 characters of the response string.
//                                System.out.println("response = " + response);
////                                Log.d("brad", response);
//                                String res = response.toString();
//                                try {
//                                    JSONObject resObject = new JSONObject(res);
////                                    userIpText = resObject.getString("user_ip");
//                                    respondText = resObject.getString("respond");
//                                    targetText = resObject.getString("target");
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
////                                Toast.makeText(MainActivity.this, targetText, Toast.LENGTH_SHORT).show();
////                                Toast.makeText(MainActivity.this, userIpText, Toast.LENGTH_SHORT).show();
//                                System.out.println(targetText);
//                                textView.setText(targetText);
//                                textView1.setText(respondText);
//
//                                //POST_TO_NODEJS--------------------------------------------------------------------
//
//                                StringRequest stringRequest_nodejs = new StringRequest(Request.Method.POST, nodeJs_Ip + "/posttest",
//                                        new Response.Listener<String>() {
//                                            @Override
//                                            public void onResponse(String response) {
//                                                System.out.println(response);
//                                            }
//                                        }, new Response.ErrorListener() {
//                                    @Override
//                                    public void onErrorResponse(VolleyError error) {
//                                        System.out.println("nodejs_error :"+error);
//                                        Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
//                                    }
//                                }){
//                                    @Override
//                                    public byte[] getBody() throws AuthFailureError {
//                                        JSONObject jsonBody2 = new JSONObject();
//                                        try {
//                                            jsonBody2.put("target", targetText);
//                                            jsonBody2.put("user_ip", userIpText);
//                                        } catch (JSONException e) {
//                                            e.printStackTrace();
//                                        }
//                                        String requestBody2 = jsonBody2.toString();
//                                        return  requestBody2.getBytes(StandardCharsets.UTF_8);
//                                    }
//                                };
//                                queue.add(stringRequest_nodejs);
//
//                                //POST_TO_NODEJS--------------------------------------------------------------------
//
//                                //POST_TO_Jetson_Xavier-------------------------------------------------------------
//
////                                JsonObjectRequest stringRequest_jsonXavier = new JsonObjectRequest(Request.Method.POST, userIpText,null,
////                                        new Response.Listener<JSONObject>() {
////                                            @Override
////                                            public void onResponse(JSONObject response) {
////                                                System.out.println(response);
////                                            }
////                                        }, new Response.ErrorListener() {
////                                    @Override
////                                    public void onErrorResponse(VolleyError error) {
////                                        System.out.println(error);
////                                    }
////                                }){
////                                    @Override
////                                    public byte[] getBody() {
////                                        JSONObject jsonBody3 = new JSONObject();
////                                        try {
////                                            jsonBody3.put("target", targetText);
//////                                        jsonBody3.put("user", "bot01");
////                                        } catch (JSONException e) {
////                                            e.printStackTrace();
////                                        }
////                                        String requestBody2 = jsonBody3.toString();
////                                        return  requestBody2.getBytes(StandardCharsets.UTF_8);
////                                    }
////                                };
////
////                                queue.add(stringRequest_jsonXavier);
//
//                                //POST_TO_Jetson_Xavier-------------------------------------------------------------
//
//                            }
//                        }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        System.out.println(error);
//                        textView.setText("That didn't work!");
//                    }
//                })
//                {
//                    @Override
//                    public byte[] getBody() {
//                        JSONObject jsonBody = new JSONObject();
//                        try {
//                            jsonBody.put("text", "go to the restaurant");
//                            jsonBody.put("user","bot01");
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                        String requestBody = jsonBody.toString();
//                        return requestBody.getBytes(StandardCharsets.UTF_8);
//                    }
//                };
//                // Add the request to the RequestQueue.
//                queue.add(jsonObjectRequest);
//            }
//        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == RECOGNIZER_RESULT && resultCode == RESULT_OK){
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            SpeechWord = matches.get(0).toString();
//            speechText.setText(SpeechWord);
//            textView2.setText(SpeechWord);
            editText2.setText(SpeechWord);
//            Log.d(TAG, SpeechWord);
            ArrayList<String> matches2 = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            SpeechWord2 = matches2.get(0).toString();

            mediaRecorder.stop();
            mediaRecorder.release();
            Toast.makeText(MainActivity.this, "Recording stopped", Toast.LENGTH_SHORT).show();

//            File file = new File(Environment.getExternalStorageDirectory() + "/Miaudio.mp3");
//            byte[] bytes = new byte[0];
//            bytes = FileUtils.readFileToByteArray(file);

            // Request a string response from the provided URL.
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, carBot_Ip,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // Display the first 500 characters of the response string.
                            System.out.println(response);
//                                Log.d("brad", response);
                            String res = response.toString();
                            try {
                                JSONObject resObject = new JSONObject(res);
//                                userIpText = resObject.getString("user_ip");
                                respondText = resObject.getString("respond");
                                targetText = resObject.getString("target");
//                                textView2 = resObject.getString("respond");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
//                            Toast.makeText(MainActivity.this, targetText, Toast.LENGTH_SHORT).show();
//                            Toast.makeText(MainActivity.this, userIpText, Toast.LENGTH_SHORT).show();
//                            textView.setText(targetText);
                            textView1.setText(respondText);
                            int speech = textToSpeech.speak(respondText, textToSpeech.QUEUE_FLUSH, null);

                            //POST_TO_NODEJS--------------------------------------------------------------------

                            StringRequest stringRequest_nodejs = new StringRequest(Request.Method.POST, nodeJs_Ip + "/posttest",
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            System.out.println(response);
                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    System.out.println(error);
                                    Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                                }
                            }){
                                @Override
                                public byte[] getBody() throws AuthFailureError {
                                    JSONObject jsonBody2 = new JSONObject();
                                    try {
                                        jsonBody2.put("target", targetText);
//                                        jsonBody2.put("user_ip", userIpText);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    String requestBody2 = jsonBody2.toString();
                                    return  requestBody2.getBytes(StandardCharsets.UTF_8);
                                }
                            };
                            queue.add(stringRequest_nodejs);

                            //POST_TO_NODEJS--------------------------------------------------------------------

                            //POST_TO_Jetson_Xavier-------------------------------------------------------------

                            JsonObjectRequest stringRequest_jsonXavier = new JsonObjectRequest(Request.Method.POST, userIpText,null,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            System.out.println(response);
                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    System.out.println(error);
                                }
                            }){
                                @Override
                                public byte[] getBody() {
                                    JSONObject jsonBody3 = new JSONObject();
                                    try {
                                        jsonBody3.put("target", targetText);
//                                        jsonBody3.put("user", "bot01");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    String requestBody2 = jsonBody3.toString();
                                    return  requestBody2.getBytes(StandardCharsets.UTF_8);
                                }
                            };

                            queue.add(stringRequest_jsonXavier);

                            //POST_TO_Jetson_Xavier-------------------------------------------------------------

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println(error);
//                    textView.setText("That didn't work!");
                }
            })
            {
                @Override
                public byte[] getBody() {
                    JSONObject jsonBody = new JSONObject();
                    try {
                        jsonBody.put("text", SpeechWord);
                        jsonBody.put("user","bot01");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String requestBody = jsonBody.toString();
                    return requestBody.getBytes(StandardCharsets.UTF_8);
                }
            };
            // Add the request to the RequestQueue.
            queue.add(jsonObjectRequest);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        if(requestCode == RECOGNIZER_RESULT && resultCode == RESULT_OK){
//            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
//            SpeechWord = matches.get(0).toString();
//            speechText.setText(SpeechWord);
////            Log.d(TAG, SpeechWord);
//
//            // Request a string response from the provided URL.
//            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, "http://140.125.32.128:5000/carbot",
//                    null,
//                    new Response.Listener<JSONObject>() {
//                        @Override
//                        public void onResponse(JSONObject response) {
//                            // Display the first 500 characters of the response string.
//                            System.out.println(response);
////                                Log.d("brad", response);
//                            String res = response.toString();
//                            try {
//                                JSONObject resObject = new JSONObject(res);
//                                userIpText = resObject.getString("user_ip");
//                                targetText = resObject.getString("target");
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                            Toast.makeText(MainActivity.this, targetText, Toast.LENGTH_SHORT).show();
//                            Toast.makeText(MainActivity.this, userIpText, Toast.LENGTH_SHORT).show();
//                            textView.setText(targetText);
//                        }
//                    }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    System.out.println(error);
//                    textView.setText("That didn't work!");
//                }
//            })
//            {
//                @Override
//                public byte[] getBody() {
//                    JSONObject jsonBody = new JSONObject();
//                    try {
//                        jsonBody.put("text", SpeechWord);
//                        jsonBody.put("user","bot01");
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    String requestBody = jsonBody.toString();
//                    return requestBody.getBytes(StandardCharsets.UTF_8);
//                }
//            };
//            // Add the request to the RequestQueue.
//            queue.add(jsonObjectRequest);
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//    }
    private boolean checkPermissions() {
        int first = ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.RECORD_AUDIO);
        int second = ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        return first == PackageManager.PERMISSION_GRANTED &&
                second == PackageManager.PERMISSION_GRANTED;
    }
    private String getFilePath() {
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File musicDirectory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File file = new File(musicDirectory, "testRecordingFile" + ".mp3");
        return file.getPath();
    }
}