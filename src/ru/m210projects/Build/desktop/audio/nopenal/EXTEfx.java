/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 * MACHINE GENERATED FILE, DO NOT EDIT
 */
package ru.m210projects.Build.desktop.audio.nopenal;

import static ru.m210projects.Build.desktop.audio.nopenal.AL.*;

import java.nio.*;

import org.lwjgl.BufferUtils;

/**
 * Native bindings to the {@code ALC_EXT_EFX} extension.
 * 
 * <p>The Effects Extension is designed to provide a generic, cross-platform framework for adding advanced DSP effects to OpenAL.</p>
 */
public class EXTEfx {

    /** ALC tokens. */
    public static final int
        ALC_EFX_MAJOR_VERSION   = 0x20001,
        ALC_EFX_MINOR_VERSION   = 0x20002,
        ALC_MAX_AUXILIARY_SENDS = 0x20003;

    /** Listener properties. */
    public static final int AL_METERS_PER_UNIT = 0x20004;

    /** Source properties. */
    public static final int
        AL_DIRECT_FILTER                     = 0x20005,
        AL_AUXILIARY_SEND_FILTER             = 0x20006,
        AL_AIR_ABSORPTION_FACTOR             = 0x20007,
        AL_ROOM_ROLLOFF_FACTOR               = 0x20008,
        AL_CONE_OUTER_GAINHF                 = 0x20009,
        AL_DIRECT_FILTER_GAINHF_AUTO         = 0x2000A,
        AL_AUXILIARY_SEND_FILTER_GAIN_AUTO   = 0x2000B,
        AL_AUXILIARY_SEND_FILTER_GAINHF_AUTO = 0x2000C;

    /** Auxiliary effect slot properties. */
    public static final int
        AL_EFFECTSLOT_NULL                = 0x0,
        AL_EFFECTSLOT_EFFECT              = 0x1,
        AL_EFFECTSLOT_GAIN                = 0x2,
        AL_EFFECTSLOT_AUXILIARY_SEND_AUTO = 0x3;

    /** Reverb effect parameters. */
    public static final int
        AL_REVERB_DENSITY               = 0x1,
        AL_REVERB_DIFFUSION             = 0x2,
        AL_REVERB_GAIN                  = 0x3,
        AL_REVERB_GAINHF                = 0x4,
        AL_REVERB_DECAY_TIME            = 0x5,
        AL_REVERB_DECAY_HFRATIO         = 0x6,
        AL_REVERB_REFLECTIONS_GAIN      = 0x7,
        AL_REVERB_REFLECTIONS_DELAY     = 0x8,
        AL_REVERB_LATE_REVERB_GAIN      = 0x9,
        AL_REVERB_LATE_REVERB_DELAY     = 0xA,
        AL_REVERB_AIR_ABSORPTION_GAINHF = 0xB,
        AL_REVERB_ROOM_ROLLOFF_FACTOR   = 0xC,
        AL_REVERB_DECAY_HFLIMIT         = 0xD;

    /** EAX Reverb effect parameters. */
    public static final int
        AL_EAXREVERB_DENSITY               = 0x1,
        AL_EAXREVERB_DIFFUSION             = 0x2,
        AL_EAXREVERB_GAIN                  = 0x3,
        AL_EAXREVERB_GAINHF                = 0x4,
        AL_EAXREVERB_GAINLF                = 0x5,
        AL_EAXREVERB_DECAY_TIME            = 0x6,
        AL_EAXREVERB_DECAY_HFRATIO         = 0x7,
        AL_EAXREVERB_DECAY_LFRATIO         = 0x8,
        AL_EAXREVERB_REFLECTIONS_GAIN      = 0x9,
        AL_EAXREVERB_REFLECTIONS_DELAY     = 0xA,
        AL_EAXREVERB_REFLECTIONS_PAN       = 0xB,
        AL_EAXREVERB_LATE_REVERB_GAIN      = 0xC,
        AL_EAXREVERB_LATE_REVERB_DELAY     = 0xD,
        AL_EAXREVERB_LATE_REVERB_PAN       = 0xE,
        AL_EAXREVERB_ECHO_TIME             = 0xF,
        AL_EAXREVERB_ECHO_DEPTH            = 0x10,
        AL_EAXREVERB_MODULATION_TIME       = 0x11,
        AL_EAXREVERB_MODULATION_DEPTH      = 0x12,
        AL_EAXREVERB_AIR_ABSORPTION_GAINHF = 0x13,
        AL_EAXREVERB_HFREFERENCE           = 0x14,
        AL_EAXREVERB_LFREFERENCE           = 0x15,
        AL_EAXREVERB_ROOM_ROLLOFF_FACTOR   = 0x16,
        AL_EAXREVERB_DECAY_HFLIMIT         = 0x17;

    /** Chorus effect parameters. */
    public static final int
        AL_CHORUS_WAVEFORM = 0x1,
        AL_CHORUS_PHASE    = 0x2,
        AL_CHORUS_RATE     = 0x3,
        AL_CHORUS_DEPTH    = 0x4,
        AL_CHORUS_FEEDBACK = 0x5,
        AL_CHORUS_DELAY    = 0x6;

    /** Distortion effect parameters. */
    public static final int
        AL_DISTORTION_EDGE           = 0x1,
        AL_DISTORTION_GAIN           = 0x2,
        AL_DISTORTION_LOWPASS_CUTOFF = 0x3,
        AL_DISTORTION_EQCENTER       = 0x4,
        AL_DISTORTION_EQBANDWIDTH    = 0x5;

    /** Echo effect parameters. */
    public static final int
        AL_ECHO_DELAY    = 0x1,
        AL_ECHO_LRDELAY  = 0x2,
        AL_ECHO_DAMPING  = 0x3,
        AL_ECHO_FEEDBACK = 0x4,
        AL_ECHO_SPREAD   = 0x5;

    /** Flanger effect parameters. */
    public static final int
        AL_FLANGER_WAVEFORM = 0x1,
        AL_FLANGER_PHASE    = 0x2,
        AL_FLANGER_RATE     = 0x3,
        AL_FLANGER_DEPTH    = 0x4,
        AL_FLANGER_FEEDBACK = 0x5,
        AL_FLANGER_DELAY    = 0x6;

    /** Frequency shifter effect parameters. */
    public static final int
        AL_FREQUENCY_SHIFTER_FREQUENCY       = 0x1,
        AL_FREQUENCY_SHIFTER_LEFT_DIRECTION  = 0x2,
        AL_FREQUENCY_SHIFTER_RIGHT_DIRECTION = 0x3;

    /** Vocal morpher effect parameters. */
    public static final int
        AL_VOCMORPHER_PHONEMEA               = 0x1,
        AL_VOCMORPHER_PHONEMEA_COARSE_TUNING = 0x2,
        AL_VOCMORPHER_PHONEMEB               = 0x3,
        AL_VOCMORPHER_PHONEMEB_COARSE_TUNING = 0x4,
        AL_VOCMORPHER_WAVEFORM               = 0x5,
        AL_VOCMORPHER_RATE                   = 0x6;

    /** Pitch shifter effect parameters. */
    public static final int
        AL_PITCH_SHIFTER_COARSE_TUNE = 0x1,
        AL_PITCH_SHIFTER_FINE_TUNE   = 0x2;

    /** Ring modulator effect parameters. */
    public static final int
        AL_RING_MODULATOR_FREQUENCY       = 0x1,
        AL_RING_MODULATOR_HIGHPASS_CUTOFF = 0x2,
        AL_RING_MODULATOR_WAVEFORM        = 0x3;

    /** Autowah effect parameters. */
    public static final int
        AL_AUTOWAH_ATTACK_TIME  = 0x1,
        AL_AUTOWAH_RELEASE_TIME = 0x2,
        AL_AUTOWAH_RESONANCE    = 0x3,
        AL_AUTOWAH_PEAK_GAIN    = 0x4;

