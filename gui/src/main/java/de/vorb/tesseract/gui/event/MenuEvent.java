package de.vorb.tesseract.gui.event;

import java.util.EventObject;

public class MenuEvent extends EventObject {

	private static final long serialVersionUID = 1L;
	private MenuEvents type;

	public MenuEvent(MenuEvents type, Object source) {
		super(source);
	    this.type = type;
	}
	
	public MenuEvents getType() {
		return type;
	}
}
