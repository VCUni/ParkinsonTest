import 'dart:io';

import 'package:parkinson_voice_test/entity/User.dart';

abstract class IClient {
  Future<User?> autoLogin();
  Future<User> login(String userlogin, String password);

  Future logout();
  Future register(User info, String password);

  Future<int> startTest(List<String> pub);

  Future<bool> upload(File file, int testid);
  Future requestResult(int testid);
  Future sendResult(String result, int testid);
  Future<String> getResult(String modulus, String exponent);

  Future<List<String>> getSamples(int testid);
  Future<int> getCurrentTest();
 
  Future<bool> changePassword(String oldpass, String newpass);
}
