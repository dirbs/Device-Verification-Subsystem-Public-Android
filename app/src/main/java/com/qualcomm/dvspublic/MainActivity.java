/**
 * Copyright (c) 2018 Qualcomm Technologies, Inc.
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without modification, are permitted (subject to the limitations in the disclaimer below) provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * Neither the name of Qualcomm Technologies, Inc. nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.qualcomm.dvspublic;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageButton;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.tapadoo.alerter.Alerter;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executor;

import studio.carbonylgroup.textfieldboxes.ExtendedEditText;
import studio.carbonylgroup.textfieldboxes.TextFieldBoxes;

import static android.content.ContentValues.TAG;


public class MainActivity extends Activity implements Executor {


    TextView errorTxt;
    TextInputEditText imeiEt;
    Button submitBtn;
    ProgressBar progressBar;
    private DecoratedBarcodeView barcodeScannerView;
//    public static boolean isCaptchaCancelled = false;
//    private GoogleApiClient mGoogleApiClient;
//    boolean isGoogleApiClientConnected = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        errorTxt = (TextView) findViewById(R.id.error_txt);
        imeiEt = (TextInputEditText) findViewById(R.id.imei_et);
        submitBtn = (Button) findViewById(R.id.submit_btn);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);


        if(getIntent().hasExtra("imei"))
            imeiEt.setText(getIntent().getStringExtra("imei"));

        //init google client
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addApi(SafetyNet.API)
//                .addConnectionCallbacks(MainActivity.this)
//                .addOnConnectionFailedListener(MainActivity.this)
//                .build();
//
//        mGoogleApiClient.connect();

        if(getIntent().hasExtra("barcode"))
        {
            imeiEt.setText(getIntent().getStringExtra("barcode"));
        }


        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        });

        KeyboardVisibilityEvent.setEventListener(
                MainActivity.this,
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        // some code depending on keyboard visiblity status
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            imeiEt.setBackground(MainActivity.this.getDrawable(R.drawable.boxed_edittext_bg));
                        }else {
                            //noinspection deprecation
                            imeiEt.setBackgroundDrawable(MainActivity.this.getResources().getDrawable(R.drawable.boxed_edittext_bg));
                        }
                    }
                });



        imeiEt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;


                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (imeiEt.getRight() - imeiEt.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here
                        Intent intent = new Intent(MainActivity.this, ScannerActivity.class);
                        startActivity(intent);
                        return true;
                    }
                }
                return false;
            }
        });
        imeiEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {
                final String imei = imeiEt.getText().toString();
                boolean isValid = true;
                if ( imei.length() < 14 || imei.length() > 16 ) {
                    isValid = false;
                    errorTxt.setText(getString(R.string.length_error));
                }
                if ( !imei.matches("^(?=.[a-fA-F]*)(?=.[0-9]*)[a-fA-F0-9]+$")) {
                    isValid = false;
                    errorTxt.setText(getString(R.string.hexa_error));
                }

                if(isValid)
                    errorTxt.setText("");

            }
        });
    }

    private void validate() {
        final String imei = imeiEt.getText().toString();
        if ( imei.length() >= 14 && imei.length() <= 16 ) {
            if ( imei.matches("^(?=.[a-fA-F]*)(?=.[0-9]*)[a-fA-F0-9]+$")) {
                errorTxt.setText("");
                verifyCaptcha(null, progressBar, submitBtn, imeiEt,MainActivity.this, imei);
            } else {
                errorTxt.setText(getString(R.string.hexa_error));
            }
        } else {
            errorTxt.setText(getString(R.string.length_error));
        }


    }


    public void verifyCaptcha(final ProgressDialog progressDialog, final ProgressBar progressBar, final Button submitBtn, final TextInputEditText scanBtn, final Activity activity, final String imei){

            submitBtn.setEnabled(false);
            scanBtn.setEnabled(false);
            SafetyNet.getClient(MainActivity.this).verifyWithRecaptcha(activity.getString(R.string.recaptcha_key)).addOnCanceledListener((Activity) MainActivity.this, new OnCanceledListener() {
                @Override
                public void onCanceled() {
                    Log.e("Task", "Cancelled");
                    submitBtn.setEnabled(true);
                    scanBtn.setEnabled(true);
                }
            })
                    .addOnSuccessListener((Activity) MainActivity.this,
                            new OnSuccessListener<SafetyNetApi.RecaptchaTokenResponse>() {
                                @Override
                                public void onSuccess(SafetyNetApi.RecaptchaTokenResponse response) {
                                    // Indicates communication with reCAPTCHA service was
                                    // successful.
                                    String userResponseToken = response.getTokenResult();
                                    if (!userResponseToken.isEmpty()) {
                                        // Validate the user response token using the
                                        // reCAPTCHA siteverify API.
                                        NetworkUtils.hideShowLoading(progressBar, submitBtn, scanBtn, progressDialog, true);
//                                        Log.e("MainActivity","token = "+result.getTokenResult());
                                        NetworkUtils.getImeiStatus(userResponseToken, imei, progressDialog, progressBar, submitBtn, scanBtn, activity);
                                    }
                                    else
                                        Log.e("token","not found");
                                }
                            })
                    .addOnFailureListener((Activity) MainActivity.this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (e instanceof ApiException) {
                                submitBtn.setEnabled(true);
                                scanBtn.setEnabled(true);
                                // An error occurred when communicating with the
                                // reCAPTCHA service. Refer to the status code to
                                // handle the error appropriately.
                                ApiException apiException = (ApiException) e;
                                int statusCode = apiException.getStatusCode();
                                Log.d(TAG, "Error: " + CommonStatusCodes
                                        .getStatusCodeString(statusCode));
                                Alerter.create(activity)
                                        .setTitle("Oops...")
                                        .setText(CommonStatusCodes
                                                .getStatusCodeString(statusCode))
                                        .setIcon(R.drawable.ic_error)
                                        .setBackgroundColorRes(R.color.colorRed)
                                        .setIconColorFilter(0) // Optional - Removes white tint
                                        .show();
                            } else {
                                // A different, unknown type of error occurred.
                                Log.d(TAG, "Error: " + e.getMessage());

                            }
                        }
                    });


//        }
//        else{
//
//            Toast.makeText(activity, "Google API client not connected", Toast.LENGTH_SHORT).show();
//        }
    }

    @Override
    public void execute(@NonNull Runnable command) {

    }
}
