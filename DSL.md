# Kotlin DSL for FFmpeg CLI Wrapper

This document describes the Kotlin DSL (Domain-Specific Language) interface for the FFmpeg CLI Wrapper. The DSL provides
a more idiomatic and concise way to build FFmpeg commands in Kotlin while maintaining full compatibility with the
existing builder pattern.

## Table of Contents

- [Overview](#overview)
- [Basic Usage](#basic-usage)
- [FFmpeg DSL](#ffmpeg-dsl)
    - [Input Configuration](#input-configuration)
    - [Output Configuration](#output-configuration)
    - [HLS Output](#hls-output)
    - [Global Settings](#global-settings)
- [FFprobe DSL](#ffprobe-dsl)
- [Extension Functions](#extension-functions)
- [Migration from Builders](#migration-from-builders)
- [Examples](#examples)

## Overview

The Kotlin DSL wraps the existing builder classes (`FFmpegBuilder`, `FFprobeBuilder`, etc.) and provides a more concise
and type-safe API. The builders are still available and can be used directly - the DSL is simply a convenience layer on
top.

**Key benefits:**

- More concise and readable code
- Type-safe property setters
- Idiomatic Kotlin syntax
- Automatic handling of `done()` calls
- Full compatibility with existing builders

## Basic Usage

### FFmpeg Command Building

Instead of the traditional builder pattern:

```kotlin
// Old way (still supported)
val builder = FFmpegBuilder()
  .setInput("input.mp4")
  .done()
  .addOutput("output.mp4")
    .setVideoCodec("libx264")
    .setAudioCodec("aac")
  .done()
```

You can now use the DSL:

```kotlin
// New DSL way
val builder = ffmpeg {
  input("input.mp4")
  output("output.mp4") {
    videoCodec = "libx264"
    audioCodec = "aac"
  }
}
```

### Direct Execution

Execute FFmpeg commands directly:

```kotlin
val ffmpeg = FFmpeg()
val ffprobe = FFprobe()
val executor = FFmpegExecutor(ffmpeg, ffprobe)

// Execute immediately
executor.run {
  input("input.mp4")
  output("output.mp4") {
    videoCodec = "libx264"
    videoResolution(640, 480)
  }
}
```

## FFmpeg DSL

### Input Configuration

Add input files with configuration:

```kotlin
ffmpeg {
  input("input.mp4") {
    format = "mp4"
    startOffset(1500, TimeUnit.MILLISECONDS)
    duration(30, TimeUnit.SECONDS)
    readAtNativeFrameRate()
    streamLoop = 2  // Loop the input
    extraArgs("-thread_queue_size", "4096")
  }
  
  // Multiple inputs
  input("audio.mp3") {
    format = "mp3"
  }
  
  // Input from probe result
  val probeResult = ffprobe.probe("input.mp4")
  input(probeResult) {
    startOffset(5, TimeUnit.SECONDS)
  }
}
```

#### Input Properties and Methods

- `format`: Set input format
- `startOffset(duration, unit)`: Set start offset
- `duration(duration, unit)`: Set duration
- `readAtNativeFrameRate()`: Read at native frame rate
- `streamLoop`: Set stream loop count (-1 for infinite)
- `videoResolution(width, height)`: Set video resolution
- `videoFrameRate(rate)`: Set video frame rate
- `extraArgs(vararg args)`: Add extra arguments

### Output Configuration

Configure output files:

```kotlin
ffmpeg {
  input("input.mp4")
  
  output("output.mp4") {
    // Format
    format = "mp4"
    
    // Video settings
    videoCodec = "libx264"
    videoResolution(1920, 1080)
    videoFrameRate(30, 1)  // 30 fps
    videoBitRate = 5_000_000  // 5 Mbps
    videoQuality = 23.0
    constantRateFactor = 23.0  // CRF for x264/x265
    videoPreset = "fast"
    videoPixelFormat = "yuv420p"
    videoFilter = "scale=640:480"
    bFrames = 2
    frames = 100  // Only encode 100 frames
    
    // Audio settings
    audioCodec = "aac"
    audioChannels = 2
    audioSampleRate = 48_000
    audioBitRate = 128_000
    audioQuality = 2.0
    audioSampleFormat = "fltp"
    audioFilter = "volume=0.5"
    
    // Subtitle settings
    subtitleCodec = "mov_text"
    
    // Timing
    startOffset(5, TimeUnit.SECONDS)
    duration(60, TimeUnit.SECONDS)
    
    // Metadata
    metadata("title", "My Video")
    metadata("author", "John Doe")
    
    // Disable streams
    disableVideo()
    disableAudio()
    disableSubtitle()
    
    // Presets
    preset = "ultrafast"
    presetFilename = "mypreset.ffpreset"
    
    // Advanced
    complexFilter = "[0:v]scale=640:480[out]"
    strict = Strict.Experimental
    targetSize = 10_000_000  // Target 10 MB file
    
    // Extra arguments
    extraArgs("-map", "0:0", "-shortest")
  }
  
  // Multiple outputs
  output("output-hd.mp4") {
    videoCodec = "libx264"
    videoResolution(1920, 1080)
  }
  
  output("output-sd.mp4") {
    videoCodec = "libx264"
    videoResolution(640, 480)
  }
  
  // URI output (e.g., streaming)
  output(URI.create("udp://10.1.0.102:1234")) {
    format = "mpegts"
    videoCodec = "libx264"
  }
  
  // Output to stdout
  stdoutOutput {
    format = "mp4"
    videoCodec = "libx264"
  }
}
```

#### Output Properties and Methods

**Video:**

- `videoCodec`: Video codec
- `videoResolution(width, height)`: Set resolution
- `videoResolution(abbreviation)`: Set resolution by name (e.g., "hd720")
- `videoFrameRate`: Frame rate (property or method)
- `videoBitRate`: Video bit rate
- `videoQuality`: Video quality
- `videoPreset`: Video preset
- `videoFilter`: Video filter
- `videoBitStreamFilter`: Video bit stream filter
- `constantRateFactor`: CRF value
- `videoPixelFormat`: Pixel format
- `bFrames`: Number of B-frames
- `videoMovFlags`: MOV flags
- `frames`: Number of frames to encode

**Audio:**

- `audioCodec`: Audio codec
- `audioChannels`: Number of audio channels
- `audioSampleRate`: Audio sample rate
- `audioSampleFormat`: Audio sample format
- `audioBitRate`: Audio bit rate
- `audioQuality`: Audio quality
- `audioBitStreamFilter`: Audio bit stream filter
- `audioFilter`: Audio filter

**Other:**

- `format`: Output format
- `subtitleCodec`: Subtitle codec
- `preset`: Encoding preset
- `presetFilename`: Preset file
- `complexFilter`: Complex filter
- `strict`: Strict mode
- `targetSize`: Target file size
- `startOffset(duration, unit)`: Start offset
- `duration(duration, unit)`: Duration
- `metadata(key, value)`: Add metadata
- `disableVideo()`, `disableAudio()`, `disableSubtitle()`: Disable streams
- `extraArgs(vararg args)`: Extra arguments

### HLS Output

Configure HLS (HTTP Live Streaming) outputs:

```kotlin
ffmpeg {
  input("input.mp4")
  
  hlsOutput("output.m3u8") {
    // HLS-specific settings
    hlsTime(10, TimeUnit.SECONDS)
    hlsSegmentFilename = "segment%03d.ts"
    hlsInitTime(5, TimeUnit.SECONDS)
    hlsListSize = 5
    hlsBaseUrl = "https://example.com/segments/"
    
    // Standard video/audio settings
    videoCodec = "libx264"
    videoResolution(1280, 720)
    videoBitRate = 2_500_000
    audioCodec = "aac"
    audioBitRate = 128_000
  }
}
```

### Global Settings

Configure global FFmpeg settings:

```kotlin
ffmpeg {
  // Override output files (default: true)
  overrideOutputFiles = false
  
  // Verbosity level
  verbosity = FFmpegBuilder.Verbosity.Debug
  
  // User agent for network streams
  userAgent = "Mozilla/5.0"
  
  // Two-pass encoding
  pass = 1
  passDirectory = "/tmp"
  passPrefix = "ffmpeg2pass"
  
  // Strict mode
  strict = Strict.Experimental
  
  // Number of threads
  threads = 4
  
  // Global filters
  audioFilter = "volume=0.8"
  videoFilter = "eq=brightness=0.1"
  
  // VBR quality for audio (0-9, where 0 is best)
  vbr(2)
  
  // Progress reporting
  progress(URI.create("tcp://127.0.0.1:9999"))
  
  // Extra arguments
  extraArgs("-benchmark")
  
  input("input.mp4")
  output("output.mp4")
}
```

## FFprobe DSL

Build FFprobe commands:

```kotlin
// Basic usage
val builder = ffprobe {
  input = "input.mp4"
}

// Custom configuration
val builder = ffprobe {
  input = "input.mp4"
  showFormat = true
  showStreams = true
  showChapters = false
  showFrames = true
  showPackets = false
  userAgent = "MyApp/1.0"
  extraArgs("-select_streams", "v:0")
}

// Execute directly
val probe = FFprobe()
val result = probe.probe {
  input = "input.mp4"
  showFormat = true
  showStreams = true
}
```

## Extension Functions

The DSL provides several extension functions for common operations:

### FFmpegExecutor Extensions

```kotlin
val executor = FFmpegExecutor(ffmpeg, ffprobe)

// Execute immediately
executor.run {
  input("input.mp4")
  output("output.mp4") {
    videoCodec = "libx264"
  }
}

// Two-pass encoding
executor.runTwoPass {
  input("input.mp4")
  output("output.mp4") {
    videoCodec = "libx264"
    videoBitRate = 1_000_000
  }
}

// Create job for later execution
val job = executor.job {
  input("input.mp4")
  output("output.mp4")
}
job.run()

// With progress listener
val job = executor.job(progressListener) {
  input("input.mp4")
  output("output.mp4")
}

// Two-pass job
val job = executor.twoPassJob {
  input("input.mp4")
  output("output.mp4")
}
```

### FFprobe Extensions

```kotlin
val probe = FFprobe()

// Probe with DSL
val result = probe.probe {
  input = "input.mp4"
  showFormat = true
  showStreams = true
}
```

### Command Building Helpers

```kotlin
// Build command arguments
val args = buildFFmpegCommand {
  input("input.mp4")
  output("output.mp4") {
    videoCodec = "libx264"
  }
}

val args = buildFFprobeCommand {
  input = "input.mp4"
  showFormat = true
}
```

## Migration from Builders

The DSL is fully compatible with the existing builders. You can mix and match:

### Before (Builder Pattern)

```kotlin
val builder = FFmpegBuilder()
  .setInput("input.mp4")
  .setStartOffset(1500, TimeUnit.MILLISECONDS)
  .done()
  .overrideOutputFiles(true)
  .addOutput("output.mp4")
    .setFormat("mp4")
    .setAudioCodec("aac")
    .setAudioChannels(1)
    .setAudioSampleRate(48_000)
    .setVideoBitRate(1_000_000)
    .setVideoCodec("libx264")
    .setVideoFrameRate(30, 1)
    .setVideoResolution(640, 480)
  .done()
  .build()
```

### After (DSL)

```kotlin
val builder = ffmpeg {
  input("input.mp4") {
    startOffset(1500, TimeUnit.MILLISECONDS)
  }
  
  output("output.mp4") {
    format = "mp4"
    audioCodec = "aac"
    audioChannels = 1
    audioSampleRate = 48_000
    videoBitRate = 1_000_000
    videoCodec = "libx264"
    videoFrameRate(30, 1)
    videoResolution(640, 480)
  }
}

val args = builder.build()
```

## Examples

### Example 1: Simple Video Conversion

```kotlin
executor.run {
  input("input.avi")
  output("output.mp4") {
    videoCodec = "libx264"
    audioCodec = "aac"
  }
}
```

### Example 2: Video with Custom Resolution and Bitrate

```kotlin
executor.run {
  input("input.mp4")
  output("output.mp4") {
    videoCodec = "libx264"
    videoResolution(1280, 720)
    videoBitRate = 2_500_000
    videoPreset = "fast"
    constantRateFactor = 23.0
    
    audioCodec = "aac"
    audioBitRate = 128_000
  }
}
```

### Example 3: Extract Audio

```kotlin
executor.run {
  input("video.mp4")
  output("audio.mp3") {
    disableVideo()
    audioCodec = "libmp3lame"
    audioBitRate = 192_000
  }
}
```

### Example 4: Create Thumbnail

```kotlin
executor.run {
  input("video.mp4") {
    startOffset(10, TimeUnit.SECONDS)
  }
  output("thumbnail.jpg") {
    frames = 1
    videoResolution(640, 480)
  }
}
```

### Example 5: HLS Streaming

```kotlin
executor.run {
  input("input.mp4")
  
  hlsOutput("stream.m3u8") {
    hlsTime(10, TimeUnit.SECONDS)
    hlsSegmentFilename = "segment%03d.ts"
    hlsListSize = 5
    
    videoCodec = "libx264"
    videoResolution(1920, 1080)
    videoBitRate = 5_000_000
    
    audioCodec = "aac"
    audioBitRate = 128_000
  }
}
```

### Example 6: Multiple Quality Outputs

```kotlin
executor.run {
  input("input.mp4")
  
  output("output-1080p.mp4") {
    videoCodec = "libx264"
    videoResolution(1920, 1080)
    videoBitRate = 5_000_000
  }
  
  output("output-720p.mp4") {
    videoCodec = "libx264"
    videoResolution(1280, 720)
    videoBitRate = 2_500_000
  }
  
  output("output-480p.mp4") {
    videoCodec = "libx264"
    videoResolution(854, 480)
    videoBitRate = 1_000_000
  }
}
```

### Example 7: Apply Filters

```kotlin
executor.run {
  input("input.mp4")
  output("output.mp4") {
    videoCodec = "libx264"
    videoFilter = "scale=640:480,eq=brightness=0.1:contrast=1.2"
    audioFilter = "volume=0.5,highpass=f=200"
  }
}
```

### Example 8: With Progress Tracking

```kotlin
val probeResult = ffprobe.probe("input.mp4")
val duration = probeResult.format?.duration ?: 0.0

val progressListener = ProgressListener { progress ->
  val percentage = (progress.out_time_ns / (duration * 1_000_000_000)) * 100
  println("Progress: ${percentage.toInt()}%")
}

executor.job(progressListener) {
  input("input.mp4")
  output("output.mp4") {
    videoCodec = "libx264"
  }
}.run()
```

### Example 9: Two-Pass Encoding

```kotlin
executor.runTwoPass {
  input("input.mp4")
  output("output.mp4") {
    videoCodec = "libx264"
    videoBitRate = 2_000_000
    audioCodec = "aac"
    audioBitRate = 128_000
  }
}
```

### Example 10: Complex Filter

```kotlin
executor.run {
  input("video1.mp4")
  input("video2.mp4")
  
  output("output.mp4") {
    complexFilter = "[0:v][1:v]hstack[v]"
    extraArgs("-map", "[v]")
    videoCodec = "libx264"
  }
}
```

## Notes

- The DSL automatically calls `done()` on builders when needed
- All builder methods are still available and can be used
- Properties with null values are not applied to the builder
- Numeric properties with zero values (where appropriate) are not applied
- The DSL uses the `@DslMarker` annotation to prevent confusion between nested scopes
- Extension functions follow Kotlin naming conventions (e.g., `run` instead of `execute`)
