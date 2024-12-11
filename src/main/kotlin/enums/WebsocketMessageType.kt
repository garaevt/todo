package enums

enum class WebsocketMessageType(val type: String) {
    NEW_TODO("new_todo"),
    UPDATE_TODO("update_todo"),
    DELETE_TODO("delete_todo")
}