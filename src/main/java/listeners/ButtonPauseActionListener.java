package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;

import readFiles.imageReader;
import readFiles.PlaySound;
import readFiles.videoPlayback;

import javax.swing.*;

public class ButtonPauseActionListener implements ActionListener {
    private readFiles.imageReader imageReader;
    private PlaySound playSound;
    private videoPlayback videoPlayback;


    public ButtonPauseActionListener(videoPlayback videoPlayback) {
        this.videoPlayback = videoPlayback;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        JButton btn = (JButton) e.getSource();
        this.imageReader = videoPlayback.getImageReader();
        this.playSound = videoPlayback.getPlaySound();

        imageReader.setNotPlay();
        playSound.setNotPlay();
//        imageReader.interrupt();

//        System.out.println("stop listener   " + imageReader.getState()+"    "+imageReader.isIfPlay());
    }
}
