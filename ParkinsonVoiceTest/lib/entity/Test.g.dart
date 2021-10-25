// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'Test.dart';

// **************************************************************************
// TypeAdapterGenerator
// **************************************************************************

class TestAdapter extends TypeAdapter<Test> {
  @override
  final int typeId = 0;

  @override
  Test read(BinaryReader reader) {
    final numOfFields = reader.readByte();
    final fields = <int, dynamic>{
      for (int i = 0; i < numOfFields; i++) reader.readByte(): reader.read(),
    };
    return Test()
      ..publickey = (fields[0] as List).cast<String>()
      ..privatekey = (fields[1] as List).cast<String>()
      ..result = fields[2] as String
      ..testid = fields[3] as int
      ..start = fields[4] as String
      ..end = fields[5] as String;
  }

  @override
  void write(BinaryWriter writer, Test obj) {
    writer
      ..writeByte(6)
      ..writeByte(0)
      ..write(obj.publickey)
      ..writeByte(1)
      ..write(obj.privatekey)
      ..writeByte(2)
      ..write(obj.result)
      ..writeByte(3)
      ..write(obj.testid)
      ..writeByte(4)
      ..write(obj.start)
      ..writeByte(5)
      ..write(obj.end);
  }

  @override
  int get hashCode => typeId.hashCode;

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is TestAdapter &&
          runtimeType == other.runtimeType &&
          typeId == other.typeId;
}
