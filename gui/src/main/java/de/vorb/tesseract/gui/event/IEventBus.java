package de.vorb.tesseract.gui.event;

public interface IEventBus {

	void unregister(Object obj);

	void register(Object obj);

	void post(Object event);
	
	static IEventBus get() {
		return EventBus.get();
	}
}
