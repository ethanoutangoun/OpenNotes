# OpenNotes

OpenNotes is an AI-powered note-taking application built with Kotlin and the OpenAI API. Users will first input short notes on the main screen. It automatically categorizes and stores notes for you, allowing you to efficiently manage and query your notes for better contextual answers. The app also provides functionality to delete notes as needed.

## Features

- **AI-Powered Categorization**: Automatically categorizes your notes based on content.
- **Contextual Search**: Query your notes with AI-powered insights for more relevant answers.
- **Note Management**: Create, pin, delete, and organize notes effortlessly.
- **Intelligent Retrieval**: Retrieves related notes based on query context.

## Installation

1. Clone the repository:
   ```sh
   git clone https://github.com/yourusername/OpenNotes.git
   cd OpenNotes
   ```
2. Open the project in Android Studio.
3. Configure your OpenAI API key by exporting OPENAI_KEY variable in environment (see build.gradle).
4. Build and run the project on an emulator or physical device.

## Device & SDK Requirements

- **Minimum SDK Version**: 24
- **Target SDK Version**: 35
- **Required Features**:
  - Internet Access (for OpenAI API calls)
  - Local Storage Permissions (for saving notes)
- More information in the project's build.gradle file

## Usage

- Add notes via text input on the "Welcome Screen".
- AI will automatically categorize them.
- Search your notes using natural language queries.
- Delete notes when no longer needed.

## Technologies Used

- **Kotlin**: Primary programming language.
- **Jetpack Compose**: UI framework.
- **OpenAI API**: AI-powered note analysis and search.
- **Room Database**: Local storage for notes.
- **ViewModel & LiveData**: State management.
- **Retrofit**: HTTP client for API calls.

## Figma Design
![image](https://github.com/user-attachments/assets/3adde875-bb4e-41f9-b38e-c610e5d222d7)

## Above and Beyond

- Implemented a **context-aware AI retrieval system** to provide better note suggestions.
- Implemented function calling using OpenAI API to manipulate database using Natural Language queries.
- Designed a **clean and intuitive UI** using custom animations and theming.



