@file:OptIn(ExperimentalStdlibApi::class)
package org.jbali.gradle.git

import java.io.File
import kotlin.math.min

/**
 * Allows reading properties of a Git repository at [rootDir].
 * Other than that path, this object is stateless. Each invocation of
 * one of its methods returns the most current information.
 */
class GitRepository(
    val rootDir: File
) {

    fun version() =
        GitRepoVersion(
            hashShort = commitHash(short = true),
            hashLong = commitHash(short = false),
            branch = branch(),
            tags = tags()
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
            .map(::GitTag)

    // TODO nullable if detached head
    fun branch(): GitBranch =
        command(listOf("symbolic-ref", "HEAD"))
            .trim()
            .split('/')
            .last()
            .let(::GitBranch)

    // TODO
//    # Determine if there are any local changes
//    gdcmd = ['git', 'diff', '--', '.']
//    with open(".dockerignore", "r") as di:
//    # what's ignored by docker can also be ignored for this check
//    for ig in di:
//    gdcmd.append(":(exclude)" + ig.strip())
//    local_mods: str = subprocess.check_output(gdcmd).decode("utf8").strip()
//
//    # Compose a tag from the commit ID and, if present, a hash of the local modifications.
//    tag: str = commit_id
//    if local_mods:
//    tag += f"_WithMods_{hash_text(local_mods, 4)}"
//
//    return VersionTag(tag)

    /**
     * Run the given git command (without "git" itself) in this repo
     * and returns its full standard output, decoded as UTF-8, untrimmed.
     */
    fun command(cmd: List<String>) =
        // TODO function for process with output capture
        ProcessBuilder()
            .command(listOf("git") + cmd)
            .directory(rootDir)
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .start().run {
                val output = inputStream.bufferedReader().readText()
                val exit = waitFor()
                if (exit != 0) {
                    throw RuntimeException("Command exited with status $exit")
                }
                output
            }

}

data class GitRepoVersion(
    val hashShort: GitCommitHash,
    val hashLong: GitCommitHash,
    val tags: List<GitTag>,
    val branch: GitBranch?
    // TODO localmods
) {
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
    override fun toString() = tag
}

data class GitBranch(
    private val branch: String
) {
    override fun toString() = branch
}
