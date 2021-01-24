package com.gunschu.jitsi_meet

import android.content.Context
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.StandardMessageCodec
import io.flutter.plugin.platform.PlatformView
import io.flutter.plugin.platform.PlatformViewFactory

class JitsiViewFactory(messenger: BinaryMessenger) : PlatformViewFactory(StandardMessageCodec.INSTANCE) {
    private val messenger: BinaryMessenger

    override fun create(context: Context, id: Int, o: Any?): PlatformView {
        return FlutterJitsiView(context = context, id = id, messenger = messenger)
    }

    init {
        this.messenger = messenger
    }
}
