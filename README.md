# Visualizer
Low frequency spectrum analyzer with support for Philips Hue.

## Install dependencies
1. Install java sdk from here: https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html
2. Install apache maven from here: https://maven.apache.org/download.cgi

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

## Hue integration requirement
I am not allowed to distribute the hue libraries or materials until hue edk comes out of beta.
Please follow the steps bellow in order to get access to the hue libraries and put them in the correct location:
1. Create an account and agree to the philips EDK terms and conditions here: https://developers.meethue.com/edk-terms-and-conditions/
2. Get access to the following git repository: https://github.com/PhilipsHue/HueSDK4EDK
3. Copy the files from https://github.com/PhilipsHue/HueSDK4EDK/tree/master/HueSDK/Windows to ./hue/sdk 

## Compile
`mvn clean install`

## Run
`java -Djava.library.path=./hue/sdk -cp ./target/visualizer-1.0-SNAPSHOT-jar-with-dependencies.jar;./hue/sdk/* com.lazydash.audio.visualizer.Main`

## Configure
Create an entertainment group called "bass" from the Official Philips Hue App: https://play.google.com/store/apps/details?id=com.philips.lighting.hue2&hl=en

## Use
1. Click on the screen of the visualiser window in order to open the settings window
2. Click on "Hue Integration" from the left sidebar
3. Click on the "start" button
4. Push the Philips Hue Bridge Push Link Button
5. Enjoy :)