package de.vorb.tesseract.gui.app;

import java.io.IOException;
import java.nio.file.Path;

import de.vorb.tesseract.gui.model.ApplicationMode;
import de.vorb.tesseract.gui.model.BoxFileModel;
import de.vorb.tesseract.gui.model.ImageModel;
import de.vorb.tesseract.gui.model.PageModel;
import de.vorb.tesseract.gui.model.ProjectModel;
import de.vorb.tesseract.gui.view.MainComponent;
import de.vorb.tesseract.gui.view.TesseractFrame;
import de.vorb.tesseract.gui.work.PageRecognitionProducer;
import de.vorb.tesseract.gui.work.ThumbnailWorker;
import de.vorb.tesseract.tools.preprocessing.Preprocessor;

public class AppProxy implements ITesseractApp {
	
	private static AppProxy instance = new AppProxy();

	public static ITesseractApp getApp() {
		return instance;
	}

	@Override
	public PageModel getPageModel() {
		return TesseractApp.get().getPageModel();
	}

	@Override
	public ProjectModel getProjectModel() {
		return TesseractApp.get().getProjectModel();
	}

	@Override
	public TesseractFrame getView() {
		return TesseractApp.get().getView();
	}

	@Override
	public Path prepareReports() throws IOException {
		return TesseractApp.get().prepareReports();
	}

	@Override
	public Preprocessor getPreprocessor(Path sourceFile) {
		return TesseractApp.get().getPreprocessor(sourceFile);
	}

	@Override
	public boolean hasPreprocessorChanged(Path sourceFile) {
		return TesseractApp.get().hasPreprocessorChanged(sourceFile);
	}

	@Override
	public String getTrainingFile() {
		return TesseractApp.get().getTrainingFile();
	}

	@Override
	public void setPageModel(PageModel model) {
		TesseractApp.get().setPageModel(model);
		
	}

	@Override
	public void setProjectModel(ProjectModel model) {
		TesseractApp.get().setProjectModel(model);
	}

	@Override
	public void setApplicationMode(ApplicationMode mode) {
		TesseractApp.get().setApplicationMode(mode);
	}

	@Override
	public void closeProject() {
		TesseractApp.get().closeProject();
	}

	@Override
	public void exit() {
		TesseractApp.get().exit();
	}

	@Override
	public ApplicationMode getApplicationMode() {
		return TesseractApp.get().getApplicationMode();
	}

	@Override
	public BoxFileModel getBoxFileModel() {
		return TesseractApp.get().getBoxFileModel();
	}

	@Override
	public int getPageSegmentationMode() {
		return TesseractApp.get().getPageSegmentationMode();
	}

	@Override
	public PageRecognitionProducer getPageRecognitionProducer() {
		return TesseractApp.get().getPageRecognitionProducer();
	}

	@Override
	public MainComponent getActiveComponent() {
		return TesseractApp.get().getActiveComponent();
	}

	@Override
	public void setImageModel(ImageModel imageModel) {
		TesseractApp.get().setImageModel(imageModel);
	}

	@Override
	public void setBoxFileModel(BoxFileModel boxFileModel) {
		TesseractApp.get().setBoxFileModel(boxFileModel);
	}

	@Override
	public void setThumbnailLoader(ThumbnailWorker thumbnailLoader) {
		TesseractApp.get().setThumbnailLoader(thumbnailLoader);
	}

	@Override
	public boolean handleCloseBoxFile() {
		return TesseractApp.get().handleCloseBoxFile();
	}
}
