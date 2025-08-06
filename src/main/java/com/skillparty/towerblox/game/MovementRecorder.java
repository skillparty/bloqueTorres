package com.skillparty.towerblox.game;

import java.util.ArrayList;
import java.util.List;
import java.io.*;

/**
 * Sistema de grabación y reproducción de movimientos de la grúa
 * Permite grabar patrones personalizados que se reproducen automáticamente
 */
public class MovementRecorder {
    
    /**
     * Representa un movimiento grabado de la grúa
     */
    public static class MovementFrame {
        public final long timestamp;
        public final double positionX;     // Posición X de la grúa (0.0 a 1.0)
        public final double positionY;     // Posición Y de la grúa (0.0 a 1.0)
        public final boolean isDropping;   // Si se está soltando el bloque
        public final double speed;         // Velocidad en este frame
        
        public MovementFrame(long timestamp, double positionX, double positionY, boolean isDropping, double speed) {
            this.timestamp = timestamp;
            this.positionX = positionX;
            this.positionY = positionY;
            this.isDropping = isDropping;
            this.speed = speed;
        }
        
        @Override
        public String toString() {
            return String.format("Frame[t=%d, x=%.3f, y=%.3f, drop=%b, speed=%.2f]", 
                               timestamp, positionX, positionY, isDropping, speed);
        }
    }
    
    /**
     * Patrón de movimiento completo
     */
    public static class MovementPattern {
        public final String name;
        public final List<MovementFrame> frames;
        public final long totalDuration;
        public final long createdTime;
        
        public MovementPattern(String name, List<MovementFrame> frames) {
            this.name = name;
            this.frames = new ArrayList<>(frames);
            this.totalDuration = frames.isEmpty() ? 0 : 
                               frames.get(frames.size() - 1).timestamp - frames.get(0).timestamp;
            this.createdTime = System.currentTimeMillis();
        }
        
        public boolean isEmpty() {
            return frames.isEmpty();
        }
        
        public int getFrameCount() {
            return frames.size();
        }
        
        @Override
        public String toString() {
            return String.format("Pattern[%s: %d frames, %.1fs]", 
                               name, frames.size(), totalDuration / 1000.0);
        }
    }
    
    // Estado de grabación
    private boolean isRecording = false;
    private boolean isReplaying = false;
    private List<MovementFrame> currentRecording;
    private long recordingStartTime;
    private String currentPatternName;
    
    // Patrones guardados
    private List<MovementPattern> savedPatterns;
    private int currentPatternIndex = 0;
    
    // Estado de reproducción
    private MovementPattern currentReplayPattern;
    private int currentReplayFrame = 0;
    private long replayStartTime;
    
    // Archivo de persistencia
    private static final String PATTERNS_FILE = "movement_patterns.dat";
    
    public MovementRecorder() {
        this.savedPatterns = new ArrayList<>();
        this.currentRecording = new ArrayList<>();
        loadPatternsFromFile();
    }
    
    /**
     * Inicia la grabación de un nuevo patrón
     */
    public void startRecording(String patternName) {
        if (isRecording) {
            stopRecording();
        }
        
        this.isRecording = true;
        this.currentPatternName = patternName;
        this.currentRecording.clear();
        this.recordingStartTime = System.currentTimeMillis();
        
        System.out.println("🔴 Iniciando grabación: " + patternName);
    }
    
    /**
     * Detiene la grabación y guarda el patrón
     */
    public MovementPattern stopRecording() {
        if (!isRecording) {
            return null;
        }
        
        this.isRecording = false;
        
        if (currentRecording.isEmpty()) {
            System.out.println("⚠️ Grabación vacía, no se guardó");
            return null;
        }
        
        // Crear el patrón
        MovementPattern pattern = new MovementPattern(currentPatternName, currentRecording);
        
        // Guardar en la lista
        savedPatterns.add(pattern);
        
        // Persistir a archivo
        savePatternsToFile();
        
        System.out.println("✅ Patrón grabado: " + pattern);
        return pattern;
    }
    
