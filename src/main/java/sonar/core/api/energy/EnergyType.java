package sonar.core.api.energy;

import net.minecraft.nbt.CompoundNBT;

/**
 * Enum representing different energy types created by various mods.
 * Can be extended for custom energy systems.
 */
public enum EnergyType {

	FE("Forge Energy", "FE", "FE/T"),
	TESLA("Tesla", "T", "T/t"),
	RF("Redstone Flux", "RF", "RF/T"),
	EU("Energy Units", "EU", "EU/T"),
	MJ("Minecraft Joules", "J", "J/T"),
	AE("Applied Energistics", "AE", "AE/t");

	private String name;
	private String storage;
	private String usage;

	EnergyType(String name, String storage, String usage) {
		this.name = name;
		this.storage = storage;
		this.usage = usage;
	}

	public String getName() {
		return name;
	}

	public String getStorageSuffix() {
		return storage;
	}

	public String getUsageSuffix() {
		return usage;
	}

	/** Compatibility - for energy types saved under their storage suffix */
	public static EnergyType readFromNBT(CompoundNBT tag, String tagName) {
		String storageName = tag.getString(tagName);
		if (!storageName.isEmpty()) {
			for (EnergyType type : EnergyType.values()) {
				if (type.name.equals(storageName)) {
					return type;
				}
			}
			return FE;
		} else {
			int storageOrdinal = tag.getInt(tagName);
			return EnergyType.values()[storageOrdinal];
		}
	}

	/** Compatibility - for energy types saved under their storage suffix */
	public static CompoundNBT writeToNBT(EnergyType type, CompoundNBT tag, String tagName) {
		tag.putInt(tagName, type.ordinal());
		return tag;
	}
}
