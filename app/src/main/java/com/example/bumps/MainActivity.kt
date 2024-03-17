package com.example.bumps

//import Golfer
//import Hole
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.bumps.ui.theme.BumpsTheme

data class Golfer(
    val name: String,
    val bumps: Int
)

data class Hole(
    val number: Int,
    val difficulty: Int
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BumpsTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    AppContent()
                }
            }
        }
    }
}

@Composable
fun AppContent() {
    var showMatrix by remember { mutableStateOf(false) }
    val golfers = remember { mutableStateListOf<Golfer>() }

    var holes = remember {
        mutableStateListOf<Hole>(
            Hole(1, 6),
            Hole(2, 8),
            Hole(3, 18),
            Hole(4, 10),
            Hole(5, 14),
            Hole(6, 12),
            Hole(7, 4),
            Hole(8, 16),
            Hole(9, 2),
            Hole(10, 11),
            Hole(11, 7),
            Hole(12, 17),
            Hole(13, 1),
            Hole(14, 13),
            Hole(15, 9),
            Hole(16, 5),
            Hole(17, 15),
            Hole(18, 3)
        )
    }
    if (!showMatrix) {
        GolferInputScreen(golfers, holes) {
            showMatrix = true
        }
    } else {
        BumpMatrixScreen(golfers, holes) {
            showMatrix = false
        }
    }
}

@Composable
fun GolferInputScreen(golfers: MutableList<Golfer>, holes: MutableList<Hole>, onCalculateBumps: () -> Unit) {
    var golferName by remember { mutableStateOf("") }
    var golferBumps by remember { mutableStateOf("") }
    var holeDifficultiesInput by remember { mutableStateOf("") }

    Column {
        TextField(value = golferName, onValueChange = { golferName = it }, label = { Text("Golfer Name") })
        TextField(value = golferBumps, onValueChange = { golferBumps = it }, label = { Text("Bumps") })
        Button(onClick = {
            golfers.add(Golfer(golferName, golferBumps.toInt()))
            golferName = ""
            golferBumps = ""
        }) {
            Text("Add Golfer")
        }
        Button(onClick = onCalculateBumps) {
            Text("Calculate Bumps")
        }
        // New section for hole difficulties input
        TextField(
            value = holeDifficultiesInput,
            onValueChange = { holeDifficultiesInput = it },
            label = { Text("Hole Difficulties (comma separated)") }
        )
        Button(onClick = {
            val difficulties = holeDifficultiesInput.split(",").mapNotNull { it.trim().toIntOrNull() }

            updateHoleDifficulties(holes, difficulties)
        }) {
            Text("Update Hole Difficulty")
        }
    }
}

fun calculateBumps(golfers: List<Golfer>, holes: List<Hole>): Map<String, List<Int>> {
    // Sort the holes by difficulty, descending
    val sortedHoles = holes.sortedBy { it.difficulty }

    // Create a map to hold the result
    val golferBumps = mutableMapOf<String, List<Int>>()

    // Assign bumps for each golfer
    golfers.forEach { golfer ->
        val bumps = sortedHoles.take(golfer.bumps).map { it.number }
        golferBumps[golfer.name] = bumps
    }

    return golferBumps
}

@Composable
fun BumpMatrixScreen(golfers: List<Golfer>, holes: List<Hole>, onBack: () -> Unit) {
    // Handle the system back button press
    BackHandler(onBack = onBack)
    // Calculate the bumps matrix
    val bumpMatrix = calculateBumps(golfers, holes)
    Log.d("tag", "$bumpMatrix")

    // Display the matrix
    //add column headers for the name of each golfer

    Column {
        Button(onClick = onBack) {
            Text("Back")
        }
        // Display a row with the first cell labeled 'hole' and another cell for the name of each golfer
        Row {
            Text("Hole", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
            golfers.forEach { golfer ->
                Text(
                    text = golfer.name,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
            }
        }



    LazyColumn {
        items(holes.sortedBy { it.number }) { hole ->
            Row {
                Text("Hole ${hole.number}: ", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                //for each golfer in golfers write a 1 if they get a bump on this hole, 0 if not
                golfers.forEach { golfer ->
                    val getsBump = bumpMatrix[golfer.name]?.contains(hole.number) ?: false
                    Text(
                        text = if (getsBump) "YES" else "-",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
    }

}

fun updateHoleDifficulties(holes: MutableList<Hole>, difficulties: List<Int>) {
    difficulties.forEachIndexed { index, difficulty ->
        if(index < holes.size) {
            holes[index] = holes[index].copy(difficulty = difficulty)
        }
    }
}

