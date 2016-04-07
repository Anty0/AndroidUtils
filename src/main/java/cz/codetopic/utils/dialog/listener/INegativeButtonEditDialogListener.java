package cz.codetopic.utils.dialog.listener;

import java.io.Serializable;

/**
 * Implement this interface in Activity or Fragment to react to negative dialog buttons.
 *
 * @author anty
 * @author Tomáš Kypta
 * @since 2.1.0
 */
public interface INegativeButtonEditDialogListener {

    void onNegativeButtonClicked(int requestCode, Serializable data, CharSequence result);
}
