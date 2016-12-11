/*
 * Taken from j3d-optional-utils
 * BSD License 
 * https://java.net/projects/j3d-optional-utils
 */
package org.jdesktop.j3d.examples.sound.audio;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;

import org.jogamp.java3d.AudioDevice3D;
import org.jogamp.java3d.MediaContainer;
import org.jogamp.java3d.Sound;
import org.jogamp.java3d.Transform3D;
import org.jogamp.java3d.View;
import org.jogamp.java3d.audioengines.AuralParameters;
import org.jogamp.java3d.audioengines.Sample;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Point3f;
import org.jogamp.vecmath.Vector3d;
import org.jogamp.vecmath.Vector3f;

import com.jogamp.openal.AL;
import com.jogamp.openal.ALConstants;
import com.jogamp.openal.util.ALut;

/**
 * This is the JOAL Sample object which encapsulates all the functionality and holds all the data associated with a
 * Sample.
 * 
 * @author David Grace (dave@dutchie.net)
 */
@SuppressWarnings(
{ "hiding", "unused" })
public class JOALSample extends Sample
{

	private static boolean debug = false;

	private static boolean debugPosition = false;

	private static boolean debugDirection = false;

	private static boolean debugDistanceGain = false;

	private static boolean debugGain = false;

	private static boolean debugLoopCount = false;

	private static boolean debugMute = false;

	private static boolean debugLoad = true;

	private static boolean debugDuration = false;

	private static boolean debugClear = true;

	private int index;

	private int[] buffer;

	private int[] source;

	private AL al;

	private static double pi = Math.PI;

	private int sampleSize;

	private int sampleBits;

	private int sampleFrequency;

	private int sampleChannels = 1;

	/** Creates a new instance of JOALSample */
	public JOALSample()
	{
	}

	/**
	 * Null Sound identifier denotes sound is not created or initialized
	 */
	public static final int NULL_SAMPLE = -1;

	/**
	 * sound data associated with sound source
	 */
	protected MediaContainer soundData = null;

	/**
	 * sound data associated with sound source
	 */
	protected int soundType = -1;

	/**
	 * Overall Scale Factor applied to sound gain.
	 */
	protected float gain = 1.0f; // Valid values are >= 0.0.

	/**
	 * Overall Scale Factor applied to sound.
	 * 
	 * @since Java 3D 1.3
	 */
	protected float rateScaleFactor = 1.0f; // Valid values are >= 0.0.

	/**
	 * Number of times sound is looped/repeated during play
	 */
	protected int loopCount = 0; // Range from 0 to POSITIVE_INFINITY(-1)

	/*
	 * Duration of sample This should match the Sound node constant of same name
	 */
	public static final int DURATION_UNKNOWN = -1;

	protected long duration = DURATION_UNKNOWN;

	protected int numberOfChannels = 0;

	protected boolean mute = false; // denotes if sample is muted

	// (playing with zero gain)

	/*
	 * 
	 * Fields associated with positional sound samples
	 * 
	 */
	/*
	 * Local to Vworld transform
	 */
	protected Transform3D vworldXfrm = new Transform3D();

	protected boolean vwXfrmFlag = false;

	/*
	 * Origin of Sound source in Listener's space.
	 */
	protected Point3f position = new Point3f(0.0f, 0.0f, 0.0f);

	protected float[] positionArray = new float[]
	{ 0, 0, 0 };

	/*
	 * Pairs of distances and gain scale factors that define piecewise linear gain attenuation between each pair.
	 */
	protected double[] attenuationDistance = null;

	protected float[] attenuationGain = null;;

	/**
	 * dirty flags denoting what has changed since last rendering
	 */
	protected int dirtyFlags = 0xFFFF;

	/*
	 * 
	 * Direction sample fields
	 * 
	 */
	/**
	 * The Cone Sound's direction vector. This is the cone axis.
	 */
	protected Vector3f direction = new Vector3f(0.0f, 0.0f, 1.0f);

	protected float[] directionArray = new float[]
	{ 0, 0, 0 };

