# Visualizer
Visualizer is a real time application that takes the input sound from the pc and processed it through an FFT in order to display a spectrum representation of the sound.

## Install dependencies
1. Install openjdk 17
2. Install openjfx 17
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

## Compile
`mvn clean install`

## Run
cd ./spectrum
`java -Djava.library.path=./dll --module-path "/<pathToJavaFxHome>/lib" --add-modules=javafx.controls,javafx.fxml -cp ./lib -jar /spectrum-1.0.0-SNAPSHOT-jar-with-dependencies.jar`