    /** Compressor effect parameters. */
    public static final int AL_COMPRESSOR_ONOFF = 0x1;

    /** Equalizer effect parameters. */
    public static final int
        AL_EQUALIZER_LOW_GAIN    = 0x1,
        AL_EQUALIZER_LOW_CUTOFF  = 0x2,
        AL_EQUALIZER_MID1_GAIN   = 0x3,
        AL_EQUALIZER_MID1_CENTER = 0x4,
        AL_EQUALIZER_MID1_WIDTH  = 0x5,
        AL_EQUALIZER_MID2_GAIN   = 0x6,
        AL_EQUALIZER_MID2_CENTER = 0x7,
        AL_EQUALIZER_MID2_WIDTH  = 0x8,
        AL_EQUALIZER_HIGH_GAIN   = 0x9,
        AL_EQUALIZER_HIGH_CUTOFF = 0xA;

    /** Effect type effect parameters. */
    public static final int
        AL_EFFECT_FIRST_PARAMETER = 0x0,
        AL_EFFECT_LAST_PARAMETER  = 0x8000,
        AL_EFFECT_TYPE            = 0x8001;

    /** Effect types */
    public static final int
        AL_EFFECT_NULL              = 0x0,
        AL_EFFECT_REVERB            = 0x1,
        AL_EFFECT_CHORUS            = 0x2,
        AL_EFFECT_DISTORTION        = 0x3,
        AL_EFFECT_ECHO              = 0x4,
        AL_EFFECT_FLANGER           = 0x5,
        AL_EFFECT_FREQUENCY_SHIFTER = 0x6,
        AL_EFFECT_VOCAL_MORPHER     = 0x7,
        AL_EFFECT_PITCH_SHIFTER     = 0x8,
        AL_EFFECT_RING_MODULATOR    = 0x9,
        AL_EFFECT_AUTOWAH           = 0xA,
        AL_EFFECT_COMPRESSOR        = 0xB,
        AL_EFFECT_EQUALIZER         = 0xC,
        AL_EFFECT_EAXREVERB         = 0x8000;

    /** Lowpass filter properties */
    public static final int
        AL_LOWPASS_GAIN   = 0x1,
        AL_LOWPASS_GAINHF = 0x2;

    /** Highpass filter properties */
    public static final int
        AL_HIGHPASS_GAIN   = 0x1,
        AL_HIGHPASS_GAINLF = 0x2;

    /** Bandpass filter properties */
    public static final int
        AL_BANDPASS_GAIN   = 0x1,
        AL_BANDPASS_GAINLF = 0x2,
        AL_BANDPASS_GAINHF = 0x3;

    /** Filter type */
    public static final int
        AL_FILTER_FIRST_PARAMETER = 0x0,
        AL_FILTER_LAST_PARAMETER  = 0x8000,
        AL_FILTER_TYPE            = 0x8001;

    /** Filter types. */
    public static final int
        AL_FILTER_NULL     = 0x0,
        AL_FILTER_LOWPASS  = 0x1,
        AL_FILTER_HIGHPASS = 0x2,
        AL_FILTER_BANDPASS = 0x3;

    /** Source property value ranges and defaults */
    public static final float
        AL_MIN_AIR_ABSORPTION_FACTOR     = 0.0f,
        AL_MAX_AIR_ABSORPTION_FACTOR     = 10.0f,
        AL_DEFAULT_AIR_ABSORPTION_FACTOR = 0.0f,
        AL_MIN_ROOM_ROLLOFF_FACTOR       = 0.0f,
        AL_MAX_ROOM_ROLLOFF_FACTOR       = 10.0f,
        AL_DEFAULT_ROOM_ROLLOFF_FACTOR   = 0.0f,
        AL_MIN_CONE_OUTER_GAINHF         = 0.0f,
        AL_MAX_CONE_OUTER_GAINHF         = 1.0f,
        AL_DEFAULT_CONE_OUTER_GAINHF     = 1.0f;

    /** Source property value ranges and defaults */
    public static final int
        AL_MIN_DIRECT_FILTER_GAINHF_AUTO             = 0x0,
        AL_MAX_DIRECT_FILTER_GAINHF_AUTO             = 0x1,
        AL_DEFAULT_DIRECT_FILTER_GAINHF_AUTO         = 0x1,
        AL_MIN_AUXILIARY_SEND_FILTER_GAIN_AUTO       = 0x0,
        AL_MAX_AUXILIARY_SEND_FILTER_GAIN_AUTO       = 0x1,
        AL_DEFAULT_AUXILIARY_SEND_FILTER_GAIN_AUTO   = 0x1,
        AL_MIN_AUXILIARY_SEND_FILTER_GAINHF_AUTO     = 0x0,
        AL_MAX_AUXILIARY_SEND_FILTER_GAINHF_AUTO     = 0x1,
        AL_DEFAULT_AUXILIARY_SEND_FILTER_GAINHF_AUTO = 0x1;

    /** Listener property value ranges and defaults. */
    public static final float
        AL_MIN_METERS_PER_UNIT     = 0x0.000002P-126f,
        AL_MAX_METERS_PER_UNIT     = 0x1.fffffeP+127f,
        AL_DEFAULT_METERS_PER_UNIT = 1.0f;

    /** Reverb effect parameter ranges and defaults */
    public static final float
        AL_REVERB_MIN_DENSITY                   = 0.0f,
        AL_REVERB_MAX_DENSITY                   = 1.0f,
        AL_REVERB_DEFAULT_DENSITY               = 1.0f,
        AL_REVERB_MIN_DIFFUSION                 = 0.0f,
        AL_REVERB_MAX_DIFFUSION                 = 1.0f,
        AL_REVERB_DEFAULT_DIFFUSION             = 1.0f,
        AL_REVERB_MIN_GAIN                      = 0.0f,
        AL_REVERB_MAX_GAIN                      = 1.0f,
        AL_REVERB_DEFAULT_GAIN                  = 0.32f,
        AL_REVERB_MIN_GAINHF                    = 0.0f,
        AL_REVERB_MAX_GAINHF                    = 1.0f,
        AL_REVERB_DEFAULT_GAINHF                = 0.89f,
        AL_REVERB_MIN_DECAY_TIME                = 0.1f,
        AL_REVERB_MAX_DECAY_TIME                = 20.0f,
        AL_REVERB_DEFAULT_DECAY_TIME            = 1.49f,
        AL_REVERB_MIN_DECAY_HFRATIO             = 0.1f,
        AL_REVERB_MAX_DECAY_HFRATIO             = 2.0f,
        AL_REVERB_DEFAULT_DECAY_HFRATIO         = 0.83f,
        AL_REVERB_MIN_REFLECTIONS_GAIN          = 0.0f,
        AL_REVERB_MAX_REFLECTIONS_GAIN          = 3.16f,
        AL_REVERB_DEFAULT_REFLECTIONS_GAIN      = 0.05f,
        AL_REVERB_MIN_REFLECTIONS_DELAY         = 0.0f,
        AL_REVERB_MAX_REFLECTIONS_DELAY         = 0.3f,
        AL_REVERB_DEFAULT_REFLECTIONS_DELAY     = 0.007f,
        AL_REVERB_MIN_LATE_REVERB_GAIN          = 0.0f,
        AL_REVERB_MAX_LATE_REVERB_GAIN          = 10.0f,
        AL_REVERB_DEFAULT_LATE_REVERB_GAIN      = 1.26f,
        AL_REVERB_MIN_LATE_REVERB_DELAY         = 0.0f,
        AL_REVERB_MAX_LATE_REVERB_DELAY         = 0.1f,
        AL_REVERB_DEFAULT_LATE_REVERB_DELAY     = 0.011f,
        AL_REVERB_MIN_AIR_ABSORPTION_GAINHF     = 0.892f,
        AL_REVERB_MAX_AIR_ABSORPTION_GAINHF     = 1.0f,
        AL_REVERB_DEFAULT_AIR_ABSORPTION_GAINHF = 0.994f,
        AL_REVERB_MIN_ROOM_ROLLOFF_FACTOR       = 0.0f,
        AL_REVERB_MAX_ROOM_ROLLOFF_FACTOR       = 10.0f,
        AL_REVERB_DEFAULT_ROOM_ROLLOFF_FACTOR   = 0.0f;

