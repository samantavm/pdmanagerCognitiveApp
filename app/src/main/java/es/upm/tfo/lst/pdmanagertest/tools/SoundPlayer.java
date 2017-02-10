package es.upm.tfo.lst.pdmanagertest.tools;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;

/**
 * Plays audio files.
 */
public class SoundPlayer
{

	private MediaPlayer mp;
	private OnSoundPlayerReady ospr;
	private OnSoundPlayerEnded ospe;
	private final String resourcePath = "android.resource://";
	
	private OnCompletionListener ocl = new OnCompletionListener()
	{
		@Override
		public void onCompletion(MediaPlayer mp)
		{
			mp.reset();
			if (ospe!=null) ospe.onSoundEnded();
		}
	};
	
	private OnPreparedListener opl = new OnPreparedListener()
	{
		@Override
		public void onPrepared(MediaPlayer mp)
		{
			mp.start();
			if (ospr!=null) ospr.onPlayerReady();
		}
	};
	
	public interface OnSoundPlayerReady { public void onPlayerReady(); }
	public interface OnSoundPlayerEnded { public void onSoundEnded(); }

	/**
	 * Instances a new SoundPlayer.
	 */
	public SoundPlayer()
	{
		mp = new MediaPlayer();
		mp.setOnCompletionListener(ocl);
		mp.setOnPreparedListener(opl);
	}
	
	/**
	 * Sets a listener to notify asynchronously that the media player 
	 * is initialized and ready to start playing sound files.
	 * Setting this listener for this class is optional.
	 * @param listener The OnSoundPlayerReady listener instance.
	 */
	public void setOnSoundPlayerReadyListener(OnSoundPlayerReady listener)
	{
		ospr = listener;
	}

	/**
	 * Sets a listener to notify asynchronously that a media resource
	 * being played has ended.
	 * Setting this listener for this class is optional.
	 * @param listener The OnSoundPlayerEnded listener instance.
	 */
	public void setOnSoundPlayerEnded(OnSoundPlayerEnded listener)
	{
		ospe = listener;
	}
	
	/**
	 * Plays the audio file under the given path (can be the SD card).
	 * The given parameter is allowed to be an URL pointing to a
	 * remote audio file on a server.
	 */
	public void play(String path)
	{
		if (mp.isPlaying() || path==null) stopPlaying();
		try
		{
			mp.setDataSource(path);
	        mp.prepareAsync();
		}
		catch (Exception e) { e.printStackTrace(); }
	}
	
	/**
	 * Plays the audio file related to the given resource within the app.
	 * These sound resources are likely to be located under the "res/raw"
	 * directory of the project as assets.
	 * Usage example form an Activity: play(getApplicationContext(), R.raw.my_sound);
	 * @param ctx The context of execution of the app.
	 * @param resID The identifier of the sound asset.
	 */
	public void play(Context ctx, int resID)
	{
		if (mp.isPlaying()) stopPlaying();
		try
		{
			String sound = resourcePath + ctx.getPackageName() + "/" + resID;
			Uri uri = Uri.parse(sound);
			mp.setDataSource(ctx, uri);
			mp.prepareAsync();
		}
		catch (Exception e) { e.printStackTrace(); }
	}
	
	/**
	 * Stops playing the current media.
	 */
	public void stopPlaying()
	{
		mp.stop();
		mp.reset();
	}
	
	/**
	 * Toggles play/pause.
	 */
	public void togglePause()
	{
		if (mp.isPlaying()) mp.pause();
		else mp.start();
	}
	
	/**
	 * Tells whether the SoundPlayer is currently playing.
	 */
	public boolean isPlaying() { return mp.isPlaying(); }
	
} // SoundPlayer
