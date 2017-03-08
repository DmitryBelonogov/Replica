package com.nougust3.replica.Model.Database;

import android.util.Log;

import com.nougust3.replica.Model.Note;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

class UpgradeProcessor {

    private final static String METHODS_PREFIX = "onUpgradeTo";

    private static UpgradeProcessor instance;

    private UpgradeProcessor() { }

    private static UpgradeProcessor getInstance() {
        if (instance == null) {
            instance = new UpgradeProcessor();
        }
        return instance;
    }

    static void process(int oldVersion, int newVersion) {
        try {
            List<Method> methodsToLaunch = getInstance().getMethodsToLaunch(oldVersion, newVersion);
            for (Method methodToLaunch : methodsToLaunch) {
                methodToLaunch.invoke(getInstance());
            }
        } catch (SecurityException | IllegalAccessException | InvocationTargetException e) {
            Log.d("Replica", "Explosion processing upgrade!", e);
        }
    }

    private List<Method> getMethodsToLaunch(int oldVersion, int newVersion) {
        List<Method> methodsToLaunch = new ArrayList<>();
        Method[] declaredMethods = getInstance().getClass().getDeclaredMethods();
        for (Method declaredMethod : declaredMethods) {
            if (declaredMethod.getName().contains(METHODS_PREFIX)) {
                int methodVersionPostfix = Integer.parseInt(declaredMethod.getName().replace(METHODS_PREFIX, ""));
                if (oldVersion <= methodVersionPostfix && methodVersionPostfix <= newVersion) {
                    methodsToLaunch.add(declaredMethod);
                }
            }
        }
        return methodsToLaunch;
    }

    private void onUpgradeTo102() {
        for (Note note : DBHelper.getInstance().getAllNotes()) {
            note.setScrollPosition(0);
        }
    }
}
