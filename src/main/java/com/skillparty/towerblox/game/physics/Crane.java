package com.skillparty.towerblox.game.physics;

import com.skillparty.towerblox.utils.Constants;
import com.skillparty.towerblox.game.MovementRecorder;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.FontMetrics;

/**
 * Represents the crane that carries and drops blocks with realistic animation
 */
public class Crane {
    private double x, y;
    private double speed;
    private double baseSpeed;
    private int gameWidth;
    private boolean movingRight;
    
    // Tower Bloxx 2005 style crane movement
    private double centerX;
    private double swingRange;
    private double minX, maxX;
    
    // Current block being carried
    private Block currentBlock;
    
    // Crane animation
    private CraneAnimation craneAnimation;
    
    // Crane visual properties
    private int craneWidth;
    private int craneHeight;
    private int hookLength;
    
    // Movimiento vertical
    private double minY, maxY;
    private boolean manualControl = false; // Para controles manuales
    
    // Professional standard movement system
    private StandardCraneMovement standardMovement;
    private boolean useStandardMovement = true;
    
    // Movement recorder for custom patterns (removing duplicate)
    
    public Crane(double startX, double startY, int gameWidth) {
        this.gameWidth = gameWidth;
        this.baseSpeed = Constants.CRANE_BASE_SPEED; // Standard speed for normal difficulty
        this.speed = baseSpeed;
        this.movingRight = true;
        
        // Initialize standard Tower Bloxx movement - professional settings
        this.centerX = gameWidth / 2.0; // Center of screen
        this.swingRange = gameWidth * 0.25; // Standard 25% range for normal difficulty
        this.minX = centerX - swingRange;
        this.maxX = centerX + swingRange;
        
        // Start at center
        this.x = centerX;
        this.y = startY;
        
        // Configurar límites verticales
        this.minY = 30; // Altura mínima
        this.maxY = startY + 200; // Altura máxima inicial
        
        this.craneWidth = 60;
        this.craneHeight = Constants.CRANE_HEIGHT;
        this.hookLength = Constants.CRANE_HOOK_LENGTH;
        
        this.craneAnimation = new CraneAnimation();
        
        // Initialize professional standard movement system
        this.standardMovement = StandardCraneMovement.createClassicPattern(
            centerX, startY, gameWidth
        );
    }
    
    /**
     * Updates the crane position and animation
     */
    public void update(long deltaTime) {
        // Convert long to double for compatibility
        double dt = deltaTime / 1000.0; // Convert milliseconds to seconds
        
        craneAnimation.update(deltaTime);
        
        // Don't move crane if animation is in progress
        if (craneAnimation.isAnimating()) {
            return; // Don't move while animating
        }
        
        // Use professional standard movement or fallback to pendulum
        if (useStandardMovement && standardMovement != null) {
            updateStandardMovement(deltaTime);
        } else {
            // Torre Bloxx 2005 style smooth pendulum movement with acceleration/deceleration
            updatePendulumMovement(dt);
        }
        
        // Update current block position if carrying one
        if (currentBlock != null && !currentBlock.isDropped()) {
            currentBlock.setX(x - currentBlock.getWidth() / 2);
            currentBlock.setY(y + craneHeight + hookLength);
        }
    }
    
