package com.example.animalazy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.animalazy.ui.theme.AnimaLazyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AnimaLazyTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AnimaLazyScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

// Modelo de datos
data class AnimalItem(
    val id: Int,
    val name: String,
    val categoryId: Int,
    val shortDescription: String,
    val detailedText: String
)

@Composable
fun AnimaLazyScreen(modifier: Modifier = Modifier) {
    val categories = remember { listOf("Todos", "Mamíferos", "Aves", "Reptiles") }
    var selectedCategoryIndex by remember { mutableIntStateOf(0) }

    val animalList = remember {
        mutableStateListOf(
            AnimalItem(1, "León", 1, "Rey de la selva", "Los leones son grandes felinos sociales que viven en manadas compuestas por hembras emparentadas, sus crías y un número reducido de machos adultos."),
            AnimalItem(2, "Águila Real", 2, "Ave de presa majestuosa", "Posee una vista extraordinariamente aguda que le permite detectar presas a varios kilómetros de distancia mientras planea en las alturas."),
            AnimalItem(3, "Camaleón", 3, "Maestro del camuflaje", "Son famosos por su capacidad para cambiar de color, sus ojos de movimiento independiente y su lengua pegajosa y rápida."),
            AnimalItem(4, "Delfín", 1, "Mamífero marino inteligente", "Utilizan la ecolocalización para navegar y cazar, además de comunicarse mediante una compleja variedad de silbidos y chasquidos."),
            AnimalItem(5, "Búho Real", 2, "Cazador nocturno silencioso", "Sus plumas especiales amortiguan el sonido del aleteo, permitiéndole abalanzarse sobre sus presas en total silencio.")
        )
    }

    val filteredList = if (selectedCategoryIndex == 0) {
        animalList
    } else {
        animalList.filter { it.categoryId == selectedCategoryIndex }
    }

    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = "Galería de Animales",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )

        // 1. LAZYROW: Transición animada de color
        Text(
            text = "Filtro por categoría:",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories.size) { index ->
                CategoryChip(
                    categoryName = categories[index],
                    isSelected = index == selectedCategoryIndex,
                    onSelect = { selectedCategoryIndex = index }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 2. LAZYCOLUMN: Cards expandibles
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = filteredList,
                key = { item -> item.id }
            ) { animal ->
                ExpandableAnimalCard(
                    modifier = Modifier.animateItem(
                        fadeInSpec = tween(300),
                        fadeOutSpec = tween(300),
                        placementSpec = spring(stiffness = Spring.StiffnessLow)
                    ),
                    animal = animal,
                    onDelete = { animalList.remove(animal) }
                )
            }
        }
    }
}

/**
 * Chip con animación de cambio de color (sin ningún icono)
 */
@Composable
fun CategoryChip(
    categoryName: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
        animationSpec = tween(durationMillis = 300),
        label = "chipBgColor"
    )

    val contentColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(durationMillis = 300),
        label = "chipTextColor"
    )

    val elevation by animateDpAsState(
        targetValue = if (isSelected) 6.dp else 1.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "chipElevation"
    )

    Surface(
        shadowElevation = elevation,
        shape = RoundedCornerShape(20.dp),
        color = backgroundColor,
        modifier = Modifier.clickable { onSelect() }
    ) {
        Text(
            text = categoryName,
            color = contentColor,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

/**
 * Card expandible usando un indicador de texto "▼" con rotación en vez de icono
 */
@Composable
fun ExpandableAnimalCard(
    modifier: Modifier = Modifier,
    animal: AnimalItem,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    // Animación de rotación para la flecha de texto "▼" (0° a 180°)
    val arrowRotationDegree by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "arrowRotation"
    )

    val cardElevation by animateDpAsState(
        targetValue = if (expanded) 8.dp else 2.dp,
        animationSpec = tween(durationMillis = 200),
        label = "cardElevation"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = cardElevation)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = animal.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = animal.shortDescription,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Botón de texto "X" estilizado para eliminar
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f))
                        .clickable { onDelete() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✕",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Botón con carácter "▼" que rota 180° con animación
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { expanded = !expanded },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "▼",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.rotate(arrowRotationDegree)
                    )
                }
            }

            // Animación para mostrar/ocultar el texto detallado
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn(animationSpec = tween(300)),
                exit = fadeOut(animationSpec = tween(200)) + shrinkVertically()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = animal.detailedText,
                        style = MaterialTheme.typography.bodySmall,
                        lineHeight = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AnimaLazyPreview() {
    AnimaLazyTheme {
        AnimaLazyScreen()
    }
}
