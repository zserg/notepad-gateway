package com.zserg.notepad.model

enum class ExpenseType (val type: String) {
    FOOD("Food"),
    TRANSPORT("Transport"),
    MISC("Misc"),
    UTILITIES("Utilities"),
    HEALTH("Health"),
    UNKNOWN("Unknown"),
    TOTAL("Total"),
    MISC_TOTAL("MiscTotal"),
    FIXED("Fixed")
   }