    /**
     * Sistema de movimiento desafiante con múltiples patrones dinámicos
     * El corazón del juego - timing y precisión son clave
     */
    private void updatePendulumMovement(double deltaTime) {
        // NUEVO: Verificar si hay un patrón grabado reproduciéndose
        if (movementRecorder != null && movementRecorder.isReplaying()) {
            MovementRecorder.Position replayPosition = movementRecorder.getReplayPosition(minX, maxX, minY, maxY);
            if (replayPosition != null) {
                // Usar posición del patrón grabado (X e Y)
                x = replayPosition.x;
                y = replayPosition.y;
                
                // Verificar si debe soltar el bloque según el patrón
                if (movementRecorder.shouldDropInReplay() && currentBlock != null && !currentBlock.isDropped()) {
                    dropBlock();
                }
                return; // Salir temprano, el patrón controla el movimiento
            }
        }
        
        // Movimiento automático normal si no hay patrón reproduciéndose
        // Calcular posición relativa en el rango de movimiento (0 a 1)
        double relativePos = (x - minX) / (maxX - minX);
        
        // NUEVO: Patrón de movimiento dinámico basado en la altura de la torre
        int towerHeight = getCurrentTowerHeight();
        MovementPattern pattern = getMovementPattern(towerHeight);
        
        // Calcular velocidad base con el patrón seleccionado
        double velocityMultiplier = calculateVelocityMultiplier(relativePos, pattern, towerHeight);
        
        // NUEVO: Micro-variaciones aleatorias para mayor desafío
        double randomVariation = 1.0 + (Math.random() - 0.5) * 0.15; // ±7.5% variación
        
        // NUEVO: Efecto de "viento" que afecta el movimiento
        double windEffect = calculateWindEffect(towerHeight);
        
        // Calcular movimiento final con todos los factores
        double movement = speed * velocityMultiplier * randomVariation * windEffect * (deltaTime / 16.0);
        
        // NUEVO: Aplicar aceleración/desaceleración no lineal
        movement *= calculateAccelerationCurve(relativePos, movingRight);
        
        if (movingRight) {
            x += movement;
            if (x >= maxX) {
                x = maxX;
                movingRight = false;
                // Pausa variable según el patrón
                craneAnimation.addPause(pattern.pauseDuration);
            }
        } else {
            x -= movement;
            if (x <= minX) {
                x = minX;
                movingRight = true;
                craneAnimation.addPause(pattern.pauseDuration);
            }
        }
    }
    
    /**
     * Patrones de movimiento que cambian según la altura
     */
    private enum MovementPattern {
        STEADY(1.0, 150),           // Movimiento constante (niveles 1-10)
        ACCELERATING(1.5, 100),     // Aceleración en el centro (niveles 11-25)
        ERRATIC(2.0, 50),           // Movimiento errático (niveles 26-50)
        PRECISION(0.8, 200),        // Movimiento lento y preciso (niveles 51-75)
        CHAOTIC(2.5, 25),           // Caótico y rápido (niveles 76-100)
        EXTREME(3.0, 10);           // Extremadamente desafiante (niveles 100+)
        
        final double speedMultiplier;
        final int pauseDuration;
        
        MovementPattern(double speedMultiplier, int pauseDuration) {
            this.speedMultiplier = speedMultiplier;
            this.pauseDuration = pauseDuration;
        }
    }
    
    /**
     * Determina el patrón de movimiento según la altura de la torre
     */
    private MovementPattern getMovementPattern(int towerHeight) {
        if (towerHeight <= 10) return MovementPattern.STEADY;
        if (towerHeight <= 25) return MovementPattern.ACCELERATING;
        if (towerHeight <= 50) return MovementPattern.ERRATIC;
        if (towerHeight <= 75) return MovementPattern.PRECISION;
        if (towerHeight <= 100) return MovementPattern.CHAOTIC;
        return MovementPattern.EXTREME;
    }
    
    /**
     * Calcula el multiplicador de velocidad según el patrón
     */
    private double calculateVelocityMultiplier(double relativePos, MovementPattern pattern, int towerHeight) {
        switch (pattern) {
            case STEADY:
                // Movimiento sinusoidal clásico
                return Math.sin(relativePos * Math.PI) * pattern.speedMultiplier;
                
            case ACCELERATING:
                // Aceleración exponencial en el centro
                double centerDistance = Math.abs(relativePos - 0.5) * 2;
                return Math.pow(1 - centerDistance, 2) * pattern.speedMultiplier;
                
            case ERRATIC:
                // Movimiento con picos aleatorios
                double base = Math.sin(relativePos * Math.PI);
                double spike = Math.sin(relativePos * Math.PI * 4) * 0.3;
                return (base + spike) * pattern.speedMultiplier;
                
            case PRECISION:
                // Movimiento muy controlado con micro-pausas
                double precision = Math.sin(relativePos * Math.PI);
                if (Math.abs(relativePos - 0.5) < 0.1) {
                    precision *= 0.3; // Muy lento en el centro
                }
                return precision * pattern.speedMultiplier;
                
            case CHAOTIC:
                // Múltiples frecuencias superpuestas
                double chaos = Math.sin(relativePos * Math.PI) * 0.6 +
                              Math.sin(relativePos * Math.PI * 3) * 0.3 +
                              Math.sin(relativePos * Math.PI * 7) * 0.1;
                return Math.abs(chaos) * pattern.speedMultiplier;
                
            case EXTREME:
                // Movimiento impredecible con cambios súbitos
                double extreme = Math.sin(relativePos * Math.PI * 2) * 
                               Math.cos(relativePos * Math.PI * 5) *
                               (1 + Math.sin(System.currentTimeMillis() * 0.01) * 0.5);
                return Math.abs(extreme) * pattern.speedMultiplier;
                
            default:
                return Math.sin(relativePos * Math.PI);
        }
    }
    
