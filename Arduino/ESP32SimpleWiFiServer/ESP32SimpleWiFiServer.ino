
//Features to put in 
//SmartConfig
//ESPNow
#include <WiFi.h>
#include <ArduinoJson.h>

const char* ssid     = "MoHsiNkaBacha";
const char* password = "ipodtouch123";
boolean ledStatus =false;
double temperature=0.00;
double pulse=0.00;
double obstacle=0.00;
double alcohol=0.00;
double temperature_now=0.00;
double pulse_now=0.00;
double obstacle_now=0.00;
double alcohol_now=0.00;
unsigned int temperaturePin=32;
unsigned int pulsePin=33;
unsigned int alcoholPin=31;
unsigned int obstaclePin=30;
WiFiServer server(80);

void setup()
{
    Serial.begin(115200);
    pinMode(5, OUTPUT);      // set the LED pin mode

    delay(10);

    // We start by connecting to a WiFi network

    Serial.println();
    Serial.println();
    Serial.print("Creating ");
    Serial.println(ssid);
    WiFi.mode(WIFI_AP_STA);
    IPAddress apLocalIp(192, 168, 50, 1);
    IPAddress apSubnetMask(255, 255, 255, 0);
    WiFi.softAPConfig(apLocalIp, apLocalIp, apSubnetMask);
    WiFi.softAP(ssid, password);
    //while (WiFi.status() != WL_CONNECTED) {
        //delay(500);
        //Serial.print(".");
    //}
    
    Serial.println("");
    Serial.println("WiFi connected.");
    Serial.println("IP address: ");
    Serial.println(WiFi.softAPIP());
    server.begin();
    startMDNS();

}

int value = 0;

void loop(){
 temperature_now=analogRead(temperaturePin);
 pulse_now=analogRead(pulsePin);
 obstacle_now=rand()/100;
 alcohol_now=rand()/100;
 
 WiFiClient client = server.available();   // listen for incoming clients

  if (client) {                             // if you get a client,
       handleClient(&client);
  }
}
