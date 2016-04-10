package eu.codetopic.utils.dialog.listener;

import java.io.Serializable;

/**
 * Implement this interface in Activity or Fragment to react to positive dialog buttons.
 *
 * @author anty
 * @author Tomáš Kypta
 * @since 2.1.0
 */
public interface IPositiveButtonEditDialogListener {

    void onPositiveButtonClicked(int requestCode, Serializable data, CharSequence result);
}
