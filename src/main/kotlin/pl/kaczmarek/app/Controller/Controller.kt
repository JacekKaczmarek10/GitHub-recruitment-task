package pl.kaczmarek.app.Controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate

@RestController
class Controller(
    private val restTemplate: RestTemplate
) {
    @GetMapping("/{username}/get-repos")
    fun getRepositoriesByUsername(@PathVariable username: String): ResponseEntity<Any> {
        try {
            val url = "https://api.github.com/users/$username/repos"
            val response = restTemplate.getForEntity(url, Array<GithubRepository>::class.java)
            val notForkedRepos = response.body?.filter { !it.fork }?.map { it }
            val repoData = notForkedRepos?.map { repo ->
                val branchesUrl = repo.branches_url.replace("{/branch}", "")
                val branchesResponse = restTemplate.getForEntity(branchesUrl, Array<Branch>::class.java)
                val branches = branchesResponse.body?.map { branch ->
                    mapOf("name" to branch.name, "sha" to branch.commit.sha)
                } ?: emptyList()
                RepositoryData(repo.name, repo.owner.login, branches)
            }
            return ResponseEntity.status(200).body(repoData)
        } catch (e: HttpClientErrorException.NotFound) {
            val errorBody = mapOf("status" to HttpStatus.NOT_FOUND.value(), "message" to "User not found")
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorBody)
        }
    }
}

data class GithubRepository(val name: String, val fork: Boolean, val branches_url: String, val owner: Owner)
data class Owner(val login: String)
data class Branch(val name: String, val commit: Commit)
data class Commit(val sha: String)
data class RepositoryData(val repositoryName: String, val ownerLogin: String, val branches: List<Map<String, String>>)