	/**
	 * Pairs of distances and gain scale factors that define piecewise linear gain BACK attenuation between each pair.
	 * These are used for defining elliptical attenuation regions.
	 */
	protected double[] backAttenuationDistance = null;

	protected float[] backAttenuationGain = null;

	/**
	 * Directional Sound's gain can be attenuated based on the listener's location off-angle from the source source
	 * direction. This can be set by three parameters: angular distance in radians gain scale factor filtering
	 * (currently the only filtering supported is lowpass)
	 */
	protected double[] angularDistance =
	{ 0.0, (Math.PI * 0.5) };

	protected float[] angularGain =
	{ 1.0f, 0.0f };

	/**
	 * Distance Filter Each sound source is attenuated by a filter based on it's distance from the listener. For now the
	 * only supported filterType will be LOW_PASS frequency cutoff. At some time full FIR filtering will be supported.
	 */
	public static final int NO_FILTERING = -1;

	public static final int LOW_PASS = 1;

	protected int angularFilterType = NO_FILTERING;

	protected float[] angularFilterCutoff =
	{ Sound.NO_FILTER, Sound.NO_FILTER };

	/*
	 * Obstruction and Occlusion parameters For now the only type of filtering supported is a low-pass filter defined by
	 * a frequency cutoff value.
	 * 
	 * @since Java 3D 1.3
	 */
	protected float obstructionGain = 1.0f; // scale factor

	protected int obstructionFilterType = NO_FILTERING;

	protected float obstructionFilterCutoff = Sound.NO_FILTER;

	protected float occlusionGain = 1.0f; // scale factor

	protected int occlusionFilterType = NO_FILTERING;

	protected float occlusionFilterCutoff = Sound.NO_FILTER;

	public long getDuration()
	{
		long duration = (long) ((double) sampleSize / sampleFrequency * 1000);
		if (debug && debugDuration)
			System.out.println("JOALSample - getDuration of " + index + " is " + duration);
		return duration;
	}

	public long getStartTime()
	{
		return 0;
	}

	public int getNumberOfChannelsUsed()
	{
		int[] i = new int[1];
		al.alGetBufferiv(index, ALConstants.AL_CHANNELS, i, 0);
		sampleChannels = i[0];
		// System.out.println("JOALSample - getNumberOfChannelsUsed of " + index + " is " + sampleChannels);
		return sampleChannels;
	}

	public void setDirtyFlags(int flags)
	{
		dirtyFlags = flags;
	}

	public int getDirtyFlags()
	{
		return dirtyFlags;
	}

	public void setSoundType(int type)
	{
		soundType = type;
	}

	public int getSoundType()
	{
		return soundType;
	}

	public void setSoundData(MediaContainer ref)
	{
		soundData = ref;
	}

	public MediaContainer getSoundData()
	{
		return soundData;
	}

	public void setMuteFlag(boolean flag)
	{
		if (debug & debugMute)
			System.out.println("JOALSample - setMuteFlag " + flag);
		mute = flag;
		if (mute)
		{
			al.alSourcef(index, ALConstants.AL_GAIN, 0);
		}
		else
		{
			al.alSourcef(index, ALConstants.AL_GAIN, gain);
		}
	}

	public boolean getMuteFlag()
	{
		return mute;
	}

	public void setVWrldXfrmFlag(boolean flag)
	{
		// this flag is ONLY true if the VirtualWorld Transform is ever set
		vwXfrmFlag = flag;
	}

	public boolean getVWrldXfrmFlag()
	{
		return vwXfrmFlag;
	}

	public void setGain(float scaleFactor)
	{
		if (debug & debugGain)
			System.out.println("JOALSample - setGain " + scaleFactor);
		gain = scaleFactor;
		al.alSourcef(index, ALConstants.AL_GAIN, scaleFactor);
	}

	public float getGain()
	{
		return gain;
	}

