package de.vorb.tesseract.gui.view;

import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.RELATIVE;
import static java.awt.GridBagConstraints.REMAINDER;
import static java.awt.GridBagConstraints.WEST;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.function.Supplier;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import de.vorb.tesseract.gui.model.PageModel;
import de.vorb.tesseract.gui.model.Scale;
import de.vorb.tesseract.gui.view.renderer.RecognitionRenderer;

class RecognitionCheckboxPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JCheckBox cbWordBoxes;
	private JCheckBox cbSymbolBoxes;
	private JCheckBox cbLineNumbers;
	private JCheckBox cbBaselines;
	private JCheckBox cbBlocks;
	private JCheckBox cbParagraphs;
	private JButton btZoomOut;
	private JButton btZoomIn;
	private Scale scale;
	private Supplier<PageModel> pageModelSupplier;
	private RecognitionRenderer renderer;
	private Runnable renderCallback;

	RecognitionCheckboxPanel(Scale scale, Supplier<PageModel> pageModelSupplier, Runnable renderCallback,
			RecognitionRenderer renderer) {
		this.scale = scale;
		this.pageModelSupplier = pageModelSupplier;
		this.renderCallback = renderCallback;
		this.renderer = renderer;

		initUI();

	}

	private void initUI() {
		this.setBorder(BorderFactory.createEmptyBorder(0, 4, 4, 4));
		this.setLayout(new GridBagLayout());
		
		CheckBoxListener checkBoxListener = new CheckBoxListener();
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = WEST;
		constraints.insets = new Insets(0, 0, 0, 5);
		constraints.gridx = 0;
		constraints.gridy = REMAINDER;	
		constraints.weightx = 0;
		constraints.weighty = 0;
		
		cbWordBoxes = new JCheckBox("Word boxes");
		this.add(cbWordBoxes, constraints);
		cbWordBoxes.setSelected(true);
		cbWordBoxes.addItemListener(checkBoxListener);

		constraints.gridx = RELATIVE;
		cbSymbolBoxes = new JCheckBox("Symbol boxes");
		this.add(cbSymbolBoxes, constraints);
		cbSymbolBoxes.setSelected(false);
		cbSymbolBoxes.addItemListener(checkBoxListener);

		cbLineNumbers = new JCheckBox("Line numbers");
		this.add(cbLineNumbers, constraints);
		cbLineNumbers.setSelected(true);
		cbLineNumbers.addItemListener(checkBoxListener);

		cbBaselines = new JCheckBox("Baseline");		
		this.add(cbBaselines, constraints);
		cbBaselines.setSelected(false);
		cbBaselines.addItemListener(checkBoxListener);

		cbBlocks = new JCheckBox("Blocks");		
		this.add(cbBlocks, constraints);
		cbBlocks.addItemListener(checkBoxListener);

		cbParagraphs = new JCheckBox("Paragraphs");		
		this.add(cbParagraphs, constraints);
		cbParagraphs.addItemListener(checkBoxListener);
		
		this.add(Box.createHorizontalStrut(20), constraints);

		btZoomOut = new JButton(new ZoomOutAction());
		Insets buttonMargin = new Insets(2, 4, 2, 4);
		btZoomOut.setMargin(buttonMargin);
		btZoomOut.setToolTipText("Zoom out");		
		this.add(btZoomOut, constraints);

		btZoomIn = new JButton(new ZoomInAction());
		btZoomIn.setMargin(buttonMargin);
		btZoomIn.setToolTipText("Zoom in");		
		this.add(btZoomIn, constraints);
		
		constraints.fill = BOTH;
		constraints.gridwidth = REMAINDER;
		constraints.weightx = 1;
		constraints.weighty = 1;
		this.add(Box.createGlue(), constraints);
	}

	private class ZoomOutAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		private ZoomOutAction() {
			this.putValue(LARGE_ICON_KEY, new ImageIcon(getClass().getResource("/icons/zoom_out.png")));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (scale.hasPrevious()) {
				renderer.render(pageModelSupplier.get(), scale.previous());
			}

			if (!scale.hasPrevious()) {
				btZoomOut.setEnabled(false);
			}

			btZoomIn.setEnabled(true);
		}
	}

	private class ZoomInAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		private ZoomInAction() {
			this.putValue(LARGE_ICON_KEY, new ImageIcon(getClass().getResource("/icons/zoom_in.png")));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (scale.hasNext()) {
				renderer.render(pageModelSupplier.get(), scale.next());
			}

			if (!scale.hasPrevious()) {
				btZoomIn.setEnabled(false);
			}

			btZoomOut.setEnabled(true);
		}
	}
	
	private class CheckBoxListener implements ItemListener {
		
		@Override
		public void itemStateChanged(ItemEvent ev) {
			if (cbWordBoxes == ev.getSource() && cbWordBoxes.isSelected()) {
				  cbSymbolBoxes.setSelected(false);
			} else if (cbSymbolBoxes == ev.getSource() &&  cbSymbolBoxes.isSelected()) {
				cbWordBoxes.setSelected(false);
			}

			renderCallback.run();
		}
	};

	JCheckBox getSymbolBoxesCheckbox() {
		return cbSymbolBoxes;
	}

	JCheckBox getWordBoxesCheckbox() {
		return cbWordBoxes;
	}

	JCheckBox getLineNumbersCheckbox() {
		return this.cbLineNumbers;
	}

	JCheckBox getBaselinesCheckbox() {
		return this.cbBaselines;
	}

	JCheckBox getBlocksCheckbox() {
		return this.cbBlocks;
	}

	JCheckBox getParagraphsCheckbox() {
		return this.cbParagraphs;
	}
}
