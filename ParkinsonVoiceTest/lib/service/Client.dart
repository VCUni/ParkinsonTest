import 'dart:io';
import 'package:parkinson_voice_test/entity/User.dart';
import 'package:parkinson_voice_test/service/IClient.dart';
import 'package:http/http.dart' as http;
import 'package:shared_preferences/shared_preferences.dart';
import 'dart:convert';
import 'package:parkinson_voice_test/service/ServiceException.dart';


class Client implements IClient {
  
  final http.Client _client = http.Client();
  //final String _url = "http://192.168.1.81:8080";
  final String _servicesUrl = "http://192.168.1.81:8080/services";
  final String _header = "auth-tok";
  String _token = "";
  
  /*testing
  List<String> words = [];
  int current = 0;
  String? publickey;*/


  @override
  Future register(User forminfo, String password) async {
    var r = await _client
        .post(Uri.parse(_servicesUrl + "/userauth/register"), body: {
      "name": forminfo.name,
      "surname": forminfo.surname,
      "userlogin": forminfo.cf,
      "dateBirth": forminfo.dateBirth,
      "role" : forminfo.role,
      "password": password
    });

    if (r.statusCode == HttpStatus.conflict)
      throw ServiceException(null, "Already exist an account with this Fiscal Code");
    if (r.statusCode != HttpStatus.ok)
      throw ServiceException(r.statusCode, "Registration failed");
  }


  Future<User> _doLogin(String userlogin, String password) async {
    var r = await _client.post(Uri.parse(_servicesUrl + "/userauth/login"),
        body: {"user": userlogin, "password": password});

    if (r.statusCode != HttpStatus.ok)
      throw ServiceException(r.statusCode, "Login failed");

    _token = r.body;

    return getCurrentUser();
  }


  @override
  Future<User> login(String userlogin, String password) async {
    var user = await _doLogin(userlogin, password);

    final prefs = await SharedPreferences.getInstance();
    prefs.setString(_header, _token);

    return user;
    //return User("name", "surname", "cf", "dateBirth", "Test");
  }


  @override
  Future logout() async {
    _token = "";

    final prefs = await SharedPreferences.getInstance();
    prefs.remove(_header);
  }


  @override
  Future<User?> autoLogin() async {
    final prefs = await SharedPreferences.getInstance();
    var token = prefs.getString(_header) ?? "";

    if (token != "") {
      _token = token;
      try {
        var res = await _client.post(Uri.parse(_servicesUrl + "/user/renew"),
            headers: {_header: _token});

        if (res.statusCode != HttpStatus.ok)
          throw ServiceException(
              res.statusCode, "Login expired. Login again.");

        _token = res.body;
        prefs.setString(_header, res.body);

        return await getCurrentUser();
      } catch (e) {
        logout();
        rethrow;
      }
    }
    return Future.value(null);
  }

  Future<User> getCurrentUser() async {
    var res = await _client.get(Uri.parse(_servicesUrl + "/user/account"),
        headers: {_header: _token});

    if (res.statusCode != HttpStatus.ok)
      throw ServiceException(res.statusCode, "Impossible obtain info of the current user");

    return User.fromJson(jsonDecode(res.body));
  }

  @override
  Future<bool> upload(File file, int testid) async {
    var request = http.MultipartRequest(
        'POST', Uri.parse(_servicesUrl + "/test/upload/"+testid.toString()));
    request.headers[_header] = _token;
    request.files.add(
        new http.MultipartFile.fromBytes('file', await file.readAsBytes()));
    var res = await request.send();
    if (res.statusCode != HttpStatus.created)
      throw ServiceException(
          res.statusCode, "Audio upload failed");
    return true;
  }

  @override
  Future<bool> requestResult(int testid) async {
    var res = await _client.get(
        Uri.parse(_servicesUrl + "/test/"+testid.toString()+"/processing"),
        headers: {_header: _token});
    if (res.statusCode != HttpStatus.ok)
      throw ServiceException(
          res.statusCode, "Attempt of start processing result of the test failed");
    return true;
    //await Future.delayed(Duration(seconds: 2), () {});
    //words = ["Completato"];
    //return "0";
  }

  @override
  Future sendResult(String result, int testid) async {
    var res = await _client.post(
        Uri.parse(_servicesUrl + "/test/"+testid.toString()+"/send/"+result),
        headers: {_header: _token});
    if (res.statusCode != HttpStatus.ok)
      throw ServiceException(
          res.statusCode, "Attempt of forwarding result failed");
    return;
    //await Future.delayed(Duration(seconds: 2), () {});
    //return result;
  }

  @override
  Future<bool> changePassword(String oldpass, String newpass) async {
    var res = await _client.post(
        Uri.parse(_servicesUrl + "/user/changepassword"),
        headers: {_header: _token},
        body: {"oldpass" : oldpass, "newpass" : newpass});

    if (res.statusCode != HttpStatus.ok)
      throw ServiceException(
          res.statusCode, "Attempt of changing password failed");

    return true;
  }


  @override
  Future<int> getCurrentTest() async{
    var res = await _client.get(Uri.parse(_servicesUrl + "/user/current"),
        headers: {_header: _token});

    if (res.statusCode != HttpStatus.ok)
      throw ServiceException(
          res.statusCode, "Impossible obtain user's current test");

    return int.parse(res.body);
    //return current;
  }



  @override
  Future<List<String>> getSamples(int testid) async{
    var res = await _client.get(Uri.parse(_servicesUrl + "/test/"+testid.toString()+"/samples"),
        headers: {_header: _token});

    if (res.statusCode != HttpStatus.ok)
      throw ServiceException(
          res.statusCode, "Impossible obtain current test's sample");

    return jsonDecode(res.body).cast<String>();
    //return words;
  }


  @override
  Future<String> getResult(int testid) async {
    var res = await _client.get(Uri.parse(_servicesUrl + "/test/"+testid.toString()+"/result"),
        headers: {_header: _token});

    if (res.statusCode != HttpStatus.ok)
      throw ServiceException(
          res.statusCode, "Impossible obtain the test's result");

    return res.body;
  }


  @override
  Future<int> startTest(List<String> pub) async {
    var res = await _client.post(
        Uri.parse(_servicesUrl + "/test/start"),
        headers: {_header: _token},
        body: pub.length!=2 ?  {"keyMod" : "", "keyExp" : "" } : {"keyMod" : pub[0], "keyExp" : pub[1] });

    if (res.statusCode != HttpStatus.ok)
      throw ServiceException(res.statusCode, "Attempt of start a new test failed");

    return int.parse(res.body);
    //words = ["Parola","Word"];
    //current = 123;
    //return 1;
  }

}
