Smart Route Finder

Smart Route Finder is an Android application that demonstrates how Artificial Intelligence algorithms can find the optimal path in a weighted graph.
The app implements the Uniform Cost Search to compute the lowest-cost route between nodes.

This project was developed as part of an Artificial Intelligence course project.

Features

• Graph visualization of nodes and weighted edges
• Route calculation using Uniform Cost Search
• Popup showing shortest path and total cost
• Route history tracking
• Like and delete routes from history
• Reset and restart options
• Simple and clean Jetpack Compose UI

App Flow
1. Welcome Screen

Displays the project introduction and features:

Graph Visualization

Step-by-Step Search

Optimal Path Discovery

A Start button takes the user to the route finder.

2. Route Finder

Users select:

Start Node

Destination Node

The system runs Uniform Cost Search to calculate the optimal path.

The result popup shows:

Start Node
Destination Node
Shortest Path
Total Cost

Example:

Start: A
End: E
Path: A → C → D → E
Total Cost: 7
3. History Screen

Shows previously calculated routes.

Users can:

Like a route

Delete a route

Filter liked routes

4. Exit Screen

Displays project credits and allows the user to close the app.

Algorithm Used

The application uses Uniform Cost Search.

Uniform Cost Search works by:

Expanding the node with the lowest path cost.

Using a priority queue to select the next node.

Continuing until the destination node is reached.

Returning the lowest-cost path.

Graph Structure Used
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

Team

Team Hacksphere ⚡

Developed for an Artificial Intelligence academic project.
