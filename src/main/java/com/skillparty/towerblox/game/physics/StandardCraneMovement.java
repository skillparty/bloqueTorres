package com.skillparty.towerblox.game.physics;

/**
 * Sistema de movimiento auténtico de la grúa del Tower Bloxx 2005
 * Replica fielmente el comportamiento pendular del juego original
 */
public class StandardCraneMovement {
    
    // Patrones de movimiento del Tower Bloxx original
    public enum MovementPattern {
        CLASSIC_PENDULUM,    // Movimiento pendular clásico (niveles 1-10)
        ACCELERATED_SWING,   // Aceleración en extremos (niveles 11-25)
        VARIABLE_SPEED,      // Velocidad variable (niveles 26-50)
        PRECISION_MODE,      // Movimiento de precisión (niveles 51-75)
        EXPERT_CHALLENGE,    // Desafío experto (niveles 76-100)
        MASTER_LEVEL        // Nivel maestro (100+)
    }
    
    // Configuración del movimiento
    private double centerX;
    private double centerY;
    private double swingRadius;
    private double currentAngle;
    private double angularSpeed;
    private MovementPattern currentPattern;
    
    // Estado del movimiento
    private boolean movingClockwise;
    private long lastUpdateTime;
    private double baseAngularSpeed;
    
    // Parámetros del Tower Bloxx 2005
    private static final double BASE_ANGULAR_SPEED = 0.02; // Velocidad base del péndulo
    private static final double SPEED_VARIATION = 0.3;     // Variación de velocidad
    private static final double ACCELERATION_FACTOR = 1.5; // Factor de aceleración
    
