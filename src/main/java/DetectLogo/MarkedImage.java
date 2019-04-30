
package DetectLogo;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MarkedImage {

    private int index;
//    private BufferedImage image;
    private byte[] imageBytes;

    private int upperX;
    private int upperY;
    private int boxWidth;
    private int boxHeight;


    //private String containLogos;
    public MarkedImage(int idx, int upperX, int upperY, int bbWidth, int bbHeight) {
        this.index = idx;

        this.upperX = upperX;
        this.upperY = upperY;
        this.boxWidth = bbWidth;
        this.boxHeight = bbHeight;

    }



    public int getIndex() {
        return this.index;
    }

//    public BufferedImage getImage() {
//        return this.image;
//    }


    public int getUpperX() {
        return this.upperX;
    }

    public int getUpperY() {
        return this.upperY;
    }

    public int getBoxWidth() {
        return this.boxWidth;
    }

    public int getBoxHeight() {
        return this.boxHeight;
    }


    public void setUpperX(int x) {
        this.upperX = x;
    }

    public void setUpperY(int y) {
        this.upperY = y;
    }

    public void setBoxWidth(int w) {
        this.boxWidth = w;
    }

    public void settBoxHeight(int h) {
        this.boxHeight = h;
    }



    //    public String getContainLogos() {
//        return this.containLogos;
//    }

    public void setIndex(int idx) {
        this.index = idx;

    }




//    public void setContainLogos(String logos) {
//        this.containLogos = logos;
//    }

}