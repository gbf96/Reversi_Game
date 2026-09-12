# Reversi (Othello) — Board Game in Kotlin

Project developed in the scope of the **Software Development Techniques (Técnicas de Desenvolvimento de Software — TDS)** course at **ISEL — Instituto Superior de Engenharia de Lisboa**.

---

## 🎓 Academic Identification

| Information | Details |
|---|---|
| **Institution** | [ISEL — Instituto Superior de Engenharia de Lisboa](https://www.isel.pt/) |
| **Degree** | BSc in Computer Science and Engineering (**LEIC** — Licenciatura em Engenharia Informática e de Computadores) |
| **Course** | Software Development Techniques (**TDS** — Técnicas de Desenvolvimento de Software) |
| **Academic Term** | Winter Semester 2025/2026 (**2526i**) |
| **Class** | **LEIC31N** |
| **Group** | **Group 11** |

### 👥 Group Members
- **52197** — Gabriel Botelho Ferreira
- **13909** — Dinis Filipe Belo Lopes
- **46975** — Bruno Filipe Lopes Carvalheira

---

## 📖 Project Overview

The objective of this project is to implement a complete and robust version of the classic strategic board game **Reversi** (also known as **Othello**) using **Kotlin**, applying fundamental software engineering techniques and architectural patterns taught in the TDS curriculum:

- **Functional Programming & Immutability**: Strictly immutable domain data structures where every move yields a new instance representing the updated game state.
- **Strong Domain Modeling**: Type-safe representations of game rules, board coordinates, directions, and player colors using Kotlin *data classes*, *sealed classes*, and *enums*.
- **State Machine Lifecycle**: Explicit modeling of the game states (`Run`, `Pass`, `Win`, `Draw`).
- **Layered Architecture & Separation of Concerns**: Strict decoupling between the pure domain logic, persistence/storage abstractions, and user interfaces.
- **Dual Interface**:
  1. **Console (CLI)**: Interactive text-based command-line interface structured with the **Command Pattern** and an extensible command execution context.
  2. **Graphical Desktop Application (GUI)**: Modern desktop user interface built with **Compose Multiplatform (Desktop JVM)** following the **MVVM (Model-View-ViewModel)** architectural pattern and powered by **Kotlin Coroutines**.

---

## ♟️ Reversi Game Rules

Reversi is played on an $8 \times 8$ grid:
1. **Initial Setup**: The game begins with 4 pieces placed in the central four squares — 2 Black (`#`) and 2 White (`@`) arranged diagonally.
2. **Valid Moves**: The player whose turn it is places a disc of their color on an empty square such that it traps (flanks) one or more opponent discs in a straight line (horizontally, vertically, or diagonally) between the newly placed disc and another disc of the current player's color.
3. **Flipping Discs**: All opponent discs flanked between the played piece and existing pieces of the same color are flipped to the current player's color.
4. **Passing the Turn (`Pass`)**: If a player has no valid legal moves on their turn, they are forced to pass. Passing is only permitted when no legal moves exist. If both players pass consecutively or the board becomes completely full, the game ends.
5. **Winning Condition**: The player with the greatest number of pieces of their color on the board at the end of the game wins. If both players have the exact same count, the game ends in a draw (`Draw`).

---

## 🚀 Key Features

### 1. Game Modes
- **Local Game (Hotseat)**: Two players share the same keyboard/screen to play turn-by-turn.
- **Distributed Multiplayer (`Clash`)**:
  - Allows two players to play asynchronously across separate processes or machines via shared storage (local file system or remote database).
  - Player 1 initializes the game session with a unique identifier (`new <color> <name>`).
  - Player 2 connects to the session (`join <name>`).
  - Strict turn validation (`isMyTurn`) prevents out-of-turn moves.
  - State synchronization is available both manually (`refresh`) and automatically (`auto-refresh` via background coroutines).

### 2. Move Suggestions (`Targets`)
- The engine dynamically calculates and exposes all valid coordinates for the current player's move.
- Both in the console (indicated with `*`) and the desktop graphical interface, players can toggle target indicators on or off.

### 3. Pluggable Storage Abstraction
The system is built on top of generic abstractions (`Storage<K, V>` and `Serializer<T>`), allowing seamless interchange of persistence mechanisms without altering domain logic:
- **`TextFileStorage`**: Persists game states into simple two-line text files within the local directory (`savedGames/`), recording the game state and board piece coordinates.
- **`MongoStorage`**: Persists game states remotely in a **MongoDB** database, enabling networked distributed matches.

---

## 🏛️ Architecture & Software Design

The project strictly follows clean, layered software architecture:

```
┌─────────────────────────────────────────────────────────────┐
│                      Presentation Layer                     │
│   ┌─────────────────────────────┐   ┌───────────────────┐   │
│   │   Reversi (Console / CLI)   │   │  ReversiCompose   │   │
│   │       (Command Pattern)     │   │   (Desktop MVVM)  │   │
│   └──────────────┬──────────────┘   └─────────┬─────────┘   │
└──────────────────┼────────────────────────────┼─────────────┘
                   │                            │
                   ▼                            ▼
┌─────────────────────────────────────────────────────────────┐
│                 Persistence / Storage Layer                 │
│    Storage<K, V>  ◄──  TextFileStorage  /  MongoStorage      │
│    Serializer<T>  ◄──  GameSerializer                       │
└──────────────────────────────┬──────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                         Domain Layer                        │
│   Game (Interface)  ◄──  Reversi (Immutable) / Clash         │
│   GameState (Run, Pass, Win, Draw)                          │
│   Coordinate, Direction, PiecesColor, Name                  │
└─────────────────────────────────────────────────────────────┘
```

- **Command Pattern & Execution Context**: In the console application, user actions (`play`, `new`, `join`, `pass`, etc.) are modeled as discrete `Command` instances executed against an immutable `CommandContext` (`CommandContext.Empty` or `CommandContext.GameInProgress`).
- **MVVM Pattern**: In the desktop UI, the `AppViewModel` encapsulates reactive UI state (`mutableStateOf`), manages coroutine lifecycles for polling/auto-refresh, and delegates domain actions.
- **Dependency Inversion Principle (DIP)**: Distributed games (`Clash`) depend purely on the generic `Storage<Name, Reversi>` interface, remaining completely decoupled from specific storage implementations.
- **State Pattern**: The lifecycle states of the game are modeled using the sealed class hierarchy `GameState`.

### 📊 System Diagrams (PlantUML)
Detailed PlantUML diagrams are available in [Reversi/docs/diagrams](Reversi/docs/diagrams/):
- **[Component Dependencies](Reversi/docs/diagrams/ComponentDependencies.png)**: Package structure and dependencies between `console`, `model`, and `storage`.
- **[Game States](Reversi/docs/diagrams/GameStates.png)**: State machine transitions between `Run`, `Pass`, `Win`, and `Draw`.
- **[Model Types](Reversi/docs/diagrams/ModelTypes.png)**: Class diagram of domain types, interfaces, and value representations.

---

## 💻 Module 1: Reversi Console (CLI)

Located in the [`Reversi/`](Reversi/) directory.

### Console Commands Reference

| Command | Description | Example Usage |
|---|---|---|
| `new <#\|@> [name]` | Starts a new local or distributed match with the chosen color (`#` Black, `@` White) | `new #` or `new # match1` |
| `join <name>` | Joins an existing distributed match by name | `join match1` |
| `play <coord>` | Plays a piece at the given coordinate (row + column) | `play 4D` or `play 3C` |
| `pass` | Passes the turn (allowed only when no valid moves exist) | `pass` |
| `targets [ON\|OFF]` | Toggles the display of valid move target indicators (`*`) | `targets ON` |
| `refresh` | Reloads the latest game state from storage (distributed games) | `refresh` |
| `show` | Redisplays the current board and game status | `show` |
| `help` | Displays the list of available commands and usage instructions | `help` |
| `exit` | Exits the application | `exit` |

---

## 🖥️ Module 2: Reversi Compose (Desktop GUI)

Located in the [`ReversiCompose/`](ReversiCompose/) directory.

Modern desktop application (JVM) developed with **Compose Multiplatform**:
- **Menu Bar**: Quick navigation for `Game` (New, Join, Refresh, Exit), `Play` (Pass), and `Options` (Show Targets, Auto-refresh).
- **Interactive Dialogs**: Windows for starting new games (local or Clash) and joining active online matches.
- **Sprite-Based Canvas Board**: Uses a dedicated sprite sheet (`sprites1.png`) to render high-resolution Black and White pieces, green felt board tiles, and move target indicators.
- **Live Status Bar (`StatusBar`)**: Displays real-time piece counts for both players, the current turn, and endgame banners (Winner / Draw).
- **Background Synchronization**: Non-blocking auto-refresh powered by Kotlin Coroutines with asynchronous delays.

---

## 🛠️ Build & Execution Instructions

### Prerequisites
- **Java Development Kit (JDK)**: Version 21 or higher.
- **Gradle**: Local Gradle installation is not required (use the provided `gradlew` / `gradlew.bat` wrappers).

### 1. Running the Console Application
```bash
# Navigate to the console project directory
cd Reversi

# On Linux / macOS:
./gradlew run --console=plain

# On Windows:
.\gradlew.bat run --console=plain
```

### 2. Running the Desktop Application (Compose Desktop)
```bash
# Navigate to the Compose project directory
cd ReversiCompose

# On Linux / macOS:
./gradlew :composeApp:run

# On Windows:
.\gradlew.bat :composeApp:run
```

### 3. Running Automated Tests
The project features comprehensive unit test coverage verifying move legality, piece flipping rules, win/draw state transitions, serialization, and storage behavior:

```bash
# Run tests for the Reversi module (Domain & Console)
cd Reversi
./gradlew test

# Run tests for the ReversiCompose module
cd ReversiCompose
./gradlew check
```

---

## 🧰 Technologies & Tools

- **Programming Language**: [Kotlin](https://kotlinlang.org/) (v2.x, JVM target 21)
- **UI Framework**: [JetBrains Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/) (Material 3)
- **Asynchronous Concurrency**: [Kotlinx Coroutines](https://github.com/Kotlin/kotlinx.coroutines)
- **I/O & File Management**: [Okio](https://square.github.io/okio/)
- **NoSQL Database Driver**: [MongoDB Kotlin Driver Sync](https://www.mongodb.com/docs/drivers/kotlin/coroutine/current/)
- **Unit Testing**: `kotlin.test` on [JUnit Platform](https://junit.org/)
- **Build System**: Gradle with Kotlin DSL (`build.gradle.kts`)
- **Architecture Modeling**: [PlantUML](https://plantuml.com/)

---

## 📄 License & Academic Attribution

This repository was developed exclusively for academic evaluation purposes in the **Software Development Techniques (Técnicas de Desenvolvimento de Software — TDS)** course at **ISEL**. All rights belong to the respective authors and course faculty.
