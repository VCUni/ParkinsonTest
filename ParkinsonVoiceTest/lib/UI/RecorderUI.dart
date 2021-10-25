import 'dart:async';
import 'dart:io';
import 'package:flutter/foundation.dart' show kIsWeb;
import 'package:flutter/material.dart';
import 'package:flutter_sound_lite/flutter_sound.dart';
import 'package:flutter_sound_platform_interface/flutter_sound_recorder_platform_interface.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:parkinson_voice_test/service/IClient.dart';
import 'DocumentUI.dart';
import 'PermissionErrorUI.dart';
import 'package:path_provider/path_provider.dart';

typedef _Fn = void Function();

const theSource = AudioSource.microphone;

class RecorderUI extends StatefulWidget {
  final String _sample;
  final IClient _client;
  final int _testid;
  final _value;

  RecorderUI(this._value, this._sample, this._client, this._testid);

  @override
  _RecorderUIState createState() => _RecorderUIState();
}

class _RecorderUIState extends State<RecorderUI> {
  Codec _codec = Codec.pcm16WAV;
  String _mPath = 'record_file.wav';
  FlutterSoundPlayer? _mPlayer = FlutterSoundPlayer();
  FlutterSoundRecorder? _mRecorder = FlutterSoundRecorder();
  bool _mPlayerIsInited = false,
      _mRecorderIsInited = false,
      _mplaybackReady = false;
  File? audio;


  @override
  void initState() {
    _mPlayer!.openAudioSession().then((value) {
      setState(() {
        _mPlayerIsInited = true;
      });
    });

    openTheRecorder().then((value) {
      setState(() {
        _mRecorderIsInited = true;
      });
    });
    super.initState();
  }


  @override
  void dispose() {
    _mPlayer!.closeAudioSession();
    _mPlayer = null;

    _mRecorder!.closeAudioSession();
    _mRecorder = null;
    super.dispose();
  }


  Future<void> openTheRecorder() async {
    if (!kIsWeb) {
      var status = await Permission.microphone.request();
      if (status != PermissionStatus.granted) {
        Navigator.pushReplacement(
            context,
            MaterialPageRoute(
                builder: (c) => PermissionErrorUI(
                    'Microphone permission not granted, ' +
                        'please restart the app.')));
        throw RecordingPermissionException('Microphone permission not granted');
      }
    }


    await _mRecorder!.openAudioSession();
    if (!await _mRecorder!.isEncoderSupported(_codec) && kIsWeb) {
      _codec = Codec.opusWebM;
      _mPath = 'record_file.webm';
      if (!await _mRecorder!.isEncoderSupported(_codec) && kIsWeb) {
        _mRecorderIsInited = true;
        return;
      }
    }
    _mRecorderIsInited = true;
  }


  void record() async {
    if (!kIsWeb) {
      var tempDir = await getTemporaryDirectory();
      _mPath = '${tempDir.path}/record_file.wav';
    }
    _mRecorder!
        .startRecorder(
            toFile: _mPath,
            codec: _codec,
            audioSource: theSource,
            numChannels: 1,
            sampleRate: 16000)
        .then((value) {
      setState(() {});
    });
  }


  void stopRecorder() async {
    await _mRecorder!.stopRecorder().then((value) {
      setState(() {
        _mplaybackReady = true;
        //audio = File(value!);
        audio = File(_mPath);
        print(audio!.path);
      });
    });
  }


  void play() async {
    assert(_mPlayerIsInited &&
        _mplaybackReady &&
        _mRecorder!.isStopped &&
        _mPlayer!.isStopped);
    await _mPlayer!
        .startPlayer(
            fromURI: _mPath,
            codec: kIsWeb ? Codec.opusWebM : Codec.pcm16WAV,
            numChannels: 1,
            sampleRate: 16000,
            whenFinished: () {
              setState(() {});
            })
        .then((value) {
      setState(() {});
    });
  }


  void stopPlayer() {
    _mPlayer!.stopPlayer().then((value) {
      setState(() {});
    });
  }


  Future<void> _uploadaudio(File? file) async {
    var temp = await widget._client.upload(file!, widget._testid);
    if (temp) {
      ScaffoldMessenger.of(context).showSnackBar(SnackBar(
          backgroundColor: Colors.green,
          content: Text(
            "audio successfully uploaded",
            style: TextStyle(fontSize: 20, color: Colors.white),
          )));
      await file.delete();
      Navigator.pushReplacement(
          context,
          MaterialPageRoute(
              builder: (c) => DocumentUI(widget._value, widget._client)));
    }
  }



  _Fn? getRecorderFn() {
    if (!_mRecorderIsInited || !_mPlayer!.isStopped) {
      return null;
    }
    return _mRecorder!.isStopped ? record : stopRecorder;
  }



  _Fn? getPlaybackFn() {
    if (!_mPlayerIsInited || !_mplaybackReady || !_mRecorder!.isStopped) {
      return null;
    }
    return _mPlayer!.isStopped ? play : stopPlayer;
  }



  @override
  Widget build(BuildContext context) {
    Widget makeBody() {
      return SingleChildScrollView(
          child: Column(
        children: [
          Text(
            "Click Record button and read aloud the text displayed \"" +
                widget._sample +
                "\"",
            style: TextStyle(fontSize: 20, wordSpacing: 5),
            textAlign: TextAlign.center,
          ),
          SizedBox(
            height: 40,
          ),
          Container(
            margin: const EdgeInsets.all(3),
            padding: const EdgeInsets.all(3),
            height: 80,
            width: double.infinity,
            decoration: BoxDecoration(
                color: Colors.white,
                border: Border.all(
                  color: Colors.indigo,
                  width: 3,
                )),
            child: Row(
                crossAxisAlignment: CrossAxisAlignment.center,
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  ElevatedButton(
                    onPressed: getRecorderFn(),
                    child: Text(_mRecorder!.isRecording ? 'Stop' : 'Record'),
                  ),
                  SizedBox(
                    width: 20,
                  ),
                  Text(_mRecorder!.isRecording
                      ? 'Recording in progress'
                      : 'Recorder is stopped'),
                ]),
          ),
          Container(
            margin: const EdgeInsets.all(3),
            padding: const EdgeInsets.all(3),
            height: 80,
            width: double.infinity,
            alignment: Alignment.center,
            decoration: BoxDecoration(
              color: Colors.white,
              border: Border.all(
                color: Colors.indigo,
                width: 3,
              ),
            ),
            child: Row(
                crossAxisAlignment: CrossAxisAlignment.center,
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  ElevatedButton(
                    onPressed: getPlaybackFn(),
                    child: Text(_mPlayer!.isPlaying ? 'Stop' : 'Play'),
                  ),
                  SizedBox(
                    width: 20,
                  ),
                  Text(_mPlayer!.isPlaying
                      ? 'Playback in progress'
                      : 'Player is stopped'),
                ]),
          ),
          SizedBox(
            height: 60,
          ),
          ElevatedButton(
            onPressed: audio != null
                ? () {
                    _uploadaudio(audio);
                  }
                : () => (null),
            child: Text(
              audio != null ? "Send Audio" : "You must register the Audio",
              style: TextStyle(fontSize: 20),
            ),
          )
        ],
      ));
    }


    return Scaffold(
      backgroundColor: Colors.blue[50],
      appBar: AppBar(
        title: const Text('Simple Recorder'),
      ),
      body: makeBody(),
    );
  }
}
