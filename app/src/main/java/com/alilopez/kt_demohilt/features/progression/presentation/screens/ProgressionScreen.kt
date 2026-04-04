package com.alilopez.kt_demohilt.features.progression.presentation.screens

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingFlat
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alilopez.kt_demohilt.features.progression.domain.entities.ProgressionEntry
import com.alilopez.kt_demohilt.features.progression.presentation.viewmodels.ProgressionViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressionScreen(
    onOpenDrawer: () -> Unit,
    viewModel: ProgressionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isDarkTheme = isSystemInDarkTheme()
    var showAddWeightDialog by remember { mutableStateOf(false) }

    val backgroundColor = if (isDarkTheme) Color(0xFF0F172A) else Color(0xFFF8FAFC)
    val accentColor = Color(0xFF10B981)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Progreso", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Menú")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddWeightDialog = true },
                containerColor = accentColor,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Registrar Peso")
            }
        },
        containerColor = backgroundColor
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (uiState.isLoading && uiState.summary == null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = accentColor)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 80.dp, top = 16.dp)
                ) {
                    uiState.summary?.let { summary ->
                        item {
                            SummaryCard(summary = summary, accentColor = accentColor, isDarkTheme = isDarkTheme)
                        }
                    }

                    item {
                        Text(
                            text = "Análisis de Peso",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkTheme) Color.White else Color.Black
                        )
                    }

                    item {
                        ChartCard(history = uiState.history, accentColor = accentColor, isDarkTheme = isDarkTheme)
                    }

                    item {
                        Text(
                            text = "Historial",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkTheme) Color.White else Color.Black
                        )
                    }

                    if (uiState.history.isEmpty()) {
                        item {
                            Text(
                                text = "Aún no tienes registros de peso.",
                                color = Color.Gray,
                                modifier = Modifier.padding(vertical = 16.dp)
                            )
                        }
                    } else {
                        items(uiState.history.reversed()) { entry ->
                            ProgressionEntryItem(entry = entry, isDarkTheme = isDarkTheme)
                        }
                    }
                }
            }
        }
    }

    if (showAddWeightDialog) {
        AddWeightDialog(
            onDismiss = { showAddWeightDialog = false },
            onConfirm = { weight ->
                viewModel.addWeightEntry(weight)
                showAddWeightDialog = false
            }
        )
    }
}

@Composable
fun ChartCard(
    history: List<ProgressionEntry>,
    accentColor: Color,
    isDarkTheme: Boolean
) {
    val cardBg = if (isDarkTheme) Color(0xFF1E293B) else Color.White
    val textColor = if (isDarkTheme) Color.White else Color.Black
    
    Card(
        modifier = Modifier.fillMaxWidth().height(250.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            if (history.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Sin datos suficientes", color = Color.Gray)
                }
            } else {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { context ->
                        LineChart(context).apply {
                            description.isEnabled = false
                            setTouchEnabled(true)
                            isDragEnabled = true
                            setScaleEnabled(true)
                            setPinchZoom(true)
                            setDrawGridBackground(false)
                            
                            xAxis.apply {
                                position = XAxis.XAxisPosition.BOTTOM
                                setDrawGridLines(false)
                                this.textColor = textColor.toArgb()
                            }
                            
                            axisLeft.apply {
                                setDrawGridLines(true)
                                gridColor = Color.Gray.copy(alpha = 0.2f).toArgb()
                                this.textColor = textColor.toArgb()
                            }
                            
                            axisRight.isEnabled = false
                            legend.isEnabled = false
                        }
                    },
                    update = { chart ->
                        val entries = history.mapIndexed { index, entry ->
                            Entry(index.toFloat(), entry.weight)
                        }
                        
                        val dataSet = LineDataSet(entries, "Peso").apply {
                            color = accentColor.toArgb()
                            valueTextColor = textColor.toArgb()
                            lineWidth = 3f
                            setDrawCircles(true)
                            setCircleColor(accentColor.toArgb())
                            circleRadius = 5f
                            setDrawCircleHole(true)
                            circleHoleColor = cardBg.toArgb()
                            mode = LineDataSet.Mode.CUBIC_BEZIER
                            setDrawFilled(true)
                            fillColor = accentColor.toArgb()
                            fillAlpha = 40
                        }
                        
                        chart.data = LineData(dataSet)
                        chart.invalidate()
                    }
                )
            }
        }
    }
}

@Composable
fun SummaryCard(
    summary: com.alilopez.kt_demohilt.features.progression.domain.entities.ProgressionSummary,
    accentColor: Color,
    isDarkTheme: Boolean
) {
    val cardBg = if (isDarkTheme) Color(0xFF1E293B) else Color.White
    val textColor = if (isDarkTheme) Color.White else Color.Black

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Peso Actual", color = Color.Gray, fontSize = 14.sp)
                    Text("${summary.currentWeight} kg", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = textColor)
                }
                TrendBadge(trend = summary.trend)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Objetivo", color = Color.Gray, fontSize = 12.sp)
                    Text("${summary.targetWeight} kg", fontWeight = FontWeight.Bold, color = textColor)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Cambio", color = Color.Gray, fontSize = 12.sp)
                    val prefix = if (summary.weightChange > 0) "+" else ""
                    Text("$prefix${summary.weightChange} kg", fontWeight = FontWeight.Bold, color = if (summary.weightChange <= 0) accentColor else Color.Red)
                }
            }
        }
    }
}

@Composable
fun TrendBadge(trend: String) {
    val (color, text, icon) = when (trend.lowercase()) {
        "mejorando" -> Triple(Color(0xFF10B981), "Mejorando", Icons.AutoMirrored.Filled.TrendingDown)
        "empeorando" -> Triple(Color(0xFFEF4444), "Empeorando", Icons.AutoMirrored.Filled.TrendingUp)
        else -> Triple(Color(0xFF64748B), "Sin cambios", Icons.AutoMirrored.Filled.TrendingFlat)
    }

    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            Text(text = text, color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ProgressionEntryItem(entry: ProgressionEntry, isDarkTheme: Boolean) {
    val textColor = if (isDarkTheme) Color.White else Color.Black
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = entry.date.split("T").firstOrNull() ?: entry.date,
                color = textColor,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = entry.date.split("T").lastOrNull()?.take(5) ?: "",
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
        Text(
            text = "${entry.weight} kg",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
    HorizontalDivider(color = Color.Gray.copy(alpha = 0.2f))
}

@Composable
fun AddWeightDialog(onDismiss: () -> Unit, onConfirm: (Float) -> Unit) {
    var weightText by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Registrar Peso") },
        text = {
            Column {
                Text("Ingresa tu peso actual en kg:")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = weightText,
                    onValueChange = { weightText = it },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { weightText.toFloatOrNull()?.let { onConfirm(it) } },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