    /** Reverb effect parameter ranges and defaults */
    public static final int
        AL_REVERB_MIN_DECAY_HFLIMIT     = 0x0,
        AL_REVERB_MAX_DECAY_HFLIMIT     = 0x1,
        AL_REVERB_DEFAULT_DECAY_HFLIMIT = 0x1;

    /** EAX reverb effect parameter ranges and defaults */
    public static final float
        AL_EAXREVERB_MIN_DENSITY                   = 0.0f,
        AL_EAXREVERB_MAX_DENSITY                   = 1.0f,
        AL_EAXREVERB_DEFAULT_DENSITY               = 1.0f,
        AL_EAXREVERB_MIN_DIFFUSION                 = 0.0f,
        AL_EAXREVERB_MAX_DIFFUSION                 = 1.0f,
        AL_EAXREVERB_DEFAULT_DIFFUSION             = 1.0f,
        AL_EAXREVERB_MIN_GAIN                      = 0.0f,
        AL_EAXREVERB_MAX_GAIN                      = 1.0f,
        AL_EAXREVERB_DEFAULT_GAIN                  = 0.32f,
        AL_EAXREVERB_MIN_GAINHF                    = 0.0f,
        AL_EAXREVERB_MAX_GAINHF                    = 1.0f,
        AL_EAXREVERB_DEFAULT_GAINHF                = 0.89f,
        AL_EAXREVERB_MIN_GAINLF                    = 0.0f,
        AL_EAXREVERB_MAX_GAINLF                    = 1.0f,
        AL_EAXREVERB_DEFAULT_GAINLF                = 1.0f,
        AL_EAXREVERB_MIN_DECAY_TIME                = 0.1f,
        AL_EAXREVERB_MAX_DECAY_TIME                = 20.0f,
        AL_EAXREVERB_DEFAULT_DECAY_TIME            = 1.49f,
        AL_EAXREVERB_MIN_DECAY_HFRATIO             = 0.1f,
        AL_EAXREVERB_MAX_DECAY_HFRATIO             = 2.0f,
        AL_EAXREVERB_DEFAULT_DECAY_HFRATIO         = 0.83f,
        AL_EAXREVERB_MIN_DECAY_LFRATIO             = 0.1f,
        AL_EAXREVERB_MAX_DECAY_LFRATIO             = 2.0f,
        AL_EAXREVERB_DEFAULT_DECAY_LFRATIO         = 1.0f,
        AL_EAXREVERB_MIN_REFLECTIONS_GAIN          = 0.0f,
        AL_EAXREVERB_MAX_REFLECTIONS_GAIN          = 3.16f,
        AL_EAXREVERB_DEFAULT_REFLECTIONS_GAIN      = 0.05f,
        AL_EAXREVERB_MIN_REFLECTIONS_DELAY         = 0.0f,
        AL_EAXREVERB_MAX_REFLECTIONS_DELAY         = 0.3f,
        AL_EAXREVERB_DEFAULT_REFLECTIONS_DELAY     = 0.007f,
        AL_EAXREVERB_DEFAULT_REFLECTIONS_PAN_XYZ   = 0.0f,
        AL_EAXREVERB_MIN_LATE_REVERB_GAIN          = 0.0f,
        AL_EAXREVERB_MAX_LATE_REVERB_GAIN          = 10.0f,
        AL_EAXREVERB_DEFAULT_LATE_REVERB_GAIN      = 1.26f,
        AL_EAXREVERB_MIN_LATE_REVERB_DELAY         = 0.0f,
        AL_EAXREVERB_MAX_LATE_REVERB_DELAY         = 0.1f,
        AL_EAXREVERB_DEFAULT_LATE_REVERB_DELAY     = 0.011f,
        AL_EAXREVERB_DEFAULT_LATE_REVERB_PAN_XYZ   = 0.0f,
        AL_EAXREVERB_MIN_ECHO_TIME                 = 0.075f,
        AL_EAXREVERB_MAX_ECHO_TIME                 = 0.25f,
        AL_EAXREVERB_DEFAULT_ECHO_TIME             = 0.25f,
        AL_EAXREVERB_MIN_ECHO_DEPTH                = 0.0f,
        AL_EAXREVERB_MAX_ECHO_DEPTH                = 1.0f,
        AL_EAXREVERB_DEFAULT_ECHO_DEPTH            = 0.0f,
        AL_EAXREVERB_MIN_MODULATION_TIME           = 0.04f,
        AL_EAXREVERB_MAX_MODULATION_TIME           = 4.0f,
        AL_EAXREVERB_DEFAULT_MODULATION_TIME       = 0.25f,
        AL_EAXREVERB_MIN_MODULATION_DEPTH          = 0.0f,
        AL_EAXREVERB_MAX_MODULATION_DEPTH          = 1.0f,
        AL_EAXREVERB_DEFAULT_MODULATION_DEPTH      = 0.0f,
        AL_EAXREVERB_MIN_AIR_ABSORPTION_GAINHF     = 0.892f,
        AL_EAXREVERB_MAX_AIR_ABSORPTION_GAINHF     = 1.0f,
        AL_EAXREVERB_DEFAULT_AIR_ABSORPTION_GAINHF = 0.994f,
        AL_EAXREVERB_MIN_HFREFERENCE               = 1000.0f,
        AL_EAXREVERB_MAX_HFREFERENCE               = 20000.0f,
        AL_EAXREVERB_DEFAULT_HFREFERENCE           = 5000.0f,
        AL_EAXREVERB_MIN_LFREFERENCE               = 20.0f,
        AL_EAXREVERB_MAX_LFREFERENCE               = 1000.0f,
        AL_EAXREVERB_DEFAULT_LFREFERENCE           = 250.0f,
        AL_EAXREVERB_MIN_ROOM_ROLLOFF_FACTOR       = 0.0f,
        AL_EAXREVERB_MAX_ROOM_ROLLOFF_FACTOR       = 10.0f,
        AL_EAXREVERB_DEFAULT_ROOM_ROLLOFF_FACTOR   = 0.0f;

    /** EAX reverb effect parameter ranges and defaults */
    public static final int
        AL_EAXREVERB_MIN_DECAY_HFLIMIT     = 0x0,
        AL_EAXREVERB_MAX_DECAY_HFLIMIT     = 0x1,
        AL_EAXREVERB_DEFAULT_DECAY_HFLIMIT = 0x1;

    /** Chorus effect parameter ranges and defaults */
    public static final int
        AL_CHORUS_WAVEFORM_SINUSOID = 0,
        AL_CHORUS_WAVEFORM_TRIANGLE = 1,
        AL_CHORUS_MIN_WAVEFORM      = 0,
        AL_CHORUS_MAX_WAVEFORM      = 1,
        AL_CHORUS_DEFAULT_WAVEFORM  = 1,
        AL_CHORUS_MIN_PHASE         = -180,
        AL_CHORUS_MAX_PHASE         = 180,
        AL_CHORUS_DEFAULT_PHASE     = 90;

