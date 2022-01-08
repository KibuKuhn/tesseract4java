package de.vorb.tesseract.gui.view.dialogs;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class LinkPane extends JEditorPane {

	private static final Logger LOGGER = LoggerFactory.getLogger(LinkPane.class);

    private static final long serialVersionUID = 1L;

    LinkPane(String htmlBody) {
        super("text/html", "<html><body style=\"" + getStyle() + "\">" + htmlBody + "</body></html>");
        addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent event) {
                if (event.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
                    if (Desktop.isDesktopSupported()) {
                    	try {
							Desktop.getDesktop().browse(event.getURL().toURI());
						} catch (IOException | URISyntaxException ex) {
							LOGGER.error(ex.getMessage(), ex);
						}
                    }
                }
            }
        });
        setEditable(false);
        setBorder(null);
    }

    private static StringBuilder getStyle() {
        JLabel label = new JLabel();
        Font font = label.getFont();
        Color color = label.getBackground();
        StringBuilder style = new StringBuilder("font-family:" + font.getFamily() + ";");
        style.append("font-weight:" + (font.isBold() ? "bold" : "normal") + ";");
        style.append("font-size:" + font.getSize() + "pt;");
        style.append("background-color: rgb("+color.getRed()+","+color.getGreen()+","+color.getBlue()+");");
        return style;
    }
}
