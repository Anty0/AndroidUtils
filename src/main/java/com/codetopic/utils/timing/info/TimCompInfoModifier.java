package com.codetopic.utils.timing.info;

import java.io.Serializable;

public interface TimCompInfoModifier extends Serializable {

    void modify(TimCompInfoData toModify);
}
