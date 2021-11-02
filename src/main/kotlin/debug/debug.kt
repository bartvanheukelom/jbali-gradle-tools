package org.jbali.gradle.debug

import org.gradle.api.tasks.bundling.AbstractArchiveTask

fun AbstractArchiveTask.logArchiveNameParts() {
    logger.info("-> $path")
    logger.info("archiveFile=${archiveFile.orNull}")
    logger.info("archiveAppendix=${archiveAppendix.orNull}")
    logger.info("archiveBaseName=${archiveBaseName.orNull}")
    logger.info("archiveClassifier=${archiveClassifier.orNull}")
    logger.info("archiveExtension=${archiveExtension.orNull}")
    logger.info("archiveFileName=${archiveFileName.orNull}")
    logger.info("archiveVersion=${archiveVersion.orNull}")
    logger.info("archiveName=${archiveName}")
    logger.info("archivePath=${archivePath}")
}
