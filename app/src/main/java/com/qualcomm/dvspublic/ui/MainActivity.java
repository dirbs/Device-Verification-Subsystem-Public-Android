/*
 * Copyright (c) 2018-2019 Qualcomm Technologies, Inc.
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without modification, are permitted (subject to the limitations in the disclaimer below) provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 *  Neither the name of Qualcomm Technologies, Inc. nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 *  The origin of this software must not be misrepresented; you must not claim that you wrote the original software. If you use this software in a product, an acknowledgment is required by displaying the trademark/log as per the details provided here: [https://www.qualcomm.com/documents/dirbs-logo-and-brand-guidelines]
 *  Altered source versions must be plainly marked as such, and must not be misrepresented as being the original software.
 *  This notice may not be removed or altered from any source distribution.
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.qualcomm.dvspublic.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.qualcomm.dvspublic.App;
import com.qualcomm.dvspublic.R;
import com.qualcomm.dvspublic.model.ImeiResponse;
import com.qualcomm.dvspublic.utils.ApiResponse;
import com.qualcomm.dvspublic.utils.ConnectionDetector;
import com.qualcomm.dvspublic.viewmodel.ImeiStatusViewModel;
import com.qualcomm.dvspublic.viewmodel.ViewModelFactory;
import com.tapadoo.alerter.Alerter;

import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class MainActivity extends FragmentActivity {

    @BindView(R.id.imeiEt)
    public TextInputEditText imeiEt;
    @BindView(R.id.imeiTil)
    public TextInputLayout imeiTil;
    @BindView(R.id.submitBtn)
    public Button submitBtn;
    //    @BindView(R.id.openScannerBtn)
//    public ImageButton openScannerBtn;
    @BindView(R.id.progressBar)
    public ProgressBar progressBar;

    @Inject
    ViewModelFactory viewModelFactory;

    public ImeiStatusViewModel viewModel;

    // Invoked upon opening of MainActivity.
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String languageToLoad = "en"; // your language
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        imeiEt.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (imeiEt.getRight() - imeiEt.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    // your action here
                    Intent intent = new Intent(MainActivity.this, ScannerActivity.class);
                    MainActivity.this.startActivity(intent);
                    return true;
                }
            }
            return false;
        });

        if (getIntent().hasExtra("imei"))
            imeiEt.setText(getIntent().getStringExtra("imei"));

        ((App) getApplication()).getAppComponent().doInjection(this);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ImeiStatusViewModel.class);
        viewModel.imeiResponse().observe(this, this::consumeResponse);
    }

//    @OnClick(R.id.openScannerBtn)
//    public void openScanActivity() {
//        Intent intent = new Intent(MainActivity.this, ScannerActivity.class);
//        startActivity(intent);
//    }

    // For validating entered imei.
    @OnClick(R.id.submitBtn)
    public void validate() {
        final String imei = imeiEt.getText().toString();
        if (imei.length() >= 14 && imei.length() <= 16) {
            if (imei.matches("^(?=.[a-fA-F]*)(?=.[0-9]*)[a-fA-F0-9]+$")) {
                imeiTil.setError(null);
                ConnectionDetector cd = new ConnectionDetector(this);
                boolean isInternetPresent = cd.isConnectingToInternet();
                if (isInternetPresent) {
                    verifyCaptcha(imei, null);
                } else {
                    showNoInternetDialog(this);
                }
            } else {
                imeiTil.setError(getString(R.string.hexa_error));
            }
        } else {
            imeiTil.setError(getString(R.string.length_error));
        }
    }

    // For showing no internet connection dialog
    public static void showNoInternetDialog(final Activity context) {

        androidx.appcompat.app.AlertDialog.Builder logoutDialog = new androidx.appcompat.app.AlertDialog.Builder(context);
        LayoutInflater inflater = context.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.status_dialog, null);
        logoutDialog.setView(dialogView).setPositiveButton(context.getResources().getString(R.string.yes_enable), (dialog, whichButton) -> {
            dialog.dismiss();
            context.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));

        })
                .setNegativeButton(context.getResources().getString(R.string.no), (dialog, whichButton) -> dialog.dismiss());

        ImageView icon = dialogView.findViewById(R.id.icon);
        TextView title = dialogView.findViewById(R.id.title);
        TextView message = dialogView.findViewById(R.id.message);

        icon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_error));
        title.setText(R.string.no_internet);
        message.setText(R.string.error_network_error);

        androidx.appcompat.app.AlertDialog alertDialog = logoutDialog.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);

        alertDialog.show();
    }

    // Invoked in every key press on imei edit text field.
    @OnTextChanged(value = R.id.imeiEt, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void imeiTextChanged() {

        final String imei = imeiEt.getText().toString();
        boolean isValid;
        if (imei.length() < 14 || imei.length() > 16) {
            isValid = false;
            if (!imei.matches("^(?=.[a-fA-F]*)(?=.[0-9]*)[a-fA-F0-9]+$")) {
                imeiTil.setError(getString(R.string.hexa_error));
            } else {
                imeiTil.setError(getString(R.string.length_error));
            }
        } else {
            if (!imei.matches("^(?=.[a-fA-F]*)(?=.[0-9]*)[a-fA-F0-9]+$")) {
                imeiTil.setError(getString(R.string.hexa_error));
                isValid = false;
            } else {
                isValid = true;
            }
        }

        if (isValid)
            imeiTil.setError(null);
    }

    // Upon submit button click user is asked to verify a captcha if needed.
    public void verifyCaptcha(final String imei, String key) {

        if (key == null)
            key = MainActivity.this.getString(R.string.recaptcha_key);

        hideShowLoading(true);
        SafetyNet
                .getClient(MainActivity.this)
                .verifyWithRecaptcha(key).addOnCanceledListener(MainActivity.this, () -> hideShowLoading(false))
                .addOnSuccessListener(MainActivity.this,
                        response -> {
                            String userResponseToken = response.getTokenResult();
                            if (!userResponseToken.isEmpty()) {
                                // Recaptcha success
                                viewModel.hitGetStatusApi(imei, userResponseToken, "Android");
                            } else {
                                hideShowLoading(false);
                                showAlerter(getString(R.string.recaptcha_request_failed));
                            }
                        })
                .addOnFailureListener(MainActivity.this, e -> {
                    hideShowLoading(false);
                    if (e instanceof ApiException) {
                        // An error occurred when communicating with the
                        ApiException apiException = (ApiException) e;
                        int statusCode = apiException.getStatusCode();
                        showAlerter(CommonStatusCodes
                                .getStatusCodeString(statusCode));
                    } else {
                        // A different, unknown type of error occurred.
                        showAlerter(getString(R.string.recaptcha_request_failed));
                    }
                });
    }

    // For showing alert messages to user.
    private void showAlerter(String message) {
        Alerter.create(MainActivity.this)
                .setTitle("Oops...")
                .setText(message)
                .setIcon(R.drawable.ic_error)
                .setBackgroundColorRes(R.color.colorRed)
                .setIconColorFilter(0) // Optional - Removes white tint
                .show();
    }

    /*
     * method to handle response
     * */
    private void consumeResponse(ApiResponse apiResponse) {

        switch (apiResponse.status) {

            case LOADING:
                hideShowLoading(true);
                break;

            case SUCCESS:
                hideShowLoading(false);
                renderSuccessResponse(apiResponse.data);
                break;

            case ERROR:
                hideShowLoading(false);
                showAlerter(getString(R.string.problemConnectingToServer));
                break;

            default:
                break;
        }
    }

    // Method to decide what to do with response.
    private void renderSuccessResponse(JsonElement response) {
        if (!response.isJsonNull()) {
            Log.d("response=", response.toString());
            Gson gson = new Gson();
            final ImeiResponse imeiResponse = gson.fromJson(response.toString(), ImeiResponse.class);

            Intent intent = new Intent(MainActivity.this, ResultActivity.class);
            intent.putExtra("ImeiResponse", imeiResponse);
            MainActivity.this.startActivity(intent);
        } else {
            showAlerter(getString(R.string.problemConnectingToServer));
        }
    }

    // For hiding or showing loader.
    public void hideShowLoading(boolean showLoader) {
        if (showLoader) {
            progressBar.setVisibility(View.VISIBLE);
            submitBtn.setEnabled(false);
            imeiEt.setEnabled(false);
//            openScannerBtn.setEnabled(false);
        } else {
            progressBar.setVisibility(View.GONE);
            submitBtn.setEnabled(true);
            imeiEt.setEnabled(true);
//            openScannerBtn.setEnabled(true);
        }
    }
}