	public void setLoopCount(int count)
	{
		if (debug & debugLoopCount)
			System.out.println("JOALSample - setLoopCount " + count);
		loopCount = count;
		if (count == 0)
			al.alSourcei(index, ALConstants.AL_LOOPING, ALConstants.AL_FALSE);
		else if (count > 0)
			al.alSourcei(index, ALConstants.AL_LOOPING, ALConstants.AL_TRUE);
		else
			al.alSourcei(index, ALConstants.AL_LOOPING, ALConstants.AL_TRUE);
	}

	public int getLoopCount()
	{
		return loopCount;
	}

	public void setPosition(Point3d position)
	{
		if (debug & debugPosition)
			System.out.println("JOALSample - setPosition " + position);
		this.position.set(position);
		// this.position = position;
		positionArray[0] = (float) position.x;
		positionArray[1] = (float) position.y;
		positionArray[2] = (float) position.z;
		al.alSourcefv(index, ALConstants.AL_POSITION, positionArray, 0);
		return;
	}

	// TODO: no get method for Position

	public void setDistanceGain(double[] frontDistance, float[] frontAttenuationScaleFactor, double[] backDistance, float[] backAttenuationScaleFactor)
	{
		if (debug & debugDistanceGain)
			System.out.println("JOALSample - setDistanceGain " + frontDistance + ", " + frontAttenuationScaleFactor);

		if (frontDistance == null)
		{
			// al.alSourcef(index, AL.AL_ROLLOFF_FACTOR, 0);
		}
		else if (frontDistance.length == 1)
		{
			double d = frontDistance[0];
			// float f = frontAttenuationScaleFactor[0];
			al.alSourcefv(index, ALConstants.AL_REFERENCE_DISTANCE, new float[]
			{ (float) d }, 0);
			al.alSourcef(index, ALConstants.AL_ROLLOFF_FACTOR, 1);
		}
		else if (frontDistance.length > 1)
		{
			// double d = frontDistance[0];
			double dmax = frontDistance[frontDistance.length - 1];
			// float f = frontAttenuationScaleFactor[0];
			// float fmax = frontAttenuationScaleFactor[frontAttenuationScaleFactor.length - 1];
			// al.alSourcefv(index, AL.AL_REFERENCE_DISTANCE, new float[]{(float) (dmax / 2)}, 0);
			al.alSourcefv(index, ALConstants.AL_MAX_DISTANCE, new float[]
			{ (float) (dmax) }, 0);
			al.alSourcef(index, ALConstants.AL_ROLLOFF_FACTOR, 1);
		}

		if (frontDistance != null)
		{
			int size = frontDistance.length;
			attenuationDistance = new double[size];
			attenuationGain = new float[size];
			for (int i = 0; i < size; i++)
			{
				attenuationDistance[i] = frontDistance[i];
				attenuationGain[i] = frontAttenuationScaleFactor[i];
			}
		}
		else
		{
			attenuationDistance = null;
			attenuationGain = null;
		}
		if (backDistance != null && frontDistance != null)
		{
			int size = backDistance.length;
			backAttenuationDistance = new double[size];
			backAttenuationGain = new float[size];
			for (int i = 0; i < size; i++)
			{
				backAttenuationDistance[i] = backDistance[i];
				backAttenuationGain[i] = backAttenuationScaleFactor[i];
			}
		}
		else
		{
			backAttenuationDistance = null;
			backAttenuationGain = null;
		}
		return;
	}

	// TODO: no get method for Back Attenuation

	public void setDirection(Vector3d direction)
	{
		if (debug && debugDirection)
			System.out.println("JOALSample - setDirection " + direction);
		this.direction.set(direction);
		directionArray[0] = (float) direction.x;
		directionArray[1] = (float) direction.y;
		directionArray[2] = (float) direction.z;
		al.alSourcefv(index, ALConstants.AL_DIRECTION, directionArray, 0);
		return;
	}

	// TODO: no get method for Direction

