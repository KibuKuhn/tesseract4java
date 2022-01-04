package de.vorb.tesseract.gui.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.MouseInputAdapter;

import de.vorb.tesseract.gui.model.BoxFileModel;
import de.vorb.tesseract.gui.model.PageModel;
import de.vorb.tesseract.gui.model.Scale;
import de.vorb.tesseract.gui.view.renderer.RecognitionRenderer;
import de.vorb.tesseract.util.AlternativeChoice;
import de.vorb.tesseract.util.FontAttributes;
import de.vorb.tesseract.util.Symbol;
import de.vorb.tesseract.util.Word;

public class RecognitionPane extends JPanel implements PageModelComponent {
	private static final long serialVersionUID = 1L;
	private static final int SCROLL_UNITS = 12;

	private final RecognitionRenderer renderer;
	private final Scale scale;

	private JLabel lblOriginal_1;
	private final JLabel lblOriginal;
	private JLabel lblRecognition_1;
	private final JLabel lblRecognition;
	private PageModel model;
	private final Timer delayer = new Timer(true);
	private RecognitionCheckboxPanel checkboxPanel;
	
	public RecognitionPane(Scale scale, final String renderingFont) {
		setLayout(new BorderLayout(0, 0));

		renderer = new RecognitionRenderer(this, renderingFont);
		this.scale = scale;

		JSplitPane splitPane = new JSplitPane();
		splitPane.setBackground(Color.WHITE);
		splitPane.setOneTouchExpandable(true);
		splitPane.setEnabled(false);
		splitPane.setResizeWeight(0.5);
		add(splitPane, BorderLayout.CENTER);

		final JScrollPane spOriginal = new JScrollPane();
		spOriginal.getHorizontalScrollBar().setUnitIncrement(SCROLL_UNITS);
		spOriginal.getVerticalScrollBar().setUnitIncrement(SCROLL_UNITS);
		splitPane.setLeftComponent(spOriginal);

		lblOriginal = new JLabel();
		lblOriginal.setVerticalAlignment(SwingConstants.TOP);
		spOriginal.setViewportView(lblOriginal);

		lblOriginal_1 = new JLabel("Original");
		lblOriginal_1.setBorder(new EmptyBorder(0, 4, 0, 0));
		spOriginal.setColumnHeaderView(lblOriginal_1);

		final JScrollPane spHOCR = new JScrollPane();
		spHOCR.getHorizontalScrollBar().setUnitIncrement(SCROLL_UNITS);
		spHOCR.getVerticalScrollBar().setUnitIncrement(SCROLL_UNITS);
		splitPane.setRightComponent(spHOCR);

		lblRecognition = new JLabel();
		lblRecognition.setVerticalAlignment(SwingConstants.TOP);
		spHOCR.setViewportView(lblRecognition);

		final JPopupMenu popupMenu = new JPopupMenu();
		final JMenuItem glyph = new JMenuItem("Show in Box Editor");
		popupMenu.add(glyph);

		final MouseInputAdapter adapter = new MouseInputAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				showPopup(e);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				showPopup(e);
			}

