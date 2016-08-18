# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/anty/Android/Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Preserve annotations, line numbers, and source file names
-keepattributes *Annotation*,SourceFile,LineNumberTable

# keep Serializables
-keepnames class * implements java.io.Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# remove Logs
-assumenosideeffects class eu.codetopic.utils.log.Log {
    public static *** v(...);
    public static *** d(...);
    public static *** i(...);
    public static *** w(...);
}

-keep class * implements eu.codetopic.utils.timing.info.TimCompInfoModifier
-keepclassmembers class * implements eu.codetopic.utils.timing.info.TimCompInfoModifier {
    public <init>();
    public void modify(eu.codetopic.utils.timing.info.TimCompInfoData);
}