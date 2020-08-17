package org.halvors.datnuclearphysicslite.common.utility;

import net.minecraft.util.ResourceLocation;
import org.halvors.datnuclearphysicslite.common.Reference;
import org.halvors.datnuclearphysicslite.common.type.EnumResource;

public class ResourceUtility {
	public static ResourceLocation getResource(final EnumResource resource, final String name) {
		return new ResourceLocation(Reference.DOMAIN, resource.getPrefix() + name);
	}
}
