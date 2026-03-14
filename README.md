Smart Route Finder

Smart Route Finder is an Android application that demonstrates how Artificial Intelligence algorithms can find the optimal path in a weighted graph.
The app implements the Uniform Cost Search (UCS) in AI to compute the lowest-cost route between nodes.
This project was developed as part of an Artificial Intelligence course project.

Features

• Interactive graph visualization
• Route calculation using Uniform Cost Search
• Popup displaying shortest path and total cost
• Route history tracking
• Like and delete saved routes
• Reset and restart functionality
• Modern UI built with Jetpack Compose

Application Screens
Welcome Screen

Introduces the application and explains the AI concept.

Features shown:

Graph Visualization

Step-by-Step Search

Optimal Path Discovery

A Start button navigates to the route finder.

Route Finder

Users select:

• Start Node
• Destination Node

The system runs Uniform Cost Search to compute the optimal path.

Example output:

Start: A
End: E

Shortest Path:
A → C → D → E

Total Cost: 7
History Screen

Displays previously calculated routes.

Users can:

• Like important routes
• Delete routes
• Filter liked routes

Exit Screen

Shows project credits and allows users to close the application.

Algorithm Explanation

The application uses Uniform Cost Search, a search algorithm commonly used in Artificial Intelligence for optimal pathfinding.

Steps:

Start from the initial node.

Use a priority queue to expand the node with the lowest cumulative cost.

Continue exploring nodes while tracking visited nodes.

Stop when the destination node is reached.

Return the path with the minimum cost.

Graph Structure

The application uses the following weighted graph:

A → B = 4
A → C = 2
B → D = 5
B → E = 10
C → D = 3
D → E = 2

Nodes:

A, B, C, D, E

Tech Stack

• Kotlin
• Jetpack Compose
• Android Studio
• Material Design 3

Installation

Clone the repository

git clone https://github.com/yourusername/smart-route-finder.git

Open the project in Android Studio

Build and run the application on an emulator or Android device.

Team

Team Hacksphere ⚡

Developed for an Artificial Intelligence project demonstrating pathfinding algorithms.