    /** Chorus effect parameter ranges and defaults */
    public static final float
        AL_CHORUS_MIN_RATE         = 0.0f,
        AL_CHORUS_MAX_RATE         = 10.0f,
        AL_CHORUS_DEFAULT_RATE     = 1.1f,
        AL_CHORUS_MIN_DEPTH        = 0.0f,
        AL_CHORUS_MAX_DEPTH        = 1.0f,
        AL_CHORUS_DEFAULT_DEPTH    = 0.1f,
        AL_CHORUS_MIN_FEEDBACK     = -1.0f,
        AL_CHORUS_MAX_FEEDBACK     = 1.0f,
        AL_CHORUS_DEFAULT_FEEDBACK = 0.25f,
        AL_CHORUS_MIN_DELAY        = 0.0f,
        AL_CHORUS_MAX_DELAY        = 0.016f,
        AL_CHORUS_DEFAULT_DELAY    = 0.016f;

    /** Distortion effect parameter ranges and defaults */
    public static final float
        AL_DISTORTION_MIN_EDGE               = 0.0f,
        AL_DISTORTION_MAX_EDGE               = 1.0f,
        AL_DISTORTION_DEFAULT_EDGE           = 0.2f,
        AL_DISTORTION_MIN_GAIN               = 0.01f,
        AL_DISTORTION_MAX_GAIN               = 1.0f,
        AL_DISTORTION_DEFAULT_GAIN           = 0.05f,
        AL_DISTORTION_MIN_LOWPASS_CUTOFF     = 80.0f,
        AL_DISTORTION_MAX_LOWPASS_CUTOFF     = 24000.0f,
        AL_DISTORTION_DEFAULT_LOWPASS_CUTOFF = 8000.0f,
        AL_DISTORTION_MIN_EQCENTER           = 80.0f,
        AL_DISTORTION_MAX_EQCENTER           = 24000.0f,
        AL_DISTORTION_DEFAULT_EQCENTER       = 3600.0f,
        AL_DISTORTION_MIN_EQBANDWIDTH        = 80.0f,
        AL_DISTORTION_MAX_EQBANDWIDTH        = 24000.0f,
        AL_DISTORTION_DEFAULT_EQBANDWIDTH    = 3600.0f;

    /** Echo effect parameter ranges and defaults */
    public static final float
        AL_ECHO_MIN_DELAY        = 0.0f,
        AL_ECHO_MAX_DELAY        = 0.207f,
        AL_ECHO_DEFAULT_DELAY    = 0.1f,
        AL_ECHO_MIN_LRDELAY      = 0.0f,
        AL_ECHO_MAX_LRDELAY      = 0.404f,
        AL_ECHO_DEFAULT_LRDELAY  = 0.1f,
        AL_ECHO_MIN_DAMPING      = 0.0f,
        AL_ECHO_MAX_DAMPING      = 0.99f,
        AL_ECHO_DEFAULT_DAMPING  = 0.5f,
        AL_ECHO_MIN_FEEDBACK     = 0.0f,
        AL_ECHO_MAX_FEEDBACK     = 1.0f,
        AL_ECHO_DEFAULT_FEEDBACK = 0.5f,
        AL_ECHO_MIN_SPREAD       = -1.0f,
        AL_ECHO_MAX_SPREAD       = 1.0f,
        AL_ECHO_DEFAULT_SPREAD   = -1.0f;

    /** Flanger effect parameter ranges and defaults */
    public static final int
        AL_FLANGER_WAVEFORM_SINUSOID = 0,
        AL_FLANGER_WAVEFORM_TRIANGLE = 1,
        AL_FLANGER_MIN_WAVEFORM      = 0,
        AL_FLANGER_MAX_WAVEFORM      = 1,
        AL_FLANGER_DEFAULT_WAVEFORM  = 1,
        AL_FLANGER_MIN_PHASE         = -180,
        AL_FLANGER_MAX_PHASE         = 180,
        AL_FLANGER_DEFAULT_PHASE     = 0;

    /** Flanger effect parameter ranges and defaults */
    public static final float
        AL_FLANGER_MIN_RATE         = 0.0f,
        AL_FLANGER_MAX_RATE         = 10.0f,
        AL_FLANGER_DEFAULT_RATE     = 0.27f,
        AL_FLANGER_MIN_DEPTH        = 0.0f,
        AL_FLANGER_MAX_DEPTH        = 1.0f,
        AL_FLANGER_DEFAULT_DEPTH    = 1.0f,
        AL_FLANGER_MIN_FEEDBACK     = -1.0f,
        AL_FLANGER_MAX_FEEDBACK     = 1.0f,
        AL_FLANGER_DEFAULT_FEEDBACK = -0.5f,
        AL_FLANGER_MIN_DELAY        = 0.0f,
        AL_FLANGER_MAX_DELAY        = 0.004f,
        AL_FLANGER_DEFAULT_DELAY    = 0.002f;

    /** Frequency shifter effect parameter ranges and defaults */
    public static final float
        AL_FREQUENCY_SHIFTER_MIN_FREQUENCY     = 0.0f,
        AL_FREQUENCY_SHIFTER_MAX_FREQUENCY     = 24000.0f,
        AL_FREQUENCY_SHIFTER_DEFAULT_FREQUENCY = 0.0f;

    /** Frequency shifter effect parameter ranges and defaults */
    public static final int
        AL_FREQUENCY_SHIFTER_MIN_LEFT_DIRECTION      = 0,
        AL_FREQUENCY_SHIFTER_MAX_LEFT_DIRECTION      = 2,
        AL_FREQUENCY_SHIFTER_DEFAULT_LEFT_DIRECTION  = 0,
        AL_FREQUENCY_SHIFTER_DIRECTION_DOWN          = 0,
        AL_FREQUENCY_SHIFTER_DIRECTION_UP            = 1,
        AL_FREQUENCY_SHIFTER_DIRECTION_OFF           = 2,
        AL_FREQUENCY_SHIFTER_MIN_RIGHT_DIRECTION     = 0,
        AL_FREQUENCY_SHIFTER_MAX_RIGHT_DIRECTION     = 2,
        AL_FREQUENCY_SHIFTER_DEFAULT_RIGHT_DIRECTION = 0;

    /** Vocal morpher effect parameter ranges and defaults */
    public static final int
        AL_VOCAL_MORPHER_MIN_PHONEMEA                   = 0,
        AL_VOCAL_MORPHER_MAX_PHONEMEA                   = 29,
        AL_VOCAL_MORPHER_DEFAULT_PHONEMEA               = 0,
        AL_VOCAL_MORPHER_MIN_PHONEMEA_COARSE_TUNING     = -24,
        AL_VOCAL_MORPHER_MAX_PHONEMEA_COARSE_TUNING     = 24,
        AL_VOCAL_MORPHER_DEFAULT_PHONEMEA_COARSE_TUNING = 0,
        AL_VOCAL_MORPHER_MIN_PHONEMEB                   = 0,
        AL_VOCAL_MORPHER_MAX_PHONEMEB                   = 29,
        AL_VOCAL_MORPHER_DEFAULT_PHONEMEB               = 10,
        AL_VOCAL_MORPHER_MIN_PHONEMEB_COARSE_TUNING     = -24,
        AL_VOCAL_MORPHER_MAX_PHONEMEB_COARSE_TUNING     = 24,
        AL_VOCAL_MORPHER_DEFAULT_PHONEMEB_COARSE_TUNING = 0,
        AL_VOCAL_MORPHER_PHONEME_A                      = 0,
        AL_VOCAL_MORPHER_PHONEME_E                      = 1,
        AL_VOCAL_MORPHER_PHONEME_I                      = 2,
        AL_VOCAL_MORPHER_PHONEME_O                      = 3,
        AL_VOCAL_MORPHER_PHONEME_U                      = 4,
        AL_VOCAL_MORPHER_PHONEME_AA                     = 5,
        AL_VOCAL_MORPHER_PHONEME_AE                     = 6,
        AL_VOCAL_MORPHER_PHONEME_AH                     = 7,
        AL_VOCAL_MORPHER_PHONEME_AO                     = 8,
        AL_VOCAL_MORPHER_PHONEME_EH                     = 9,
        AL_VOCAL_MORPHER_PHONEME_ER                     = 10,
        AL_VOCAL_MORPHER_PHONEME_IH                     = 11,
        AL_VOCAL_MORPHER_PHONEME_IY                     = 12,
        AL_VOCAL_MORPHER_PHONEME_UH                     = 13,
        AL_VOCAL_MORPHER_PHONEME_UW                     = 14,
        AL_VOCAL_MORPHER_PHONEME_B                      = 15,
        AL_VOCAL_MORPHER_PHONEME_D                      = 16,
        AL_VOCAL_MORPHER_PHONEME_F                      = 17,
        AL_VOCAL_MORPHER_PHONEME_G                      = 18,
        AL_VOCAL_MORPHER_PHONEME_J                      = 19,
        AL_VOCAL_MORPHER_PHONEME_K                      = 20,
        AL_VOCAL_MORPHER_PHONEME_L                      = 21,
        AL_VOCAL_MORPHER_PHONEME_M                      = 22,
        AL_VOCAL_MORPHER_PHONEME_N                      = 23,
        AL_VOCAL_MORPHER_PHONEME_P                      = 24,
        AL_VOCAL_MORPHER_PHONEME_R                      = 25,
        AL_VOCAL_MORPHER_PHONEME_S                      = 26,
        AL_VOCAL_MORPHER_PHONEME_T                      = 27,
        AL_VOCAL_MORPHER_PHONEME_V                      = 28,
        AL_VOCAL_MORPHER_PHONEME_Z                      = 29,
        AL_VOCAL_MORPHER_WAVEFORM_SINUSOID              = 0,
        AL_VOCAL_MORPHER_WAVEFORM_TRIANGLE              = 1,
        AL_VOCAL_MORPHER_WAVEFORM_SAWTOOTH              = 2,
        AL_VOCAL_MORPHER_MIN_WAVEFORM                   = 0,
        AL_VOCAL_MORPHER_MAX_WAVEFORM                   = 2,
        AL_VOCAL_MORPHER_DEFAULT_WAVEFORM               = 0;

