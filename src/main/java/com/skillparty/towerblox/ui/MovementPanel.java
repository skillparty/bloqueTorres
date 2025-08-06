package com.skillparty.towerblox.ui;

import com.skillparty.towerblox.game.MovementRecorder;
import com.skillparty.towerblox.game.MovementRecorder.MovementPattern;
import com.skillparty.towerblox.game.physics.Crane;
import com.skillparty.towerblox.ui.components.FontManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.ArrayList;

/**
 * Panel para grabar y gestionar movimientos de la grúa
 */
public class MovementPanel extends JPanel implements KeyListener {
    private static final long serialVersionUID = 1L;
    
    private GameWindow parentWindow;
    private FontManager fontManager;
    private MovementRecorder movementRecorder;
    private Crane democrane;
    
    // Colores profesionales
    private static final Color BACKGROUND_COLOR = new Color(15, 23, 42);
    private static final Color PANEL_COLOR = new Color(30, 41, 59, 200);
    private static final Color BORDER_COLOR = new Color(59, 130, 246);
    private static final Color TEXT_COLOR = new Color(248, 250, 252);
    private static final Color RECORDING_COLOR = new Color(239, 68, 68);
    private static final Color PLAYING_COLOR = new Color(16, 185, 129);
    private static final Color BUTTON_COLOR = new Color(99, 102, 241);
    
    // Estado del panel
    private boolean isRecording = false;
    private String currentRecordingName = "";
    private int selectedPatternIndex = 0;
    
    // Grúa de demostración
    private static final int DEMO_WIDTH = 600;
    private static final int DEMO_HEIGHT = 200;
    private static final int CRANE_Y = 50;
    
    public MovementPanel(GameWindow parentWindow) {
        this.parentWindow = parentWindow;
        this.fontManager = FontManager.getInstance();
        // Crear una instancia temporal que será reemplazada
        this.movementRecorder = new MovementRecorder();
        
        // Crear grúa de demostración
        this.democrane = new Crane(DEMO_WIDTH / 2, CRANE_Y, DEMO_WIDTH);
        
        initializePanel();
        setupTimer();
    }
    
