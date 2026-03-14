package com.example.smartroutefinder



import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartroutefinder.ui.theme.SmartRouteFinderTheme
import java.util.PriorityQueue
import kotlin.system.exitProcess

// --- Data Models ---
data class Edge(val to: String, val cost: Int)

data class PathResult(
    val start: String,
    val end: String,
    val path: List<String>,
    val totalCost: Int,
    var isLiked: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

data class NodeState(val node: String, val cost: Int, val path: List<String>) : Comparable<NodeState> {
    override fun compareTo(other: NodeState): Int = this.cost.compareTo(other.cost)
}

val graph = mapOf(
    "A" to listOf(Edge("B", 4), Edge("C", 2)),
    "B" to listOf(Edge("D", 5), Edge("E", 10)),
    "C" to listOf(Edge("D", 3)),
    "D" to listOf(Edge("E", 2)),
    "E" to emptyList()
)

// --- Logic ---
fun uniformCostSearch(start: String, goal: String): PathResult? {
    val s = start.uppercase().trim()
    val g = goal.uppercase().trim()
    if (s.isEmpty() || g.isEmpty()) return null

    val priorityQueue = PriorityQueue<NodeState>()
    priorityQueue.add(NodeState(s, 0, listOf(s)))
    val visited = mutableSetOf<String>()

    while (priorityQueue.isNotEmpty()) {
        val current = priorityQueue.poll() ?: break
        if (current.node == g) return PathResult(s, g, current.path, current.cost)
        if (current.node !in visited) {
            visited.add(current.node)
            graph[current.node]?.forEach { edge ->
                if (edge.to !in visited) {
                    priorityQueue.add(NodeState(edge.to, current.cost + edge.cost, current.path + edge.to))
                }
            }
        }
    }
    return null
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmartRouteFinderTheme {
                var currentScreen by remember { mutableStateOf("home") }
                val historyList = remember { mutableStateListOf<PathResult>() }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (currentScreen == "finder") {
                            BottomAppBar(containerColor = Color.Transparent) {
                                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                    Button(
                                        onClick = { currentScreen = "history" },
                                        modifier = Modifier.width(200.dp).height(50.dp)
                                    ) {
                                        Text("NEXT", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        when (currentScreen) {
                            "home" -> HomeScreen { currentScreen = "finder" }
                            "finder" -> FinderScreen(
                                onRestart = { currentScreen = "home" },
                                onResultGenerated = { historyList.add(0, it) }
                            )
                            "history" -> HistoryScreen(
                                history = historyList,
                                onBack = { currentScreen = "finder" },
                                onNext = { currentScreen = "exit" },
                                onDelete = { historyList.remove(it) },
                                onToggleLike = { item ->
                                    val index = historyList.indexOf(item)
                                    if (index != -1) {
                                        historyList[index] = historyList[index].copy(isLiked = !item.isLiked)
                                    }
                                }
                            )
                            "exit" -> ExitScreen()
                        }
                    }
                }
            }
        }
    }
}

// --- Screen 1: Home ---
@Composable
fun HomeScreen(onStart: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.sai),
            contentDescription = "Logo",
            modifier = Modifier.size(100.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text("Smart Route Finder", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
        Text("Powered by Uniform Cost Search Algorithm", fontSize = 16.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Explore how AI finds the lowest-cost path\nthrough a weighted graph step by step.",
            fontSize = 14.sp, textAlign = TextAlign.Center, color = Color.Gray
        )
        Spacer(modifier = Modifier.height(24.dp))
        Column(horizontalAlignment = Alignment.Start) {
            Text("📊 Graph Visualization", fontSize = 16.sp)
            Text("🔎 Step-by-Step Search", fontSize = 16.sp)
            Text("⭐ Optimal Path Discovery", fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.height(40.dp))
        Button(onClick = onStart, modifier = Modifier.width(200.dp).height(50.dp)) {
            Text("START", fontWeight = FontWeight.Bold)
        }
    }
}

// --- Screen 2: Finder ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinderScreen(onRestart: () -> Unit, onResultGenerated: (PathResult) -> Unit) {
    val validNodes = listOf("A", "B", "C", "D", "E")
    var startNode by remember { mutableStateOf("") }
    var destNode by remember { mutableStateOf("") }
    var startExpanded by remember { mutableStateOf(false) }
    var destExpanded by remember { mutableStateOf(false) }

    var showPopup by remember { mutableStateOf(false) }
    var showErrorPopup by remember { mutableStateOf(false) }
    var lastResult by remember { mutableStateOf<PathResult?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        GraphVisualizer()
        Spacer(modifier = Modifier.height(20.dp))

        Text("Select or Enter Start Node:", modifier = Modifier.align(Alignment.Start))
        ExposedDropdownMenuBox(expanded = startExpanded, onExpandedChange = { startExpanded = it }) {
            OutlinedTextField(
                value = startNode,
                onValueChange = { startNode = it },
                placeholder = { Text("e.g. A") },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = startExpanded) }
            )
            ExposedDropdownMenu(expanded = startExpanded, onDismissRequest = { startExpanded = false }) {
                validNodes.forEach { DropdownMenuItem(text = { Text(it) }, onClick = { startNode = it; startExpanded = false }) }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text("Select or Enter Destination:", modifier = Modifier.align(Alignment.Start))
        ExposedDropdownMenuBox(expanded = destExpanded, onExpandedChange = { destExpanded = it }) {
            OutlinedTextField(
                value = destNode,
                onValueChange = { destNode = it },
                placeholder = { Text("e.g. E") },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = destExpanded) }
            )
            ExposedDropdownMenu(expanded = destExpanded, onDismissRequest = { destExpanded = false }) {
                validNodes.forEach { DropdownMenuItem(text = { Text(it) }, onClick = { destNode = it; destExpanded = false }) }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Button(modifier = Modifier.fillMaxWidth().height(55.dp), onClick = {
            val s = startNode.uppercase().trim()
            val d = destNode.uppercase().trim()

            if (s !in validNodes || d !in validNodes) {
                showErrorPopup = true
            } else {
                val res = uniformCostSearch(s, d)
                if (res != null) {
                    lastResult = res
                    onResultGenerated(res)
                    showPopup = true
                }
            }
        }) { Text("CALCULATE PATH") }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = onRestart,
                modifier = Modifier.weight(1f).height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
            ) { Text("RESTART") }
            Button(
                onClick = {
                    startNode = ""
                    destNode = ""
                },
                modifier = Modifier.weight(1f).height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) { Text("RESET") }
        }
    }

    if (showPopup && lastResult != null) {
        AlertDialog(
            onDismissRequest = { showPopup = false },
            title = { Text("Route Found!") },
            text = {
                Text("Start: ${lastResult?.start}\nEnd: ${lastResult?.end}\n\nPath: ${lastResult?.path?.joinToString(" → ")}\nTotal Cost: ${lastResult?.totalCost}", fontSize = 16.sp)
            },
            confirmButton = { Button(onClick = { showPopup = false }) { Text("OK") } }
        )
    }

    if (showErrorPopup) {
        AlertDialog(
            onDismissRequest = { showErrorPopup = false },
            title = { Text("Invalid Input") },
            text = { Text("Input can't be taken. Please enter valid nodes only (A, B, C, D, or E).") },
            confirmButton = { Button(onClick = { showErrorPopup = false }) { Text("Try Again") } }
        )
    }
}

