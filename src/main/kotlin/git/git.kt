@file:OptIn(ExperimentalStdlibApi::class)
package org.jbali.gradle.git

import java.io.File
import java.net.URI
import java.security.MessageDigest
import kotlin.math.min

/**
 * Allows reading properties of a Git repository at [rootDir].
 * Other than that path, this object is stateless. Each invocation of
 * one of its methods returns the most current information.
 */
class GitRepository(
    val rootDir: File
) {

    fun version(
        ignoreAllLocalMods: Boolean = false,
        excludeModifications: List<String> = emptyList()
    ) =
        GitRepoVersion(
            hashShort = commitHash(short = true),
            hashLong = commitHash(short = false),
            branch = branch(),
            tags = tags(),
            remoteUrl = remoteUrl(),
            modifications =
                if (ignoreAllLocalMods) null else {
                    modifications(
                        excludes = excludeModifications
                    )
                }
        )

    fun commitHash(short: Boolean = true): GitCommitHash =
        command(buildList {
            add("rev-parse")
            if (short) {
                add("--short")
            }
            add("HEAD")
        })
            .trim()
            .let(::GitCommitHash)

    fun tags(): List<GitTag> =
        command(listOf("tag", "--points-at", "HEAD"))
            .lines()
            .filter { it.isNotBlank() }
            .map(::GitTag)

    // TODO nullable if detached head
    fun branch(): GitBranch? =
        try {
            command(listOf("symbolic-ref", "--short", "HEAD"))
                .trim()
                .let(::GitBranch)
        } catch (e: RuntimeException) {
            // probably got an error because of detached HEAD
            // TODO only catch the real error
            null
        }

    fun remoteUrl(): URI =
        command(listOf("ls-remote", "--get-url"))
            .trim()
            .let(::URI)

    fun modifications(
        excludes: List<String> = emptyList()
    ): GitModifications? =
        command(
            listOf("diff", "--", ".") +
                    excludes.map { ":(exclude)$it" }
        )
            .takeIf { it.isNotBlank() }
            ?.let(::GitModifications)

    /**
     * Run the given git command (without "git" itself) in this repo
     * and returns its full standard output, decoded as UTF-8, untrimmed.
     */
    fun command(gitCmd: List<String>): String {
        val cmd = listOf("git") + gitCmd
        // TODO function for process with output capture
        return ProcessBuilder()
            .command(cmd)
            .directory(rootDir)
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .start().run {
                val output = inputStream.bufferedReader().readText()
                val exit = waitFor()
                if (exit != 0) {
                    throw RuntimeException("Command $cmd exited with status $exit")
                }
                output
            }
    }

}

data class GitRepoVersion(
    val hashShort: GitCommitHash,
    val hashLong: GitCommitHash,
    val tags: List<GitTag>,
    val branch: GitBranch?,
    val remoteUrl: URI,
    val modifications: GitModifications?
) {

    val hashes by lazy {
        listOf(hashShort, hashLong)
    }

    val versions: List<String> by lazy {
        versionNames()
    }

    fun versionNames(
        branchesWithMods: Boolean = false
    ): List<String> =
        buildList {

            // hashes and tags, more or less immutable
            if (modifications != null) {
                val modSuf = "_WithMods_${modifications.shortHashString}"
    //            hashes.map { "$it$modSuf" }
                add("$hashShort$modSuf")
            } else {
                addAll(tags)
                addAll(hashes)
            }

            // branches, mutable
            if (modifications == null || branchesWithMods) {
                branch?.let { add(it) }
            }

        }.map { it.toString() }

    /**
     * - If there are [modifications], then `${hashShort}_WithMods_${modsHash}`.
     * - Or if this version is clean and tagged in git, the first of the [tags].
     * - Otherwise, [hashShort].
     */
    val canonicalVersion get() = versions.first()

    init {
        require(hashLong.isLong)
        require(hashShort prefixEquals hashLong)
    }
}

data class GitCommitHash(
    private val hash: String
) {
    init {
        check(hash.matches(regex)) {
            "$hash != $regex"
        }
    }

    val isLong get() =
        hash.length == 40

    infix fun prefixEquals(other: GitCommitHash): Boolean {
        val smallest = min(hash.length, other.hash.length)
        return hash.substring(0, smallest) == other.hash.substring(0, smallest)
    }

    override fun toString() = hash

    companion object {
        private val regex = Regex("^[0-9a-f]{7,40}$")
    }
}

data class GitTag(
    private val tag: String
) {
    init {
        require(tag.isNotBlank())
    }
    override fun toString() = tag
}

data class GitBranch(
    private val branch: String
) {
    init {
        require(branch.isNotBlank())
    }
    override fun toString() = branch
}

data class GitModifications(
    private val dump: String
) {
    val hash: ByteArray by lazy {
        MessageDigest.getInstance("SHA-256")
            .digest(dump.toByteArray(Charsets.UTF_8))
    }
    val shortHashString by lazy {
        hash.toHexString().substring(0, 4)
    }
}


private fun ByteArray.toHexString() =
    @OptIn(ExperimentalUnsignedTypes::class)
    asUByteArray().joinToString("") {
        it.toString(16).padStart(2, '0')
    }
