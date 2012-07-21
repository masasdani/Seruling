package com.masasdani.seruling.engine;

import java.util.Random;

public class Flute
{
	float vents[] = new float[] { 40.3f, 60.1f };
	int	NRofVents = vents.length;
	int Mmax = 120;
	int inptr = 0;
	int outptr = 0;
	int	jetLength = 2 * Mmax;
	float RB0 = -0.4794f; // Reflection filter coefficients
	float RB1 = -0.2603f;
	float DCA = 0.9950f; // DC killer (high-pass filter) coefficient
	int	degree = 2;
	float AR = -0.8632f;
	float BR = -(1 + AR);
	float ARI = 0;
	float BRI = 0;

	float ventTarget = 0;
	float ventState	= 0;

	//compute vent operation times
	//int openVentHere = Math.round(1 * samples / 3);
	//int closeVentHere = Math.round(2 * samples / 3);
	//variable parameters of the flute model
	//frequency dependent loss filter

	float backGain = -0.97f; // Real reflection coefficient for the mouth end
	float noiseGain = 4.0f; // Gain of the white noise signal
	float inputGain = 100.0f; // Maximum amplitude of input
	float voicedGain = 0.02f; // Gain of the feedback from the tube to the nonlin
	float dirFeedback = -0.1f; // Gain from the nonlinearity back to itself
	float sigMin = 0.001f; // Input gain of the sigmoid function
	float sigmOffset = 0.0f; // Offset of the sigmoid function
	float sigmOut = 300.0f;	// Output gain of the sigmoid function

	//Coefficient of the leaky integrator (if 0.0, integrator is removed)
	float integra = 0.0f;

	/*------------------------------------------------------------------------------
	% Init delay lines
	%
	% 27.11.1995 Rami Hanninen
	%
	% Replaced the traditional ring-buffer delay line implementation with a more
	% direct vector approach.  Even if the ring-buffered implementation is usually
	% the most efficient method to implement a delay line, this is not the case
	% with MatLab because executing for-loops and mod-operations in MatLab is much
	% slower than the fast array operations MatLab offers.
	%
	% In the new implementation, the newest element in the delay line is LINE(1)
	% and the oldest is LINE(LINELENGTH), exept with the lower delay line which is
	% reversed so that the newest element is LOWER(LENGTH) and the oldest is
	% LOWER(1).
	%
	*/
	float[]	jet = new float[jetLength]; // Delay line for the air jet
	float[]	upper = new float[Mmax]; // Upper trail of the digital waveguide
	float[]	lower = new float[Mmax]; // Lower trail of the digital waveguide

	//Allocate memory for output vectors

	float	max	= 0;
	float[]	uppOut;

	float lossInput = 0.0f;	//Input for the freq-dependent loss filter
	float reflDelayX = 0.0f;//First unit delay of the reflection function
	float reflDelayY = 0.0f;//Second unit delay of the reflection function
	float sigmInput = 0.0f;	//Input of the sigmoid function
	float sigmOutput = 0.0f;//Output of the sigmoid function
	float dcxOutput0 = 0.0f;//output of the DC killer filter
	float dcxOutput1 = 0.0f;//Last output of the DC killer filter
	float integrInput = 0.0f; //Input of the leaky integrator
	float integrOutput = 0.0f;//Output of the integrator
	float[] ventOutput = new float[NRofVents]; //3-port vent output	
	float[] ventOutputPrev = new float[NRofVents]; //3-port vent output	

	float	temp;
	public final static Random	rand = new Random();
	
	public Flute(int bufferSize){
		uppOut = new float[bufferSize];
	}

	public void flute( short[] buffer, int bufferSize, float power, float frequency, int fs){
		int M = (int) ((float)fs / 2.0f / frequency + 0.5f);
		int jetLength_ch = 2 * M;

		float lossFreq		= (float) Math.exp(0.006f * M) - 0.85f;
		float lossGain		= 0.992f - 0.0005f * M;
		float lossScale		= 1 - lossFreq;

		for (int n = 0; n < bufferSize ; n++)
		{
			ARI = ventState * AR;
			BRI = ventState * BR;
			//feed jet line
			jet[0] = (power * 100) * (noiseGain * 2 * (rand.nextFloat() - 0.5f) + integrOutput);

			//sigmoid nonlinearity
			sigmOutput = sigmOut * (float) Math.tanh(inputGain * sigmOffset - sigMin * jet[jetLength_ch - 1]);

			// DC killer (a 1st order high-pass filter, direct form II)
			temp = sigmOutput + DCA * dcxOutput1;
			dcxOutput0 = temp - dcxOutput1; //differentiation removes DC component
			dcxOutput1 = temp;
			
			outptr = inptr - M;
			if(outptr < 0){
				outptr += Mmax;
			}

			// Add (subtract) the output of the sigmoid function (DC killed) and the
			// reflected signal from the end of the lower delay line and feed the
			// result into the beginning of the upper delay line.

			lossInput = dcxOutput0 + backGain * lower[0];

			// Boundary losses (1st order all-pole low-pass filter):
			// Note that because the delay line have been shifted when we come
			// here the next time, the previous output is now in UPPER(2),
			// not in UPPER(1) as one might think.

			upper[0] = lossScale * lossGain * lossInput + lossFreq * upper[1];

			// Reflection filter
			reflDelayY = RB0 * upper[M - 1] + RB1 * (reflDelayX - reflDelayY);
			reflDelayX = upper[M - 1]; // Update unit delay of the reflection filter

			// Feed reflected signal into the beginning of the lower delay line
			lower[M - 1] = reflDelayY;

			// Feedback loop:
			integrInput = voicedGain * (lower[0] + dirFeedback * dcxOutput0);

			// Integration:
			integrOutput = (1 - integra) * integrInput + integra * integrOutput;

			// Output of the model:
			// Subtract output of refl. filter from its input (and differentiate):
			uppOut[n] = upper[M - 1];

			if (Math.abs(uppOut[n]) > max)
				max = Math.abs(uppOut[n]);
			
			// Move delay lines
			for (int j = jetLength_ch - 1; j > 0; j--)
				jet[j] = jet[j - 1];
			jet[0] = 0;

			for (int j = M - 1; j > 0; j--)
				upper[j] = upper[j - 1];
			upper[0] = 0;

			for (int j = 0; j < M - 1; j++)
				lower[j] = lower[j + 1];
			lower[M - 1] = 0;
			
			inptr++;
			if (inptr >= Mmax)
				inptr = 0;
		}

		for (int i = 0; i < bufferSize; i++)
			buffer[i] = (short) (uppOut[i] / max * Short.MAX_VALUE);

	}


}
