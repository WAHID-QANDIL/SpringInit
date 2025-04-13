package com.wahid.springinit.springinit

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile

@Suppress("DEPRECATION")
class GenerateSpringPatternAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        val options = arrayOf("Generate MVC Pattern", "Generate Clean Arch Pattern")
        val choice = Messages.showDialog(
            project,
            "Choose which Spring pattern to generate for package com.wahid.springinit.springinit:",
            "Generate Spring Pattern",
            options,
            0,
            Messages.getQuestionIcon()
        )

        when (choice) {
            0 -> generateMvcPattern(project)
            1 -> generateCleanArchPattern(project)
            else -> return // User cancelled
        }
    }

    /**
     * Generates folders for a basic Spring MVC pattern.
     *
     * Creates (under the base package com/wahid/springinit/springinit):
     *   - controller
     *   - service
     *   - repository
     *   - model
     */
    private fun generateMvcPattern(project: Project) {
        val projectName = project.name
        val basePackagePath = "com/$projectName"
        val targetDir = promptForTargetDirectory(project) ?: return

        // In the target directory, navigate (or create) the base package folder.
        val packageDir = getOrCreateSubdirectory(targetDir, basePackagePath, project) ?: return

        val foldersToCreate = listOf("controller", "service", "repository", "model")
        WriteAction.run<Throwable> {
            for (folder in foldersToCreate) {
                // Check if folder already exists
                if (packageDir.findChild(folder) == null) {
                    packageDir.createChildDirectory(this, folder)
                }
            }
        }
        Messages.showInfoMessage(
            project,
            "Spring MVC pattern folders created under ${packageDir.path}",
            "Success"
        )
    }

    /**
     * Generates folders for a basic Clean Architecture pattern.
     *
     * Creates (under the base package com/wahid/springinit/springinit):
     *   - domain
     *   - usecase
     *   - adapter
     *   - infrastructure
     *   - config
     */
    private fun generateCleanArchPattern(project: Project) {
        val projectName = project.name
        val basePackagePath = "com/$projectName"
        val targetDir = promptForTargetDirectory(project) ?: return

        // In the target directory, navigate (or create) the base package folder.
        val packageDir = getOrCreateSubdirectory(targetDir, basePackagePath, project) ?: return

        val foldersToCreate = listOf("domain", "usecase", "adapter", "infrastructure", "config")
        WriteAction.run<Throwable> {
            for (folder in foldersToCreate) {
                if (packageDir.findChild(folder) == null) {
                    packageDir.createChildDirectory(this, folder)
                }
            }
        }
        Messages.showInfoMessage(
            project,
            "Clean Architecture pattern folders created under ${packageDir.path}",
            "Success"
        )
    }

    /**
     * Prompts the user to enter a target directory (relative to the project base directory)
     * where the pattern will be generated.
     */
    private fun promptForTargetDirectory(project: Project): VirtualFile? {
        val baseDir = project.baseDir ?: run {
            Messages.showErrorDialog(project, "Could not get project base directory.", "Error")
            return null
        }
        val path = Messages.showInputDialog(
            project,
            "Enter the path (relative to project base) where the Spring package is located or leave blank for project root:",
            "Select Target Directory",
            Messages.getQuestionIcon(),
            "src",
            null
        ) ?: return null

        return if (path.isEmpty()) baseDir else {
            val target = LocalFileSystem.getInstance().findFileByPath("${baseDir.path}/$path")
            if (target == null || !target.isDirectory) {
                Messages.showErrorDialog(project, "Invalid directory path specified.", "Error")
                null
            } else {
                target
            }
        }
    }

    /**
     * Traverses (or creates) a subdirectory structure (given by subPath) under [baseDir].
     * subPath should use '/' as separator.
     */
    private fun getOrCreateSubdirectory(baseDir: VirtualFile, subPath: String, project: Project): VirtualFile? {
        var currentDir = baseDir
        val parts = subPath.split("/").filter { it.isNotEmpty() }
        for (part in parts) {
            var child = currentDir.findChild(part)
            if (child == null || !child.isDirectory) {
                child = WriteAction.computeAndWait<VirtualFile, Throwable> {
                    currentDir.createChildDirectory(this, part)
                }
            }
            currentDir = child!!
        }
        return currentDir
    }
}
