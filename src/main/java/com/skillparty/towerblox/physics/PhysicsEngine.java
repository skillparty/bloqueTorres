package com.skillparty.towerblox.physics;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Lightweight but realistic physics engine for Tower Bloxx
 * Handles gravity, momentum, collisions, and precise timing
 * 
 * @author joseAlejandro
 * @version 2.0 - Production Ready
 */
public class PhysicsEngine {
    
    // Physics constants (tuned for Tower Bloxx feel)
    private static final double GRAVITY = 980.0; // pixels/s² (realistic gravity)
    private static final double AIR_RESISTANCE = 0.02; // coefficient
    private static final double TERMINAL_VELOCITY = 850.0; // pixels/s
    private static final double BOUNCE_DAMPING = 0.4; // energy loss on bounce
    private static final double FRICTION = 0.15; // horizontal friction
    
    // Collision detection
    private static final double COLLISION_THRESHOLD = 0.5; // pixels
    private static final int MAX_ITERATIONS = 10; // for precise collision resolution
    
    // Active physics bodies
    private List<PhysicsBody> bodies;
    private List<CollisionListener> collisionListeners;
    
    /**
     * Physics body representation
     */
    public static class PhysicsBody {
        public double x, y;           // Position
        public double vx, vy;          // Velocity
        public double ax, ay;          // Acceleration
        public double width, height;   // Dimensions
        public double mass;            // Mass (affects momentum)
        public double rotation;        // Rotation angle (radians)
        public double angularVelocity; // Rotation speed
        public boolean isStatic;       // Static bodies don't move
        public boolean hasGravity;     // Apply gravity?
        public Object userData;        // Reference to game object
        
        // Collision bounds
        private Rectangle2D.Double bounds;
        
        public PhysicsBody(double x, double y, double width, double height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.mass = width * height * 0.001; // Simple mass calculation
            this.vx = 0;
            this.vy = 0;
            this.ax = 0;
            this.ay = 0;
            this.rotation = 0;
            this.angularVelocity = 0;
            this.isStatic = false;
            this.hasGravity = true;
            this.bounds = new Rectangle2D.Double(x, y, width, height);
        }
        
        public void updateBounds() {
            bounds.setRect(x, y, width, height);
        }
        
        public Rectangle2D.Double getBounds() {
            return bounds;
        }
        
        // Apply impulse (instant velocity change)
        public void applyImpulse(double fx, double fy) {
            if (!isStatic) {
                vx += fx / mass;
                vy += fy / mass;
            }
        }
        
        // Apply force (continuous acceleration)
        public void applyForce(double fx, double fy) {
            if (!isStatic) {
                ax += fx / mass;
                ay += fy / mass;
            }
        }
    }
    
    /**
     * Collision event listener
     */
    public interface CollisionListener {
        void onCollision(PhysicsBody bodyA, PhysicsBody bodyB, CollisionInfo info);
    }
    
    /**
     * Collision information
     */
    public static class CollisionInfo {
        public double penetrationDepth;
        public double normalX, normalY; // Collision normal
        public double contactX, contactY; // Contact point
        public double relativeVelocity;
        public boolean isGroundCollision;
        
        public CollisionInfo() {
            this.penetrationDepth = 0;
            this.normalX = 0;
            this.normalY = -1; // Default upward normal
            this.isGroundCollision = false;
        }
    }
    
    /**
     * Constructs the physics engine
     */
    public PhysicsEngine() {
        this.bodies = new ArrayList<>();
        this.collisionListeners = new ArrayList<>();
    }
    
    /**
     * Updates all physics bodies
     */
    public void update(double deltaTime) {
        // Clamp delta time to prevent instability
        deltaTime = Math.min(deltaTime, 0.033); // Max 30ms per frame
        
        // Update each body
        for (PhysicsBody body : bodies) {
            if (body.isStatic) continue;
            
            // Reset accelerations
            body.ax = 0;
            body.ay = 0;
            
            // Apply gravity
            if (body.hasGravity) {
                body.ay += GRAVITY;
            }
            
            // Apply air resistance (proportional to velocity squared)
            double speed = Math.sqrt(body.vx * body.vx + body.vy * body.vy);
            if (speed > 0) {
                double dragX = -AIR_RESISTANCE * body.vx * speed;
                double dragY = -AIR_RESISTANCE * body.vy * speed;
                body.applyForce(dragX, dragY);
            }
            
            // Update velocity
            body.vx += body.ax * deltaTime;
            body.vy += body.ay * deltaTime;
            
            // Apply friction to horizontal movement
            body.vx *= (1.0 - FRICTION * deltaTime);
            
            // Limit to terminal velocity
            if (body.vy > TERMINAL_VELOCITY) {
                body.vy = TERMINAL_VELOCITY;
            }
            
            // Update position
            body.x += body.vx * deltaTime;
            body.y += body.vy * deltaTime;
            
            // Update rotation
            body.rotation += body.angularVelocity * deltaTime;
            
            // Update bounds
            body.updateBounds();
        }
        
        // Detect and resolve collisions
        detectCollisions();
    }
    
    /**
     * Detects collisions between bodies
     */
    private void detectCollisions() {
        for (int i = 0; i < bodies.size(); i++) {
            PhysicsBody bodyA = bodies.get(i);
            
            for (int j = i + 1; j < bodies.size(); j++) {
                PhysicsBody bodyB = bodies.get(j);
                
                // Skip if both are static
                if (bodyA.isStatic && bodyB.isStatic) continue;
                
                // Check for collision
                if (checkCollision(bodyA, bodyB)) {
                    CollisionInfo info = resolveCollision(bodyA, bodyB);
                    
                    // Notify listeners
                    for (CollisionListener listener : collisionListeners) {
                        listener.onCollision(bodyA, bodyB, info);
                    }
                }
            }
        }
    }
    
