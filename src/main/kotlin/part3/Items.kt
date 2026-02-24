package tpo.maxim.part3

interface Items

data class Medal(val reason: String): Items

data class Cup(val competition: String, val year: Int) : Items
