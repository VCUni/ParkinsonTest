import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:parkinson_voice_test/entity/User.dart';
import 'package:parkinson_voice_test/service/Client.dart';
import 'package:parkinson_voice_test/service/IClient.dart';
import 'package:parkinson_voice_test/service/MqttClientFactoryServer.dart';
import 'HomeUI.dart';
import 'RegisterUI.dart';

class LoginUI extends StatefulWidget {
  final IClient _client = new Client();

  @override
  _LoginUIState createState() => _LoginUIState();
}

class _LoginUIState extends State<LoginUI> {
  final _form = GlobalKey<FormState>();
  TextEditingController _cf = TextEditingController();
  TextEditingController _password = TextEditingController();
  Future<User?> _userlogin = Future.value(null);
  bool _obscureText = true;
  late bool _state;


  @override
  void initState() {
    super.initState();
    _isAccepted();
    _login(widget._client.autoLogin());
  }


  void _login(Future<User?> user) {
    setState(() {
      _userlogin = user;
    });

    _userlogin.then((value) {
      if (value != null)
        Navigator.pushReplacement(context,
            MaterialPageRoute(builder: (c) => HomeUI(value, widget._client)));
    }).onError((error, stackTrace) {
      ScaffoldMessenger.of(context).showSnackBar(SnackBar(
          content: Text(
        "Error: " + error.toString(),
        style: TextStyle(fontSize: 20),
      )));
    });
  }


  void _loginClick() {
    if (_form.currentState!.validate()) {
      _login(widget._client.login(_cf.text, _password.text));
    }
  }


  Future<bool> acceptPolicy() async {
    SharedPreferences pref = await SharedPreferences.getInstance();
    pref.setBool('state', true);
    return true;
  }


  void _isAccepted() async {
    final prefs = await SharedPreferences.getInstance();
    setState(() {
      _state = prefs.getBool('state') ?? false;
    });
    if (!_state) showAlertDialog(context);
    return;
  }



  showAlertDialog(BuildContext context) {
    Widget okButton = TextButton(
        child: Text("I accept the privacy policy"),
        onPressed: () async {
          await acceptPolicy();
          Navigator.pop(context);
        });

    AlertDialog alert = AlertDialog(
      title: Text(
        "Privacy Policy",
        textAlign: TextAlign.center,
      ),
      content: Column(
        children: [
          Icon(
            Icons.notification_important_rounded,
            size: 30,
            color: Colors.red,
          ),
          SizedBox(
            height: 20,
          ),
          Text('''This app collect personal data from its user:
  View user information: 
   - name
   - surname
   - fiscal code
   - date of birth.
   - Store and Analize the sent audio

The result is not visible to other users.
This app require internet and microphone permission.'''),
        ],
      ),
      actions: [
        okButton,
      ],
    );

    showDialog(
      barrierDismissible: false,
      context: context,
      builder: (BuildContext context) {
        return alert;
      },
    );
  }



  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        leading: Icon(
          Icons.account_circle_rounded,
          size: 40,
        ),
        title: Text(
          'Sign in',
          style: TextStyle(fontSize: 35),
        ),
        centerTitle: true,
        backgroundColor: Colors.blue,
      ),
      body: Center(
        child: SingleChildScrollView(
          child: Column(children: [
            FutureBuilder(
              future: _userlogin,
              builder: (c, asyncsnapshot) => asyncsnapshot.connectionState ==
                      ConnectionState.waiting
                  ? CircularProgressIndicator()
                  : SizedBox(
                      width: 300,
                      child: Form(
                        key: _form,
                        autovalidateMode: AutovalidateMode.onUserInteraction,
                        child: Column(
                          children: [
                            TextFormField(
                              decoration: InputDecoration(
                                icon: Icon(
                                  Icons.account_box_rounded,
                                  size: 30,
                                  color: Colors.blue,
                                ),
                                labelText: 'Fiscal Code',
                              ),
                              validator: (String? value) {
                                if (value == null || value.trim().isEmpty)
                                  return 'Fiscal Code is required';
                                else
                                  return null;
                              },
                              controller: _cf,
                              toolbarOptions: ToolbarOptions(
                                  copy: true,
                                  cut: true,
                                  paste: true,
                                  selectAll: true),
                              textInputAction: TextInputAction.next,
                            ),
                            SizedBox(height: 30),
                            TextFormField(
                              obscureText: _obscureText,
                              decoration: InputDecoration(
                                icon: Icon(Icons.lock,
                                    size: 30, color: Colors.blue),
                                labelText: 'Password',
                                suffixIcon: IconButton(
                                    onPressed: () {
                                      setState(() {
                                        _obscureText = !_obscureText;
                                      });
                                    },
                                    icon: Icon(
                                      _obscureText
                                          ? Icons.visibility
                                          : Icons.visibility_off,
                                    )),
                              ),
                              controller: _password,
                              validator: (String? value) {
                                if (value == null || value.trim().isEmpty)
                                  return 'Password is required';
                                else
                                  return null;
                              },
                              toolbarOptions: ToolbarOptions(
                                  copy: false,
                                  cut: false,
                                  paste: true,
                                  selectAll: true),
                              textInputAction: TextInputAction.done,
                            ),
                            SizedBox(height: 40),
                            SizedBox(
                                width: 200,
                                height: 35,
                                child: ElevatedButton(
                                  onPressed: _loginClick,
                                  child: Text(
                                    "Login",
                                    style: TextStyle(fontSize: 20),
                                  ),
                                )),
                            SizedBox(height: 10),
                            TextButton(
                              child: Text(
                                "Register now",
                                style: TextStyle(fontSize: 15),
                              ),
                              onPressed: () => Navigator.push(
                                  context,
                                  MaterialPageRoute(
                                    builder: (c) => RegisterUI(widget._client),
                                  )),
                            ),
                          ],
                        ),
                      ),
                    ),
            ),
          ]),
        ),
      ),
    );
  }
}
