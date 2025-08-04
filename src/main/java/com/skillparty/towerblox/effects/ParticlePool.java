package com.skillparty.towerblox.effects;

import java.util.ArrayList;
import java.util.List;

/**
 * Object pool for particles to avoid garbage collection overhead
 * Reuses particle objects for better performance
 */
public class ParticlePool {
    private final List<Particle> availableParticles;
    private final List<Particle> activeParticles;
    private final int maxPoolSize;
    
    public ParticlePool(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
        this.availableParticles = new ArrayList<>(maxPoolSize);
        this.activeParticles = new ArrayList<>(maxPoolSize);
        
        // Pre-create particles
        for (int i = 0; i < maxPoolSize; i++) {
            availableParticles.add(new Particle());
        }
    }
    
    /**
     * Get a particle from the pool
     */
    public Particle acquire() {
        Particle particle;
        
        if (!availableParticles.isEmpty()) {
            // Reuse existing particle
            particle = availableParticles.remove(availableParticles.size() - 1);
        } else if (activeParticles.size() < maxPoolSize) {
            // Create new particle if under limit
            particle = new Particle();
        } else {
            // Pool is full, reuse oldest active particle
            particle = activeParticles.remove(0);
            particle.reset();
        }
        
        activeParticles.add(particle);
        return particle;
    }
    
    /**
     * Return a particle to the pool
     */
    public void release(Particle particle) {
        if (activeParticles.remove(particle)) {
            particle.reset();
            availableParticles.add(particle);
        }
    }
    
    /**
     * Update all active particles and return inactive ones to pool
     */
    public void updateAndCleanup(double deltaTime) {
        for (int i = activeParticles.size() - 1; i >= 0; i--) {
            Particle particle = activeParticles.get(i);
            particle.update(deltaTime);
            
            if (!particle.isActive()) {
                release(particle);
            }
        }
    }
    
    /**
     * Get all active particles for rendering
     */
    public List<Particle> getActiveParticles() {
        return new ArrayList<>(activeParticles);
    }
    
    /**
     * Get pool statistics
     */
    public int getActiveCount() {
        return activeParticles.size();
    }
    
    public int getAvailableCount() {
        return availableParticles.size();
    }
    
    public int getMaxPoolSize() {
        return maxPoolSize;
    }
    
    /**
     * Clear all particles
     */
    public void clear() {
        for (Particle particle : activeParticles) {
            particle.reset();
            availableParticles.add(particle);
        }
        activeParticles.clear();
    }
}