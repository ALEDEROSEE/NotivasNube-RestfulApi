package com.tecsup.notivasnube.ui.theme

import androidx.compose.ui.graphics.Color

// Esquema de color de NOTIVas.
// Semilla: #1B5EA8 (azul). El azul transmite orden y lectura tranquila, que es
// lo que quiero para una app de notas académicas; el acento violeta (tertiary)
// se usa solo para los estados "en riesgo".

// --- Claro ---
val primaryLight = Color(0xFF1B5EA8)
val onPrimaryLight = Color(0xFFFFFFFF)
val primaryContainerLight = Color(0xFFD3E4FF)
val onPrimaryContainerLight = Color(0xFF001C39)

val secondaryLight = Color(0xFF545F71)
val onSecondaryLight = Color(0xFFFFFFFF)
val secondaryContainerLight = Color(0xFFD8E3F8)
val onSecondaryContainerLight = Color(0xFF111C2B)

val tertiaryLight = Color(0xFF6D5676)
val onTertiaryLight = Color(0xFFFFFFFF)
val tertiaryContainerLight = Color(0xFFF6D9FF)
val onTertiaryContainerLight = Color(0xFF27132F)

val errorLight = Color(0xFFBA1A1A)
val onErrorLight = Color(0xFFFFFFFF)
val errorContainerLight = Color(0xFFFFDAD6)
val onErrorContainerLight = Color(0xFF410002)

val backgroundLight = Color(0xFFFDFBFF)
val onBackgroundLight = Color(0xFF1A1C1E)
val surfaceLight = Color(0xFFFDFBFF)
val onSurfaceLight = Color(0xFF1A1C1E)
val surfaceVariantLight = Color(0xFFDFE2EB)
val onSurfaceVariantLight = Color(0xFF43474E)
val outlineLight = Color(0xFF73777F)

// --- Oscuro ---
val primaryDark = Color(0xFFA2C9FF)
val onPrimaryDark = Color(0xFF00325B)
val primaryContainerDark = Color(0xFF004880)
val onPrimaryContainerDark = Color(0xFFD3E4FF)

val secondaryDark = Color(0xFFBCC7DC)
val onSecondaryDark = Color(0xFF263141)
val secondaryContainerDark = Color(0xFF3C4758)
val onSecondaryContainerDark = Color(0xFFD8E3F8)

val tertiaryDark = Color(0xFFDABDE2)
val onTertiaryDark = Color(0xFF3D2946)
val tertiaryContainerDark = Color(0xFF543F5D)
val onTertiaryContainerDark = Color(0xFFF6D9FF)

val errorDark = Color(0xFFFFB4AB)
val onErrorDark = Color(0xFF690005)
val errorContainerDark = Color(0xFF93000A)
val onErrorContainerDark = Color(0xFFFFDAD6)

val backgroundDark = Color(0xFF1A1C1E)
val onBackgroundDark = Color(0xFFE2E2E6)
val surfaceDark = Color(0xFF1A1C1E)
val onSurfaceDark = Color(0xFFE2E2E6)
val surfaceVariantDark = Color(0xFF43474E)
val onSurfaceVariantDark = Color(0xFFC3C7CF)
val outlineDark = Color(0xFF8D9199)

// Colores de apoyo para los semáforos de nota (no forman parte del ColorScheme,
// los uso como tokens propios en las tarjetas de curso).
val notaAprobada = Color(0xFF2E7D32)
val notaEnRiesgo = Color(0xFFB26A00)
val notaDesaprobada = Color(0xFFBA1A1A)
