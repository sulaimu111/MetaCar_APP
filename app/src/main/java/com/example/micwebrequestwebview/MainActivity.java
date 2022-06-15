package com.example.micwebrequestwebview;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
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

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    TextView textView, textView1, textView2;
    EditText speechText, editText2;
    ImageButton speechButton;
    String userText, userIpText, targetText, respondText, SpeechWord;
    RequestQueue queue;
    Button button1, button2;
    WebView webView;
    String nodeJs_Ip = "http://140.125.32.138:3000";
    //    String carBot_Ip = "http://140.125.32.128:5000/carbot";
    String carBot_Ip = "http://140.125.32.145:5000/carbot";

    private static final int RECOGNIZER_RESULT = 1;
//    private static final String TAG = "MyAppTag";

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

        speechButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                speechIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speech to text");
                startActivityForResult(speechIntent, RECOGNIZER_RESULT);
            }
        });

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                        textView.setText("That didn't work!");
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
                            textView.setText(targetText);
                            textView1.setText(respondText);

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
                    textView.setText("That didn't work!");
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
}