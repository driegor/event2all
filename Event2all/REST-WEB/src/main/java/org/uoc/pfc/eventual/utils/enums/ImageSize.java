package org.uoc.pfc.eventual.utils.enums;

public enum ImageSize {

    THUMBNAIL(240, 320, Boolean.TRUE), MEDIUM_MOBILE(480, 640, Boolean.TRUE), MEDIUM_WEB(600, 800, Boolean.TRUE), HD(768, 1024, Boolean.TRUE);

    int     height;
    int     width;
    boolean HQ;

    ImageSize(int height, int weight, boolean HQ) {
	this.height = height;
	width = weight;
	this.HQ = HQ;
    }

    public int getHeight() {
	return height;
    }

    public int getWidth() {
	return width;
    }

    public boolean isHQ() {
	return HQ;
    }

    public void setHQ(boolean hQ) {
	HQ = hQ;
    }

    public void setHeight(int height) {
	this.height = height;
    }

    public void setWidth(int width) {
	this.width = width;
    }

}
