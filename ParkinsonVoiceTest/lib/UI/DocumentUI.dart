import 'dart:math';
import 'dart:typed_data';
import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:parkinson_voice_test/UI/RecorderUI.dart';
import 'package:parkinson_voice_test/entity/Boxes.dart';
import 'package:parkinson_voice_test/entity/Test.dart';
import 'package:parkinson_voice_test/service/IClient.dart';
import 'HomeUI.dart';
import 'package:pointycastle/asymmetric/api.dart';
import "package:pointycastle/export.dart" hide State;
import 'package:hive/hive.dart';
import 'package:hive_flutter/hive_flutter.dart';

class DocumentUI extends StatefulWidget {
  final IClient _client;
  final _value;
  DocumentUI(this._value, this._client);

  @override
  _DocumentUIState createState() => _DocumentUIState();
}

class _DocumentUIState extends State<DocumentUI> {
  String? _sample, _level;
  int _counter = 0;
  Future<List<String>> _samples = Future.value([]);
  List<String> _samplelist = [];
  bool pressed = false;
  late final Box box;
  int _testing = 0;
  List<String> publicKey = [];
  List<String> privateKey = [];


  @override
  void initState() {
    super.initState();
    box = Boxes.getTests();
    initTest();
  }


  Future<void> initTest() async {
    _testing = await widget._client.getCurrentTest();
    if (_testing == 0)
      startTest();
    else
      _getSamples();
  }


  List<String> genKey() {
    SecureRandom mySecureRandom = exampleSecureRandom();
    final keyGen = RSAKeyGenerator();
    final rsaParams =
        RSAKeyGeneratorParameters(BigInt.parse('65537'), 2048, 64);
    final paramsWithRnd = ParametersWithRandom(rsaParams, mySecureRandom);
    keyGen.init(paramsWithRnd);
    var keyPair = keyGen.generateKeyPair();

    final rsaPublic = keyPair.publicKey as RSAPublicKey;
    final rsaPrivate = keyPair.privateKey as RSAPrivateKey;

    publicKey = ['${rsaPublic.modulus}', '${rsaPublic.exponent}'];

    privateKey = [
      '${rsaPrivate.modulus}',
      '${rsaPrivate.exponent}', // private exponent
      '${rsaPrivate.p}', // the two prime numbers
      '${rsaPrivate.q}'
    ];

    return publicKey;
  }


  void _addInfo(String result, int testid, List<String> publickey,
      List<String> privatekey) {
    final test1 = Test()
      ..result = result
      ..start = DateFormat("dd/MM/yyyy HH:mm:ss").format(DateTime.now())
      ..end = "-"
      ..testid = testid
      ..publickey = publickey
      ..privatekey = privatekey;

    box.put(test1.testid, test1);
  }


  SecureRandom exampleSecureRandom() {
    final secureRandom = FortunaRandom();
    final seedSource = Random.secure();
    final seeds = <int>[];
    for (int i = 0; i < 32; i++) {
      seeds.add(seedSource.nextInt(255));
    }
    secureRandom.seed(KeyParameter(Uint8List.fromList(seeds)));
    return secureRandom;
  }


  void startTest() {
    List<String> pub = [];
    if (widget._value.role == "Test") pub = genKey();
    widget._client.startTest(pub).then((value) {
      if (widget._value.role == "Test")
        _addInfo("", value, publicKey, privateKey);
      setState(() {
        _testing = value;
      });
      _getSamples();
    }).onError((error, stackTrace) {
      ScaffoldMessenger.of(context).showSnackBar(SnackBar(
          content: Text(
        "Error: " + error.toString(),
        style: TextStyle(fontSize: 20),
      )));
    });
  }


  void _getSamples() {
    setState(() {
      _samples = widget._client.getSamples(_testing);
    });

    _samples.then((value) {
      setState(() {
        _samplelist = value;
        if (_counter != _samplelist.length) _sample = _samplelist[_counter];
      });
    }).onError((error, stackTrace) {
      ScaffoldMessenger.of(context).showSnackBar(SnackBar(
          content: Text(
        "Error: " + error.toString(),
        style: TextStyle(fontSize: 20),
      )));
    });
    return;
  }


  Future<void> _obtainaudio(String _sample) async {
    await Navigator.push(
        context,
        MaterialPageRoute(
          builder: (c) =>
              RecorderUI(widget._value, _sample, widget._client, _testing),
        ));

    setState(() {});
  }


  void _onPressed(BuildContext c) {
    setState(() {
      pressed = true;
    });
    if (widget._value.role == "Train")
      _onPressedTrain(c);
    else
      _onPressedTest(c);
  }



  void _onPressedTest(BuildContext c) async {
    widget._client.requestResult(_testing).then((value) {
      ScaffoldMessenger.of(context).showSnackBar(SnackBar(
          backgroundColor: Colors.green,
          content: Text(
            "Request Sent, the result will be sent after data processing",
            style: TextStyle(fontSize: 20, color: Colors.white),
          )));
      Navigator.pushReplacement(
          context,
          MaterialPageRoute(
              builder: (c) => HomeUI(widget._value, widget._client)));
    }).onError((error, stackTrace) {
      ScaffoldMessenger.of(context).showSnackBar(SnackBar(
          content: Text(
        "Error: " + error.toString(),
        style: TextStyle(fontSize: 20),
      )));
      setState(() {
        pressed = false;
      });
    });
  }



