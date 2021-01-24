package com.gunschu.jitsi_meet

import android.content.Context
import android.view.View
import android.widget.TextView
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.platform.PlatformView
import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.annotation.NonNull
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import org.jitsi.meet.sdk.JitsiMeetUserInfo
import org.jitsi.meet.sdk.JitsiMeetView
import java.net.URL

class FlutterJitsiView internal constructor(context: Context, messenger: BinaryMessenger?, id: Int) : PlatformView, MethodCallHandler {
    private val JITSI_PLUGIN_TAG = "JITSI_MEET_PLUGIN"

    private val jitsiMeetView: JitsiMeetView
    private val methodChannel: MethodChannel

    override fun getView(): View {
        return jitsiMeetView
    }

    override fun onMethodCall(methodCall: MethodCall, result: MethodChannel.Result) {
        when (methodCall.method) {
            "join" -> join(methodCall, result)
            else -> result.notImplemented()
        }
    }

    private fun join(call: MethodCall, result: Result) {
        val room = call.argument<String>("room")
        if (room.isNullOrBlank()) {
            result.error("400",
                    "room can not be null or empty",
                    "room can not be null or empty")
            return
        }

        Log.d(JITSI_PLUGIN_TAG, "Joining Room: $room")

        val userInfo = JitsiMeetUserInfo()
        userInfo.displayName = call.argument("userDisplayName")
        userInfo.email = call.argument("userEmail")
        if (call.argument<String?>("userAvatarURL") != null) {
            userInfo.avatar = URL(call.argument("userAvatarURL"))
        }

        var serverURLString = call.argument<String>("serverURL")
        if (serverURLString == null) {
            serverURLString = "https://meet.jit.si";
        }
        val serverURL = URL(serverURLString)
        Log.d(JITSI_PLUGIN_TAG, "Server URL: $serverURL, $serverURLString")

        val optionsBuilder = JitsiMeetConferenceOptions.Builder()

        // Set meeting options
        optionsBuilder
                .setServerURL(serverURL)
                .setRoom(room)
                .setSubject(call.argument("subject"))
                .setToken(call.argument("token"))
                .setAudioMuted(call.argument("audioMuted") ?: false)
                .setAudioOnly(call.argument("audioOnly") ?: false)
                .setVideoMuted(call.argument("videoMuted") ?: false)
                .setUserInfo(userInfo)

        // Add feature flags into options, reading given Map
        if (call.argument<HashMap<String, Any>?>("featureFlags") != null) {
            val featureFlags = call.argument<HashMap<String, Any>>("featureFlags")
            featureFlags!!.forEach { (key, value) ->
                if (value is Boolean) {
                    val boolVal = value.toString().toBoolean()
                    optionsBuilder.setFeatureFlag(key, boolVal)
                } else {
                    val intVal = value.toString().toInt()
                    optionsBuilder.setFeatureFlag(key, intVal)
                }
            }
        }

        // Build with meeting options and feature flags
        val options = optionsBuilder.build()

        jitsiMeetView.join(options)
        result.success(null)
    }

    override fun dispose() {
    }

    init {
        jitsiMeetView = JitsiMeetView(context)
        methodChannel = MethodChannel(messenger, "breaklounge/jitsiview_$id")
        methodChannel.setMethodCallHandler(this)
    }
}
