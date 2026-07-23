/**
 * @author duytoan
 * @since Mar 05 2026
 *
 * Enum representing the various statuses of a login process.
 * This can be used to track the current state of an authentication flow.
 *
 * Enum Members:
 * - LOADING: Indicates that the login process is currently ongoing.
 * - OK: Indicates that the login process was successful.
 * - FAIL: Indicates that the login process encountered a failure.
 */
export enum LoginStatus {
  LOADING,
  OK,
  FAIL,
}

/**
 * @author duytoan
 * @since Mar 05 2026
 *
 * Enum representing different types of input fields.
 *
 * This enum is used to define constants for various input types
 * that can be utilized in form elements or user-interface components.
 *
 * Members:
 * - TEXT: Represents a standard text input field.
 * - PASSWORD: Represents a password input field where characters are hidden.
 * - NUMBER: Represents a numeric input field for numerical values.
 */
export enum INPUT_TYPES {
  TEXT,
  PASSWORD,
  NUMBER,
}
