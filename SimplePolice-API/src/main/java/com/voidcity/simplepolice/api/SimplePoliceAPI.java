package com.voidcity.simplepolice.api;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

/**
 * The main API interface for SimplePolice plugin.
 * This interface provides methods to interact with the plugin's core functionality.
 */
public interface SimplePoliceAPI {
    /**
     * Check if a player is a police officer.
     *
     * @param playerId The UUID of the player to check
     * @return true if the player is a police officer, false otherwise
     */
    boolean isPoliceOfficer(@NotNull UUID playerId);

    /**
     * Add a player as a police officer.
     *
     * @param player The player to add as a police officer
     * @return true if the player was added successfully, false if they were already a police officer
     */
    boolean addPoliceOfficer(@NotNull Player player);

    /**
     * Remove a player from being a police officer.
     *
     * @param player The player to remove from being a police officer
     * @return true if the player was removed successfully, false if they weren't a police officer
     */
    boolean removePoliceOfficer(@NotNull Player player);

    /**
     * Get a set of all police officer UUIDs.
     *
     * @return An unmodifiable set of police officer UUIDs
     */
    @NotNull
    Set<UUID> getPoliceOfficers();

    /**
     * Arrest a player.
     *
     * @param officer The police officer performing the arrest
     * @param criminal The player being arrested
     * @return true if the arrest was successful, false otherwise
     */
    boolean arrestPlayer(@NotNull Player officer, @NotNull Player criminal);

    /**
     * Get the current jail location.
     *
     * @return The jail location, or null if not set
     */
    @Nullable
    Location getJailLocation();

    /**
     * Set the jail location.
     *
     * @param location The new jail location
     */
    void setJailLocation(@Nullable Location location);

    /**
     * Check if a player is currently under arrest.
     *
     * @param playerId The UUID of the player to check
     * @return true if the player is under arrest, false otherwise
     */
    boolean isUnderArrest(@NotNull UUID playerId);

    /**
     * Get the remaining jail time for a player in seconds.
     *
     * @param playerId The UUID of the player to check
     * @return The remaining jail time in seconds, or 0 if not jailed
     */
    int getRemainingJailTime(@NotNull UUID playerId);

    /**
     * Release a player from jail early.
     *
     * @param playerId The UUID of the player to release
     * @return true if the player was released, false if they weren't in jail
     */
    boolean releaseFromJail(@NotNull UUID playerId);

    /**
     * Fine a player.
     *
     * @param officer The police officer issuing the fine
     * @param criminal The player being fined
     * @param amount The amount to fine
     * @return true if the fine was successful, false otherwise
     */
    boolean finePlayer(@NotNull Player officer, @NotNull Player criminal, double amount);
} 