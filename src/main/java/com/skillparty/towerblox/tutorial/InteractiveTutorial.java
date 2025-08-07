package com.skillparty.towerblox.tutorial;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Tutorial interactivo para nuevos jugadores
 * Explica los controles básicos y mecánicas del juego
 * 
 * @author joseAlejandro
 */
public class InteractiveTutorial extends JDialog {
    
    private int currentStep = 0;
    private final TutorialStep[] tutorialSteps;
    private JLabel titleLabel;
    private JTextArea instructionArea;
    private JButton nextButton;
    private JButton skipButton;
    private JPanel animationPanel;
    
    public InteractiveTutorial(Frame parent) {
        super(parent, "Tutorial - javaTower", true);
        this.tutorialSteps = createTutorialSteps();
        initializeComponents();
        setupLayout();
        showCurrentStep();
        
        setSize(600, 450);
        setLocationRelativeTo(parent);
        setResizable(false);
    }
    
    private TutorialStep[] createTutorialSteps() {
        return new TutorialStep[] {
            new TutorialStep(
                "¡Bienvenido a javaTower! 🏗️",
                "Tu objetivo es construir la torre más alta posible.\n\n" +
                "Los bloques se mueven automáticamente de izquierda a derecha.\n" +
                "Tu trabajo es soltarlos en el momento perfecto para crear una torre estable.\n\n" +
                "¿Listo para aprender los controles básicos?",
                "🎮"
            ),
            
            new TutorialStep(
                "Control Principal: ESPACIADOR ⌨️",
                "Presiona la BARRA ESPACIADORA para soltar el bloque.\n\n" +
                "🎯 CONSEJO: Intenta alinear perfectamente el bloque con el de abajo\n" +
                "para obtener puntos extra y mantener la estabilidad de la torre.\n\n" +
                "La precisión es clave para construir torres altas.",
                "⌨️"
            ),
            
            new TutorialStep(
                "Sistema de Puntuación 🏆",
                "Ganas puntos por:\n" +
                "• Colocar bloques correctamente (+10 puntos base)\n" +
                "• Alineación perfecta (+50 puntos bonus)\n" +
                "• Construir combos consecutivos (x2, x3, x4...)\n" +
                "• Altura de la torre (bonus por altura)\n\n" +
                "¡Cuanto más preciso seas, más puntos obtendrás!",
                "🏆"
            ),
            
            new TutorialStep(
                "Controles Adicionales 🎮",
                "P - Pausar/Reanudar el juego\n" +
                "ESC - Menú principal / Pausa\n" +
                "1, 2, 3 - Cambio rápido de dificultad (en menú)\n" +
                "FLECHAS - Navegación en menús\n" +
                "ENTER - Confirmar selección\n\n" +
                "¡Estos atajos te harán más eficiente!",
                "⌨️"
            ),
            
            new TutorialStep(
                "Niveles de Dificultad 📈",
                "🟢 FÁCIL: Bloques se mueven lentamente\n" +
                "🟡 NORMAL: Velocidad estándar, equilibrio perfecto\n" +
                "🔴 DIFÍCIL: ¡Velocidad máxima para expertos!\n\n" +
                "Los niveles más difíciles otorgan multiplicadores de puntuación.\n" +
                "¡Comienza en fácil si eres nuevo en el juego!",
                "📈"
            ),
            
            new TutorialStep(
                "Estabilidad de la Torre ⚖️",
                "Observa el medidor de estabilidad:\n\n" +
                "🟢 Verde (80-100%): Torre muy estable\n" +
                "🟡 Amarillo (50-79%): Cuidado, torre inestable\n" +
                "🔴 Rojo (0-49%): ¡Peligro! Torre a punto de caer\n\n" +
                "Si la estabilidad llega a 0%, ¡se acabó el juego!",
                "⚖️"
            ),
            
            new TutorialStep(
                "¡Listo para Jugar! 🚀",
                "Has completado el tutorial básico.\n\n" +
                "Recuerda:\n" +
                "• Usa ESPACIO para soltar bloques\n" +
                "• Apunta a la alineación perfecta\n" +
                "• Mantén la estabilidad de la torre\n" +
                "• ¡Diviértete construyendo!\n\n" +
                "¡Buena suerte, constructor! 🏗️👷‍♂️",
                "🎉"
            )
        };
    }
    