    /**
     * Graba un frame de movimiento con posición X e Y
     */
    public void recordFrame(double craneX, double craneY, double minX, double maxX, double minY, double maxY, boolean isDropping, double speed) {
        if (!isRecording) {
            return;
        }
        
        // Normalizar posiciones a 0.0-1.0
        double normalizedX = (craneX - minX) / (maxX - minX);
        normalizedX = Math.max(0.0, Math.min(1.0, normalizedX));
        
        double normalizedY = (craneY - minY) / (maxY - minY);
        normalizedY = Math.max(0.0, Math.min(1.0, normalizedY));
        
        long timestamp = System.currentTimeMillis() - recordingStartTime;
        
        MovementFrame frame = new MovementFrame(timestamp, normalizedX, normalizedY, isDropping, speed);
        currentRecording.add(frame);
    }
    
    /**
     * Inicia la reproducción de un patrón
     */
    public void startReplay(MovementPattern pattern) {
        if (pattern == null || pattern.isEmpty()) {
            return;
        }
        
        this.isReplaying = true;
        this.currentReplayPattern = pattern;
        this.currentReplayFrame = 0;
        this.replayStartTime = System.currentTimeMillis();
        
        System.out.println("▶️ Reproduciendo: " + pattern.name);
    }
    
    /**
     * Inicia la reproducción del siguiente patrón guardado
     */
    public void startNextReplay() {
        if (savedPatterns.isEmpty()) {
            return;
        }
        
        currentPatternIndex = (currentPatternIndex + 1) % savedPatterns.size();
        MovementPattern pattern = savedPatterns.get(currentPatternIndex);
        startReplay(pattern);
    }
    
    /**
     * Detiene la reproducción actual
     */
    public void stopReplay() {
        this.isReplaying = false;
        this.currentReplayPattern = null;
        this.currentReplayFrame = 0;
    }
    
    /**
     * Clase para devolver posición X e Y
     */
    public static class Position {
        public final double x, y;
        
        public Position(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }
    
    /**
     * Obtiene la posición de la grúa para el frame actual de reproducción
     */
    public Position getReplayPosition(double minX, double maxX, double minY, double maxY) {
        if (!isReplaying || currentReplayPattern == null) {
            return null;
        }
        
        long elapsed = System.currentTimeMillis() - replayStartTime;
        
        // Buscar el frame apropiado
        while (currentReplayFrame < currentReplayPattern.frames.size()) {
            MovementFrame frame = currentReplayPattern.frames.get(currentReplayFrame);
            
            if (frame.timestamp <= elapsed) {
                // Convertir posiciones normalizadas a coordenadas reales
                double realX = minX + frame.positionX * (maxX - minX);
                double realY = minY + frame.positionY * (maxY - minY);
                
                // Avanzar al siguiente frame si es necesario
                if (currentReplayFrame < currentReplayPattern.frames.size() - 1) {
                    MovementFrame nextFrame = currentReplayPattern.frames.get(currentReplayFrame + 1);
                    if (nextFrame.timestamp <= elapsed) {
                        currentReplayFrame++;
                    }
                }
                
                return new Position(realX, realY);
            } else {
                break;
            }
        }
        
        // Si llegamos al final, detener reproducción
        if (currentReplayFrame >= currentReplayPattern.frames.size()) {
            stopReplay();
            return null;
        }
        
        return null;
    }
    
    /**
     * Verifica si debe soltar el bloque en el frame actual
     */
    public boolean shouldDropInReplay() {
        if (!isReplaying || currentReplayPattern == null || currentReplayFrame >= currentReplayPattern.frames.size()) {
            return false;
        }
        
        MovementFrame frame = currentReplayPattern.frames.get(currentReplayFrame);
        return frame.isDropping;
    }
    
