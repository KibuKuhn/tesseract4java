package de.vorb.tesseract.gui.event;

class EventBus implements IEventBus {

	private static IEventBus instance = new EventBus();

	static IEventBus get() {
		return instance;
	}

	private com.google.common.eventbus.EventBus eventBus;

	private EventBus() {
		eventBus = new com.google.common.eventbus.EventBus();
	};

	@Override
	public void post(Object event) {
		eventBus.post(event);
	}

	@Override
	public void register(Object obj) {
		eventBus.register(obj);
	}

	@Override
	public void unregister(Object obj) {
		eventBus.unregister(obj);
	}

}
