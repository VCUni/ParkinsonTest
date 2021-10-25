import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:parkinson_voice_test/service/IClient.dart';
import 'package:parkinson_voice_test/entity/User.dart';

class RegisterUI extends StatefulWidget {
  final IClient _client;
  RegisterUI(this._client);

  @override
  _RegisterUIState createState() => _RegisterUIState();
}

class _RegisterUIState extends State<RegisterUI> {
  final _form = GlobalKey<FormState>();
  String _dateBirth = "";
  String _role = "Test";
  TextEditingController _name = TextEditingController();
  TextEditingController _surname = TextEditingController();
  TextEditingController _cf = TextEditingController();
  TextEditingController _password = TextEditingController();
  Future _userregister = Future.value();

  AutovalidateMode _validatemode = AutovalidateMode.disabled;
  bool _obscureText1 = true;
  bool _obscureText2 = true;


  @override
  void initState() {
    super.initState();
  }


  void _register() {
    if (_form.currentState!.validate() && _dateBirth != "") {
      setState(() {
        _userregister = widget._client.register(
            new User(_name.text, _surname.text, _cf.text, _dateBirth, _role),
            _password.text);
      });

      _userregister.then((value) {
        Navigator.pop(context);
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(
            backgroundColor: Colors.blue,
            content: Text(
              "Account registration successful",
              style: TextStyle(fontSize: 20, color: Colors.white),
            )));
      }).onError((error, stackTrace) {
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(
            content: Text(
          "Error, registration failed " + error.toString(),
          style: TextStyle(fontSize: 20, color: Colors.red),
        )));
      });
    } else
      setState(() {
        _validatemode = AutovalidateMode.onUserInteraction;
      });
  }



  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(
          'Sign up',
          style: TextStyle(fontSize: 35),
        ),
        centerTitle: true,
        backgroundColor: Colors.blue,
      ),
      body: Center(
        child: SizedBox(
          width: 300,
          child: SingleChildScrollView(
            child: Column(children: [
              FutureBuilder(
                future: _userregister,
                builder: (c, asyncsnapshot) =>
                    asyncsnapshot.connectionState == ConnectionState.waiting
                        ? CircularProgressIndicator()
                        : Form(
                            autovalidateMode: _validatemode,
                            key: _form,
                            child: Column(
                              children: [
                                TextFormField(
                                  decoration: const InputDecoration(
                                    labelText: 'Name',
                                  ),
                                  validator: (String? value) {
                                    if (value == null || value.trim().isEmpty)
                                      return 'Name is required';
                                    else
                                      return null;
                                  },
                                  controller: _name,
                                  textInputAction: TextInputAction.next,
                                ),
                                SizedBox(height: 12.0),
                                TextFormField(
                                  decoration: InputDecoration(
                                    labelText: 'Surname',
                                  ),
                                  validator: (String? value) {
                                    if (value == null || value.trim().isEmpty)
                                      return 'Surname is required';
                                    else
                                      return null;
                                  },
                                  controller: _surname,
                                  textInputAction: TextInputAction.next,
                                ),
                                SizedBox(height: 12.0),
                                TextFormField(
                                  decoration: InputDecoration(
                                    labelText: 'Fiscal Code',
                                  ),
                                  validator: (String? value) {
                                    if (value == null || value.trim().isEmpty)
                                      return 'Fiscal Code is required';
                                    else
                                      return null;
                                  },
                                  controller: _cf,
                                  textInputAction: TextInputAction.next,
                                ),
                                SizedBox(height: 12.0),
                                GestureDetector(
                                  onTap: () {
                                    showDatePicker(
                                            context: context,
                                            initialDate: DateTime.now(),
                                            firstDate: DateTime(1910),
                                            lastDate: DateTime.now())
                                        .then((value) {
                                      if (value != null)
                                        setState(() {
                                          _dateBirth = DateFormat("dd/MM/yyyy")
                                              .format(value);
                                        });
                                    });
                                  },
                                  child: TextFormField(
                                    enabled: false,
                                    decoration: InputDecoration(
                                      labelText: _dateBirth == ""
                                          ? 'Date of Birth'
                                          : _dateBirth,
                                    ),
                                  ),
                                ),
                                SizedBox(height: 12.0),
                                DropdownButton<String>(
                                    isExpanded: true,
                                    value: _role,
                                    items: <String>["Test", 'Train']
                                        .map((String value) {
                                      return DropdownMenuItem<String>(
                                        value: value,
                                        child: Text(value),
                                      );
                                    }).toList(),
                                    onChanged: (value) {
                                      setState(() {
                                        _role = value.toString();
                                      });
                                    }),
                                SizedBox(height: 12.0),
                                TextFormField(
                                  obscureText: _obscureText1,
                                  decoration: InputDecoration(
                                    labelText: 'Password',
                                    suffixIcon: IconButton(
                                        onPressed: () {
                                          setState(() {
                                            _obscureText1 = !_obscureText1;
                                          });
                                        },
                                        icon: Icon(_obscureText1
                                            ? Icons.visibility
                                            : Icons.visibility_off)),
                                  ),
                                  controller: _password,
                                  textInputAction: TextInputAction.next,
                                  validator: (String? value) {
                                    if (value == null || value.trim().isEmpty)
                                      return 'Password is required';
                                    else
                                      return null;
                                  },
                                ),
                                SizedBox(height: 12.0),
                                TextFormField(
                                  obscureText: _obscureText2,
                                  decoration: InputDecoration(
                                      labelText: 'Confirm Password',
                                      suffixIcon: IconButton(
                                          onPressed: () {
                                            setState(() {
                                              _obscureText2 = !_obscureText2;
                                            });
                                          },
                                          icon: Icon(_obscureText2
                                              ? Icons.visibility
                                              : Icons.visibility_off))),
                                  validator: (String? value) {
                                    if (value == null || value.trim().isEmpty)
                                      return 'Confirm Password is required';
                                    if (_password.text != value)
                                      return 'The Confirm Password is different from Password';
                                    else
                                      return null;
                                  },
                                  textInputAction: TextInputAction.done,
                                ),
                                SizedBox(
                                  height: 40,
                                ),
                                SizedBox(
                                    width: 200,
                                    height: 35,
                                    child: ElevatedButton(
                                      onPressed: _register,
                                      child: Text(
                                        "Register now",
                                        style: TextStyle(fontSize: 20),
                                      ),
                                    )),
                              ],
                            ),
                          ),
              ),
            ]),
          ),
        ),
      ),
    );
  }
}
