package de.vorb.tesseract.gui.event;

import java.util.EventObject;

import de.vorb.tesseract.gui.model.Scale;

public class ScaleEvent extends EventObject {

	private static final long serialVersionUID = 1L;

	public ScaleEvent(Scale source) {
		super(source);
	}
	
	
	@Override
	public Scale getSource() {
		return (Scale) super.getSource();
	}
}
