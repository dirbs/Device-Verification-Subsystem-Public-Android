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

package com.qualcomm.dvspublic;

import android.Manifest;
import android.app.Activity;
import android.os.Build;

import com.google.zxing.Result;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.qualcomm.dvspublic.ui.MainActivity;
import com.qualcomm.dvspublic.ui.ScannerActivity;
import com.qualcomm.dvspublic.utils.Utils;

import androidx.test.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.runner.AndroidJUnit4;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.InstrumentationRegistry.getInstrumentation;
import static androidx.test.InstrumentationRegistry.getTargetContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static com.qualcomm.dvspublic.utils.EspressoTestMatcher.withDrawable;

/**
 * Created by Hamza on 24/04/2017.
 */
@RunWith(AndroidJUnit4.class)
public class ScannerActivityTests {
    @Rule
    public ActivityTestRule<ScannerActivity> mScannerActivityTestRule =
            new ActivityTestRule<>(ScannerActivity.class, true, true);
    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA);
    @Test
    public void testflashBtnClick() {
        //can be replaced with any non alpha numeric characters b/w 14 to 16
        String imei = "1234567890qw,.;";

        Activity scannerActivity = mScannerActivityTestRule.getActivity();
        boolean isFlash = ((ScannerActivity) scannerActivity).isFlash;
        if(((ScannerActivity) scannerActivity).hasFlash()) {
            onView(withId(R.id.flashBtn)).perform(click());
            InstrumentationRegistry.getInstrumentation().waitForIdleSync();
            Assert.assertEquals(((ScannerActivity) scannerActivity).isFlash, !isFlash);
        }

    }
    @Test
    public void testToggleFlash() {
        //can be replaced with any non alpha numeric characters b/w 14 to 16
        String imei = "1234567890qw,.;";

        final Activity scannerActivity = mScannerActivityTestRule.getActivity();
        final boolean isFlash = ((ScannerActivity) scannerActivity).isFlash;
        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((ScannerActivity) scannerActivity).toggleFlash();
                    Assert.assertEquals(((ScannerActivity) scannerActivity).isFlash, !isFlash);
                }
            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

    }
    @Test
    public void testToggleFlashOn() {
        //can be replaced with any non alpha numeric characters b/w 14 to 16
        String imei = "1234567890qw,.;";

        final Activity scannerActivity = mScannerActivityTestRule.getActivity();
        final boolean isFlash = ((ScannerActivity) scannerActivity).isFlash;
        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((ScannerActivity) scannerActivity).toggleFlash();
                    Assert.assertEquals(((ScannerActivity) scannerActivity).isFlash, !isFlash);
                }
            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

    }
    @Test
    public void testFlashBtnIcon() {
        //can be replaced with any non alpha numeric characters b/w 14 to 16
        String imei = "1234567890qw,.;";

        Activity scannerActivity = mScannerActivityTestRule.getActivity();

        if(((ScannerActivity)scannerActivity).isFlash)
            onView(withId(R.id.flashBtn)).check(matches(withDrawable(R.drawable.ic_action_flash_on)));
        else
            onView(withId(R.id.flashBtn)).check(matches(withDrawable(R.drawable.ic_action_flash_off)));

    }
    @Test
    public void testScannerActivityHomeButton() {
        Activity scannerActivity = mScannerActivityTestRule.getActivity();

        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click());

    }
    @Test
    public void showImeiShortLengthError() {
//        can be replaced with any alpha numeric characters less than 14
        String imei = "1234567890asd";
        Activity scannerActivity = mScannerActivityTestRule.getActivity();
        Result resultZxing = new Result(imei, null, null ,null);
        BarcodeResult result = new BarcodeResult(resultZxing, null);
        ((ScannerActivity)scannerActivity).callback.barcodeResult(result);

        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        onView(withId(R.id.submitBtn)).perform(click());
        onView(withId(R.id.imeiTil)).check
                (matches(Utils.hasTextInputLayoutErrorText(mScannerActivityTestRule.getActivity().getString(R.string.length_error))));

    }
    @Test
    public void showImeiLongLengthError() {
        //can be replaced with any non alpha numeric characters b/w 14 to 16
        String imei = "1234567890qw,.;";

        Activity scannerActivity = mScannerActivityTestRule.getActivity();
        Result resultZxing = new Result(imei, null, null ,null);
        BarcodeResult result = new BarcodeResult(resultZxing, null);
        ((ScannerActivity)scannerActivity).callback.barcodeResult(result);

        getInstrumentation().waitForIdleSync();
        onView(withId(R.id.submitBtn)).perform(click());
        onView(withId(R.id.imeiTil)).check
                (matches(Utils.hasTextInputLayoutErrorText(mScannerActivityTestRule.getActivity().getString(R.string.hexa_error))));
    }

}
