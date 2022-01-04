package de.vorb.tesseract.gui.view;

import javax.swing.JComponent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.util.Objects;


public class Canvas extends JComponent {
    private static final long serialVersionUID = 1L;

    private static final Dimension DIM_EMPTY = new Dimension(0, 0);

    private Image image;

    public Canvas() {
    }

    public void setImage(Image image) {
        this.image = image;
    }

    @Override
    public void paintComponent(Graphics g) {
        final Rectangle rect = getVisibleRect();

        if (Objects.nonNull(image)) {
            g.setClip(rect.x, rect.y, rect.width, rect.height);
            g.drawImage(image, 0, 0, null);
        } else {
            g.setColor(Color.WHITE);
            g.fillRect((int) rect.getX(), (int) rect.getY(),
                    (int) rect.getWidth(), (int) rect.getHeight());
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return image == null ? DIM_EMPTY : new Dimension(image.getWidth(null), image.getHeight(null));
    }
}
