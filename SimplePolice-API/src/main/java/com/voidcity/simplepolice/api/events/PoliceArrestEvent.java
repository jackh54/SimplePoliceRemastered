package com.voidcity.simplepolice.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Event called when a police officer attempts to arrest a player.
 */
public class PoliceArrestEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player officer;
    private final Player criminal;
    private boolean cancelled;
    private String cancelReason;

    public PoliceArrestEvent(@NotNull Player officer, @NotNull Player criminal) {
        this.officer = officer;
        this.criminal = criminal;
        this.cancelled = false;
        this.cancelReason = "";
    }

    /**
     * Get the police officer performing the arrest.
     *
     * @return The police officer
     */
    @NotNull
    public Player getOfficer() {
        return officer;
    }

    /**
     * Get the player being arrested.
     *
     * @return The criminal
     */
    @NotNull
    public Player getCriminal() {
        return criminal;
    }

    /**
     * Get the reason why the arrest was cancelled.
     *
     * @return The cancel reason, or empty string if not cancelled
     */
    @NotNull
    public String getCancelReason() {
        return cancelReason;
    }

    /**
     * Set the reason why the arrest was cancelled.
     *
     * @param reason The reason for cancellation
     */
    public void setCancelReason(@NotNull String reason) {
        this.cancelReason = reason;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    @NotNull
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
} 