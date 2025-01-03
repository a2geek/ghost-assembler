package a2geek.asm.service;

import a2geek.asm.definition.CpuDefinition;
import a2geek.asm.io.AssemblerByteArrayOutputStream;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tracks current state of the assembly process.
 * This is setup as a ThreadLocal so all components have access to the state
 * without passing the state object or pieces of the state object around.
 * 
 * @author Rob
 */
public class AssemblerState {
	private static final ThreadLocal<AssemblerState> current = new ThreadLocal<>();

    /**
	 * Get the current AssemblerState.
	 */
	public static AssemblerState get() {
		return current.get();
	}
	/**
	 * Initialize this state with the file to be processed.
	 * Fully resets the AssemblerService.
	 */
	public static void init(File file) throws IOException {
		current.set(new AssemblerState());
		get().file = file;
		get().reset();
		get().localVars.add(null);
	}
	/** 
	 * Initialize this state with the source code to be processed.
	 * Fully resets the AssemblerService.
	 */
	public static void init(String source) throws IOException {
		current.set(new AssemblerState());
		get().source = source;
		get().reset();
		get().localVars.add(null);
	}
	/**
	 * Remove the current AssemblerState.
	 */
	public static void remove() {
		current.remove();
	}
	/**
	 * Reset the current AssemblerState without losing variables.
	 */
	public void reset() throws IOException {
		cpu = null;
		if (reader != null) reader.close();
		if (output != null) output.reset();
		// Leave variables alone!
		pc = 0;
		localScope = 0;
		// Setup reader
		reader = null;
		if (file != null) {
			reader = new LineNumberReader(new FileReader(file));
		} else if (source != null) {
			reader = new LineNumberReader(new StringReader(source));
		}
		// Set lines to be active by default
		active = true;
		// Back to default state of not identifying labels
		identifyLabels = false;
	}

	private File file = null;
	private String source = null;
	private CpuDefinition cpu = null;
	private LineNumberReader reader = null;
	private AssemblerByteArrayOutputStream output = new AssemblerByteArrayOutputStream();
	private Map<String,Long> globalVars = new HashMap<>();
	private List<Map<String,Long>> localVars = new ArrayList<>();
	private int localScope = 0;
	private long pc = 0;
	private boolean active = true;
	private boolean identifyLabels = false;

	public boolean containsGlobalVariable(String name) {
		return globalVars.containsKey(name);
	}
	public void addGlobalVariable_PC(String name) {
		addGlobalVariable(name, pc);
	}
	public void addGlobalVariable(String name, Long value) {
		globalVars.put(name, value);
	}
	
	public boolean containsLocalVariable(String name) {
		if (localVars.get(localScope) == null) {
			return false;
		} else {
			return localVars.get(localScope).containsKey(name);
		}
	}
	public void addLocalVariable_PC(String name) {
		addLocalVariable(name, pc);
	}
	public void addLocalVariable(String name, Long value) {
		if (localVars.get(localScope) == null) {
			localVars.set(localScope, new HashMap<>());
		}
		localVars.get(localScope).put(name, value);
	}
	public Map<String,Long> getVariables() {
        Map<String, Long> vars = new HashMap<>(globalVars);
		if (localVars.get(localScope) != null) vars.putAll(localVars.get(localScope));
		return vars;
	}
	public void nextLocalScope() {
		localScope++;
		if (localVars.size() <= localScope) {
			localVars.add(null);
		}
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public boolean isActive() {
		return active;
	}
	public void setIdentifyLabels(boolean identifyLabels) {
		this.identifyLabels = identifyLabels;
	}
	public boolean isIdentifyLabels() {
		return this.identifyLabels;
	}
	
	public void incrementPC() {
		pc++;
	}
	public void incrementPC(long value) {
		pc+= value;
	}
	public void setPC(long value) {
		pc = value;
	}
	public long getPC() {
		return pc;
	}
	
	public LineNumberReader getReader() {
		return reader;
	}
	public AssemblerByteArrayOutputStream getOutput() {
		return output;
	}
	
	public void setCpuDefinition(CpuDefinition cpuDefinition) {
		this.cpu = cpuDefinition;
	}
	public CpuDefinition getCpuDefinition() {
		return cpu;
	}
}
