package com.overlord.mynotes.navigation

sealed class Screen (val route: String){
    object NotesScreen: Screen("notes_screen")
    object DetailedNoteScreen: Screen("detailed_note_screen")
    object Settings: Screen("settings")

    object GPTScreen: Screen("gpt_screen")

    fun withArgs(vararg args: String): String{
        return buildString {
            append(route)
            args.forEach { arg->
                append("/$arg")
            }
        }
    }
}