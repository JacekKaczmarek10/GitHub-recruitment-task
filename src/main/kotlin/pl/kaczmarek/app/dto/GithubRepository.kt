package pl.kaczmarek.app.dto

data class GithubRepository(val name: String, val fork: Boolean, val branches_url: String, val owner: Owner)