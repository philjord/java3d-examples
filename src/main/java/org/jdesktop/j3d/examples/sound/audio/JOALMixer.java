/*
 * Taken from j3d-optional-utils
 * BSD License 
 * https://java.net/projects/j3d-optional-utils
 */

package org.jdesktop.j3d.examples.sound.audio;

import java.util.HashMap;

import org.jogamp.java3d.AudioDevice;
import org.jogamp.java3d.AudioDevice3D;
import org.jogamp.java3d.AudioDevice3DL2;
import org.jogamp.java3d.AuralAttributes;
import org.jogamp.java3d.MediaContainer;
import org.jogamp.java3d.PhysicalEnvironment;
import org.jogamp.java3d.Sound;
import org.jogamp.java3d.Transform3D;
import org.jogamp.java3d.View;
import org.jogamp.java3d.audioengines.AudioEngine3DL2;
import org.jogamp.java3d.audioengines.Sample;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Vector3d;
import org.jogamp.vecmath.Vector3f;

import com.jogamp.openal.AL;
import com.jogamp.openal.ALC;
import com.jogamp.openal.ALCConstants;
import com.jogamp.openal.ALCcontext;
import com.jogamp.openal.ALCdevice;
import com.jogamp.openal.ALConstants;
import com.jogamp.openal.ALException;
import com.jogamp.openal.ALFactory;
import com.jogamp.openal.util.ALut;

/**
 * This class is a concrete implementation of AudioEngine3DL2 that uses the JOAL/OpenAL sound library to provide
 * rendering of the Sound nodes in Java3D. <br>
 * Notes: <br>
 * 1. getChannelsAvailable, getNumberOfChannelsUsed and getTotalChannels have not been implemented yet and thus if you
 * try to play more sounds than the system has then they will not play. I am still trying to find out exactly how to
 * implement these functions, possibly there is no limit when rendering sound using software rendering. Also at the
 * moment I think I have limited the implementation to 64 sounds. <br>
 * 2. This implementation only plays wav files. <br>
 * 3. Other functions not implemented have the comment 'NOT IMPLEMENTED' in their respective JavaDoc comment, otherwise
 * the function has been implemented. <br>
 * 4. Webstart demos are available at http://www.dutchie.net/joal/default.htm <br>
 * Usage: <br>
 * 1. You must have the OpenAL drivers installed on your machine, these can be downloaded for Windows, MacOS, Linux at
 * http://www.openal.org/downloads.html <br>
 * 2. You must have JOAL installed which can be downloaded from https://joal.dev.java.net/ . This includes joal.zip and
 * the native dll/so for your selected platform. <br>
 * 3. To use in your application you simply use: <br>
 * java -Dj3d.audiodevice=org.jdesktop.j3d.audioengines.joal.JOALMixer [your_application_class] and call
 * viewer.createAudioDevice(); as normal.
 * 
 * @author David Grace (dave@dutchie.net)
 */
@SuppressWarnings(
{ "unchecked", "unused", "hiding" })
public class JOALMixer extends AudioEngine3DL2 implements AudioDevice, AudioDevice3D, AudioDevice3DL2
{

	// Debug boolean
	private static boolean debug = false;

	private static boolean debugVersion = true;

	private static boolean debugView = false;

	private static boolean debugPrepareSound = true;

	private static boolean debugGetTotalChannels = false;

	private static boolean debugSampleDuration = false;

	private static boolean debugVelocity = false;

	private static boolean debugPosition = false;

	private static boolean debugDirection = false;

	private static boolean debugDistanceGain = false;

	private static boolean debugGain = false;

	private static boolean debugLoopCount = false;

	private static boolean debugMute = true;

	private static boolean debugUnmute = true;

	private static boolean debugStart = true;

	private static boolean debugStartTime = true;

	private static boolean debugStop = false;

	private static boolean debugClearSound = true;

	// Dictates whether or not buffers are shared between samples
	private boolean shareBuffer = true;

	private HashMap<Object, Object> sharedBuffers = new HashMap<Object, Object>();

	private boolean calculateDopplerEffect = false;

	// Determines method to call for added or setting sound into ArrayList
	static final int ADD_TO_LIST = 1;

	static final int SET_INTO_LIST = 2;

	// AL for access to JOAL
	static AL al;

	static ALC alc;

	// Temp arrays for passing data from Java to JOAL
	private float[] singleArray = new float[1];

	// private float[] tripleArray = new float[3];

	// Java3D View data
	// private View reference;
	private Transform3D transform = new Transform3D();

	private float[] position = new float[3];

	private float[] lastPosition = new float[3];

	private Vector3f positionVector = new Vector3f();

	// private Vector3f lastPositionVector = new Vector3f();
	private Vector3f viewVector = new Vector3f(0, 0, -1);

	private Vector3f upVector = new Vector3f(0, 1, 0);

	// JOAL listener data

	private float[] velocity =
	{ 0.0f, 0.0f, 0.0f };

	private float[] noVelocity =
	{ 0.0f, 0.0f, 0.0f };

