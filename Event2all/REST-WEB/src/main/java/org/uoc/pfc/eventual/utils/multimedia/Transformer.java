package org.uoc.pfc.eventual.utils.multimedia;

import static org.imgscalr.Scalr.OP_ANTIALIAS;
import static org.imgscalr.Scalr.OP_BRIGHTER;
import static org.imgscalr.Scalr.pad;
import static org.imgscalr.Scalr.resize;
import static org.imgscalr.Scalr.Mode.FIT_TO_HEIGHT;

import java.awt.Color;
import java.awt.image.BufferedImage;

import org.imgscalr.Scalr.Method;
import org.uoc.pfc.eventual.utils.enums.ImageSize;

public class Transformer {

    public static BufferedImage resizeImage(BufferedImage img, ImageSize imgSize) {
	return resizeImage(img, imgSize, null);
    }

    public static BufferedImage resizeImage(BufferedImage img, ImageSize imgSize, Color color) {
	img = resize(img, imgSize.isHQ() ? Method.ULTRA_QUALITY : Method.ULTRA_QUALITY, FIT_TO_HEIGHT, imgSize.getWidth(), imgSize.getHeight(), OP_ANTIALIAS,
		OP_BRIGHTER);
	return color != null ? pad(img, 4, color) : img;
    }

}
