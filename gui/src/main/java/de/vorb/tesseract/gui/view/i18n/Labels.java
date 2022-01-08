package de.vorb.tesseract.gui.view.i18n;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import de.vorb.tesseract.gui.model.PreferencesUtil;
import de.vorb.tesseract.gui.view.dialogs.PreferencesDialog;

public final class Labels {

    private Labels() {}

    public static String getLabel(String key) {
    	String language = PreferencesUtil.getPreferences().get(PreferencesDialog.KEY_LANGUAGE, Locale.ENGLISH.getLanguage());
    	Locale locale = Locale.forLanguageTag(language);
        final ResourceBundle labels = ResourceBundle.getBundle("l10n/labels",
                locale);

        try {
            return labels.getString(key);
        } catch (MissingResourceException e) {
            return "?";
        }
    }
}
