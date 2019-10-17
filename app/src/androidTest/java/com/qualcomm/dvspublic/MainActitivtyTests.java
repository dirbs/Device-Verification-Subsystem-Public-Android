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
import android.util.Log;

import com.qualcomm.dvspublic.ui.MainActivity;
import com.qualcomm.dvspublic.ui.ScannerActivity;
import com.qualcomm.dvspublic.utils.Urls;
import com.qualcomm.dvspublic.utils.Utils;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;

import androidx.test.espresso.intent.Intents;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertFalse;

public class MainActitivtyTests {

    @Rule
    public ActivityTestRule<MainActivity> mMainActivityTestRule =
            new ActivityTestRule<>(MainActivity.class, true, true);
    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA);

    String imeiResponseStr = "{\n" +
            "    \"gsma\": {\n" +
            "        \"model_name\": \"RNE-L21\",\n" +
            "        \"brand\": \"HUAWEI\"\n" +
            "    },\n" +
            "    \"compliant\": {\n" +
            "        \"link_to_help\": \"dirbs.net.pk/help\",\n" +
            "        \"inactivity_reasons\": [\n" +
            "            \"Your device is not registered\"\n" +
            "        ],\n" +
            "        \"block_date\": \"N/A\",\n" +
            "        \"status\": \"Non compliant\"\n" +
            "    },\n" +
            "    \"imei\": \"86967603177684\"\n" +
            "}";
    @After
    public void resetBaseUrl(){
        Urls.BASE_URL = BuildConfig.BASE_URL;
    }

    @Test
    public void showImeiHexaError() {
        //can be replaced with any non alpha numeric characters b/w 14 to 16
        String imei = "1234567890qw,.;";

        //find enter imei edittext , populate it with IMEI and perform click on check IMEI button
        onView(withId(R.id.imeiEt)).perform(typeText(imei), closeSoftKeyboard());
        onView(withId(R.id.submitBtn)).perform(click());

        onView(withId(R.id.imeiTil)).check
                (matches(Utils.hasTextInputLayoutErrorText(mMainActivityTestRule.getActivity().getString(R.string.hexa_error))));
    }
    @Test
    public void showImeiShortError() {
        //can be replaced with any non alpha numeric characters b/w 14 to 16
        String imei = "1234567890";

        //find enter imei edittext , populate it with IMEI and perform click on check IMEI button
        onView(withId(R.id.imeiEt)).perform(typeText(imei), closeSoftKeyboard());
        onView(withId(R.id.submitBtn)).perform(click());

        onView(withId(R.id.imeiTil)).check
                (matches(Utils.hasTextInputLayoutErrorText(mMainActivityTestRule.getActivity().getString(R.string.length_error))));
    }
    @Test
    public void showImeiOK() {
        //can be replaced with any non alpha numeric characters b/w 14 to 16
        String imei = "123456789012345";

        //find enter imei edittext , populate it with IMEI and perform click on check IMEI button
        onView(withId(R.id.imeiEt)).perform(typeText(imei), closeSoftKeyboard());

        onView(withId(R.id.imeiTil)).check
                (matches(Utils.hasTextInputLayoutErrorText(null)));

    }
    @Test
    public void recaptchaOk() throws Throwable {

        androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override public void run() {
                Log.e("recaptcha","running the instrumentation run");
                mMainActivityTestRule.getActivity().verifyCaptcha( "123456789012345", null);

            }
        });
        Thread.sleep(10000);
        androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().waitForIdleSync();
//        onView(withId(R.id.imeiEt)).check(matches(not(isEnabled())));
//        onView(withId(R.id.submitBtn)).check(matches(not(isEnabled())));
    }
    @Test
    public void recaptchaError() throws Throwable {
        final String key = "invalid key test";

        androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override public void run() {
                Log.e("recaptcha","running the instrumentation run");
                mMainActivityTestRule.getActivity().verifyCaptcha( "123456789012345", key);

            }
        });
        Thread.sleep(10000);
        androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        onView(withId(R.id.imeiEt)).check(matches(isEnabled()));
        onView(withId(R.id.submitBtn)).check(matches(isEnabled()));

    }

    @Test
    public void testImeiApiCall(){
        MockWebServer server = new MockWebServer();
//      Schedule some responses.
        server.enqueue(new MockResponse().setResponseCode(200).setBody(imeiResponseStr));

//      Start the server.
        try {
            server.start();
            Urls.BASE_URL = server.url("/").toString();
            androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
                @Override public void run() {
                    ((MainActivity)mMainActivityTestRule.getActivity()).viewModel.hitGetStatusApi("86967603177684", "test_token", "Android");
                }
            });
            androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().waitForIdleSync();

//            assertFalse(mMainActivityTestRule.getActivity().getWindow().getDecorView().getRootView().isShown());
            server.shutdown();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Test
    public void testImeiApiErrorCall(){
        MockWebServer server = new MockWebServer();
//      Schedule some responses.
        server.enqueue(new MockResponse().setResponseCode(500).setBody("Server Error"));
//      Start the server.
        try {
            server.start();
            Urls.BASE_URL = server.url("/").toString();
            Thread.sleep(2000L);
            androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
                @Override public void run() {
                    ((MainActivity)mMainActivityTestRule.getActivity()).viewModel.hitGetStatusApi("86967603177684", "test_token", "Android");
                }
            });
            androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().waitForIdleSync();
            assertTrue(mMainActivityTestRule.getActivity().getWindow().getDecorView().getRootView().isShown());
            server.shutdown();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }



    }
    @Test
    public void openScannerTest(){
        Intents.init();
        onView(withId(R.id.openScannerBtn)).perform(click());
        intended(hasComponent(ScannerActivity.class.getName()));
        Intents.release();

    }



}
