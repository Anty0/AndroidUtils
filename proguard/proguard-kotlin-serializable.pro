-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.SerializationKt
-keep,includedescriptorclasses class eu.codetopic.**$$serializer { *; }
-keepclassmembers class eu.codetopic.** {
    *** Companion;
}
-keepclasseswithmembers class eu.codetopic.** {
    kotlinx.serialization.KSerializer serializer(...);
}