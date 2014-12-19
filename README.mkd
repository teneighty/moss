Moss
====

Moss is a [Conky][conky] like live wallpaper for android. It provides system statistics
such as uptime, cpu usage, network usage, top processes, and battery level.
It is highly configurable. Configuration tries to closely follow [Conky's][conky] syntax. 

For example configurations please see [the samples on the Moss site][samples].

# Build from Command Line

Be sure you have jdk1.7, android-ndk-r10 and android-sdk SDK 21 and 21.0.+ build tools.

    # setup your ndk patch
    echo ndk.dir=${ANDROID_NDK} > local.properties

    # Sorry about the submodules
    git submodule init && git submodule update

    # build and install
    ./gradlew installDebug

[conky]: http://www.conky.com
[samples]: http://teneighty.github.com/moss/samples.html
