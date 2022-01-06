package de.vorb.tesseract.gui.app;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JViewport;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.xml.transform.TransformerException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.vorb.tesseract.gui.event.ScaleEvent;
import de.vorb.tesseract.gui.event.ScaleListener;
import de.vorb.tesseract.gui.io.PlainTextWriter;
import de.vorb.tesseract.gui.model.ApplicationMode;
import de.vorb.tesseract.gui.model.BoxFileModel;
import de.vorb.tesseract.gui.model.FilteredListModel;
import de.vorb.tesseract.gui.model.ImageModel;
import de.vorb.tesseract.gui.model.PageModel;
import de.vorb.tesseract.gui.model.PageThumbnail;
import de.vorb.tesseract.gui.model.PreferencesUtil;
import de.vorb.tesseract.gui.model.ProjectModel;
import de.vorb.tesseract.gui.model.SymbolListModel;
import de.vorb.tesseract.gui.model.SymbolOrder;
import de.vorb.tesseract.gui.util.DocumentWriter;
import de.vorb.tesseract.gui.view.BoxFileModelComponent;
import de.vorb.tesseract.gui.view.EvaluationPane;
import de.vorb.tesseract.gui.view.FeatureDebugger;
import de.vorb.tesseract.gui.view.FilteredTable;
import de.vorb.tesseract.gui.view.ImageModelComponent;
import de.vorb.tesseract.gui.view.MainComponent;
import de.vorb.tesseract.gui.view.PageModelComponent;
import de.vorb.tesseract.gui.view.PreprocessingPane;
import de.vorb.tesseract.gui.view.SymbolOverview;
import de.vorb.tesseract.gui.view.TesseractFrame;
import de.vorb.tesseract.gui.view.dialogs.Dialogs;
import de.vorb.tesseract.gui.view.dialogs.PreferencesDialog;
import de.vorb.tesseract.gui.work.PageRecognitionProducer;
import de.vorb.tesseract.gui.work.PreprocessingWorker;
import de.vorb.tesseract.gui.work.RecognitionWorker;
import de.vorb.tesseract.gui.work.ThumbnailWorker;
import de.vorb.tesseract.gui.work.ThumbnailWorker.Task;
import de.vorb.tesseract.tools.preprocessing.DefaultPreprocessor;
import de.vorb.tesseract.tools.preprocessing.Preprocessor;
import de.vorb.tesseract.tools.recognition.RecognitionProducer;
import de.vorb.tesseract.util.Box;
import de.vorb.tesseract.util.Symbol;
import de.vorb.tesseract.util.TraineddataFiles;
import de.vorb.tesseract.util.feat.Feature3D;
import de.vorb.util.FileNames;
import eu.digitisation.input.Batch;
import eu.digitisation.input.Parameters;
import eu.digitisation.input.WarningException;
import eu.digitisation.output.Report;

