package eu.codetopic.utils.data.preferences

interface IPreferencesData {

    val isCreated: Boolean

    val isDestroyed: Boolean

    val broadcastActionChanged: String

    val name: String?

    fun init()

    fun destroy()
}