	private float[] orientation =
	{ 0.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f };

	private long timeOfThisViewUpdate = -1;

	private long timeSinceLastViewUpdate = -1;

	private float timeSinceLastViewUpdateInSeconds = -1;

	private long timeOfLastViewUpdate = -1;

	/** Creates a new instance of JOALMixer */
	public JOALMixer(PhysicalEnvironment physicalEnvironment)
	{
		super(physicalEnvironment);
		if (debug)
			System.out.println("JOALMixer - constructor...");
		// initialize();
		// if (debug & debugVersion) System.out.println("JOAL - AL_DOPPLER_FACTOR: " +
		// al.alGetFloat(AL.AL_DOPPLER_FACTOR));
		// if (debug & debugVersion) System.out.println("JOAL - AL_SPEED_OF_SOUND: " +
		// al.alGetFloat(AL.AL_SPEED_OF_SOUND));
		// if (debug & debugVersion) System.out.println("JOAL - AL_DISTANCE_MODEL: " +
		// al.alGetInteger(AL.AL_DISTANCE_MODEL));
	}

	/**
	 * Set overall gain control of all sounds playing on the audio device.
	 * 
	 * @param scaleFactor
	 *            scale factor applied to calculated amplitudes for all sounds playing on this device
	 */
	public void setGain(float scaleFactor)
	{
		singleArray[0] = scaleFactor;
		al.alListenerfv(ALConstants.AL_GAIN, singleArray, 0);
	}

	/**
	 * NOT IMPLEMENTED <br>
	 * Resumes audio device engine (if previously paused) without reinitializing the device. Causes all paused cached
	 * sounds to be resumed and all streaming sounds restarted.
	 */
	public void resume()
	{

	}

	/**
	 * NOT IMPLEMENTED <br>
	 * Pauses audio device engine without closing the device and associated threads. Causes all cached sounds to be
	 * paused and all streaming sounds to be stopped.
	 */
	public void pause()
	{

	}

	/**
	 * Save a reference to the current View object.
	 * 
	 * @param reference
	 *            to current view object
	 */
	public void setView(View reference)
	{
		if (debug && debugView)
			System.out.println("JOALMixer - setView...");
		if (reference.getAllCanvas3Ds().hasNext())
		{
			reference.getUserHeadToVworld(transform);
			transform.get(positionVector);
			position[0] = positionVector.x;
			position[1] = positionVector.y;
			position[2] = positionVector.z;
			al.alListenerfv(ALConstants.AL_POSITION, position, 0);

			// Update velocity information
			if (timeOfLastViewUpdate == -1)
			{
				timeOfLastViewUpdate = System.nanoTime();
				lastPosition[0] = positionVector.x;
				lastPosition[1] = positionVector.y;
				lastPosition[2] = positionVector.z;
			}
			else
			{
				timeOfThisViewUpdate = System.nanoTime();
				timeSinceLastViewUpdate = timeOfLastViewUpdate - timeOfThisViewUpdate;
				timeOfLastViewUpdate = timeOfThisViewUpdate;

				if (calculateDopplerEffect)
				{
					timeSinceLastViewUpdateInSeconds = timeSinceLastViewUpdate / 1000000;
					velocity[0] = (lastPosition[0] - position[0]) / timeSinceLastViewUpdateInSeconds;
					velocity[1] = (lastPosition[1] - position[1]) / timeSinceLastViewUpdateInSeconds;
					velocity[2] = (lastPosition[2] - position[2]) / timeSinceLastViewUpdateInSeconds;
					al.alListenerfv(ALConstants.AL_VELOCITY, velocity, 0);
					if (debug & debugVelocity)
						System.out.println("JOALMixer - velocity: " + velocity[0] + ", " + velocity[1] + ", " + velocity[2]);
				}
				else
				{
					al.alListenerfv(ALConstants.AL_VELOCITY, noVelocity, 0);
				}

				lastPosition[0] = positionVector.x;
				lastPosition[1] = positionVector.y;
				lastPosition[2] = positionVector.z;
			}
			// Vector3f viewVector= new Vector3f(0, 0, -1);
			// Vector3f upVector = new Vector3f(0, 1, 0);
			viewVector.set(0, 0, -1);
			upVector.set(0, 1, 0);
			// get viewVector
			transform.transform(viewVector);

			// get upVector
			transform.transform(upVector);
			orientation[0] = viewVector.x;
			orientation[1] = viewVector.y;
			orientation[2] = viewVector.z;
			orientation[3] = upVector.x;
			orientation[4] = upVector.y;
			orientation[5] = upVector.z;

			al.alListenerfv(ALConstants.AL_ORIENTATION, orientation, 0);
		}
		super.setView(reference);
	}

	/**
	 * Prepare Sound in device <br>
	 * Makes sound assessible to device - in this case attempts to load sound Stores sound type and data.
	 * 
	 * @param soundType
	 *            denotes type of sound: Background, Point or Cone
	 * @param soundData
	 *            descrition of sound source data
	 * @return index into sample vector of Sample object for sound
	 */

