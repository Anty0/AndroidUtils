package cz.codetopic.utils.dialog.listener;

import java.io.Serializable;

/**
 * Implement this interface in Activity or Fragment to react to neutral dialog buttons.
 *
 * @author anty
 * @author Tomáš Kypta
 * @since 2.1.0
 */
public interface INeutralButtonDataDialogListener {

    void onNeutralButtonClicked(int requestCode, Serializable data);
}
