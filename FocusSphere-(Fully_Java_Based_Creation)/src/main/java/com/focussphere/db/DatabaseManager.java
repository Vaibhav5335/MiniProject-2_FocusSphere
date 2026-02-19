package com.focussphere.db;

import com.focussphere.model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:focussphere.db";

    // =================== INIT ===================
    public static void initialize() {
        try (Connection c = getConnection(); Statement s = c.createStatement()) {
            s.execute("""
                CREATE TABLE IF NOT EXISTS tasks (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    title TEXT NOT NULL,
                    description TEXT,
                    due_date TEXT,
                    priority TEXT DEFAULT 'Medium',
                    completed INTEGER DEFAULT 0,
                    tags TEXT,
                    recurring TEXT,
                    created_at TEXT DEFAULT (datetime('now','localtime'))
                )""");

            s.execute("""
                CREATE TABLE IF NOT EXISTS notes (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    title TEXT NOT NULL,
                    content TEXT,
                    created_at TEXT DEFAULT (datetime('now','localtime')),
                    updated_at TEXT DEFAULT (datetime('now','localtime'))
                )""");

            s.execute("""
                CREATE TABLE IF NOT EXISTS expenses (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    description TEXT NOT NULL,
                    amount REAL NOT NULL,
                    date TEXT,
                    category TEXT
                )""");

            s.execute("""
                CREATE TABLE IF NOT EXISTS schedule_events (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    title TEXT NOT NULL,
                    start_time TEXT,
                    end_time TEXT,
                    color TEXT DEFAULT '#6366f1',
                    date TEXT
                )""");

            s.execute("""
                CREATE TABLE IF NOT EXISTS habits (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    completed_days TEXT DEFAULT '',
                    created_at TEXT DEFAULT (datetime('now','localtime'))
                )""");

            s.execute("""
                CREATE TABLE IF NOT EXISTS settings (
                    key TEXT PRIMARY KEY,
                    value TEXT
                )""");

            s.execute("""
                CREATE TABLE IF NOT EXISTS moods (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    mood TEXT NOT NULL,
                    logged_at TEXT DEFAULT (datetime('now','localtime'))
                )""");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    // =================== TASKS ===================
    public static int addTask(Task t) {
        String sql = "INSERT INTO tasks(title,description,due_date,priority,tags,recurring) VALUES(?,?,?,?,?,?)";
        try (Connection c = getConnection();
             PreparedStatement p = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            p.setString(1, t.getTitle());
            p.setString(2, t.getDescription());
            p.setString(3, t.getDueDate());
            p.setString(4, t.getPriority());
            p.setString(5, t.getTags());
            p.setString(6, t.getRecurring());
            p.executeUpdate();
            ResultSet rs = p.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return -1;
    }

    public static List<Task> getAllTasks() {
        List<Task> list = new ArrayList<>();
        try (Connection c = getConnection();
             Statement s = c.createStatement();
             ResultSet r = s.executeQuery(
                 "SELECT * FROM tasks ORDER BY completed ASC, created_at DESC")) {
            while (r.next()) {
                Task t = new Task();
                t.setId(r.getInt("id"));
                t.setTitle(r.getString("title"));
                t.setDescription(r.getString("description"));
                t.setDueDate(r.getString("due_date"));
                t.setPriority(r.getString("priority"));
                t.setCompleted(r.getInt("completed") == 1);
                t.setTags(r.getString("tags"));
                t.setRecurring(r.getString("recurring"));
                t.setCreatedAt(r.getString("created_at"));
                list.add(t);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public static void updateTaskCompleted(int id, boolean completed) {
        try (Connection c = getConnection();
             PreparedStatement p = c.prepareStatement(
                 "UPDATE tasks SET completed=? WHERE id=?")) {
            p.setInt(1, completed ? 1 : 0);
            p.setInt(2, id);
            p.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static void deleteTask(int id) {
        try (Connection c = getConnection();
             PreparedStatement p = c.prepareStatement(
                 "DELETE FROM tasks WHERE id=?")) {
            p.setInt(1, id);
            p.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static void deleteCompletedTasks() {
        try (Connection c = getConnection();
             Statement s = c.createStatement()) {
            s.execute("DELETE FROM tasks WHERE completed=1");
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // =================== NOTES ===================
    public static int addNote(Note n) {
        String sql = "INSERT INTO notes(title,content) VALUES(?,?)";
        try (Connection c = getConnection();
             PreparedStatement p = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            p.setString(1, n.getTitle());
            p.setString(2, n.getContent());
            p.executeUpdate();
            ResultSet rs = p.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return -1;
    }

    public static List<Note> getAllNotes() {
        List<Note> list = new ArrayList<>();
        try (Connection c = getConnection();
             Statement s = c.createStatement();
             ResultSet r = s.executeQuery(
                 "SELECT * FROM notes ORDER BY updated_at DESC")) {
            while (r.next()) {
                Note n = new Note();
                n.setId(r.getInt("id"));
                n.setTitle(r.getString("title"));
                n.setContent(r.getString("content"));
                n.setCreatedAt(r.getString("created_at"));
                n.setUpdatedAt(r.getString("updated_at"));
                list.add(n);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public static void updateNote(Note n) {
        try (Connection c = getConnection();
             PreparedStatement p = c.prepareStatement(
                 "UPDATE notes SET title=?,content=?,updated_at=datetime('now','localtime') WHERE id=?")) {
            p.setString(1, n.getTitle());
            p.setString(2, n.getContent());
            p.setInt(3, n.getId());
            p.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static void deleteNote(int id) {
        try (Connection c = getConnection();
             PreparedStatement p = c.prepareStatement("DELETE FROM notes WHERE id=?")) {
            p.setInt(1, id);
            p.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // =================== EXPENSES ===================
    public static int addExpense(Expense e) {
        String sql = "INSERT INTO expenses(description,amount,date,category) VALUES(?,?,?,?)";
        try (Connection c = getConnection();
             PreparedStatement p = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            p.setString(1, e.getDescription());
            p.setDouble(2, e.getAmount());
            p.setString(3, e.getDate());
            p.setString(4, e.getCategory());
            p.executeUpdate();
            ResultSet rs = p.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException ex) { ex.printStackTrace(); }
        return -1;
    }

    public static List<Expense> getAllExpenses() {
        List<Expense> list = new ArrayList<>();
        try (Connection c = getConnection();
             Statement s = c.createStatement();
             ResultSet r = s.executeQuery(
                 "SELECT * FROM expenses ORDER BY date DESC")) {
            while (r.next()) {
                Expense e = new Expense();
                e.setId(r.getInt("id"));
                e.setDescription(r.getString("description"));
                e.setAmount(r.getDouble("amount"));
                e.setDate(r.getString("date"));
                e.setCategory(r.getString("category"));
                list.add(e);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public static void deleteExpense(int id) {
        try (Connection c = getConnection();
             PreparedStatement p = c.prepareStatement("DELETE FROM expenses WHERE id=?")) {
            p.setInt(1, id);
            p.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static double getTotalExpenses() {
        try (Connection c = getConnection();
             Statement s = c.createStatement();
             ResultSet r = s.executeQuery("SELECT COALESCE(SUM(amount),0) FROM expenses")) {
            if (r.next()) return r.getDouble(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    // =================== SCHEDULE EVENTS ===================
    public static int addEvent(ScheduleEvent ev) {
        String sql = "INSERT INTO schedule_events(title,start_time,end_time,color,date) VALUES(?,?,?,?,?)";
        try (Connection c = getConnection();
             PreparedStatement p = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            p.setString(1, ev.getTitle());
            p.setString(2, ev.getStartTime());
            p.setString(3, ev.getEndTime());
            p.setString(4, ev.getColor());
            p.setString(5, ev.getDate());
            p.executeUpdate();
            ResultSet rs = p.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return -1;
    }

    public static List<ScheduleEvent> getEventsForDate(String date) {
        List<ScheduleEvent> list = new ArrayList<>();
        try (Connection c = getConnection();
             PreparedStatement p = c.prepareStatement(
                 "SELECT * FROM schedule_events WHERE date=? ORDER BY start_time")) {
            p.setString(1, date);
            ResultSet r = p.executeQuery();
            while (r.next()) {
                ScheduleEvent e = new ScheduleEvent();
                e.setId(r.getInt("id"));
                e.setTitle(r.getString("title"));
                e.setStartTime(r.getString("start_time"));
                e.setEndTime(r.getString("end_time"));
                e.setColor(r.getString("color"));
                e.setDate(r.getString("date"));
                list.add(e);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public static void deleteEvent(int id) {
        try (Connection c = getConnection();
             PreparedStatement p = c.prepareStatement(
                 "DELETE FROM schedule_events WHERE id=?")) {
            p.setInt(1, id);
            p.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static int countEventsToday() {
        String today = java.time.LocalDate.now().toString();
        try (Connection c = getConnection();
             PreparedStatement p = c.prepareStatement(
                 "SELECT COUNT(*) FROM schedule_events WHERE date=?")) {
            p.setString(1, today);
            ResultSet r = p.executeQuery();
            if (r.next()) return r.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    // =================== HABITS ===================
    public static int addHabit(Habit h) {
        String sql = "INSERT INTO habits(name,completed_days) VALUES(?,?)";
        try (Connection c = getConnection();
             PreparedStatement p = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            p.setString(1, h.getName());
            p.setString(2, h.getCompletedDays());
            p.executeUpdate();
            ResultSet rs = p.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return -1;
    }

    public static List<Habit> getAllHabits() {
        List<Habit> list = new ArrayList<>();
        try (Connection c = getConnection();
             Statement s = c.createStatement();
             ResultSet r = s.executeQuery("SELECT * FROM habits ORDER BY created_at DESC")) {
            while (r.next()) {
                Habit h = new Habit();
                h.setId(r.getInt("id"));
                h.setName(r.getString("name"));
                h.setCompletedDays(r.getString("completed_days"));
                h.setCreatedAt(r.getString("created_at"));
                list.add(h);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public static void updateHabitDays(int id, String days) {
        try (Connection c = getConnection();
             PreparedStatement p = c.prepareStatement(
                 "UPDATE habits SET completed_days=? WHERE id=?")) {
            p.setString(1, days);
            p.setInt(2, id);
            p.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static void deleteHabit(int id) {
        try (Connection c = getConnection();
             PreparedStatement p = c.prepareStatement("DELETE FROM habits WHERE id=?")) {
            p.setInt(1, id);
            p.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // =================== SETTINGS ===================
    public static void saveSetting(String key, String value) {
        try (Connection c = getConnection();
             PreparedStatement p = c.prepareStatement(
                 "INSERT OR REPLACE INTO settings(key,value) VALUES(?,?)")) {
            p.setString(1, key);
            p.setString(2, value);
            p.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static String getSetting(String key) {
        try (Connection c = getConnection();
             PreparedStatement p = c.prepareStatement(
                 "SELECT value FROM settings WHERE key=?")) {
            p.setString(1, key);
            ResultSet r = p.executeQuery();
            if (r.next()) return r.getString(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // =================== MOODS ===================
    public static void logMood(String mood) {
        try (Connection c = getConnection();
             PreparedStatement p = c.prepareStatement(
                 "INSERT INTO moods(mood) VALUES(?)")) {
            p.setString(1, mood);
            p.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static String getLatestMood() {
        try (Connection c = getConnection();
             Statement s = c.createStatement();
             ResultSet r = s.executeQuery(
                 "SELECT mood FROM moods ORDER BY logged_at DESC LIMIT 1")) {
            if (r.next()) return r.getString(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // =================== STATS HELPERS ===================
    public static int countPendingTasks() {
        try (Connection c = getConnection();
             Statement s = c.createStatement();
             ResultSet r = s.executeQuery(
                 "SELECT COUNT(*) FROM tasks WHERE completed=0")) {
            if (r.next()) return r.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public static int countCompletedTasks() {
        try (Connection c = getConnection();
             Statement s = c.createStatement();
             ResultSet r = s.executeQuery(
                 "SELECT COUNT(*) FROM tasks WHERE completed=1")) {
            if (r.next()) return r.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public static int countOverdueTasks() {
        String today = java.time.LocalDate.now().toString();
        try (Connection c = getConnection();
             PreparedStatement p = c.prepareStatement(
                 "SELECT COUNT(*) FROM tasks WHERE completed=0 AND due_date < ? AND due_date IS NOT NULL AND due_date != ''")) {
            p.setString(1, today);
            ResultSet r = p.executeQuery();
            if (r.next()) return r.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public static int countHighPriorityTasks() {
        try (Connection c = getConnection();
             Statement s = c.createStatement();
             ResultSet r = s.executeQuery(
                 "SELECT COUNT(*) FROM tasks WHERE priority='High' AND completed=0")) {
            if (r.next()) return r.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    /** Wipe everything */
    public static void wipeAllData() {
        try (Connection c = getConnection(); Statement s = c.createStatement()) {
            s.execute("DELETE FROM tasks");
            s.execute("DELETE FROM notes");
            s.execute("DELETE FROM expenses");
            s.execute("DELETE FROM schedule_events");
            s.execute("DELETE FROM habits");
            s.execute("DELETE FROM settings");
            s.execute("DELETE FROM moods");
        } catch (SQLException e) { e.printStackTrace(); }
    }
}