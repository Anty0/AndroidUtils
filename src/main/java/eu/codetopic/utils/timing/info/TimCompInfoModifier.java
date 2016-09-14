package eu.codetopic.utils.timing.info;

import android.content.Context;
import android.support.annotation.MainThread;

@MainThread
public interface TimCompInfoModifier {

    void modify(Context context, TimCompInfoData toModify);
}
