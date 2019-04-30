package readFiles;// CSCI 576 Final Project
// File:        videoPlayback.java
// Programmers: Christopher Mangus, Louis Schwartz

import listeners.ButtonPauseActionListener;
import listeners.ButtonPlayActionListener;
import listeners.ButtonStopActionListener;
import javax.sound.sampled.DataLine.Info;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;

import AnalyzeAd.DetectAd;
import shot.Shot;

public class videoPlayback {
	private imageReader imageReader;
	private final int EXTERNAL_BUFFER_SIZE = 3200;
	private Queue<Shot> adShots;


	public void setAdShots(Queue<Shot> adShots) {
		this.adShots = adShots;
	}

	public readFiles.imageReader getImageReader() {
		return imageReader;
	}

	public PlaySound getPlaySound() {
		return playSound;
	}

	private PlaySound playSound;

	public void setImageReader(readFiles.imageReader imageReader) {
		this.imageReader = imageReader;
	}

	public void setPlaySound(PlaySound playSound) {
		this.playSound = playSound;
	}

	public videoPlayback(imageReader imageReader, PlaySound playSound) {
		this.imageReader = imageReader;
		this.playSound = playSound;
	}

    public static void main(String[] args) {
		try {
			// get the command line parameters
			if (args.length < 2) {
				System.err.println("usage: java videoPlayback video.rgb audio.wav");
			return;
			}
			String vfilename = args[0];
			String afilename = args[1];

			boolean ifRemoveAd = false;



			// initializes the playSound and imageReader Objects
			PlaySound playSound = new PlaySound();

			File file = new File(vfilename);
			InputStream is = new FileInputStream(file);

			JLabel lbIm = new JLabel();
			lbIm.setPreferredSize(new Dimension(480, 270));

			long numFrames = file.length()/(480 * 270 * 3);
			imageReader imageReader = new imageReader(playSound, lbIm, is, numFrames, 0);

			if( ifRemoveAd ) {
				Queue<Shot> adShots = DetectAd.detect(args[0], args[1]);
//				Queue<Shot> adShots = new LinkedList<>();
//				adShots.offer(new Shot(0, 450, 10));
//				adShots.offer(new Shot(6000, 6450, 10));

				Queue<Shot> videoShots = new LinkedList<>(adShots);
				Queue<Shot> audioShots = new LinkedList<>(adShots);


				imageReader.setIfRemoveAd(ifRemoveAd);
				imageReader.setAdShots(videoShots);

//				System.out.println(imageReader.getShot()+"	vvv"+ifRemoveAd);

				playSound.setIfRemoveAd(ifRemoveAd);
				playSound.setAdShots(audioShots);
			}


			videoPlayback videoPlayback = new videoPlayback(imageReader, playSound);
			// opens the inputStream
			videoPlayback.playSoundSetting(afilename, playSound);
			videoPlayback.playInterface(lbIm);

//			imageReader.setVideoPlayback(videoPlayback);



			videoPlayback.startVedio();
			videoPlayback.startAudio();


//			System.out.println("play Listener   "+playSound.getState()+"  "+playSound.isIfplay());
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    public void startVedio() {
		imageReader.start();
	}

	public void startAudio( ) {
		playSound.start();
	}

	private void playSoundSetting(String afilename, PlaySound playSound) {
		try {
			FileInputStream inputStream = new FileInputStream(afilename);

			InputStream bufferedIn = new BufferedInputStream(inputStream);

			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(bufferedIn);
			AudioFormat audioFormat = audioInputStream.getFormat();

			Info info = new Info(SourceDataLine.class, audioFormat);
			SourceDataLine dataLine = (SourceDataLine) AudioSystem.getLine(info);

			dataLine.open(audioFormat, this.EXTERNAL_BUFFER_SIZE);
			dataLine.start();

			playSound.setDataLine(dataLine);
			playSound.setAudioInputStream(audioInputStream);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}

	private void playInterface(JLabel lbIm) {
		JFrame frame = new JFrame();


		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Vedio Player");
//		frame.setSize(WIDTH,HEIGHT+22);


		GridBagLayout gLayout = new GridBagLayout();
		frame.getContentPane().setLayout(gLayout);

		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.HORIZONTAL;// this field is used when the component's display area is larger than the component's requested size
		c.anchor = GridBagConstraints.CENTER;// This field is used when the component is smaller than its display area.
		c.weightx = 0.5;
		c.gridx = 0;
		c.gridy = 0;

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 1;


		frame.getContentPane().add(lbIm, c);

		JPanel sliderPanel = new JPanel();
		frame.getContentPane().add(sliderPanel);

		sliderPanel.setBackground(Color.gray);// display play, pause, stop button

		JButton btnPlay = new JButton();
		btnPlay.setIcon(null);
		btnPlay.setText("Play");

		btnPlay.addActionListener(new ButtonPlayActionListener(this));// TODO: some problem maybe show here
		sliderPanel.add(btnPlay);

		JButton btnPause = new JButton();
		btnPause.setIcon(null);
		btnPause.setText("Pause");

		btnPause.addActionListener(new ButtonPauseActionListener(this));
		sliderPanel.add(btnPause);

		JButton btnStop = new JButton();
		btnStop.setIcon(null);
		btnStop.setText("Stop");
		btnStop.addActionListener(new ButtonStopActionListener());
		sliderPanel.add(btnStop);

		frame.pack();
		frame.setVisible(true);
	}


}