	public int prepareSound(int soundType, MediaContainer soundData)
	{
		if (debug && debugPrepareSound)
		{
			if (soundData.getURLObject() != null)
			{
				System.out.println("JOALMixer - prepareSound - " + soundData + " - " + soundData.getURLObject());
			}
			else if (soundData.getURLString() != null)
			{
				System.out.println("JOALMixer - prepareSound - " + soundData + " - " + soundData.getURLString());
			}
			else
				System.out.println("JOALMixer - prepareSound - " + soundData + " - " + soundData.getInputStream());
		}
		int index = JOALSample.NULL_SAMPLE;
		int methodType = ADD_TO_LIST;

		if (soundData == null)
			return JOALSample.NULL_SAMPLE;
		synchronized (samples)
		{
			int samplesSize = samples.size();
			index = samplesSize;
			samples.ensureCapacity(index + 1);

			JOALSample joalSample = new JOALSample();
			boolean error = true;

			// Code added here to address bug id 500 - JOALMixer should share buffers.
			// If the MediaContainer has a URLObject and has been loaded before
			// then the same buffer will be used.
			// As yet I am unable to determine when a buffer should be removed
			// from the HashMap to release the buffer from memory

			if (shareBuffer)
			{
				if (soundData.getURLObject() != null)
				{
					if (sharedBuffers.containsKey(soundData.getURLObject()))
					{
						error = joalSample.load(al, (int[]) sharedBuffers.get(soundData.getURLObject()), soundType);
					}
					else
					{
						error = joalSample.load(al, soundData, soundType);
						sharedBuffers.put(soundData.getURLObject(), joalSample.getBuffer());
					}
				}
				else if (soundData.getURLString() != null)
				{
					if (sharedBuffers.containsKey(soundData.getURLString()))
					{
						error = joalSample.load(al, (int[]) sharedBuffers.get(soundData.getURLString()), soundType);
					}
					else
					{
						error = joalSample.load(al, soundData, soundType);
						sharedBuffers.put(soundData.getURLString(), joalSample.getBuffer());
					}
				}
				else
				{
					if (sharedBuffers.containsKey(soundData.getInputStream()))
					{
						error = joalSample.load(al, (int[]) sharedBuffers.get(soundData.getInputStream()), soundType);
					}
					else
					{
						error = joalSample.load(al, soundData, soundType);
						sharedBuffers.put(soundData.getInputStream(), joalSample.getBuffer());
					}
				}

			}
			else
			{
				error = joalSample.load(al, soundData, soundType);
			}

			if (error)
				return JOALSample.NULL_SAMPLE;
			if (methodType == SET_INTO_LIST)
				samples.set(index, joalSample);
			else
				samples.add(index, joalSample);
			if (debug)
				System.out.println("JOALMixer - prepareSound - return: " + index);
			return index;
		}
	}

	/**
	 * Clear Sound. Removes/clears associated sound data with this sound source node
	 * 
	 * @param index
	 *            device specific reference number to device driver sample
	 */
	public void clearSound(int index)
	{
		if (debug && debugClearSound)
			System.out.println("JOALMixer - clearSound " + index);
		Sample sample = null;
		if ((sample = getSample(index)) == null)
			return;
		sample.clear();
		synchronized (samples)
		{
			samples.set(index, null);
		}
	}

	/**
	 * Get length of time a sample would play if allowed to play to completion.
	 * 
	 * @param index
	 *            device specific reference number to device driver sample
	 * @return length of sample in milliseconds
	 */
	public long getSampleDuration(int index)
	{
		if (debug && debugSampleDuration)
			System.out.println("JOALMixer - getSampleDuration for " + index);
		return super.getSampleDuration(index);
	}

	/**
	 * NOT IMPLEMENTED <br>
	 * Get number of channels used by a particular sample on the audio device.
	 * 
	 * @param index
	 *            device specific reference number to device driver sample
	 * @return number of channels currently being used by this sample.
	 */
	public int getNumberOfChannelsUsed(int index)
	{
		// if (debug) System.out.println("JOALMixer - getNumberOfChannelsUsed...");
		return super.getNumberOfChannelsUsed(index);
	}

	/**
	 * NOT IMPLEMENTED <br>
	 * Get number of channels that would be used by a particular sample on the audio device given the mute flag passed
	 * in as a parameter.
	 * 
	 * @param index
	 *            device specific reference number to device driver sample
	 * @param muteFlag
	 *            denotes the mute state to assume while executing this query. This mute value does not have to match
	 *            the current mute state of the sample.
	 * @return number of channels that would be used by this sample if it were playing.
	 */
	public int getNumberOfChannelsUsed(int index, boolean muteFlag)
	{
		// if (debug) System.out.println("JOALMixer - getNumberOfChannelsUsed...boolean");
		return 1;
	}

	/**
	 * Start sample playing on audio device
	 * 
	 * @param index
	 *            device specific reference number to device driver sample
	 * @return status: < 0 denotes an error
	 */
	public int startSample(int index)
	{
		if (debug & debugStart)
			System.out.println("JOALMixer - start..." + index);
		// super.startSample(index);
		JOALSample s = (JOALSample) getSample(index);
		if (s == null)
			return -1;
		else
			return s.startSample();
	}

