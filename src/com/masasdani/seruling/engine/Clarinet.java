package com.masasdani.seruling.engine;

import java.util.Random;


public class Clarinet
{
	float	c			= 347.0f;
	float	rho			= 1.2f;
	float	a			= 0.01f;
	float	Z0			= rho * c / ((float) Math.PI * a * a);
	int		Mmax		= 110;																														//(int) Math.floor(fs / 440.0f + 0.5f);
																																					//int										N					= (int) Math.floor(l * fs / c);
	float	upper[]		= new float[Mmax];
	float	lower[]		= new float[Mmax];

	float	bRL[]		= new float[] { -0.2162f, -0.2171f, -0.0545f };
	float	aRL[]		= new float[] { 1, -0.6032f, 0.0910f };

	float	bTL[]		= new float[] { -0.2162f + 1, -0.2171f + -0.6032f, -0.0545f + 0.0910f };
	float	aTL[]		= new float[] { 1, -0.6032f, 0.0910f };

	float	stateRL[]	= new float[2];

	float	stateTL[]	= new float[2];

	float	bL[]		= new float[] { 0.806451596106077f, -1.855863155228909f, 1.371191452991298f, -0.312274852426121f, -0.006883256612646f };
	float	aL[]		= new float[] { 1.000000000000000f, -2.392436499871960f, 1.891289981326362f, -0.511406512428537f, 0.015235504020645f };

	float	R0			= 0.9f;

	float	aw			= 0.015f;
	float	S			= 0.034f * aw;
	float	k			= S * 10000000.0f;
	float	H0			= 0.0007f;

	float	y0[];
	float	yL[];

	float	U[];

	float	stateLU[]	= new float[4];
	float	stateLL[]	= new float[4];

	float									multiplier			= 5000.0f;

	public Clarinet(int bufferSize)
	{
		y0 = new float[bufferSize];
		yL = new float[bufferSize];
		U = new float[bufferSize];
	}
	
	float dp;
	float x;
	float pr;

	float outL;
	float out0;

	float y0_prev = 0;

	float pm = multiplier;
	
	int inptr = 0;
	int outptr = 0;
	Random random = new Random();
	
	float pm_prev = multiplier;
	float nu = .97f;
	
	public void clarinet(short[] buffer, int bufferSize, float power, float frequency, int fs)
	{
		int M = (int) ((float)fs /  frequency + 0.5f);
		
		for (int n = 0; n < bufferSize; n++){
			
			pm = 2 *power;
			if (pm < 0.05f)
				pm = 0.1f;
			else if (pm > 1.2f)
				pm = 1.2f;
			if (n == 0){
				dp = (float) Math.abs(multiplier * pm - y0_prev);
			}else{
				dp = (float) Math.abs(multiplier * pm - y0[n - 1]);
			}
			x = (float) Math.min(H0, dp * S / k);
			U[n] = aw * (H0 - x) * (float) Math.sqrt(dp * 2 / rho);
			pr = U[n] * Z0;

			
			outptr = inptr - M;
			if(outptr < 0)
			{
				outptr += Mmax;
			}

			{
				outL = bL[0] * upper[outptr] + stateLU[0];
				stateLU[0] = bL[1] * upper[outptr] + stateLU[1] - aL[1] * outL;
				stateLU[1] = bL[2] * upper[outptr] + stateLU[2] - aL[2] * outL;
				stateLU[2] = bL[3] * upper[outptr] + stateLU[3] - aL[3] * outL;
				stateLU[3] = bL[4] * upper[outptr] - aL[4] * outL;
			}

			{
				out0 = bL[0] * lower[outptr] + stateLL[0];
				stateLL[0] = bL[1] * lower[outptr] + stateLL[1] - aL[1] * out0;
				stateLL[1] = bL[2] * lower[outptr] + stateLL[2] - aL[2] * out0;
				stateLL[2] = bL[3] * lower[outptr] + stateLL[3] - aL[3] * out0;
				stateLL[3] = bL[4] * lower[outptr] - aL[4] * out0;

			}

			upper[inptr] = pr + R0 * out0;
			{
				lower[inptr] = bRL[0] * outL + stateRL[0];
				stateRL[0] = bRL[1] * outL + stateRL[1] - aRL[1] * lower[inptr];
				stateRL[1] = bRL[2] * outL - aRL[2] * lower[inptr];
			}

			y0[n] = out0 + upper[inptr];
			{
				yL[n] = bTL[0] * outL + stateTL[0];
				stateTL[0] = bTL[1] * outL + stateTL[1] - aTL[1] * yL[n];
				stateTL[1] = bTL[2] * outL - aTL[2] * yL[n];
			}

			inptr++;
			if (inptr >= Mmax)
				inptr = 0;
		}

		y0_prev = y0[bufferSize - 1];

		for (int i = 0; i < bufferSize; i++)
			buffer[i] = (short) (yL[i] / 400.0f * Short.MAX_VALUE);
	}
}
