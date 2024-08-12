# Android Utils

> [!IMPORTANT]
> Code in this repository is not actively maintained, but feel free to create issues or pull requests if you find something that needs fixing.
> The repository stopped being maintained during times of Android 8, so ... you can imagine what that means.

Powerful collection of Android utilities that I have written during my high school years when I was mostly programming in Java and Kotlin for Android.

Repository is organized as a single library you can include in a project, where each namespace contains all the utilities that are related to a specific topic.

Most notable feature is the `NotificationManager` which allows you to have persistent notifications with their own lifecycles. Only downside is to avoid clogging up the main thread it uses a separate process to handle all notifications - your app will have to support that.

## Usage

> [!NOTE]
> Project is currently not published to any Maven repository. Tell me if you want me to publish it (or maybe split part of it into a separate library and publish that).

> [!NOTE]
> Including this library in your project will also include the [JavaUtils](https://github.com/Anty0/JavaUtils) library.

To include this library in your project, add the following to your `build.gradle`:

```gradle
dependencies {
    implementation project(path: ':utils')
    implementation project(path: ':utils:javautils')
}
```

Then add the repository as a submodule:

```bash
git submodule add git@github.com:Anty0/AndroidUtils.git utils
git submodule update --init --recursive
```

## Features

- `eu.codetopic.utils`
  - `broadcast`
    - `BroadcastsConnector` - Trigger intent when a certain action is broadcasted
    - `OnceReceiver` - Receiver that will only receive one broadcast and then unregister itself
    - `SkipSupportBroadcastReceiver` - Receiver that will skip certain number of broadcasts before actually receiving one
  - `bundle`
    - `BundleBuilder` - Fluent builder for creating bundles
    - `BundleListSerializer`, `BundleSerializer` and `SerializableBundleWrapper` - Serialize and deserialize values into Bundle that normally cannot be added to Bundle ; Uses kotlin serialization
  - `cursor` - Cursor for use with Arrays ; Allows you to iterate over an array as if it was a database cursor
  - `data.getter` - Interface for providing data alongside with an action one can use to listen for changes using BroadcastReceiver
  - `data.preferences` - Collection of SharedPreferences wrappers and extensions ; Most notably there is a implementation of versioned SharedPreferences that allows you to have upgrade path for SharedPreferences ; Also there is some broadcast magic that allows you to listen for changes in SharedPreferences using BroadcastReceiver
  - `debug` - Continuation of the `eu.codetopic.java.utils.debug` package from JavaUtils with some Android specific tools
    - `AndroidDebugModeExtension` - Sets default debug mode on or off based on the build type
    - `BaseDebugActivity` - Activity that can be used to see various debug information ; Extend this activity to use it and override `prepareDebugItems` to add your own debug items
  - `export` - Export data to file in background with progress reporting
  - `ids` - Generate sequential ids with persistent storage ; Useful for generating unique ids for notifications
  - `log` - You know how I created my own logger in JavaUtils? Well, this is continuation of that with Android specific tools and it is crazy. There is a whole UI element for viewing detected errors and notifications to remind you that something went wrong.
    - `AndroidLoggerExtension` - Adds Android log as one of the log listeners - so all logs will be printed to Android log as well ; Enables issue detection
    - `ui` - UI to view all detected issues
    - `notify` - Show notification when an issue is detected
  - `network` - Simplified checking if network is available
  - `notifications.manager` - Whole framework for managing notifications with their own lifecycles and full persistence ; The framework is built with a single thing in mind - you don't need working notifications to use the app, so these is no reason to crash the app if something goes wrong instead an error is logged, which is picked up by the `log` package ; There is a known issue with some manufacturers when they play with battery optimizations so much, that they inadvertently break multiprocess communication (refusing to start the second process for example) which will break the notifications, but the app should still work nonetheless
  - `simple` - Some abstract classes and interfaces require you to implement more methods than you actually need. In this package you will find some "simple" implementations (meaning they do nothing) of those classes and interfaces so you can extend them and override only the methods you need
  - `thread` - Utilities for running pieces of code on main thread, plus some tools for progress reporting and showing notifications with progress
  - `ui` - A lot of utilities for working with UI
    - `activity.fragment` - Implementation of Activity for managing showing full screen fragments
    - `activity.modular` - ModularActivity implementation, plus implementation of some modules ; This is a special activity that can be extended using modules that can add their own logic to be executed during different lifecycle events of the activity. This can be used to do almost anything with one advantage - you can mix and match modules based on your needs not being limited to a single inheritance chain.
    - `activity.navigation` - NavigationActivity that can be used to show between different fragments
    - `animation` - Contains tool for animating view visibility change
    - `container` - When you want to show a list of items to the user
      - `adapter` - Different Adapters for various use cases including autoloading, etc. ; All with support for animating changes
      - `items` - Base class and some implementations for items that can be shown in a ui container
      - `list` - Show list of items in a ListView
      - `recycler` - Show list of items in a RecyclerView
      - `swipe` - Swipe to refresh support for containers
    - `view` - Kotlin extensions ; Random functions ; ViewHolder implementation (WeakReference way of passing view references to background tasks), plus a module for modular Activity
  - `AndroidUtils` - Random functions that don't fit anywhere else
  - `Constants` - Constants used in the library ; Can be used to override some resources used by the library
  - `extensions` - Kotlin extensions for Android classes
  - `PrefNames` - Constants for SharedPreferences keys used in some SharedPreferences wrappers/extensions
  - `UtilsBase` - Since there are multiple components that need to be initialized in a specific order to work properly, this class is can be used to initialize all of them at once using a simple `UtilsBase.prepare(this) { addParams(/* set specific params for each process your app uses (this will be done automatically for all processes in use by the library) */ processNamePrimary to bundleOf(PARAM_INITIALIZE_UTILS to true)) }` and `UtilsBase.initialize(this) { processName, _ -> /* your initialization code based on current process */ }`. While using `UtilsBase` is not required, you may need to checkout its implementation to see how to properly initialize the library components you plan on using.
