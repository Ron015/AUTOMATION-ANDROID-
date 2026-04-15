# Automation Android (Java + AccessibilityService)

A full-featured Android automation engine inspired by Tasker/Auto-clicker workflows.

## Features

- **AccessibilityService automation runtime**
  - UI tree traversal across active window
  - Dynamic node discovery
  - Actions: `click`, `long_click`, `input`, `scroll`, `swipe`, `wait`
- **Advanced selector system**
  - Select by `text`, `id`, `desc`, `class`, `package`, `bounds`, `index`, `regex`
  - Fallback selector chain via `find[]`
  - XPath-like selector path traversal via `path[]`
- **JSON scripting engine**
  - Parses scripts into executable steps
  - Combined selectors and action payloads
  - Conditional blocks (`if`) and loop blocks (`loop`)
- **Smart delay system**
  - `delay`, `delay_before`, `delay_after`
  - `delay_random` for humanized interaction
  - `wait_for`, `wait_until_gone` polling conditions
- **Retry engine**
  - `max_attempts`, `delay`, `on_fail`
  - `until_found` + `timeout`
- **Recorder mode**
  - Converts click/text/scroll interactions into JSON steps
- **Floating bot controller**
  - Overlay Start / Pause / Stop
- **Visual Builder**
  - Build steps from UI form
  - Live JSON generation and persistence

## Modules

- `service/AutomationAccessibilityService` – entry point for automation & recorder
- `engine/AutomationEngine` – orchestrates step execution, conditions, loops
- `selector/SelectorEngine` – tree traversal + selector matching + path navigation
- `actions/ActionExecutor` – performs Accessibility actions and gestures
- `engine/DelayManager` – delay and wait synchronizations
- `engine/RetryManager` – retry/skip/stop failure policies
- `parser/ScriptParser` – JSON → model conversion
- `overlay/FloatingControllerService` – chat-head style bot controls
- `ui/ScriptEditorActivity` – visual step editor and JSON preview

## Example JSON

```json
{
  "steps": [
    {
      "action": "click",
      "find": [
        { "id": "com.app:id/login_btn" },
        { "text": "Login" },
        { "desc": "login button" }
      ],
      "delay_before": 1000,
      "delay_after": 1500,
      "retry": {
        "max_attempts": 5,
        "delay": 1000,
        "on_fail": "retry"
      }
    },
    {
      "action": "input",
      "id": "com.app:id/username",
      "value": "user123"
    },
    {
      "action": "input",
      "id": "com.app:id/password",
      "value": "pass123"
    },
    {
      "action": "if",
      "if": {
        "exists": { "text": "Submit" },
        "then": [
          { "action": "click", "text": "Submit" }
        ],
        "else": [
          { "action": "scroll" }
        ]
      }
    },
    {
      "action": "loop",
      "loop": {
        "count": 3,
        "steps": [
          { "action": "swipe", "duration": 600, "delay_random": { "min": 500, "max": 1200 } }
        ]
      }
    }
  ]
}
```

## Compliance & Safety

This app is designed for accessibility-driven test automation and personal productivity workflows.

- Clearly disclose Accessibility usage in onboarding/UI.
- Only automate apps/workflows where you have user consent and legal permission.
- Do not use for credential theft, bypassing platform security, or abusive automation.

## Running

1. Open project in Android Studio (JDK 17 / AGP 8.4+).
2. Build and install on a physical device/emulator API 26+.
3. Launch app and:
   - Enable Accessibility service
   - Grant overlay permission
   - Configure JSON in visual builder
   - Start automation from floating controller

## CI Workflow

This repository includes a GitHub Actions workflow at `.github/workflows/android-build.yml` that installs JDK 17 + Android SDK and runs:

- `gradle :app:assembleDebug --stacktrace`

It also uploads `app-debug.apk` as a workflow artifact.
