import 'package:hive/hive.dart';
import 'Test.dart';

class Boxes{

  static Box<Test>? _box;

  static Box<Test> getTests() {
    if(_box==null) _box=Hive.box('testBox');
    return _box!;
  }
}