    /** Vocal morpher effect parameter ranges and defaults */
    public static final float
        AL_VOCAL_MORPHER_MIN_RATE     = 0.0f,
        AL_VOCAL_MORPHER_MAX_RATE     = 10.0f,
        AL_VOCAL_MORPHER_DEFAULT_RATE = 1.41f;

    /** Pitch shifter effect parameter ranges and defaults */
    public static final int
        AL_PITCH_SHIFTER_MIN_COARSE_TUNE     = -12,
        AL_PITCH_SHIFTER_MAX_COARSE_TUNE     = 12,
        AL_PITCH_SHIFTER_DEFAULT_COARSE_TUNE = 12,
        AL_PITCH_SHIFTER_MIN_FINE_TUNE       = -50,
        AL_PITCH_SHIFTER_MAX_FINE_TUNE       = 50,
        AL_PITCH_SHIFTER_DEFAULT_FINE_TUNE   = 0;

    /** Ring modulator effect parameter ranges and defaults */
    public static final float
        AL_RING_MODULATOR_MIN_FREQUENCY           = 0.0f,
        AL_RING_MODULATOR_MAX_FREQUENCY           = 8000.0f,
        AL_RING_MODULATOR_DEFAULT_FREQUENCY       = 440.0f,
        AL_RING_MODULATOR_MIN_HIGHPASS_CUTOFF     = 0.0f,
        AL_RING_MODULATOR_MAX_HIGHPASS_CUTOFF     = 24000.0f,
        AL_RING_MODULATOR_DEFAULT_HIGHPASS_CUTOFF = 800.0f;

    /** Ring modulator effect parameter ranges and defaults */
    public static final int
        AL_RING_MODULATOR_SINUSOID         = 0,
        AL_RING_MODULATOR_SAWTOOTH         = 1,
        AL_RING_MODULATOR_SQUARE           = 2,
        AL_RING_MODULATOR_MIN_WAVEFORM     = 0,
        AL_RING_MODULATOR_MAX_WAVEFORM     = 2,
        AL_RING_MODULATOR_DEFAULT_WAVEFORM = 0;

    /** Autowah effect parameter ranges and defaults */
    public static final float
        AL_AUTOWAH_MIN_ATTACK_TIME      = 1.0E-4f,
        AL_AUTOWAH_MAX_ATTACK_TIME      = 1.0f,
        AL_AUTOWAH_DEFAULT_ATTACK_TIME  = 0.06f,
        AL_AUTOWAH_MIN_RELEASE_TIME     = 1.0E-4f,
        AL_AUTOWAH_MAX_RELEASE_TIME     = 1.0f,
        AL_AUTOWAH_DEFAULT_RELEASE_TIME = 0.06f,
        AL_AUTOWAH_MIN_RESONANCE        = 2.0f,
        AL_AUTOWAH_MAX_RESONANCE        = 1000.0f,
        AL_AUTOWAH_DEFAULT_RESONANCE    = 1000.0f,
        AL_AUTOWAH_MIN_PEAK_GAIN        = 3.0E-5f,
        AL_AUTOWAH_MAX_PEAK_GAIN        = 31621.0f,
        AL_AUTOWAH_DEFAULT_PEAK_GAIN    = 11.22f;

    /** Compressor effect parameter ranges and defaults */
    public static final int
        AL_COMPRESSOR_MIN_ONOFF     = 0,
        AL_COMPRESSOR_MAX_ONOFF     = 1,
        AL_COMPRESSOR_DEFAULT_ONOFF = 1;

    /** Equalizer effect parameter ranges and defaults */
    public static final float
        AL_EQUALIZER_MIN_LOW_GAIN        = 0.126f,
        AL_EQUALIZER_MAX_LOW_GAIN        = 7.943f,
        AL_EQUALIZER_DEFAULT_LOW_GAIN    = 1.0f,
        AL_EQUALIZER_MIN_LOW_CUTOFF      = 50.0f,
        AL_EQUALIZER_MAX_LOW_CUTOFF      = 800.0f,
        AL_EQUALIZER_DEFAULT_LOW_CUTOFF  = 200.0f,
        AL_EQUALIZER_MIN_MID1_GAIN       = 0.126f,
        AL_EQUALIZER_MAX_MID1_GAIN       = 7.943f,
        AL_EQUALIZER_DEFAULT_MID1_GAIN   = 1.0f,
        AL_EQUALIZER_MIN_MID1_CENTER     = 200.0f,
        AL_EQUALIZER_MAX_MID1_CENTER     = 3000.0f,
        AL_EQUALIZER_DEFAULT_MID1_CENTER = 500.0f,
        AL_EQUALIZER_MIN_MID1_WIDTH      = 0.01f,
        AL_EQUALIZER_MAX_MID1_WIDTH      = 1.0f,
        AL_EQUALIZER_DEFAULT_MID1_WIDTH  = 1.0f,
        AL_EQUALIZER_MIN_MID2_GAIN       = 0.126f,
        AL_EQUALIZER_MAX_MID2_GAIN       = 7.943f,
        AL_EQUALIZER_DEFAULT_MID2_GAIN   = 1.0f,
        AL_EQUALIZER_MIN_MID2_CENTER     = 1000.0f,
        AL_EQUALIZER_MAX_MID2_CENTER     = 8000.0f,
        AL_EQUALIZER_DEFAULT_MID2_CENTER = 3000.0f,
        AL_EQUALIZER_MIN_MID2_WIDTH      = 0.01f,
        AL_EQUALIZER_MAX_MID2_WIDTH      = 1.0f,
        AL_EQUALIZER_DEFAULT_MID2_WIDTH  = 1.0f,
        AL_EQUALIZER_MIN_HIGH_GAIN       = 0.126f,
        AL_EQUALIZER_MAX_HIGH_GAIN       = 7.943f,
        AL_EQUALIZER_DEFAULT_HIGH_GAIN   = 1.0f,
        AL_EQUALIZER_MIN_HIGH_CUTOFF     = 4000.0f,
        AL_EQUALIZER_MAX_HIGH_CUTOFF     = 16000.0f,
        AL_EQUALIZER_DEFAULT_HIGH_CUTOFF = 6000.0f;

