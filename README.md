# FFmpeg CLI Wrapper for Java

by Andrew Brampton ([bramp.net](https://bramp.net)) (c) 2013-2024

[!["Buy Me A Coffee"](https://www.buymeacoffee.com/assets/img/custom_images/orange_img.png)](https://www.buymeacoffee.com/bramp)

A fluent interface for running FFmpeg from Java.

![Java](https://img.shields.io/badge/Java-8+-brightgreen.svg)
[![PR Checks](https://github.com/bramp/ffmpeg-cli-wrapper/actions/workflows/pr-checks.yml/badge.svg)](https://github.com/bramp/ffmpeg-cli-wrapper/actions/workflows/pr-checks.yml)
[![Coverage Status](https://img.shields.io/coveralls/bramp/ffmpeg-cli-wrapper.svg)](https://coveralls.io/github/bramp/ffmpeg-cli-wrapper)
[![Maven Central](https://img.shields.io/maven-central/v/net.bramp.ffmpeg/ffmpeg.svg)](http://mvnrepository.com/artifact/net.bramp.ffmpeg/ffmpeg)
[![Libraries.io](https://img.shields.io/librariesio/github/bramp/ffmpeg-cli-wrapper.svg)](https://libraries.io/github/bramp/ffmpeg-cli-wrapper)

[GitHub](https://github.com/bramp/ffmpeg-cli-wrapper) | [API docs](https://bramp.github.io/ffmpeg-cli-wrapper/)

## Install

We currently support Java 8 and above. Use Gradle to install the dependency.

For Gradle (Kotlin DSL, `build.gradle.kts`):
```kotlin
implementation("com.eygraber:ffmpeg-cli-wrapper:0.9.0")
```

For Gradle (Groovy DSL, `build.gradle`):
```groovy
implementation 'com.eygraber:ffmpeg-cli-wrapper:0.9.0'
```

The latest release version can be found on [Maven Central](http://mvnrepository.com/artifact/com.eygraber/ffmpeg).

## Usage

### Video Encoding

Code:

```java
FFmpeg ffmpeg = new FFmpeg("/path/to/ffmpeg");
FFprobe ffprobe = new FFprobe("/path/to/ffprobe");

FFmpegBuilder builder = new FFmpegBuilder()

  .setInput("input.mp4")     // Filename, or a FFmpegProbeResult
  .done()
  .overrideOutputFiles(true) // Override the output if it exists

  .addOutput("output.mp4")   // Filename for the destination
    .setFormat("mp4")        // Format is inferred from filename, or can be set
    .setTargetSize(250_000)  // Aim for a 250KB file

    .disableSubtitle()       // No subtiles

    .setAudioChannels(1)         // Mono audio
    .setAudioCodec("aac")        // using the aac codec
    .setAudioSampleRate(48_000)  // at 48KHz
    .setAudioBitRate(32768)      // at 32 kbit/s

    .setVideoCodec("libx264")     // Video using x264
    .setVideoFrameRate(24, 1)     // at 24 frames per second
    .setVideoResolution(640, 480) // at 640x480 resolution

    .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL) // Allow FFmpeg to use experimental specs
  .done();

FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);

// Run a one-pass encode
executor.createJob(builder).run();

// Or run a two-pass encode (which is better quality at the cost of being slower)
executor.createTwoPassJob(builder).run();
```

### Get Media Information

Code:

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

### Get progress while encoding

```java
FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);

FFmpegProbeResult in = ffprobe.probe("input.flv");

FFmpegBuilder builder = new FFmpegBuilder()
 .setInput(in) // Or filename
 .done()
 .addOutput("output.mp4")
 .done();

FFmpegJob job = executor.createJob(builder, new ProgressListener() {

 // Using the FFmpegProbeResult determine the duration of the input
 final double duration_ns = in.getFormat().duration * TimeUnit.SECONDS.toNanos(1);

 @Override
 public void progress(Progress progress) {
  double percentage = progress.out_time_ns / duration_ns;

  // Print out interesting information about the progress
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

## Building & Contributing

If you wish to make changes, then building is simple with Gradle:

```bash
# To build the project (compiles, tests, and assembles the JAR)
./gradlew build

# To run tests
./gradlew test

# To generate Javadoc
./gradlew javadoc
```

## Install FFmpeg on Ubuntu

We only the support the original FFmpeg, not the libav version. Before Ubuntu 12.04, and in 15.04
and later the original FFmpeg is shipped. If you have to run on a version with libav, you can install
FFmpeg from a PPA, or using the static build. More information [here](http://askubuntu.com/q/373322/34845)

## Get involved

We welcome contributions. Please check the [issue tracker](https://github.com/bramp/ffmpeg-cli-wrapper/issues).
If you see something you wish to work on, please either comment on the issue, or just send a pull
request. Want to work on something else, then just open a issue, and we can discuss! We appreciate
documentation improvements, code cleanup, or new features. Please be mindful that all work is done
on a volunteer basis, thus we can be slow to reply.

## Licence (Simplified BSD License)

```plaintext
Copyright (c) 2013-2024, Andrew Brampton
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, this
  list of conditions and the following disclaimer.

* Redistributions in binary form must reproduce the above copyright notice,
  this list of conditions and the following disclaimer in the documentation
  and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
```
