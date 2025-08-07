package com.skillparty.towerblox.ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * Menú de pausa mejorado para el juego
 * Permite configurar opciones y reanudar/salir del juego
 * 
 * @author joseAlejandro
 */
public class PauseMenu extends JPanel {
    private JButton resumeButton;
    private JButton optionsButton;
    private JButton mainMenuButton;
    private JButton quitButton;
    private JSlider volumeSlider;
    private JCheckBox soundEnabledCheckBox;
    
    private boolean isVisible = false;
    private Runnable resumeCallback;
    private Runnable mainMenuCallback;
    private Runnable quitCallback;
    
    public PauseMenu() {
        initializeComponents();
        setupLayout();
        setupKeyBindings();
        setOpaque(false); // Para overlay transparente
    }
    
    private void initializeComponents() {
        // Crear botones con texto mejorado
        resumeButton = new JButton("▶ Reanudar (ESC)");
        optionsButton = new JButton("⚙ Opciones");
        mainMenuButton = new JButton("🏠 Menú Principal");
        quitButton = new JButton("❌ Salir del Juego");
        
        // Controles de sonido
        soundEnabledCheckBox = new JCheckBox("Sonido activado", true);
        volumeSlider = new JSlider(0, 100, 70);
        volumeSlider.setPaintLabels(true);
        volumeSlider.setPaintTicks(true);
        volumeSlider.setMajorTickSpacing(25);
        
        // Configurar estilos
        setupButtonStyles();
    }
    
    private void setupButtonStyles() {
        Font buttonFont = new Font("Ubuntu Mono", Font.BOLD, 16);
        Color buttonColor = new Color(59, 130, 246);
        Color hoverColor = new Color(99, 102, 241);
        
        JButton[] buttons = {resumeButton, optionsButton, mainMenuButton, quitButton};
        
        for (JButton button : buttons) {
            button.setFont(buttonFont);
            button.setForeground(Color.WHITE);
            button.setBackground(buttonColor);
            button.setBorder(BorderFactory.createRaisedBevelBorder());
            button.setFocusPainted(false);
            button.setPreferredSize(new Dimension(250, 40));
            
            // Efecto hover
            button.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    button.setBackground(hoverColor);
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    button.setBackground(buttonColor);
                }
            });
        }
        
        // Configurar actions
        resumeButton.addActionListener(e -> resumeGame());
        optionsButton.addActionListener(e -> toggleOptions());
        mainMenuButton.addActionListener(e -> goToMainMenu());
        quitButton.addActionListener(e -> quitGame());
    }
    
    private void setupLayout() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Panel principal con fondo semi-transparente
        JPanel mainPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fondo semi-transparente
                g2d.setColor(new Color(0, 0, 0, 180));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                // Borde
                g2d.setColor(new Color(59, 130, 246));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 20, 20);
                
                g2d.dispose();
            }
        };
        mainPanel.setOpaque(false);
        
        GridBagConstraints panelGbc = new GridBagConstraints();
        panelGbc.insets = new Insets(10, 10, 10, 10);
        panelGbc.anchor = GridBagConstraints.CENTER;
        
        // Título
        JLabel titleLabel = new JLabel("⏸ JUEGO PAUSADO");
        titleLabel.setFont(new Font("Ubuntu Mono", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panelGbc.gridx = 0; panelGbc.gridy = 0;
        panelGbc.gridwidth = 2;
        mainPanel.add(titleLabel, panelGbc);
        
        // Botones
        panelGbc.gridwidth = 1;
        panelGbc.fill = GridBagConstraints.HORIZONTAL;
        
        panelGbc.gridy++; mainPanel.add(resumeButton, panelGbc);
        panelGbc.gridy++; mainPanel.add(optionsButton, panelGbc);
        panelGbc.gridy++; mainPanel.add(mainMenuButton, panelGbc);
        panelGbc.gridy++; mainPanel.add(quitButton, panelGbc);
        
        // Panel de opciones (inicialmente oculto)
        JPanel optionsPanel = createOptionsPanel();
        panelGbc.gridy++;
        panelGbc.gridwidth = 2;
        mainPanel.add(optionsPanel, panelGbc);
        optionsPanel.setVisible(false);
        
        // Añadir panel principal al center
        gbc.anchor = GridBagConstraints.CENTER;
        add(mainPanel, gbc);
    }
    
    private JPanel createOptionsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY), 
            "Opciones de Sonido",
            0, 0, 
            new Font("Ubuntu Mono", Font.BOLD, 14), 
            Color.WHITE));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Checkbox de sonido
        soundEnabledCheckBox.setForeground(Color.WHITE);
        soundEnabledCheckBox.setOpaque(false);
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(soundEnabledCheckBox, gbc);
        
        // Slider de volumen
        JLabel volumeLabel = new JLabel("Volumen:");
        volumeLabel.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        panel.add(volumeLabel, gbc);
        
        gbc.gridx = 1;
        panel.add(volumeSlider, gbc);
        
        return panel;
    }
    
    private void setupKeyBindings() {
        // ESC para reanudar
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "resume");
        getActionMap().put("resume", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resumeGame();
            }
        });
        
        // ENTER para reanudar
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "resume");
    }
    
    private void resumeGame() {
        setVisible(false);
        if (resumeCallback != null) {
            resumeCallback.run();
        }
    }
    
    private void toggleOptions() {
        // Toggle visibility of options panel
        Component optionsPanel = ((JPanel)getComponent(0)).getComponent(5);
        optionsPanel.setVisible(!optionsPanel.isVisible());
        repaint();
    }
    
    private void goToMainMenu() {
        if (mainMenuCallback != null) {
            mainMenuCallback.run();
        }
    }
    
    private void quitGame() {
        if (quitCallback != null) {
            quitCallback.run();
        } else {
            System.exit(0);
        }
    }
    
    // Setters para callbacks
    public void setResumeCallback(Runnable callback) {
        this.resumeCallback = callback;
    }
    
    public void setMainMenuCallback(Runnable callback) {
        this.mainMenuCallback = callback;
    }
    
    public void setQuitCallback(Runnable callback) {
        this.quitCallback = callback;
    }
    
    // Getters para configuraciones
    public boolean isSoundEnabled() {
        return soundEnabledCheckBox.isSelected();
    }
    
    public int getVolumeLevel() {
        return volumeSlider.getValue();
    }
    
    public void setSoundEnabled(boolean enabled) {
        soundEnabledCheckBox.setSelected(enabled);
    }
    
    public void setVolumeLevel(int volume) {
        volumeSlider.setValue(Math.max(0, Math.min(100, volume)));
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (isVisible) {
            // Fondo semi-transparente que cubre toda la pantalla
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setColor(new Color(0, 0, 0, 100));
            g2d.fillRect(0, 0, getWidth(), getHeight());
            g2d.dispose();
        }
    }
}
