package com.arhiser.increment.tools;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.util.Date;

/**
 * Created by arhis on 22.06.2016.
 */
public class CustomLogger implements Thread.UncaughtExceptionHandler {

    private static CustomLogger instance;

    private Context context;

    Thread.UncaughtExceptionHandler defaultExceptionHandler;

    public static void initInstance(Context context) {
        instance = new CustomLogger(context);
        instance.writeString("logger", "logger initialized.");
    }

    public static CustomLogger getInstance() {
        return instance;
    }

    public CustomLogger(Context context) {
        this.context = context;
        defaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        File file = new File(getLogFilePath());
        try {
            PrintStream ps = null;
            ps = new PrintStream(new FileOutputStream(file, true));
            ex.printStackTrace(ps);
            ps.append('\n');
            ps.flush();
            ps.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        defaultExceptionHandler.uncaughtException(thread, ex);
    }

    public String getLogFilePath() {
        File externalFiles = context.getExternalFilesDir(null);
        if (externalFiles != null) {
            return externalFiles.getAbsolutePath() + "/log.txt";
        } else {
            return context.getFilesDir() + "/log.txt";
        }
    }

    public void writeString(String tag, String string) {
        string = string.replaceAll("\n\r", "");
        Log.i(tag, string);
        try {
            File file = new File(getLogFilePath());
            if (!file.exists()) {
                file.createNewFile();
            }

            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(file, true));
            outputStreamWriter.write(new Date().toString() + ": " + tag + ": " + string + '\n');
            outputStreamWriter.flush();
            outputStreamWriter.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
