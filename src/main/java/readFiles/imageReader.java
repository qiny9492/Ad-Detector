package readFiles;

import listeners.ButtonPauseActionListener;
import listeners.ButtonPlayActionListener;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.Deque;
import java.util.Queue;
import javax.swing.*;
import javax.swing.JButton;

import shot.Shot;


public class imageReader extends Thread{

	private PlaySound playSound;
	private videoPlayback videoPlayback;


	private  long numFrames;
	private long frameCounter = 0;

	private final int WIDTH = 480;
	private final int HEIGHT = 270;

	public InputStream is;
	private BufferedImage img;

	private byte[] bytes;
	private final double FPS = 30;
	private double sampleRatePerFrame = 1;
	private long FramesSkipped;

	private boolean ifPlay = false;
	private JLabel lbIm;

	private Shot shot;
	private Queue<Shot> adShots;
	private boolean ifRemoveAd = false;

	public long getNumFrames() {
		return numFrames;
	}

	public boolean isIfRemoveAd() {
		return ifRemoveAd;
	}

	public long getFramesSkipped() {
		return FramesSkipped;
	}

	public void setFramesSkipped(long framesSkipped) {
		FramesSkipped = framesSkipped;
	}

	public Queue<Shot> getAdShots() {
		return adShots;
	}

	public void run(){
		play();
	}

	public void setNotPlay() {
		ifPlay = false;
	}

	public void setPlay() {
		ifPlay = true;
	}

	public Shot getShot() {
		return shot;
	}

	public boolean isIfPlay() {
		return ifPlay;
	}

	public void setAdShots(Queue<Shot> adShots) {
		this.adShots = adShots;
	}

	public void setIfRemoveAd(boolean ifRemoveAd) {
		this.ifRemoveAd = ifRemoveAd;
	}

	public void setLbIm(JLabel lbIm) {
		this.lbIm = lbIm;
	}

	public void setVideoPlayback(readFiles.videoPlayback videoPlayback) {
		this.videoPlayback = videoPlayback;
	}

	public long getFrameCounter() {
		return frameCounter;
	}

	public InputStream getIs() {
		return is;
	}

	public JLabel getLbIm() {
		return lbIm;
	}

	public void setShot(Shot shot) {
		this.shot = shot;
	}

	public imageReader(PlaySound pSound, JLabel lbIm, InputStream is, long numFrames, long frameCounter){
		this.playSound = pSound;
		sampleRatePerFrame = FPS/48000;
		this.lbIm = lbIm;

		this.is = is;


		img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		long len = WIDTH*HEIGHT*3;
		bytes = new byte[(int)len];

		this.numFrames = numFrames;
		this.frameCounter = frameCounter;
	}

	/**
	 * Plays the video file to a JFrame.
	 */
	private void play(){
		while( frameCounter < numFrames ) {
//			System.out.println(ifPlay);
			while (!ifPlay);
//			System.out.println(ifPlay);

			handleSynchronization();

			while (!ifPlay);
			readBytes();
			lbIm.setIcon(new ImageIcon(img));
		}
		//		handleSynchronization();

//		System.out.println("jjj");

	}

	private void handleSynchronization() {

		int offset = 10;

//		System.out.println(frameCounter+"	"+getSampleDiff());

		//video faster
		while (ifPlay && frameCounter - getSampleDiff() > offset ){}
		// audio faster
		while( ifPlay && getSampleDiff() - frameCounter > offset ){
//			System.out.println(playSound.getPosition() + "	"+getSampleDiff() +"	"+ frameCounter);
			readBytes();
			lbIm.setIcon(new ImageIcon(img));
		}

	}

	private double getSampleDiff() {
//		System.out.println(playSound.getPosition()+"	jjjjj");
		return playSound.getPosition() * sampleRatePerFrame + FramesSkipped;
	}


	private  void readBytes() {
		try {
			int offset = 0;
			int numRead = 0;


			if( ifRemoveAd ) {
				if( shot == null && !adShots.isEmpty() )
					shot = adShots.poll();

				if( shot != null && frameCounter == shot.getStart() ) {
//					System.out.println("shot start  "+shot.getStart()+"	end  "+shot.getEnd());
					int frames = shot.getEnd() - shot.getStart();
//					System.out.println("videop "+frames);
					FramesSkipped += frames;

					long numCount = frames * WIDTH * HEIGHT * 3;

					while ( numCount > 0 && ( numRead = is.read(bytes) ) >= 0 ) {
						numCount -= numRead;
					}

					frameCounter += frames;
					shot = adShots.poll();
				}
			}


			numRead = 0;

			while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
				offset += numRead;
			}

			if( offset >  0 ) {
				frameCounter++;

				int ind = 0;
				for(int y = 0; y < HEIGHT; y++){
					for(int x = 0; x < WIDTH; x++){
						byte r = bytes[ind];
						byte g = bytes[ind+HEIGHT*WIDTH];
						byte b = bytes[ind+HEIGHT*WIDTH*2];

						int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
						img.setRGB(x,y,pix);
						ind++;
					}
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
