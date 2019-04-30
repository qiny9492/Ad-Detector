package readFiles;// CSCI 576 Final Project

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Deque;
import java.util.Queue;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.DataLine.Info;

import shot.Shot;

public class PlaySound extends Thread{
	private boolean ifplay = false;

	private SourceDataLine dataLine;
	private AudioInputStream audioInputStream;

	private Shot curShot;
	private Queue<Shot> adShots;
	private long sampleSkipped;

	public long getSampleSkipped() {
		return sampleSkipped;
	}


	public void setSampleSkipped(long sampleSkipped) {
		this.sampleSkipped = sampleSkipped;
	}

	public void setIfRemoveAd(boolean ifRemoveAd) {
		this.ifRemoveAd = ifRemoveAd;
	}

	public Queue<Shot> getAdShots() {
		return adShots;
	}


	public boolean isIfRemoveAd() {
		return ifRemoveAd;
	}

	public Shot getCurShot() {
		return curShot;
	}

	public void setCurShot(Shot curShot) {
		this.curShot = curShot;
	}

	private boolean ifRemoveAd = false;

	private double sampleRatePerFrame = (30*1.0)/48000;

	private final int EXTERNAL_BUFFER_SIZE = 3200;


	public void setIfplay(boolean ifplay) {
		this.ifplay = ifplay;
	}

	public void setAdShots(Queue<Shot> adShots) {
		this.adShots = adShots;
	}

	public void run(){
		try {
			this.play();
		}
		catch (PlayWaveException e) {
			e.printStackTrace();
			return;
		}
	}

	public void setNotPlay() {
		ifplay = false;
	}

	public void setPlay() {
		ifplay = true;
	}

	public void setDataLine(SourceDataLine dataLine) {
		this.dataLine = dataLine;
	}

	public AudioInputStream getAudioInputStream() {
		return audioInputStream;
	}

	public void setAudioInputStream(AudioInputStream audioInputStream) {
		this.audioInputStream = audioInputStream;
	}

	public SourceDataLine getDataLine() {
		return dataLine;
	}

	public boolean isIfplay() {
		return ifplay;
	}

	public void play() throws PlayWaveException {
//		System.out.println("jjjj");
		int readBytes = 0;
		byte[] audioBuffer = new byte[this.EXTERNAL_BUFFER_SIZE];

		try {
			while (readBytes != -1) {
				while( !ifplay ) {}

				if( ifRemoveAd ) {
					if( curShot == null && !adShots.isEmpty() )
						curShot = adShots.poll();

					if( curShot != null ) {
						double VideoFrame = (getPosition() * sampleRatePerFrame);
//						System.out.println(sampleSkipped+"	"+getPosition()+ "	"+(getPosition() * sampleRatePerFrame)+"	"+sampleRatePerFrame);

						if( (VideoFrame + sampleSkipped) >= curShot.getStart() - 1 && (VideoFrame+sampleSkipped) <= curShot.getStart() + 1 ) {
							long samples = (long)((curShot.getEnd() - curShot.getStart())*1.0/30*48000);
							long numBytes = samples*2;

							sampleSkipped += (curShot.getEnd() - curShot.getStart());
//							System.out.println("sample skippeds "+sampleSkipped);

							while ( numBytes > 0 && (readBytes = audioInputStream.read(audioBuffer)) >= 0 ) {
								numBytes -= readBytes;
							}
							curShot = adShots.poll();
						}
					}
				}

				readBytes = audioInputStream.read(audioBuffer, 0, audioBuffer.length);

				if (readBytes >= 0) {
					dataLine.write(audioBuffer, 0, readBytes);
				}

			}
		}
		catch (IOException e1) {
			throw new PlayWaveException(e1);
		}
		finally {
			// plays what's left and and closes the audioChannel
			dataLine.drain();
//			dataLine.close();

		}
	}

	public long getPosition() {
		return dataLine.getLongFramePosition() ;
	}
}