    /**
     * Calcula el efecto del "viento" que aumenta con la altura
     */
    private double calculateWindEffect(int towerHeight) {
        if (towerHeight < 30) return 1.0; // Sin viento en niveles bajos
        
        // El viento aumenta con la altura y tiene variación temporal
        double windStrength = Math.min((towerHeight - 30) * 0.02, 0.4); // Máximo 40% de efecto
        double windVariation = Math.sin(System.currentTimeMillis() * 0.003) * windStrength;
        
        return 1.0 + windVariation;
    }
    
    /**
     * Curva de aceleración no lineal para mayor desafío
     */
    private double calculateAccelerationCurve(double relativePos, boolean movingRight) {
        // Crear una curva asimétrica que favorece una dirección
        double asymmetry = movingRight ? 
            Math.pow(relativePos, 1.5) : 
            Math.pow(1 - relativePos, 1.5);
            
        return 0.5 + asymmetry * 0.5; // Rango de 0.5 a 1.0
    }
    
    /**
     * Professional standard movement using the StandardCraneMovement system
     */
    private void updateStandardMovement(long deltaTime) {
        // Check if movement recorder is replaying
        if (movementRecorder != null && movementRecorder.isReplaying()) {
            MovementRecorder.Position replayPosition = movementRecorder.getReplayPosition(minX, maxX, minY, maxY);
            if (replayPosition != null) {
                x = replayPosition.x;
                y = replayPosition.y;
                
                if (movementRecorder.shouldDropInReplay() && currentBlock != null && !currentBlock.isDropped()) {
                    dropBlock();
                }
                return;
            }
        }
        
        // Use standard movement pattern
        double[] newPosition = standardMovement.updatePosition(deltaTime);
        x = newPosition[0];
        
        // Keep Y position stable unless manual control is enabled
        if (manualControl) {
            y = newPosition[1];
        }
        
        // Ensure position stays within bounds
        if (x < minX) x = minX;
        if (x > maxX) x = maxX;
        if (y < minY) y = minY;
        if (y > maxY) y = maxY;
    }
    
    /**
     * Obtiene la altura actual de la torre (necesario para los cálculos)
     */
    private int getCurrentTowerHeight() {
        // Este método será llamado desde GameEngine para proporcionar la altura actual
        return currentTowerHeight;
    }
    
    // ===== PUBLIC METHODS FOR CONTROLLING CRANE MOVEMENT =====
    
    /**
     * Switches to professional standard movement (Tower Bloxx 2005 style)
     */
    public void enableStandardMovement() {
        this.useStandardMovement = true;
        if (standardMovement != null) {
            standardMovement.reset();
        }
        System.out.println("🏗️ Switched to Standard Movement (Tower Bloxx 2005 style)");
    }
    
    /**
     * Switches to custom pendulum movement
     */
    public void enableCustomMovement() {
        this.useStandardMovement = false;
        System.out.println("🏗️ Switched to Custom Pendulum Movement");
    }
    
    /**
     * Changes the standard movement pattern
     */
    public void setMovementPattern(StandardCraneMovement.MovementPattern pattern) {
        if (standardMovement != null) {
            standardMovement.setPattern(pattern);
            System.out.println("🏗️ Movement pattern changed to: " + pattern.name());
        }
    }
    
    /**
     * Adjusts the movement speed (1.0 = normal, 2.0 = double speed, etc.)
     */
    public void setMovementSpeed(double speedMultiplier) {
        if (standardMovement != null) {
            standardMovement.setAngularSpeed(standardMovement.getAngularSpeed() * speedMultiplier);
            System.out.println("🏗️ Movement speed adjusted to: " + speedMultiplier + "x");
        }
    }
    