    private void initializeComponents() {
        // Título
        titleLabel = new JLabel();
        titleLabel.setFont(new Font("Ubuntu Mono", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setForeground(new Color(59, 130, 246));
        
        // Área de instrucciones
        instructionArea = new JTextArea();
        instructionArea.setFont(new Font("Ubuntu Mono", Font.PLAIN, 14));
        instructionArea.setWrapStyleWord(true);
        instructionArea.setLineWrap(true);
        instructionArea.setEditable(false);
        instructionArea.setOpaque(false);
        instructionArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel de animación (para futuros gráficos)
        animationPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Dibujar icono grande del paso actual
                String icon = tutorialSteps[currentStep].getIcon();
                g2d.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
                g2d.setColor(new Color(99, 102, 241));
                
                FontMetrics fm = g2d.getFontMetrics();
                int iconWidth = fm.stringWidth(icon);
                int iconHeight = fm.getHeight();
                
                g2d.drawString(icon, 
                    (getWidth() - iconWidth) / 2, 
                    (getHeight() + iconHeight) / 2 - fm.getDescent());
                
                g2d.dispose();
            }
        };
        animationPanel.setPreferredSize(new Dimension(150, 100));
        animationPanel.setBackground(new Color(240, 242, 247));
        
        // Botones
        nextButton = new JButton("Siguiente ➡️");
        skipButton = new JButton("Saltar Tutorial");
        
        setupButtonStyles();
        setupEventHandlers();
    }
    
    private void setupButtonStyles() {
        Font buttonFont = new Font("Ubuntu Mono", Font.BOLD, 12);
        
        nextButton.setFont(buttonFont);
        nextButton.setBackground(new Color(59, 130, 246));
        nextButton.setForeground(Color.WHITE);
        nextButton.setFocusPainted(false);
        
        skipButton.setFont(buttonFont);
        skipButton.setBackground(new Color(156, 163, 175));
        skipButton.setForeground(Color.WHITE);
        skipButton.setFocusPainted(false);
    }
    
    private void setupEventHandlers() {
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nextStep();
            }
        });
        
        skipButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        // Permitir navegación con teclado
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                    case KeyEvent.VK_SPACE:
                    case KeyEvent.VK_RIGHT:
                        nextStep();
                        break;
                    case KeyEvent.VK_ESCAPE:
                        dispose();
                        break;
                }
            }
        });
        
        setFocusable(true);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        
        // Panel superior con título
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(248, 250, 252));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Panel central con contenido
        JPanel contentPanel = new JPanel(new BorderLayout(15, 15));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        contentPanel.add(animationPanel, BorderLayout.WEST);
        contentPanel.add(new JScrollPane(instructionArea), BorderLayout.CENTER);
        
        // Panel inferior con botones y progreso
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(new Color(248, 250, 252));
        footerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        // Progress bar
        JProgressBar progressBar = new JProgressBar(0, tutorialSteps.length - 1);
        progressBar.setValue(currentStep);
        progressBar.setStringPainted(true);
        progressBar.setString(String.format("Paso %d de %d", currentStep + 1, tutorialSteps.length));
        
        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(skipButton);
        buttonPanel.add(nextButton);
        
        footerPanel.add(progressBar, BorderLayout.CENTER);
        footerPanel.add(buttonPanel, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
        
        // Actualizar progress bar cuando cambie el paso
        addPropertyChangeListener("currentStep", evt -> {
            progressBar.setValue(currentStep);
            progressBar.setString(String.format("Paso %d de %d", currentStep + 1, tutorialSteps.length));
        });
    }
    
    private void showCurrentStep() {
        TutorialStep step = tutorialSteps[currentStep];
        titleLabel.setText(step.getTitle());
        instructionArea.setText(step.getContent());
        
        // Actualizar botón
        if (currentStep == tutorialSteps.length - 1) {
            nextButton.setText("¡Empezar a Jugar! 🚀");
        } else {
            nextButton.setText("Siguiente ➡️");
        }
        
        animationPanel.repaint();
        firePropertyChange("currentStep", currentStep - 1, currentStep);
    }
    
    private void nextStep() {
        if (currentStep < tutorialSteps.length - 1) {
            currentStep++;
            showCurrentStep();
        } else {
            dispose();
        }
    }
    
    /**
     * Clase interna para representar cada paso del tutorial
     */
    private static class TutorialStep {
        private final String title;
        private final String content;
        private final String icon;
        
        public TutorialStep(String title, String content, String icon) {
            this.title = title;
            this.content = content;
            this.icon = icon;
        }
        
        public String getTitle() { return title; }
        public String getContent() { return content; }
        public String getIcon() { return icon; }
    }
    
    /**
     * Método estático para mostrar el tutorial
     */
    public static void showTutorial(Frame parent) {
        SwingUtilities.invokeLater(() -> {
            InteractiveTutorial tutorial = new InteractiveTutorial(parent);
            tutorial.setVisible(true);
        });
    }
}
