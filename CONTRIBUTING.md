# Guía de Contribución - Tower Bloxx Java

¡Gracias por tu interés en contribuir a Tower Bloxx Java! Esta guía te ayudará a empezar.

## 🚀 Primeros Pasos

### Configuración del Entorno

1. **Fork** el repositorio en GitHub
2. **Clona** tu fork localmente:
   ```bash
   git clone https://github.com/tu-usuario/bloqueTorres.git
   cd bloqueTorres
   ```
3. **Configura** el repositorio upstream:
   ```bash
   git remote add upstream https://github.com/skillparty/bloqueTorres.git
   ```
4. **Verifica** que tienes las herramientas necesarias:
   ```bash
   java -version  # Java 11+
   mvn -version   # Maven 3.6+
   ```

### Compilación y Pruebas

```bash
# Compilar el proyecto
mvn clean compile

# Ejecutar todas las pruebas
mvn test

# Ejecutar el juego
mvn exec:java -Dexec.mainClass="com.skillparty.towerblox.Main"
```

## 📋 Tipos de Contribuciones

### 🐛 Reportar Bugs

Antes de reportar un bug:
- Busca en los [issues existentes](https://github.com/skillparty/bloqueTorres/issues)
- Verifica que puedes reproducir el problema
- Prueba con la última versión

**Template para reportar bugs:**
```markdown
**Descripción del Bug**
Una descripción clara y concisa del problema.

**Pasos para Reproducir**
1. Ve a '...'
2. Haz clic en '...'
3. Observa el error

**Comportamiento Esperado**
Qué esperabas que pasara.

**Screenshots**
Si es aplicable, añade screenshots.

**Información del Sistema:**
- OS: [e.g. Windows 10, macOS 12, Ubuntu 20.04]
- Java Version: [e.g. OpenJDK 11.0.2]
- Maven Version: [e.g. 3.8.1]
```

### ✨ Sugerir Mejoras

Para sugerir nuevas características:
- Abre un issue con la etiqueta "enhancement"
- Describe claramente la funcionalidad propuesta
- Explica por qué sería útil
- Considera la complejidad de implementación

### 🔧 Contribuir Código

#### Flujo de Trabajo

1. **Crea una rama** para tu feature:
   ```bash
   git checkout -b feature/nueva-caracteristica
   ```

2. **Haz tus cambios** siguiendo las guías de estilo

3. **Añade pruebas** para tu código:
   ```bash
   # Ejecutar pruebas específicas
   mvn test -Dtest=TuNuevaClaseTest
   ```

4. **Commit** tus cambios:
   ```bash
   git add .
   git commit -m "feat: añadir nueva característica"
   ```

5. **Push** a tu fork:
   ```bash
   git push origin feature/nueva-caracteristica
   ```

6. **Abre un Pull Request** en GitHub

#### Convenciones de Commit

Usamos [Conventional Commits](https://www.conventionalcommits.org/):

- `feat:` nueva funcionalidad
- `fix:` corrección de bug
- `docs:` cambios en documentación
- `style:` formateo, punto y coma faltante, etc.
- `refactor:` refactoring de código
- `test:` añadir o modificar pruebas
- `chore:` tareas de mantenimiento

**Ejemplos:**
```bash
git commit -m "feat: añadir animación de explosión al game over"
git commit -m "fix: corregir colisión de bloques en bordes"
git commit -m "docs: actualizar README con nuevos controles"
```

## 🎨 Guías de Estilo

### Código Java

#### Nomenclatura
- **Clases**: PascalCase (`GameEngine`, `ScoreManager`)
- **Métodos**: camelCase (`updateScore`, `checkCollision`)
- **Variables**: camelCase (`currentScore`, `isGameOver`)
- **Constantes**: UPPER_SNAKE_CASE (`MAX_BLOCKS`, `GAME_WIDTH`)

#### Estructura de Clases
```java
package com.skillparty.towerblox.categoria;

import java.util.List;
import com.skillparty.towerblox.other.Class;

/**
 * Descripción clara de la clase
 */
public class MiClase {
    // Constantes primero
    private static final int CONSTANT_VALUE = 100;
    
    // Variables de instancia
    private int instanceVariable;
    
    // Constructor
    public MiClase(int value) {
        this.instanceVariable = value;
    }
    
    // Métodos públicos
    public void publicMethod() {
        // implementación
    }
    
    // Métodos privados al final
    private void privateMethod() {
        // implementación
    }
}
```

#### Documentación
- Usa JavaDoc para clases y métodos públicos
- Comenta código complejo
- Explica el "por qué", no solo el "qué"

```java
/**
 * Calcula la puntuación basada en la alineación del bloque
 * 
 * @param block El bloque que se está evaluando
 * @param blockBelow El bloque de referencia debajo
 * @return Puntuación entre 0 y 1000 puntos
 */
public int calculateAlignmentScore(Block block, Block blockBelow) {
    // La alineación perfecta otorga puntos bonus
    if (isPerfectAlignment(block, blockBelow)) {
        return BASE_SCORE + PERFECT_BONUS;
    }
    // ... resto de la lógica
}
```

### Pruebas

#### Estructura de Pruebas
```java
public class MiClaseTest {
    private MiClase miClase;
    
    @Before
    public void setUp() {
        miClase = new MiClase(100);
    }
    
    @Test
    public void testMetodoBasico() {
        // Given
        int expectedValue = 200;
        
        // When
        int result = miClase.calculate(100);
        
        // Then
        assertEquals(expectedValue, result);
    }
    
    @Test
    public void testCasoLimite() {
        // Prueba casos extremos
    }
}
```

#### Cobertura de Pruebas
- Apunta a 80%+ de cobertura
- Prueba casos felices y casos límite
- Incluye pruebas de integración para flujos completos

## 🏗️ Arquitectura del Proyecto

### Principios de Diseño

1. **Separación de Responsabilidades**: Cada clase tiene una responsabilidad clara
2. **Bajo Acoplamiento**: Las clases dependen de abstracciones, no de implementaciones
3. **Alta Cohesión**: Los elementos relacionados están agrupados
4. **Principio DRY**: No repetir código

### Estructura de Paquetes

```
com.skillparty.towerblox/
├── game/           # Lógica del juego
│   └── physics/    # Sistema de física
├── ui/             # Interfaz de usuario
│   └── components/ # Componentes reutilizables
├── score/          # Sistema de puntuaciones
└── utils/          # Utilidades generales
```

### Patrones Utilizados

- **MVC**: Separación entre modelo, vista y controlador
- **Observer**: Para eventos del juego
- **Singleton**: Para managers globales
- **State Machine**: Para estados del juego

## 🧪 Testing

### Tipos de Pruebas

1. **Unitarias**: Prueban clases individuales
2. **Integración**: Prueban interacción entre componentes
3. **End-to-End**: Prueban flujos completos del usuario

### Ejecutar Pruebas

```bash
# Todas las pruebas
mvn test

# Pruebas específicas
mvn test -Dtest=BlockTest

# Con cobertura
mvn test jacoco:report
```

### Escribir Buenas Pruebas

- **Nombres descriptivos**: `testBlockFallsWithGravity()`
- **Arrange-Act-Assert**: Estructura clara
- **Una aserción por test**: Fácil de debuggear
- **Datos de prueba**: Usa builders o factories

## 📦 Releases

### Versionado

Seguimos [Semantic Versioning](https://semver.org/):
- **MAJOR**: Cambios incompatibles
- **MINOR**: Nueva funcionalidad compatible
- **PATCH**: Correcciones de bugs

### Proceso de Release

1. Actualizar versión en `pom.xml`
2. Actualizar `CHANGELOG.md`
3. Crear tag: `git tag v1.2.3`
4. Push tag: `git push origin v1.2.3`
5. Crear release en GitHub

## ❓ Preguntas Frecuentes

### ¿Cómo añado una nueva funcionalidad?

1. Abre un issue para discutir la idea
2. Espera feedback del equipo
3. Implementa siguiendo las guías
4. Añade pruebas
5. Abre un Pull Request

### ¿Cómo reporto un problema de seguridad?

Para problemas de seguridad, envía un email privado en lugar de abrir un issue público.

### ¿Puedo contribuir sin saber Java?

¡Sí! Puedes contribuir con:
- Documentación
- Traducciones
- Reportar bugs
- Sugerir mejoras
- Diseño gráfico

## 🤝 Código de Conducta

### Nuestro Compromiso

Nos comprometemos a hacer de la participación en nuestro proyecto una experiencia libre de acoso para todos.

### Estándares

Ejemplos de comportamiento que contribuye a crear un ambiente positivo:
- Usar lenguaje acogedor e inclusivo
- Respetar diferentes puntos de vista
- Aceptar críticas constructivas
- Enfocarse en lo que es mejor para la comunidad

### Aplicación

Los casos de comportamiento abusivo pueden ser reportados contactando al equipo del proyecto.

## 📞 Contacto

- **Issues**: [GitHub Issues](https://github.com/skillparty/bloqueTorres/issues)
- **Discussions**: [GitHub Discussions](https://github.com/skillparty/bloqueTorres/discussions)
- **Email**: skillparty@example.com

---

¡Gracias por contribuir a Tower Bloxx Java! 🎮