# Migration Guide: Java to Kotlin

This guide helps you migrate from the legacy Java version of FFmpeg CLI Wrapper to the new Kotlin version.

## Table of Contents

- [Overview](#overview)
- [Key Changes](#key-changes)
- [Installation](#installation)
- [Package Changes](#package-changes)
- [API Changes](#api-changes)
    - [FFmpeg and FFprobe Construction](#ffmpeg-and-ffprobe-construction)
    - [Builder Pattern](#builder-pattern)
    - [Kotlin DSL (Recommended)](#kotlin-dsl-recommended)
- [Migration Examples](#migration-examples)
    - [Basic Video Encoding](#basic-video-encoding)
    - [Two-Pass Encoding](#two-pass-encoding)
    - [Get Media Information](#get-media-information)
    - [Progress Tracking](#progress-tracking)
    - [Multiple Outputs](#multiple-outputs)
    - [HLS Streaming](#hls-streaming)
- [Breaking Changes](#breaking-changes)
- [New Features](#new-features)
- [Best Practices](#best-practices)

## Overview

The FFmpeg CLI Wrapper has been completely rewritten in Kotlin, providing:

- **100% Kotlin**: Native Kotlin implementation with better type safety
- **Kotlin DSL**: More concise and expressive syntax for Kotlin users
- **Backward Compatibility**: Original builder pattern still supported
- **Improved API**: Better null safety and immutability
- **Enhanced Features**: New convenience methods and extensions

## Key Changes

1. **Language**: Codebase migrated from Java to Kotlin
2. **Package**: Changed from `net.bramp.ffmpeg` to `net.bramp.ffmpeg.kotlin`
3. **Builder Pattern**: Still available but enhanced with Kotlin features
4. **DSL**: New Kotlin DSL for more idiomatic code
5. **Null Safety**: Leverages Kotlin's null safety features
6. **Properties**: Java getters/setters replaced with Kotlin properties where appropriate

## Installation

### Previous Version (Java)

Maven:

```xml
<dependency>
    <groupId>net.bramp.ffmpeg</groupId>
    <artifactId>ffmpeg</artifactId>
    <version>0.8.0</version>
</dependency>
```

Gradle:

```groovy
implementation 'net.bramp.ffmpeg:ffmpeg:0.8.0'
```

### New Version (Kotlin)

Gradle (Kotlin DSL):

```kotlin
implementation("com.eygraber:ffmpeg-cli-wrapper:0.10.0")
```

Gradle (Groovy DSL):

```groovy
implementation 'com.eygraber:ffmpeg-cli-wrapper:0.10.0'
```

## Package Changes

### Java Version

```java
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
```

### Kotlin Version

```kotlin
import net.bramp.ffmpeg.kotlin.FFmpeg
import net.bramp.ffmpeg.kotlin.FFprobe
import net.bramp.ffmpeg.kotlin.FFmpegExecutor
import net.bramp.ffmpeg.kotlin.builder.FFmpegBuilder
import net.bramp.ffmpeg.kotlin.probe.FFmpegProbeResult
```

## API Changes

### FFmpeg and FFprobe Construction

#### Java Version

```java
FFmpeg ffmpeg = new FFmpeg("/path/to/ffmpeg");
FFprobe ffprobe = new FFprobe("/path/to/ffprobe");
```

#### Kotlin Version (Builder Pattern)

```kotlin
val ffmpeg = FFmpeg("/path/to/ffmpeg")
val ffprobe = FFprobe("/path/to/ffprobe")
```

#### Kotlin Version (Default Paths)

```kotlin
// Uses default system paths
val ffmpeg = FFmpeg()
val ffprobe = FFprobe()
```

### Builder Pattern

The builder pattern remains available and works similarly, with Kotlin syntax improvements.

#### Java Version

```java
FFmpegBuilder builder = new FFmpegBuilder()
    .setInput("input.mp4")
    .done()
    .addOutput("output.mp4")
        .setVideoCodec("libx264")
        .setAudioCodec("aac")
    .done();
```

#### Kotlin Version (Builder Pattern)

```kotlin
val builder = FFmpegBuilder()
    .setInput("input.mp4")
    .done()
    .addOutput("output.mp4")
        .setVideoCodec("libx264")
        .setAudioCodec("aac")
    .done()
```

### Kotlin DSL (Recommended)

The new Kotlin DSL provides a more concise and readable syntax:

```kotlin
import net.bramp.ffmpeg.kotlin.dsl.ffmpeg

val builder = ffmpeg {
    input("input.mp4")
    output("output.mp4") {
        videoCodec = "libx264"
        audioCodec = "aac"
    }
}
```

## Migration Examples

### Basic Video Encoding

#### Java Version

```java
FFmpeg ffmpeg = new FFmpeg("/path/to/ffmpeg");
FFprobe ffprobe = new FFprobe("/path/to/ffprobe");

FFmpegBuilder builder = new FFmpegBuilder()
    .setInput("input.mp4")
    .done()
    .overrideOutputFiles(true)
    .addOutput("output.mp4")
        .setFormat("mp4")
        .setTargetSize(250_000)
        .disableSubtitle()
        .setAudioChannels(1)
        .setAudioCodec("aac")
        .setAudioSampleRate(48_000)
        .setAudioBitRate(32768)
        .setVideoCodec("libx264")
        .setVideoFrameRate(24, 1)
        .setVideoResolution(640, 480)
        .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL)
    .done();

FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
executor.createJob(builder).run();
```

#### Kotlin Version (Builder Pattern)

```kotlin
val ffmpeg = FFmpeg("/path/to/ffmpeg")
val ffprobe = FFprobe("/path/to/ffprobe")

val builder = FFmpegBuilder()
    .setInput("input.mp4")
    .done()
    .overrideOutputFiles(true)
    .addOutput("output.mp4")
        .setFormat("mp4")
        .setTargetSize(250_000)
        .disableSubtitle()
        .setAudioChannels(1)
        .setAudioCodec("aac")
        .setAudioSampleRate(48_000)
        .setAudioBitRate(32768)
        .setVideoCodec("libx264")
        .setVideoFrameRate(24, 1)
        .setVideoResolution(640, 480)
        .setStrict(Strict.EXPERIMENTAL)
    .done()

val executor = FFmpegExecutor(ffmpeg, ffprobe)
executor.createJob(builder).run()
```

#### Kotlin Version (DSL) - Recommended

```kotlin
import net.bramp.ffmpeg.kotlin.dsl.run

val ffmpeg = FFmpeg()
val ffprobe = FFprobe()
val executor = FFmpegExecutor(ffmpeg, ffprobe)

executor.run {
    input("input.mp4")
    output("output.mp4") {
        format = "mp4"
        targetSize = 250_000
        disableSubtitle()
        
        audioChannels = 1
        audioCodec = "aac"
        audioSampleRate = 48_000
        audioBitRate = 32768
        
        videoCodec = "libx264"
        videoFrameRate(24, 1)
        videoResolution(640, 480)
        strict = Strict.EXPERIMENTAL
    }
}
```

### Two-Pass Encoding

#### Java Version

```java
FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
executor.createTwoPassJob(builder).run();
```

#### Kotlin Version (Builder Pattern)

```kotlin
val executor = FFmpegExecutor(ffmpeg, ffprobe)
executor.createTwoPassJob(builder).run()
```

#### Kotlin Version (DSL) - Recommended

```kotlin
import net.bramp.ffmpeg.kotlin.dsl.runTwoPass

executor.runTwoPass {
    input("input.mp4")
    output("output.mp4") {
        videoCodec = "libx264"
        videoBitRate = 2_000_000
        audioCodec = "aac"
    }
}
```

### Get Media Information

#### Java Version

```java
FFprobe ffprobe = new FFprobe("/path/to/ffprobe");
FFmpegProbeResult probeResult = ffprobe.probe("input.mp4");

FFmpegFormat format = probeResult.getFormat();
System.out.format("%nFile: '%s' ; Format: '%s' ; Duration: %.3fs", 
    format.filename,
    format.format_long_name,
    format.duration
);

FFmpegStream stream = probeResult.getStreams().get(0);
System.out.format("%nCodec: '%s' ; Width: %dpx ; Height: %dpx",
    stream.codec_long_name,
    stream.width,
    stream.height
);
```

#### Kotlin Version

```kotlin
val ffprobe = FFprobe()
val probeResult = ffprobe.probe("input.mp4")

val format = probeResult.format
println("File: '${format?.filename}' ; Format: '${format?.format_long_name}' ; Duration: ${format?.duration}s")

val stream = probeResult.streams?.firstOrNull()
println("Codec: '${stream?.codec_long_name}' ; Width: ${stream?.width}px ; Height: ${stream?.height}px")
```

#### Kotlin Version (DSL) - Recommended

```kotlin
import net.bramp.ffmpeg.kotlin.dsl.probe

val probeResult = ffprobe.probe {
    input = "input.mp4"
    showFormat = true
    showStreams = true
}

probeResult.format?.let { format ->
    println("File: '${format.filename}' ; Format: '${format.format_long_name}' ; Duration: ${format.duration}s")
}

probeResult.streams?.firstOrNull()?.let { stream ->
    println("Codec: '${stream.codec_long_name}' ; Width: ${stream.width}px ; Height: ${stream.height}px")
}
```

### Progress Tracking

#### Java Version

```java
FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
FFmpegProbeResult in = ffprobe.probe("input.flv");

FFmpegBuilder builder = new FFmpegBuilder()
    .setInput(in)
    .done()
    .addOutput("output.mp4")
    .done();

FFmpegJob job = executor.createJob(builder, new ProgressListener() {
    final double duration_ns = in.getFormat().duration * TimeUnit.SECONDS.toNanos(1);

    @Override
    public void progress(Progress progress) {
        double percentage = progress.out_time_ns / duration_ns;
        
        System.out.println(String.format(
            "[%.0f%%] status:%s frame:%d time:%s ms fps:%.0f speed:%.2fx",
            percentage * 100,
            progress.status,
            progress.frame,
            FFmpegUtils.toTimecode(progress.out_time_ns, TimeUnit.NANOSECONDS),
            progress.fps.doubleValue(),
            progress.speed
        ));
    }
});

job.run();
```

#### Kotlin Version (Builder Pattern)

```kotlin
val executor = FFmpegExecutor(ffmpeg, ffprobe)
val probeResult = ffprobe.probe("input.flv")

val builder = FFmpegBuilder()
    .setInput(probeResult)
    .done()
    .addOutput("output.mp4")
    .done()

val durationNs = (probeResult.format?.duration ?: 0.0) * TimeUnit.SECONDS.toNanos(1)

val job = executor.createJob(builder, ProgressListener { progress ->
    val percentage = progress.out_time_ns / durationNs
    
    println(
        "[${(percentage * 100).toInt()}%] status:${progress.status} " +
        "frame:${progress.frame} time:${FFmpegUtils.toTimecode(progress.out_time_ns, TimeUnit.NANOSECONDS)} " +
        "fps:${progress.fps.toDouble().toInt()} speed:${String.format("%.2f", progress.speed)}x"
    )
})

job.run()
```

#### Kotlin Version (DSL) - Recommended

```kotlin
import net.bramp.ffmpeg.kotlin.dsl.job
import net.bramp.ffmpeg.kotlin.progress.ProgressListener

val probeResult = ffprobe.probe("input.flv")
val durationNs = (probeResult.format?.duration ?: 0.0) * TimeUnit.SECONDS.toNanos(1)

val progressListener = ProgressListener { progress ->
    val percentage = progress.out_time_ns / durationNs
    println("[${(percentage * 100).toInt()}%] status:${progress.status} frame:${progress.frame}")
}

executor.job(progressListener) {
    input(probeResult)
    output("output.mp4")
}.run()
```

### Multiple Outputs

#### Java Version

```java
FFmpegBuilder builder = new FFmpegBuilder()
    .setInput("input.mp4")
    .done()
    .addOutput("hd.mp4")
        .setVideoCodec("libx264")
        .setVideoResolution(1920, 1080)
        .setVideoBitRate(5_000_000)
    .done()
    .addOutput("sd.mp4")
        .setVideoCodec("libx264")
        .setVideoResolution(640, 480)
        .setVideoBitRate(1_000_000)
    .done();

executor.createJob(builder).run();
```

#### Kotlin Version (DSL) - Recommended

```kotlin
executor.run {
    input("input.mp4")
    
    output("hd.mp4") {
        videoCodec = "libx264"
        videoResolution(1920, 1080)
        videoBitRate = 5_000_000
    }
    
    output("sd.mp4") {
        videoCodec = "libx264"
        videoResolution(640, 480)
        videoBitRate = 1_000_000
    }
}
```

### HLS Streaming

#### Java Version

```java
FFmpegBuilder builder = new FFmpegBuilder()
    .setInput("input.mp4")
    .done()
    .addHlsOutput("stream.m3u8")
        .setHlsTime(10, TimeUnit.SECONDS)
        .setHlsSegmentFileName("segment%03d.ts")
        .setVideoCodec("libx264")
    .done();

executor.createJob(builder).run();
```

#### Kotlin Version (DSL) - Recommended

```kotlin
import net.bramp.ffmpeg.kotlin.dsl.hlsOutput

executor.run {
    input("input.mp4")
    
    hlsOutput("stream.m3u8") {
        hlsTime(10, TimeUnit.SECONDS)
        hlsSegmentFilename = "segment%03d.ts"
        videoCodec = "libx264"
    }
}
```

## Breaking Changes

### 1. Package Names

All classes moved from `net.bramp.ffmpeg` to `net.bramp.ffmpeg.kotlin`

**Migration**: Update all import statements to use the new package.

### 2. Nullable Types

Many return types are now nullable in Kotlin, reflecting actual API behavior.

**Java**:

```java
FFmpegFormat format = probeResult.getFormat(); // Never null (could throw NPE)
```

**Kotlin**:

```kotlin
val format = probeResult.format // Nullable type: FFmpegFormat?
```

**Migration**: Add null checks or use Kotlin's safe call operator (`?.`).

### 3. Properties vs Methods

Kotlin properties replace Java getters/setters where appropriate.

**Java**:

```java
String filename = format.getFilename();
format.setFilename("new.mp4");
```

**Kotlin**:

```kotlin
val filename = format.filename
format.filename = "new.mp4"
```

### 4. Collection Types

Collections are now properly typed in Kotlin.

**Java**:

```java
List<FFmpegStream> streams = probeResult.getStreams();
```

**Kotlin**:

```kotlin
val streams: List<FFmpegStream>? = probeResult.streams
```

### 5. Enums

Enum values follow Kotlin conventions.

**Java**:

```java
builder.setStrict(FFmpegBuilder.Strict.EXPERIMENTAL);
```

**Kotlin**:

```kotlin
builder.setStrict(Strict.EXPERIMENTAL)
// or with DSL:
strict = Strict.EXPERIMENTAL
```

## New Features

### 1. Kotlin DSL

The biggest new feature - write more concise and readable code:

```kotlin
executor.run {
    input("input.mp4") {
        startOffset(5, TimeUnit.SECONDS)
    }
    output("output.mp4") {
        videoCodec = "libx264"
        videoResolution(1280, 720)
        audioCodec = "aac"
    }
}
```

### 2. Extension Functions

Convenient extension functions for common operations:

```kotlin
// Direct execution
executor.run { ... }
executor.runTwoPass { ... }

// Job creation
val job = executor.job { ... }
val job = executor.job(progressListener) { ... }

// FFprobe
val result = ffprobe.probe { ... }
```

### 3. Type-Safe Builders

Better type safety with Kotlin's type system:

```kotlin
output("output.mp4") {
    videoCodec = "libx264"  // String property
    videoBitRate = 2_000_000  // Long property
    videoQuality = 23.0  // Double property
}
```

### 4. Improved Null Safety

Kotlin's null safety prevents many runtime errors:

```kotlin
val format = probeResult.format
val duration = format?.duration ?: 0.0  // Safe null handling
```

### 5. Named Parameters

Use named parameters for clarity:

```kotlin
executor.job(
    progressListener = myListener
) {
    input("input.mp4")
    output("output.mp4")
}
```

### 6. Default Parameters

Many methods now have default parameters:

```kotlin
val ffmpeg = FFmpeg()  // Uses default path
val ffprobe = FFprobe()  // Uses default path
```

## Best Practices

### 1. Use the Kotlin DSL

For Kotlin projects, prefer the DSL over the builder pattern:

```kotlin
// ✅ Recommended
executor.run {
    input("input.mp4")
    output("output.mp4") {
        videoCodec = "libx264"
    }
}

// ❌ Less idiomatic
val builder = FFmpegBuilder()
    .setInput("input.mp4")
    .done()
    .addOutput("output.mp4")
        .setVideoCodec("libx264")
    .done()
executor.createJob(builder).run()
```

### 2. Leverage Null Safety

Use Kotlin's null-safe operators:

```kotlin
// ✅ Safe null handling
val duration = probeResult.format?.duration ?: 0.0

// ❌ Potential NPE
val duration = probeResult.format!!.duration
```

### 3. Use Named Parameters

Improve code readability with named parameters:

```kotlin
// ✅ Clear intent
videoResolution(width = 1920, height = 1080)

// ❌ Less clear
videoResolution(1920, 1080)
```

### 4. Scope Functions

Use Kotlin's scope functions for cleaner code:

```kotlin
probeResult.format?.let { format ->
    println("Duration: ${format.duration}s")
    println("Format: ${format.format_long_name}")
}
```

### 5. Extension Functions

Import extension functions for convenience:

```kotlin
import net.bramp.ffmpeg.kotlin.dsl.run
import net.bramp.ffmpeg.kotlin.dsl.probe

executor.run { ... }
ffprobe.probe { ... }
```

### 6. Type Inference

Let Kotlin infer types where possible:

```kotlin
// ✅ Concise
val ffmpeg = FFmpeg()
val executor = FFmpegExecutor(ffmpeg, ffprobe)

// ❌ Verbose
val ffmpeg: FFmpeg = FFmpeg()
val executor: FFmpegExecutor = FFmpegExecutor(ffmpeg, ffprobe)
```

### 7. Immutability

Prefer `val` over `var`:

```kotlin
// ✅ Immutable
val ffmpeg = FFmpeg()
val builder = ffmpeg { ... }

// ❌ Mutable (unless necessary)
var ffmpeg = FFmpeg()
var builder = ffmpeg { ... }
```

## Java Interoperability

The Kotlin version maintains good Java interoperability. Java code can still use the library:

```java
// Java code using Kotlin library
FFmpeg ffmpeg = new FFmpeg();
FFprobe ffprobe = new FFprobe();
FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);

FFmpegBuilder builder = new FFmpegBuilder()
    .setInput("input.mp4")
    .done()
    .addOutput("output.mp4")
        .setVideoCodec("libx264")
    .done();

executor.createJob(builder).run();
```

However, the DSL functions are less ergonomic from Java and should be avoided.

## Migration Checklist

- [ ] Update dependency from `net.bramp.ffmpeg:ffmpeg` to `com.eygraber:ffmpeg-cli-wrapper`
- [ ] Update all imports from `net.bramp.ffmpeg` to `net.bramp.ffmpeg.kotlin`
- [ ] Convert Java getters/setters to Kotlin properties
- [ ] Add null checks or use safe call operators for nullable types
- [ ] Consider migrating to Kotlin DSL for Kotlin code
- [ ] Update enum references if needed
- [ ] Test all FFmpeg operations to ensure compatibility
- [ ] Update documentation and examples

## Getting Help

- **Documentation**: See [README.md](README.md) for basic usage
- **DSL Guide**: See [DSL.md](DSL.md) for comprehensive DSL documentation
- **Issues**: Report issues on [GitHub](https://github.com/eygraber/ffmpeg-cli-wrapper/issues)
- **Examples**: Check the test files for real-world examples

## Conclusion

The migration from Java to Kotlin brings significant improvements in code clarity, safety, and developer experience.
While the builder pattern remains available for backward compatibility, we strongly recommend using the Kotlin DSL for
new Kotlin projects to take full advantage of Kotlin's features.

For projects that need to maintain Java compatibility or have existing Java codebases, the builder pattern continues to
work as before with minimal changes required beyond package name updates.
