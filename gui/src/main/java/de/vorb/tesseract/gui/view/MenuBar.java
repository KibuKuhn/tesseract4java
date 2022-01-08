package de.vorb.tesseract.gui.view;

import static java.awt.event.InputEvent.CTRL_DOWN_MASK;
import static java.awt.event.InputEvent.SHIFT_DOWN_MASK;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;

import de.vorb.tesseract.gui.app.AppProxy;
import de.vorb.tesseract.gui.event.IEventBus;
import de.vorb.tesseract.gui.event.MenuEvent;
import de.vorb.tesseract.gui.event.MenuEvents;
import de.vorb.tesseract.gui.view.i18n.Labels;

class MenuBar extends JMenuBar {

	private static final long serialVersionUID = 1L;
	private SaveBoxFile saveBoxFile;
	private OpenProjectDirectory openProjectDirectory;
	private BatchExport batchExport;
	private ImportTranscriptions importTranscriptions;
	private CloseProject closeProject;

	MenuBar() {
		initUI();
	}

	private void initUI() {


		JMenu file = new JMenu(Labels.getLabel("menu_file"));
		this.add(file);
		file.add(new NewProject());
		file.add(new OpenBoxFile());
		file.add(new JSeparator());
		file.add(saveBoxFile = new SaveBoxFile());
		file.add(openProjectDirectory = new OpenProjectDirectory());
		file.add(closeProject = new CloseProject());
		file.add(new JSeparator());
		file.add(importTranscriptions = new ImportTranscriptions());
		file.add(batchExport = new BatchExport());
		file.add(new JSeparator());
		file.add(new Exit());

		JMenu edit = new JMenu("Edit");
		this.add(edit);
		edit.add(new Preferences());

		JMenu tools = new JMenu("Tools");
		this.add(tools);
		tools.add(new CharacterHistogram());
		tools.add(new InspectUnicharset());
		tools.add(new TesseractTrainer());

		JMenu help = new JMenu(Labels.getLabel("menu_help"));
		this.add(help);
		help.add(new About());

	}

	private class NewProject extends AbstractAction {

		private static final long serialVersionUID = 1L;

		private NewProject() {
			putValue(NAME, "New Project");
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_N, CTRL_DOWN_MASK));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			IEventBus.get().post(new MenuEvent(MenuEvents.NewProject, e.getSource()));
		}
	}

	private class OpenBoxFile extends AbstractAction {

		private static final long serialVersionUID = 1L;

		private OpenBoxFile() {
			putValue(NAME, "Open Box File...");
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, CTRL_DOWN_MASK | SHIFT_DOWN_MASK));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			IEventBus.get().post(new MenuEvent(MenuEvents.OpenBoxFile, e.getSource()));
		}
	}

	private class SaveBoxFile extends AbstractAction {

		private static final long serialVersionUID = 1L;

		private SaveBoxFile() {
			putValue(NAME, "Save Box File");
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, CTRL_DOWN_MASK | SHIFT_DOWN_MASK));
			putValue(LARGE_ICON_KEY,new ImageIcon(TesseractFrame.class.getResource("/icons/table_save.png")));
			setEnabled(false);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			IEventBus.get().post(new MenuEvent(MenuEvents.SaveBoxFile, e.getSource()));
		}
	}

	private class OpenProjectDirectory extends AbstractAction {

		private static final long serialVersionUID = 1L;

		private OpenProjectDirectory() {
			putValue(NAME, "Open Project Directory");
			setEnabled(false);
			putValue(LARGE_ICON_KEY, new ImageIcon(TesseractFrame.class.getResource("/icons/folder_explore.png")));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			IEventBus.get().post(new MenuEvent(MenuEvents.OpenProjectDirectory, e.getSource()));
		}
	}

	private class CloseProject extends AbstractAction {

		private static final long serialVersionUID = 1L;

		private CloseProject() {
			putValue(NAME, "Close Project");
			setEnabled(false);
		}


		@Override
		public void actionPerformed(ActionEvent e) {
			IEventBus.get().post(new MenuEvent(MenuEvents.Close, e.getSource()));
		}
	}

	private class ImportTranscriptions extends AbstractAction {

		private static final long serialVersionUID = 1L;

		private ImportTranscriptions() {
			putValue(NAME, "Import Transcriptions...");
			setEnabled(false);
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_I, CTRL_DOWN_MASK | SHIFT_DOWN_MASK));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			IEventBus.get().post(new MenuEvent(MenuEvents.ImportTranscriptions, e.getSource()));
		}
	}

	private class BatchExport extends AbstractAction {

		private static final long serialVersionUID = 1L;

		private BatchExport() {
			putValue(NAME, "Batch Export...");
			setEnabled(false);
			putValue(LARGE_ICON_KEY, new ImageIcon(TesseractFrame.class.getResource("/icons/book_next.png")));
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_E, CTRL_DOWN_MASK | SHIFT_DOWN_MASK));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			IEventBus.get().post(new MenuEvent(MenuEvents.Batch, e.getSource()));
		}
	}

	private class Exit extends AbstractAction {

		private static final long serialVersionUID = 1L;

		private Exit() {
			putValue(NAME, "Exit");
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Q, CTRL_DOWN_MASK));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			IEventBus.get().post(new MenuEvent(MenuEvents.Exit, e.getSource()));
		}
	}

	private class Preferences extends AbstractAction {

		private static final long serialVersionUID = 1L;

		private Preferences() {
			putValue(NAME, "Preferences");
			putValue(LARGE_ICON_KEY, new ImageIcon(TesseractFrame.class.getResource("/icons/cog.png")));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			IEventBus.get().post(new MenuEvent(MenuEvents.Preferences, e.getSource()));
		}
	}

	private class CharacterHistogram extends AbstractAction {

		private static final long serialVersionUID = 1L;

		private CharacterHistogram() {
			putValue(NAME, "Character Histogram...");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			IEventBus.get().post(new MenuEvent(MenuEvents.CharacterHistogram, e.getSource()));
		}

	}

	private class InspectUnicharset extends AbstractAction {

		private static final long serialVersionUID = 1L;

		private InspectUnicharset() {
			putValue(NAME, "Debug Unicharset...");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			IEventBus.get().post(new MenuEvent(MenuEvents.InspectUnicharset, e.getSource()));
		}

	}

	private class TesseractTrainer extends AbstractAction {

		private static final long serialVersionUID = 1L;

		private TesseractTrainer() {
			putValue(NAME, "Tesseract Trainer...");
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_T, CTRL_DOWN_MASK | SHIFT_DOWN_MASK));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			IEventBus.get().post(new MenuEvent(MenuEvents.TesseractTrainer, e.getSource()));
		}
	}

	private class About extends AbstractAction {

		private static final long serialVersionUID = 1L;

		private About() {
			putValue(NAME, Labels.getLabel("menu_about"));
			putValue(LARGE_ICON_KEY, new ImageIcon(TesseractFrame.class.getResource("/icons/information.png")));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			IEventBus.get().post(new MenuEvent(MenuEvents.About, e.getSource()));
		}

	}

	void onApplicationModeChanged(boolean boxFileEnabled, boolean projectEnabled) {
		this.saveBoxFile.setEnabled(boxFileEnabled);
		this.openProjectDirectory.setEnabled(projectEnabled);
		this.batchExport.setEnabled(projectEnabled);
		this.importTranscriptions.setEnabled(projectEnabled);
		this.closeProject.setEnabled(projectEnabled);
	}
}