    /** Lowpass filter parameter ranges and defaults */
    public static final float
        AL_LOWPASS_MIN_GAIN       = 0.0f,
        AL_LOWPASS_MAX_GAIN       = 1.0f,
        AL_LOWPASS_DEFAULT_GAIN   = 1.0f,
        AL_LOWPASS_MIN_GAINHF     = 0.0f,
        AL_LOWPASS_MAX_GAINHF     = 1.0f,
        AL_LOWPASS_DEFAULT_GAINHF = 1.0f;

    /** Highpass filter parameter ranges and defaults */
    public static final float
        AL_HIGHPASS_MIN_GAIN       = 0.0f,
        AL_HIGHPASS_MAX_GAIN       = 1.0f,
        AL_HIGHPASS_DEFAULT_GAIN   = 1.0f,
        AL_HIGHPASS_MIN_GAINLF     = 0.0f,
        AL_HIGHPASS_MAX_GAINLF     = 1.0f,
        AL_HIGHPASS_DEFAULT_GAINLF = 1.0f;

    /** Bandpass filter parameter ranges and defaults */
    public static final float
        AL_BANDPASS_MIN_GAIN       = 0.0f,
        AL_BANDPASS_MAX_GAIN       = 1.0f,
        AL_BANDPASS_DEFAULT_GAIN   = 1.0f,
        AL_BANDPASS_MIN_GAINHF     = 0.0f,
        AL_BANDPASS_MAX_GAINHF     = 1.0f,
        AL_BANDPASS_DEFAULT_GAINHF = 1.0f,
        AL_BANDPASS_MIN_GAINLF     = 0.0f,
        AL_BANDPASS_MAX_GAINLF     = 1.0f,
        AL_BANDPASS_DEFAULT_GAINLF = 1.0f;

    protected EXTEfx() {
        throw new UnsupportedOperationException();
    }

    // --- [ alGenEffects ] ---

    /**
     * Requests a number of effects.
     *
     * @param effects the buffer that will receive the effects
     */
    
    public static void alGenEffects(IntBuffer effects) {
    	getNative().alGenEffects(effects.remaining(), effects);
    }

    /** Requests a number of effects. */
    
    public static int alGenEffects() {
        IntBuffer effects = BufferUtils.createIntBuffer(1);
        alGenEffects(effects);
        return effects.get(0);
    }

    // --- [ alDeleteEffects ] ---

    /**
     * Deletes a number of effects.
     *
     * @param effects the effect to delete
     */
    
    public static void alDeleteEffects(IntBuffer effects) {
    	getNative().alDeleteEffects(effects.remaining(), effects);
    }

    /** Deletes a number of effects. */
    
    public static void alDeleteEffects(int effect) {
    	IntBuffer effects = BufferUtils.createIntBuffer(1);
    	effects.put(effect).rewind();
    	alDeleteEffects(effects);
    }

    // --- [ alIsEffect ] ---

    /**
     * Verifies whether the given object name is an effect.
     *
     * @param effect a value that may be a effect name
     */
    
    public static boolean alIsEffect(int effect) {
        return getNative().alIsEffect(effect);
    }

    // --- [ alEffecti ] ---

    /**
     * Sets the integer value of an effect parameter.
     *
     * @param effect the effect to modify
     * @param param  the parameter to modify
     * @param value  the parameter value
     */
    
    public static void alEffecti(int effect, int param, int value) {
    	getNative().alEffecti(effect, param, value);
    }

    // --- [ alEffectiv ] ---

    /**
     * Pointer version of {@link #alEffecti Effecti}.
     *
     * @param effect the effect to modify
     * @param param  the parameter to modify
     * @param values the parameter values
     */
    
    public static void alEffectiv(int effect, int param, IntBuffer values) {
    	getNative().alEffectiv(effect, param, values);
    }

    // --- [ alEffectf ] ---

    /**
     * Sets the float value of an effect parameter.
     *
     * @param effect the effect to modify
     * @param param  the parameter to modify
     * @param value  the parameter value
     */
    
    public static void alEffectf(int effect, int param, float value) {
    	getNative().alEffectf(effect, param, value);
    }

    // --- [ alEffectfv ] ---

    /**
     * Pointer version of {@link #alEffectf Effectf}.
     *
     * @param effect the effect to modify
     * @param param  the parameter to modify
     * @param values the parameter values
     */
    
    public static void alEffectfv(int effect, int param, FloatBuffer values) {
    	getNative().alEffectfv(effect, param, values);
    }

    // --- [ alGetEffecti ] ---

    /**
     * Returns the integer value of the specified effect parameter.
     *
     * @param effect the effect to query
     * @param param  the parameter to query
     * @param value  the parameter value
     */
    
    public static void alGetEffecti(int effect, int param, IntBuffer value) {
    	getNative().alGetEffecti(effect, param, value);
    }

    /**
     * Returns the integer value of the specified effect parameter.
     *
     * @param effect the effect to query
     * @param param  the parameter to query
     */
    
    public static int alGetEffecti(int effect, int param) {
    	IntBuffer effects = BufferUtils.createIntBuffer(1);
    	alGetEffecti(effect, param, effects);
        return effects.get(0);
       
    }

    // --- [ alGetEffectiv ] ---

    /**
     * Returns the integer values of the specified effect parameter.
     *
     * @param effect the effect to query
     * @param param  the parameter to query
     * @param values the parameter values
     */
    
    public static void alGetEffectiv(int effect, int param, IntBuffer values) {
    	getNative().alGetEffectiv(effect, param, values);
    }

    // --- [ alGetEffectf ] ---

    /**
     * Returns the float value of the specified effect parameter.
     *
     * @param effect the effect to query
     * @param param  the parameter to query
     * @param value  the parameter value
     */
    
    public static void alGetEffectf(int effect, int param, FloatBuffer value) {
    	getNative().alGetEffectf(effect, param, value);
    }

    /**
     * Returns the float value of the specified effect parameter.
     *
     * @param effect the effect to query
     * @param param  the parameter to query
     */
    
    public static float alGetEffectf(int effect, int param) {
        FloatBuffer value = BufferUtils.createFloatBuffer(1);
        alGetEffectf(effect, param, value);
        return value.get(0);
    }

    // --- [ alGetEffectfv ] ---

    /**
     * Returns the float values of the specified effect parameter.
     *
     * @param effect the effect to query
     * @param param  the parameter to query
     * @param values the parameter values
     */
    
    public static void alGetEffectfv(int effect, int param, FloatBuffer values) {
    	getNative().alGetEffectfv(effect, param, values);
    }

    // --- [ alGenFilters ] ---

    /**
     * Requests a number of filters.
     *
     * @param filters the buffer that will receive the filters
     */
    
    public static void alGenFilters(IntBuffer filters) {
    	getNative().alGenFilters(filters.remaining(), filters);
    }

    /** Requests a number of filters. */
    
    public static int alGenFilters() {
        IntBuffer filters = BufferUtils.createIntBuffer(1);
        getNative().alGenFilters(1, filters);
        return filters.get(0);
    }

    // --- [ alDeleteFilters ] ---

    /**
     * Deletes a number of filters.
     *
     * @param filters the filter to delete
     */
    
    public static void alDeleteFilters(IntBuffer filters) {
    	getNative().alDeleteFilters(filters.remaining(), filters);
    }

    /** Deletes a number of filters. */
    
    public static void alDeleteFilters(int filter) {
    	IntBuffer filters = BufferUtils.createIntBuffer(1);
    	filters.put(filter).rewind();
        getNative().alDeleteFilters(1, filters);
    }

