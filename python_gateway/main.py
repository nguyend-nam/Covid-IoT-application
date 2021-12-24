import sys
import random
import time
import serial.tools.list_ports
import requests

from Adafruit_IO import *

AIO_FEED_ID = ""	# Adafruit IO feed Id
AIO_USERNAME = ""	# Adafruit IO username
AIO_KEY = ""		# Adafruit IO api key


def connected(client):
    print("Ket noi thanh cong ...")
    client.subscribe(AIO_FEED_ID)


def subscribe(client , userdata , mid , granted_qos):
    print("Subscribe thanh cong ...")


def disconnected(client):
    print("Ngat ket noi ...")
    sys.exit (1)


def message(client , feed_id , payload):
    print("Nhan du lieu: " + payload)


client = MQTTClient(AIO_USERNAME , AIO_KEY)
client.on_connect = connected
client.on_disconnect = disconnected
client.on_message = message
client.on_subscribe = subscribe
client.connect ()
client.loop_background ()

# while True:
#     pass

def getPort():
    ports = serial.tools.list_ports.comports()
    N = len(ports)
    commPort = "None"

    for i in range (0, N):
        port = ports[i]
        strPort = str(port)

        if "com0com" in strPort:
            splitPort = strPort.split(" ")
            commPort = (splitPort[0])

    print(commPort)
    return commPort


ser = serial.Serial(port=getPort(), baudrate=115200)


mess = ""
temperature = ""
humidity = ""

def processData(data):
    data = data.replace("!", "")
    data = data.replace("#", "")
    splitData = data.split (":")
    #print(splitData)

    if splitData[0] == "MODE":
        temperature = splitData[1]
        #client.publish("bbc - temp", splitData[1])
        print("Mode: " + temperature)

    else:
        print("Invalid syntax")
        break

    if temperature == "0":
        print("Mode 0: Cases")
        client.publish("bbc - led", 0)
        response = requests.get("https://covid-api.mmediagroup.fr/v1/cases?country=")
        value = response.json()['Vietnam']['All']['confirmed']
        client.publish("bbc - temp", value)
        print(value)

        response1 = requests.get("https://covid-api.mmediagroup.fr/v1/cases?country=")
        value1 = response1.json()['Vietnam']['All']['deaths']
        client.publish("bbc - temp - 1", value1)
        print(value1)

    elif temperature == "1":
        print("Mode 1: Vaccines")
        client.publish("bbc - led", 1)
        response2 = requests.get("https://covid-api.mmediagroup.fr/v1/vaccines?country=Vietnam")
        value2 = response2.json()['All']['people_vaccinated']
        client.publish("bbc - humid", value2)
        print(value2)

        response3 = requests.get("https://covid-api.mmediagroup.fr/v1/vaccines?country=Vietnam")
        value3 = response3.json()['All']['people_partially_vaccinated']
        client.publish("bbc - humid - 1", value3)
        print(value3)
    print("\n")


mess = ""
def readSerial () :
    bytesToRead = ser.inWaiting()
    if ( bytesToRead > 0) :
        global mess
        mess = mess + ser.read(bytesToRead).decode("UTF -8")
        while ("#" in mess) and ("!" in mess):
            start = mess.find("!")
            end = mess.find("#")
            processData(mess[start:end + 1])
            if ( end == len( mess )) :
                mess = ""
            else :
                mess = mess[end +1:]



while True:
    readSerial()

    time.sleep(10)