	/**
	 * NOT IMPLEMENTED <br>
	 * Get time this sample begun playing on the audio device.
	 * 
	 * @param index
	 *            device specific reference number to device driver sample
	 * @return system clock time sample started
	 */
	public long getStartTime(int index)
	{
		if (debug && debugStartTime)
			System.out.println("JOALMixer - getStartTime for " + index);
		return -1;
	}

	/**
	 * Stop sample playing on audio device
	 * 
	 * @param index
	 *            device specific reference number to device driver sample
	 * @return status: < 0 denotes an error
	 */
	public int stopSample(int index)
	{
		if (debug & debugStop)
			System.out.println("JOALMixer - stopSample " + index);
		JOALSample s = (JOALSample) getSample(index);
		if (s == null)
			return -1;
		else
			return s.stopSample();
	}

	/**
	 * Set gain scale factor applied to sample.
	 * 
	 * @param index
	 *            device specific reference number to device driver sample
	 * @param scaleFactor
	 *            floating point multiplier applied to sample amplitude
	 */
	public void setSampleGain(int index, float scaleFactor)
	{
		if (debug & debugGain)
			System.out.println("JOALMixer - setSampleGain for " + index + " to " + scaleFactor);
		JOALSample s = (JOALSample) getSample(index);
		if (s == null)
			return;
		else
			s.setGain(scaleFactor);
	}

	/**
	 * NOT IMPLEMENTED COMPLETELY <br>
	 * Set number of times sample is looped <br>
	 * Works for values 0 (no loop) and (-1) infinite loop but not for a fix number of loops (>1) <br>
	 * 
	 * @param index
	 *            device specific reference number to device driver sample
	 * @param count
	 *            number of times sample is repeated
	 */
	public void setLoop(int index, int count)
	{
		if (debug & debugLoopCount)
			System.out.println("JOALMixer - setLoop for " + index + " to " + count);
		super.setLoop(index, count);
	}

	/**
	 * DOES NOTHING - does not need to do anything special for JOAL<br>
	 * Set the transform for local to virtual world coordinate space
	 * 
	 * @param index
	 *            device specific reference number to device driver sample
	 * @param trans
	 *            is a reference to virtual world composite transform
	 */
	public void setVworldXfrm(int index, Transform3D trans)
	{
		if (debug)
			System.out.println("JOALMixer - setVworldXfrm...");
		super.setVworldXfrm(index, trans);
	}

	/**
	 * Set location of sample.
	 * 
	 * @param index
	 *            device specific reference number to device driver sample
	 * @param position
	 *            point location in virtual world coordinate of sample
	 */
	public void setPosition(int index, Point3d position)
	{
		if (debug & debugPosition)
			System.out.println("JOALMixer - setPosition for " + index + " to " + position);
		super.setPosition(index, position);
	}

	/**
	 * Set elliptical distance attenuation arrays applied to sample amplitude<br>
	 * OPENAL/JOAL calculates the distance attenuation using its default model which is the inverse distance clamped
	 * model (equivalent to the IASIG I3DL2 distance model) <br>
	 * This is: <br>
	 * distance = max(distance, AL_REFERENCE_DISTANCE); <br>
	 * distance = max(distance, AL_MAX_DISTANCE); <br>
	 * gain = AL_REFERENCE_DISTANCE / (AL_REFERENCE_DISTANCE + AL_ROLLOFF_FACTOR * (distance - AL_REFERENCE_DISTANCE));
	 * <br>
	 * This function calculates the AL_REFERENCE_DISTANCE, AL_MAX_DISTANCE and AL_ROLLOFF_FACTOR from the given distance
	 * attenuation data from Java3D. <br>
	 * 
	 * @param index
	 *            device specific reference number to device driver sample
	 * @param frontDistance
	 *            defines an array of distance along the position axis thru which ellipses pass
	 * @param frontAttenuationScaleFactor
	 *            gain scale factors
	 * @param backDistance
	 *            defines an array of distance along the negative axis thru which ellipses pass
	 * @param backAttenuationScaleFactor
	 *            gain scale factors
	 */
	public void setDistanceGain(int index, double[] frontDistance, float[] frontAttenuationScaleFactor, double[] backDistance,
			float[] backAttenuationScaleFactor)
	{
		if (debug & debugDistanceGain)
			System.out.println("JOALMixer - setDistanceGain for " + index + " with " + frontDistance + ", " + frontAttenuationScaleFactor
					+ ", " + backDistance + ", " + backAttenuationScaleFactor);
		super.setDistanceGain(index, frontDistance, frontAttenuationScaleFactor, backDistance, backAttenuationScaleFactor);
	}