  void _onPressedTrain(BuildContext c) async {
    widget._client.sendResult(_level!, _testing).then((value) {
      ScaffoldMessenger.of(context).showSnackBar(SnackBar(
          backgroundColor: Colors.green,
          content: Text(
            "Result Sent",
            style: TextStyle(fontSize: 20, color: Colors.white),
          )));
      Navigator.pushReplacement(
          context,
          MaterialPageRoute(
              builder: (c) => HomeUI(widget._value, widget._client)));
    }).onError((error, stackTrace) {
      ScaffoldMessenger.of(context).showSnackBar(SnackBar(
          content: Text(
        "Error: " + error.toString(),
        style: TextStyle(fontSize: 20),
      )));
      setState(() {
        pressed = false;
      });
    });
  }



  Widget _buildAudioButton() {
    return Column(children: [
      SizedBox(
        height: 50,
      ),
      ElevatedButton(
          style: ButtonStyle(
              backgroundColor: MaterialStateProperty.all<Color>(Colors.blue),
              fixedSize: MaterialStateProperty.all<Size>(Size(120, 60)),
              shape: MaterialStateProperty.all<RoundedRectangleBorder>(
                  RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(18.0),
              ))),
          child: Icon(
            Icons.folder,
            size: 30,
          ),
          onPressed: () => _obtainaudio(_sample!))
    ]);
  }



  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(
          leading: Icon(Icons.record_voice_over_rounded),
          title: Text(
            'Test',
            style: TextStyle(fontSize: 25),
          ),
          centerTitle: true,
          backgroundColor: Colors.blue,
        ),
        body: Center(
            child: SingleChildScrollView(
                child: FutureBuilder(
          future: _samples,
          builder: (c, asyncsnapshot) => asyncsnapshot.connectionState ==
                  ConnectionState.waiting
              ? CircularProgressIndicator()
              : Center(
                  child: SizedBox(
                    width: 300,
                    height: 650,
                    child: SingleChildScrollView(
                      child: Column(children: [
                        Text(
                          "You must upload audio files, " +
                              "click the folder buttons and then press on Perform request",
                          style: TextStyle(fontSize: 20, wordSpacing: 5),
                          textAlign: TextAlign.center,
                        ),
                        SizedBox(height: 40),
                        _samplelist.length != _counter
                            ? _buildAudioButton()
                            : Text(
                                "No other audio upload remained, press Perform request"),
                        SizedBox(height: 80),
                        Center(
                          child: widget._value.role == "Test"
                              ? null
                              : Row(
                                  crossAxisAlignment: CrossAxisAlignment.center,
                                  mainAxisAlignment: MainAxisAlignment.center,
                                  children: [
                                      Text(
                                        "Insert the level",
                                        style: TextStyle(
                                            fontSize: 20, wordSpacing: 5),
                                        textAlign: TextAlign.center,
                                      ),
                                      SizedBox(
                                        width: 20,
                                      ),
                                      DropdownButton<String>(
                                          value: _level,
                                          items: <String>[
                                            '1',
                                            '2',
                                            '3',
                                            '4',
                                            '5'
                                          ].map((String value) {
                                            return DropdownMenuItem<String>(
                                              value: value,
                                              child: Text(value),
                                            );
                                          }).toList(),
                                          onChanged: (value) {
                                            setState(() {
                                              _level = value.toString();
                                            });
                                          }),
                                    ]),
                        ),
                        SizedBox(
                          height: 30,
                        ),
                        pressed
                            ? SizedBox(
                                height: 50,
                                width: 50,
                                child: CircularProgressIndicator(),
                              )
                            : SizedBox(
                                width: 200,
                                height: 50,
                                child: ElevatedButton(
                                  onPressed: () => (!pressed &&
                                          _counter == _samplelist.length &&
                                          (widget._value.role == "Train"
                                              ? _level != null
                                              : true))
                                      ? _onPressed(context)
                                      : null,
                                  child: Text(
                                    (_counter == _samplelist.length &&
                                            (widget._value.role == "Train"
                                                ? _level != null
                                                : true))
                                        ? "Perform request"
                                        : "You must upload all the files",
                                    style: TextStyle(fontSize: 20),
                                  ),
                                )),
                        SizedBox(
                          height: 50,
                        ),
                        SizedBox(
                            width: 150,
                            height: 30,
                            child: ElevatedButton(
                              child: Text("Return Home"),
                              onPressed: () => Navigator.pushReplacement(
                                  context,
                                  MaterialPageRoute(
                                      builder: (c) => HomeUI(
                                          widget._value, widget._client))),
                            )),
                      ]),
                    ),
                  ),
                ),
        ))));
  }
}
