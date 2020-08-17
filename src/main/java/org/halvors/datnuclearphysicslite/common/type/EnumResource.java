package org.halvors.datnuclearphysicslite.common.type;

public enum EnumResource {
	GUI("textures/gui"),
	GUI_COMPONENT("textures/gui/components"),
	MODEL("block"),
	SOUND("sounds"),
	TEXTURE_MODELS("models");

	private final String prefix;

	EnumResource(final String prefix) {
		this.prefix = prefix;
	}

	public String getPrefix() {
		return prefix + "/";
	}
}