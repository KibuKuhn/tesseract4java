package de.vorb.tesseract.gui.util;

public interface FilterProvider<T> {
    Filter<T> getFilterFor(String filterText);
}
