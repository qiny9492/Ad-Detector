
package DetectLogo;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MarkedImage {

    private int index;
    private BufferedImage image;
    private byte[] imageBytes;

    private int upperX;
    private int upperY;
    private int boxWidth;
    private int boxHeight;


    //private String containLogos;
    public MarkedImage(int idx, BufferedImage img, int upperX, int upperY, int bbWidth, int bbHeight) {
        this.index = idx;

        this.upperX = upperX;
        this.upperY = upperY;
        this.boxWidth = bbWidth;
        this.boxHeight = bbHeight;

        //this.image = img;

        this.image = new BufferedImage(img.getWidth(),
                img.getHeight(), img.getType());
        Graphics2D g2d = this.image.createGraphics();
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();


        //this.containLogos = containLogos;
    }



    public int getIndex() {
        return this.index;
    }

    public BufferedImage getImage() {
        return this.image;
    }

    private void BufferedImageToBytes() {

        imageBytes = new byte[image.getWidth() * image.getHeight() * 3];

        int idx = 0;
        for( int j = 0; j < image.getHeight(); j++ ) {
            for( int i = 0; i < image.getWidth(); i++ ) {
                int pixel = image.getRGB(i, j);


                byte r = Integer.valueOf(((pixel >> 16) & 0xff)).byteValue();
                byte g = Integer.valueOf((pixel >> 8) & 0xff).byteValue();
                byte b = Integer.valueOf((pixel) & 0xff).byteValue();

                int step = image.getHeight() * image.getWidth();
                imageBytes[idx] = r;
                imageBytes[idx + step] = g;
                imageBytes[idx + step * 2] = b;
                idx++;
            }
        }
    }

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



    public byte[] getImageBytes() {
        BufferedImageToBytes();
        return imageBytes;
    }

    //    public String getContainLogos() {
//        return this.containLogos;
//    }

    public void setIndex(int idx) {
        this.index = idx;

    }

    public void setImage(BufferedImage img) {

        this.image = new BufferedImage(img.getWidth(),
                img.getHeight(), img.getType());
        Graphics2D g2d = this.image.createGraphics();
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();
    }


    public void display() {
        JFrame frame = new JFrame();
        JPanel panel = new JPanel();

        //frame.getContentPane().setLayout(BorderLayout.CENTER);

        JLabel label1 = new JLabel(new ImageIcon(image));

        panel.add(label1);
        frame.getContentPane().add(panel,BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }


//    public void setContainLogos(String logos) {
//        this.containLogos = logos;
//    }

}