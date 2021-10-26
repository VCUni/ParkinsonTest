import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:parkinson_voice_test/entity/Boxes.dart';
import 'package:parkinson_voice_test/entity/Test.dart';
import 'package:parkinson_voice_test/service/IClient.dart';
import 'package:hive/hive.dart';
import 'package:hive_flutter/hive_flutter.dart';
import 'package:encrypt/encrypt.dart' hide SecureRandom, Key;
import 'package:pointycastle/asymmetric/api.dart';
import "package:pointycastle/export.dart" hide State;

class UserResultsUI extends StatefulWidget {
  final IClient _client;

  UserResultsUI(this._client);

  @override
  _UserResultsUIState createState() => _UserResultsUIState();
}

class _UserResultsUIState extends State<UserResultsUI> {
  List<Test> _tests = [];
  late final Box _box;
  Future<bool> ready = Future.value(false);


  @override
  void initState() {
    super.initState();
    _box = Boxes.getTests();
    _tests = _box.values.toList().cast<Test>();
    if (_tests.length != 0) _requestPending();  //For iOS
  }



  Future<bool> _requestPending() async {
    Iterable<Test> pending = _tests.where((element) => element.result == "");
    if (pending.length != 0) {
      for (Test t in pending) {
        widget._client.getResult(t.testid).then((value) {
          t.end = DateFormat("dd/MM/yyyy HH:mm:ss").format(DateTime.now());

          RSAPrivateKey priv = new RSAPrivateKey(
              BigInt.parse(t.privatekey[0]),
              BigInt.parse(t.privatekey[1]),
              BigInt.parse(t.privatekey[2]),
              BigInt.parse(t.privatekey[3]));

          final encrypter = Encrypter(RSA(publicKey: null, privateKey: priv));
          final decrypted = encrypter.decrypt(Encrypted.fromBase64(value));
          t.result = decrypted;
          _box.put(t.testid, t);
        }).onError((error, stackTrace) {
          print("Error: " + error.toString());
          return;
        });
      }
      return true;
    }
    return true;
  }

  

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(
          title: Text(
            'MyResults',
            style: TextStyle(fontSize: 35),
          ),
          centerTitle: true,
          backgroundColor: Colors.blue,
        ),
        body: 
          FutureBuilder(
              future: _requestPending(),
              builder: (c, asyncsnapshot) => asyncsnapshot.connectionState ==
                      ConnectionState.waiting
                  ? CircularProgressIndicator()
                  : _tests.length == 0
                      ? Center(
                          child: Text(
                            "No result found",
                            style: Theme.of(context).textTheme.headline5,
                          ),
                        )
                      : ListView.builder(
                          itemCount: _tests.length,
                          itemBuilder: (context, index) =>
                              TestWidget(_tests[_tests.length - index - 1])))
        );
  }
}



class TestWidget extends StatelessWidget {
  final Test t;

  TestWidget(this.t);

  @override
  Widget build(BuildContext context) {
    return ListTile(
      leading: Icon(Icons.perm_device_information_rounded),
      title: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
        Text("Result: " + t.result),
      ]),
      subtitle: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
        Text("TestID: " + t.testid.toString()),
        Text("Start Date " + t.start),
        Text("End Date " + t.end)
      ]),
    );
  }
}
