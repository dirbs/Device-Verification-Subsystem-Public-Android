# DVS Public Android App
##	System Requirements
###	Software Requirements
-	JDK 1.8 or more
-	Android Studio 3.1.2
-	Android SDK v27 (minimum)
-	Minimum Android version 16
-	Gradle 4.4
-	Android Plugin version 3.1.2

##	reCAPTCHA Configuration
For reCAPTCHA configurations go to this [link](https://www.google.com/recaptcha/admin#list):
1.	In Register a new site section enter label for app e.g. Android DVS Public.
2.	Select “Android” type of reCAPTCHA.
3.	Enter Package Name.
4.	Accept the reCAPTCHA Terms of Service
5.	Click on “Register” button.
6.	Copy “Site Key”.

##	App Configuration
-	To change the logo of app go to app/res/drawable folder and paste logo file but make sure the file name should be “company_logo.png”
-	To change the colors of the app go to app/res/values/colors.xml file and mention hex color code of required color
-	Open app/res/values/strings.xml file and add site key (copied in section 3.1 step 6) in value section of “reCAPTCHA_key”
`<string name=”reCAPTCHA_key”>ENTER RECAPTCHA KEY HERE</string>`
-	Add API Gateway URL in value section of “api_gateway_url”
`<string name=”api_gateway_url”>ENTER API GATEWAY URL HERE</string>`

© 2016-2018 Qualcomm Technologies, Inc. All rights reserved.
