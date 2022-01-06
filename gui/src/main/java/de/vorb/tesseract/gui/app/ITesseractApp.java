package de.vorb.tesseract.gui.app;

import java.io.IOException;
import java.nio.file.Path;

import de.vorb.tesseract.gui.model.ApplicationMode;
import de.vorb.tesseract.gui.model.BoxFileModel;
import de.vorb.tesseract.gui.model.ImageModel;
import de.vorb.tesseract.gui.model.PageModel;
import de.vorb.tesseract.gui.model.ProjectModel;
import de.vorb.tesseract.gui.view.MainComponent;
import de.vorb.tesseract.gui.view.PageModelComponent;
import de.vorb.tesseract.gui.view.TesseractFrame;
import de.vorb.tesseract.gui.work.PageRecognitionProducer;
import de.vorb.tesseract.gui.work.ThumbnailWorker;
import de.vorb.tesseract.tools.preprocessing.Preprocessor;

public interface ITesseractApp {

	PageModel getPageModel();

	ProjectModel getProjectModel();

	TesseractFrame getView();

	Path prepareReports() throws IOException;

	Preprocessor getPreprocessor(Path sourceFile);

	boolean hasPreprocessorChanged(Path sourceFile);

	String getTrainingFile();

	void setPageModel(PageModel object);

	void setProjectModel(ProjectModel object);

	void setApplicationMode(ApplicationMode none);

	void closeProject();

	void exit();

	ApplicationMode getApplicationMode();

	BoxFileModel getBoxFileModel();

	int getPageSegmentationMode();

	PageRecognitionProducer getPageRecognitionProducer();

	MainComponent getActiveComponent();

	void setImageModel(ImageModel imageModel);

	void setBoxFileModel(BoxFileModel boxFileModel);

	void setThumbnailLoader(ThumbnailWorker thumbnailLoader);

	boolean handleCloseBoxFile();

}