    /**
     * Constructor privado - usar métodos factory
     */
    private StandardCraneMovement(double centerX, double centerY, double swingRadius) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.swingRadius = swingRadius;
        this.currentAngle = 0; // Empezar en el centro
        this.baseAngularSpeed = BASE_ANGULAR_SPEED;
        this.angularSpeed = baseAngularSpeed;
        this.currentPattern = MovementPattern.CLASSIC_PENDULUM;
        this.movingClockwise = true;
        this.lastUpdateTime = System.currentTimeMillis();
    }
    
    /**
     * Crea el patrón clásico del Tower Bloxx 2005
     */
    public static StandardCraneMovement createClassicPattern(double centerX, double centerY, int gameWidth) {
        double radius = gameWidth * 0.25; // 25% del ancho de pantalla
        return new StandardCraneMovement(centerX, centerY, radius);
    }
    
    /**
     * Crea un patrón personalizado
     */
    public static StandardCraneMovement createCustomPattern(double centerX, double centerY, 
                                                           double radius, MovementPattern pattern) {
        StandardCraneMovement movement = new StandardCraneMovement(centerX, centerY, radius);
        movement.setPattern(pattern);
        return movement;
    }
    
    /**
     * Actualiza la posición del péndulo
     */
    public double[] updatePosition(long deltaTime) {
        long currentTime = System.currentTimeMillis();
        double dt = (currentTime - lastUpdateTime) / 1000.0; // Convertir a segundos
        lastUpdateTime = currentTime;
        
        // Calcular velocidad angular según el patrón
        double speedMultiplier = calculateSpeedMultiplier();
        double currentSpeed = baseAngularSpeed * speedMultiplier;
        
        // Actualizar ángulo
        if (movingClockwise) {
            currentAngle += currentSpeed * dt;
            if (currentAngle >= Math.PI) {
                currentAngle = Math.PI;
                movingClockwise = false;
            }
        } else {
            currentAngle -= currentSpeed * dt;
            if (currentAngle <= 0) {
                currentAngle = 0;
                movingClockwise = true;
            }
        }
        
        // Calcular posición X e Y
        double x = centerX + Math.sin(currentAngle) * swingRadius;
        double y = centerY; // Y permanece constante en el movimiento básico
        
        return new double[]{x, y};
    }
    
    /**
     * Calcula el multiplicador de velocidad según el patrón actual
     */
    private double calculateSpeedMultiplier() {
        switch (currentPattern) {
            case CLASSIC_PENDULUM:
                // Movimiento sinusoidal clásico - más lento en extremos
                return 1.0 + Math.cos(currentAngle) * SPEED_VARIATION;
                
            case ACCELERATED_SWING:
                // Aceleración en los extremos
                double extremeDistance = Math.abs(currentAngle - Math.PI/2) / (Math.PI/2);
                return 1.0 + extremeDistance * ACCELERATION_FACTOR;
                
            case VARIABLE_SPEED:
                // Velocidad que cambia con el tiempo
                long time = System.currentTimeMillis();
                double timeVariation = Math.sin(time * 0.001) * 0.5;
                return 1.0 + timeVariation + Math.cos(currentAngle) * SPEED_VARIATION;
                
            case PRECISION_MODE:
                // Movimiento muy controlado y lento
                return 0.6 + Math.cos(currentAngle * 2) * 0.2;
                
            case EXPERT_CHALLENGE:
                // Movimiento errático y desafiante
                double chaos = Math.sin(currentAngle * 3) * 0.4;
                double timeChao = Math.cos(System.currentTimeMillis() * 0.003) * 0.3;
                return 1.2 + chaos + timeChao;
                
            case MASTER_LEVEL:
                // Movimiento extremadamente desafiante
                double masterChaos = Math.sin(currentAngle * 5) * 0.6;
                double masterTime = Math.sin(System.currentTimeMillis() * 0.005) * 0.4;
                double masterAccel = Math.pow(Math.abs(Math.cos(currentAngle)), 2) * 0.8;
                return 1.5 + masterChaos + masterTime + masterAccel;
                
            default:
                return 1.0;
        }
    }
    
    /**
     * Obtiene el progreso del movimiento (0.0 a 1.0)
     */
    public double getMovementProgress() {
        return currentAngle / Math.PI;
    }
    
    /**
     * Verifica si se está moviendo hacia la derecha
     */
    public boolean isMovingRight() {
        return movingClockwise && currentAngle < Math.PI/2 || 
               !movingClockwise && currentAngle > Math.PI/2;
    }
    
    /**
     * Reinicia el movimiento
     */
    public void reset() {
        currentAngle = 0;
        movingClockwise = true;
        lastUpdateTime = System.currentTimeMillis();
    }
    
    /**
     * Cambia el patrón de movimiento
     */
    public void setPattern(MovementPattern pattern) {
        this.currentPattern = pattern;
        
        // Ajustar velocidad base según el patrón
        switch (pattern) {
            case CLASSIC_PENDULUM:
                baseAngularSpeed = BASE_ANGULAR_SPEED;
                break;
            case ACCELERATED_SWING:
                baseAngularSpeed = BASE_ANGULAR_SPEED * 1.2;
                break;
            case VARIABLE_SPEED:
                baseAngularSpeed = BASE_ANGULAR_SPEED * 1.1;
                break;
            case PRECISION_MODE:
                baseAngularSpeed = BASE_ANGULAR_SPEED * 0.7;
                break;
            case EXPERT_CHALLENGE:
                baseAngularSpeed = BASE_ANGULAR_SPEED * 1.4;
                break;
            case MASTER_LEVEL:
                baseAngularSpeed = BASE_ANGULAR_SPEED * 1.6;
                break;
        }
    }
    
    /**
     * Ajusta la velocidad angular
     */
    public void setAngularSpeed(double speed) {
        this.baseAngularSpeed = Math.max(0.005, Math.min(0.1, speed));
    }
    
    /**
     * Ajusta el radio de movimiento
     */
    public void setSwingRadius(double radius) {
        this.swingRadius = Math.max(50, radius);
    }
    
    /**
     * Obtiene información del estado actual
     */
    public String getStatusInfo() {
        return String.format("Pattern: %s | Angle: %.2f | Speed: %.3f | Direction: %s",
                           currentPattern.name(),
                           Math.toDegrees(currentAngle),
                           angularSpeed,
                           movingClockwise ? "CW" : "CCW");
    }
    
    // Getters
    public MovementPattern getCurrentPattern() { return currentPattern; }
    public double getCurrentAngle() { return currentAngle; }
    public double getAngularSpeed() { return baseAngularSpeed; }
    public double getSwingRadius() { return swingRadius; }
    public double getCenterX() { return centerX; }
    public double getCenterY() { return centerY; }
    
    // Setters
    public void setCenterX(double centerX) { this.centerX = centerX; }
    public void setCenterY(double centerY) { this.centerY = centerY; }
}