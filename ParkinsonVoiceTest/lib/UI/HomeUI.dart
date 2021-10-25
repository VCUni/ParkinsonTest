import 'package:flutter/material.dart';
import 'package:parkinson_voice_test/service/IClient.dart';
import 'package:parkinson_voice_test/service/MqttClientFactoryServer.dart';
import 'DocumentUI.dart';
import 'LoginUI.dart';
import 'UserResultsUI.dart';

class HomeUI extends StatefulWidget {
  final IClient _client;
  final _value;
  HomeUI(this._value, this._client);

  @override
  _HomeUIState createState() => _HomeUIState();
}

class _HomeUIState extends State<HomeUI> {
  Future<int> _currentTest = Future.value(0);
  int _testing = 0;


  @override
  void initState() {
    super.initState();
    
    makeClient('192.168.1.81', 'subscriber'+widget._value.cf, widget._client);
    
    _initTestHistory();
  }


  Future<void> _initTestHistory() async {
    setState(() {
      _currentTest = widget._client.getCurrentTest();
    });

    _currentTest
        .then((value) => setState(() {
              _testing = value;
            }))
        .onError((error, stackTrace) {
      ScaffoldMessenger.of(context)
          .showSnackBar(SnackBar(content: Text("Error " + error.toString())));
      return;
    });
    return;
  }


  Future<void> _logoutClick() async {
    await widget._client.logout();
    Navigator.of(context).pushAndRemoveUntil(
        MaterialPageRoute(builder: (c) => LoginUI()), (route) => false);
  }



  Widget _buildDrawer() => Drawer(
        child: ListView(
          children: [
            ListTile(
              leading: widget._value.role == "Train"
                  ? null
                  : Icon(Icons.recent_actors),
              title: Text(
                widget._value.role == "Train" ? "" : "MyResults",
                style: TextStyle(fontSize: 10, color: Colors.black),
              ),
              onTap: widget._value.role == "Train"
                  ? () => (null)
                  : () => Navigator.push(
                      context,
                      MaterialPageRoute(
                          builder: (ctx) => UserResultsUI(widget._client))),
            ),
            ListTile(
              leading: Icon(Icons.logout_rounded),
              title: Text(
                "Sign out",
                style: TextStyle(fontSize: 10, color: Colors.black),
              ),
              onTap: _logoutClick,
            ),
          ],
        ),
      );



  Widget _buildInfo(String info, String value) {
    return Column(children: [
      Text(
        info + ": " + value,
        style: TextStyle(
            fontSize: 20.0,
            color: Colors.black54,
            letterSpacing: 2.0,
            fontWeight: FontWeight.w400),
      ),
      SizedBox(
        height: 20,
      ),
    ]);
  }



  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(
          title: Text(
            'Home',
            style: TextStyle(fontSize: 35),
          ),
          centerTitle: true,
          backgroundColor: Colors.blue,
        ),
        drawer: _buildDrawer(),
        body: Center(
            child: SingleChildScrollView(
                child: FutureBuilder(
          future: _currentTest,
          builder: (c, asyncsnapshot) => asyncsnapshot.connectionState ==
                  ConnectionState.waiting
              ? CircularProgressIndicator()
              : SingleChildScrollView(
                  child: Center(
                    child: Column(
                      children: <Widget>[
                        SizedBox(
                          height: 40,
                        ),
                        _buildInfo("Name", widget._value.name),
                        _buildInfo("Surname", widget._value.surname),
                        _buildInfo("Date of Birth", widget._value.dateBirth),
                        _buildInfo("CF", widget._value.cf),
                        _buildInfo("Role", widget._value.role),
                        SizedBox(
                          height: 20,
                        ),
                        SizedBox(
                          height: 40,
                        ),
                        ElevatedButton(
                          onPressed: () {
                            Navigator.pushReplacement(
                                context,
                                MaterialPageRoute(
                                    builder: (c) => DocumentUI(
                                        widget._value, widget._client)));
                          },
                          style: ButtonStyle(
                              backgroundColor:
                                  MaterialStateProperty.all<Color>(Colors.blue),
                              fixedSize: MaterialStateProperty.all<Size>(
                                  Size(160, 50)),
                              shape: MaterialStateProperty.all<
                                      RoundedRectangleBorder>(
                                  RoundedRectangleBorder(
                                borderRadius: BorderRadius.circular(18.0),
                              ))),
                          child: Column(
                            children: [
                              Icon(
                                _testing != 0
                                    ? Icons.play_circle_filled_rounded
                                    : Icons.play_for_work_rounded,
                                size: 30,
                              ),
                              Text(_testing != 0
                                  ? "Continue test"
                                  : "Start test")
                            ],
                          ),
                        ),
                        SizedBox(
                          height: 40,
                        ),
                        Changepass(widget._client),
                        SizedBox(
                          height: 50,
                        ),
                      ],
                    ),
                  ),
                ),
        ))));
  }
}



