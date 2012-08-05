package com.serym.hackathon.aardvark;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import com.serym.hackathon.aardvark.bouncer.R;

/**
 * SoundManager is used to initialize the sound system and to play sounds of
 * different types.
 */
public class SoundManager {

	/**
	 * Maximum simultaneous sound effects.
	 */
	private static final int MAX_STREAMS = 2;

	/**
	 * The channel used to play the sounds on.
	 */
	private static final int SOUND_STREAM = AudioManager.STREAM_NOTIFICATION;

	/**
	 * The context the SoundManager is using, if initialized. null otherwise.
	 */
	private static Context context;

	/**
	 * The sound pool the SoundManager is using, if initialized. null otherwise.
	 */
	private static SoundPool soundPool;

	/**
	 * The audio manager the SoundManager is using, if initialized. null
	 * otherwise.
	 */
	private static AudioManager audioManager;

	/**
	 * The id for the SoundType.ACCEPT sound.
	 */
	private static int acceptSoundId;

	/**
	 * The id for the SoundType.REJECT sound.
	 */
	private static int rejectSoundId;

	/**
	 * Returns the context currently associated with the SoundManager.
	 * 
	 * @return the context associated with the SoundManager
	 */
	public static Context getContext() {
		return SoundManager.context;
	}

	/**
	 * Sets the context SoundManager is associated with. Must be called before
	 * any other SoundManager methods.
	 * 
	 * @param context
	 *            the context to use with the SoundManager
	 */
	public static void setContext(Context context) {
		if (SoundManager.soundPool == null) {
			// Create new sound pool
			SoundManager.soundPool = new SoundPool(MAX_STREAMS, SOUND_STREAM, 0);
		} else if (SoundManager.context != context) {
			// Unload sounds from old context
			SoundManager.soundPool.unload(acceptSoundId);
			SoundManager.soundPool.unload(rejectSoundId);
		}

		if (SoundManager.context != context) {
			// Load sounds
			SoundManager.acceptSoundId = SoundManager.soundPool.load(context,
					R.raw.accept, 1);
			SoundManager.rejectSoundId = SoundManager.soundPool.load(context,
					R.raw.reject, 1);

			// Get the audio manager for this context
			SoundManager.audioManager = (AudioManager) context
					.getSystemService(Context.AUDIO_SERVICE);
		}

		SoundManager.context = context;
	}

	/**
	 * Plays a sound corresponding to the given type. Must call
	 * SoundManager.setContext first.
	 * 
	 * @param type
	 *            the sound type
	 * 
	 * @see SoundType
	 */
	public static void playSound(SoundType type) {
		if (SoundManager.context == null || SoundManager.soundPool == null
				|| SoundManager.audioManager == null) {
			System.err.println(SoundManager.context + ";"
					+ SoundManager.soundPool + ";" + SoundManager.audioManager);
			throw new IllegalStateException(
					"Must call SoundManager.setContext first");
		}

		int soundId;
		switch (type) {
		case ACCEPT: {
			soundId = SoundManager.acceptSoundId;
			break;
		}
		case REJECT: {
			soundId = SoundManager.rejectSoundId;
			break;
		}
		default: {
			throw new IllegalArgumentException("Invalid sound type");
		}
		}

		// Calculate correct volume
		float streamVolume = SoundManager.audioManager
				.getStreamVolume(SOUND_STREAM)
				/ SoundManager.audioManager.getStreamMaxVolume(SOUND_STREAM);

		// Play it!
		SoundManager.soundPool.play(soundId, streamVolume, streamVolume, 1, 0,
				1.0f);
	}
}