    /**
     * Gets the current movement progress (0.0 to 1.0)
     */
    public double getMovementProgress() {
        if (useStandardMovement && standardMovement != null) {
            return standardMovement.getMovementProgress();
        }
        return (x - minX) / (maxX - minX);
    }
    
    /**
     * Checks if using standard movement
     */
    public boolean isUsingStandardMovement() {
        return useStandardMovement;
    }
    
    // Variable para almacenar la altura actual de la torre
    private int currentTowerHeight = 0;
    
    // Sistema de grabación de movimientos
    private MovementRecorder movementRecorder;
    
    /**
     * Sistema avanzado de rango dinámico que se adapta a la altura y dificultad
     */
    public void updateSwingRange(int towerHeight) {
        // Actualizar altura para los cálculos de movimiento
        this.currentTowerHeight = towerHeight;
        
        // Rango base más desafiante
        double baseRange = gameWidth * 0.3; // Aumentado a 30% para más desafío inicial
        
        // Reducción progresiva más agresiva
        double reductionFactor;
        if (towerHeight <= 20) {
            reductionFactor = towerHeight * 0.015; // Reducción gradual inicial
        } else if (towerHeight <= 50) {
            reductionFactor = 0.3 + (towerHeight - 20) * 0.02; // Reducción acelerada
        } else if (towerHeight <= 100) {
            reductionFactor = 0.9 + (towerHeight - 50) * 0.008; // Reducción extrema
        } else {
            reductionFactor = Math.min(1.3 + (towerHeight - 100) * 0.005, 0.95); // Máximo 95% reducción
        }
        
        this.swingRange = baseRange * (1.0 - Math.min(reductionFactor, 0.95));
        
        // NUEVO: Rango mínimo absoluto para mantener jugabilidad
        this.swingRange = Math.max(this.swingRange, gameWidth * 0.08); // Mínimo 8% del ancho
        
        this.minX = centerX - swingRange;
        this.maxX = centerX + swingRange;
        
        // Ajustar posición actual si está fuera de los nuevos límites
        if (x < minX) {
            x = minX;
            movingRight = true;
        } else if (x > maxX) {
            x = maxX;
            movingRight = false;
        }
        
        // NUEVO: Ajustar velocidad base según el rango para mantener el desafío
        double rangeRatio = swingRange / (gameWidth * 0.3);
        double speedAdjustment = 1.0 + (1.0 - rangeRatio) * 0.5; // Más rápido con rango menor
        this.speed = this.baseSpeed * speedAdjustment;
    }
    
    /**
     * Renders the crane with realistic claw animation
     */
    public void render(Graphics2D g2d) {
        // Save original stroke
        BasicStroke originalStroke = (BasicStroke) g2d.getStroke();
        
        // Set crane color
        g2d.setColor(new Color(169, 169, 169)); // Dark gray
        
        // Draw crane arm (horizontal beam)
        g2d.setStroke(new BasicStroke(8));
        g2d.drawLine(0, (int)y, gameWidth, (int)y);
        
        // Draw crane support (vertical beam at current position)
        g2d.setStroke(new BasicStroke(6));
        g2d.drawLine((int)x, (int)y, (int)x, (int)(y + craneHeight));
        
        // Draw hook cable
        g2d.setStroke(new BasicStroke(2));
        g2d.setColor(Color.BLACK);
        g2d.drawLine((int)x, (int)(y + craneHeight), (int)x, (int)(y + craneHeight + hookLength));
        
        // Draw claw with realistic animation
        craneAnimation.renderClaw(g2d, x, y + craneHeight + hookLength);
        
        // Render current block if it exists and hasn't been dropped
        if (currentBlock != null && !currentBlock.isDropped()) {
            currentBlock.render(g2d);
        }
        
        // Restore original stroke
        g2d.setStroke(originalStroke);
        
        // Draw crane position indicator
        renderPositionIndicator(g2d);
    }
    

    
    /**
     * Renderiza indicadores avanzados de posición y desafío
     */
    private void renderPositionIndicator(Graphics2D g2d) {
        // Flecha de dirección más visible
        g2d.setColor(Color.YELLOW);
        g2d.setStroke(new BasicStroke(3));
        int arrowY = (int)y - 20;
        int arrowSize = 12;
        
        if (movingRight) {
            // Flecha derecha más prominente
            g2d.drawLine((int)x, arrowY, (int)x + arrowSize, arrowY);
            g2d.drawLine((int)x + arrowSize, arrowY, (int)x + arrowSize - 4, arrowY - 4);
            g2d.drawLine((int)x + arrowSize, arrowY, (int)x + arrowSize - 4, arrowY + 4);
        } else {
            // Flecha izquierda más prominente
            g2d.drawLine((int)x, arrowY, (int)x - arrowSize, arrowY);
            g2d.drawLine((int)x - arrowSize, arrowY, (int)x - arrowSize + 4, arrowY - 4);
            g2d.drawLine((int)x - arrowSize, arrowY, (int)x - arrowSize + 4, arrowY + 4);
        }
        
        // NUEVO: Indicador de zona de precisión
        renderPrecisionZone(g2d);
        
        // NUEVO: Indicador de patrón de movimiento
        renderMovementPattern(g2d, arrowY - 25);
        
        // NUEVO: Medidor de velocidad visual
        renderSpeedMeter(g2d, arrowY - 40);
    }
    
