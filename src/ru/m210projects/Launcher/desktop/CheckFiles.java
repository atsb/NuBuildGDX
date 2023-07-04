package ru.m210projects.Launcher.desktop;

public class CheckFiles {
    private boolean value;
    private String label;

    public CheckFiles(boolean value, String label) {
        this.value = value;
        this.label = label;
    }

    public boolean getValue() {
        return this.value;
    }

    public String getLabel() {
        return this.label;
    }
}