    // --- [ alIsFilter ] ---

    /**
     * Verifies whether the given object name is a filter.
     *
     * @param filter a value that may be a filter name
     */
    
    public static boolean alIsFilter(int filter) {
        return getNative().alIsFilter(filter);
    }

    // --- [ alFilteri ] ---

    /**
     * Sets the integer value of a filter parameter.
     *
     * @param filter the filter to modify
     * @param param  the parameter to modify
     * @param value  the parameter value
     */
    
    public static void alFilteri(int filter, int param, int value) {
    	getNative().alFilteri(filter, param, value);
    }

    // --- [ alFilteriv ] ---

    /**
     * Pointer version of {@link #alFilteri Filteri}.
     *
     * @param filter the filter to modify
     * @param param  the parameter to modify
     * @param values the parameter values
     */
    
    public static void alFilteriv(int filter, int param, IntBuffer values) {
        getNative().alFilteriv(filter, param, values);
    }

    // --- [ alFilterf ] ---

    /**
     * Sets the float value of a filter parameter.
     *
     * @param filter the filter to modify
     * @param param  the parameter to modify
     * @param value  the parameter value
     */
    
    public static void alFilterf(int filter, int param, float value) {
    	getNative().alFilterf(filter, param, value);
    }

    // --- [ alFilterfv ] ---

    /**
     * Pointer version of {@link #alFilterf Filterf}.
     *
     * @param filter the filter to modify
     * @param param  the parameter to modify
     * @param values the parameter values
     */
    
    public static void alFilterfv(int filter, int param, FloatBuffer values) {
        getNative().alFilterfv(filter, param, values);
    }

    // --- [ alGetFilteri ] ---

    /**
     * Returns the integer value of the specified filter parameter.
     *
     * @param filter the filter to query
     * @param param  the parameter to query
     * @param value  the parameter value
     */
    
    public static void alGetFilteri(int filter, int param, IntBuffer value) {
        getNative().alGetFilteri(filter, param, value);
    }

    /**
     * Returns the integer value of the specified filter parameter.
     *
     * @param filter the filter to query
     * @param param  the parameter to query
     */
    
    public static int alGetFilteri(int filter, int param) {
        IntBuffer value = BufferUtils.createIntBuffer(1);
        getNative().alGetFilteri(filter, param, value);
        return value.get(0);
    }

    // --- [ alGetFilteriv ] ---

    /**
     * Returns the integer values of the specified filter parameter.
     *
     * @param filter the filter to query
     * @param param  the parameter to query
     * @param values the parameter values
     */
    
    public static void alGetFilteriv(int filter, int param, IntBuffer values) {
        getNative().alGetFilteriv(filter, param, values);
    }

    // --- [ alGetFilterf ] ---

    /**
     * Returns the float value of the specified filter parameter.
     *
     * @param filter the effect to query
     * @param param  the parameter to query
     * @param value  the parameter value
     */
    
    public static void alGetFilterf(int filter, int param, FloatBuffer value) {
        getNative().alGetFilterf(filter, param, value);
    }

    /**
     * Returns the float value of the specified filter parameter.
     *
     * @param filter the effect to query
     * @param param  the parameter to query
     */
    
    public static float alGetFilterf(int filter, int param) {
        FloatBuffer value = BufferUtils.createFloatBuffer(1);
        getNative().alGetFilterf(filter, param, value);
        return value.get(0);
    }

    // --- [ alGetFilterfv ] ---

    /**
     * Returns the float values of the specified filter parameter.
     *
     * @param filter the effect to query
     * @param param  the parameter to query
     * @param values the parameter values
     */
    
    public static void alGetFilterfv(int filter, int param, FloatBuffer values) {
        getNative().alGetFilterfv(filter, param, values);
    }

    // --- [ alGenAuxiliaryEffectSlots ] ---

    /**
     * Requests a number of effect slots.
     *
     * @param effectSlots the buffer that will receive the effect slots
     */
    
    public static void alGenAuxiliaryEffectSlots(IntBuffer effectSlots) {
    	getNative().alGenAuxiliaryEffectSlots(effectSlots.remaining(), effectSlots);
    }

    /** Requests a number of effect slots. */
    
    public static int alGenAuxiliaryEffectSlots() {
        IntBuffer effectSlots = BufferUtils.createIntBuffer(1);
        getNative().alGenAuxiliaryEffectSlots(1, effectSlots);
        return effectSlots.get(0);
    }

    // --- [ alDeleteAuxiliaryEffectSlots ] ---

    /**
     * Deletes a number of effect slots.
     *
     * @param effectSlots the effectSlot to delete
     */
    
    public static void alDeleteAuxiliaryEffectSlots(IntBuffer effectSlots) {
    	getNative().alDeleteAuxiliaryEffectSlots(effectSlots.remaining(), effectSlots);
    }

    /** Deletes a number of effect slots. */
    
    public static void alDeleteAuxiliaryEffectSlots(int effectSlot) {
        IntBuffer effectSlots = BufferUtils.createIntBuffer(1);
        effectSlots.put(effectSlot).rewind();
        getNative().alDeleteAuxiliaryEffectSlots(1, effectSlots);
    }

    // --- [ alIsAuxiliaryEffectSlot ] ---

    /**
     * Verifies whether the given object name is an effect slot.
     *
     * @param effectSlot a value that may be an effect slot name
     */
    
    public static boolean alIsAuxiliaryEffectSlot(int effectSlot) {
        return getNative().alIsAuxiliaryEffectSlot(effectSlot);
    }

    // --- [ alAuxiliaryEffectSloti ] ---

    /**
     * Sets the integer value of an effect slot parameter.
     *
     * @param effectSlot the effect slot to modify
     * @param param      the parameter to modify
     * @param value      the parameter value
     */
    
    public static void alAuxiliaryEffectSloti(int effectSlot, int param, int value) {
        getNative().alAuxiliaryEffectSloti(effectSlot, param, value);
    }

    // --- [ alAuxiliaryEffectSlotiv ] ---

    /**
     * Pointer version of {@link #alAuxiliaryEffectSloti AuxiliaryEffectSloti}.
     *
     * @param effectSlot the effect slot to modify
     * @param param      the parameter to modify
     * @param values     the parameter values
     */
    
    public static void alAuxiliaryEffectSlotiv(int effectSlot, int param, IntBuffer values) {
        getNative().alAuxiliaryEffectSlotiv(effectSlot, param, values);
    }

    // --- [ alAuxiliaryEffectSlotf ] ---

    /**
     * Sets the float value of an effect slot parameter.
     *
     * @param effectSlot the effect slot to modify
     * @param param      the parameter to modify
     * @param value      the parameter value
     */
    
    public static void alAuxiliaryEffectSlotf(int effectSlot, int param, float value) {
        getNative().alAuxiliaryEffectSlotf(effectSlot, param, value);
    }

    // --- [ alAuxiliaryEffectSlotfv ] ---

    /**
     * Pointer version of {@link #alAuxiliaryEffectSlotf AuxiliaryEffectSlotf}.
     *
     * @param effectSlot the effect slot to modify
     * @param param      the parameter to modify
     * @param values     the parameter values
     */
    
    public static void alAuxiliaryEffectSlotfv(int effectSlot, int param, FloatBuffer values) {
        getNative().alAuxiliaryEffectSlotfv(effectSlot, param, values);
    }

    // --- [ alGetAuxiliaryEffectSloti ] ---

    /**
     * Returns the integer value of the specified effect slot parameter.
     *
     * @param effectSlot the effect slot to query
     * @param param      the parameter to query
     * @param value      the parameter value
     */
    
    public static void alGetAuxiliaryEffectSloti(int effectSlot, int param, IntBuffer value) {
        getNative().alGetAuxiliaryEffectSloti(effectSlot, param, value);
    }