    /**
     * Renderiza la zona de precisión óptima
     */
    private void renderPrecisionZone(Graphics2D g2d) {
        // Calcular zona de precisión (centro del rango)
        double precisionZoneSize = swingRange * 0.2; // 20% del rango total
        double precisionLeft = centerX - precisionZoneSize / 2;
        double precisionRight = centerX + precisionZoneSize / 2;
        
        // Renderizar zona de precisión
        g2d.setColor(new Color(0, 255, 0, 50)); // Verde semi-transparente
        g2d.fillRect((int)precisionLeft, (int)y - 10, (int)precisionZoneSize, 5);
        
        // Borde de la zona de precisión
        g2d.setColor(Color.GREEN);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRect((int)precisionLeft, (int)y - 10, (int)precisionZoneSize, 5);
        
        // Indicador si estamos en la zona de precisión
        if (x >= precisionLeft && x <= precisionRight) {
            g2d.setColor(Color.GREEN);
            g2d.fillOval((int)x - 3, (int)y - 8, 6, 6);
        }
    }
    
    /**
     * Renderiza el indicador del patrón de movimiento actual
     */
    private void renderMovementPattern(Graphics2D g2d, int y) {
        MovementPattern pattern = getMovementPattern(currentTowerHeight);
        
        // Color según el patrón
        Color patternColor;
        switch (pattern) {
            case STEADY: patternColor = Color.GREEN; break;
            case ACCELERATING: patternColor = Color.YELLOW; break;
            case ERRATIC: patternColor = Color.ORANGE; break;
            case PRECISION: patternColor = Color.CYAN; break;
            case CHAOTIC: patternColor = Color.RED; break;
            case EXTREME: patternColor = Color.MAGENTA; break;
            default: patternColor = Color.WHITE;
        }
        
        g2d.setColor(patternColor);
        g2d.setFont(new Font("Arial", Font.BOLD, 10));
        String patternText = pattern.name();
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(patternText, (int)x - fm.stringWidth(patternText)/2, y);
    }
    
    /**
     * Renderiza un medidor visual de velocidad
     */
    private void renderSpeedMeter(Graphics2D g2d, int y) {
        double speedRatio = speed / (baseSpeed * 3.0); // Normalizar a rango 0-1
        speedRatio = Math.min(speedRatio, 1.0);
        
        // Barra de fondo
        g2d.setColor(Color.DARK_GRAY);
        g2d.fillRect((int)x - 20, y, 40, 4);
        
        // Barra de velocidad
        Color speedColor = speedRatio < 0.5 ? Color.GREEN : 
                          speedRatio < 0.8 ? Color.YELLOW : Color.RED;
        g2d.setColor(speedColor);
        g2d.fillRect((int)x - 20, y, (int)(40 * speedRatio), 4);
        
        // Borde
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRect((int)x - 20, y, 40, 4);
    }
    
