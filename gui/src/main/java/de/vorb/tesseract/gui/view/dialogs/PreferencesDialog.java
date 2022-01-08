package de.vorb.tesseract.gui.view.dialogs;

import static java.awt.GridBagConstraints.EAST;
import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.RELATIVE;
import static java.awt.GridBagConstraints.REMAINDER;
import static java.awt.GridBagConstraints.WEST;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Arrays;
import java.util.Locale;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.EmptyBorder;

import de.vorb.tesseract.gui.model.PreferencesUtil;

public class PreferencesDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	private static final String[] PSM_MODES = { "0 - PSM_OSD_ONLY", "1 - PSM_AUTO_OSD", "2 - PSM_AUTO_ONLY",
			"3 - (DEFAULT) PSM_AUTO", "4 - PSM_SINGLE_COLUMN", "5 - PSM_SINGLE_BLOCK_VERT_TEXT", "6 - PSM_SINGLE_BLOCK",
			"7 - PSM_SINGLE_LINE", "8 - PSM_SINGLE_WORD", "9 - PSM_CIRCLE_WORD", "10 - PSM_SINGLE_CHAR",
			"11 - PSM_SPARSE_TEXT", "12 - PSM_SPARSE_TEXT_OSD", "13 - PSM_RAW_LINE", };
	public static final int DEFAULT_PSM_MODE = 3;

	public static final String KEY_LANGDATA_DIR = "langdata_dir";
	public static final String KEY_RENDERING_FONT = "rendering_font";
	public static final String KEY_EDITOR_FONT = "editor_font";
	public static final String KEY_PAGE_SEG_MODE = "page_seg_mode";
	public static final String KEY_LANGUAGE = "language";
	public static final String KEY_LAF = "laf";

	private JTextField tfLangdataDir;
	private JComboBox<String> comboRenderingFont;
	private JComboBox<String> comboEditorFont;
	private JComboBox<String> comboPageSegMode;
	private JComboBox<Locale> comboLanguage;

	private ResultState resultState = ResultState.CANCEL;

	private JComboBox<LookAndFeelInfo> comboLaf;


	public enum ResultState {
		APPROVE, CANCEL
	}

	
	public PreferencesDialog() {
		Preferences pref = PreferencesUtil.getPreferences();
		initUI(pref);
		initData(pref);
	}

	private void initUI(Preferences pref) {
		setIconImage(Toolkit.getDefaultToolkit().getImage(PreferencesDialog.class.getResource("/logos/logo_16.png")));
		setModalityType(ModalityType.APPLICATION_MODAL);
		setTitle("General Preferences");
		// setBounds(100, 100, 450, 300);
		JPanel pane = (JPanel) getContentPane();
		pane.setLayout(new GridBagLayout());
		pane.setBorder(new EmptyBorder(5, 5, 5, 5));

		GridBagConstraints constraints = new GridBagConstraints();

		//language
		constraints.anchor = WEST;
		constraints.insets = new Insets(0, 0, 5, 5);
		constraints.gridx = 0;
		constraints.gridy = 0;
		pane.add(new JLabel("Language:"), constraints);
		constraints.fill = HORIZONTAL;
		constraints.gridx = RELATIVE;
		constraints.gridwidth = REMAINDER;
		constraints.weightx = 1;
		constraints.insets.right = 0;
		comboLanguage = new JComboBox<>();
		comboLanguage.setRenderer(new LocaleRenderer());
		pane.add(comboLanguage, constraints);
		
		//L&F
		constraints.gridx = 0;
		constraints.gridy += 1;
		constraints.fill = NONE;
		constraints.weightx = 0;
		constraints.gridwidth = 1;
		constraints.insets.right = 5;
		pane.add(new JLabel("Look & Feel:"), constraints);
		constraints.fill = HORIZONTAL;
		constraints.gridx = RELATIVE;
		constraints.gridwidth = REMAINDER;
		constraints.weightx = 1;
		constraints.insets.right = 0;
		comboLaf = new JComboBox<>();
		comboLaf.setRenderer(new LafRenderer());
		pane.add(comboLaf, constraints);
		
		//Langdata
		constraints.gridx = 0;
		constraints.gridy += 1;
		constraints.fill = NONE;
		constraints.weightx = 0;
		constraints.gridwidth = 1;
		constraints.insets.right = 5;
		pane.add(new JLabel("\"Langdata\" directory:"), constraints);
		constraints.fill = HORIZONTAL;
		constraints.gridx = RELATIVE;
		constraints.fill = HORIZONTAL;
		constraints.weightx = 1;
		tfLangdataDir = new JTextField(pref.get(KEY_LANGDATA_DIR, ""));
		tfLangdataDir.setColumns(30);
		pane.add(tfLangdataDir, constraints);
		constraints.gridwidth = REMAINDER;
		constraints.fill = NONE;
		constraints.anchor = EAST;
		constraints.insets.right = 0;
		pane.add(new JButton(new SelectAction()), constraints);
		
		// Rendering font
		constraints.gridx = 0;
		constraints.gridy += 1;
		constraints.gridwidth = 1;
		constraints.insets.right = 5;
		constraints.anchor = WEST;
		constraints.fill = NONE;
		constraints.weightx = 0;
		pane.add(new JLabel("Rendering font:"), constraints);
		
		
		constraints.fill = HORIZONTAL;
		constraints.gridx = RELATIVE;
		constraints.gridwidth = REMAINDER;
		constraints.weightx = 1;
		constraints.fill = HORIZONTAL;
		constraints.insets.right = 0;
		comboRenderingFont = new JComboBox<>();
		pane.add(comboRenderingFont, constraints);
		
		// Editor font
		constraints.gridx = 0;
		constraints.gridy +=1;
		constraints.fill = NONE;
		constraints.gridwidth = 1;
		constraints.weightx = 0;
		constraints.fill = NONE;
		constraints.insets.right = 5;
		pane.add(new JLabel("Editor font:"), constraints);
		constraints.fill = HORIZONTAL;
		constraints.gridx = RELATIVE;
		constraints.gridwidth = REMAINDER;
		constraints.weightx = 1;
		constraints.fill = HORIZONTAL;
		constraints.insets.right = 0;
		comboEditorFont = new JComboBox<>();
		pane.add(comboEditorFont, constraints);
		
		// Page Segmentation Modes
		constraints.gridx = 0;
		constraints.gridy += 1;
		constraints.fill = NONE;
		constraints.gridwidth = 1;
		constraints.weightx = 0;
		constraints.fill = NONE;
		constraints.insets.right = 5;
		pane.add(new JLabel("Page Segmentation Mode:"), constraints);
				
		comboPageSegMode = new JComboBox<>();
		constraints.fill = HORIZONTAL;
		constraints.gridx = RELATIVE;
		constraints.gridwidth = REMAINDER;
		constraints.weightx = 1;
		constraints.fill = HORIZONTAL;
		constraints.insets.right = 0;
		pane.add(comboPageSegMode, constraints);
		
		//filler
		constraints.gridx = 0;
		constraints.gridy += 1;
		constraints.gridwidth = REMAINDER;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.BOTH;
		pane.add(Box.createGlue(), constraints);
		
		addButtonPane(pane, constraints);
		
		pack();
		Dimension size = getSize();
		size.height += 10;
		setMinimumSize(size);
	}
	
	private void initData(Preferences pref) {
		Locale[] supportedLocales = new Locale[] {Locale.ENGLISH, Locale.GERMAN};
		Locale defaultLocale = Locale.getDefault();
		Locale initialLocale = Arrays.stream(supportedLocales)
		                             .filter(sl -> sl.getLanguage().equals(defaultLocale.getLanguage()) ||
		                           		  		sl.getLanguage().equals(defaultLocale.getLanguage()))
		                             .findFirst()
		                             .orElse(Locale.ENGLISH);
		String language = pref.get(KEY_LANGUAGE, initialLocale.getLanguage());
		Locale selectedLocale = Arrays.stream(supportedLocales)
		                              .filter(l -> l.getLanguage().equals(language))
		                              .findFirst()
		                              .orElse(initialLocale);
		DefaultComboBoxModel<Locale> model = new DefaultComboBoxModel<>();
		Arrays.stream(supportedLocales)
		      .sorted((l1, l2) -> l1.getDisplayLanguage().compareTo(l2.getDisplayLanguage()))
		      .forEach(model::addElement);
		comboLanguage.setModel(model);
		comboLanguage.setSelectedItem(selectedLocale);
		
		DefaultComboBoxModel<LookAndFeelInfo> lafModel = new DefaultComboBoxModel<>();
		LookAndFeelInfo[] installedLookAndFeels = UIManager.getInstalledLookAndFeels();
		Arrays.stream(installedLookAndFeels)
		      .sorted((l1, l2) -> l1.getName().compareTo(l2.getName()))
		      .forEach(lafModel::addElement);
		String initialLafClassName = UIManager.getSystemLookAndFeelClassName();
		String selectedLafClassName = pref.get(KEY_LAF, initialLafClassName);
		LookAndFeelInfo selectedLaflInfo = Arrays.stream(installedLookAndFeels)		      
		                                         .filter(laf -> laf.getClassName().equals(selectedLafClassName))
		                                         .findFirst()
		                                         .get();
		comboLaf.setModel(lafModel);
		comboLaf.setSelectedItem(selectedLaflInfo);
		
		final GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		final String[] availableFontFamilyNames = graphicsEnvironment.getAvailableFontFamilyNames();
		final String initialFontFamilyName = pref.get(PreferencesDialog.KEY_EDITOR_FONT, Font.SANS_SERIF);
		Arrays.stream(availableFontFamilyNames)
		      .sorted()
		      .forEach(comboRenderingFont::addItem);
		comboRenderingFont.setSelectedItem(initialFontFamilyName);
		
		
		final String initialEditorFontFamilyName = pref.get(PreferencesDialog.KEY_RENDERING_FONT, Font.MONOSPACED);
		Arrays.stream(availableFontFamilyNames)
		      .sorted()
		      .forEach(comboEditorFont::addItem);
		comboEditorFont.setSelectedItem(initialEditorFontFamilyName);
		
		final int pageSegMode = pref.getInt(PreferencesDialog.KEY_PAGE_SEG_MODE, DEFAULT_PSM_MODE);
		Arrays.stream(PSM_MODES)
		      .sorted()
		      .forEach(comboPageSegMode::addItem);
		comboPageSegMode.setSelectedItem(PSM_MODES[pageSegMode]);
	}

	private void addButtonPane(JPanel pane, GridBagConstraints constraints) {
		JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		JButton okButton = new JButton(new SaveAction());
		getRootPane().setDefaultButton(okButton);
		buttonPane.add(okButton);
		buttonPane.add(Box.createHorizontalStrut(5));
		buttonPane.add(new JButton(new CancelAction()));		
		constraints.gridx = 0;
		constraints.gridy += 1;
		constraints.fill = HORIZONTAL;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.gridwidth = REMAINDER;
		constraints.gridheight = REMAINDER;
		pane.add(buttonPane, constraints);
	}

	private class CancelAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		private CancelAction() {
			putValue(NAME, "Cancel");
			putValue(ACTION_COMMAND_KEY, "Cancel");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			PreferencesDialog.this.setState(ResultState.CANCEL);
			PreferencesDialog.this.dispose();
		}
	}

	private class SaveAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public SaveAction() {
			putValue(NAME, "Save");
			putValue(ACTION_COMMAND_KEY, "OK");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			PreferencesDialog.this.setState(ResultState.APPROVE);
			PreferencesDialog.this.dispose();
		}
	}

	private class SelectAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		private SelectAction() {
			putValue(NAME, "Select...");
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			final JFileChooser fc = new JFileChooser();
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

			try {
				final File currentDir = new File(tfLangdataDir.getText());
				if (currentDir.isDirectory()) {
					fc.setCurrentDirectory(currentDir);
				}
			} catch (Exception e) {
			}

			final int result = fc.showOpenDialog(PreferencesDialog.this);
			if (result == JFileChooser.APPROVE_OPTION) {
				tfLangdataDir.setText(fc.getSelectedFile().getAbsolutePath());
			}

		};
	}

	private void setState(ResultState state) {
		this.resultState = state;
	}

	public JTextField getTfLangdataDir() {
		return tfLangdataDir;
	}

	public JComboBox<String> getComboRenderingFont() {
		return comboRenderingFont;
	}

	public JComboBox<String> getComboEditorFont() {
		return comboEditorFont;
	}

	public int getPageSegmentationMode() {
		return comboPageSegMode.getSelectedIndex();
	}

	public ResultState showPreferencesDialog(Component parent) {
		setLocationRelativeTo(parent);
		setVisible(true);
		return resultState;
	}

	public Locale getSelectedLanguage() {
		return (Locale) comboLanguage.getSelectedItem();
	}

	public LookAndFeelInfo getSelectedLaf() {
		return (LookAndFeelInfo) comboLaf.getSelectedItem();
	}
}
