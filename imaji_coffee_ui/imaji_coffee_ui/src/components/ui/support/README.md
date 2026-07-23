# Support widget

This widget offers two support paths:

1. **Talk to admin** opens the live chat route.
2. **FAQ Assistant** answers common questions from a local, approved topic list.

The FAQ assistant is intentionally static for now. It is designed to be safe and fast without an external model or extra backend dependency. If a backend assistant is added later, it should keep the same modal flow and preserve the admin escalation button.

The FAQ assistant does not route to chat directly; escalation is handled by the support widget container so FAQ and admin chat stay decoupled.
