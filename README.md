# Visualizer
Java audio spectrum analyzer with support for hue lights.

## Install dependencies
1. Install openjdk 11
2. Install openjfx 11
3. Install apache maven

## Audio Setup
1. Right click on windows volume icon from the app tray and select sounds
2. Click on the recording tab
3. Set "Stereo Mix" as the default device

## Optional Audio Setup
If you do not have a "Stereo Mix" device you will have to use "Virtual Cable"
1. Download and install "Virtual Cable" from here: https://www.vb-audio.com/Cable/index.htm
2. Set "CABLE Input" as the default device for playback
3. Set "CABLE Output" as the default device for recording
4. Open the properties window for "CABLE Output"
5. Click on the "Listen" tab
6. Enable the "Listen to this device" feature
7. Select the audio device that you want as the playback device. (ex: "Realtek High Definition Audio")
8. Apply and close

## Hue plugin requirement
I am not allowed to distribute the hue libraries or materials until hue edk comes out of beta.
Please follow the steps bellow in order to get access to the hue libraries and put them in the correct location:
1. Create an account and agree to the philips EDK terms and conditions here: https://developers.meethue.com/edk-terms-and-conditions/
2. Get access to the following git repository: https://github.com/PhilipsHue/HueSDK4EDK
3. Go to https://github.com/PhilipsHue/HueSDK4EDK/tree/master/HueSDK/Windows and copy the following files:
    4. jar file to: ./lib
    5. dll file to: ./dll 

## Compile
`mvn clean install`

## Hue plugin installation:
Copy hue/target/hue-1.0.0-SNAPSHOT-plugin.jar to spectrum/plugins

## Run
`java -Djava.library.path=./dll --module-path "C:\Program Files\Java\javafx-sdk-11.0.2\lib" --add-modules=javafx.controls,javafx.fxml -cp ./target/spectrum-1.0.0-SNAPSHOT-jar-with-dependencies.jar Main`

## Configure
Create an entertainment group called "bass" from the Official Philips Hue App: https://play.google.com/store/apps/details?id=com.philips.lighting.hue2&hl=en

## Use
1. Click on the screen of the visualiser window in order to open the settings window
2. Click on "Hue Integration" from the left sidebar
3. Click on the "start" button
4. Push the Philips Hue Bridge Push Link Button (only once)
5. Enjoy :)