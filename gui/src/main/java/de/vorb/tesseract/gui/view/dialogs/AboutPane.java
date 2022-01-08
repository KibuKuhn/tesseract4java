package de.vorb.tesseract.gui.view.dialogs;

import java.awt.Component;

import javax.swing.JOptionPane;

import de.vorb.tesseract.gui.app.AppProxy;
import de.vorb.tesseract.gui.view.i18n.Labels;

public class AboutPane {

	public static void showDialog(Component parent) {
		String message = Labels.getLabel("about_message");
		Component parentComp = AppProxy.getApp().getView();
		JOptionPane.showMessageDialog(parentComp, new LinkPane(message), Labels.getLabel("about_title"),
				JOptionPane.INFORMATION_MESSAGE);
	}

}
