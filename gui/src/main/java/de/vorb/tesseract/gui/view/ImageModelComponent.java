package de.vorb.tesseract.gui.view;

import de.vorb.tesseract.gui.model.ImageModel;

public interface ImageModelComponent extends MainComponent {
    void setImageModel(ImageModel model);

    ImageModel getImageModel();
}
