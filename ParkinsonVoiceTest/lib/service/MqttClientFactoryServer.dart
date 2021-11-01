import 'dart:io';

import 'package:encrypt/encrypt.dart';
import 'package:intl/intl.dart';
import 'package:mqtt_client/mqtt_client.dart';
import 'package:mqtt_client/mqtt_server_client.dart';
import 'package:parkinson_voice_test/entity/Boxes.dart';
import 'package:parkinson_voice_test/entity/Test.dart';
import 'package:parkinson_voice_test/service/IClient.dart';
import 'package:hive/hive.dart';
import 'package:hive_flutter/hive_flutter.dart';
import 'package:encrypt/encrypt.dart' hide SecureRandom, Key;
import 'package:pointycastle/asymmetric/api.dart';
import "package:pointycastle/export.dart" hide State;



Future<MqttClient> makeClient(String url, String subscriberId, IClient httpClient) async {
  var subscriber = MqttServerClient(url, subscriberId);
  Box box = Boxes.getTests();
  subscriber.logging(on: true);
  subscriber.port = 1883;
  subscriber.onConnected = onConnected;
  subscriber.onDisconnected = onDisconnected;
  subscriber.onSubscribed = onSubscribed;
  subscriber.onSubscribeFail = onSubscribeFail;
  subscriber.pongCallback = pong;
  subscriber.keepAlivePeriod = 60;

   final connMess = MqttConnectMessage()
      .withClientIdentifier("flutter")
      .withWillTopic('willtopic')
      .withWillMessage('My Will message')
      //.startClean()
      .withWillQos(MqttQos.atLeastOnce);
  subscriber.connectionMessage = connMess;
  try {
    print('Connecting');
    await subscriber.connect();
  } catch (e) {
    print('Exception: $e');
    subscriber.disconnect();
  }

  if (subscriber.connectionStatus!.state == MqttConnectionState.connected) {
    print('client connected');
    print('Subscribing to the topic/tests topic');
    const topic = 'topic/tests';
    subscriber.subscribe(topic, MqttQos.atLeastOnce);

    subscriber.updates!.listen((List<MqttReceivedMessage<MqttMessage>> c) {
      
      final MqttPublishMessage message = c[0].payload as MqttPublishMessage;
      final payload = MqttPublishPayload.bytesToStringAsString(message.payload.message);
      print('Received message:"$payload" from topic: ${c[0].topic}>');
      
      Test? t = box.get(int.parse(payload));
      if(t!=null){
        httpClient.getResult(t.publickey[0], t.publickey[1]).then((value) {
          t.end =  DateFormat("dd/MM/yyyy HH:mm:ss").format(DateTime.now());

          RSAPrivateKey priv = new RSAPrivateKey(BigInt.parse(t.privatekey[0]), BigInt.parse(t.privatekey[1]), 
            BigInt.parse(t.privatekey[2]), BigInt.parse(t.privatekey[3]));

          final encrypter = Encrypter(RSA(publicKey: null, privateKey: priv));
          final decrypted = encrypter.decrypt(Encrypted.fromBase64(value));
          t.result = decrypted;
          box.put(t.testid, t);
        }).onError((error, stackTrace) {
          print("Error: " + error.toString());
        });
      }
    });

  } else {
    print(
        'client connection failed - disconnecting, status is ${subscriber.connectionStatus}');
    subscriber.disconnect();
    exit(-1);
  }

  return subscriber;
}


void onConnected() {
  print('Connected');
}

void onDisconnected() {
  print('Disconnected');
}

void onSubscribed(String topic) {
  print('Subscribed topic: $topic');
}

void onSubscribeFail(String topic) {
  print('Failed to subscribe topic: $topic');
}

void pong() {
  print('Ping response client callback invoked');
}