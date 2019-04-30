package listeners;

import readFiles.PlaySound;
import readFiles.imageReader;
import readFiles.videoPlayback;
import shot.Shot;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Queue;

public class ButtonPlayActionListener implements ActionListener {
    private readFiles.imageReader imageReader;
    private PlaySound playSound;
    private videoPlayback videoPlayback;

    public ButtonPlayActionListener(videoPlayback videoPlayback) {
        this.videoPlayback = videoPlayback;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton btn = (JButton) e.getSource();

        PlaySound pSound = videoPlayback.getPlaySound();

        playSound = new PlaySound();
        playSound.setIfRemoveAd(pSound.isIfRemoveAd());
        playSound.setAdShots(pSound.getAdShots());
        playSound.setCurShot(pSound.getCurShot());

        playSound.setDataLine(pSound.getDataLine());
        playSound.setAudioInputStream(pSound.getAudioInputStream());
        playSound.setSampleSkipped(pSound.getSampleSkipped());

        imageReader = videoPlayback.getImageReader();
        JLabel lbIm = imageReader.getLbIm();
        InputStream is = imageReader.getIs();

        long fileLength = imageReader.getNumFrames();
        long frameCounter = imageReader.getFrameCounter();

        boolean ifRemoveAd = imageReader.isIfRemoveAd();
        Queue<Shot> adShots = imageReader.getAdShots();
        Shot shot = imageReader.getShot();
        long FramesSkipped = imageReader.getFramesSkipped();

        imageReader = new imageReader(pSound, lbIm, is, fileLength, frameCounter);
        imageReader.setShot(shot);
        imageReader.setIfRemoveAd(ifRemoveAd);
        imageReader.setAdShots(adShots);
        imageReader.setFramesSkipped(FramesSkipped);

        videoPlayback.setImageReader(imageReader);
        videoPlayback.setPlaySound(playSound);


        imageReader.setPlay();
        playSound.setPlay();

        videoPlayback.startVedio();
        videoPlayback.startAudio();



//        System.out.println("play Listener   "+playSound.getState()+"  "+playSound.isIfplay());

//        imageReader.start();
//        playSound.run();
    }
}
