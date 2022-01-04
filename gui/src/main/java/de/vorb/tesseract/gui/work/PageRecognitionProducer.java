package de.vorb.tesseract.gui.work;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;

import javax.annotation.CheckForNull;
import javax.imageio.ImageIO;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.leptonica.PIX;
import org.bytedeco.leptonica.global.lept;
import org.bytedeco.tesseract.INT_FEATURE_STRUCT;
import org.bytedeco.tesseract.TBLOB;
import org.bytedeco.tesseract.global.tesseract;

import de.vorb.tesseract.gui.app.TesseractApp;
import de.vorb.tesseract.tools.recognition.RecognitionProducer;
import de.vorb.tesseract.util.feat.Feature3D;


public class PageRecognitionProducer extends RecognitionProducer {
    private final Path tessdataDir;
    private PIX lastPix;

    private final TesseractApp controller;
    private final HashMap<String, String> variables = new HashMap<>();
    private int pageSegmentationMode = tesseract.PSM_AUTO;

    public PageRecognitionProducer(TesseractApp controller,
            Path tessdataDir, String trainingFile) {
        super(trainingFile);

        this.controller = controller;
        this.tessdataDir = tessdataDir;

        // save choices for choice iterator
        variables.put("save_blob_choices", "T");

        // heavy noise reduction
        // variables.put("textord_heavy_nr", "T");

        // language_model_penalty_non_dict_word
        variables.put("language_model_penalty_non_dict_word", "0.3");

        // blacklist doesn't work
        // variables.put("tessedit_char_blacklist", "=§«°·»¼ÃÆØå¼½æàâèéøɔ$");
    }

    @Override
    public void init() throws IOException {
        setHandle(tesseract.TessBaseAPICreate());

        reset();
    }

    @Override
    public void reset() throws IOException {
        // init LibTess with data path, language and OCR engine mode
        tesseract.TessBaseAPIInit2(getHandle(),
                tessdataDir.toString(),
                getTrainingFile(),
                tesseract.OEM_DEFAULT);

        // set page segmentation mode
        tesseract.TessBaseAPISetPageSegMode(getHandle(), pageSegmentationMode);

        // set variables
        for (Entry<String, String> var : variables.entrySet()) {
            tesseract.TessBaseAPISetVariable(getHandle(), var.getKey(), var.getValue());
        }
    }

    @Override
    public void close() throws IOException {
        tesseract.TessBaseAPIDelete(getHandle());
    }

    public void setPageSegmentationMode(int pageSegmentationMode) {
        this.pageSegmentationMode = pageSegmentationMode;
        tesseract.TessBaseAPISetPageSegMode(getHandle(), pageSegmentationMode);
    }

    public void loadImage(Path imageFile) {
        if (Objects.nonNull(lastPix)) {
            // destroy old pix
            lept.pixDestroy(lastPix);
        }

        final PIX pix = lept.pixRead(imageFile.toString());

        tesseract.TessBaseAPISetImage2(getHandle(), pix);

        lastPix = pix;
    }

    @CheckForNull
    public PIX getImage() {
        return lastPix;
    }

    @CheckForNull
    public PIX getThresholdedImage() {
        return tesseract.TessBaseAPIGetThresholdedImage(getHandle());
    }

    public List<Feature3D> getFeaturesForSymbol(BufferedImage symbol) {
        if (Objects.isNull(lastPix)) {
            return Collections.emptyList();
        }

        final int padding = 5;
        // draw a 5px white padding around the symbol
        final BufferedImage symbolWithPadding = new BufferedImage(
                symbol.getWidth() + padding + padding,
                symbol.getHeight() + padding + padding,
                BufferedImage.TYPE_BYTE_BINARY);

        // draw the symbol on the new image
        final Graphics2D g2d = symbolWithPadding.createGraphics();
        g2d.setBackground(Color.WHITE);
        g2d.clearRect(0, 0, symbolWithPadding.getWidth(),
                symbolWithPadding.getHeight());
        g2d.drawImage(symbol, padding, padding, null);
        g2d.dispose();

        // FIXME
        if (Objects.isNull(controller.getProjectModel())) {
            return Collections.emptyList();
        }

        final String symbolFile = controller.getProjectModel().getProjectDir().resolve(
                "symbol.png").toString();
        try {
            ImageIO.write(symbolWithPadding, "PNG", new File(symbolFile));
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }

        try (final PIX pixSymbol = lept.pixRead(symbolFile);
             final IntPointer numFeatures = new IntPointer(1);
             final IntPointer outlineIndexes = new IntPointer(512);
             final BytePointer features = new BytePointer(4 * 512);
             final INT_FEATURE_STRUCT intFeatures = new INT_FEATURE_STRUCT(features)) {

            final TBLOB blob = tesseract.TessMakeTBLOB(pixSymbol);

            lept.pixDestroy(pixSymbol);

            tesseract.TessBaseAPIGetFeaturesForBlob(getHandle(), blob, intFeatures, numFeatures, outlineIndexes);

            // make a list of Features3D
            final ArrayList<Feature3D> featureList = new ArrayList<>(numFeatures.get());

            for (int i = 0; i < numFeatures.get(); i++) {
                final int x = features.get(i * 4) & 0xFF;
                final int y = features.get(i * 4 + 1) & 0xFF;
                final int theta = features.get(i * 4 + 2) & 0xFF;
                final byte cpMisses = features.get(i * 4 + 3);
                final int outlineIndex = outlineIndexes.get(i);

                final Feature3D feat = new Feature3D(x, y, theta, cpMisses, outlineIndex);

                featureList.add(feat);
            }

            return featureList;
        }
    }

    public void setVariable(String key, String value) {
        variables.put(key, value);
    }
}
