## bitrise settings

Uses [`selenide`](https://github.com/codeborne/selenide) to automatically configure application settings on Bitrise. Bitrise currently lacks API support for [rolling builds](https://blog.bitrise.io/auto-cancel-builds-and-keep-rolling).

This project uses the API to filter all apps owned by an organization, and then configure the settings via the web UI.

## Environment variables

| ENV name      | Description
| ------------- | ----
| BITRISE_TOKEN | API token for bitrise
| BITRISE_USER  | Bitrise username
| BITRISE_PASS  | Bitrise password
| BITRISE_ORG   | Bitrise organization

## Example Output
```
[1] iOS JavaScript WIP - https://www.bitrise.io/app/123#/settings
rolling builds? true
           PRs? true
        pushes? false

[2] iOS react-native Yarn - https://www.bitrise.io/app/456#/settings
rolling builds? true
           PRs? true
        pushes? false


Process finished with exit code 0
```