class Changepass extends StatefulWidget {
  final IClient _client;
  Changepass(this._client);

  @override
  _ChangepassState createState() => _ChangepassState();
}

class _ChangepassState extends State<Changepass> {
  TextEditingController _oldpassword = TextEditingController();
  TextEditingController _newpassword = TextEditingController();
  final _form = GlobalKey<FormState>();


  @override
  void initState() {
    super.initState();
  }


  void _change() async {
    if (_form.currentState!.validate()) {
      bool temp = await widget._client
          .changePassword(_oldpassword.text, _newpassword.text);

      if (temp) {
        Navigator.pushReplacement(
            context, MaterialPageRoute(builder: (c) => LoginUI()));
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(
            backgroundColor: Colors.blue,
            content: Text(
              "Password changed",
              style: TextStyle(fontSize: 20, color: Colors.white),
            )));
      } else {
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(
            content: Text(
          "Change Password failed",
          style: TextStyle(fontSize: 20, color: Colors.red),
        )));
      }
    }
  }



  @override
  Widget build(BuildContext context) {
    return Center(
      child: ElevatedButton(
        child: const Text('Change Password'),
        onPressed: () {
          showModalBottomSheet<void>(
            context: context,
            builder: (BuildContext context) {
              return SingleChildScrollView(
                  child: Container(
                      height: 350,
                      color: Colors.white,
                      child: Center(
                        child: Form(
                          key: _form,
                          child: Column(
                            mainAxisAlignment: MainAxisAlignment.center,
                            mainAxisSize: MainAxisSize.min,
                            children: <Widget>[
                              SizedBox(height: 20),
                              SizedBox(
                                width: 300,
                                child: TextFormField(
                                  decoration:
                                      InputDecoration(labelText: 'Password'),
                                  controller: _oldpassword,
                                  textInputAction: TextInputAction.next,
                                  validator: (String? value) {
                                    if (value == null || value.trim().isEmpty)
                                      return 'Password is required';
                                    else
                                      return null;
                                  },
                                ),
                              ),
                              SizedBox(height: 15.0),
                              SizedBox(
                                width: 300,
                                child: TextFormField(
                                  decoration: InputDecoration(
                                      labelText: 'New Password'),
                                  validator: (String? value) {
                                    if (value == null || value.trim().isEmpty)
                                      return 'New Password is required';
                                  },
                                  controller: _newpassword,
                                  textInputAction: TextInputAction.done,
                                ),
                              ),
                              SizedBox(height: 20),
                              SizedBox(
                                  width: 150,
                                  height: 40,
                                  child: ElevatedButton(
                                    onPressed: _change,
                                    child: Text(
                                      "Submit",
                                      style: TextStyle(fontSize: 25),
                                    ),
                                  )),
                              SizedBox(height: 20),
                              ElevatedButton(
                                child: const Text('Close'),
                                onPressed: () => Navigator.pop(context),
                              )
                            ],
                          ),
                        ),
                      )));
            },
          );
        },
      ),
    );
  }
}
