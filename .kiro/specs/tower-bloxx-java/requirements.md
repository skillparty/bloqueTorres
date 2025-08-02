# Requirements Document

## Introduction

Este proyecto consiste en desarrollar una versión mejorada del juego Tower Bloxx en Java. El juego incluirá un sistema de menús con múltiples dificultades, gráficos realistas para la garra, tipografía Ubuntu Monospace, un logo estilo terminal de Linux, y un sistema de puntuaciones con nicknames de 3 caracteres. El objetivo es crear una experiencia de juego fluida y atractiva que mantenga la esencia del Tower Bloxx original mientras añade mejoras visuales y de jugabilidad.

## Requirements

### Requirement 1

**User Story:** Como jugador, quiero acceder a un menú de inicio con opciones de dificultad, para poder elegir el nivel de desafío que prefiera.

#### Acceptance Criteria

1. WHEN el juego se inicia THEN el sistema SHALL mostrar un menú principal con las opciones: Fácil, Normal, Difícil
2. WHEN el jugador selecciona una dificultad THEN el sistema SHALL iniciar el juego con la velocidad correspondiente a esa dificultad
3. WHEN el jugador está en el menú THEN el sistema SHALL mostrar el logo del juego en estilo terminal de Linux
4. IF el jugador selecciona "Fácil" THEN el sistema SHALL configurar la velocidad del juego al 70% de la velocidad base
5. IF el jugador selecciona "Normal" THEN el sistema SHALL configurar la velocidad del juego al 100% de la velocidad base
6. IF el jugador selecciona "Difícil" THEN el sistema SHALL configurar la velocidad del juego al 130% de la velocidad base

### Requirement 2

**User Story:** Como jugador, quiero que el juego use la tipografía Ubuntu Monospace, para tener una experiencia visual consistente y moderna.

#### Acceptance Criteria

1. WHEN el juego se ejecuta THEN el sistema SHALL cargar y usar la fuente Ubuntu Monospace para todos los textos
2. IF la fuente Ubuntu Monospace no está disponible THEN el sistema SHALL usar una fuente monospace alternativa
3. WHEN se muestra cualquier texto en pantalla THEN el sistema SHALL aplicar la tipografía Ubuntu Monospace

### Requirement 3

**User Story:** Como jugador, quiero ver un logo estilo terminal de Linux, para tener una experiencia visual única y reconocible.

#### Acceptance Criteria

1. WHEN el menú principal se muestra THEN el sistema SHALL mostrar el logo del juego usando caracteres ASCII
2. WHEN el logo se renderiza THEN el sistema SHALL usar colores y estilo similar a las terminales de Linux
3. WHEN el logo aparece THEN el sistema SHALL incluir el nombre "Tower Bloxx" en formato ASCII art

### Requirement 4

**User Story:** Como jugador, quiero que la garra se abra de forma realista al soltar bloques, para tener una experiencia más inmersiva.

#### Acceptance Criteria

1. WHEN el jugador suelta un bloque THEN el sistema SHALL animar la garra abriéndose gradualmente
2. WHEN la garra se abre THEN el sistema SHALL mostrar una animación fluida de apertura en múltiples frames
3. WHEN la animación de apertura termina THEN el sistema SHALL cerrar la garra gradualmente
4. WHEN el bloque es soltado THEN el sistema SHALL sincronizar la apertura de la garra con la liberación del bloque

### Requirement 5

**User Story:** Como jugador, quiero acceder a un tablero de puntuaciones donde pueda ingresar mi nickname de 3 caracteres, para competir y registrar mis logros.

#### Acceptance Criteria

1. WHEN el juego termina THEN el sistema SHALL mostrar la pantalla de puntuaciones si el puntaje califica para el top
2. WHEN el jugador ingresa su nickname THEN el sistema SHALL limitar la entrada a exactamente 3 caracteres
3. WHEN el jugador confirma su nickname THEN el sistema SHALL guardar el puntaje con el nickname en el tablero
4. WHEN se muestra el tablero THEN el sistema SHALL mostrar los top 10 puntajes con sus respectivos nicknames
5. WHEN el tablero se muestra THEN el sistema SHALL ordenar los puntajes de mayor a menor
6. IF el jugador intenta ingresar más de 3 caracteres THEN el sistema SHALL rechazar los caracteres adicionales

### Requirement 6

**User Story:** Como jugador, quiero que el juego tenga mecánicas de Tower Bloxx funcionales, para poder disfrutar del gameplay clásico.

#### Acceptance Criteria

1. WHEN el juego inicia THEN el sistema SHALL mostrar una garra que se mueve horizontalmente
2. WHEN el jugador presiona la tecla de acción THEN el sistema SHALL soltar el bloque actual
3. WHEN un bloque es soltado THEN el sistema SHALL aplicar física realista para que caiga
4. WHEN un bloque aterriza THEN el sistema SHALL verificar la alineación y estabilidad de la torre
5. WHEN los bloques están mal alineados THEN el sistema SHALL reducir la estabilidad de la torre
6. WHEN la torre se vuelve inestable THEN el sistema SHALL terminar el juego
7. WHEN un bloque se coloca perfectamente THEN el sistema SHALL otorgar puntos bonus

### Requirement 7

**User Story:** Como desarrollador, quiero que el código esté preparado para subir al repositorio especificado, para mantener el control de versiones del proyecto.

#### Acceptance Criteria

1. WHEN el proyecto se desarrolla THEN el sistema SHALL estructurar el código para ser compatible con Git
2. WHEN el código esté listo THEN el sistema SHALL incluir un archivo README con instrucciones de instalación
3. WHEN se prepare el repositorio THEN el sistema SHALL incluir un .gitignore apropiado para proyectos Java
4. WHEN el proyecto esté completo THEN el sistema SHALL estar listo para subir a https://github.com/skillparty/bloqueTorres