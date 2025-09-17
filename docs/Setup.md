# 🔑 Onboarding & Setup

Follow these steps to get the TrashbinCloud app up and running on your local machine.

## Prerequisites

- **Android Studio**: Narwhal 3 Feature Drop 2025.1.3
- **Git**: [Download Git](https://git-scm.com/downloads)
- **Firebase Account**


#### 1. Clone the Repository

```bash
git clone https://github.com/radleigh123/smart-dustbin.git
cd smart-dustbin
```

#### 2. Service Account Key

1. Go to the [Firebase Console](https://console.firebase.google.com/u/0/project/smart-dustbin-43323).
2. Navigate to "General".
3. Under "Your apps" > Download "google-services.json".
4. Place the JSON file in the `app/` directory of your cloned repository.
    ```text
    trashbincloud/
    └── app/
        └── serviceAccountKey.json  <-- Place the file here
    ```

#### 3. Android Studio Setup

1. On the toolbar, click `Build > Clean and Assemble Project with Tests `.
2. Let Android Studio and Gradle sync and build the project.
3. Run the app on an emulator or physical device.

### Project Structure

```text
com.eldroid.trashbincloud/
|-- contract/         # MVP Contracts
|-- model/            # Data models and repositories
    |-- repository/
    |-- data/
    |-- entity/
|-- presenter/        # Presenter implementations
|-- view/             # Activities and Fragments

res/
|-- drawable/        # Image assets
|-- font/            # Custom fonts
|-- layout/          # XML layout files
|-- values/          # Strings, colors, styles
|-- navigation/      # Navigation graph XML
|-- menu/            # Menu XML files
```

### Conventions

**MVP Architecture**: The app follows the Model-View-Presenter pattern for better separation of concerns.

##### Naming

- **Activities/Fragments** -> `fragment_<path_to_fragment>.xml` (e.g., `fragment_auth_login.xml`)
- **Adapters** -> `adapter_<name>.xml` (e.g., `adapter_trashbin_item.xml`)
- **View Binding**: Enabled for type-safe UI interactions.

##### UI Consistency

- `colors.xml`: Define and use consistent color schemes.
- `strings.xml`: Centralize all user-facing text.
- `dimens.xml`: Standardize spacing and sizing.
- `styles.xml`: Centralize text appearances and themes.

---

## Troubleshooting

- **App won't register/login**:
  - Ensure the `serviceAccountKey.json` file is correctly placed in the `app/` directory.
  - Check your internet connection.
- **Build errors**:
  - Make sure you have the correct version of Android Studio and all SDK components installed.
  - Try `File > Invalidate Caches / Restart` in Android Studio.
  - Check the `build.gradle` files for any version mismatches.
  - Ensure all dependencies are correctly downloaded by running `./gradlew build --refresh-dependencies` in the terminal.