	/**
	 * Set direction vector of sample.
	 * 
	 * @param index
	 *            device specific reference number to device driver sample
	 * @param direction
	 *            vector in virtual world coordinate.
	 */
	public void setDirection(int index, Vector3d direction)
	{
		if (debug & debugDirection)
			System.out.println("JOALMixer - setDirection for " + index + " to " + direction);
		super.setDirection(index, direction);
	}

	/**
	 * Set angular attenuation arrays affecting angular amplitude attenuation and angular distance filtering <br>
	 * Angular attenuation in OpenAL/JOAL is determined by AL_CONE_INNER_ANGLE, AL_CONE_OUTER_ANGLE and
	 * AL_CONE_OUTER_GAIN <br>
	 * The region inside and AL_CONE_OUTER_ANGLE has its gain unchanged (AL_GAIN), the region inside AL_CONE_INNER_ANGLE
	 * and AL_CONE_OUTER_ANGLE has its gain between AL_GAIN and AL_CONE_OUTER_GAIN, and the region outside
	 * AL_CONE_OUTER_ANGLE has gain of AL_CONE_OUTER_GAIN <br>
	 * This function calculates the AL_CONE_INNER_ANGLE, AL_CONE_OUTER_ANGLE and AL_CONE_OUTER_GAIN from the given
	 * angular attenuation data from Java3D. <br>
	 * 
	 * @param index
	 *            device specific reference number to device driver sample
	 * @param filterType
	 *            denotes type of filtering (on no filtering) applied to sample.
	 * @param angle
	 *            array containing angular distances from sound axis
	 * @param attenuationScaleFactor
	 *            array containing gain scale factor
	 * @param filterCutoff
	 *            array containing filter cutoff frequencies. The filter values for each tuples can be set to
	 *            Sound.NO_FILTER.
	 */
	public void setAngularAttenuation(int index, int filterType, double[] angle, float[] attenuationScaleFactor, float[] filterCutoff)
	{
		if (debug)
			System.out.println("JOALMixer - setAngularAttenuation...");
		super.setAngularAttenuation(index, filterType, angle, attenuationScaleFactor, filterCutoff);
	}

	/**
	 * NOT IMPLEMENTED <br>
	 * Set rolloff value for current aural attribute applied to all samples.
	 * 
	 * @param rolloff
	 *            scale factor applied to standard speed of sound.
	 */
	public void setRolloff(float rolloff)
	{
		if (debug)
			System.out.println("JOALMixer - setRolloff...");
	}

	/**
	 * NOT IMPLEMENTED <br>
	 * Set reverberation surface reflection coefficient value for current aural attribute applied to all samples.
	 * 
	 * @param coefficient
	 *            applied to amplitude of reverbation added at each iteration of reverb processing.
	 */
	public void setReflectionCoefficient(float coefficient)
	{
		if (debug)
			System.out.println("JOALMixer - setReflectionCoefficient...");
	}

	/**
	 * NOT IMPLEMENTED <br>
	 * Set reverberation delay time for current aural attribute applied to all samples.
	 * 
	 * @param reverbDelay
	 *            amount of time in millisecond between each iteration of reverb processing.
	 */
	public void setReverbDelay(float reverbDelay)
	{
		if (debug)
			System.out.println("JOALMixer - setReverbDelay...");
	}

	/**
	 * NOT IMPLEMENTED <br>
	 * Set reverberation order for current aural attribute applied to all samples.
	 * 
	 * @param reverbOrder
	 *            number of times reverb process loop is iterated.
	 */
	public void setReverbOrder(int reverbOrder)
	{
		if (debug)
			System.out.println("JOALMixer - setReverbOrder...");
	}

	/**
	 * NOT IMPLEMENTED <br>
	 * Set distance filter for current aural attribute applied to all samples.
	 * 
	 * @param filterType
	 *            denotes type of filtering (on no filtering) applied to all sample based on distance between listener
	 *            and sound.
	 * @param dist
	 *            is an attenuation array of distance and low-pass filter values.
	 */
	public void setDistanceFilter(int filterType, double[] distance, float[] dist)
	{
		if (debug)
			System.out.println("JOALMixer - setDistanceFilter...");
	}

	/**
	 * NOT IMPLEMENTED <br>
	 * Set frequency scale factor for current aural attribute applied to all samples.
	 * 
	 * @param frequencyScaleFactor
	 *            frequency scale factor applied to samples normal playback rate.
	 */
	public void setFrequencyScaleFactor(float frequencyScaleFactor)
	{
		if (debug)
			System.out.println("JOALMixer - setFrequencyScaleFactor to " + frequencyScaleFactor);
	}

	/**
	 * NOT IMPLEMENTED <br>
	 * Set velocity scale factor for current aural attribute applied to all samples when Doppler is calculated.
	 * 
	 * @param velocityScaleFactor
	 *            scale factor applied to postional samples' listener-to-soundSource velocity. playback rate.
	 */
	public void setVelocityScaleFactor(float velocityScaleFactor)
	{
		if (debug)
			System.out.println("JOALMixer - setVelocityScaleFactor to " + velocityScaleFactor);
		super.setVelocityScaleFactor(velocityScaleFactor);
	}

