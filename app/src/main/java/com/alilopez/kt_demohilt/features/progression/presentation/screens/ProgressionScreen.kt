package com.alilopez.kt_demohilt.features.progression.presentation.screens

import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingFlat
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    var showEditTargetDialog by remember { mutableStateOf(false) }

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
                            SummaryCard(
                                summary = summary,
                                accentColor = accentColor,
                                isDarkTheme = isDarkTheme,
                                onEditTargetWeight = { showEditTargetDialog = true }
                            )
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

    if (showEditTargetDialog) {
        EditTargetWeightDialog(
            currentTarget = uiState.summary?.targetWeight ?: 0f,
            onDismiss = { showEditTargetDialog = false },
            onConfirm = { newTarget ->
                viewModel.updateTargetWeight(newTarget)
                showEditTargetDialog = false
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
    isDarkTheme: Boolean,
    onEditTargetWeight: () -> Unit = {}
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text("Objetivo", color = Color.Gray, fontSize = 12.sp)
                        Text("${summary.targetWeight} kg", fontWeight = FontWeight.Bold, color = textColor)
                    }
                    IconButton(
                        onClick = onEditTargetWeight,
                        modifier = Modifier.size(32.dp).padding(start = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar peso objetivo",
                            tint = accentColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }
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
fun EditTargetWeightDialog(
    currentTarget: Float,
    onDismiss: () -> Unit,
    onConfirm: (Float) -> Unit
) {
    var weightText by remember { mutableStateOf(if (currentTarget > 0f) currentTarget.toString() else "") }
    val accentColor = Color(0xFF10B981)
    val isDarkTheme = androidx.compose.foundation.isSystemInDarkTheme()
    val cardBg = if (isDarkTheme) Color(0xFF1E293B) else Color.White
    val textColor = if (isDarkTheme) Color.White else Color(0xFF0F172A)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = cardBg,
        title = {
            Text(
                "Peso Objetivo",
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        },
        text = {
            Column {
                Text("Ingresa tu nuevo peso objetivo en kg:", color = Color.Gray, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = weightText,
                    onValueChange = { weightText = it },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = accentColor,
                        unfocusedBorderColor = Color.Gray.copy(alpha = 0.4f),
                        focusedTextColor = textColor,
                        unfocusedTextColor = textColor,
                        focusedLabelColor = accentColor,
                        cursorColor = accentColor
                    ),
                    label = { Text("Peso (kg)") },
                    placeholder = { Text("Ej. 70.5", color = Color.Gray) }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { weightText.toFloatOrNull()?.let { onConfirm(it) } },
                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                enabled = weightText.toFloatOrNull() != null
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = Color.Gray)
            }
        }
    )
}

@Composable
fun AddWeightDialog(onDismiss: () -> Unit, onConfirm: (Float) -> Unit) {
    var weightText by remember { mutableStateOf("") }
    val accentColor = Color(0xFF10B981)
    val isDarkTheme = androidx.compose.foundation.isSystemInDarkTheme()
    val cardBg = if (isDarkTheme) Color(0xFF1E293B) else Color.White
    val textColor = if (isDarkTheme) Color.White else Color(0xFF0F172A)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = cardBg,
        title = {
            Text("Registrar Peso", fontWeight = FontWeight.Bold, color = textColor)
        },
        text = {
            Column {
                Text("Ingresa tu peso actual en kg:", color = Color.Gray, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = weightText,
                    onValueChange = { weightText = it },
                    label = { Text("Peso (kg)") },
                    placeholder = { Text("Ej. 70.5", color = Color.Gray) },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = accentColor,
                        unfocusedBorderColor = Color.Gray.copy(alpha = 0.4f),
                        focusedTextColor = textColor,
                        unfocusedTextColor = textColor,
                        focusedLabelColor = accentColor,
                        cursorColor = accentColor
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { weightText.toFloatOrNull()?.let { onConfirm(it) } },
                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                enabled = weightText.toFloatOrNull() != null
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = Color.Gray)
            }
        }
    )
}
