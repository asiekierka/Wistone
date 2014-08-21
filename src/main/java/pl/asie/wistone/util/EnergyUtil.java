package pl.asie.wistone.util;

import pl.asie.wistone.Wistone;

public class EnergyUtil {
	// RF is used as the base unit. (32000 RF = 3200 MJ = 1 coal)
	public static float calculateTransmissionEnergyUsage(int frequency, float multiplier, int dataLength) {
		if(Wistone.IGNORE_POWER) return 0.0f;
		
		// Base energy usage: multiplier 1.0f, frequency 1, dataLength 1
		float baseEnergy = 1.0f; // 1 RF/tick for base
		baseEnergy *= 1.0f + (frequency / 64);
		baseEnergy *= Math.pow(multiplier, 1.3);
		baseEnergy *= Math.pow(dataLength, 0.5);
		return baseEnergy;
	}
	
	public static float calculateReceptionEnergyUsage(int frequency, float multiplier, int dataLength) {
		if(Wistone.IGNORE_POWER) return 0.0f;
		
		// Base energy usage: multiplier 1.0f, frequency 1, dataLength 1
		float baseEnergy = 0.0f; // No energy used for regular reception.
		if(multiplier > 1.0f) {
			baseEnergy = (float)Math.pow(multiplier, 1.6);
		}
		return baseEnergy;
	}
}
