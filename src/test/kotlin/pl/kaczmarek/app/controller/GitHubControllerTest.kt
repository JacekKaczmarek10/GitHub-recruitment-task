package pl.kaczmarek.app.controller

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import pl.kaczmarek.app.dto.*

@ExtendWith(MockitoExtension::class)
class GitHubControllerTest {

    @Mock
    lateinit var restTemplate: RestTemplate

    @Test
    fun `test getRepositoriesByUsername when user exists`() {
        val username = "testuser"
        val repositories = arrayOf(
            GithubRepository("repo1", false, "https://api.github.com/repos/user1/repo1/branches", Owner("user1")),
            GithubRepository("repo2", false, "https://api.github.com/repos/user1/repo2/branches", Owner("user1"))
        )
        val branchesRepo1 = arrayOf(Branch("branch1", Commit("sha1")), Branch("branch2", Commit("sha2")))
        val branchesRepo2 = arrayOf(Branch("branch3", Commit("sha3")), Branch("branch4", Commit("sha4")))
        val repoData = listOf(
            RepositoryData("repo1", "user1", listOf(mapOf("name" to "branch1", "sha" to "sha1"), mapOf("name" to "branch2", "sha" to "sha2"))),
            RepositoryData("repo2", "user1", listOf(mapOf("name" to "branch3", "sha" to "sha3"), mapOf("name" to "branch4", "sha" to "sha4")))
        )
        `when`(restTemplate.getForEntity("https://api.github.com/users/$username/repos", Array<GithubRepository>::class.java))
            .thenReturn(ResponseEntity(repositories, HttpStatus.OK))
        `when`(restTemplate.getForEntity("https://api.github.com/repos/user1/repo1/branches", Array<Branch>::class.java))
            .thenReturn(ResponseEntity(branchesRepo1, HttpStatus.OK))
        `when`(restTemplate.getForEntity("https://api.github.com/repos/user1/repo2/branches", Array<Branch>::class.java))
            .thenReturn(ResponseEntity(branchesRepo2, HttpStatus.OK))
        val controller = GitHubController(restTemplate)

        val responseEntity = controller.getRepositoriesByUsername(username)

        assertEquals(HttpStatus.OK, responseEntity.statusCode)
        assertEquals(repoData, responseEntity.body)
    }

    @Test
    fun `test getRepositoriesByUsername when user does not exist`() {
        val username = "nonexistentuser"
        `when`(restTemplate.getForEntity("https://api.github.com/users/$username/repos", Array<GithubRepository>::class.java))
            .thenThrow(HttpClientErrorException(HttpStatus.NOT_FOUND))
        val controller = GitHubController(restTemplate)

        val exception = assertThrows(HttpClientErrorException::class.java) {
            controller.getRepositoriesByUsername(username)
        }

        assertEquals(HttpStatus.NOT_FOUND, exception.statusCode)
    }
}
