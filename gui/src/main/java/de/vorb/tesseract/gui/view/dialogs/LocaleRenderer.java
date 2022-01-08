package de.vorb.tesseract.gui.view.dialogs;

import java.awt.Component;
import java.util.Locale;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JList;

import de.vorb.tesseract.gui.util.Icons;

public class LocaleRenderer extends DefaultListCellRenderer
{

    private static final long serialVersionUID = 1L;

    @Override
    public Component getListCellRendererComponent(JList<? extends Object> list, Object value, int index,
            boolean isSelected, boolean cellHasFocus) {

        var locale = (Locale) value;
        var component = super.getListCellRendererComponent(list, locale.getDisplayLanguage(locale), index, isSelected,
                cellHasFocus);
        var icon = getIcon(locale);
        setIcon(icon);
        return component;
    }

    private Icon getIcon(Locale locale) {
        var iconname = locale.getLanguage() + 25;
        return Icons.get().getIcon(iconname);
    }

}
