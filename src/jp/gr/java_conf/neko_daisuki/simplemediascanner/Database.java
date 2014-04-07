package jp.gr.java_conf.neko_daisuki.simplemediascanner;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import android.util.SparseArray;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

public class Database {

    public static class Task extends Row {

        @SerializedName("path")
        private String mPath;

        private Task(int id, String path) {
            super(id);
            mPath = path;
        }

        public String getPath() {
            return mPath;
        }

        public String toString() {
            String fmt = "Task(id=%d, path=%s)";
            return String.format(Locale.ROOT, fmt, getId(), mPath);
        }
    }

    public static class Schedule extends Row {

        @SerializedName("hour")
        private int mHour;      // -1 indicates that this is hourly.

        @SerializedName("minute")
        private int mMinute;

        public Schedule(int id, int hour, int minute) {
            super(id);
            mHour = hour;
            mMinute = minute;
        }
    }

    public static class TaskSchedule extends Row {

        @SerializedName("task_id")
        private int mTaskId;

        @SerializedName("schedule_id")
        private int mScheduleId;

        public TaskSchedule(int id, int taskId, int scheduleId) {
            super(id);
            mTaskId = taskId;
            mScheduleId = scheduleId;
        }

        public String toString() {
            String fmt = "TaskSchedule(task_id=%d, schedule_id=%d)";
            return String.format(Locale.ROOT, fmt, mTaskId, mScheduleId);
        }
    }

    private static class Row {

        @SerializedName("id")
        private int mId;

        public Row(int id) {
            mId = id;
        }

        public final int getId() {
            return mId;
        }

        @Override
        public int hashCode() {
            return Integer.valueOf(mId).hashCode();
        }

        @Override
        public boolean equals(Object o) {
            Row row;
            try {
                row = getClass().cast(o);
            }
            catch (ClassCastException e) {
                return false;
            }
            return mId == row.getId();
        }
    }

    private static final Type TASK_COLLECTION_TYPE = new TypeToken<Collection<Task>>() {}.getType();
    private static final Type SCHEDULE_COLLECTION_TYPE = new TypeToken<Collection<Schedule>>() {}.getType();

    // documents
    private SparseArray<Task> mTasks;
    private SparseArray<Schedule> mSchedules;
    private List<TaskSchedule> mTaskSchedule;

    public void addTask(String path) {
        editTask(getNewId(mTasks), path);
    }

    public void editTask(int id, String path) {
        mTasks.put(id, new Task(id, path));
    }

    public Task getTask(int id) {
        return mTasks.get(id);
    }

    public Task[] getTasks() {
        int size = mTasks.size();
        Task[] tasks = new Task[size];
        for (int i = 0; i < size; i++) {
            tasks[i] = mTasks.valueAt(i);
        }
        return tasks;
    }

    public void removeTask(int id) {
        mTasks.remove(id);
    }

    public void addSchedule(int hour, int minute) {
        int id = getNewId(mSchedules);
        mSchedules.put(id, new Schedule(id, hour, minute));
    }

    public void read(File directory) throws IOException {
        Gson gson = makeGson();
        String path = directory.getAbsolutePath();
        mTasks = readTasks(gson, path);
        mSchedules = readSchedules(gson, path);
    }

    public void write(File directory) throws IOException {
        Gson gson = makeGson();
        String path = directory.getAbsolutePath();
        writeTasks(gson, path);
        writeSchedules(gson, path);
    }

    public void initializeEmptyDatabase() {
        mTasks = new SparseArray<Task>();
        mSchedules = new SparseArray<Schedule>();
        mTaskSchedule = new ArrayList<TaskSchedule>();
    }

    private Gson makeGson() {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        return builder.create();
    }

    private String getSchedulePath(String directory) {
        return String.format("%s/schedules.json", directory);
    }

    private String getTasksPath(String directory) {
        return String.format("%s/tasks.json", directory);
    }

    private <E extends Row> SparseArray<E> makeArray(Collection<E> c) {
        SparseArray<E> a = new SparseArray<E>();
        for (E e: c) {
            a.put(e.getId(), e);
        }
        return a;
    }

    private SparseArray<Schedule> readSchedules(Gson gson, String directory) throws IOException {
        Collection<Schedule> schedules;
        Reader reader = new FileReader(getSchedulePath(directory));
        try {
            schedules = gson.fromJson(reader, SCHEDULE_COLLECTION_TYPE);
        }
        finally {
            reader.close();
        }
        return makeArray(schedules);
    }

    private SparseArray<Task> readTasks(Gson gson, String directory) throws IOException {
        /*
         * Reading/writing SparseArrays with Gson is difficult for me. So I
         * converts arrays to usual collections.
         */
        Collection<Task> tasks;
        Reader reader = new FileReader(getTasksPath(directory));
        try {
            tasks = gson.fromJson(reader, TASK_COLLECTION_TYPE);
        }
        finally {
            reader.close();
        }
        return makeArray(tasks);
    }

    private void writeSchedules(Gson gson, String directory) throws IOException {
        Writer writer = new FileWriter(getSchedulePath(directory));
        try {
            gson.toJson(makeCollection(mSchedules), writer);
        }
        finally {
            writer.close();
        }
    }

    private void writeTasks(Gson gson, String directory) throws IOException {
        Writer writer = new FileWriter(getTasksPath(directory));
        try {
            gson.toJson(makeCollection(mTasks), writer);
        }
        finally {
            writer.close();
        }
    }

    private <E> Collection<E> makeCollection(SparseArray<E> a) {
        int size = a.size();
        Collection<E> set = new HashSet<E>(size);
        for (int i = 0; i < size; i++) {
            set.add(a.valueAt(i));
        }
        return set;
    }

    private int getNewId(SparseArray<?> a) {
        int size = a.size();
        return 0 < size ? a.keyAt(size - 1) + 1 : 0;
    }
}