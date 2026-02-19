# MiniProject-2_FocusSphere
FocusSphere is a premium Java Spring Boot productivity app. It combines advanced task management, markdown notes, habit tracking, expenses, and a daily schedule into one beautiful interface. With safe local data storage, financial charting, and detailed analytics, it transforms how you organize your life, boosting focus and efficiency daily.

FocusSphere is a premium, all-in-one personal productivity workspace designed to centralize and elevate your digital organization. Built on a robust Spring Boot backend with a stunning, high-performance frontend, it seamlessly combines task management, note-taking, habit tracking, financial planning, and scheduling into a single, immersive interface.

At the heart of the application is the Task Command Center, which goes beyond simple to-do lists by supporting nested subtasks, recurring schedules, priority matrixing, and smart tagging. This is complemented by a distraction-free Note Editor that features real-time auto-saving and markdown support, making it perfect for both quick thoughts and deep drafting.

For personal development, the Habit Tracker uses gamification elements like consistency streaks and confetti celebrations to reinforce positive behaviors. Financial discipline is maintained through the Expense Ledger, which offers detailed spending categorization, budget cap alerts, and interactive doughnut charts for visual analysis. The Daily Timeline binds these elements together, providing a linear, 24-hour view of your scheduled commitments to prevent time crunches.

What truly sets FocusSphere apart is its Integrated Analytics Engine. By aggregating data from every module, it generates comprehensive insights such as a daily "Productivity Score," mood correlation graphs, and focus-hour metrics. This feedback loop empowers users to understand their peak performance times and optimize their routines.

From a technical perspective, the app runs on a secure Java 17 Spring Boot server, ensuring enterprise-grade reliability. The frontend creates a fluid user experience using Tailwind CSS for advanced styling and Vanilla JavaScript for instant state management, free from the bloat of heavy frameworks. All data is persisted locally via browser LocalStorage, guaranteeing 100% data privacy and offline capability, with options to export/import JSON backups. With its sophisticated dark mode, fluid animations ("glassmorphism" design), and holistic approach to productivity, FocusSphere transforms the mundane task of planning into an engaging, premium experience.

<p align="center">
  <strong>⚡ FocusSphere</strong><br/>
  <em>A Comprehensive Desktop Productivity Dashboard</em>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-17+-blue?style=flat-square&logo=openjdk" alt="Java 17+"/>
  <img src="https://img.shields.io/badge/JavaFX-17.0.6-purple?style=flat-square" alt="JavaFX 17"/>
  <img src="https://img.shields.io/badge/SQLite-3.42-green?style=flat-square&logo=sqlite" alt="SQLite"/>
  <img src="https://img.shields.io/badge/Maven-Build-red?style=flat-square&logo=apachemaven" alt="Maven"/>
  <img src="https://img.shields.io/badge/License-MIT-yellow?style=flat-square" alt="MIT License"/>
</p>

---

## 📋 Table of Contents