			private void showPopup(MouseEvent e) {
				if (!e.isPopupTrigger()) {
					return;
				}

				if (checkboxPanel.getSymbolBoxesCheckbox().isSelected()) {
					final Symbol symbol = findSymbolAt(e.getX(), e.getY());

					if (Objects.nonNull(symbol)) {
//						final Symbol s = symbol;
						popupMenu.removeAll();
						popupMenu.add(String.format("Alternative choices for symbol \"%s\" (confidence = %.2f%%):",
								symbol.getText(), symbol.getConfidence()));
						popupMenu.add(new JSeparator());

						for (AlternativeChoice alt : symbol.getAlternatives()) {
							popupMenu.add(String.format("- \"%s\" (confidence = %.2f%%)", alt.getText(),
									alt.getConfidence()));
						}

						popupMenu.show(e.getComponent(), e.getX(), e.getY());
					}
				} else if (checkboxPanel.getWordBoxesCheckbox().isSelected()) {
					final Word word = findWordAt(e.getX(), e.getY());

					if (Objects.nonNull(word)) {				

						popupMenu.removeAll();
						popupMenu.add(String.format("Word confidence = %.2f%%", word.getConfidence()));
						popupMenu.add(new JSeparator());
						final FontAttributes fa = word.getFontAttributes();
						popupMenu.add(String.format("Font ID = %d", fa.getFontID()));
						popupMenu.add(String.format("Font size = %dpx", fa.getSize()));

						if (fa.isBold()) {
							popupMenu.add("Bold");
						}
						if (fa.isItalic()) {
							popupMenu.add("Italic");
						}
						if (fa.isSerif()) {
							popupMenu.add("Serif");
						}
						if (fa.isMonospace()) {
							popupMenu.add("Monospace");
						}
						if (fa.isSmallCaps()) {
							popupMenu.add("Small Caps");
						}
						if (fa.isUnderlined()) {
							popupMenu.add("Underlined");
						}

						popupMenu.show(e.getComponent(), e.getX(), e.getY());
					}
				}
			}
		};

		lblRecognition.addMouseListener(adapter);
		lblOriginal.addMouseListener(adapter);

		lblRecognition_1 = new JLabel("Recognition Result");
		lblRecognition_1.setBorder(new EmptyBorder(0, 4, 0, 0));
		spHOCR.setColumnHeaderView(lblRecognition_1);

		checkboxPanel = new RecognitionCheckboxPanel(scale, this::getPageModel, this::render, renderer);
		add(checkboxPanel, BorderLayout.NORTH);
		
		// comboFont.setModel(new DefaultComboBoxModel<String>(new String[] {
		// "Antiqua", "Fraktur" }));

		spOriginal.getViewport().addChangeListener(e -> {
			spHOCR.getHorizontalScrollBar().setModel(spOriginal.getHorizontalScrollBar().getModel());
			spHOCR.getVerticalScrollBar().setModel(spOriginal.getVerticalScrollBar().getModel());
		});

		spHOCR.getViewport().addChangeListener(e -> {
			spOriginal.getHorizontalScrollBar().setModel(spHOCR.getHorizontalScrollBar().getModel());
			spOriginal.getVerticalScrollBar().setModel(spHOCR.getVerticalScrollBar().getModel());
		});
	}

	private Symbol findSymbolAt(int x, int y) {
		if (Objects.isNull(model)) {
			return null;
		}

		final Iterator<Symbol> symbolIt = model.getPage().symbolIterator();
		while (symbolIt.hasNext()) {
			final Symbol symb = symbolIt.next();
			final de.vorb.tesseract.util.Box bbox = symb.getBoundingBox();

			final int scaledX0 = Scale.scaled(bbox.getX(), scale.current());
			final int scaledY0 = Scale.scaled(bbox.getY(), scale.current());
			final int scaledX1 = scaledX0 + Scale.scaled(bbox.getWidth(), scale.current());
			final int scaledY1 = scaledY0 + Scale.scaled(bbox.getHeight(), scale.current());

			if (x >= scaledX0 && x <= scaledX1 && y >= scaledY0 && y <= scaledY1) {
				return symb;
			}
		}

		return null;
	}

	private Word findWordAt(int x, int y) {
		if (Objects.isNull(model)) {
			return null;
		}

		final Iterator<Word> wordIt = model.getPage().wordIterator();

		while (wordIt.hasNext()) {
			final Word word = wordIt.next();
			final de.vorb.tesseract.util.Box bbox = word.getBoundingBox();

			final int scaledX0 = Scale.scaled(bbox.getX(), scale.current());
			final int scaledY0 = Scale.scaled(bbox.getY(), scale.current());
			final int scaledX1 = scaledX0 + Scale.scaled(bbox.getWidth(), scale.current());
			final int scaledY1 = scaledY0 + Scale.scaled(bbox.getHeight(), scale.current());

			if (x >= scaledX0 && x <= scaledX1 && y >= scaledY0 && y <= scaledY1) {
				return word;
			}
		}

		return null;
	}

	public PageModel getPageModel() {
		return model;
	}

	public void setPageModel(PageModel model) {
		this.model = model;
		render();
	}

	public void render() {
		delayer.purge();

		delayer.schedule(new TimerTask() {
			@Override
			public void run() {
				renderer.render(model, scale.current());
			}
		}, 200);
	}

	@Override
	public Component asComponent() {
		return this;
	}

	@Override
	public void freeResources() {
		lblOriginal.setIcon(null);
		lblRecognition.setIcon(null);
	}

	@Override
	public BoxFileModel getBoxFileModel() {
		if (!Objects.isNull(model)) {
			return model.toBoxFileModel();
		} else {
			return null;
		}
	}

	public JLabel getCanvasOriginal() {
		return lblOriginal;
	}

	public JLabel getCanvasRecognition() {
		return lblRecognition;
	}

	public JCheckBox getWordBoxes() {
		return this.checkboxPanel.getWordBoxesCheckbox();
	}

	public JCheckBox getSymbolBoxes() {
		return checkboxPanel.getSymbolBoxesCheckbox();
	}

	public JCheckBox getLineNumbers() {
		return checkboxPanel.getLineNumbersCheckbox();
	}

	public JCheckBox getBaselines() {
		return checkboxPanel.getBaselinesCheckbox();
	}

	public JCheckBox getBlocks() {
		return checkboxPanel.getBlocksCheckbox();
	}

	public JCheckBox getParagraphs() {
		return checkboxPanel.getParagraphsCheckbox();
	}

	public void setRenderingFont(String renderingFont) {
		renderer.setRenderingFont(renderingFont);
	}
}
