package de.vorb.tesseract.gui.view.renderer;

import de.vorb.tesseract.gui.model.PageModel;

/**
 * Page renderer.
 *
 * @author Paul Vorbach
 */
public interface PageRenderer {

    /**
     * Renders the information of a page on an optionally given background.
     *
     * @param pageModel page model to render
     * @param scale     scaling factor
     */
    void render(PageModel pageModel, final float scale);
}
