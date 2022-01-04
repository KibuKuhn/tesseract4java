package de.vorb.tesseract.gui.view.renderer;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Objects;

import javax.swing.ImageIcon;

import de.vorb.tesseract.gui.model.PageModel;
import de.vorb.tesseract.gui.model.Scale;
import de.vorb.tesseract.gui.view.EvaluationPane;

public class EvaluationPaneRenderer implements PageRenderer {
    private final EvaluationPane evaluationPane;

    public EvaluationPaneRenderer(EvaluationPane evaluationPane) {
        this.evaluationPane = evaluationPane;
    }

    @Override
    public void render(PageModel pageModel, float scale) {
        boolean modelPresent = !Objects.isNull(pageModel);

        evaluationPane.getSaveTranscriptionButton().setEnabled(modelPresent);
        evaluationPane.getGenerateReportButton().setEnabled(modelPresent);
        evaluationPane.getTextAreaTranscript().setEnabled(modelPresent);

        if (modelPresent) {
            final BufferedImage img =
                    pageModel.getImageModel().getSourceImage();

            final int w = Scale.scaled(img.getWidth(), scale);
            final int h = Scale.scaled(img.getHeight(), scale);

            evaluationPane.getOriginal().setIcon(
                    new ImageIcon(img.getScaledInstance(w, h,
                            Image.SCALE_SMOOTH)));
            evaluationPane.getTextAreaTranscript().setText(
                    pageModel.getTranscription());
        } else {
            evaluationPane.getOriginal().setIcon(null);
            evaluationPane.getTextAreaTranscript().setText("");
        }
    }
}
