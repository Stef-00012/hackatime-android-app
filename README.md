# Hackatime Backend Server

The backend server used in the [Hackatime Stats](https://git.stefdp.com/Stef/Hackatime-android-app) app

## How to run

1. Get a `firebase-adminsdk.json` file from firebase and put it in the repo root directory
2. Run `bun db:setup` to setup the database migration
3. start the server with `bun dev` (or `bun run build` and `bun start` for production)