	/**
	 * Mute sample.
	 * 
	 * @param index
	 *            device specific reference number to device driver sample
	 */
	public void muteSample(int index)
	{
		if (debug && debugMute)
			System.out.println("JOALMixer - muteSample " + index);
		Sample sample = getSample(index);
		if (sample != null)
			sample.setMuteFlag(true);
	}

	/**
	 * Unmute sample.
	 * 
	 * @param index
	 *            device specific reference number to device driver sample
	 */
	public void unmuteSample(int index)
	{
		if (debug && debugUnmute)
			System.out.println("JOALMixer - unmuteSample for " + index);
		Sample sample = getSample(index);
		if (sample != null)
			sample.setMuteFlag(false);
	}

	/**
	 * Pause sample.
	 * 
	 * @param index
	 *            device specific reference number to device driver sample
	 */
	public void pauseSample(int index)
	{
		if (debug)
			System.out.println("JOALMixer - pauseSample for " + index);
		al.alSourcePause(index);
	}

	/**
	 * Unpause sample.
	 * 
	 * @param index
	 *            device specific reference number to device driver sample
	 */
	public void unpauseSample(int index)
	{
		if (debug)
			System.out.println("JOALMixer - unpauseSample for " + index);
		al.alSourcePlay(index);
	}

	/**
	 * DOES NOTHING - not really needed when using JOAL <br>
	 * Update sample Implies that some parameters affecting rendering have been modified.
	 * 
	 * @param index
	 *            device specific reference number to device driver sample
	 */
	public void updateSample(int index)
	{
		// if (debug) System.out.println("JOALMixer - updateSample " + index);
	}

	/**
	 * Code to initialize the device
	 * 
	 * @return flag: true is initialized sucessfully, false if error
	 */
	public boolean initialize()
	{
		if (debug)
			System.out.println("JOALMixer - initialize...");

		try
		{
			ALut.alutInit();
			al = ALFactory.getAL();
			al.alDistanceModel(ALConstants.AL_INVERSE_DISTANCE);
			// if (debug & debugVersion) System.out.println("JOALMixer - JOAL version: " + Version.getVersion());
			if (debug & debugVersion)
				System.out.println("JOALMixer - JOAL renderer: " + al.alGetString(ALConstants.AL_RENDERER));
			if (debug & debugVersion)
				System.out.println("JOALMixer - JOAL vendor: " + al.alGetString(ALConstants.AL_VENDOR));
			if (debug & debugVersion)
				System.out.println("JOALMixer - JOAL extension: " + al.alGetString(ALConstants.AL_EXTENSIONS));

		}
		catch (ALException e)
		{
			e.printStackTrace();
			if (debug)
				System.out.println("JOALMixer - initialize failed - Error initializing JOALMixer, code: " + al.alGetError());
			return false;
		}
		if (debug)
			System.out.println("JOALMixer - initialize success.");
		return true;
	}

	/**
	 * NOT IMPLEMENTED <br>
	 * Code to close the device
	 * 
	 * @return flag: true is closed sucessfully, false if error
	 */
	public boolean close()
	{

		if (debug)
			System.out.println("JOALMixer - close...");
		// Not sure if all the samples should be clear(ed), if yes then uncomment
		/*
		 * int index = 0; synchronized(samples) { for (Object s:samples){ Sample sample = (Sample) s; if ( (sample =
		 * getSample(index)) == null){ } else { sample.clear(); samples.set(index, null); } } } return true;
		 */

		return false;
	}

	/**
	 * Set Type of Audio Playback physical transducer(s) sound is output to <br>
	 * Valid types are HEADPHONE, MONO_SPEAKER, STEREO_SPEAKERS
	 * 
	 * @param type
	 *            of audio output device
	 */
	public void setAudioPlaybackType(int type)
	{
		if (debug)
			System.out.println("JOALMixer - setAudioPlaybackType to " + type);
		super.setAudioPlaybackType(type);
	}

	/**
	 * Get Type of Audio Playback Output Device returns audio playback type to which sound is currently output
	 */
	public int getAudioPlaybackType()
	{
		if (debug)
			System.out.println("JOALMixer - getAudioPlaybackType...");
		return super.getAudioPlaybackType();
	}

	/**
	 * Set Distance from the Center Ear to a Speaker
	 * 
	 * @param distance
	 *            from the center ear and to the speaker
	 */
	public void setCenterEarToSpeaker(float distance)
	{
		if (debug)
			System.out.println("JOALMixer - setCenterEarToSpeaker to " + distance);
		super.setCenterEarToSpeaker(distance);
	}

	/**
	 * Get Distance from Ear to Speaker returns value set as distance from listener's ear to speaker
	 */
	public float getCenterEarToSpeaker()
	{
		if (debug)
			System.out.println("JOALMixer - getCenterEarToSpeaker...");
		return super.getCenterEarToSpeaker();
	}

