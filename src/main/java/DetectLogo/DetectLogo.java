package DetectLogo;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import com.google.api.gax.paging.Page;

import com.google.cloud.vision.v1.BoundingPoly;
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;

import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.EntityAnnotation;

import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Feature.Type;

import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.Vertex;
import com.google.common.io.Files;
import com.google.protobuf.ByteString;

import com.google.protobuf.util.JsonFormat;
import shot.Shot;
//import com.googlecode.objectify.impl.Path;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

//import com.google.cloud.storage.*;



public class DetectLogo {

    private final int WIDTH = 480;
    private final int HEIGHT = 270;

    private String folderPath;
    private String videoPath;

    private Queue<MarkedImage> marked;


    private Set<String> nonRepetLogo = new HashSet<>();
    private Queue<String> logos = new LinkedList<>();

    private Queue<Shot> adShots;

    public DetectLogo(String logoFolderPath, String videoPath, Queue<Shot>adShots) {
        this.folderPath = logoFolderPath;
        this.videoPath = videoPath;
        this.marked = new LinkedList<>();
        this.adShots = adShots;
    }


    public void Detecting( ) throws Exception {

        //ArrayList<EntityAnnotation> givenLogo = detectLogos("/Users/cindy/Documents/csci576Multimedia/Mcdonalds_logo.bmp");

        //BufferedImage image = ImageIO.read(new File("/Users/cindy/Documents/csci576Multimedia/test.jpg"));

//        int HEIGHT = 270;
//        int WIDTH = 480;

//        ArrayList<String> logoText = ReadLogo("/Users/cindy/Documents/csci576Multimedia/logo/");
//        File file = new File("/Users/cindy/Documents/csci576Multimedia/data_test1.rgb");

//        for (int j = 0; j<logoText.size(); j++) {
//            System.out.println(logoText.get(j));
//        }


        ArrayList<String> logoText = ReadLogo(this.folderPath);
        File file = new File(this.videoPath);

        RandomAccessFile raf = new RandomAccessFile(file, "r");
        BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);



//        ArrayList<MarkedImage> marked = new ArrayList<>();
//        ArrayList<String> logoList = new ArrayList<>();

        long frameLength = WIDTH*HEIGHT*3;

        byte[] bytes = new byte[(int) frameLength];

//        TODO: run from start
        int stepsize = 30;
        int idx= 0; //2065 0
        long offset = frameLength * 0;//2065 0
        int ind = 0;
        boolean detected = false;

