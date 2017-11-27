void handleClient(WiFiClient *client){
    Serial.println("New Client.");           // print a message out the serial port
    String currentLine = "";                // make a String to hold incoming data from the client
    while (client->connected()) {            // loop while the client's connected
      if (client->available()) {             // if there's bytes to read from the client,
        char c = client->read();             // read a byte, then
        Serial.write(c);                    // print it out the serial monitor
        if (c == '\n') {                    // if the byte is a newline character

          // if the current line is blank, you got two newline characters in a row.
          // that's the end of the client HTTP request, so send a response:
          if (currentLine.length() == 0) {
            // HTTP headers always start with a response code (e.g. HTTP/1.1 200 OK)
            // and a content-type so the client knows what's coming, then a blank line:
            client->println("HTTP/1.1 200 OK");
            client->println("Content-type:text/html");
            client->println();

            // the content of the HTTP response follows the header:
            //client->print("Click <a href=\"/H\">here</a> to turn the LED on pin 5 on.<br>");
            //client->print("Click <a href=\"/L\">here</a> to turn the LED on pin 5 off.<br>");
            temperature=temperature_now;
            pulse=pulse_now;
            obstacle=obstacle_now;
            alcohol=alcohol_now;
            client->print("Temp: ");
            client->print(temperature);
            client->print(" Pulse: ");
            client->print(pulse);
            client->print(" Obstc: ");
            client->print(obstacle);
            client->print(" Alkoh: ");
            client->print(alcohol);
            client->print(" End");
            // The HTTP response ends with another blank line:
            client->println();
            // break out of the while loop:
            break;
          } else {    // if you got a newline, then clear currentLine:
            currentLine = "";
          }
        } else if (c != '\r') {  // if you got anything else but a carriage return character,
          currentLine += c;      // add it to the end of the currentLine
        }

        // Check to see if the client request was "GET /H" or "GET /L":
        if (currentLine.endsWith("GET /H")) {
          ledStatus=true;
        }
        if (currentLine.endsWith("GET /L")) {
          ledStatus=false;
        }
      }
    }
    // close the connection:
    client->stop();
    Serial.println("Client Disconnected.");
}
