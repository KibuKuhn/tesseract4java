package de.vorb.tesseract.gui.app;

import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.prefs.Preferences;
import java.util.stream.StreamSupport;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.ProgressMonitor;
import javax.swing.filechooser.FileFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;

import de.vorb.tesseract.gui.event.IEventBus;
import de.vorb.tesseract.gui.event.MenuEvent;
import de.vorb.tesseract.gui.io.BoxFileReader;
import de.vorb.tesseract.gui.io.BoxFileWriter;
import de.vorb.tesseract.gui.model.ApplicationMode;
import de.vorb.tesseract.gui.model.BatchExportModel;
import de.vorb.tesseract.gui.model.BoxFileModel;
import de.vorb.tesseract.gui.model.PageModel;
import de.vorb.tesseract.gui.model.PageThumbnail;
import de.vorb.tesseract.gui.model.PreferencesUtil;
import de.vorb.tesseract.gui.model.ProjectModel;
import de.vorb.tesseract.gui.view.PageModelComponent;
import de.vorb.tesseract.gui.view.dialogs.BatchExportDialog;
import de.vorb.tesseract.gui.view.dialogs.CharacterHistogram;
import de.vorb.tesseract.gui.view.dialogs.Dialogs;
import de.vorb.tesseract.gui.view.dialogs.ImportTranscriptionDialog;
import de.vorb.tesseract.gui.view.dialogs.NewProjectDialog;
import de.vorb.tesseract.gui.view.dialogs.PreferencesDialog;
import de.vorb.tesseract.gui.view.dialogs.PreferencesDialog.ResultState;
import de.vorb.tesseract.gui.view.dialogs.UnicharsetDebugger;
import de.vorb.tesseract.gui.work.BatchExecutor;
import de.vorb.tesseract.gui.work.PageListWorker;
import de.vorb.tesseract.gui.work.ThumbnailWorker;
import de.vorb.tesseract.tools.training.Unicharset;
import de.vorb.tesseract.util.Symbol;
import de.vorb.util.FileNames;

class MenuHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(MenuHandler.class);
	
	static final String KEY_BOX_FILE = "box_file";

	private ITesseractApp app;

	MenuHandler() {
		IEventBus.get().register(this);
		app = AppProxy.getApp();
	}

	@Subscribe
	public void handleMenuAction(MenuEvent event) {
		switch (event.getType()) {
		case Batch:
			handleBatchExport();
			break;
		case CharacterHistogram:
			handleCharacterHistogram();
			break;
		case Close:
			handleCloseProject();
			break;
		case Exit:
			handleExit();
			break;
		case ImportTranscriptions:
			handleImportTranscriptions();
			break;
		case InspectUnicharset:
			handleInspectUnicharset();
			break;
		case NewProject:
			handleNewProject();
			break;
		case OpenBoxFile:
			handleOpenBoxFile();
			break;
		case OpenProjectDirectory:
			handleOpenProjectDirectory();
			break;
		case Preferences:
			handlePreferences();
			break;
		case SaveBoxFile:
			handleSaveBoxFile();
			break;
		case TesseractTrainer:
			handleTesseractTrainer();
			break;
		default:
			throw new IllegalArgumentException("Unknown menu event type: " + event.getType());
		}
	}

	private void handleBatchExport() {
		BatchExportModel export = BatchExportDialog.showBatchExportDialog();
		if (Objects.nonNull(export)) {
			BatchExecutor batchExec = new BatchExecutor(export);

			try {
				int totalFiles = (int) StreamSupport.stream(app.getProjectModel().getImageFiles().spliterator(), false)
						.count();
				ProgressMonitor progressMonitor = new ProgressMonitor(app.getView(), "Processing:", "", 0,
						totalFiles + 1);
				progressMonitor.setProgress(0);

				try (BufferedWriter errorLog = Files.newBufferedWriter(export.getDestinationDir().resolve("errors.log"),
						StandardCharsets.UTF_8)) {

					batchExec.start(progressMonitor, errorLog);
				}
			} catch (IOException | InterruptedException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
	}

	private void handleCharacterHistogram() {		
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setFileFilter(new CharacterHistogramFileFilter());

		if (fc.showOpenDialog(app.getView()) != JFileChooser.APPROVE_OPTION) {
			return;
		}

		Path textFile = fc.getSelectedFile().toPath();

		try (BufferedReader reader = Files.newBufferedReader(textFile, StandardCharsets.UTF_8)) {

			final Map<Character, Integer> histogram = new TreeMap<>();

			// build up a histogram
			int c;
			while ((c = reader.read()) != -1) {
				final char character = (char) c;

				Integer val = histogram.get(character);

				if (val == null) {
					val = 0;
				}

				histogram.put(character, val + 1);
			}

			final CharacterHistogram ch = new CharacterHistogram(histogram);
			ch.setLocationRelativeTo(app.getView());
			ch.setVisible(true);

		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
			Dialogs.showError(app.getView(), "Invalid text file", "Could not read the text file.");
		}

	}

	boolean handleCloseProject() {		
		final boolean really = Dialogs.ask(app.getView(), "Confirmation", "Do you really want to close this project?");
		if (really) {
			app.closeProject();
		}

		return really;
	}

	private void handleExit() {
		app.exit();
	}

	private void handleImportTranscriptions() {
		
		final ImportTranscriptionDialog importDialog = new ImportTranscriptionDialog();
		importDialog.setVisible(true);

		ProjectModel projectModel = app.getProjectModel();
		if (!(importDialog.isApproved() && Objects.nonNull(projectModel))) {
			return;
		}
		Path file = importDialog.getTranscriptionFile();
		final String sep = importDialog.getPageSeparator();

		try {
			Files.createDirectories(projectModel.getTranscriptionDir());
			app.getView().getProgressBar().setIndeterminate(true);

			try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
				StreamSupport.stream(projectModel.getImageFiles().spliterator(), false)
						.forEach(imgFile -> processImageFile(projectModel, sep, reader, imgFile));
				Dialogs.showInfo(app.getView(), "Import Transcriptions", "Transcription file successfully imported.");
			}
		} catch (IOException | IllegalStateException e) {
			LOGGER.error(e.getMessage(), e);
			Dialogs.showError(app.getView(), "Import Exception", "Could not import the transcription file.");
		} finally {
			app.getView().getProgressBar().setIndeterminate(false);
		}

	}

	private void processImageFile(ProjectModel projectModel, String sep, BufferedReader reader, Path imgFile) {
		Path filename = FileNames.replaceExtension(imgFile, "txt").getFileName();
		Path transcription = projectModel.getTranscriptionDir().resolve(filename);
		try (BufferedWriter writer = Files.newBufferedWriter(transcription, StandardCharsets.UTF_8)) {
			int lines = 0;
			String line;
			while ((line = reader.readLine()) != null) {
				// if the line equals the separator, create the next
				// file
				if (line.equals(sep)) {
					break;
				}

				lines++;
				// otherwise write the line to the current file
				writer.write(line);
				writer.write('\n');
			}

			// if a transcription file is empty, delete it
			if (lines == 0) {
				Files.delete(transcription);
			}

			writer.write('\n');
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	private void handleInspectUnicharset() {
		
		final JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setFileFilter(new InspectUnicharsetFileFilter());

		if (fc.showOpenDialog(app.getView()) != JFileChooser.APPROVE_OPTION) {
			return;
		}

		final Path unicharsetFile = fc.getSelectedFile().toPath();
		try (BufferedReader unicharsetReader = Files.newBufferedReader(unicharsetFile, StandardCharsets.UTF_8)) {

			Unicharset unicharset = Unicharset.readFrom(unicharsetReader);

			// show the unicharset dialog
			final UnicharsetDebugger uniDebugger = new UnicharsetDebugger(unicharset);
			uniDebugger.setLocationRelativeTo(app.getView());
			uniDebugger.setVisible(true);
		} catch (IOException e) {
			Dialogs.showError(app.getView(), "Invalid Unicharset",
					"Could not read the unicharset file. It may have an incompatible version.");
		}
	}

	private void handleNewProject() {		
		if (app.getApplicationMode() == ApplicationMode.BOX_FILE && !app.handleCloseBoxFile()) {
			return;
		} else if (app.getApplicationMode() == ApplicationMode.PROJECT && !handleCloseProject()) {
			return;
		}

		ProjectModel projectModel = NewProjectDialog.showDialog(app.getView());
		if (Objects.isNull(projectModel)) {
			return;
		}

		app.setProjectModel(projectModel);
		DefaultListModel<PageThumbnail> pages = app.getView().getPages().getListModel();
		ThumbnailWorker thumbnailLoader = new ThumbnailWorker(projectModel, pages);
		thumbnailLoader.execute();
		app.setThumbnailLoader(thumbnailLoader);

		final PageListWorker pageListLoader = new PageListWorker(projectModel, pages);

		pageListLoader.execute();
		app.setApplicationMode(ApplicationMode.PROJECT);
	}

	private void handleOpenBoxFile() {
		
		if (app.getApplicationMode() == ApplicationMode.BOX_FILE && !app.handleCloseBoxFile()) {
			return;
		} else if (app.getApplicationMode() == ApplicationMode.PROJECT && !handleCloseProject()) {
			return;
		}

		JFileChooser fc = new JFileChooser();
		String lastBoxFile = PreferencesUtil.getPreferences().get(KEY_BOX_FILE, null);
		if (lastBoxFile == null) {
			Path dir = Paths.get(lastBoxFile).getParent();
			if (Files.isDirectory(dir)) {
				fc.setCurrentDirectory(dir.toFile());
			}
		}

		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setFileFilter(new OpenBoxFileFilter());
		if (fc.showOpenDialog(app.getView()) == JFileChooser.APPROVE_OPTION) {
			final Path imageFile = fc.getSelectedFile().toPath();

			try {
				Path boxFile = FileNames.replaceExtension(imageFile, "box");
				BufferedImage image = ImageIO.read(imageFile.toFile());
				List<Symbol> boxes = BoxFileReader.readBoxFile(boxFile, image.getHeight());
				app.setApplicationMode(ApplicationMode.BOX_FILE);
				app.getView().getScale().setTo100Percent();
				PreferencesUtil.getPreferences().put(KEY_BOX_FILE, boxFile.toAbsolutePath().toString());
				app.setBoxFileModel(new BoxFileModel(boxFile, image, boxes));
			} catch (IOException | IndexOutOfBoundsException e) {
				LOGGER.error(e.getMessage(), e);
				Dialogs.showError(app.getView(), "Error", "Box file could not be opened.");
			}
		}
	}

	private void handleOpenProjectDirectory() {
		
		if (Desktop.isDesktopSupported()) {
			try {
				Desktop.getDesktop().browse(app.getProjectModel().getProjectDir().toUri());
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
				Dialogs.showError(app.getView(), "Exception", "Project directory could not be opened.");
			}
		}
		else {
			LOGGER.error("Desktop not supported");
		}
	}

	private void handlePreferences() {
		
		final PreferencesDialog prefDialog = new PreferencesDialog();
		final ResultState result = prefDialog.showPreferencesDialog(app.getView());
		if (result == ResultState.APPROVE) {
			final Preferences globalPrefs = PreferencesUtil.getPreferences();
			try {
				final Path langdataDir = Paths.get(prefDialog.getTfLangdataDir().getText());
				if (Files.isDirectory(langdataDir)) {
					globalPrefs.put(PreferencesDialog.KEY_LANGDATA_DIR, langdataDir.toString());
				}

				final String renderingFont = (String) prefDialog.getComboRenderingFont().getSelectedItem();
				globalPrefs.put(PreferencesDialog.KEY_RENDERING_FONT, renderingFont);

				final String editorFont = (String) prefDialog.getComboEditorFont().getSelectedItem();
				globalPrefs.put(PreferencesDialog.KEY_EDITOR_FONT, editorFont);

				// Update the page segmentation mode if necessary
				int currentPageSegMode = app.getPageSegmentationMode();
				int pageSegMode = prefDialog.getPageSegmentationMode();
				boolean hasPageSegModeChanged = currentPageSegMode != pageSegMode;
				if (hasPageSegModeChanged) {
					globalPrefs.putInt(PreferencesDialog.KEY_PAGE_SEG_MODE, pageSegMode);
					app.getPageRecognitionProducer().setPageSegmentationMode(pageSegMode);

					// Update model with new segmentation mode
					if (app.getActiveComponent() instanceof PageModelComponent) {
						final PageModel pageModel = ((PageModelComponent) app.getActiveComponent()).getPageModel();
						if (Objects.nonNull(pageModel)) {
							app.setImageModel(pageModel.getImageModel());
						}
					}
				}

				app.getView().getRecognitionPane().setRenderingFont(renderingFont);
				if (app.getView().getActiveComponent() == app.getView().getRecognitionPane()) {
					app.getView().getRecognitionPane().render();
				}

				app.getView().getEvaluationPane().setEditorFont(editorFont);
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
				Dialogs.showWarning(app.getView(), "Error", "Could not save the preferences.");
			}
		}
	}

	private void handleSaveBoxFile() {
		
		final BoxFileModel boxFileModel = app.getBoxFileModel();

		if (Objects.nonNull(boxFileModel)) {
			try {
				BoxFileWriter.writeBoxFile(boxFileModel);
				Dialogs.showInfo(app.getView(), "Saved", "The box file has been saved.");
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
				Dialogs.showError(app.getView(), "Error", "Box file could not be written.");
			}
		} else {
			Dialogs.showWarning(app.getView(), "Warning", "No box file present.");
		}
	}

	private void handleTesseractTrainer() {		
		final TesseractTrainer trainer = new TesseractTrainer();
		trainer.setLocationRelativeTo(app.getView());
		trainer.setVisible(true);
	}

	private static class OpenBoxFileFilter extends FileFilter {
		@Override
		public String getDescription() {
			return "Image files";
		}

		@Override
		public boolean accept(File f) {
			final String fname = f.getName();
			return f.canRead()
					&& (f.isDirectory() || f.isFile() && (fname.endsWith(".png") || fname.endsWith(".tif")
							|| fname.endsWith(".tiff") || fname.endsWith(".jpg") || fname.endsWith(".jpeg")));
		}
	}

	private static class InspectUnicharsetFileFilter extends FileFilter {
		@Override
		public String getDescription() {
			return "Unicharset files";
		}

		@Override
		public boolean accept(File f) {
			return f.isDirectory() || f.getName().endsWith("unicharset");
		}
	}

	private static class CharacterHistogramFileFilter extends FileFilter {
		@Override
		public String getDescription() {
			return "Text files";
		}

		@Override
		public boolean accept(File f) {
			return f.canRead();
		}
	}
}