	public void setAngularAttenuation(int filterType, double[] angle, float[] attenuationScaleFactor, float[] filterCutoff)
	{
		if (angle != null)
		{
			if (angle.length == 1)
			{
				float f = radiansToDegrees(angle[0]);
				// al.alSourcef(index, AL.AL_CONE_OUTER_ANGLE, (float) angle[0]);
				// al.alSourcef(index, AL.AL_CONE_OUTER_ANGLE, 30);
				// al.alSourcef(index, AL.AL_CONE_INNER_ANGLE, 45);
				al.alSourcef(index, ALConstants.AL_CONE_INNER_ANGLE, f / 2);
				al.alSourcef(index, ALConstants.AL_CONE_OUTER_ANGLE, f);

			}
			else if (angle.length == 2)
			{
				float f1 = radiansToDegrees(angle[0]);
				float f2 = radiansToDegrees(angle[1]);
				al.alSourcef(index, ALConstants.AL_CONE_INNER_ANGLE, f1);
				al.alSourcef(index, ALConstants.AL_CONE_OUTER_ANGLE, f2);
			}
			else
			{
				float f1 = radiansToDegrees(angle[0]);
				float f2 = radiansToDegrees(angle[angle.length - 1]);
				al.alSourcef(index, ALConstants.AL_CONE_INNER_ANGLE, f1);
				al.alSourcef(index, ALConstants.AL_CONE_OUTER_ANGLE, f2);
			}
		}

		if (angle != null)
		{
			int size = angle.length;
			angularDistance = new double[size];
			angularGain = new float[size];
			if (filterType != NO_FILTERING && filterCutoff != null)
				angularFilterCutoff = new float[size];
			else
				angularFilterCutoff = null;
			for (int i = 0; i < size; i++)
			{
				angularDistance[i] = angle[i];
				angularGain[i] = attenuationScaleFactor[i];
				if (filterType != NO_FILTERING)
					angularFilterCutoff[i] = filterCutoff[i];
			}
			angularFilterType = filterType;
		}
		else
		{
			angularDistance = null;
			angularGain = null;
			angularFilterCutoff = null;
			angularFilterType = NO_FILTERING;
		}
	}

	// TODO: no get method for Angular Attenuation

	/*
	 * Set Rate ScaleFactor
	 * 
	 * @since Java 3D 1.3
	 */
	public void setRateScaleFactor(float scaleFactor)
	{
		rateScaleFactor = scaleFactor;
		al.alSourcef(index, ALConstants.AL_PITCH, scaleFactor);
	}

	/*
	 * Get Rate ScaleFactor
	 * 
	 * @since Java 3D 1.3
	 */
	public float getRateScaleFactor()
	{
		return rateScaleFactor;
	}

	/*
	 * Set Obstruction Gain
	 * 
	 * @since Java 3D 1.3
	 */
	public void setObstructionGain(float scaleFactor)
	{
		obstructionGain = scaleFactor;
	}

	/*
	 * Get Obstruction Gain
	 * 
	 * @since Java 3D 1.3
	 */
	public float getObstructionGain()
	{
		return obstructionGain;
	}

	/*
	 * Set Obstruction Filter Cutoff Frequency
	 * 
	 * @since Java 3D 1.3
	 */
	public void setObstructionFilter(float cutoffFrequency)
	{
		obstructionFilterType = LOW_PASS;
		obstructionFilterCutoff = cutoffFrequency;
	}

	// TODO: no get method for Obstruction Filtering

	/*
	 * Set Occlusion Gain
	 * 
	 * @since Java 3D 1.3
	 */
	public void setOcclusionGain(float scaleFactor)
	{
		occlusionGain = scaleFactor;
	}

	/*
	 * Get Occlusion Gain
	 * 
	 * @since Java 3D 1.3
	 */
	public float getOcclusionGain()
	{
		return occlusionGain;
	}

	/*
	 * Set Occlusion Filter Cutoff Frequency
	 * 
	 * @since Java 3D 1.3
	 */
	public void setOcclusionFilter(float cutoffFrequency)
	{
		occlusionFilterType = LOW_PASS;
		occlusionFilterCutoff = cutoffFrequency;
	}

	// TODO: no get method for Occlusion Filtering

