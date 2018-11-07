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
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.AppCompatImageButton;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
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
import com.tapadoo.alerter.Alerter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executor;

import static android.content.ContentValues.TAG;

public class NetworkUtils{
    public static boolean isRequestInProcess = false;

    public static void getImeiStatus(String token, String imei, final ProgressDialog progressDialog, final ProgressBar progressBar, final Button submitBtn, final TextInputEditText scanBtn, final Activity activity) {
        String url = activity.getResources().getString(R.string.api_gateway_url)+"api/v1/basicstatus?imei="+imei+"&token="+token+"&source=android";
        Log.e(TAG, url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        isRequestInProcess = false;
                        hideShowLoading(progressBar, submitBtn, scanBtn, progressDialog, false);

                        parseJsonShowResult(response, activity);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        isRequestInProcess = false;
                        hideShowLoading(progressBar, submitBtn, scanBtn, progressDialog, false);
                        try {
                            if (error != null) {
                                error.printStackTrace();
                                if(error.networkResponse!=null)
                                {
                                    if(error.networkResponse.statusCode >= 400 && error.networkResponse.statusCode < 500)
                                    {
                                        if(error.networkResponse.statusCode == 403)
                                        {
                                            String body = new String(error.networkResponse.data,"UTF-8");
                                            Log.e("422", body+"");
                                            JSONObject mJsonObject = new JSONObject(body);
                                            JSONObject errorObj = mJsonObject.getJSONObject("error");
                                            Alerter.create(activity)
                                                    .setTitle("Oops...")
                                                    .setText(errorObj.getString("message"))
                                                    .setIcon(R.drawable.ic_error)
                                                    .setBackgroundColorRes(R.color.colorRed)
                                                    .setIconColorFilter(0) // Optional - Removes white tint
                                                    .show();

                                        }
                                        else if(error.networkResponse.statusCode == 422)
                                        {
                                            String body = new String(error.networkResponse.data,"UTF-8");
                                            Log.e("422", body+"");
                                            JSONObject mJsonObject = new JSONObject(body);
                                            JSONObject errorObj = mJsonObject.getJSONObject("error");
                                            if (mJsonObject.has("error")) {
                                                if(errorObj.optJSONObject("errors")!=null)
                                                {

                                                    Iterator<String> iter =  errorObj.optJSONObject("errors").keys();
                                                    String errors="";
                                                    while (iter.hasNext()) {
                                                        String key = iter.next();
                                                        for (int i = 0; i <  errorObj.optJSONObject("errors").getJSONArray(key).length(); i++) {

                                                            errors=errors+"- "+ errorObj.optJSONObject("errors").getJSONArray(key).getString(0)+"\n";
                                                        }


                                                    }

                                                    Alerter.create(activity)
                                                            .setTitle("Oops...")
                                                            .setText(errors)
                                                            .setIcon(R.drawable.ic_error)
                                                            .setBackgroundColorRes(R.color.colorRed)
                                                            .setIconColorFilter(0) // Optional - Removes white tint
                                                            .show();
                                                }
                                            }

                                        }

                                    }
                                    else {
                                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                                            Alerter.create(activity)
                                                    .setTitle("Oops...")
                                                    .setText(activity.getString(R.string.error_network_timeout))
                                                    .setIcon(R.drawable.ic_error)
                                                    .setBackgroundColorRes(R.color.colorRed)
                                                    .setIconColorFilter(0) // Optional - Removes white tint
                                                    .show();
                                        } else if (error instanceof AuthFailureError) {


                                            Alerter.create(activity)
                                                    .setTitle("Oops...")
                                                    .setText(activity.getString(R.string.error_auth_failure))
                                                    .setIcon(R.drawable.ic_error)
                                                    .setBackgroundColorRes(R.color.colorRed)
                                                    .setIconColorFilter(0) // Optional - Removes white tint
                                                    .show();
                                        } else if (error instanceof ServerError) {

                                            Alerter.create(activity)
                                                    .setTitle("Oops...")
                                                    .setText(activity.getString(R.string.error_server_error))
                                                    .setIcon(R.drawable.ic_error)
                                                    .setBackgroundColorRes(R.color.colorRed)
                                                    .setIconColorFilter(0) // Optional - Removes white tint
                                                    .show();
                                        } else if (error instanceof NetworkError) {

                                            Alerter.create(activity)
                                                    .setTitle("Oops...")
                                                    .setText(activity.getString(R.string.error_network_error))
                                                    .setIcon(R.drawable.ic_error)
                                                    .setBackgroundColorRes(R.color.colorRed)
                                                    .setIconColorFilter(0) // Optional - Removes white tint
                                                    .show();
                                        } else if (error instanceof ParseError) {

                                            Alerter.create(activity)
                                                    .setTitle("Oops...")
                                                    .setText(activity.getString(R.string.error_parse_error))
                                                    .setIcon(R.drawable.ic_error)
                                                    .setBackgroundColorRes(R.color.colorRed)
                                                    .setIconColorFilter(0) // Optional - Removes white tint
                                                    .show();
                                        }
                                    }
                                }
                                else
                                {
                                    Alerter.create(activity)
                                            .setTitle("Oops...")
                                            .setText(activity.getString(R.string.error_server_error))
                                            .setIcon(R.drawable.ic_error)
                                            .setBackgroundColorRes(R.color.colorRed)
                                            .setIconColorFilter(0) // Optional - Removes white tint
                                            .show();
                                }
                            }
                            else
                            {
                                Alerter.create(activity)
                                        .setTitle("Oops...")
                                        .setText(activity.getString(R.string.error_server_error))
                                        .setIcon(R.drawable.ic_error)
                                        .setBackgroundColorRes(R.color.colorRed)
                                        .setIconColorFilter(0) // Optional - Removes white tint
                                        .show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }) {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
//                MyPrefrences myPrefrences = new MyPrefrences(activity);
//                params.put("Authorization", "Bearer " + myPrefrences.getString("token",""));
                return params;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

//                params.put("imei_number", imei);


                return params;
            }

        };
        if(!isRequestInProcess) {
            isRequestInProcess = true;
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(50000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            RequestQueue requestQueue = Volley.newRequestQueue(activity);

            requestQueue.add(stringRequest);
        }
        else
        {
            Toast.makeText(activity, "A request is already in process. Please wait", Toast.LENGTH_SHORT).show();
        }
    }
    public static void parseJsonShowResult(String response, Activity activity){
        try {
            JSONObject mJsonObject = new JSONObject(response);
            Log.e("response",response);

            String imeiNumber = "N/A";
            if(mJsonObject.has("imei"))
                imeiNumber = mJsonObject.getString("imei");

            String brand = "N/A";
            if(mJsonObject.get("gsma") != null && !mJsonObject.isNull("gsma"))
                if(mJsonObject.getJSONObject("gsma").has("brand"))
                    brand = mJsonObject.getJSONObject("gsma").getString("brand");

            String model = "N/A";
            if(mJsonObject.get("gsma") != null && !mJsonObject.isNull("gsma"))
                if(mJsonObject.getJSONObject("gsma").has("model_name") && !mJsonObject.get("gsma").equals("null"))
                    model = mJsonObject.getJSONObject("gsma").getString("model_name");

            String status = "N/A";
            if(mJsonObject.getJSONObject("compliant")!=null && !mJsonObject.isNull("compliant"))
                if(mJsonObject.getJSONObject("compliant").has("status"))
                    status = mJsonObject.getJSONObject("compliant").getString("status");

            String link = "N/A";
            if(mJsonObject.getJSONObject("compliant")!=null && !mJsonObject.isNull("compliant"))
                if(mJsonObject.getJSONObject("compliant").has("link_to_help"))
                    link = mJsonObject.getJSONObject("compliant").getString("link_to_help");

            String date = "N/A";
            if(mJsonObject.getJSONObject("compliant")!=null && !mJsonObject.isNull("compliant"))
                if(mJsonObject.getJSONObject("compliant").has("block_date"))
                    date = mJsonObject.getJSONObject("compliant").getString("block_date");

            String reason = "N/A";
            if(mJsonObject.getJSONObject("compliant")!=null && !mJsonObject.isNull("compliant"))
                if(mJsonObject.getJSONObject("compliant").has("inactivity_reasons")) {
                    JSONArray reasonsArr = mJsonObject.getJSONObject("compliant").getJSONArray("inactivity_reasons");

                    for (int i = 0; i < reasonsArr.length(); i++) {
                        if (i == 0) {
                            reason = reasonsArr.getString(i);
                        } else {
                            reason = reason + ", " + reasonsArr.getString(i);
                        }
                    }
                }

            Intent intent = new Intent(activity, ResultActivity.class);
            intent.putExtra("imei", imeiNumber);
            intent.putExtra("brand", brand);
            intent.putExtra("model", model);
            intent.putExtra("status", status);
            intent.putExtra("link", link);
            intent.putExtra("date", date);
            intent.putExtra("reason", reason);
            activity.startActivity(intent);



        } catch (Exception e) {
            e.printStackTrace();
            Alerter.create(activity)
                    .setTitle("Oops...")
                    .setText(activity.getString(R.string.error_server_error))
                    .setIcon(R.drawable.ic_error)
                    .setBackgroundColorRes(R.color.colorRed)
                    .setIconColorFilter(0) // Optional - Removes white tint
                    .show();
        }
    }
    public static void hideShowLoading(ProgressBar progressBar, Button submitBtn, TextInputEditText scanBtn, ProgressDialog progressDialog, boolean showLoader){
        if(progressDialog == null) {
            if(showLoader)
            {
                progressBar.setVisibility(View.VISIBLE);
                submitBtn.setEnabled(false);
                scanBtn.setEnabled(false);
            }
            else{
                progressBar.setVisibility(View.GONE);
                submitBtn.setEnabled(true);
                scanBtn.setEnabled(true);
            }
        }
        else
        {
            if(showLoader){
                progressDialog.show();
            }
            else{
                progressDialog.hide();
            }
        }

    }
}
