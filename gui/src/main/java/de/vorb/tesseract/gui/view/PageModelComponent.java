package de.vorb.tesseract.gui.view;

import de.vorb.tesseract.gui.model.BoxFileModel;
import de.vorb.tesseract.gui.model.PageModel;

public interface PageModelComponent extends MainComponent {
    
	void setPageModel(PageModel model);

    PageModel getPageModel();

    BoxFileModel getBoxFileModel();
}
