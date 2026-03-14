🚀 Smart Route Finder – AI Path Optimization App
📌 Overview

Smart Route Finder is an Android application that demonstrates how Artificial Intelligence finds the optimal path in a weighted graph using the Uniform Cost Search (UCS) algorithm.

The app allows users to select a start node and destination node, then calculates the lowest-cost path between them. It also visualizes the graph and stores previous results in a history section.

This project was developed as part of an Artificial Intelligence course project (Units 1–3 concepts).

🧠 AI Concept Used
Uniform Cost Search (UCS)

Uniform Cost Search is a graph traversal algorithm that expands the node with the lowest path cost first.

Key features:

Guarantees the optimal path

Uses a Priority Queue

Works on weighted graphs

Algorithm steps:

Start from the initial node.

Add nodes to a priority queue based on cost.

Always expand the lowest cost node first.

Continue until the goal node is reached.

Return the optimal path and total cost.

📱 Application Features
🏠 Home Screen

Displays the app logo and introduction

Explains the purpose of the application

Start button to begin route search

🔎 Route Finder

Users can:

Select Start Node

Select Destination Node

Calculate the optimal path

View:

Path sequence

Total cost

📊 Graph Visualization

The application visually displays a weighted graph with nodes and edges.

Nodes:

A, B, C, D, E

Example Graph:

A → B (4)
A → C (2)
B → D (5)
B → E (10)
C → D (3)
D → E (2)
⭐ Route History

The app stores previous searches.

Features:

View past routes

Like favorite routes

Delete routes

Filter liked routes

📦 Data Persistence

Search history is stored using:

SharedPreferences

Gson JSON serialization

🛠 Technologies Used

Kotlin

Android Studio

Jetpack Compose

Material 3 UI

Gson Library

Priority Queue (Java/Kotlin)

🧩 App Architecture
MainActivity
 ├── Home Screen
 ├── Finder Screen
 │     └── Uniform Cost Search Algorithm
 ├── History Screen
 └── Exit Screen

Core Components:

Edge → Represents graph edges
PathResult → Stores search results
NodeState → Used in priority queue
GraphVisualizer → Displays graph UI
📷 App Workflow
Home Screen
      ↓
Route Finder
      ↓
Path Calculation (UCS Algorithm)
      ↓
Result Popup
      ↓
History Screen
      ↓
Exit Screen
🧪 Example Output

Input:

Start Node: A
Destination: E

Output:

Optimal Path:
A → C → D → E

Total Cost:
7
🎯 Learning Objectives

This project demonstrates:

Graph search algorithms

Artificial Intelligence path finding

Android app development

Data persistence in mobile apps

UI development with Jetpack Compose

👨‍💻 Team

Team Hacksphere ⚡

Developed for AI Course Project
