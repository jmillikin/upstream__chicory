package com.dylibso.chicory.maven.wast;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.File;
import java.util.Set;

public class Wast {

    @JsonProperty("source_filename")
    private File sourceFilename;

    @JsonProperty("commands")
    private Command[] commands;

    public Wast() {}

    public File getSourceFilename() {
        return sourceFilename;
    }

    public Command[] getCommands() {
        return commands;
    }
}