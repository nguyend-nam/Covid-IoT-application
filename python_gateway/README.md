> code skeleton: [nhanksd85](https://github.com/nhanksd85)\
> application idea and modify: [Dinh Nam Nguyen](https://github.com/NguyenD-Nam)
<br>

The python source includes:
- Importing Adafruit IO library for connecting and subscribing to [Adafruit IO server](https://io.adafruit.com/) with MQTT protocol.
- Establishing serial connection to gateway with [com0com](http://com0com.sourceforge.net/) and hercules application.
- Proccessing data transmitted by hercules in format `!MODE:0##` for mode 0 (reading cases data) and `!MODE:1##` for mode 1 (reading vaccination data).
- Importing requests library for fetching JSON API to get [Covid-19 stats](https://github.com/M-Media-Group/Covid-19-API) and then publish to Adafruit IO server.

*Optional*:
- Working with ThingsBoard server using PAHO MQTT.
