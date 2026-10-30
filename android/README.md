# Sidee Android spike (Phase 3)

A minimal APK that proves the plan's spike gate before any commitment
to a full Android build:

1. Chaquopy runs the shared Python core + `paho-mqtt` and the
   dashboard server starts on loopback inside the app.
2. SSDP discovery with a Wi-Fi multicast lock (the Activity acquires
   one; a TV, or a simulated TV on the LAN, is still needed to prove
   discovery end to end).
3. MQTT/TLS to the TV broker and a PIN flow (same core code as
   desktop; needs a TV or simulated TV to prove end to end).
4. The Android WebView loads the loopback dashboard and the grid;
   a session survives an app restart in private storage.
5. The APK builds in CI (Android SDK + Chaquopy + Gradle).

## What this is, and is not

- One Activity (`MainActivity`) + one Python entry module
  (`sidee_android.py`). The dashboard, pairing, catalogue and grid
  are the shared desktop core, copied into the Chaquopy source tree
  at build time by the `copyCore` Gradle task — never a second copy
  committed to the repo. The generated tree
  (`app/src/main/python/core`, `app/src/main/python/assets`) is
  gitignored.
- The WebView loads **only** `127.0.0.1` / `localhost`. Every other
  URL is refused in `shouldOverrideUrlLoading`. Tile URLs are sent
  to the TV by the dashboard code, never opened in the WebView.
- The dashboard binds loopback only (`host="127.0.0.1"`), a new
  option on `core.webui.Dashboard` (desktop default unchanged).
- Spike application ID: `io.github.johncarter2021.sidee` (fork
  owner). A release identifier is still an open decision.
- Sideload only in v1 (no Play). Unsigned debug APK in CI.

## Building

CI builds the debug APK (see the `android` job in
`.github/workflows/build.yml`). Locally you need JDK 17, Gradle
8.9, and the Android SDK (API 34, build-tools 34.0.0):

```
export ANDROID_HOME=/path/to/android-sdk
cd android
gradle :app:assembleDebug
```

The APK lands at `app/build/outputs/apk/debug/app-debug.apk`.

## What the spike does not yet prove

Items 2 and 3 need a TV (or a simulated TV on the LAN) and a phone
or emulator run. The CI emulator step, when it lands, proves items
1 and 4 on an emulator; items 2 and 3 stay open until a TV or
simulated TV is available. If any gate item fails, the plan's
fallbacks apply in order: defer Android, then a native Kotlin port
— never a WebView wrapper around a PC dashboard.
