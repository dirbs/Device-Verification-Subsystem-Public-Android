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

#### Documentation

[DVS-API-Installation-Guide-1.0.0.pdf](https://github.com/dirbs/Documentation/blob/master/Device-Verification-Subsystem/DVS-API-Installation-Guide-1.0.0.pdf)<br />
[DVS-SPA-Installation-Guide-Authority-App-1.0.0.pdf](https://github.com/dirbs/Documentation/blob/master/Device-Verification-Subsystem/DVS-SPA-Installation-Guide-Authority-App-1.0.0.pdf)<br />
[DVS-SPA-Installation-Guide-Public-App-1.0.0.pdf](https://github.com/dirbs/Documentation/blob/master/Device-Verification-Subsystem/DVS-SPA-Installation-Guide-Public-App-1.0.0.pdf)<br />
[DVS-Authority-Web-App-UserGuide-1.0.0.pdf](https://github.com/dirbs/Documentation/blob/master/Device-Verification-Subsystem/DVS-Authority-Web-App-UserGuide-1.0.0.pdf)<br />
[DVS-Public-Web-App- User-Guide-1.0.0.pdf](https://github.com/dirbs/Documentation/blob/master/Device-Verification-Subsystem/DVS-Public-Web-App-%20User-Guide-1.0.0.pdf)<br />
[DVS-Developer-Guide-iOS-App-Authority-1.0.0.pdf](https://github.com/dirbs/Documentation/blob/master/Device-Verification-Subsystem/DVS-Developer-Guide-iOS-App-Authority-1.0.0.pdf)<br />
[DVS-Developer-Guide-iOS-App-Public-1.0.0.pdf](https://github.com/dirbs/Documentation/blob/master/Device-Verification-Subsystem/DVS-Developer-Guide-iOS-App-Public-1.0.0.pdf)<br />
[DVS-Developer-Guide-Android-App-Authority-1.0.0.pdf](https://github.com/dirbs/Documentation/blob/master/Device-Verification-Subsystem/DVS-Developer-Guide-Android-App-Authority-1.0.0.pdf)<br />
[DVS-Developer-Guide-Android-App-Public-1.0.0.pdf](https://github.com/dirbs/Documentation/blob/master/Device-Verification-Subsystem/DVS-Developer-Guide-Android-App-Public-1.0.0.pdf)<br />
[DVS-Authority-Mobile-App-User-Guide-1.0.0.pdf](https://github.com/dirbs/Documentation/blob/master/Device-Verification-Subsystem/DVS-Authority-Mobile-App-User-Guide-1.0.0.pdf)<br />
[DVS -Public-Mobile- App-User-Guide-1.0.0.pdf](https://github.com/dirbs/Documentation/blob/master/Device-Verification-Subsystem/DVS%20-Public-Mobile-%20App-User-Guide-1.0.0.pdf)<br />

#### Frontend Application Repos

[Device-Verification-Subsystem-Authority-Frontend](https://github.com/dirbs/Device-Verification-Subsystem-Authority-Frontend)<br />
[Device-Verification-Subsystem-Public-Frontend](https://github.com/dirbs/Device-Verification-Subsystem-Public-Frontend)<br />

#### Backend Application Repos

[Device-Verification-Subsystem](https://github.com/dirbs/Device-Verification-Subsystem)<br />

#### Mobile Application Repos

[Device-Verification-Subsystem-Authority-Android](https://github.com/dirbs/Device-Verification-Subsystem-Authority-Android)<br />
[Device-Verification-Subsystem-Authority-iOS](https://github.com/dirbs/Device-Verification-Subsystem-Authority-iOS)<br />
[Device-Verification-Subsystem-Public-iOS](https://github.com/dirbs/Device-Verification-Subsystem-Public-iOS)<br />

© 2016-2018 Qualcomm Technologies, Inc. All rights reserved.