	/**
	 * Set Angle Offset To Speaker
	 * 
	 * @param angle
	 *            in radian between head coordinate Z axis and vector to speaker
	 */
	public void setAngleOffsetToSpeaker(float angle)
	{
		if (debug)
			System.out.println("JOALMixer - setAngleOffsetToSpeaker " + angle);
		super.setAngleOffsetToSpeaker(angle);
	}

	/**
	 * Get Angle Offset To Speaker returns value set as angle between vector to speaker and Z head axis
	 */
	public float getAngleOffsetToSpeaker()
	{
		if (debug)
			System.out.println("JOALMixer - getAngleOffsetToSpeaker...");
		return super.getAngleOffsetToSpeaker();
	}

	/**
	 * NOT IMPLEMENTED <br>
	 * Query total number of channels available for sound rendering for this audio device. returns number of maximum
	 * sound channels you can run with this library/device-driver.
	 */
	public int getTotalChannels()
	{
		if (debug && debugGetTotalChannels)
			System.out.println("JOALMixer - getTotalChannels...");
		return 64;
	}

	/**
	 * NOT IMPLEMENTED <br>
	 * Query number of channels currently available for use by the returns number of sound channels currently available
	 * (number not being used by active sounds).
	 */
	public int getChannelsAvailable()
	{
		if (debug)
			System.out.println("JOALMixer - getChannelsAvailable...");
		return 8;
	}

	/**
	 * Query number of channels that would be used to render a particular sound node.
	 * 
	 * @param sound
	 *            refenence to sound node that query to be performed on returns number of sound channels used by a
	 *            specific Sound node
	 * @deprecated This method is now part of the Sound class
	 */
	public int getChannelsUsedForSound(Sound sound)
	{
		if (debug)
			System.out.println("JOALMixer - getChannelsUsedForSound...");
		return super.getChannelsUsedForSound(sound);
	}

	/**
	 * Set scale factor applied to sample playback rate for a particular sound associated with the audio device <br>
	 * Changing the device sample rate affects both the pitch and speed. This scale factor is applied to ALL sound types
	 * <br>
	 * Changes (scales) the playback rate of a sound independent of Doppler rate changes.
	 * 
	 * @param index
	 *            device specific reference to device driver sample
	 * @param scaleFactor
	 *            non-negative factor applied to calculated amplitudes for all sounds playing on this device
	 * @see Sound#setRateScaleFactor
	 */
	public void setRateScaleFactor(int index, float scaleFactor)
	{
		JOALSample s = (JOALSample) getSample(index);
		if (s == null)
			return;
		else
			s.setRateScaleFactor(scaleFactor);
	}

	/**
	 * NOT IMPLEMENTED <br>
	 * Set late reflection (referred to as 'reverb') attenuation. This scale factor is applied to iterative,
	 * indistinguishable late reflections that constitute the tail of reverberated sound in the aural environment <br>
	 * This parameter, along with the early reflection coefficient, defines the reflective/absorptive characteristic of
	 * the surfaces in the current listening region.
	 * 
	 * @param coefficient
	 *            late reflection attenuation factor
	 * @see AuralAttributes#setReverbCoefficient
	 */
	public void setReverbCoefficient(float coefficient)
	{
	}

	/**
	 * NOT IMPLEMENTED Sets the early reflection delay time <br>
	 * In this form, the parameter specifies the delay time between each order of reflection (while reverberation is
	 * being rendered) explicitly given in milliseconds.
	 * 
	 * @param reflectionDelay
	 *            time between each order of early reflection
	 * @see AuralAttributes#setReflectionDelay
	 */
	public void setReflectionDelay(float reflectionDelay)
	{
	}

	/**
	 * NOT IMPLEMENTED <br>
	 * Set reverb decay time <br>
	 * Defines the reverberation decay curve.
	 * 
	 * @param time
	 *            decay time in milliseconds
	 * @see AuralAttributes#setDecayTime
	 */
	public void setDecayTime(float time)
	{
	}

	/**
	 * NOT IMPLEMENTED <br>
	 * Set reverb decay filter <br>
	 * This provides for frequencies above the given cutoff frequency to be attenuated during reverb decay at a
	 * different rate than frequencies below this value <br>
	 * Thus, defining a different reverb decay curve for frequencies above the cutoff value.
	 * 
	 * @param frequencyCutoff
	 *            value of frequencies in Hertz above which a low-pass filter is applied.
	 * @see AuralAttributes#setDecayFilter
	 */
	public void setDecayFilter(float frequencyCutoff)
	{
	}

	/**
	 * NOT IMPLEMENTED <br>
	 * Set reverb diffusion <br>
	 * This defines the echo dispersement (also referred to as 'echo density') <br>
	 * The value of this reverb parameter is expressed as a percent of the audio device's minimum-to-maximum values.
	 * 
	 * @param diffusion
	 *            percentage expressed within the range of 0.0 and 1.0
	 * @see AuralAttributes#setDiffusion
	 */
	public void setDiffusion(float diffusion)
	{
	}

