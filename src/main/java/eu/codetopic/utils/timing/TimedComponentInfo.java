package eu.codetopic.utils.timing;

import android.content.ComponentName;
import android.content.Context;

import java.io.Serializable;

import eu.codetopic.utils.Utils;
import eu.codetopic.utils.exceptions.NoAnnotationPresentException;

public class TimedComponentInfo implements Serializable {

    private final Class<?> mComponent;
    private final TimedComponent mComponentInfo;

    TimedComponentInfo(Class<?> componentClass) {
        mComponent = componentClass;
        mComponentInfo = componentClass.getAnnotation(TimedComponent.class);
        if (mComponentInfo == null)
            throw new NoAnnotationPresentException("TimedComponent annotation is not present in "
                    + componentClass.getName());
    }

    public Class<?> getComponentClass() {
        return mComponent;
    }

    public ComponentName getComponentName(Context context) {
        return new ComponentName(context, mComponent);
    }

    public boolean isEnabled(Context context) {
        return Utils.isComponentEnabled(context, mComponent);
    }

    public TimedComponent getComponentInfo() {
        return mComponentInfo;
    }
}
