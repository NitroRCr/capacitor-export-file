# capacitor-export-file

A Capacitor plugin to export files using the Android SAF (Storage Access Framework) API and iOS UIDocumentPickerViewController.

- Supports Capacitor v7.
- Works on Android, iOS & Web

## Requirements

* **Kotlin** must be configured in your Android project (required for Android support).
* **iOS 16.0+** is required for the iOS implementation.

## Install

```bash
npm install capacitor-export-file
npx cap sync
```

## API

<docgen-index>

* [`exportFile(...)`](#exportfile)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### exportFile(...)

```typescript
exportFile(options: { uri: string; filename?: string; }) => Promise<{ uri: string; }>
```

| Param         | Type                                             |
| ------------- | ------------------------------------------------ |
| **`options`** | <code>{ uri: string; filename?: string; }</code> |

**Returns:** <code>Promise&lt;{ uri: string; }&gt;</code>

--------------------

</docgen-api>
