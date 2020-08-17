package org.halvors.datnuclearphysicslite.common;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.common.config.Configuration;
import org.halvors.datnuclearphysicslite.common.network.PacketHandler;
import org.halvors.datnuclearphysicslite.common.science.unit.EnumElectricUnit;

import java.util.ArrayList;
import java.util.List;

public class ConfigurationManager {
    public static class General {
        public static EnumElectricUnit electricUnit;
        public static double toTesla;
        public static double toJoules;
        public static double fromTesla;
        public static double fromJoules;

        public static boolean enableAntimatterPower;
        public static boolean enableRadiationRoisoning;

        public static double fulminationOutputMultiplier;

        public static int antimatterParticleDensity;
        public static double darkMatterSpawnChance;

        public static double quantumAssemblerEntropyChance;
        public static boolean allowGeneratedQuantumAssemblerRecipes;
    }

    // TODO: Testing new options.
    public static class Energy {
        public static int particleAcceleratorEnergyPerTick;
    }

    public static void loadConfiguration(final Configuration configuration) {
        configuration.load();

        General.electricUnit = EnumElectricUnit.fromSymbol(configuration.get(Configuration.CATEGORY_GENERAL, "electricUnit", EnumElectricUnit.JOULE.getSymbol(), null, EnumElectricUnit.getSymbols().toArray(new String[EnumElectricUnit.values().length])).getString());
        General.toTesla = configuration.get(Configuration.CATEGORY_GENERAL, "ForgeToTesla", 1.0).getDouble(); // Conversion multiplier from Forge Energy to Tesla (FE * ForgeToTesla = Tesla)
        General.toJoules = configuration.get(Configuration.CATEGORY_GENERAL, "ForgeToJoules", 2.5).getDouble(); // Conversion multiplier from Forge Energy to Tesla (FE * ForgeToJoules = Joules)
        General.fromTesla = configuration.get(Configuration.CATEGORY_GENERAL, "ForgeFromTesla", 1.0).getDouble(); // Conversion multiplier from Tesla to Forge Energy (Tesla * ForgeFromTesla = FE)
        General.fromJoules = configuration.get(Configuration.CATEGORY_GENERAL, "ForgeFromJoules", 0.4).getDouble(); // Conversion multiplier from Joules to Forge Energy (Joules * ForgeFromJoules = FE)

        General.enableAntimatterPower = configuration.get(Configuration.CATEGORY_GENERAL, "enableAntimatterPower", true).getBoolean();
        General.enableRadiationRoisoning = configuration.get(Configuration.CATEGORY_GENERAL, "enableRadiationRoisoning", true).getBoolean();

        General.antimatterParticleDensity = configuration.get(Configuration.CATEGORY_GENERAL, "antimatterParticleDensity", 1).getInt();
        General.fulminationOutputMultiplier = configuration.get(Configuration.CATEGORY_GENERAL, "fulminationOutputMultiplier", 1.0).getDouble();

        General.darkMatterSpawnChance = configuration.get(Configuration.CATEGORY_GENERAL, "darkMatterSpawnChance", 0.2).getDouble();

        General.quantumAssemblerEntropyChance = configuration.get(Configuration.CATEGORY_GENERAL, "quantumAssemblerEntropyChance", 0.008).getDouble();
        General.allowGeneratedQuantumAssemblerRecipes = configuration.get(Configuration.CATEGORY_GENERAL, "allowGeneratedQuantumAssemblerRecipes", true).getBoolean();

        // TODO: Testing new options, and fix category.
        Energy.particleAcceleratorEnergyPerTick = configuration.get(Configuration.CATEGORY_GENERAL, "particleAcceleratorEnergyPerTick", 19000).getInt();

        configuration.save();
    }

    public static void saveConfiguration(final Configuration configuration) {
        configuration.save();
    }

    public static void readConfiguration(final ByteBuf dataStream) {
        General.electricUnit = EnumElectricUnit.values()[dataStream.readInt()];
        General.toTesla = dataStream.readDouble();
        General.toJoules = dataStream.readDouble();
        General.fromTesla = dataStream.readDouble();
        General.fromJoules = dataStream.readDouble();

        General.enableAntimatterPower = dataStream.readBoolean();
        General.enableRadiationRoisoning = dataStream.readBoolean();

        General.antimatterParticleDensity = dataStream.readInt();
        General.fulminationOutputMultiplier = dataStream.readDouble();

        General.darkMatterSpawnChance = dataStream.readDouble();

        General.quantumAssemblerEntropyChance = dataStream.readDouble();
        General.allowGeneratedQuantumAssemblerRecipes = dataStream.readBoolean();

        // TODO: Testing new options.
        Energy.particleAcceleratorEnergyPerTick = dataStream.readInt();
    }

    public static void writeConfiguration(final ByteBuf dataStream) {
        final List<Object> objects = new ArrayList<>();

        objects.add(General.electricUnit.ordinal());
        objects.add(General.toTesla);
        objects.add(General.toJoules);
        objects.add(General.fromTesla);
        objects.add(General.fromJoules);

        objects.add(General.enableAntimatterPower);
        objects.add(General.enableRadiationRoisoning);


        objects.add(General.antimatterParticleDensity);
        objects.add(General.fulminationOutputMultiplier);
        objects.add(General.darkMatterSpawnChance);

        objects.add(General.quantumAssemblerEntropyChance);
        objects.add(General.allowGeneratedQuantumAssemblerRecipes);

        // TODO: Testing new options.
        objects.add(Energy.particleAcceleratorEnergyPerTick);

        PacketHandler.writeObjects(objects, dataStream);
    }
}
