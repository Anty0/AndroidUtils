package eu.codetopic.utils.timing.info;

import android.content.Context;

import java.io.Serializable;

public interface TimCompInfoModifier {

    void modify(Context context, TimCompInfoData toModify);
}