    /**
     * Checks if two bodies are colliding
     */
    private boolean checkCollision(PhysicsBody a, PhysicsBody b) {
        return a.getBounds().intersects(b.getBounds());
    }
    
    /**
     * Resolves collision between two bodies
     */
    private CollisionInfo resolveCollision(PhysicsBody a, PhysicsBody b) {
        CollisionInfo info = new CollisionInfo();
        
        // Calculate overlap
        Rectangle2D.Double boundsA = a.getBounds();
        Rectangle2D.Double boundsB = b.getBounds();
        
        double overlapX = Math.min(boundsA.x + boundsA.width - boundsB.x,
                                   boundsB.x + boundsB.width - boundsA.x);
        double overlapY = Math.min(boundsA.y + boundsA.height - boundsB.y,
                                   boundsB.y + boundsB.height - boundsA.y);
        
        // Determine collision normal (smallest overlap axis)
        if (overlapX < overlapY) {
            // Horizontal collision
            info.normalX = (boundsA.x < boundsB.x) ? -1 : 1;
            info.normalY = 0;
            info.penetrationDepth = overlapX;
        } else {
            // Vertical collision
            info.normalX = 0;
            info.normalY = (boundsA.y < boundsB.y) ? -1 : 1;
            info.penetrationDepth = overlapY;
            info.isGroundCollision = (info.normalY < 0);
        }
        
        // Calculate relative velocity
        double relVx = a.vx - b.vx;
        double relVy = a.vy - b.vy;
        info.relativeVelocity = relVx * info.normalX + relVy * info.normalY;
        
        // Don't resolve if velocities are separating
        if (info.relativeVelocity > 0) {
            return info;
        }
        
        // Calculate restitution (bounciness)
        double restitution = info.isGroundCollision ? BOUNCE_DAMPING : 0.2;
        
        // Calculate impulse scalar
        double impulseScalar = -(1 + restitution) * info.relativeVelocity;
        impulseScalar /= (1 / a.mass + 1 / b.mass);
        
        // Apply impulse
        double impulseX = impulseScalar * info.normalX;
        double impulseY = impulseScalar * info.normalY;
        
        if (!a.isStatic) {
            a.vx += impulseX / a.mass;
            a.vy += impulseY / a.mass;
            
            // Add slight rotation on impact
            a.angularVelocity += (Math.random() - 0.5) * 0.5;
        }
        
        if (!b.isStatic) {
            b.vx -= impulseX / b.mass;
            b.vy -= impulseY / b.mass;
            
            // Add slight rotation on impact
            b.angularVelocity += (Math.random() - 0.5) * 0.5;
        }
        
        // Positional correction (prevent sinking)
        double percent = 0.8; // Penetration percentage to correct
        double slop = 0.01; // Allowable penetration
        double correctionMagnitude = Math.max(info.penetrationDepth - slop, 0) / 
                                    (1 / a.mass + 1 / b.mass) * percent;
        
        double correctionX = correctionMagnitude * info.normalX;
        double correctionY = correctionMagnitude * info.normalY;
        
        if (!a.isStatic) {
            a.x += correctionX / a.mass;
            a.y += correctionY / a.mass;
        }
        
        if (!b.isStatic) {
            b.x -= correctionX / b.mass;
            b.y -= correctionY / b.mass;
        }
        
        // Update contact point
        info.contactX = (boundsA.getCenterX() + boundsB.getCenterX()) / 2;
        info.contactY = (boundsA.getCenterY() + boundsB.getCenterY()) / 2;
        
        return info;
    }
    
    /**
     * Adds a physics body to the simulation
     */
    public void addBody(PhysicsBody body) {
        bodies.add(body);
    }
    
    /**
     * Removes a physics body from the simulation
     */
    public void removeBody(PhysicsBody body) {
        bodies.remove(body);
    }
    
    /**
     * Clears all bodies
     */
    public void clearBodies() {
        bodies.clear();
    }
    
    /**
     * Adds a collision listener
     */
    public void addCollisionListener(CollisionListener listener) {
        collisionListeners.add(listener);
    }
    
    /**
     * Gets all active bodies
     */
    public List<PhysicsBody> getBodies() {
        return new ArrayList<>(bodies);
    }
    
    /**
     * Finds bodies within a region
     */
    public List<PhysicsBody> getBodiesInRegion(double x, double y, double width, double height) {
        Rectangle2D.Double region = new Rectangle2D.Double(x, y, width, height);
        List<PhysicsBody> result = new ArrayList<>();
        
        for (PhysicsBody body : bodies) {
            if (region.intersects(body.getBounds())) {
                result.add(body);
            }
        }
        
        return result;
    }
    
    /**
     * Applies explosion force to bodies
     */
    public void applyExplosion(double centerX, double centerY, double force, double radius) {
        for (PhysicsBody body : bodies) {
            if (body.isStatic) continue;
            
            double dx = body.x + body.width/2 - centerX;
            double dy = body.y + body.height/2 - centerY;
            double distance = Math.sqrt(dx * dx + dy * dy);
            
            if (distance < radius && distance > 0) {
                double falloff = 1.0 - (distance / radius);
                double impulse = force * falloff;
                
                // Normalize and apply
                dx /= distance;
                dy /= distance;
                
                body.applyImpulse(dx * impulse, dy * impulse);
                
                // Add rotation
                body.angularVelocity += (Math.random() - 0.5) * falloff * 5.0;
            }
        }
    }
    
    /**
     * Gets physics statistics
     */
    public String getStats() {
        return String.format("Bodies: %d | FPS Target: 60 | Gravity: %.0f px/s²", 
                            bodies.size(), GRAVITY);
    }
}
