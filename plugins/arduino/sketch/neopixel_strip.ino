#include <Adafruit_NeoPixel.h>

#define PIN 6

int leds = 48;
float msDelay = 1000 / 60;

Adafruit_NeoPixel strip = Adafruit_NeoPixel(leds, PIN, NEO_GRB + NEO_KHZ800);


void setup() {
  // put your setup code here, to run once:
  Serial.begin(115200);
  while (!Serial) {
    ; // wait for serial port to connect. Needed for native USB
  }
  
  strip.begin();
  strip.setBrightness(255);
  strip.show(); // Initialize all pixels to 'off'

}

void loop() {
  // put your main code here, to run repeatedly:
  // reply only when you receive data:
  
  Serial.println("refresh");
  // wait for serial to fill

  while (Serial.available() == 0) {
    delay(1);
  }
  
  if (Serial.available() > 0) {
    // read the incoming byte:

    byte buffer[leds * 3];
    for (int i = 0; i < leds * 3; i++) {
      buffer[i] = 0;
    }
    
    Serial.readBytes(buffer, leds * 3);

    int k = 0;
    for (int i=0; i<leds; i++) {
      byte r = buffer[k++];
      byte g = buffer[k++];
      byte b = buffer[k++];

      strip.setPixelColor(i, r, g, b);

    }

    strip.show();
  }

  delay(msDelay);
}
