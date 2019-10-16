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

import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import com.google.gson.Gson;
import com.qualcomm.dvspublic.model.ImeiResponse;
import com.qualcomm.dvspublic.ui.ResultActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import androidx.test.espresso.intent.Intents;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasData;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertFalse;

public class ResultActivityTests {
    @Rule
    public ActivityTestRule<ResultActivity> mResultActivityTestRule =
            new ActivityTestRule<>(ResultActivity.class, true, false);
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

    @Before
    public void setUp() {
        Intent intent = new Intent();

        Gson gson = new Gson();
        final ImeiResponse imeiResponse = gson.fromJson(imeiResponseStr, ImeiResponse.class);
        intent.putExtra("ImeiResponse", imeiResponse);
        mResultActivityTestRule.launchActivity(intent);
    }


    @Test
    public void linkClickTest() {

        Intents.init();

        onView(withId(R.id.link_to_help_txt)).perform(click());

        intended(allOf(hasAction(Intent.ACTION_VIEW), hasData(Uri.parse("http://dirbs.net.pk/help"))));

        Intents.release();

    }

    @Test
    public void checkView() {
        onView(withId(R.id.imei_txt)).check(matches(withText("86967603177684")));
        onView(withId(R.id.brand_txt)).check(matches(withText("HUAWEI")));
        onView(withId(R.id.model_name_txt)).check(matches(withText("RNE-L21")));
        onView(withId(R.id.imei_compliance_status_txt)).check(matches(withText("Non compliant")));
        onView(withId(R.id.link_to_help_txt)).check(matches(withText("dirbs.net.pk/help")));
        onView(withId(R.id.block_as_of_date_txt)).check(matches(withText("N/A")));
        onView(withId(R.id.reason_noncompliance_txt)).check(matches(withText("Your device is not registered")));

        mResultActivityTestRule.getActivity().imeiResponse.setImei("123456789012345");
        mResultActivityTestRule.getActivity().imeiResponse.getGsma().setBrand("LG");
        mResultActivityTestRule.getActivity().imeiResponse.getGsma().setModelName("1");
        mResultActivityTestRule.getActivity().imeiResponse.getCompliant().setBlockDate("1/1/2019");
        mResultActivityTestRule.getActivity().imeiResponse.getCompliant().setLinkToHelp("dirbs.net/help");
        List<String> list = new ArrayList<>();
        list.add("N/A");
        mResultActivityTestRule.getActivity().imeiResponse.getCompliant().setInactivityReasons(list);
        mResultActivityTestRule.getActivity().imeiResponse.getCompliant().setStatus("Compliant");
        mResultActivityTestRule.getActivity().binding.invalidateAll();

        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        try {
            Thread.sleep(1000);
            onView(withId(R.id.imei_txt)).check(matches(withText("123456789012345")));
            onView(withId(R.id.brand_txt)).check(matches(withText("LG")));
            onView(withId(R.id.model_name_txt)).check(matches(withText("1")));
            onView(withId(R.id.imei_compliance_status_txt)).check(matches(withText("Compliant")));
            onView(withId(R.id.link_to_help_txt)).check(matches(withText("dirbs.net/help")));
            onView(withId(R.id.block_as_of_date_txt)).check(matches(withText("1/1/2019")));
            onView(withId(R.id.reason_noncompliance_txt)).check(matches(withText("N/A")));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }


    @Test
    public void closeBtnTest() {
        onView(withId(R.id.cancelBtn)).perform(click());
    }
    @Test
    public void imeiResponseModelTest(){
        Gson gson = new Gson();
        final ImeiResponse imeiResponse = gson.fromJson(imeiResponseStr, ImeiResponse.class);
        imeiResponse.setImei("86967603177684");
        imeiResponse.getGsma().setBrand("HUAWEI");
        imeiResponse.getGsma().setModelName("RNE-L21");
        imeiResponse.getCompliant().setBlockDate("N/A");
        imeiResponse.getCompliant().setLinkToHelp("dirbs.net.pk/help");
        imeiResponse.getCompliant().setReason("Your device is not registered");
        imeiResponse.getCompliant().setStatus("Non compliant");

        assertEquals(imeiResponse.getImei(), "86967603177684");
        assertEquals(imeiResponse.getGsma().getBrand(), "HUAWEI");
        assertEquals(imeiResponse.getGsma().getModelName(), "RNE-L21");
        assertEquals(imeiResponse.getCompliant().getBlockDate(), "N/A");
        assertEquals(imeiResponse.getCompliant().getLinkToHelp(), "dirbs.net.pk/help");
        assertEquals(imeiResponse.getCompliant().getReason(), "Your device is not registered");
        assertEquals(imeiResponse.getCompliant().getStatus(), "Non compliant");

        assertEquals(imeiResponse.toString(),"ImeiResponse{" +
                "gsma = '" + imeiResponse.getGsma() + '\'' +
                ",compliant = '" + imeiResponse.getCompliant() + '\'' +
                ",imei = '" + imeiResponse.getImei() + '\'' +
                "}");
        assertEquals(imeiResponse.getGsma().toString(), "Gsma{" +
                "model_name = '" + imeiResponse.getGsma().getModelName() + '\'' +
                ",brand = '" + imeiResponse.getGsma().getBrand() + '\'' +
                "}");
        assertEquals(imeiResponse.getCompliant().toString(), "Compliant{" +
                "link_to_help = '" + imeiResponse.getCompliant().getLinkToHelp() + '\'' +
                ",block_date = '" + imeiResponse.getCompliant().getBlockDate() + '\'' +
                ",inactivity_reasons = '" + imeiResponse.getCompliant().getInactivityReasons() + '\'' +
                ",status = '" + imeiResponse.getCompliant().getStatus() + '\'' +
                "}");



    }
}