    /**
     * Drops the current block and triggers claw animation
     */
    public void dropBlock() {
        if (currentBlock == null || currentBlock.isDropped() || craneAnimation.isAnimating()) {
            return;
        }
        
        // Start claw opening animation
        craneAnimation.startReleaseAnimation();
        
        // Drop the block
        currentBlock.drop();
    }
    

    
    /**
     * Sets the current block being carried
     */
    public void setCurrentBlock(Block block) {
        this.currentBlock = block;
        if (block != null) {
            // Position block at crane location
            block.setX(x - block.getWidth() / 2);
            block.setY(y + craneHeight + hookLength);
            
            // Start grab animation when picking up a new block
            craneAnimation.startGrabAnimation();
        }
    }
    
    /**
     * Resets the crane to initial state
     */
    public void reset() {
        this.x = gameWidth / 2.0;
        this.speed = baseSpeed;
        this.movingRight = true;
        this.currentBlock = null;
        this.craneAnimation.reset();
    }
    
    /**
     * Sets the crane speed (used for difficulty adjustment)
     */
    public void setSpeed(double speed) {
        this.speed = Math.max(0.5, Math.min(10.0, speed)); // Clamp between 0.5 and 10
    }
    
    /**
     * Gets crane status information
     */
    public String getStatus() {
        String blockStatus = currentBlock != null ? 
            (currentBlock.isDropped() ? "Dropped" : "Carrying") : "No Block";
        String animationStatus = craneAnimation.getCurrentState().toString();
        
        return String.format("Pos: %.0f | Speed: %.1f | Block: %s | Animation: %s", 
                           x, speed, blockStatus, animationStatus);
    }
    
    /**
     * Checks if crane is ready to accept a new block
     */
    public boolean isReadyForNewBlock() {
        return currentBlock == null || currentBlock.isDropped();
    }
    
    /**
     * Gets the drop zone bounds (where blocks will land)
     */
    public double getDropZoneLeft() {
        return x - (currentBlock != null ? currentBlock.getWidth() / 2 : 25);
    }
    
    public double getDropZoneRight() {
        return x + (currentBlock != null ? currentBlock.getWidth() / 2 : 25);
    }
    
    // Getters
    public double getX() { return x; }
    public double getY() { return y; }
    public double getSpeed() { return speed; }
    public double getBaseSpeed() { return baseSpeed; }
    public double getSwingRange() { return swingRange; }
    public Block getCurrentBlock() { return currentBlock; }
    public boolean isMovingRight() { return movingRight; }
    public boolean isAnimating() { return craneAnimation.isAnimating(); }
    public int getAnimationFrame() { return craneAnimation.getCurrentFrame(); }
    public boolean isOpening() { return craneAnimation.isOpening(); }
    public boolean isClosing() { return craneAnimation.isClosing(); }
    
    // Setters
    public void setX(double x) { 
        this.x = Math.max(craneWidth/2, Math.min(gameWidth - craneWidth/2, x)); 
    }
    
    public void setY(double y) {
        // FIXED: Allow crane to go much higher for very tall towers
        // Remove restrictive limit that was causing distance reduction at high levels
        this.y = Math.max(-2000, y); // Allow crane to go much higher to maintain proper distances
    }
    
    public void setMovingRight(boolean movingRight) { 
        this.movingRight = movingRight; 
    }
    
    public void setMovementRecorder(MovementRecorder recorder) {
        this.movementRecorder = recorder;
    }
    
    /**
     * Habilita/deshabilita el control manual de la grúa
     */
    public void setManualControl(boolean manual) {
        this.manualControl = manual;
    }
    
    /**
     * Mueve la grúa manualmente hacia arriba
     */
    public void moveUp(double amount) {
        if (manualControl) {
            y = Math.max(minY, y - amount);
        }
    }
    
    /**
     * Mueve la grúa manualmente hacia abajo
     */
    public void moveDown(double amount) {
        if (manualControl) {
            y = Math.min(maxY, y + amount);
        }
    }
    
    /**
     * Mueve la grúa manualmente hacia la izquierda
     */
    public void moveLeft(double amount) {
        if (manualControl) {
            x = Math.max(minX, x - amount);
        }
    }
    
    /**
     * Mueve la grúa manualmente hacia la derecha
     */
    public void moveRight(double amount) {
        if (manualControl) {
            x = Math.min(maxX, x + amount);
        }
    }
}