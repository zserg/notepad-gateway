package com.zserg.notepad.model

import java.time.LocalDateTime

class FindRequest {
    var fromDate: LocalDateTime? = null
    var toDate: LocalDateTime? = null
    var title: String? = null
    var tags: List<String>? = null
}