public class TesseractApp extends WindowAdapter
		implements ListSelectionListener, ChangeListener, ScaleListener, ActionListener, ITesseractApp {
	
	static {
		initLogging();
	}
	private static final Logger LOGGER = LoggerFactory.getLogger(TesseractApp.class);
	private static TesseractApp tesseractApp;
	
	public static TesseractApp get() {
		return tesseractApp;
	}

	public static void main(String[] args) {		
		setLookAndFeel();

		try {
			tesseractApp = new TesseractApp();
		} catch (Throwable e) {
			Dialogs.showError(null, "Fatal error",
					String.format("The necessary libraries could not be loaded: '%s'", e.getMessage()));

			throw e;
		}
	}

	private static void initLogging() {
		System.setProperty("logDir", System.getProperty("user.dir"));
		System.setProperty("logFile", "tesseract4java.log");
		System.setProperty("logLevel", "INFO");		
	}

	private static void setLookAndFeel() {
		try {
			String laf = System.getProperty("laf", UIManager.getSystemLookAndFeelClassName());
			UIManager.setLookAndFeel(laf);
		} catch (Exception e1) {
			// fail silently
			try {
				UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			} catch (Exception e2) {
				// fail silently
			}

			// If the system LaF is not available, use whatever LaF is already
			// being used.
		}
	}

	// constants
	private static final String KEY_TRAINING_FILE = "training_file";

	public static final Preprocessor DEFAULT_PREPROCESSOR = new DefaultPreprocessor();

	// components references
	private final TesseractFrame view;

	private ApplicationMode mode = ApplicationMode.NONE;

	private final FeatureDebugger featureDebugger;
	private MainComponent activeComponent;

	private final PageRecognitionProducer pageRecognitionProducer;
	private PreprocessingWorker preprocessingWorker;

	// IO workers, timers and tasks
	private ThumbnailWorker thumbnailLoader;
	private final Timer pageSelectionTimer = new Timer("PageSelectionTimer");

	private TimerTask lastPageSelectionTask;
	private final Timer thumbnailLoadTimer = new Timer("ThumbnailLoadTimer");

	private TimerTask lastThumbnailLoadTask;

	private final List<Task> tasks = new LinkedList<>();

	// models
	private ProjectModel projectModel;
	private PageThumbnail pageThumbnail;

	private String lastTraineddataFile;

	// preprocessing
	private Preprocessor defaultPreprocessor = new DefaultPreprocessor();
	private final Map<Path, Preprocessor> preprocessors = new HashMap<>();

	private Set<Path> changedPreprocessors = new HashSet<>();

	private RecognitionWorker recognitionWorker;
	private MenuHandler menuHandler;

	public TesseractApp() {
		view = new TesseractFrame();
		featureDebugger = new FeatureDebugger(view);

		setApplicationMode(ApplicationMode.NONE);

		handleActiveComponentChange();

		final Path tessdataDir = TraineddataFiles.getTessdataDir();
		if (!Files.isReadable(tessdataDir)) {
			Dialogs.showError(null, "Fatal Error",
					String.format("The tessdata directory could not be read: '%s'", tessdataDir.toAbsolutePath()));
		}

		pageRecognitionProducer = new PageRecognitionProducer(this, TraineddataFiles.getTessdataDir(),
				RecognitionProducer.DEFAULT_TRAINING_FILE);

		// init traineddata files
		try {
			final List<String> traineddataFiles = TraineddataFiles.getAvailable();

			// prepare traineddata file list model
			final DefaultListModel<String> traineddataFilesModel = new DefaultListModel<>();

			traineddataFiles.forEach(traineddataFilesModel::addElement);

			final JList<String> traineddataFilesList = view.getTraineddataFiles().getList();

			// wrap it in a filtered model
			traineddataFilesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			traineddataFilesList.setModel(new FilteredListModel<>(traineddataFilesModel));

			lastTraineddataFile = PreferencesUtil.getPreferences().get(KEY_TRAINING_FILE,
					RecognitionProducer.DEFAULT_TRAINING_FILE);

			traineddataFilesList.setSelectedValue(lastTraineddataFile, true);

			// handle the new traineddata file selection
			handleTraineddataFileSelection();
		} catch (IOException e) {
			Dialogs.showError(view, "Error", "Traineddata files could not be found.");
		}

		try {
			pageRecognitionProducer.init();
			pageRecognitionProducer.setPageSegmentationMode(getPageSegmentationMode());

		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}

		// register listeners
		view.addWindowListener(this);
		view.getMainTabs().addChangeListener(this);

		// menu
		menuHandler = new MenuHandler();
			

		view.getPages().getList().addListSelectionListener(this);
		final JViewport pagesViewport = (JViewport) view.getPages().getList().getParent();
		pagesViewport.addChangeListener(this);
		view.getTraineddataFiles().getList().addListSelectionListener(this);
		view.getScale().addScaleListener(this);

		{
			// preprocessing pane
			final PreprocessingPane preprocessingPane = view.getPreprocessingPane();

			preprocessingPane.getPreviewButton().addActionListener(this);
			preprocessingPane.getApplyPageButton().addActionListener(this);
			preprocessingPane.getApplyAllPagesButton().addActionListener(this);
		}

		{
			// glyph overview pane
			final SymbolOverview symbolOverview = view.getSymbolOverview();
			symbolOverview.getSymbolGroupList().getList().addListSelectionListener(this);
			symbolOverview.getSymbolVariantList().getList().addListSelectionListener(this);
			symbolOverview.getSymbolVariantList().getCompareToPrototype().addActionListener(this);
			symbolOverview.getSymbolVariantList().getShowInBoxEditor().addActionListener(this);
			symbolOverview.getSymbolVariantList().getOrderingComboBox().addActionListener(this);
		}

		{
			// evaluation pane
			final EvaluationPane evalPane = view.getEvaluationPane();
			evalPane.getSaveTranscriptionButton().addActionListener(this);
			evalPane.getGenerateReportButton().addActionListener(this);
			evalPane.getUseOCRResultButton().addActionListener(this);
		}

		view.setVisible(true);
	}
	

	@Override
	public void actionPerformed(ActionEvent evt) {
		final Object source = evt.getSource();
		final SymbolOverview symbolOverview = view.getSymbolOverview();
		final PreprocessingPane preprocessingPane = view.getPreprocessingPane();
		final EvaluationPane evalPane = view.getEvaluationPane();

//		if (source.equals(view.getMenuItemExit())) {
//			handleExit();
//		} else if (source.equals(view.getMenuItemNewProject())) {
//			handleNewProject();
//			// } else if (source.equals(view.getMenuItemOpenProject())) {
//			// handleOpenProject();
//		} else if (source.equals(view.getMenuItemOpenBoxFile())) {
//			handleOpenBoxFile();
//			// } else if (source.equals(view.getMenuItemSaveProject())) {
//			// handleSaveProject();
//		} else if (source.equals(view.getMenuItemSaveBoxFile())) {
//			handleSaveBoxFile();
//		} else if (source.equals(view.getMenuItemCloseProject())) {
//			handleCloseProject();
//		} else if (source.equals(view.getMenuItemOpenProjectDirectory())) {
//			handleOpenProjectDirectory();
//		} else if (source.equals(view.getMenuItemImportTranscriptions())) {
//			handleImportTranscriptions();
//		} else if (source.equals(view.getMenuItemBatchExport())) {
//			handleBatchExport();
//		} else if (source.equals(view.getMenuItemPreferences())) {
//			handlePreferences();
//		} else if (source.equals(view.getMenuItemCharacterHistogram())) {
//			handleCharacterHistogram();
//		} else if (source.equals(view.getMenuItemInspectUnicharset())) {
//			handleInspectUnicharset();
//		} else if (source.equals(view.getMenuItemTesseractTrainer())) {
//			handleTesseractTrainer();
		if (preprocessingPane.getPreviewButton().equals(source)) {
			handlePreprocessorPreview();
		} else if (preprocessingPane.getApplyPageButton().equals(source)) {
			handlePreprocessorChange(false);
		} else if (preprocessingPane.getApplyAllPagesButton().equals(source)) {
			handlePreprocessorChange(true);
		} else if (source.equals(symbolOverview.getSymbolVariantList().getCompareToPrototype())) {
			handleCompareSymbolToPrototype();
		} else if (source.equals(symbolOverview.getSymbolVariantList().getShowInBoxEditor())) {
			handleShowSymbolInBoxEditor();
		} else if (source.equals(symbolOverview.getSymbolVariantList().getOrderingComboBox())) {
			handleSymbolReordering();
		} else if (source.equals(evalPane.getSaveTranscriptionButton())) {
			handleTranscriptionSave();
		} else if (source.equals(evalPane.getGenerateReportButton())) {
			handleGenerateReport();
		} else if (source.equals(evalPane.getUseOCRResultButton())) {
			handleUseOCRResult();
		} else {
			throw new UnsupportedOperationException(String.format("Unhandled ActionEvent: '%s'", evt));
		}
	}

	@Override
	public PageModel getPageModel() {
		final MainComponent active = view.getActiveComponent();

		if (active instanceof PageModelComponent) {
			return ((PageModelComponent) active).getPageModel();
		}

		return null;
	}

	public PageRecognitionProducer getPageRecognitionProducer() {
		return pageRecognitionProducer;
	}

	@Override
	public ProjectModel getProjectModel() {
		return projectModel;
	}

	public Path getSelectedPage() {
		PageThumbnail thumbnail = view.getPages().getList().getSelectedValue();
		return thumbnail == null ? null : thumbnail.getFile();
	}

	public String getTrainingFile() {
		return view.getTraineddataFiles().getList().getSelectedValue();
	}

	public TesseractFrame getView() {
		return view;
	}

	private void handleActiveComponentChange() {
		final MainComponent active = view.getActiveComponent();

		// didn't change
		if (active == activeComponent) {
			return;
		}

		if (mode == ApplicationMode.BOX_FILE) {
			// if we're in box file mode, everything is simple
			if (active == view.getBoxEditor()) {
				view.getBoxEditor().setBoxFileModel(view.getSymbolOverview().getBoxFileModel());
			} else {
				view.getSymbolOverview().setBoxFileModel(view.getBoxEditor().getBoxFileModel());
			}
		} else if (mode == ApplicationMode.PROJECT) {
			// in project mode, it's a bit more complicated

			if (active instanceof ImageModelComponent) {
				if (activeComponent instanceof ImageModelComponent) {
					// ImageModelComponent -> ImageModelComponent
					setImageModel(((ImageModelComponent) activeComponent).getImageModel());
				} else if (activeComponent instanceof PageModelComponent) {
					// PageModelComponent -> ImageModelComponent
					final PageModel pm = ((PageModelComponent) activeComponent).getPageModel();

					if (Objects.nonNull(pm)) {
						setImageModel(pm.getImageModel());
					} else {
						setImageModel(null);
					}
				} else {
					setImageModel(null);
				}
			} else if (active instanceof PageModelComponent) {
				if (activeComponent instanceof PageModelComponent) {
					// PageModelComponent -> PageModelComponent
					setPageModel(((PageModelComponent) activeComponent).getPageModel());
				} else if (activeComponent instanceof ImageModelComponent) {
					// ImageModelComponent -> PageModelComponent
					setImageModel(((ImageModelComponent) activeComponent).getImageModel());
				} else {
					setPageModel(null);
				}
			}
		}

		activeComponent = active;
	}

	private void handleUseOCRResult() {
		if (Objects.nonNull(getPageModel())) {
			final StringWriter ocrResult = new StringWriter();
			try {
				new PlainTextWriter(true).write(getPageModel().getPage(), ocrResult);

				view.getEvaluationPane().getTextAreaTranscript().setText(ocrResult.toString());
			} catch (IOException e) {
				Dialogs.showWarning(view, "Error", "Could not use the OCR result.");
			}
		}
	}

	private void handleCompareSymbolToPrototype() {
		final Symbol selected = view.getSymbolOverview().getSymbolVariantList().getList().getSelectedValue();

		final PageModel pm = getPageModel();
		if (Objects.nonNull(pm)) {
			final BufferedImage pageImg = pm.getImageModel().getPreprocessedImage();
			final Box symbolBox = selected.getBoundingBox();
			final BufferedImage symbolImg = pageImg.getSubimage(symbolBox.getX(), symbolBox.getY(),
					symbolBox.getWidth(), symbolBox.getHeight());

			final List<Feature3D> features = pageRecognitionProducer.getFeaturesForSymbol(symbolImg);

			featureDebugger.setFeatures(features);
			featureDebugger.setVisible(true);
		}
	}

	private void handleGenerateReport() {
		final Path transcriptionFile = handleTranscriptionSave();

		if (Objects.isNull(transcriptionFile)) {
			Dialogs.showWarning(view, "Report", "The report could not be generated.");
			return;
		}

		final Path sourceFile = getPageModel().getImageModel().getSourceFile();
		final Path fname = FileNames.replaceExtension(sourceFile, "txt").getFileName();
		final Path repName = FileNames.replaceExtension(fname, "html");
		final Path plain = projectModel.getOCRDir().resolve(fname);
		final Path report = projectModel.getEvaluationDir().resolve(repName);

		try {
			final Path equivalencesFile = prepareReports();

			// generate report
			final Batch reportBatch = new Batch(transcriptionFile.toFile(), plain.toFile());
			final Parameters pars = new Parameters();
			pars.eqfile.setValue(equivalencesFile.toFile());
			final Report rep = new Report(reportBatch, pars);

			// write to file
			DocumentWriter.writeToFile(rep.document(), report);

			if (Desktop.isDesktopSupported()) {
				Desktop.getDesktop().open(report.toFile());
			}
		} catch (WarningException | IOException | TransformerException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	public Path prepareReports() throws IOException {
		Files.createDirectories(projectModel.getEvaluationDir());

		final Path equivalencesFile = projectModel.getProjectDir().resolve("character_equivalences.csv");

		if (!Files.exists(equivalencesFile)) {
			// copy the default character equivalences to the equivalences file

			try (final BufferedInputStream defaultEq = new BufferedInputStream(
					getClass().getResourceAsStream("/default_character_equivalences.csv"));
					final BufferedOutputStream eq = new BufferedOutputStream(
							new FileOutputStream(equivalencesFile.toFile()))) {

				int c;
				while ((c = defaultEq.read()) != -1) {
					eq.write(c);
				}

			}
		}

		return equivalencesFile;
	}

	private Path handleTranscriptionSave() {
		try {
			if (Objects.nonNull(projectModel) && Objects.nonNull(getPageModel())) {
				Files.createDirectories(projectModel.getTranscriptionDir());

				final Path sourceFile = getPageModel().getImageModel().getSourceFile();
				final Path fileName = FileNames.replaceExtension(sourceFile, "txt").getFileName();

				final Path transcriptionFile = projectModel.getTranscriptionDir().resolve(fileName);

				try (final Writer writer = Files.newBufferedWriter(transcriptionFile, StandardCharsets.UTF_8)) {

					final String transcription = view.getEvaluationPane().getTextAreaTranscript().getText();

					writer.write(transcription);

					return transcriptionFile;
				}
			}
		} catch (IOException e) {
			Dialogs.showError(view, "Exception", "Transcription could not be saved.");
		}

		return null;
	}

	public void setProjectModel(ProjectModel model) {
		projectModel = model;

		if (Objects.nonNull(model)) {
			view.setTitle(String.format("tesseract4java - %s", model.getProjectName()));
		} else {
			view.setTitle("tesseract4java");
			view.getPages().getListModel().removeAllElements();
		}
	}

	private void handleOpenProject() {
		if (mode == ApplicationMode.BOX_FILE && !handleCloseBoxFile()) {
			return;
		} else if (mode == ApplicationMode.PROJECT && !menuHandler.handleCloseProject()) {
			return;
		}

		final JFileChooser fc = new JFileChooser();
		fc.setFileFilter(new FileFilter() {
			@Override
			public String getDescription() {
				return "Tesseract Project Files (*.tesseract-project)";
			}

			@Override
			public boolean accept(File f) {
				return f.isFile() && f.getName().endsWith(".tesseract-project");
			}
		});
		final int result = fc.showOpenDialog(view);
		if (result == JFileChooser.APPROVE_OPTION) {
			// TODO load project

		}
	}

	private void handleSaveProject() {
		// TODO fix me
		final JFileChooser fc = new JFileChooser(projectModel.getProjectDir().toFile());
		fc.setFileFilter(new FileFilter() {
			@Override
			public String getDescription() {
				return "Tesseract Project Files (*.tesseract-project)";
			}

			@Override
			public boolean accept(File f) {
				return f.isFile() && f.getName().endsWith(".tesseract-project");
			}
		});
		final int result = fc.showSaveDialog(view);
		if (result == JFileChooser.APPROVE_OPTION) {
			// TODO save project

		}
	}

	public BoxFileModel getBoxFileModel() {
		if (mode == ApplicationMode.NONE) {
			return null;
		} else if (mode == ApplicationMode.BOX_FILE) {
			// first check box editor, then symbol overview
			final BoxFileModel model = view.getBoxEditor().getBoxFileModel();

			if (Objects.nonNull(model)) {
				return model;
			} else {
				return view.getSymbolOverview().getBoxFileModel();
			}
		} else {
			final MainComponent active = view.getActiveComponent();

			if (active instanceof PageModelComponent) {
				return ((PageModelComponent) active).getBoxFileModel();
			} else {
				return null;
			}
		}
	}

	public boolean handleCloseBoxFile() {
		final boolean really = Dialogs.ask(view, "Confirmation",
				"Do you really want to close this box file? All unsaved changes will be lost.");

		if (really) {
			setBoxFileModel(null);

			setApplicationMode(ApplicationMode.NONE);
		}

		return really;
	}

	private void handlePageSelection() {
		final PageThumbnail pt = view.getPages().getList().getSelectedValue();

		// don't do anything, if no page is selected
		if (pt == null) {
			return;
		}

		final Preprocessor preprocessor = getPreprocessor(pt.getFile());
		view.getPreprocessingPane().setPreprocessor(preprocessor);

		// ask to save box file
		if (view.getActiveComponent() == view.getBoxEditor() && view.getBoxEditor().hasChanged()) {
			final boolean changePage = Dialogs.ask(view, "Unsaved Changes",
					"The current box file has not been saved. Do you really want to change the page?");

			if (!changePage) {
				// reselect the old page
				view.getPages().getList().setSelectedValue(pageThumbnail, true);
				// don't change the page
				return;
			}
		} else if (view.getActiveComponent() == view.getSymbolOverview()) {
			view.getSymbolOverview().freeResources();
		}

		pageThumbnail = pt;

		// cancel the last page loading task if it is present
		if (Objects.nonNull(lastPageSelectionTask)) {
			lastPageSelectionTask.cancel();
		}

		// new task
		final TimerTask task = new TimerTask() {
			@Override
			public void run() {
				// cancel last task
				if (Objects.nonNull(preprocessingWorker)) {
					preprocessingWorker.cancel(false);
				}

				// create SwingWorker to preprocess page
				final PreprocessingWorker pw = new PreprocessingWorker(TesseractApp.this, getPreprocessor(pt.getFile()),
						pt.getFile(), getProjectModel().getPreprocessedDir());

				// save reference
				preprocessingWorker = pw;

				view.getProgressBar().setIndeterminate(true);
				// execute it
				pw.execute();
			}
		};

		// run the page loader with a delay of 1 second
		// the user has 1 second to change the page before it starts loading
		pageSelectionTimer.schedule(task, 500);

		// set as new timer task
		lastPageSelectionTask = task;
	}

	private void handleShowSymbolInBoxEditor() {
		final Symbol selected = view.getSymbolOverview().getSymbolVariantList().getList().getSelectedValue();

		if (selected == null) {
			return;
		}

		view.getMainTabs().setSelectedComponent(view.getBoxEditor());

		final FilteredTable<Symbol> symbols = view.getBoxEditor().getSymbols();
		symbols.getTextField().setText("");
		final ListModel<Symbol> model = symbols.getListModel();
		final int size = model.getSize();

		// find the selected symbol in
		for (int i = 0; i < size; i++) {
			if (selected == model.getElementAt(i)) {
				symbols.getTable().setRowSelectionInterval(i, i);
			}
		}
	}

	private void handleSymbolGroupSelection() {
		final JList<Entry<String, List<Symbol>>> selectionList = view.getSymbolOverview().getSymbolGroupList()
				.getList();

		final int index = selectionList.getSelectedIndex();
		if (index == -1) {
			return;
		}

		final List<Symbol> symbols = selectionList.getModel().getElementAt(index).getValue();

		final BoxFileModel bfm = view.getSymbolOverview().getBoxFileModel();

		if (Objects.isNull(bfm)) {
			return;
		}

		final SymbolListModel model = new SymbolListModel(bfm.getImage());
		symbols.forEach(model::addElement);

		// get combo box
		final JComboBox<SymbolOrder> ordering = view.getSymbolOverview().getSymbolVariantList().getOrderingComboBox();

		// sort symbols
		model.sortBy((SymbolOrder) ordering.getSelectedItem());

		view.getSymbolOverview().getSymbolVariantList().getList().setModel(model);
	}

	private void handleSymbolReordering() {
		// get combo box
		final JComboBox<SymbolOrder> ordering = view.getSymbolOverview().getSymbolVariantList().getOrderingComboBox();

		// get model
		final SymbolListModel model = (SymbolListModel) view.getSymbolOverview().getSymbolVariantList().getList()
				.getModel();

		// sort symbols
		model.sortBy((SymbolOrder) ordering.getSelectedItem());
	}

	private void handleThumbnailLoading() {
		if (Objects.isNull(thumbnailLoader)) {
			return;
		}

		final ThumbnailWorker thumbnailLoader = this.thumbnailLoader;

		tasks.forEach(Task::cancel);
		tasks.clear();

		if (Objects.nonNull(lastThumbnailLoadTask)) {
			lastThumbnailLoadTask.cancel();
		}

		thumbnailLoadTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				SwingUtilities.invokeLater(() -> {
					final JList<PageThumbnail> list = view.getPages().getList();
					final ListModel<PageThumbnail> model = list.getModel();

					final int first = list.getFirstVisibleIndex();
					final int last = list.getLastVisibleIndex();

					for (int i = first; i <= last; i++) {
						final PageThumbnail pt = model.getElementAt(i);

						if (pt == null || pt.getThumbnail() != null) {
							continue;
						}

						final Task t = new Task(i, pt);
						tasks.add(t);
						thumbnailLoader.submitTask(t);
					}
				});
			}
		}, 500); // 500ms delay
	}

	private void handleTraineddataFileSelection() {

		final String traineddataFile = view.getTraineddataFiles().getList().getSelectedValue();

		if (traineddataFile != null) {
			PreferencesUtil.getPreferences().put(KEY_TRAINING_FILE, traineddataFile);

			pageRecognitionProducer.setTrainingFile(traineddataFile);

			// try {
			// final <IntTemplates> prototypes = loadPrototypes();
			// featureDebugger.setPrototypes(prototypes);
			// } catch (IOException e) {
			// LOGGER.error(e.getMessage(), e);
			// }

			// if the traineddata file has changed, ask to reload the page
			if (!view.getPages().getList().isSelectionEmpty()
					&& !Objects.equals(traineddataFile, lastTraineddataFile)) {
				handlePageSelection();
			}

			lastTraineddataFile = traineddataFile;
		}
	}

	private void handlePreprocessorPreview() {
		final Path selectedPage = getSelectedPage();

		// if no page is selected, simply ignore it
		if (Objects.isNull(selectedPage)) {
			Dialogs.showWarning(view, "No page selection",
					"No page has been selected. You need to select a page first.");
			return;
		}

		final ProjectModel projectModel = getProjectModel();

		if (Objects.isNull(projectModel)) {
			Dialogs.showWarning(view, "No project",
					"No project has been selected. You need to create a project first.");
			return;
		}

		final Preprocessor preprocessor = view.getPreprocessingPane().getPreprocessor();

		if (Objects.nonNull(preprocessingWorker)) {
			preprocessingWorker.cancel(false);
		}

		final PreprocessingWorker pw = new PreprocessingWorker(this, preprocessor, selectedPage,
				projectModel.getProjectDir());

		preprocessingWorker = pw;

		view.getProgressBar().setIndeterminate(true);
		pw.execute();
	}

	private void handlePreprocessorChange(boolean allPages) {
		final Preprocessor preprocessor = view.getPreprocessingPane().getPreprocessor();

		if (allPages && Dialogs.ask(view, "Confirmation",
				"Do you really want to apply the current preprocessing methods to all pages?")) {
			defaultPreprocessor = preprocessor;
			preprocessors.clear();

			if (Objects.nonNull(getSelectedPage())) {
				handlePreprocessorPreview();
			}
		} else if (Objects.nonNull(getSelectedPage())) {
			setPreprocessor(getSelectedPage(), preprocessor);
		}
	}

	public int getPageSegmentationMode() {
		return PreferencesUtil.getPreferences().getInt(PreferencesDialog.KEY_PAGE_SEG_MODE,
				PreferencesDialog.DEFAULT_PSM_MODE);
	}

	public void setPageModel(PageModel model) {
		if (Objects.nonNull(projectModel) && Objects.nonNull(model)) {
			try {
				// plain text file name
				final Path filename = FileNames
						.replaceExtension(model.getImageModel().getSourceFile().getFileName(), "txt");

				// create ocr directory
				Files.createDirectories(projectModel.getOCRDir());

				// write the plain text ocr file
				final Path plain = projectModel.getOCRDir().resolve(filename);

				final Writer writer = Files.newBufferedWriter(plain, StandardCharsets.UTF_8);
				new PlainTextWriter(true).write(model.getPage(), writer);
				writer.close();

				// read the transcription file
				final Path transcriptionFile = projectModel.getTranscriptionDir().resolve(filename);

				if (Files.isRegularFile(transcriptionFile)) {
					final byte[] bytes = Files.readAllBytes(transcriptionFile);
					final String transcription = new String(bytes, StandardCharsets.UTF_8);

					model = model.withTranscription(transcription);
				}
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}

		final MainComponent active = view.getActiveComponent();

		if (active instanceof PageModelComponent) {
			((PageModelComponent) active).setPageModel(model);
		} else if (active instanceof BoxFileModelComponent) {
			if (Objects.nonNull(model)) {
				((BoxFileModelComponent) active).setBoxFileModel(model.toBoxFileModel());
			} else {
				((BoxFileModelComponent) active).setBoxFileModel(null);
			}
		}
	}

	public void setBoxFileModel(BoxFileModel model) {
		final MainComponent active = view.getActiveComponent();
		if (active instanceof BoxFileModelComponent) {
			((BoxFileModelComponent) active).setBoxFileModel(model);
		} else {
			Dialogs.showWarning(view, "Illegal Action", "Could not set the box file");
		}
	}

	public void setImageModel(ImageModel model) {
		view.getProgressBar().setIndeterminate(false);
		final MainComponent active = view.getActiveComponent();

		if (active instanceof PageModelComponent) {
			((PageModelComponent) active).setPageModel(null);

			if (Objects.nonNull(recognitionWorker)) {
				recognitionWorker.cancel(false);
			}

			final String trainingFile = getTrainingFile();

			if (Objects.isNull(trainingFile)) {
				Dialogs.showWarning(view, "Warning", "Please select a traineddata file.");
				return;
			} else if (Objects.isNull(model)) {
				return;
			}

			final RecognitionWorker rw = new RecognitionWorker(this, model, trainingFile);

			rw.execute();

			recognitionWorker = rw;

			return;
		} else if (!(active instanceof ImageModelComponent)) {
			return;
		}

		if (Objects.isNull(model)) {
			((ImageModelComponent) active).setImageModel(model);
			return;
		}

		final Path sourceFile = model.getSourceFile();
		final Path selectedPage = getSelectedPage();

		if (Objects.isNull(selectedPage) || !sourceFile.equals(selectedPage)) {

			((ImageModelComponent) active).setImageModel(null);
			return;
		}

		((ImageModelComponent) active).setImageModel(model);
	}

	// TODO prototype loading?
	// private <IntTemplates> loadPrototypes() throws IOException {
	// final Path tessdir = TraineddataFiles.getTessdataDir();
	// final Path base = tmpDir.resolve(TMP_TRAINING_FILE_BASE);
	//
	// TessdataManager.extract(
	// tessdir.resolve(lastTraineddataFile + ".traineddata"), base);
	//
	// final Path prototypeFile =
	// tmpDir.resolve(tmpDir.resolve(TMP_TRAINING_FILE_BASE
	// + "inttemp"));
	//
	// final InputStream in = Files.newInputStream(prototypeFile);
	// final InputBuffer buf =
	// InputBuffer.allocate(new BufferedInputStream(in));
	//
	// try {
	// final IntTemplates prototypes = IntTemplates.readFrom(buf);
	//
	// return .of(prototypes);
	// } catch (IOException e) {
	// throw e;
	// } finally {
	// // close input buffer, even if an error occurred
	// buf.close();
	// }
	// }

	@Override
	public void stateChanged(ChangeEvent evt) {
		final Object source = evt.getSource();
		if (source == view.getPages().getList().getParent()) {
			handleThumbnailLoading();
		} else if (source == view.getMainTabs()) {
			handleActiveComponentChange();
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent evt) {
		if (evt.getValueIsAdjusting()) {
			return;
		}

		final Object source = evt.getSource();
		if (source.equals(view.getPages().getList())) {
			handlePageSelection();
		} else if (source.equals(view.getTraineddataFiles().getList())) {
			handleTraineddataFileSelection();
		} else if (source.equals(view.getSymbolOverview().getSymbolGroupList().getList())) {
			handleSymbolGroupSelection();
		}
	}

	@Override
	public void windowClosing(WindowEvent e) {
		if (mode == ApplicationMode.PROJECT) {
			if (!menuHandler.handleCloseProject()) {
				return;
			}
		} else if (mode == ApplicationMode.BOX_FILE) {
			if (!handleCloseBoxFile()) {
				return;
			}
		}

		pageSelectionTimer.cancel();
		thumbnailLoadTimer.cancel();

		if (Objects.nonNull(preprocessingWorker)) {
			preprocessingWorker.cancel(true);
		}

		if (Objects.nonNull(recognitionWorker)) {
			recognitionWorker.cancel(true);
		}

		view.dispose();
	}

	@Override
	public void windowClosed(WindowEvent evt) {
		// forcefully shut down the application after 3 seconds
		try {
			Thread.sleep(3000);
			System.exit(0);
		} catch (InterruptedException e) {
			System.exit(0);
		}
	}

	public Preprocessor getDefaultPreprocessor() {
		return defaultPreprocessor;
	}

	public Preprocessor getPreprocessor(Path sourceFile) {
		final Preprocessor preprocessor = preprocessors.get(sourceFile);

		if (preprocessor == null) {
			return defaultPreprocessor;
		}

		return preprocessors.get(sourceFile);
	}

	public boolean hasPreprocessorChanged(Path sourceFile) {
		// try to remove it and return true if the set contained the sourceFile
		return changedPreprocessors.contains(sourceFile);
	}

	public void setDefaultPreprocessor(Preprocessor preprocessor) {
		defaultPreprocessor = preprocessor;
	}

	public void setPreprocessor(Path sourceFile, Preprocessor preprocessor) {
		if (preprocessor.equals(defaultPreprocessor)) {
			preprocessors.remove(sourceFile);
		} else {
			preprocessors.put(sourceFile, preprocessor);
		}
	}

	public void setPreprocessorChanged(Path sourceFile, boolean changed) {
		if (changed) {
			changedPreprocessors.add(sourceFile);
		} else {
			changedPreprocessors.remove(sourceFile);
		}
	}

	public void setApplicationMode(ApplicationMode mode) {
		ApplicationMode current = this.mode;
		this.mode = mode;
		view.onApplicationModeChanged(mode, current);
	}

	public ApplicationMode getApplicationMode() {
		return mode;
	}

	@Override
	public void scaleChanged(ScaleEvent scaleEvent) {
		if (scaleEvent.getSource() == view.getScale()) {
			view.getScaleLabel().setText(scaleEvent.getSource().toString());
		}
	}

	public void setThumbnailLoader(ThumbnailWorker thumbnailLoader) {
		this.thumbnailLoader = thumbnailLoader;
	}

	public MainComponent getActiveComponent() {
		return activeComponent;
	}

	@Override
	public void closeProject() {
		this.setPageModel(null);
		this.setProjectModel(null);
		this.setApplicationMode(ApplicationMode.NONE);
	}

	@Override
	public void exit() {
		this.windowClosing(new WindowEvent(this.getView(), WindowEvent.WINDOW_CLOSING));
		if (!this.getView().isVisible()) {
			this.windowClosed(new WindowEvent(this.getView(), WindowEvent.WINDOW_CLOSED));
		}
	}
}
