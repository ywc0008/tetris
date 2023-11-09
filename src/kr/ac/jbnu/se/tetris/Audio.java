package kr.ac.jbnu.se.tetris;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;


public class Audio {
	private Clip clip;
	private File audioFile;
	private AudioInputStream audioInputStream;
	private boolean isLoop;
	
	public Audio(String pathName, boolean isLoop) {
		try {
			clip=AudioSystem.getClip();
			audioFile= new File(pathName);
			audioInputStream=AudioSystem.getAudioInputStream(audioFile);
			clip.open(audioInputStream);	
		}catch(LineUnavailableException | UnsupportedAudioFileException | IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
	public void bgmStart() {
		clip.setFramePosition(0);
		clip.start();
		if(isLoop) clip.loop(clip.LOOP_CONTINUOUSLY);
	}
	public void bgmStop() {
		clip.stop();
	}
}
