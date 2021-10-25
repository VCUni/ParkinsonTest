import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:parkinson_voice_test/entity/Test.dart';

import 'UI/LoginUI.dart';

import 'package:hive/hive.dart';
import 'package:hive_flutter/hive_flutter.dart';

void main() async {
  await Hive.initFlutter();
  Hive.registerAdapter(TestAdapter());
  await Hive.openBox<Test>('testBox');
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Parkinson Response',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: LoginUI(),
    );
  }
}




