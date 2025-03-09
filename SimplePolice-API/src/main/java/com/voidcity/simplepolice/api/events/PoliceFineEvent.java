package com.voidcity.simplepolice.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Event called when a police officer attempts to fine a player.
 */
public class PoliceFineEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player officer;
    private final Player criminal;
    private double amount;
    private boolean cancelled;
    private String cancelReason;

    public PoliceFineEvent(@NotNull Player officer, @NotNull Player criminal, double amount) {
        this.officer = officer;
        this.criminal = criminal;
        this.amount = amount;
        this.cancelled = false;
        this.cancelReason = "";
    }

    /**
     * Get the police officer issuing the fine.
     *
     * @return The police officer
     */
    @NotNull
    public Player getOfficer() {
        return officer;
    }

    /**
     * Get the player being fined.
     *
     * @return The criminal
     */
    @NotNull
    public Player getCriminal() {
        return criminal;
    }

    /**
     * Get the fine amount.
     *
     * @return The fine amount
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Set the fine amount.
     *
     * @param amount The new fine amount
     */
    public void setAmount(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Fine amount cannot be negative");
        }
        this.amount = amount;
    }

    /**
     * Get the reason why the fine was cancelled.
     *
     * @return The cancel reason, or empty string if not cancelled
     */
    @NotNull
    public String getCancelReason() {
        return cancelReason;
    }

    /**
     * Set the reason why the fine was cancelled.
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