	/**
	 * Clears/re-initialize fields associated with sample data for this sound, and frees any device specific data
	 * associated with this sample.
	 */
	public void clear()
	{
		if (debug && debugClear)
			System.out.println("JOALSample - clear");
		// Added to clear function to clear resources held by JOAL/OpenAL
		// clearOpenALBuffer();
		soundData = null;
		soundType = NULL_SAMPLE;
		gain = 1.0f;
		loopCount = 0;
		duration = DURATION_UNKNOWN;
		numberOfChannels = 0;
		vworldXfrm.setIdentity();
		vwXfrmFlag = false;
		position.set(0, 0, 0);
		positionArray[0] = 0;
		positionArray[1] = 0;
		positionArray[2] = 0;
		attenuationDistance = null;
		attenuationGain = null;
		direction.set(0.0f, 0.0f, 1.0f);
		directionArray[0] = 0;
		directionArray[1] = 0;
		directionArray[2] = 0;
		backAttenuationDistance = null;
		backAttenuationGain = null;
		if (angularDistance != null)
		{
			angularDistance[0] = 0.0f;
			angularDistance[1] = (float) (Math.PI) * 0.5f;
		}
		if (angularGain != null)
		{
			angularGain[0] = 1.0f;
			angularGain[1] = 0.0f;
		}
		angularFilterType = NO_FILTERING;
		if (angularFilterCutoff != null)
		{
			angularFilterCutoff[0] = Sound.NO_FILTER;
			angularFilterCutoff[1] = Sound.NO_FILTER;
		}
		obstructionGain = 1.0f;
		obstructionFilterType = NO_FILTERING;
		obstructionFilterCutoff = Sound.NO_FILTER;
		occlusionGain = 1.0f;
		occlusionFilterType = NO_FILTERING;
		occlusionFilterCutoff = Sound.NO_FILTER;
	}

	/*
	 * Render
	 */
	public void render(int dirtyFlags, View view, AuralParameters attribs)
	{
		// meant to be overridden
	}