- [Overview](#-overview)
- [Key Features](#-key-features)
- [Technology Stack](#-technology-stack)
- [Architecture](#-architecture)
- [Module Breakdown](#-module-breakdown)
  - [Dashboard](#1--dashboard-dashboardviewjava)
  - [Task Manager](#2--task-manager-tasksviewjava)
  - [Notes Editor](#3--notes-editor-notesviewjava)
  - [Habit Tracker](#4--habit-tracker-habitsviewjava)
  - [Expense Manager](#5--expense-manager-expensesviewjava)
  - [Schedule Planner](#6--schedule-planner-scheduleviewjava)
  - [Analytics Dashboard](#7--analytics-dashboard-analyticsviewjava)
- [Shared Components](#-shared-components)
- [Database Schema](#-database-schema)
- [Design System](#-design-system)
- [Getting Started](#-getting-started)
- [Keyboard Shortcuts](#-keyboard-shortcuts)
- [Project Structure](#-project-structure)

---

## 🌟 Overview

**FocusSphere** is a fully Java-based, feature-rich desktop productivity application built entirely with **JavaFX 17** and **SQLite**. It consolidates seven essential productivity tools into a single, unified interface — eliminating the need to switch between multiple applications for task management, note-taking, habit tracking, expense monitoring, schedule planning, and performance analytics.

The application follows a **single-window, multi-view architecture** with a persistent sidebar navigation, a contextual header bar, and a dynamic content area that renders the active module. All user data is stored locally in an embedded SQLite database (`focussphere.db`), ensuring privacy, offline availability, and zero external dependencies.

---

## ✨ Key Features

| Feature | Description |
|---------|-------------|
| **Unified Dashboard** | At-a-glance overview of pending tasks, expenses, events, mood, and quick notes |
| **Task Management** | Full CRUD with priorities, due dates, tags, filtering, and Pomodoro integration |
| **Rich Notes Editor** | Multi-document note-taking with auto-save, search, word count, and formatting toolbar |
| **Habit Tracker** | 7-day visual grid, streak tracking, and daily toggle for building consistent routines |
| **Expense Tracker** | Categorized expense logging with budget bar, monthly limits, and spending summaries |
| **Schedule Planner** | 24-hour visual timeline with color-coded event blocks and current-time indicator |
| **Analytics Dashboard** | KPI cards, task trends, productivity scores, mood history, and weekly activity bars |
| **Pomodoro Timer** | Floating widget with 25/5 work-break cycles, task binding, and mode switching |
| **Dark / Light Theme** | Full dual-theme support with persisted user preference |
| **Keyboard Shortcuts** | Power-user navigation with `Ctrl+1` through `Ctrl+7`, `Ctrl+K` quick find, and more |

---

## 🛠 Technology Stack

| Layer | Technology | Purpose |
|-------|-----------|---------|
| **Language** | Java 17+ | Core application logic, business rules, and data processing |
| **UI Framework** | JavaFX 17.0.6 | Desktop GUI rendering, scene graph management, and event handling |
| **Database** | SQLite 3.42 (via JDBC) | Local persistent storage for all user data |
| **Build Tool** | Apache Maven 3.x | Dependency management, compilation, and execution |
| **Styling** | JavaFX CSS | Custom design system with glassmorphism, gradients, and dual theming |

### Why These Choices?

- **JavaFX** provides a mature, GPU-accelerated UI toolkit with CSS-based styling, making it ideal for building polished desktop interfaces without web dependencies.
- **SQLite** offers zero-configuration, serverless, file-based persistence — the entire database is a single `focussphere.db` file, making the app fully portable.
- **Maven** ensures reproducible builds and automatic dependency resolution for JavaFX modules and the SQLite JDBC driver.

---

## 🏗 Architecture

FocusSphere follows a **layered architecture** with clear separation of concerns:

```
┌──────────────────────────────────────────────────────────────────┐
│                        PRESENTATION LAYER                        │
│  ┌──────────┐  ┌───────────┐  ┌──────────────────────────────┐  │
│  │ Sidebar  │  │ HeaderBar │  │       Content Area           │  │
│  │          │  │           │  │  ┌─────────────────────────┐ │  │
│  │ Nav Btns │  │ Greeting  │  │  │    Active View          │ │  │
│  │          │  │ Date      │  │  │  (Dashboard / Tasks /   │ │  │
│  │ Settings │  │ Quick Find│  │  │   Notes / Habits /      │ │  │
│  │ Theme    │  │ Pomodoro  │  │  │   Expenses / Schedule / │ │  │
│  │          │  │           │  │  │   Analytics)            │ │  │
│  └──────────┘  └───────────┘  │  └─────────────────────────┘ │  │
│                               └──────────────────────────────┘  │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │  Floating Overlays: PomodoroWidget, ToastNotification   │   │
│  └──────────────────────────────────────────────────────────┘   │
├──────────────────────────────────────────────────────────────────┤
│                        APPLICATION LAYER                         │
│                App.java (Controller / Navigator)                 │
│     Navigation • Theme Management • Settings • Shortcuts         │
├──────────────────────────────────────────────────────────────────┤
│                          MODEL LAYER                             │
│       Task │ Note │ Habit │ Expense │ ScheduleEvent              │
├──────────────────────────────────────────────────────────────────┤
│                       PERSISTENCE LAYER                          │
│               DatabaseManager.java (SQLite JDBC)                 │
│          7 Tables │ CRUD Operations │ Aggregations                │
└──────────────────────────────────────────────────────────────────┘
```

### Data Flow

1. **User Action** → UI event fired in a View (button click, text change, etc.)
2. **View Logic** → View calls `DatabaseManager` static methods for data operations
3. **Database Layer** → `DatabaseManager` executes parameterized SQL via JDBC
4. **State Update** → View calls its own `refresh()` method to re-render from database state
5. **Cross-View Updates** → `App.navigateTo()` triggers `refresh()` on the target view

### Navigation Model

The `App` class acts as a **central coordinator**:

- Maintains a `StackPane` (`rootStack`) as the scene root with theme CSS class
- Uses a `BorderPane` layout: **Left** = Sidebar, **Top** = HeaderBar, **Center** = Content Area
- `navigateTo(String view)` clears the content area, calls `refresh()` on the target view, and updates the sidebar active state
- The `PomodoroWidget` and `ToastNotification` are rendered as overlays on the root `StackPane`, independent of navigation

---

## 📦 Module Breakdown

### 1. 🏠 Dashboard (`DashboardView.java`)

**Purpose:** Centralized overview providing instant visibility into all productivity metrics.

**Layout:** Vertical scrollable container with three row sections.

| Section | Components | Behavior |
|---------|-----------|----------|
| **Stat Cards Row** | Pending Tasks, Total Spent, Events Today | Clickable cards — each navigates to its respective view. Values pulled from `DatabaseManager` aggregate queries. |
| **Quick Notes + Weather** | Auto-saving scratchpad + simulated weather widget | Scratchpad persists to `settings` table with key `"scratchpad"`. Weather randomizes from a preset array on button click. |
| **Mood + Quote** | 4 mood buttons + rotating motivational quotes | Mood selection logged to `moods` table with timestamp. Active mood visually highlighted. Quote randomized from an internal array. |

**Technical Details:**
- Extends `ScrollPane` with `fitToWidth` for responsive horizontal behavior
- Stat card values use `DatabaseManager.countPendingTasks()`, `getTotalExpenses()`, and `countEventsToday()`
- Scratchpad auto-saves on every keystroke via a `textProperty` change listener

---

### 2. ✅ Task Manager (`TasksView.java`)

**Purpose:** Full-featured task management system with priorities, due dates, tagging, and filtering.

**Layout:** Vertical container with header, input form, filter bar, scrollable task list, and statistics row.

| Feature | Implementation |
|---------|---------------|
| **Task Creation** | Inline form with title field, `DatePicker`, `ComboBox` (Low/Medium/High), and Add button. Supports `#tag` syntax in the title field — tags are auto-extracted and stored separately. |
| **Task Cards** | Each task rendered as an `HBox` with: `CheckBox` (toggle completion), title label (with strikethrough when done), priority badge (color-coded), due date badge (red if overdue), tag badges, Pomodoro button, and delete button. |
| **Filtering** | `ComboBox` with All / Active / Completed. Filter applied client-side during `refresh()`. |
| **Bulk Actions** | "Clear Completed" button deletes all finished tasks via `DatabaseManager.deleteCompletedTasks()`. |
| **Statistics Row** | Four mini-stat cards showing: Completed count, Pending count, High Priority count, Overdue count. |
| **Pomodoro Integration** | Per-task ⏱ button calls `App.startPomodoroWithTask(title)`, launching the floating timer pre-loaded with the task name. |

**Technical Details:**
- Overdue detection: `LocalDate.parse(dueDate).isBefore(LocalDate.now())`
- Priority color mapping: High → `#ef4444`, Medium → `#f59e0b`, Low → `#22c55e`
- Tag extraction uses regex split on whitespace, filtering tokens starting with `#`
- Completion percentage displayed in a `stats-pill` badge next to the view title

---

### 3. 📝 Notes Editor (`NotesView.java`)

**Purpose:** Multi-document text editor with real-time search, auto-save, and formatting support.

**Layout:** Horizontal split — left sidebar (document list) + right editor pane.

| Feature | Implementation |
|---------|---------------|
| **Document List** | Scrollable `VBox` of note items showing title and 60-character content preview. Active note highlighted with accent border. |
| **Search** | Real-time text filtering as the user types. Matches against title and content. |
| **Editor** | `TextField` for title + `TextArea` for content body. Both bound to auto-save. |
| **Auto-Save** | Debounced at 600ms using `java.util.Timer`. Status indicator shows "Saving..." → "Saved ✓". |
| **Formatting Toolbar** | Buttons for Bold (`**bold**`), Italic (`*italic*`), Heading (`# Heading`), List (`- item`), Code (`` ```code``` ``). Appends markdown syntax to the content area. |
| **Word Count** | Real-time word counter in the footer, calculated via `content.trim().split("\\s+").length`. |
| **Empty State** | When no note is selected, displays a centered icon with instructional text. |

**Technical Details:**
- Debounce pattern: each keystroke cancels the previous `Timer` and schedules a new `TimerTask` at 600ms
- `Platform.runLater()` ensures database writes and UI updates happen on the JavaFX Application Thread
- Note ordering: `ORDER BY updated_at DESC` — most recently edited notes appear first

---

### 4. 🔄 Habit Tracker (`HabitsView.java`)

**Purpose:** Daily habit monitoring with visual streaks and a 7-day completion grid.

**Layout:** Vertical scrollable list of habit cards.

| Feature | Implementation |
|---------|---------------|
| **Habit Creation** | Modal `TextInputDialog` for entering the habit name. Stored via `DatabaseManager.addHabit()`. |
| **7-Day Grid** | Each habit card shows the last 7 days as clickable boxes. Completed days are highlighted green. Day labels show 2-letter abbreviations (Mo, Tu, We...). |
| **Streak Counter** | Calculated by `Habit.getCurrentStreak()` — walks backward from today counting consecutive completed days. Displayed with a 🔥 icon. |
| **Toggle Mechanism** | Clicking a day box toggles that date in the habit's `completedDays` comma-separated string. `DatabaseManager.updateHabitDays()` persists the change. |

**Technical Details:**
- `completedDays` stored as a comma-separated string of ISO dates (e.g., `"2026-02-17,2026-02-18,2026-02-19"`)
- `Habit.getCompletedDaysSet()` parses this into a `HashSet<String>` for O(1) lookups
- `Habit.toggleDay(dateStr)` adds or removes a date from the set, then re-serializes

---

### 5. 💰 Expense Manager (`ExpensesView.java`)

**Purpose:** Financial tracking with categorized expenses, budget monitoring, and spending analysis.

**Layout:** Horizontal split — left form panel + right transaction list with budget overview.

| Feature | Implementation |
|---------|---------------|
| **Expense Entry** | Four-field form: Description, Amount ($), Date (`DatePicker` defaulting to today), Category (free-text with placeholder suggestions). |
| **Budget Bar** | `ProgressBar` showing spending against a user-defined monthly budget. Color transitions: green (< 50%) → amber (50–80%) → red (> 80%). |
| **Budget Setting** | "Set Budget" button opens a `TextInputDialog`. Value persisted to `settings` table. |
| **Transaction List** | Each expense rendered as an `HBox` card with: category dot (color derived from name hash), description, amount (red text), date, and delete button. |
| **Total Display** | Large formatted total (`$XX.XX`) pulled from `DatabaseManager.getTotalExpenses()`. |

**Technical Details:**
- Category colors generated deterministically via `Math.abs(category.hashCode()) % colors.length` from an 8-color palette
- Budget ratio: `total / monthlyBudget`, clamped to `[0, 1]` for the progress bar
- Budget bar color applied via inline style: `-fx-accent:<color>`
- Default budget: $1000, overridden by persisted `"monthlyBudget"` setting

---

### 6. 📅 Schedule Planner (`ScheduleView.java`)

**Purpose:** Visual daily schedule with a 24-hour timeline, color-coded event blocks, and current-time indicator.

**Layout:** Vertical container with header and scrollable timeline `Pane`.

| Feature | Implementation |
|---------|---------------|
| **24-Hour Timeline** | A `Pane` of height `24 × 60px`. Hour labels positioned at `y = hour × 60px`. Horizontal divider lines at each hour mark. |
| **Event Blocks** | Colored `VBox` overlays positioned at `y = (startMinute / 60) × 60px` with height proportional to duration. Shows title and time range. |
| **Current Time Line** | Red horizontal line at `y = (currentHour × 60 + currentMinute) / 60 × 60px`. Red dot marker on the left edge. |
| **Auto-Scroll** | On load, the scroll position jumps to the current hour minus 1. |
| **Event Creation** | Dialog with: Title field, Start time (hour + minute Spinners), End time (Spinners), Color picker (4 radio buttons). |
| **Event Deletion** | Right-click context menu on any event block → "Delete" option. |

**Technical Details:**
- Timeline uses absolute positioning (`setLayoutX/Y`) rather than layout managers
- `ScheduleEvent.getStartMinute()` parses `"HH:mm"` into minutes-from-midnight
- `ScheduleEvent.getDurationMinutes()` calculates `endMinutes - startMinutes`
- Events queried per-date: `DatabaseManager.getEventsForDate(today)`
- Event block colors stored as hex strings with `cc` alpha suffix for slight transparency

---

### 7. 📈 Analytics Dashboard (`AnalyticsView.java`)

**Purpose:** Data-driven insights with charts, KPI cards, and weekly activity visualization.

**Layout:** Vertical scrollable container with KPI row + two chart rows.

| Section | Components |
|---------|-----------|
| **KPI Cards** | 4 stat cards: Tasks Done (✅), Habit Streak (🔥), Expenses ($), Focus Time (⏱). Each with colored value and descriptive label. |
| **Charts Row 1** | `LineChart` (Task Completion Trend) + `BarChart` (Productivity Score). |
| **Charts Row 2** | `LineChart` (Mood Tracker) + Custom weekly activity bars. |

| KPI | Data Source | Calculation |
|-----|-----------|-------------|
| Tasks Done | `getAllTasks()` | Count where `isCompleted() == true` |
| Habit Streak | `getAllHabits()` | Maximum `getCurrentStreak()` across all habits |
| Expenses | `getAllExpenses()` | Sum of `getAmount()` within the selected date range |
| Focus Time | `getEventsForDate()` | Sum of `(endMinutes - startMinutes) / 60` across date range |

| Chart | X-Axis | Y-Axis | Data |
|-------|--------|--------|------|
| Task Trend | Dates (MM-dd) | Count | Completed tasks grouped by creation date |
| Productivity | Dates (MM-dd) | Score | `(habitsCompleted × 20) + (eventsCount × 10)` per day |
| Mood | Dates (MM-dd) | 1–4 scale | Awesome=4, Good=3, Tired=2, Stressed=1 |
| Weekly Bars | Day names | Activity % | Combined habit/event/task score per day |

**Technical Details:**
- Date range controlled by `ComboBox`: Last 7 / 30 / 90 days
- Charts use JavaFX `LineChart` and `BarChart` with `CategoryAxis` × `NumberAxis`
- Mood Y-axis uses a custom `StringConverter` to display text labels (Awsme, Good, Tired, Strss)
- Weekly activity bars use rotated `ProgressBar` widgets with `bar-high` / `bar-med` / `bar-low` CSS classes

---

## 🧩 Shared Components

### `Sidebar.java`
- Persistent left navigation panel with 7 view buttons + Settings + Theme toggle
- Active item highlighted with `nav-button-active` CSS class (left accent border + glow)
- Settings dialog: Display Name, Accent Color selection, Export Backup, Wipe All Data

### `HeaderBar.java`
- Contextual greeting based on time of day ("Good Morning/Afternoon/Evening, {name}!")
- Current date display formatted as "Thursday, February 19, 2026"
- Quick Find button (triggers `Ctrl+K` command palette)
- Pomodoro toggle button

### `PomodoroWidget.java`
- Floating overlay anchored to top-right of the window
- 25-minute work / 5-minute break timer with play/pause/reset/skip controls
- Task name binding — shows which task the user is focusing on
- Mode label changes color: indigo for work, green for break
- Uses `javafx.animation.Timeline` with 1-second `KeyFrame` ticks

### `ToastNotification.java`
- Non-modal slide-up notification at the bottom center
- Success (green) or error (red) variants with emoji prefix
- Animated entrance: `TranslateTransition` (slide up) + `FadeTransition` (fade in)
- Auto-dismiss after 2.5 seconds with fade-out animation

---

## 🗄 Database Schema

All data is stored in `focussphere.db` (auto-created on first launch).

```sql
-- Core data tables
tasks (id, title, description, due_date, priority, completed, tags, recurring, created_at)
notes (id, title, content, created_at, updated_at)
expenses (id, description, amount, date, category)
schedule_events (id, title, start_time, end_time, color, date)
habits (id, name, completed_days, created_at)

-- System tables
settings (key PRIMARY KEY, value)          -- Key-value store for preferences
moods (id, mood, logged_at)                -- Mood log with timestamps
```

**Key design decisions:**
- All timestamps use `datetime('now','localtime')` for local timezone consistency
- `habits.completed_days` uses a comma-separated date string for simplicity (avoids a junction table)
- `settings` table uses `INSERT OR REPLACE` (upsert) for idempotent preference saving
- All queries use `PreparedStatement` with parameterized values to prevent SQL injection

---

## 🎨 Design System

### Theme Architecture

FocusSphere supports **two distinct visual themes** toggled at runtime:

| Aspect | Dark Theme | Light Theme |
|--------|-----------|-------------|
| Background | `#0b1120` → `#1e293b` gradient | `#ffffff` → `#f1f5f9` gradient |
| Cards | `rgba(16, 24, 42, 0.82)` glass | `rgba(255, 255, 255, 0.92)` glass |
| Text Primary | `#f1f5f9` | `#1e293b` |
| Text Secondary | `#94a3b8` | `#64748b` |
| Accent | `#818cf8` (Indigo 400) | `#4f46e5` (Indigo 600) |
| Borders | `rgba(255, 255, 255, 0.09)` | `rgba(226, 232, 240, 0.7)` |

### Design Principles

- **Glassmorphism** — Cards use semi-transparent backgrounds with subtle borders and drop shadows
- **Consistent spacing** — 10–12px border radii, 14–22px padding, 16–20px gaps
- **Hierarchy through contrast** — Primary text bright, secondary muted, accents vibrant
- **Interactive feedback** — Hover states with background brightening and shadow deepening
- **Clean scrollbars** — 6px-wide custom scrollbar thumbs, hidden increment/decrement arrows

---

## 🚀 Getting Started

### Prerequisites

- **Java 17+** (JDK, not just JRE)
- **Apache Maven 3.x** installed and on PATH

### Build & Run

```bash
# 1. Clone or navigate to the project directory
cd FocusSphere-(Fully_Java_Based_Creation)

# 2. Compile the project
mvn compile

# 3. Launch the application
mvn javafx:run
```

The application will open a 1300×820 window with a 1.5-second branded loading screen, then navigate to the Dashboard.

### First-Time Setup

1. Click **⚙ Settings** in the sidebar bottom
2. Enter your **Display Name** (shown in the header greeting)
3. Optionally set a **Monthly Budget** for the Expense Tracker
4. Start adding tasks, notes, habits, and schedule blocks!

---

## ⌨ Keyboard Shortcuts

| Shortcut | Action |
|----------|--------|
| `Ctrl + 1` | Navigate to Dashboard |
| `Ctrl + 2` | Navigate to Tasks |
| `Ctrl + 3` | Navigate to Notes |
| `Ctrl + 4` | Navigate to Habits |
| `Ctrl + 5` | Navigate to Expenses |
| `Ctrl + 6` | Navigate to Schedule |
| `Ctrl + 7` | Navigate to Analytics |
| `Ctrl + N` | Go to Tasks + focus input field |
| `Ctrl + K` | Open Quick Find command palette |
| `Escape` | Close Pomodoro widget |

---

## 📁 Project Structure

```
FocusSphere-(Fully_Java_Based_Creation)/
├── pom.xml                              # Maven build configuration
├── focussphere.db                       # SQLite database (auto-generated)
├── README.md                            # This documentation
├── run.txt                              # Quick-start commands
│
└── src/main/
    ├── java/com/focussphere/
    │   ├── App.java                     # Application entry, navigation, theme mgmt
    │   ├── Launcher.java                # Non-Application entry point (packaging)
    │   │
    │   ├── components/
    │   │   ├── Sidebar.java             # Navigation panel + settings dialog
    │   │   ├── HeaderBar.java           # Greeting, date, quick find, pomodoro btn
    │   │   ├── PomodoroWidget.java      # Floating focus timer overlay
    │   │   └── ToastNotification.java   # Animated notification popups
    │   │
    │   ├── views/
    │   │   ├── DashboardView.java       # Overview: stats, scratchpad, mood, quotes
    │   │   ├── TasksView.java           # Task CRUD, priorities, tags, filtering
    │   │   ├── NotesView.java           # Multi-doc editor with auto-save
    │   │   ├── HabitsView.java          # Daily habit grid + streak tracking
    │   │   ├── ExpensesView.java        # Expense logging + budget monitoring
    │   │   ├── ScheduleView.java        # 24-hour visual timeline planner
    │   │   └── AnalyticsView.java       # Charts, KPIs, weekly activity
    │   │
    │   ├── model/
    │   │   ├── Task.java                # Task data model (POJO)
    │   │   ├── Note.java                # Note data model with word count
    │   │   ├── Habit.java               # Habit model with streak calculation
    │   │   ├── Expense.java             # Expense data model
    │   │   └── ScheduleEvent.java       # Event model with time parsing
    │   │
    │   └── db/
    │       └── DatabaseManager.java     # All SQL operations (static methods)
    │
    └── resources/
        └── styles.css                   # Complete JavaFX CSS design system
```

---

<p align="center">
  Built with ❤️ using Java 17, JavaFX, and SQLite<br/>
  <strong>FocusSphere</strong> — Your productivity, unified.
</p>
