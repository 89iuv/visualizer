# Visualizer
Low frequency spectrum analyzer with support for Philips HUE.

# Environment
  - java 1.8.0_202
  - maven 3.3.9

# Install
1. Install java sdk from here: https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html
2. Install apache maven from here: https://maven.apache.org/download.cgi

# Audio
1. Right click on windows volume icon from the try and select sounds.
2. Click on the recording tab.
3. Set "Stereo Mix" as the default device.

# Optional
If you do not have a "Stereo Mix" device you will have to use "Virtual Cable"
1. Download and install "VCable" from here: https://www.vb-audio.com/Cable/index.htm
2. Set "VCable" as the default device.

# Setup
1. Create an account and agree to the philips EDK terms and conditions here: https://developers.meethue.com/login/?redirect_to=https%3A%2F%2Fdevelopers.meethue.com%2Fedk-terms-and-conditions%2F
2. Get access to the following git repository: https://github.com/PhilipsHue/HueSDK4EDK
3. Copy the files from https://github.com/PhilipsHue/HueSDK4EDK/tree/master/HueSDK/Windows to ./hue/sdk
 
# Compile
`mvn clean install`

# Run
`java -Djava.library.path=./hue/sdk -cp ./target/visualizer-1.0-SNAPSHOT-jar-with-dependencies.jar;./hue/sdk/* com.lazydash.audio.visualiser.Main`

# Use
1. Create an entertainment group called "bass" from the Official Philips Hue App: https://play.google.com/store/apps/details?id=com.philips.lighting.hue2&hl=en
2. Click on the screen of the visualiser window in order to open the settings window
4. Click on "Hue Integration" from the left sidebar
5. Click on the "start" button
6. Push the Philips Hue Bridge Push Link Button
7. Enjoy :)
