package com.serene.avatarduels.spirits.config;

import com.projectkorra.projectkorra.configuration.ConfigManager;
import com.serene.avatarduels.AvatarDuels;
import com.serene.avatarduels.spirits.SpiritElement;
import org.bukkit.configuration.file.FileConfiguration;
import oshi.util.tuples.Pair;

import static com.serene.avatarduels.AvatarDuels.createAbilityConfig;

public class Config {


    public Config() {
        loadConfig();
    }

    private void loadConfig() {

        FileConfiguration language = ConfigManager.languageConfig.get();

        //Rank configuration
        language.addDefault("Chat.Colors.Spirit", SpiritElement.NEUTRAL.getDefaultColor().getName());
        language.addDefault("Chat.Colors.SpiritSub", "DARK_PURPLE");
        language.addDefault("Chat.Colors.LightSpirit", SpiritElement.LIGHT.getDefaultColor().getName());
        language.addDefault("Chat.Colors.LightSpiritSub", "WHITE");
        language.addDefault("Chat.Colors.DarkSpirit", SpiritElement.DARK.getDefaultColor().getName());
        language.addDefault("Chat.Colors.DarkSpiritSub", "DARK_GRAY");
        language.addDefault("Chat.Prefixes.Spirit", "[Spirit]");
        language.addDefault("Chat.Prefixes.LightSpirit", "[LightSpirit]");
        language.addDefault("Chat.Prefixes.DarkSpirit", "[DarkSpirit]");

        //Descriptions & Instructions

        language.addDefault("Abilities.Spirit.Possess.DeathMessage", "{victim} succumbed to {attacker}'s {ability}");
        language.addDefault("Abilities.Spirit.PossessRecoil.DeathMessage", "{victim} failed to possess {attacker}");

        //Ability configuration

        createAbilityConfig("Agility",
                new Pair<>("Abilities.Spirits.Neutral.Agility.Enabled", true),
                new Pair<>("Abilities.Spirits.Neutral.Agility.Dash.Cooldown", 2000),
                new Pair<>("Abilities.Spirits.Neutral.Agility.Dash.Distance", 3),
                new Pair<>("Abilities.Spirits.Neutral.Agility.Soar.Cooldown", 4500),
                new Pair<>("Abilities.Spirits.Neutral.Agility.Soar.Duration", 2000),
                new Pair<>("Abilities.Spirits.Neutral.Agility.Soar.Speed", 0.8),
                new Pair<>("Language.Abilities.Spirit.Agility.Description", "This ability offers you 2 modes of mobility. The first being the ability to dash forward very quickly. The second being the ability to soar through the skies as if gravity is non-existent."),
                new Pair<>("Language.Abilities.Spirit.Agility.Instructions", "Left-Click: Dash ⏐ Hold shift: Soar")
        );

        createAbilityConfig("Possess",
                new Pair<>("Abilities.Spirits.Neutral.Possess.Enabled", true),
                new Pair<>("Abilities.Spirits.Neutral.Possess.Cooldown", 5000),
                new Pair<>("Abilities.Spirits.Neutral.Possess.Range", 15),
                new Pair<>("Abilities.Spirits.Neutral.Possess.MinDamage", 2),
                new Pair<>("Abilities.Spirits.Neutral.Possess.MaxDamage", 4),
                new Pair<>("Abilities.Spirits.Neutral.Possess.FailureSelfDamage", 6),
                new Pair<>("Abilities.Spirits.Neutral.Possess.Duration", 4000),
                new Pair<>("Abilities.Spirits.Neutral.Possess.Speed", 0.8D),
                new Pair<>("Abilities.Spirits.Neutral.Possess.Durability", 6),
                new Pair<>("Abilities.Spirits.Neutral.Possess.ChargeTime", 0),
                new Pair<>("Language.Abilities.Spirit.Possess.Description", "Allows the spirit to possess the body of a human. Successful possession will slow the target and damage them, but if the target fights back, they can break possession and harm the possessor."),
                new Pair<>("Language.Abilities.Spirit.Possess.Instructions", "Tap sneak and ram into a player/mob")
        );

        createAbilityConfig("SpiritBlast",
                new Pair<>("Abilities.Spirits.Neutral.SpiritBlast.Enabled", true),
                new Pair<>("Abilities.Spirits.Neutral.SpiritBlast.Cooldown", 9000),
                new Pair<>("Abilities.Spirits.Neutral.SpiritBlast.Range", 50),
                new Pair<>("Abilities.Spirits.Neutral.SpiritBlast.Damage", 2),
                new Pair<>("Abilities.Spirits.Neutral.SpiritBlast.MaxBlasts", 3),
                new Pair<>("Abilities.Spirits.Neutral.SpiritBlast.Duration", 3000),
                new Pair<>("Abilities.Spirits.Neutral.SpiritBlast.Speed", 1.2D),
                new Pair<>("Abilities.Spirits.Neutral.SpiritBlast.Radius", 0.3D),
                new Pair<>("Abilities.Spirits.Neutral.SpiritBlast.CanRedirect", true),
                new Pair<>("Abilities.Spirits.Neutral.SpiritBlast.CanAlwaysRedirect", false),
                new Pair<>("Language.Abilities.Spirit.SpiritBlast.Description", "Release multiple blasts of spirit energy in quick succession that damages all enemies they come across."),
                new Pair<>("Language.Abilities.Spirit.SpiritBlast.Instructions", "Left-Click (Multiple): Release Spirit Blast | Tap Sneak: Redirect")
        );

        createAbilityConfig("SpiritualBody",
                new Pair<>("Abilities.Spirits.Neutral.Passive.SpiritualBody.Enabled", true),
                new Pair<>("Abilities.Spirits.Neutral.Passive.SpiritualBody.FallDamageModifier", 0.0),
                new Pair<>("Language.Abilities.Spirit.Passive.SpiritualBody.Description", "Spirits do not have a physical form. As such, they are immune to all forms of kinetic damage like fall damage."),
                new Pair<>("Language.Abilities.Spirit.Passive.SpiritualBody.Instructions", "Spirits are passively immune to fall damage")
        );

        createAbilityConfig("Vanish",
                new Pair<>("Abilities.Spirits.Neutral.Vanish.Enabled", true),
                new Pair<>("Abilities.Spirits.Neutral.Vanish.Cooldown", 7000),
                new Pair<>("Abilities.Spirits.Neutral.Vanish.Duration", 10000),
                new Pair<>("Abilities.Spirits.Neutral.Vanish.ChargeTime", 1500),
                new Pair<>("Abilities.Spirits.Neutral.Vanish.Range", 20),
                new Pair<>("Abilities.Spirits.Neutral.Vanish.Radius", 10),
                new Pair<>("Abilities.Spirits.Neutral.Vanish.ParticleFrequency", 5),
                new Pair<>("Abilities.Spirits.Neutral.Vanish.RemoveFire", true),
                new Pair<>("Abilities.Spirits.Neutral.Vanish.DivideRange.Enabled", true),
                new Pair<>("Abilities.Spirits.Neutral.Vanish.DivideRange.HealthRequired", 10),
                new Pair<>("Abilities.Spirits.Neutral.Vanish.DivideRange.DivideFactor", 2),
                new Pair<>("Language.Abilities.Spirit.Vanish.Description", "Spirits are often seen disappearing into thin air and then reappearing somewhere different. With this ability, you can harness that power as well! However, there is a certain duration you are able to vanish for and a radius of how far away from your original location you're allowed to get!"),
                new Pair<>("Language.Abilities.Spirit.Vanish.Instructions", "Hold shift: Disappear ⏐ Release shift: Reappear")
        );

        createAbilityConfig("Alleviate",
                new Pair<>("Abilities.Spirits.LightSpirit.Alleviate.Enabled", true),
                new Pair<>("Abilities.Spirits.LightSpirit.Alleviate.Others.Cooldown", 5000),
                new Pair<>("Abilities.Spirits.LightSpirit.Alleviate.Others.Range", 5),
                new Pair<>("Abilities.Spirits.LightSpirit.Alleviate.Others.PotionInterval", 2000),
                new Pair<>("Abilities.Spirits.LightSpirit.Alleviate.Others.HealInterval", 5000),
                new Pair<>("Abilities.Spirits.LightSpirit.Alleviate.Others.SelfDamage", 6),
                new Pair<>("Abilities.Spirits.LightSpirit.Alleviate.Self.Cooldown", 5000),
                new Pair<>("Abilities.Spirits.LightSpirit.Alleviate.Self.ChargeTime", 2000),
                new Pair<>("Abilities.Spirits.LightSpirit.Alleviate.Self.HealDuration", 1.5),
                new Pair<>("Abilities.Spirits.LightSpirit.Alleviate.Self.NightVisionDuration", 1.5),
                new Pair<>("Abilities.Spirits.LightSpirit.Alleviate.Self.RemoveNegativePotionEffects", true),
                new Pair<>("Abilities.Spirits.LightSpirit.Alleviate.ParticleColor.Red", 255),
                new Pair<>("Abilities.Spirits.LightSpirit.Alleviate.ParticleColor.Green", 255),
                new Pair<>("Abilities.Spirits.LightSpirit.Alleviate.ParticleColor.Blue", 255),
                new Pair<>("Language.Abilities.LightSpirit.Alleviate.Description", "The healing ability for LightSpirits, this allows you to heal yourself and others! When healing, whoever is being healed will be removed of ANY negative potion effects as well as receive regeneration for a period of time."),
                new Pair<>("Language.Abilities.LightSpirit.Alleviate.Instructions", "Hold Shift while looking at a target: Heal them ⏐ Hold Shift while looking away: Heal yourself.")
        );


        createAbilityConfig("LightBlast",
                new Pair<>("Abilities.Spirits.LightSpirit.LightBlast.Enabled", true),
                new Pair<>("Abilities.Spirits.LightSpirit.LightBlast.Cooldown", 0),
                new Pair<>("Abilities.Spirits.LightSpirit.LightBlast.Controllable", false),
                new Pair<>("Abilities.Spirits.LightSpirit.LightBlast.Damage", 2),
                new Pair<>("Abilities.Spirits.LightSpirit.LightBlast.Range", 10),
                new Pair<>("Abilities.Spirits.LightSpirit.LightBlast.SelectionDuration", 2000),
                new Pair<>("Abilities.Spirits.LightSpirit.LightBlast.PotionDuration", 10),
                new Pair<>("Abilities.Spirits.LightSpirit.LightBlast.PotionPower", 1),
                new Pair<>("Abilities.Spirits.LightSpirit.LightBlast.FirstBlastSpeed", 1),
                new Pair<>("Abilities.Spirits.LightSpirit.LightBlast.SecondBlastSpeed", 0.2),
                new Pair<>("Abilities.Spirits.LightSpirit.LightBlast.BlastRadius", 2),
                new Pair<>("Language.Abilities.LightSpirit.LightBlast.Description", "Unleash a powerful light blast that can damage enemies and apply beneficial potions."),
                new Pair<>("Language.Abilities.LightSpirit.LightBlast.Instructions", "Right-click to unleash the Light Blast!")
        );

        createAbilityConfig("Orb",
                new Pair<>("Abilities.Spirits.LightSpirit.Orb.Enabled", true),
                new Pair<>("Abilities.Spirits.LightSpirit.Orb.Cooldown", 10000),
                new Pair<>("Abilities.Spirits.LightSpirit.Orb.ChargeTime", 2000),
                new Pair<>("Abilities.Spirits.LightSpirit.Orb.Duration", 30000),
                new Pair<>("Abilities.Spirits.LightSpirit.Orb.WarmUpTime", 500),
                new Pair<>("Abilities.Spirits.LightSpirit.Orb.Damage", 3),
                new Pair<>("Abilities.Spirits.LightSpirit.Orb.PlaceRange", 20),
                new Pair<>("Abilities.Spirits.LightSpirit.Orb.DetonateRange", 3),
                new Pair<>("Abilities.Spirits.LightSpirit.Orb.EffectRange", 5),
                new Pair<>("Abilities.Spirits.LightSpirit.Orb.BlindnessDuration", 120),
                new Pair<>("Abilities.Spirits.LightSpirit.Orb.NauseaDuration", 300),
                new Pair<>("Abilities.Spirits.LightSpirit.Orb.PotionPower", 2),
                new Pair<>("Abilities.Spirits.LightSpirit.Orb.RequireGround", true),
                new Pair<>("Language.Abilities.LightSpirit.Orb.Description", "Create and place an orb that detonates, causing damage and applying negative effects on enemies."),
                new Pair<>("Language.Abilities.LightSpirit.Orb.Instructions", "Right-click to place the orb, which will detonate after the warm-up time!")
        );

        createAbilityConfig("Shelter",
                new Pair<>("Abilities.Spirits.LightSpirit.Shelter.Enabled", true),
                new Pair<>("Abilities.Spirits.LightSpirit.Shelter.RemoveOnDamage", true),
                new Pair<>("Abilities.Spirits.LightSpirit.Shelter.Duration", 7000),
                new Pair<>("Abilities.Spirits.LightSpirit.Shelter.Others.ClickDelay", 2000),
                new Pair<>("Abilities.Spirits.LightSpirit.Shelter.Others.Cooldown", 10000),
                new Pair<>("Abilities.Spirits.LightSpirit.Shelter.Self.Cooldown", 10000),
                new Pair<>("Abilities.Spirits.LightSpirit.Shelter.Others.Radius", 5),
                new Pair<>("Abilities.Spirits.LightSpirit.Shelter.Self.Radius", 4),
                new Pair<>("Abilities.Spirits.LightSpirit.Shelter.Others.KnockbackPower", 1),
                new Pair<>("Abilities.Spirits.LightSpirit.Shelter.Self.KnockbackPower", 1),
                new Pair<>("Abilities.Spirits.LightSpirit.Shelter.Others.Range", 10),
                new Pair<>("Abilities.Spirits.LightSpirit.Shelter.RemoveIfFarAway.Enabled", true),
                new Pair<>("Abilities.Spirits.LightSpirit.Shelter.RemoveIfFarAway.Range", 5),
                new Pair<>("Abilities.Spirits.LightSpirit.Shelter.Self.BlockArrows", true),
                new Pair<>("Abilities.Spirits.LightSpirit.Shelter.Others.BlockArrows", true),
                new Pair<>("Language.Abilities.LightSpirit.Shelter.Description", "Create a protective shelter that can shield allies from damage for a short duration."),
                new Pair<>("Language.Abilities.LightSpirit.Shelter.Instructions", "Right-click to create a shelter that blocks incoming attacks!")
        );

        createAbilityConfig("Intoxicate",
                new Pair<>("Abilities.Spirits.DarkSpirit.Intoxicate.Enabled", true),
                new Pair<>("Abilities.Spirits.DarkSpirit.Intoxicate.Cooldown", 5000),
                new Pair<>("Abilities.Spirits.DarkSpirit.Intoxicate.Range", 5),
                new Pair<>("Abilities.Spirits.DarkSpirit.Intoxicate.PotionInterval", 2000),
                new Pair<>("Abilities.Spirits.DarkSpirit.Intoxicate.HarmInterval", 5000),
                new Pair<>("Abilities.Spirits.DarkSpirit.Intoxicate.SelfDamage", 4),
                new Pair<>("Abilities.Spirits.DarkSpirit.Intoxicate.BlastSpeed", 0.5),
                new Pair<>("Abilities.Spirits.DarkSpirit.Intoxicate.ParticleColor.Red", 255),
                new Pair<>("Abilities.Spirits.DarkSpirit.Intoxicate.ParticleColor.Green", 0),
                new Pair<>("Abilities.Spirits.DarkSpirit.Intoxicate.ParticleColor.Blue", 0),
                new Pair<>("Abilities.Spirits.DarkSpirit.Intoxicate.Potions.WitherDuration", 5),
                new Pair<>("Abilities.Spirits.DarkSpirit.Intoxicate.Potions.WitherPower", 1),
                new Pair<>("Abilities.Spirits.DarkSpirit.Intoxicate.Potions.HungerDuration", 50),
                new Pair<>("Abilities.Spirits.DarkSpirit.Intoxicate.Potions.HungerPower", 1),
                new Pair<>("Abilities.Spirits.DarkSpirit.Intoxicate.Potions.ConfusionDuration", 15),
                new Pair<>("Abilities.Spirits.DarkSpirit.Intoxicate.Potions.ConfusionPower", 1),
                new Pair<>("Language.Abilities.DarkSpirit.Intoxicate.Description", "Inflict confusion and harm onto your enemies, intoxifying them with negative effects."),
                new Pair<>("Language.Abilities.DarkSpirit.Intoxicate.Instructions", "Right-click to intoxicate a target within range!")
        );

        createAbilityConfig("Shackle",
                new Pair<>("Abilities.Spirits.DarkSpirit.Shackle.Enabled", true),
                new Pair<>("Abilities.Spirits.DarkSpirit.Shackle.Cooldown", 5000),
                new Pair<>("Abilities.Spirits.DarkSpirit.Shackle.Duration", 2500),
                new Pair<>("Abilities.Spirits.DarkSpirit.Shackle.Range", 20),
                new Pair<>("Abilities.Spirits.DarkSpirit.Shackle.Radius", 2),
                new Pair<>("Language.Abilities.DarkSpirit.Shackle.Description", "Bind a target in place, preventing them from moving for a short duration."),
                new Pair<>("Language.Abilities.DarkSpirit.Shackle.Instructions", "Right-click to shackle a target within range.")
        );

        createAbilityConfig("Strike",
                new Pair<>("Abilities.Spirits.DarkSpirit.Strike.Enabled", true),
                new Pair<>("Abilities.Spirits.DarkSpirit.Strike.Cooldown", 4000),
                new Pair<>("Abilities.Spirits.DarkSpirit.Strike.Range", 5),
                new Pair<>("Abilities.Spirits.DarkSpirit.Strike.Damage", 3),
                new Pair<>("Abilities.Spirits.DarkSpirit.Strike.Radius", 1),
                new Pair<>("Language.Abilities.DarkSpirit.Strike.Description", "Strike down your enemies with a powerful attack that deals damage."),
                new Pair<>("Language.Abilities.DarkSpirit.Strike.Instructions", "Right-click to strike an enemy within range!")
        );

        createAbilityConfig("Corruption",
                new Pair<>("Abilities.Spirits.DarkSpirit.Corruption.Enabled", true),
                new Pair<>("Abilities.Spirits.DarkSpirit.Corruption.Cooldown", 12000),
                new Pair<>("Abilities.Spirits.DarkSpirit.Corruption.Radius", 6),
                new Pair<>("Abilities.Spirits.DarkSpirit.Corruption.Duration", 15),
                new Pair<>("Abilities.Spirits.DarkSpirit.Corruption.EffectDuration", 2),
                new Pair<>("Abilities.Spirits.DarkSpirit.Corruption.EffectAmplifier", 1),
                new Pair<>("Language.Abilities.DarkSpirit.Corruption.Description", "Dark spirits are entities filled with rage and malevolence. They are able to infect and influence the area around them ★\n" +
                        "and imbue their negative energies to it. They could also summon more dark spirits within this area in order to spread their corruption. Mobs and land are also affected in this area of influence."),
                new Pair<>("Language.Abilities.DarkSpirit.Corruption.Instructions", "Hold Sneak to cast Corruption in the specified radius and weaken your foes!")
        );

        //COMBOS

        createAbilityConfig("Rejuvenate",
                new Pair<>("Abilities.Spirits.LightSpirit.Combo.Rejuvenate.Enabled", true),
                new Pair<>("Abilities.Spirits.LightSpirit.Combo.Rejuvenate.Cooldown", 15000),
                new Pair<>("Abilities.Spirits.LightSpirit.Combo.Rejuvenate.Duration", 10000),
                new Pair<>("Abilities.Spirits.LightSpirit.Combo.Rejuvenate.Radius", 5),
                new Pair<>("Abilities.Spirits.LightSpirit.Combo.Rejuvenate.Damage", 1),
                new Pair<>("Abilities.Spirits.LightSpirit.Combo.Rejuvenate.EffectInterval", 10),
                new Pair<>("Abilities.Spirits.LightSpirit.Combo.Rejuvenate.HurtDarkSpirits", true),
                new Pair<>("Abilities.Spirits.LightSpirit.Combo.Rejuvenate.HurtMonsters", true),
                new Pair<>("Language.Abilities.LightSpirit.Rejuvenate.Description", "A powerful ability that rejuvenates allies while damaging dark spirits and monsters within the radius."),
                new Pair<>("Language.Abilities.LightSpirit.Rejuvenate.Instructions", "Right-click to cast Rejuvenate!")
        );

        createAbilityConfig("Levitation",
                new Pair<>("Abilities.Spirits.Neutral.Combo.Levitation.Enabled", true),
                new Pair<>("Abilities.Spirits.Neutral.Combo.Levitation.Cooldown", 10000),
                new Pair<>("Abilities.Spirits.Neutral.Combo.Levitation.Duration", 6000),
                new Pair<>("Abilities.Spirits.Neutral.Combo.Levitation.Range", 10),
                new Pair<>("Abilities.Spirits.Neutral.Combo.Levitation.AllowedHealthLoss", 4),
                new Pair<>("Abilities.Spirits.Neutral.Combo.Levitation.AbilityCooldownMultipliers.Agility.Enabled", true),
                new Pair<>("Abilities.Spirits.Neutral.Combo.Levitation.AbilityCooldownMultipliers.Agility.Multiplier", 2),
                new Pair<>("Abilities.Spirits.Neutral.Combo.Levitation.AbilityCooldownMultipliers.Phase.Enabled", true),
                new Pair<>("Abilities.Spirits.Neutral.Combo.Levitation.AbilityCooldownMultipliers.Phase.Multiplier", 4),
                new Pair<>("Abilities.Spirits.Neutral.Combo.Levitation.AbilityCooldownMultipliers.Levitation.Enabled", true),
                new Pair<>("Abilities.Spirits.Neutral.Combo.Levitation.AbilityCooldownMultipliers.Levitation.Multiplier", 3),
                new Pair<>("Language.Abilities.Neutral.Levitation.Description", "Allows you to levitate for a brief period, avoiding ground-based attacks while using health to maintain the effect."),
                new Pair<>("Language.Abilities.Neutral.Levitation.Instructions", "Right-click to levitate and avoid ground attacks!")
        );

        createAbilityConfig("Phase",
                new Pair<>("Abilities.Spirits.Neutral.Combo.Phase.Enabled", true),
                new Pair<>("Abilities.Spirits.Neutral.Combo.Phase.CooldownMultiplier", 4),
                new Pair<>("Abilities.Spirits.Neutral.Combo.Phase.Duration", 10000),
                new Pair<>("Abilities.Spirits.Neutral.Combo.Phase.Range", 10),
                new Pair<>("Abilities.Spirits.Neutral.Combo.Phase.MinHealth", 6),
                new Pair<>("Abilities.Spirits.Neutral.Combo.Phase.Vanish.ApplyCooldown", true),
                new Pair<>("Abilities.Spirits.Neutral.Combo.Phase.Vanish.CooldownMultiplier", 4),
                new Pair<>("Language.Abilities.Neutral.Phase.Description", "Phase through enemies and obstacles for a limited time, gaining invulnerability but requiring health to activate."),
                new Pair<>("Language.Abilities.Neutral.Phase.Instructions", "Right-click to phase through enemies!")
        );

        createAbilityConfig("DarkBlast",
                new Pair<>("Abilities.Spirits.DarkSpirit.DarkBlast.Enabled", true),
                new Pair<>("Abilities.Spirits.DarkSpirit.DarkBlast.Cooldown", 0),
                new Pair<>("Abilities.Spirits.DarkSpirit.DarkBlast.Controllable", false),
                new Pair<>("Abilities.Spirits.DarkSpirit.DarkBlast.Damage", 4),
                new Pair<>("Abilities.Spirits.DarkSpirit.DarkBlast.Range", 10),
                new Pair<>("Abilities.Spirits.DarkSpirit.DarkBlast.DurationOfSelection", 2000),
                new Pair<>("Abilities.Spirits.DarkSpirit.DarkBlast.PotionDuration", 5),
                new Pair<>("Abilities.Spirits.DarkSpirit.DarkBlast.PotionPower", 1),
                new Pair<>("Abilities.Spirits.DarkSpirit.DarkBlast.FirstBlastSpeed", 1),
                new Pair<>("Abilities.Spirits.DarkSpirit.DarkBlast.SecondBlastSpeed", 0.2),
                new Pair<>("Abilities.Spirits.DarkSpirit.DarkBlast.BlastRadius", 2),
                new Pair<>("Language.Abilities.DarkSpirit.DarkBlast.Description", "Unleash a powerful dark blast that can damage and apply effects on nearby targets."),
                new Pair<>("Language.Abilities.DarkSpirit.DarkBlast.Instructions", "Right-click to shoot a dark blast!")
        );

        createAbilityConfig("Infest",
                new Pair<>("Abilities.Spirits.DarkSpirit.Combo.Infest.Enabled", true),
                new Pair<>("Abilities.Spirits.DarkSpirit.Combo.Infest.Cooldown", 15000),
                new Pair<>("Abilities.Spirits.DarkSpirit.Combo.Infest.Duration", 10000),
                new Pair<>("Abilities.Spirits.DarkSpirit.Combo.Infest.Radius", 8),
                new Pair<>("Abilities.Spirits.DarkSpirit.Combo.Infest.EffectInterval", 10),
                new Pair<>("Abilities.Spirits.DarkSpirit.Combo.Infest.Damage", 1),
                new Pair<>("Abilities.Spirits.DarkSpirit.Combo.Infest.DamageEntities", true),
                new Pair<>("Abilities.Spirits.DarkSpirit.Combo.Infest.HealDarkSpirits", true),
                new Pair<>("Language.Abilities.DarkSpirit.Infest.Description", "Infest an area, dealing damage to enemies and healing dark spirits within the radius."),
                new Pair<>("Language.Abilities.DarkSpirit.Infest.Instructions", "Right-click to infest an area!")
        );
        ConfigManager.languageConfig.save();
        AvatarDuels.plugin.saveConfig();
    }
}