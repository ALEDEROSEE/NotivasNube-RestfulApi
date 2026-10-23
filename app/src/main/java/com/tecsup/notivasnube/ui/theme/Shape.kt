package com.tecsup.notivasnube.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Formas de NOTIVas: esquinas claramente redondeadas. La app muestra muchas
// tarjetas de curso una debajo de otra y el radio alto ayuda a separarlas
// visualmente sin tener que pintar bordes.
val NotivasShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(36.dp)
)