    /**
     * Returns the integer value of the specified effect slot parameter.
     *
     * @param effectSlot the effect slot to query
     * @param param      the parameter to query
     */
    
    public static int alGetAuxiliaryEffectSloti(int effectSlot, int param) {
        IntBuffer value = BufferUtils.createIntBuffer(1);
        getNative().alGetAuxiliaryEffectSloti(effectSlot, param, value);
        return value.get(0);
    }

    // --- [ alGetAuxiliaryEffectSlotiv ] ---

    /**
     * Returns the integer values of the specified effect slot parameter.
     *
     * @param effectSlot the effect slot to query
     * @param param      the parameter to query
     * @param values     the parameter values
     */
    
    public static void alGetAuxiliaryEffectSlotiv(int effectSlot, int param, IntBuffer values) {
        getNative().alGetAuxiliaryEffectSlotiv(effectSlot, param, values);
    }

    // --- [ alGetAuxiliaryEffectSlotf ] ---

    /**
     * Returns the float value of the specified filter parameter.
     *
     * @param effectSlot the effect slot to query
     * @param param      the parameter to query
     * @param value      the parameter value
     */
    
    public static void alGetAuxiliaryEffectSlotf(int effectSlot, int param, FloatBuffer value) {
        getNative().alGetAuxiliaryEffectSlotf(effectSlot, param, value);
    }

    /**
     * Returns the float value of the specified filter parameter.
     *
     * @param effectSlot the effect slot to query
     * @param param      the parameter to query
     */
    
    public static float alGetAuxiliaryEffectSlotf(int effectSlot, int param) {
        FloatBuffer value = BufferUtils.createFloatBuffer(1);
        getNative().alGetAuxiliaryEffectSlotf(effectSlot, param, value);
        return value.get(0);
    }

    // --- [ alGetAuxiliaryEffectSlotfv ] ---

    /**
     * Returns the float values of the specified effect slot parameter.
     *
     * @param effectSlot the effect to query
     * @param param      the parameter to query
     * @param values     the parameter values
     */
    
    public static void alGetAuxiliaryEffectSlotfv(int effectSlot, int param, FloatBuffer values) {
        getNative().alGetAuxiliaryEffectSlotfv(effectSlot, param, values);
    }

    /** Array version of: {@link #alGenEffects GenEffects} */
    
    public static void alGenEffects(int[] effects) {
        getNative().alGenEffects(effects.length, effects);
    }

    /** Array version of: {@link #alDeleteEffects DeleteEffects} */
    
    public static void alDeleteEffects(int[] effects) {
    	 getNative().alDeleteEffects(effects.length, effects);
    }

    /** Array version of: {@link #alEffectiv Effectiv} */
    
    public static void alEffectiv(int effect, int param, int[] values) {
        getNative().alEffectiv(effect, param, values);
    }

    /** Array version of: {@link #alEffectfv Effectfv} */
    
    public static void alEffectfv(int effect, int param, float[] values) {
        getNative().alEffectfv(effect, param, values);
    }

    /** Array version of: {@link #alGetEffecti GetEffecti} */
    
    public static void alGetEffecti(int effect, int param, int[] value) {
        getNative().alGetEffecti(effect, param, value);
    }

    /** Array version of: {@link #alGetEffectiv GetEffectiv} */
    
    public static void alGetEffectiv(int effect, int param, int[] values) {
        getNative().alGetEffectiv(effect, param, values);
    }

    /** Array version of: {@link #alGetEffectf GetEffectf} */
    
    public static void alGetEffectf(int effect, int param, float[] value) {
        getNative().alGetEffectf(effect, param, value);
    }

    /** Array version of: {@link #alGetEffectfv GetEffectfv} */
    
    public static void alGetEffectfv(int effect, int param, float[] values) {
        getNative().alGetEffectfv(effect, param, values);
    }

    /** Array version of: {@link #alGenFilters GenFilters} */
    
    public static void alGenFilters(int[] filters) {
        getNative().alGenFilters(filters.length, filters);
    }

    /** Array version of: {@link #alDeleteFilters DeleteFilters} */
    
    public static void alDeleteFilters(int[] filters) {
        getNative().alDeleteFilters(filters.length, filters);
    }

    /** Array version of: {@link #alFilteriv Filteriv} */
    
    public static void alFilteriv(int filter, int param, int[] values) {
        getNative().alFilteriv(filter, param, values);
    }

    /** Array version of: {@link #alFilterfv Filterfv} */
    
    public static void alFilterfv(int filter, int param, float[] values) {
        getNative().alFilterfv(filter, param, values);
    }

    /** Array version of: {@link #alGetFilteri GetFilteri} */
    
    public static void alGetFilteri(int filter, int param, int[] value) {
        getNative().alGetFilteri(filter, param, value);
    }

    /** Array version of: {@link #alGetFilteriv GetFilteriv} */
    
    public static void alGetFilteriv(int filter, int param, int[] values) {
        getNative().alGetFilteriv(filter, param, values);
    }

    /** Array version of: {@link #alGetFilterf GetFilterf} */
    
    public static void alGetFilterf(int filter, int param, float[] value) {
        getNative().alGetFilterf(filter, param, value);
    }

    /** Array version of: {@link #alGetFilterfv GetFilterfv} */
    
    public static void alGetFilterfv(int filter, int param, float[] values) {
        getNative().alGetFilterfv(filter, param, values);
    }

    /** Array version of: {@link #alGenAuxiliaryEffectSlots GenAuxiliaryEffectSlots} */
    
    public static void alGenAuxiliaryEffectSlots(int[] effectSlots) {
        getNative().alGenAuxiliaryEffectSlots(effectSlots.length, effectSlots);
    }

    /** Array version of: {@link #alDeleteAuxiliaryEffectSlots DeleteAuxiliaryEffectSlots} */
    
    public static void alDeleteAuxiliaryEffectSlots(int[] effectSlots) {
        getNative().alDeleteAuxiliaryEffectSlots(effectSlots.length, effectSlots);
    }

    /** Array version of: {@link #alAuxiliaryEffectSlotiv AuxiliaryEffectSlotiv} */
    
    public static void alAuxiliaryEffectSlotiv(int effectSlot, int param, int[] values) {
        getNative().alAuxiliaryEffectSlotiv(effectSlot, param, values);
    }

    /** Array version of: {@link #alAuxiliaryEffectSlotfv AuxiliaryEffectSlotfv} */
    
    public static void alAuxiliaryEffectSlotfv(int effectSlot, int param, float[] values) {
        getNative().alAuxiliaryEffectSlotfv(effectSlot, param, values);
    }

    /** Array version of: {@link #alGetAuxiliaryEffectSloti GetAuxiliaryEffectSloti} */
    
    public static void alGetAuxiliaryEffectSloti(int effectSlot, int param, int[] value) {
        getNative().alGetAuxiliaryEffectSloti(effectSlot, param, value);
    }

    /** Array version of: {@link #alGetAuxiliaryEffectSlotiv GetAuxiliaryEffectSlotiv} */
    
    public static void alGetAuxiliaryEffectSlotiv(int effectSlot, int param, int[] values) {
        getNative().alGetAuxiliaryEffectSlotiv(effectSlot, param, values);
    }

    /** Array version of: {@link #alGetAuxiliaryEffectSlotf GetAuxiliaryEffectSlotf} */
    
    public static void alGetAuxiliaryEffectSlotf(int effectSlot, int param, float[] value) {
        getNative().alGetAuxiliaryEffectSlotf(effectSlot, param, value);
    }

    /** Array version of: {@link #alGetAuxiliaryEffectSlotfv GetAuxiliaryEffectSlotfv} */
    
    public static void alGetAuxiliaryEffectSlotfv(int effectSlot, int param, float[] values) {
        getNative().alGetAuxiliaryEffectSlotfv(effectSlot, param, values);
    }

}