	/**
	 * Load the sound ready to by played.
	 * 
	 * @return error true if error occurred
	 */
	public boolean load(AL al, MediaContainer soundData, int soundType)
	{
		if (debug && debugLoad)
		{
			if (soundData.getURLObject() != null)
				System.out.print("JOALSample - load " + soundData.getURLObject() + "...");
			else
				System.out.print("JOALSample - load " + soundData + "...");
		}
		this.al = al;
		this.soundType = soundType;

		InputStream is = soundData.getInputStream();
		if (is == null)
		{
			URL url = soundData.getURLObject();
			// Issue 481: JOALSample: cannot load if only URLString is given in MediaContainer
			if (null == url)
			{
				try
				{
					url = new URL(soundData.getURLString());
				}
				catch (MalformedURLException ex)
				{
					ex.printStackTrace();
					return true;
				}
			}
			try
			{
				is = url.openStream();
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
				return true;
			}
		}
		// if (debug && debugLoad) System.out.println("JOALSample - load - is: " + is);
		buffer = new int[1];

		// Sources are points emitting sound.
		source = new int[1];

		int[] format = new int[1];
		int[] size = new int[1];
		ByteBuffer[] data = new ByteBuffer[1];
		int[] freq = new int[1];
		int[] loop = new int[1];

		// load wav data into buffers
		al.alGenBuffers(1, buffer, 0);
		int errorCode = al.alGetError();

		// Note: This function should really return true when an error is generated by JOAL
		// but an error code of 40961 is given with some samples that are still played
		// by JOAL (bug 490). Thus the checking of this error is disabled as a fix to
		// this bug.

		if (errorCode != ALConstants.AL_NO_ERROR)
		{
			System.out.print(" error generating buffer - JOAL error code: " + errorCode + " - ");
			// return true;
		}

		ALut.alutLoadWAVFile(is, format, data, size, freq, loop);
		al.alBufferData(buffer[0], format[0], data[0], size[0], freq[0]);

		// bind buffers into audio sources
		al.alGenSources(1, source, 0);
		sampleSize = size[0];
		sampleFrequency = freq[0];
		sampleBits = format[0];

		if (soundType == AudioDevice3D.BACKGROUND_SOUND)
		{
			if (debug && debugLoad)
				System.out.print(" BackgroundSound...");
			al.alSourcei(source[0], ALConstants.AL_BUFFER, buffer[0]);
			al.alSourcef(source[0], ALConstants.AL_PITCH, 1.0f);
			al.alSourcef(source[0], ALConstants.AL_GAIN, 1.0f);
			// al.alSourcefv(source[0], AL.AL_POSITION, position, 0);
			// al.alSourcefv(source[0], AL.AL_POSITION, sourceVel, 0);
			al.alSourcei(source[0], ALConstants.AL_LOOPING, ALConstants.AL_TRUE);
			al.alSourcei(source[0], ALConstants.AL_ROLLOFF_FACTOR, 0);
			al.alSourcei(source[0], ALConstants.AL_SOURCE_RELATIVE, ALConstants.AL_TRUE);
			if (debug && debugLoad)
				System.out.println(" success, sourceID: " + source[0]);
		}
		else if (soundType == AudioDevice3D.POINT_SOUND)
		{
			if (debug && debugLoad)
				System.out.print(" PointSound...");
			al.alGenSources(1, source, 0);

			al.alSourcei(source[0], ALConstants.AL_BUFFER, buffer[0]);
			al.alSourcef(source[0], ALConstants.AL_PITCH, 1.0f);
			al.alSourcef(source[0], ALConstants.AL_GAIN, 1.0f);
			// al.alSourcefv(source[0], AL.AL_POSITION, position, 0);
			// al.alSourcefv(source[0], AL.AL_POSITION, sourceVel, 0);
			al.alSourcei(source[0], ALConstants.AL_LOOPING, ALConstants.AL_TRUE);
			if (debug && debugLoad)
				System.out.println(" success, sourceID: " + source[0]);
		}
		else if (soundType == AudioDevice3D.CONE_SOUND)
		{
			if (debug && debugLoad)
				System.out.print(" ConeSound...");
			al.alGenSources(1, source, 0);

			al.alSourcei(source[0], ALConstants.AL_BUFFER, buffer[0]);
			al.alSourcef(source[0], ALConstants.AL_PITCH, 1.0f);
			al.alSourcef(source[0], ALConstants.AL_GAIN, 1.0f);
			// al.alSourcefv(source[0], AL.AL_POSITION, position, 0);
			// al.alSourcefv(source[0], AL.AL_POSITION, sourceVel, 0);
			al.alSourcei(source[0], ALConstants.AL_LOOPING, ALConstants.AL_TRUE);
			if (debug && debugLoad)
				System.out.println(" success, sourceID: " + source[0]);
		}

		index = source[0];
		return false;
	}

