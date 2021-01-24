import 'dart:async';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:jitsi_meet/jitsi_meet.dart';

typedef void JitsiViewCreatedCallback(JitsiViewController controller);

class JitsiView extends StatefulWidget {
  const JitsiView({
    Key key,
    this.onJitsiViewCreated,
  }) : super(key: key);

  final JitsiViewCreatedCallback onJitsiViewCreated;

  @override
  State<StatefulWidget> createState() => _TextViewState();
}

class _TextViewState extends State<JitsiView> {
  @override
  Widget build(BuildContext context) {
    if (defaultTargetPlatform == TargetPlatform.android) {
      return AndroidView(
        viewType: 'ponnamkarthik/flutterwebview',
        onPlatformViewCreated: _onPlatformViewCreated,
      );
    }
    return Text(
        '$defaultTargetPlatform is not yet supported by the text_view plugin');
  }

  void _onPlatformViewCreated(int id) {
    if (widget.onJitsiViewCreated == null) {
      return;
    }
    widget.onJitsiViewCreated(new JitsiViewController._(id));
  }
}

class JitsiViewController {
  JitsiViewController._(int id)
      : _channel = new MethodChannel('breaklounge/jitsiview_$id');

  final MethodChannel _channel;

  Future<void> join(JitsiMeetingOptions options) async {
    assert(options != null);
    return _channel.invokeMethod('join', options);
  }
}