// --- Screen 3: History ---
@Composable
fun HistoryScreen(
    history: List<PathResult>,
    onBack: () -> Unit,
    onNext: () -> Unit,
    onDelete: (PathResult) -> Unit,
    onToggleLike: (PathResult) -> Unit
) {
    var showOnlyLiked by remember { mutableStateOf(false) }
    val displayList = if (showOnlyLiked) history.filter { it.isLiked } else history

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text("ROUTE FINDING DETAILS", fontSize = 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { showOnlyLiked = !showOnlyLiked },
            modifier = Modifier.align(Alignment.CenterHorizontally),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (showOnlyLiked) Color(0xFFFFB74D) else MaterialTheme.colorScheme.surfaceVariant,
                contentColor = if (showOnlyLiked) Color.White else Color.Black
            )
        ) {
            Icon(if (showOnlyLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(if (showOnlyLiked) "Showing Liked Routes" else "Show All Routes")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (displayList.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(if (showOnlyLiked) "No liked routes yet." else "No history available.", color = Color.Gray)
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(displayList) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                        elevation = CardDefaults.cardElevation(4.dp),
                        colors = CardDefaults.cardColors(containerColor = if (item.isLiked) Color(0xFFFFF8E1) else MaterialTheme.colorScheme.surface)
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Route: ${item.start} to ${item.end}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("Path: ${item.path.joinToString(" → ")}", fontSize = 14.sp)
                                Text("Total Cost: ${item.totalCost}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            }
                            IconButton(onClick = { onToggleLike(item) }) {
                                Icon(imageVector = if (item.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder, contentDescription = "Like", tint = if (item.isLiked) Color.Red else Color.Gray)
                            }
                            IconButton(onClick = { onDelete(item) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onBack, modifier = Modifier.fillMaxWidth().height(50.dp)) { Text("BACK TO FINDER") }
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("FINISH", fontWeight = FontWeight.Bold)
        }
    }
}

// --- Screen 4: Exit ---
@Composable
fun ExitScreen() {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Pushes content below to center
        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "Team Hacksphere ⚡ ",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { exitProcess(0) },
            modifier = Modifier.width(200.dp).height(55.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)) // Standard Blue
        ) {
            Text("BYE", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
        }

        // Pushes content above to center
        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "MADE FOR AI",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            color = Color.LightGray,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun GraphVisualizer() {
    Box(modifier = Modifier.fillMaxWidth().height(200.dp).background(Color(0xFFF0F0F0), shape = MaterialTheme.shapes.medium).padding(10.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val pos = mapOf("A" to Offset(80f, 250f), "B" to Offset(250f, 100f), "C" to Offset(250f, 400f), "D" to Offset(450f, 250f), "E" to Offset(650f, 250f))
            val paint = Paint().apply { color = android.graphics.Color.BLACK; textSize = 34f; textAlign = Paint.Align.CENTER; typeface = Typeface.DEFAULT_BOLD }
            graph.forEach { (from, edges) ->
                edges.forEach { edge ->
                    val start = pos[from]!!
                    val end = pos[edge.to]!!
                    drawLine(color = Color.Gray, start = start, end = end, strokeWidth = 4f)
                    drawContext.canvas.nativeCanvas.drawText("${edge.cost}", (start.x + end.x) / 2, (start.y + end.y) / 2 - 10, paint.apply { color = android.graphics.Color.DKGRAY })
                }
            }
            pos.forEach { (label, offset) ->
                drawCircle(color = Color(0xFF6200EE), radius = 30f, center = offset)
                drawContext.canvas.nativeCanvas.drawText(label, offset.x, offset.y + 12f, paint.apply { color = android.graphics.Color.WHITE })
            }
        }
    }
}
