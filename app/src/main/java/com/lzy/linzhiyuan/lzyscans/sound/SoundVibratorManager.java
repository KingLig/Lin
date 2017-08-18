package com.lzy.linzhiyuan.lzyscans.sound;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Vibrator;
import android.util.Log;

import java.util.HashMap;

public class SoundVibratorManager {
	private static SoundVibratorManager soundlVibratorManagerInstance;
	private static SoundPool soundPool;
	private static HashMap<Integer, Integer> soundPoolMap;
	private static AudioManager audioManager;
	private static Context context;

	/**
	 * Requests the instance of the Sound Manager and creates it if it does not exist.
	 * @return Returns the single instance of the SoundVibatorManager
	 */
	public static synchronized SoundVibratorManager getInstance() {
		if (soundlVibratorManagerInstance == null){
			soundlVibratorManagerInstance = new SoundVibratorManager();
		}
		return soundlVibratorManagerInstance;
	}

	/**
	 * Initialises the storage for the sounds
	 * @param theContext  The Application context
	 */
	public static void initSounds(Context theContext) {
		context = theContext;
		soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
		soundPoolMap = new HashMap<Integer, Integer>();
		audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
	}

	/**
	 * Add a new Sound to the SoundPool
	 * 
	 * @param index
	 *            - The Sound Index for Retrieval
	 * @param soundID
	 *            - The Android ID for the Sound asset.
	 */
	public static void addSound(int index, int soundID) {
		soundPoolMap.put(index, soundPool.load(context,soundID, 1));
	}

	/**
	 * Loads the various sound assets Currently hardcoded but could easily be
	 * changed to be flexible.
	 */
	public static void loadSounds(int[] resourceIdArray) {
		for(int i=0;i<resourceIdArray.length;i++){
			soundPoolMap.put(i+1, soundPool.load(context, resourceIdArray[i], 1));
		}
		/*soundPoolMap.put(1, soundPool.load(context, R.raw.beep, 1));
		soundPoolMap.put(2, soundPool.load(context, R.raw.buzz, 1));*/
	}

	/**
	 * Plays a Sound
	 * 
	 * @param index
	 *            - The Index of the Sound to be played
	 * @param speed
	 *            - The Speed to play not, not currently used but included for
	 *            compatibility
	 */
	public static void playSound(int index, float speed) {
		float streamVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		//streamVolume = streamVolume / audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		int result = soundPool.play(soundPoolMap.get(index), 1, 1, 0, 0, speed);
        Log.i("SoundVibratorManager",result+"");
        /*
		float streamMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		soundPool.play(soundPoolMap.get(index), streamMaxVolume, streamMaxVolume, 1, 0, speed);*/
		
	}

	/**
	 * Stop a Sound
	 * 
	 * @param index
	 *            - index of the sound to be stopped
	 */
	public static void stopSound(int index) {
		soundPool.stop(soundPoolMap.get(index));
	}

	public static void cleanup() {
        if(soundPool!=null){
            soundPool.release();
            soundPool = null;
            soundPoolMap.clear();
        }
		//audioManager.unloadSoundEffects();
		soundlVibratorManagerInstance = null;

	}

	public static void playVibrator(Context context, long timelong) {
		Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		if(vibrator.hasVibrator()){
			vibrator.vibrate(timelong);
		}
	}
}
