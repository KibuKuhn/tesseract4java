package de.vorb.tesseract.gui.util;

import java.awt.Image;

import javax.swing.Icon;

public interface Icons
{

    Image getImage(String iconName);

    Icon getIcon(String iconName);

    void clearCache();
    
    static Icons get() {
    	return IconsFactory.get();
    }
}
