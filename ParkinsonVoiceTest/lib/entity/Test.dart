import 'package:hive/hive.dart';

part 'Test.g.dart';

@HiveType(typeId: 0)
class Test {
  @HiveField(0)
  late List<String> publickey;
  @HiveField(1)
  late List<String> privatekey;
  @HiveField(2)
  late String result;
  @HiveField(3)
  late int testid;
  @HiveField(4)
  late String start;
  @HiveField(5)
  late String end;
}