import 'package:flutter/material.dart';

class PermissionErrorUI extends StatefulWidget {
  final String _error;
  PermissionErrorUI(this._error);

  @override
  _PermissionErrorUIState createState() => _PermissionErrorUIState();
}

class _PermissionErrorUIState extends State<PermissionErrorUI> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(
          title: Text(
            'Permission Denied',
            style: TextStyle(fontSize: 30),
          ),
          centerTitle: true,
          backgroundColor: Colors.red,
        ),
        body: Center(
            child: SizedBox(
          width: 300,
          child: SingleChildScrollView(
              child: Column(
            children: [
              SizedBox(
                height: 100,
              ),
              Icon(
                Icons.error,
                color: Colors.red,
                size: 200,
              ),
              SizedBox(
                height: 100,
              ),
              Text(
                widget._error,
                textAlign: TextAlign.center,
                style: TextStyle(fontSize: 30),
              )
            ],
          )),
        )));
  }
}