    /**
     * Elimina un patrón guardado
     */
    public boolean deletePattern(int index) {
        if (index >= 0 && index < savedPatterns.size()) {
            MovementPattern removed = savedPatterns.remove(index);
            savePatternsToFile();
            System.out.println("🗑️ Patrón eliminado: " + removed.name);
            return true;
        }
        return false;
    }
    
    /**
     * Elimina un patrón por nombre
     */
    public boolean deletePattern(String name) {
        for (int i = 0; i < savedPatterns.size(); i++) {
            if (savedPatterns.get(i).name.equals(name)) {
                return deletePattern(i);
            }
        }
        return false;
    }
    
    /**
     * Guarda los patrones a archivo
     */
    private void savePatternsToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(PATTERNS_FILE))) {
            oos.writeInt(savedPatterns.size());
            
            for (MovementPattern pattern : savedPatterns) {
                oos.writeUTF(pattern.name);
                oos.writeInt(pattern.frames.size());
                
                for (MovementFrame frame : pattern.frames) {
                    oos.writeLong(frame.timestamp);
                    oos.writeDouble(frame.positionX);
                    oos.writeDouble(frame.positionY);
                    oos.writeBoolean(frame.isDropping);
                    oos.writeDouble(frame.speed);
                }
            }
            
            System.out.println("💾 Patrones guardados: " + savedPatterns.size());
        } catch (IOException e) {
            System.err.println("Error guardando patrones: " + e.getMessage());
        }
    }
    
    /**
     * Carga los patrones desde archivo
     */
    private void loadPatternsFromFile() {
        File file = new File(PATTERNS_FILE);
        if (!file.exists()) {
            return;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(PATTERNS_FILE))) {
            int patternCount = ois.readInt();
            
            for (int i = 0; i < patternCount; i++) {
                String name = ois.readUTF();
                int frameCount = ois.readInt();
                
                List<MovementFrame> frames = new ArrayList<>();
                for (int j = 0; j < frameCount; j++) {
                    long timestamp = ois.readLong();
                    double positionX = ois.readDouble();
                    double positionY = ois.readDouble();
                    boolean isDropping = ois.readBoolean();
                    double speed = ois.readDouble();
                    
                    frames.add(new MovementFrame(timestamp, positionX, positionY, isDropping, speed));
                }
                
                savedPatterns.add(new MovementPattern(name, frames));
            }
            
            System.out.println("📁 Patrones cargados: " + savedPatterns.size());
        } catch (IOException e) {
            System.err.println("Error cargando patrones: " + e.getMessage());
        }
    }
    
    // Getters
    public boolean isRecording() { return isRecording; }
    public boolean isReplaying() { return isReplaying; }
    public List<MovementPattern> getSavedPatterns() { return new ArrayList<>(savedPatterns); }
    public String getCurrentRecordingName() { return currentPatternName; }
    public MovementPattern getCurrentReplayPattern() { return currentReplayPattern; }
    public int getRecordingFrameCount() { return currentRecording.size(); }
    public long getRecordingDuration() { 
        return isRecording ? System.currentTimeMillis() - recordingStartTime : 0; 
    }
    
    /**
     * Obtiene información del estado actual
     */
    public String getStatusInfo() {
        if (isRecording) {
            return String.format("🔴 Grabando: %s (%d frames, %.1fs)", 
                               currentPatternName, currentRecording.size(), 
                               getRecordingDuration() / 1000.0);
        } else if (isReplaying && currentReplayPattern != null) {
            long elapsed = System.currentTimeMillis() - replayStartTime;
            return String.format("▶️ Reproduciendo: %s (%.1fs/%.1fs)", 
                               currentReplayPattern.name, elapsed / 1000.0, 
                               currentReplayPattern.totalDuration / 1000.0);
        } else {
            return String.format("⏹️ Inactivo (%d patrones guardados)", savedPatterns.size());
        }
    }
}