package com.duytoan.imajicoffee.imaji_coffee_be.enums;

/**
 * Conversation Status Enum
 *
 * OPEN: New conversation waiting for admin assignment
 * WAITING: No admin is currently available, user is queued
 * PENDING: Assigned to admin, awaiting admin acceptance
 * CLOSED: Conversation ended, no new messages allowed
 * ADMIN_ACTIVE: Legacy - admin actively responding
 * AI_ACTIVE: Legacy - AI handling the conversation
 */
public enum ConversationStatus {
    OPEN,
    WAITING,
    PENDING,
    CLOSED,
    ADMIN_ACTIVE,
    AI_ACTIVE
}
