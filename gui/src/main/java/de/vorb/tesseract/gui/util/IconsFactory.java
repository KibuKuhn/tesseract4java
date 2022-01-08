package de.vorb.tesseract.gui.util;

import java.awt.Image;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;

class IconsFactory implements Icons
{
	private static IconsFactory instance = new IconsFactory();
	
    private Map<String, ImageIcon> icons = new HashMap<>();

    @Override
    public Image getImage(String iconName) {
        return getImageIcon(iconName).getImage();
    }

    @Override
    public Icon getIcon(String iconName) {
        return getImageIcon(iconName);
    }

    @Override
    public void clearCache() {
        icons = new HashMap<>();
    }

    private ImageIcon getImageIcon(final String iconName) {
        var imageIcon = icons.get(iconName);
        if (imageIcon == null) {           
            var resource = getClass().getResource("/icons/" + iconName + ".png");
            imageIcon = new ImageIcon(resource, iconName);
            icons.put(iconName, imageIcon);
        }
        return imageIcon;
    }

	static Icons get() {
		return instance;
	}
}