    private void initializePanel() {
        setBackground(BACKGROUND_COLOR);
        setPreferredSize(new Dimension(800, 600));
        setFocusable(true);
        addKeyListener(this);
        
        // Agregar mouse listener para interacción y foco
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                requestFocusInWindow(); // Obtener foco al hacer clic
                System.out.println("🖱️ Click en MovementPanel - Foco solicitado");
                handleMouseClick(e.getX(), e.getY());
            }
        });
    }
    
    private void setupTimer() {
        Timer timer = new Timer(16, e -> {
            updateDemo();
            repaint();
        });
        timer.start();
    }
    
    private void updateDemo() {
        if (democrane != null) {
            democrane.update(16);
            
            // Habilitar control manual para la grúa de demostración
            democrane.setManualControl(true);
            
            // Grabar movimiento si está activo
            if (movementRecorder != null && movementRecorder.isRecording()) {
                movementRecorder.recordFrame(
                    democrane.getX(),
                    democrane.getY(),
                    100, // minX demo
                    DEMO_WIDTH - 100, // maxX demo
                    30, // minY demo
                    CRANE_Y + 150, // maxY demo
                    false, // no dropping in demo
                    democrane.getSpeed()
                );
            }
            
            // Aplicar reproducción si está activa
            if (movementRecorder != null) {
                MovementRecorder.Position replayPosition = movementRecorder.getReplayPosition(
                    100, DEMO_WIDTH - 100, 30, CRANE_Y + 150);
                if (replayPosition != null) {
                    democrane.setX(replayPosition.x);
                    democrane.setY(replayPosition.y);
                }
            }
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // Título principal
        drawTitle(g2d);
        
        // Área de demostración
        drawDemoArea(g2d);
        
        // Panel de control
        drawControlPanel(g2d);
        
        // Lista de patrones guardados
        drawPatternList(g2d);
        
        // Instrucciones
        drawInstructions(g2d);
        
        g2d.dispose();
    }
    
    private void drawTitle(Graphics2D g2d) {
        g2d.setFont(fontManager.getFont(FontManager.LOGO_SIZE, Font.BOLD));
        g2d.setColor(TEXT_COLOR);
        
        String title = "GRABADOR DE MOVIMIENTOS";
        FontMetrics fm = g2d.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(title)) / 2;
        g2d.drawString(title, x, 40);
        
        // Subtítulo
        g2d.setFont(fontManager.getFont(FontManager.MENU_SIZE));
        g2d.setColor(new Color(148, 163, 184));
        String subtitle = "Graba patrones personalizados para la grúa";
        fm = g2d.getFontMetrics();
        x = (getWidth() - fm.stringWidth(subtitle)) / 2;
        g2d.drawString(subtitle, x, 65);
    }
    
    private void drawDemoArea(Graphics2D g2d) {
        int areaX = (getWidth() - DEMO_WIDTH) / 2;
        int areaY = 80;
        
        // Fondo del área de demostración
        g2d.setColor(PANEL_COLOR);
        g2d.fillRoundRect(areaX, areaY, DEMO_WIDTH, DEMO_HEIGHT, 15, 15);
        
        // Borde
        g2d.setColor(BORDER_COLOR);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(areaX, areaY, DEMO_WIDTH, DEMO_HEIGHT, 15, 15);
        
        // Transformar coordenadas para la grúa
        g2d.translate(areaX, areaY);
        
        // Dibujar grúa de demostración
        if (democrane != null) {
            democrane.render(g2d);
        }
        
        // Indicador de estado
        drawRecordingIndicator(g2d, 10, 10);
        
        // Restaurar transformación
        g2d.translate(-areaX, -areaY);
    }
    
    private void drawRecordingIndicator(Graphics2D g2d, int x, int y) {
        if (movementRecorder != null) {
            if (movementRecorder.isRecording()) {
                // Indicador de grabación parpadeante
                long time = System.currentTimeMillis();
                if ((time / 500) % 2 == 0) {
                    g2d.setColor(RECORDING_COLOR);
                    g2d.fillOval(x, y, 12, 12);
                }
                
                g2d.setColor(TEXT_COLOR);
                g2d.setFont(fontManager.getFont(FontManager.SMALL_SIZE, Font.BOLD));
                g2d.drawString("🔴 GRABANDO: " + movementRecorder.getCurrentRecordingName(), x + 20, y + 10);
                
            } else if (movementRecorder.isReplaying()) {
                g2d.setColor(PLAYING_COLOR);
                g2d.fillOval(x, y, 12, 12);
                
                g2d.setColor(TEXT_COLOR);
                g2d.setFont(fontManager.getFont(FontManager.SMALL_SIZE, Font.BOLD));
                MovementPattern current = movementRecorder.getCurrentReplayPattern();
                if (current != null) {
                    g2d.drawString("▶️ REPRODUCIENDO: " + current.name, x + 20, y + 10);
                }
            }
        } else {
            // Mostrar que no está conectado
            g2d.setColor(Color.RED);
            g2d.setFont(fontManager.getFont(FontManager.SMALL_SIZE, Font.BOLD));
            g2d.drawString("❌ MovementRecorder no conectado", x + 20, y + 10);
        }
    }
    
    private void drawControlPanel(Graphics2D g2d) {
        int panelX = 50;
        int panelY = 300;
        int panelWidth = 300;
        int panelHeight = 200;
        
        // Fondo del panel de control
        g2d.setColor(PANEL_COLOR);
        g2d.fillRoundRect(panelX, panelY, panelWidth, panelHeight, 15, 15);
        
        g2d.setColor(BORDER_COLOR);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(panelX, panelY, panelWidth, panelHeight, 15, 15);
        
        // Título del panel
        g2d.setColor(TEXT_COLOR);
        g2d.setFont(fontManager.getFont(FontManager.MENU_SIZE, Font.BOLD));
        g2d.drawString("CONTROLES", panelX + 20, panelY + 30);
        
        // Botones de control
        int buttonY = panelY + 50;
        int buttonHeight = 30;
        int buttonSpacing = 35;
        
        // Botón de grabación
        boolean isRecording = movementRecorder != null && movementRecorder.isRecording();
        drawControlButton(g2d, panelX + 20, buttonY, 260, buttonHeight, 
                         isRecording ? "DETENER GRABACIÓN (R)" : "INICIAR GRABACIÓN (R)",
                         isRecording ? RECORDING_COLOR : BUTTON_COLOR);
        
        buttonY += buttonSpacing;
        
        // Botón de reproducción
        boolean isReplaying = movementRecorder != null && movementRecorder.isReplaying();
        drawControlButton(g2d, panelX + 20, buttonY, 260, buttonHeight,
                         isReplaying ? "DETENER REPRODUCCIÓN (P)" : "REPRODUCIR PATRÓN (P)",
                         isReplaying ? PLAYING_COLOR : BUTTON_COLOR);
        
        buttonY += buttonSpacing;
        
        // Botón de anterior patrón
        drawControlButton(g2d, panelX + 20, buttonY, 80, buttonHeight,
                         "ANTERIOR (B)", BUTTON_COLOR);
        
        // Botón de siguiente patrón
        drawControlButton(g2d, panelX + 110, buttonY, 80, buttonHeight,
                         "SIGUIENTE (N)", BUTTON_COLOR);
        
        // Botón de eliminar
        drawControlButton(g2d, panelX + 200, buttonY, 80, buttonHeight,
                         "ELIMINAR (DEL)", new Color(239, 68, 68));
        
        buttonY += buttonSpacing;
        
        // Estado actual
        g2d.setColor(new Color(148, 163, 184));
        g2d.setFont(fontManager.getFont(FontManager.SMALL_SIZE));
        String statusInfo = movementRecorder != null ? movementRecorder.getStatusInfo() : "❌ MovementRecorder no conectado";
        g2d.drawString("Estado: " + statusInfo, panelX + 20, buttonY + 10);
    }
    
    private void drawControlButton(Graphics2D g2d, int x, int y, int width, int height, String text, Color color) {
        // Fondo del botón
        g2d.setColor(color);
        g2d.fillRoundRect(x, y, width, height, 8, 8);
        
        // Borde
        g2d.setColor(color.brighter());
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRoundRect(x, y, width, height, 8, 8);
        
        // Texto
        g2d.setColor(Color.WHITE);
        g2d.setFont(fontManager.getFont(FontManager.SMALL_SIZE, Font.BOLD));
        FontMetrics fm = g2d.getFontMetrics();
        int textX = x + (width - fm.stringWidth(text)) / 2;
        int textY = y + (height + fm.getAscent()) / 2 - 2;
        g2d.drawString(text, textX, textY);
    }
    
    private void drawPatternList(Graphics2D g2d) {
        int listX = 400;
        int listY = 300;
        int listWidth = 350;
        int listHeight = 200;
        
        // Fondo de la lista
        g2d.setColor(PANEL_COLOR);
        g2d.fillRoundRect(listX, listY, listWidth, listHeight, 15, 15);
        
        g2d.setColor(BORDER_COLOR);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(listX, listY, listWidth, listHeight, 15, 15);
        
        // Título
        g2d.setColor(TEXT_COLOR);
        g2d.setFont(fontManager.getFont(FontManager.MENU_SIZE, Font.BOLD));
        g2d.drawString("PATRONES GUARDADOS", listX + 20, listY + 30);
        
        // Lista de patrones
        List<MovementPattern> patterns = movementRecorder != null ? 
            movementRecorder.getSavedPatterns() : new ArrayList<>();
        g2d.setFont(fontManager.getFont(FontManager.SMALL_SIZE));
        
        int itemY = listY + 55;
        int itemHeight = 20;
        
        if (patterns.isEmpty()) {
            g2d.setColor(new Color(148, 163, 184));
            g2d.drawString("No hay patrones guardados", listX + 20, itemY);
            g2d.drawString("Presiona R para grabar tu primer patrón", listX + 20, itemY + 20);
        } else {
            // Asegurar que el índice seleccionado esté en rango
            if (selectedPatternIndex >= patterns.size()) {
                selectedPatternIndex = patterns.size() - 1;
            }
            if (selectedPatternIndex < 0) {
                selectedPatternIndex = 0;
            }
            
            for (int i = 0; i < patterns.size() && i < 7; i++) {
                MovementPattern pattern = patterns.get(i);
                
                // Resaltar patrón seleccionado
                if (i == selectedPatternIndex) {
                    g2d.setColor(new Color(99, 102, 241, 150));
                    g2d.fillRoundRect(listX + 15, itemY - 15, listWidth - 30, itemHeight, 5, 5);
                    
                    // Borde adicional para el seleccionado
                    g2d.setColor(BORDER_COLOR);
                    g2d.setStroke(new BasicStroke(1));
                    g2d.drawRoundRect(listX + 15, itemY - 15, listWidth - 30, itemHeight, 5, 5);
                }
                
                // Color del texto
                g2d.setColor(i == selectedPatternIndex ? Color.WHITE : TEXT_COLOR);
                
                // Indicador de selección
                String indicator = i == selectedPatternIndex ? "► " : "  ";
                String patternInfo = String.format("%s%d. %s (%d frames, %.1fs)", 
                                                 indicator, i + 1, pattern.name, pattern.getFrameCount(),
                                                 pattern.totalDuration / 1000.0);
                g2d.drawString(patternInfo, listX + 20, itemY);
                
                itemY += itemHeight;
            }
            
            if (patterns.size() > 7) {
                g2d.setColor(new Color(148, 163, 184));
                g2d.drawString("... y " + (patterns.size() - 7) + " más", listX + 20, itemY);
            }
            
            // Mostrar información del patrón seleccionado
            if (selectedPatternIndex < patterns.size()) {
                MovementPattern selected = patterns.get(selectedPatternIndex);
                g2d.setColor(new Color(148, 163, 184));
                g2d.setFont(fontManager.getFont(FontManager.SMALL_SIZE - 2));
                g2d.drawString("Seleccionado: " + selected.name, listX + 20, itemY + 15);
            }
        }
    }
    
    private void drawInstructions(Graphics2D g2d) {
        int instrY = 520;
        
        g2d.setColor(new Color(148, 163, 184));
        g2d.setFont(fontManager.getFont(FontManager.SMALL_SIZE));
        
        String[] instructions = {
            "CONTROLES: ← → ↑ ↓ Mover grúa | ESPACIO Soltar | R Grabar | P Reproducir | B Anterior | N Siguiente | DEL Eliminar | ESC Volver",
            "Para grabar: Presiona R, ingresa nombre, usa las flechas para mover la grúa en X e Y, presiona R para detener",
            "Los patrones grabados se reproducirán automáticamente durante el juego con movimiento completo"
        };
        
        for (int i = 0; i < instructions.length; i++) {
            FontMetrics fm = g2d.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(instructions[i])) / 2;
            g2d.drawString(instructions[i], x, instrY + i * 15);
        }
    }
    
    private void handleMouseClick(int x, int y) {
        // Implementar clicks en botones si es necesario
        // Por ahora usamos solo teclado
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        System.out.println("🎹 Tecla presionada: " + KeyEvent.getKeyText(e.getKeyCode()));
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                // Mover grúa a la izquierda
                if (democrane != null) {
                    democrane.moveLeft(10);
                }
                break;
                
            case KeyEvent.VK_RIGHT:
                // Mover grúa a la derecha
                if (democrane != null) {
                    democrane.moveRight(10);
                }
                break;
                
            case KeyEvent.VK_UP:
                // Mover grúa hacia arriba
                if (democrane != null) {
                    democrane.moveUp(10);
                }
                break;
                
            case KeyEvent.VK_DOWN:
                // Mover grúa hacia abajo
                if (democrane != null) {
                    democrane.moveDown(10);
                }
                break;
                
            case KeyEvent.VK_SPACE:
                // Simular soltar bloque (para grabación)
                if (movementRecorder != null && movementRecorder.isRecording() && democrane != null) {
                    movementRecorder.recordFrame(
                        democrane.getX(), democrane.getY(),
                        100, DEMO_WIDTH - 100, 30, CRANE_Y + 150,
                        true, democrane.getSpeed()
                    );
                }
                break;
                
            case KeyEvent.VK_R:
                // Alternar grabación
                if (movementRecorder != null) {
                    if (movementRecorder.isRecording()) {
                        System.out.println("🛑 Deteniendo grabación...");
                        movementRecorder.stopRecording();
                    } else {
                        String name = JOptionPane.showInputDialog(this, 
                            "Nombre del patrón:", "Nuevo Patrón", JOptionPane.QUESTION_MESSAGE);
                        if (name != null && !name.trim().isEmpty()) {
                            System.out.println("🔴 Iniciando grabación: " + name.trim());
                            movementRecorder.startRecording(name.trim());
                        }
                    }
                } else {
                    System.out.println("❌ MovementRecorder es null!");
                }
                break;
                
            case KeyEvent.VK_P:
                // Alternar reproducción
                if (movementRecorder != null) {
                    if (movementRecorder.isReplaying()) {
                        movementRecorder.stopReplay();
                    } else {
                        List<MovementPattern> patterns = movementRecorder.getSavedPatterns();
                        if (!patterns.isEmpty() && selectedPatternIndex < patterns.size()) {
                            movementRecorder.startReplay(patterns.get(selectedPatternIndex));
                        }
                    }
                }
                break;
                
            case KeyEvent.VK_N:
                // Siguiente patrón
                if (movementRecorder != null) {
                    List<MovementPattern> patterns = movementRecorder.getSavedPatterns();
                    if (!patterns.isEmpty()) {
                        selectedPatternIndex = (selectedPatternIndex + 1) % patterns.size();
                        System.out.println("📋 Patrón seleccionado: " + selectedPatternIndex + " (" + patterns.get(selectedPatternIndex).name + ")");
                    } else {
                        selectedPatternIndex = 0;
                        System.out.println("📋 No hay patrones disponibles");
                    }
                }
                break;
                
            case KeyEvent.VK_B:
                // Patrón anterior
                if (movementRecorder != null) {
                    List<MovementPattern> patterns = movementRecorder.getSavedPatterns();
                    if (!patterns.isEmpty()) {
                        selectedPatternIndex = (selectedPatternIndex - 1 + patterns.size()) % patterns.size();
                        System.out.println("📋 Patrón seleccionado: " + selectedPatternIndex + " (" + patterns.get(selectedPatternIndex).name + ")");
                    } else {
                        selectedPatternIndex = 0;
                        System.out.println("📋 No hay patrones disponibles");
                    }
                }
                break;
                
            case KeyEvent.VK_DELETE:
                // Eliminar patrón seleccionado
                if (movementRecorder != null) {
                    List<MovementPattern> patterns = movementRecorder.getSavedPatterns();
                    if (!patterns.isEmpty() && selectedPatternIndex >= 0 && selectedPatternIndex < patterns.size()) {
                        MovementPattern patternToDelete = patterns.get(selectedPatternIndex);
                        int result = JOptionPane.showConfirmDialog(this,
                            "¿Eliminar el patrón '" + patternToDelete.name + "'?", 
                            "Confirmar eliminación",
                            JOptionPane.YES_NO_OPTION);
                        if (result == JOptionPane.YES_OPTION) {
                            System.out.println("🗑️ Eliminando patrón: " + patternToDelete.name + " (índice " + selectedPatternIndex + ")");
                            boolean deleted = movementRecorder.deletePattern(selectedPatternIndex);
                            if (deleted) {
                                // Actualizar índice seleccionado
                                List<MovementPattern> updatedPatterns = movementRecorder.getSavedPatterns();
                                if (updatedPatterns.isEmpty()) {
                                    selectedPatternIndex = 0;
                                } else if (selectedPatternIndex >= updatedPatterns.size()) {
                                    selectedPatternIndex = updatedPatterns.size() - 1;
                                }
                                System.out.println("✅ Patrón eliminado. Nuevo índice seleccionado: " + selectedPatternIndex);
                            } else {
                                System.out.println("❌ Error al eliminar el patrón");
                            }
                        }
                    } else {
                        System.out.println("⚠️ No hay patrón válido para eliminar. Patrones: " + patterns.size() + ", Índice: " + selectedPatternIndex);
                    }
                }
                break;
                
            case KeyEvent.VK_ESCAPE:
                // Volver al menú principal
                parentWindow.showMenu();
                break;
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        // No usado
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
        // No usado
    }
    
    public MovementRecorder getMovementRecorder() {
        return movementRecorder;
    }
    
    public void setMovementRecorder(MovementRecorder recorder) {
        this.movementRecorder = recorder;
        System.out.println("✅ MovementRecorder conectado al MovementPanel");
        
        // Test inmediato del MovementRecorder
        if (recorder != null) {
            System.out.println("🧪 Probando MovementRecorder...");
            recorder.startRecording("test");
            System.out.println("🧪 Estado después de startRecording: " + recorder.isRecording());
            recorder.stopRecording();
            System.out.println("🧪 Patrones guardados: " + recorder.getSavedPatterns().size());
        }
    }
}