        int readFrames = 0;


//        raf.length() frameLength * 2075
        while (offset <= raf.length()) {
            detected = false;
//
//            if( idx > 1000 )
//                System.out.println("index "+idx+"  "+(offset/frameLength));

            if( !adShots.isEmpty() && readFrames == adShots.peek().getStart() ) {
                Shot shot = adShots.poll();

                int frames = (shot.getEnd() - shot.getStart());
                readFrames += frames;

                offset += frames * frameLength;
                idx += frames;
            }

            ind = 0;
            raf.seek(offset);
            raf.read(bytes);

            readFrames++;
            for(int y = 0; y < HEIGHT; y++)
            {
                for(int x = 0; x < WIDTH; x++)
                {
                    byte a = 0;
                    byte r = bytes[ind];
                    byte g = bytes[ind+HEIGHT*WIDTH];
                    byte b = bytes[ind+HEIGHT*WIDTH*2];

                    int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
                    //int pix = ((a << 24) + (r << 16) + (g << 8) + b);

                    img.setRGB(x,y,pix);

                    ind++;

                }
            }

            //display(img);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write( img, "jpg", baos );
            baos.flush();
            byte[] jpgByte = baos.toByteArray();
            baos.close();

            ByteString imgBytes = ByteString.copyFrom(jpgByte);




            ArrayList<EntityAnnotation> logoInfo = detectLogos(imgBytes);
            String logoContent = "";
            int upperX = 0;
            int upperY = 0;
            int bbWidth = 0;
            int bbHeight = 0;
            //System.out.println("size: " + logoInfo.size());
            for (int i=0; i < logoInfo.size(); i++) {
                logoContent = logoInfo.get(i).getDescription();
                //System.out.println(logoContent);
                for (int n = 0; n < logoText.size(); n++) {
                    if (logoContent.contains(logoText.get(n))) {
                        detected = true;

                        String abbr = MappingName(logoText.get(n));
                        if( !nonRepetLogo.contains(abbr) ) {
                            nonRepetLogo.add(abbr);
                            logos.add(abbr);
                        }


                        BoundingPoly vertices = logoInfo.get(i).getBoundingPoly();
                        upperX = vertices.getVertices(0).getX();
                        upperY = vertices.getVertices(0).getY();
                        bbWidth = vertices.getVertices(2).getX() - upperX + 1;
                        bbHeight = vertices.getVertices(2).getY() - upperY + 1;

                        DrawBoundingBox(img,upperX,upperY,bbWidth,bbHeight);
                    }
                }
//                if (logoContent.contains("starbucks") || logoContent.contains("subway") || logoContent.contains("nfl") || logoContent.contains("mcdonalds")) {
//                    //System.out.println(i);

            }




            //display(img);
            if (detected) {
                MarkedImage mk = new MarkedImage(idx, upperX, upperY, bbWidth, bbHeight);
                this.marked.offer(mk);
            }


            offset = offset + frameLength * stepsize;
            //System.out.println(idx);
            idx = idx + stepsize;

        }

//      for (int j = 0; j<marked.size(); j++) {
//          System.out.println(marked.get(j).getIndex());
//          System.out.println(logoList.get(j));
//          display(marked.get(j).getImage());
//      }



    }


    public ArrayList<String> ReadLogo(String folderPath) throws Exception {
        ArrayList<String> logoText = new ArrayList<>();
        String logoDescription;

        File folder = new File(folderPath);
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if (file.isFile()) {
                if( !file.getName().endsWith(".bmp") ) continue;
                ByteString imgBytes = ByteString.readFrom(new FileInputStream(folderPath + file.getName()));
                ArrayList<EntityAnnotation> logoList = detectLogos(imgBytes);
                logoDescription = logoList.get(0).getDescription();
//                if(logoDescription.contains(" ")){
//                    logoDescription= logoDescription.substring(0, logoDescription.indexOf(" "));
//                }
                logoText.add(logoDescription);

            }
        }


        return logoText;
    }


    public void DrawBoundingBox(BufferedImage img,int x, int y, int width, int height) {
        Graphics g = img.getGraphics();
        g.setColor(Color.RED);
        g.drawRect(x, y, width, height);// upper-left x, y, width, height

    }

    public ArrayList<EntityAnnotation> detectLogos(ByteString imgBytes) throws Exception, IOException {


        ArrayList<EntityAnnotation> logoInfo = new ArrayList<>();

        List<AnnotateImageRequest> requests = new ArrayList<>();

        //ByteString imgBytes = ByteString.readFrom(new FileInputStream(filePath));

        Image img = Image.newBuilder().setContent(imgBytes).build();
        Feature feat = Feature.newBuilder().setType(Type.LOGO_DETECTION).build();
        AnnotateImageRequest request =
                AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
        requests.add(request);

        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();

            for (AnnotateImageResponse res : responses) {
                if (res.hasError()) {
                    System.out.printf("Error: %s\n", res.getError().getMessage());
                    return null;
                }

                // For full list of available annotations, see http://g.co/cloud/vision/docs
                for (EntityAnnotation annotation : res.getLogoAnnotationsList()) {

                    logoInfo.add(annotation);
                    // System.out.println(annotation.getDescription());
                    // System.out.println(annotation.getBoundingPoly());
                }
            }
        }

        return logoInfo;
    }

    public String MappingName (String logoName) {
        String fileName = "";

        if (logoName.contains("subway")) {
            fileName = "subway";
        }else if (logoName.contains("starbucks")) {
            fileName = "starbucks";
        }else if (logoName.contains("mcdonalds")) {
            fileName = "mcd";
        }else if (logoName.contains("nfl")) {
            fileName = "nfl";
        }else if (logoName.contains("american eagle")) {
            fileName = "ae";
        }else if (logoName.contains("hard rock cafe")) {
            fileName = "hrc";
        }
        return fileName;
    }








    public byte[] rgbToBmp(String filePath) throws Exception {

        byte[] rgbBytes = new byte[WIDTH*HEIGHT*3];

        File file = new File(filePath);

        RandomAccessFile raf = new RandomAccessFile(file, "r");

        BufferedImage rgbImg = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);

        raf.seek(0);

        raf.read(rgbBytes);

        int ind = 0;


        for(int y = 0; y < HEIGHT; y++)
        {
            for(int x = 0; x < WIDTH; x++)
            {
                byte a = 0;
                byte r = rgbBytes[ind];
                byte g = rgbBytes[ind+HEIGHT*WIDTH];
                byte b = rgbBytes[ind+HEIGHT*WIDTH*2];


                int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);


                //int pix = ((a << 24) + (r << 16) + (g << 8) + b)
                rgbImg.setRGB(x,y,pix);
                ind++;
            }
        }


        ByteArrayOutputStream bbaos = new ByteArrayOutputStream();
        ImageIO.write( rgbImg, "bmp", bbaos );

        bbaos.flush();

        byte[] bmpBytes = bbaos.toByteArray();
        bbaos.close();
        return bmpBytes;


    }

//    public void display(BufferedImage img) {
//        JFrame frame = new JFrame();
//        JPanel panel = new JPanel();
//
//        //frame.getContentPane().setLayout(BorderLayout.CENTER);
//
//        JLabel label1 = new JLabel(new ImageIcon(img));
//
//        panel.add(label1);
//        frame.getContentPane().add(panel,BorderLayout.CENTER);
//        frame.pack();
//        frame.setVisible(true);
//    }


    public Queue<MarkedImage> getIndexAndImage(){
        return this.marked;
    }


    public Queue<String> getLogos() {
        return logos;
    }
}