	/**
	 * Load the sound ready to by played reusing the shared buffer.
	 * 
	 * @return error true if error occurred
	 */
	public boolean load(AL al, int[] buffer, int soundType)
	{
		if (debug && debugLoad)
		{
			System.out.print("JOALSample - load using shared buffer" + "...");
		}
		this.al = al;
		this.soundType = soundType;

		this.buffer = buffer;

		// Sources are points emitting sound.
		source = new int[1];

		int[] format = new int[1];
		int[] size = new int[1];
		ByteBuffer[] data = new ByteBuffer[1];
		int[] freq = new int[1];
		// int[] loop = new int[1];

		al.alBufferData(buffer[0], format[0], data[0], size[0], freq[0]);

		// bind buffers into audio sources
		al.alGenSources(1, source, 0);
		sampleSize = size[0];
		sampleFrequency = freq[0];
		sampleBits = format[0];

		if (soundType == AudioDevice3D.BACKGROUND_SOUND)
		{
			if (debug && debugLoad)
				System.out.print(" BackgroundSound...");
			al.alSourcei(source[0], ALConstants.AL_BUFFER, buffer[0]);
			al.alSourcef(source[0], ALConstants.AL_PITCH, 1.0f);
			al.alSourcef(source[0], ALConstants.AL_GAIN, 1.0f);
			// al.alSourcefv(source[0], AL.AL_POSITION, position, 0);
			// al.alSourcefv(source[0], AL.AL_POSITION, sourceVel, 0);
			al.alSourcei(source[0], ALConstants.AL_LOOPING, ALConstants.AL_TRUE);
			al.alSourcei(source[0], ALConstants.AL_ROLLOFF_FACTOR, 0);
			al.alSourcei(source[0], ALConstants.AL_SOURCE_RELATIVE, ALConstants.AL_TRUE);
			if (debug && debugLoad)
				System.out.println(" success, sourceID: " + source[0]);
		}
		else if (soundType == AudioDevice3D.POINT_SOUND)
		{
			if (debug && debugLoad)
				System.out.print(" PointSound...");
			al.alGenSources(1, source, 0);

			al.alSourcei(source[0], ALConstants.AL_BUFFER, buffer[0]);
			al.alSourcef(source[0], ALConstants.AL_PITCH, 1.0f);
			al.alSourcef(source[0], ALConstants.AL_GAIN, 1.0f);
			// al.alSourcefv(source[0], AL.AL_POSITION, position, 0);
			// al.alSourcefv(source[0], AL.AL_POSITION, sourceVel, 0);
			al.alSourcei(source[0], ALConstants.AL_LOOPING, ALConstants.AL_TRUE);
			if (debug && debugLoad)
				System.out.println(" success, sourceID: " + source[0]);
		}
		else if (soundType == AudioDevice3D.CONE_SOUND)
		{
			if (debug && debugLoad)
				System.out.print(" ConeSound...");
			al.alGenSources(1, source, 0);

			al.alSourcei(source[0], ALConstants.AL_BUFFER, buffer[0]);
			al.alSourcef(source[0], ALConstants.AL_PITCH, 1.0f);
			al.alSourcef(source[0], ALConstants.AL_GAIN, 1.0f);
			// al.alSourcefv(source[0], AL.AL_POSITION, position, 0);
			// al.alSourcefv(source[0], AL.AL_POSITION, sourceVel, 0);
			al.alSourcei(source[0], ALConstants.AL_LOOPING, ALConstants.AL_TRUE);
			if (debug && debugLoad)
				System.out.println(" success, sourceID: " + source[0]);
		}

		index = source[0];
		return false;
	}

	public int startSample()
	{
		al.alGetError();
		al.alSourcePlay(index);
		int errorCode = al.alGetError();
		if (errorCode == ALConstants.AL_NO_ERROR)
			return 1;
		else
			return -1;
	}

	public int stopSample()
	{
		al.alGetError();
		al.alSourceStop(index);
		int errorCode = al.alGetError();
		if (errorCode == ALConstants.AL_NO_ERROR)
			return 1;
		else
		{
			System.out.println("JOALAudioDevice3D - stopSample...error stopping sample " + index);
			return -1;
		}
	}

	// Debug print flags and methods

	static final protected boolean debugFlag = false;

	static final protected boolean internalErrors = false;

	protected void debugPrint(String message)
	{
		if (debugFlag)
			System.out.println(message);
	}

	protected void debugPrintln(String message)
	{
		if (debugFlag)
			System.out.println(message);
	}

	private float radiansToDegrees(double radians)
	{
		return (float) (radians * 180 / pi);
	}

	public AL getAl()
	{
		return al;
	}

	public int[] getBuffer()
	{
		return buffer;
	}

	/**
	 * Clears all resources held by JOAL/OpenAL
	 */
	private void clearOpenALBuffer()
	{
		al.alDeleteBuffers(1, buffer, 0);
		al.alDeleteSources(1, source, 0);
	}

	/**
	 * Check for OpenAL errors and report error code
	 */
	private boolean checkForErrors()
	{
		int i = al.alGetError();
		if (i != ALConstants.AL_NO_ERROR)
		{
			if (debug)
				System.out.println("JOALSample - openAL error - " + i);
			return true;
		}
		else
			return false;
	}
}
