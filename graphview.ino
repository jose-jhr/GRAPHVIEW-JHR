#define pot A0
#define temp A1
boolean sendData = false;

void setup() {
   Serial.begin(9600);
   pinMode(INPUT,temp);
   pinMode(INPUT,pot);
}

void loop() {
  if(Serial.available()>0){
    char date = Serial.read();
    sendData = date == '1'? true : false;
  }
 
  if(sendData){
    float potValue = analogRead(pot);
    //queremos que los datos esten entre 0 y 50
    potValue = (potValue*50)/1023;
    int tempValue = (analogRead(temp))/2;
    Serial.println("p"+String(potValue));
    delay(600);
    Serial.println("t"+String(tempValue));
    delay(600);
  }
  
  
}
