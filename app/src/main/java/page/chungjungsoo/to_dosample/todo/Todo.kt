package page.chungjungsoo.to_dosample.todo

import java.sql.Date
import java.time.LocalDateTime

class Todo (
    var title : String? = null,
    var description : String? = null,
    var finished : Boolean = false,
    var date : Long = 0
)