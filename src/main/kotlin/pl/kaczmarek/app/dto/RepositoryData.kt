package pl.kaczmarek.app.dto

data class RepositoryData(val repositoryName: String, val ownerLogin: String, val branches: List<Map<String, String>>)