	/**
	 * NOT IMPLEMENTED <br>
	 * Set reverb density <br>
	 * This defines the modal density (also referred to as 'spectral coloration') <br>
	 * The value of this parameter is expressed as a percent of the audio device's minimum-to-maximum values for this
	 * reverb parameter.
	 * 
	 * @param density
	 *            reverb density expressed as a percentage, within the range of 0.0 and 1.0
	 * @see AuralAttributes#setDensity
	 */
	public void setDensity(float density)
	{
	}

	/**
	 * NOT IMPLEMENTED <br>
	 * Set the obstruction gain control <br>
	 * This method allows for attenuating sound waves traveling between the sound source and the listener obstructed by
	 * objects <br>
	 * Direct sound signals/waves for obstructed sound source are attenuated but not indirect (reflected) waves. There
	 * is no corresponding Core AuralAttributes method at this time.
	 * 
	 * @param index
	 *            device specific reference to device driver sample
	 * @param scaleFactor
	 *            non-negative factor applied to direct sound gain
	 */
	public void setObstructionGain(int index, float scaleFactor)
	{
	}

	/**
	 * NOT IMPLEMENTED <br>
	 * Set the obstruction filter control <br>
	 * This provides for frequencies above the given cutoff frequency to be attenuated, during while the gain of an
	 * obstruction signal is being calculated, at a different rate than frequencies below this value. There is no
	 * corresponding Core AuralAttributes method at this time.
	 * 
	 * @param index
	 *            device specific reference to device driver sample
	 * @param frequencyCutoff
	 *            value of frequencies in Hertz above which a low-pass filter is applied.
	 */
	public void setObstructionFilter(int index, float frequencyCutoff)
	{
	}

	/**
	 * NOT IMPLEMENTED <br>
	 * Set the occlusion gain control <br>
	 * This method allows for attenuating sound waves traveling between the sound source and the listener occluded by
	 * objects <br>
	 * Both direct and indirect sound signals/waves for occluded sound sources are attenuated <br>
	 * There is no corresponding Core AuralAttributes method at this time.
	 * 
	 * @param index
	 *            device specific reference to device driver sample
	 * @param scaleFactor
	 *            non-negative factor applied to direct sound gain
	 */
	public void setOcclusionGain(int index, float scaleFactor)
	{
	}

	/**
	 * NOT IMPLEMENTED <br>
	 * Set the occlusion filter control <br>
	 * This provides for frequencies above the given cutoff frequency to be attenuated, during while the gain of an
	 * occluded signal is being calculated, at a different rate than frequencies below this value <br>
	 * There is no corresponding Core AuralAttributes method at this time.
	 * 
	 * @param index
	 *            device specific reference to device driver sample
	 * @param frequencyCutoff
	 *            value of frequencies in Hertz above which a low-pass filter is applied.
	 */
	public void setOcclusionFilter(int index, float frequencyCutoff)
	{
	}

	/**
	 * Unused code for more specific initialization of a sound device. Maybe useful later if the option of selecting
	 * which device on the machine is used. At the moment it is the 'default' device returned by OpenAL/JOAL that is
	 * used.
	 */
	static int initOpenAL()
	{

		ALCdevice device;
		ALCcontext context;
		String deviceSpecifier;
		String deviceName = "DirectSound3D";

		// Get handle to device.
		device = alc.alcOpenDevice(deviceName);

		// Get the device specifier.
		deviceSpecifier = alc.alcGetString(device, ALCConstants.ALC_DEVICE_SPECIFIER);

		System.out.println("Using device " + deviceSpecifier);

		// Create audio context.
		context = alc.alcCreateContext(device, null);

		// Set active context.
		alc.alcMakeContextCurrent(context);

		// Check for an error.
		if (alc.alcGetError(device) != ALCConstants.ALC_NO_ERROR)
			return ALConstants.AL_FALSE;

		return ALConstants.AL_TRUE;
	}

	/**
	 * Unused code for to release resources created with the function above. Maybe useful later if the option of
	 * selecting which device on the machine is used. This would be used to close device.
	 */
	public static void exitOpenAL()
	{
		try
		{
			if (alc != null)
			{
				ALCcontext curContext;
				ALCdevice curDevice;

				// Get the current context.
				curContext = alc.alcGetCurrentContext();

				// Get the device used by that context.
				curDevice = alc.alcGetContextsDevice(curContext);

				// Reset the current context to NULL.
				alc.alcMakeContextCurrent(null);

				// Release the context and the device.
				alc.alcDestroyContext(curContext);
				alc.alcCloseDevice(curDevice);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void calculateOrientation(View view)
	{
		Vector3f viewPosition = new Vector3f();

		Vector3f viewVector = new Vector3f(0, 0, -1);
		Vector3f upVector = new Vector3f(0, 1, 0);

		Transform3D viewTransform = new Transform3D();
		view.getUserHeadToVworld(viewTransform);

		// get position
		viewTransform.get(viewPosition);

		// get viewVector
		viewTransform.transform(viewVector);

		// get upVector
		viewTransform.transform(upVector);

		System.out.println("Position: " + viewPosition);
		System.out.println("viewVector: " + viewVector);
		System.out.println("upVector: " + upVector);
	}

}
