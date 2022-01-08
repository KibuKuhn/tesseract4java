package de.vorb.tesseract.gui.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.util.LinkedList;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

import de.vorb.tesseract.gui.model.ApplicationMode;
import de.vorb.tesseract.gui.model.PageThumbnail;
import de.vorb.tesseract.gui.model.PreferencesUtil;
import de.vorb.tesseract.gui.model.Scale;
import de.vorb.tesseract.gui.util.Filter;
import de.vorb.tesseract.gui.util.Icons;
import de.vorb.tesseract.gui.view.dialogs.PreferencesDialog;
import de.vorb.tesseract.gui.view.i18n.Labels;
import de.vorb.tesseract.gui.view.renderer.PageListCellRenderer;

/**
 * Swing component that allows to compare the results of Tesseract.
 */
public class TesseractFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	private final FilteredList<PageThumbnail> listPages;
	private final FilteredList<String> listTrainingFiles;
	private final PreprocessingPane preprocessingPane;
	private final BoxEditor boxEditor;
	private final SymbolOverview glyphOverview;
	private final RecognitionPane recognitionPane;
	private final EvaluationPane evaluationPane;

	private final JLabel lblScaleFactor;
	private final JProgressBar pbLoadPage;
	private final JSplitPane spMain;
	private final JTabbedPane tabsMain;

	private final Scale scale;

	/**
	 * Create the application.
	 */
	public TesseractFrame() {
		super();
		final Toolkit toolkit = Toolkit.getDefaultToolkit();

		// load and set multiple icon sizes
		final List<Image> appIcons = new LinkedList<>();
		appIcons.add(toolkit.getImage(TesseractFrame.class.getResource("/logos/logo_16.png")));
		appIcons.add(toolkit.getImage(TesseractFrame.class.getResource("/logos/logo_96.png")));
		appIcons.add(toolkit.getImage(TesseractFrame.class.getResource("/logos/logo_256.png")));
		setIconImages(appIcons);

		setLocationByPlatform(true);
		setMinimumSize(new Dimension(1100, 680));
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		scale = new Scale();
		preprocessingPane = new PreprocessingPane();
		boxEditor = new BoxEditor(scale);
		glyphOverview = new SymbolOverview();

		final Preferences prefs = PreferencesUtil.getPreferences();
		final String renderingFont = prefs.get(PreferencesDialog.KEY_RENDERING_FONT, Font.SANS_SERIF);
		final String editorFont = prefs.get(PreferencesDialog.KEY_EDITOR_FONT, Font.MONOSPACED);

		recognitionPane = new RecognitionPane(scale, renderingFont);
		evaluationPane = new EvaluationPane(scale, editorFont);
		evaluationPane.getGenerateReportButton()
				.setIcon(new ImageIcon(TesseractFrame.class.getResource("/icons/report.png")));
		pbLoadPage = new JProgressBar();
		spMain = new JSplitPane();

		listPages = new FilteredList<>(query -> {
			final String[] terms = query.toLowerCase().split("\\s+");

			final Filter<PageThumbnail> filter;
			if (query.isEmpty()) {
				filter = null;
			} else {
				// item must contain all terms in query
				filter = item -> {
					String filename = item.getFile().getFileName().toString().toLowerCase();
					for (String term : terms) {
						if (!filename.contains(term)) {
							return false;
						}
					}
					return true;
				};
			}
			return filter;
		});
		listPages.getList().setCellRenderer(new PageListCellRenderer());

		listPages.setMinimumSize(new Dimension(250, 100));
		listPages.getList().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listPages.setBorder(BorderFactory.createTitledBorder("Image"));

		// filtered string list
		listTrainingFiles = new FilteredList<>(query -> {
			final String[] terms = query.toLowerCase().split("\\s+");

			final Filter<String> filter;
			if (query.isEmpty()) {
				filter = null;
			} else {
				// item must contain all terms in query
				filter = item -> {
					for (String term : terms) {
						if (!item.toLowerCase().contains(term)) {
							return false;
						}
					}
					return true;
				};
			}
			return filter;
		});

		listTrainingFiles.setBorder(BorderFactory.createTitledBorder("Traineddata File"));

		setTitle(Labels.getLabel("frame_title"));

		// Menu
		final JMenuBar menuBar = new MenuBar();
		setJMenuBar(menuBar);

		// Contents

		final JPanel panel = new JPanel();
		panel.setBackground(SystemColor.menu);
		panel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(panel, BorderLayout.SOUTH);
		final GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 0, 50, 417, 0, 0 };
		gbl_panel.rowHeights = new int[] { 14, 0 };
		gbl_panel.columnWeights = new double[] { 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE };
		gbl_panel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		panel.setLayout(gbl_panel);

		JLabel lblScale = new JLabel("Scale:");
		GridBagConstraints gbc_lblScale = new GridBagConstraints();
		gbc_lblScale.insets = new Insets(0, 0, 0, 5);
		gbc_lblScale.gridx = 0;
		gbc_lblScale.gridy = 0;
		panel.add(lblScale, gbc_lblScale);

		lblScaleFactor = new JLabel(scale.toString());
		GridBagConstraints gbc_lblScaleFactor = new GridBagConstraints();
		gbc_lblScaleFactor.anchor = GridBagConstraints.WEST;
		gbc_lblScaleFactor.insets = new Insets(0, 0, 0, 5);
		gbc_lblScaleFactor.gridx = 1;
		gbc_lblScaleFactor.gridy = 0;
		panel.add(lblScaleFactor, gbc_lblScaleFactor);

		GridBagConstraints gbc_pbRegognitionProgress = new GridBagConstraints();
		gbc_pbRegognitionProgress.gridx = 3;
		gbc_pbRegognitionProgress.gridy = 0;
		panel.add(pbLoadPage, gbc_pbRegognitionProgress);
		getContentPane().add(spMain, BorderLayout.CENTER);

		tabsMain = new JTabbedPane();
		tabsMain.addTab(Labels.getLabel("tab_main_preprocessing"), Icons.get().getIcon("contrast"),
				preprocessingPane);

		tabsMain.addTab(Labels.getLabel("tab_main_boxeditor"), Icons.get().getIcon("table_edit"), boxEditor);

		tabsMain.addTab(Labels.getLabel("tab_main_symboloverview"),
				Icons.get().getIcon("application_view_icons"), glyphOverview);

		tabsMain.addTab(Labels.getLabel("tab_main_recognition"),
				Icons.get().getIcon("application_tile_horizontal"), recognitionPane);

		tabsMain.addTab(Labels.getLabel("tab_main_evaluation"), Icons.get().getIcon("chart_pie"),
				evaluationPane);

		spMain.setRightComponent(tabsMain);

		JSplitPane splitPane = new JSplitPane();
		splitPane.setResizeWeight(1.0);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		spMain.setLeftComponent(splitPane);
		splitPane.setLeftComponent(listPages);
		splitPane.setRightComponent(listTrainingFiles);
	}

	public MainComponent getActiveComponent() {
		return (MainComponent) tabsMain.getSelectedComponent();
	}

	public BoxEditor getBoxEditor() {
		return boxEditor;
	}

	public JTabbedPane getMainTabs() {
		return tabsMain;
	}



	public FilteredList<PageThumbnail> getPages() {
		return listPages;
	}

	public PreprocessingPane getPreprocessingPane() {
		return preprocessingPane;
	}

	public JProgressBar getProgressBar() {
		return pbLoadPage;
	}

	public RecognitionPane getRecognitionPane() {
		return recognitionPane;
	}

	public Scale getScale() {
		return scale;
	}

	public JLabel getScaleLabel() {
		return lblScaleFactor;
	}

	public SymbolOverview getSymbolOverview() {
		return glyphOverview;
	}

	public EvaluationPane getEvaluationPane() {
		return evaluationPane;
	}

	public FilteredList<String> getTraineddataFiles() {
		return listTrainingFiles;
	}

	public void onApplicationModeChanged(ApplicationMode newMode, ApplicationMode currentMode) {
		boolean projectEnabled;
		boolean boxFileEnabled;
		if (newMode == ApplicationMode.NONE) {
			tabsMain.setEnabled(false);
			projectEnabled = false;
			boxFileEnabled = false;
		} else {
			tabsMain.setEnabled(true);
			boxFileEnabled = true;

			if (newMode == ApplicationMode.BOX_FILE) {
				// set box file tabs active
				tabsMain.setEnabledAt(0, false);
				tabsMain.setEnabledAt(1, true);
				tabsMain.setEnabledAt(2, true);
				tabsMain.setEnabledAt(3, false);
				tabsMain.setEnabledAt(4, false);
				tabsMain.setSelectedIndex(1);

				projectEnabled = false;
			} else {
				// set all tabs active
				tabsMain.setEnabledAt(0, true);
				tabsMain.setEnabledAt(1, true);
				tabsMain.setEnabledAt(2, true);
				tabsMain.setEnabledAt(3, true);
				tabsMain.setEnabledAt(4, true);

				projectEnabled = true;
			}
		}

		MenuBar menuBar = (MenuBar) this.getJMenuBar();
		menuBar.onApplicationModeChanged(boxFileEnabled, projectEnabled);
		this.glyphOverview.getSymbolVariantList().getCompareToPrototype().setVisible(projectEnabled);
	}
}
