package com.wahid.springinit.springinit

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile

@Suppress("DEPRECATION")
class CreateFileFolderAction : AnAction() {
    override fun actionPerformed(p0: AnActionEvent) {
        val project = p0.project ?: return


        val options = arrayOf("File", "Folder")
        val choice = Messages.showDialog(
            project,
            "Choose what to create ?",
            "Create File/Folder",
            options,
            0,
            Messages.getQuestionIcon()
        )

        when (choice) {
            0 -> createFile(project)
            1 -> createFolder(project)
        }

    }

     private fun createFolder(project: Project) {
        val folderName = Messages.showInputDialog(
            project,
            "Enter the name of the folder:",
            "Create New Folder",
            Messages.getQuestionIcon()
        ) ?: return  // Elvis operator for null check

        if (folderName.isEmpty()) {
            return // User cancelled or entered an empty name
        }

        val baseDir: VirtualFile = project.baseDir ?: run {
            Messages.showErrorDialog(project, "Could not get project base directory.", "Error")
            return
        }

        // Prompt the user to select the target directory
        val targetDir: VirtualFile = selectDirectory(project, baseDir) ?: return // User cancelled directory selection

        // Check if the folder already exists
        val existingFolder = targetDir.findChild(folderName)
        if (existingFolder != null) {
            Messages.showErrorDialog(
                project,
                "Folder '$folderName' already exists in the target directory.",
                "Error"
            )
            return
        }

        // Create the folder within a WriteAction
        WriteAction.run<Throwable> {
            try {
//                val newFolder: VirtualFile = targetDir.createChildDirectory(this, folderName)
                Messages.showInfoMessage(
                    project,
                    "Folder '$folderName' created successfully in ${targetDir.path}",
                    "Success"
                )
            } catch (ex: Exception) {
                Messages.showErrorDialog(project, "Error creating folder: ${ex.message}", "Error")
                ex.printStackTrace() // Log the exception for debugging purposes
            }
        }
    }

    private fun createFile(project: Project) {
        val fileName = Messages.showInputDialog(
            project,
            "Enter the name of the file",
            "Create New File",
            Messages.getQuestionIcon()
        ) ?: return

        if (fileName.isNotEmpty()) {
            val baseDir: VirtualFile = project.baseDir ?: run {
                Messages.showErrorDialog(project, "Could not get project base directory.", "Error")
                return
            }

            val targetDir = selectDirectory(project, baseDir)
            val existingFile = targetDir?.findChild(fileName)
            if (existingFile != null) {
                Messages.showErrorDialog(
                    project,
                    "File '$fileName' already exists in the target directory.",
                    "Error"
                )
                return
            }
            WriteAction.run<Throwable> {
                try {
//                    val newFile: VirtualFile? = targetDir?.createChildData(this, fileName)
                    // You can set initial content for the file here if needed
                    // newFile.setBinaryContent("Initial content".toByteArray())
                    if (targetDir != null) {
                        targetDir.createChildData(this, fileName)
                        Messages.showInfoMessage(
                            project,
                            "File '$fileName' created successfully in ${targetDir.path}",
                            "Success"
                        )
                    } else {
                        return@run
                    }


                } catch (ex: Exception) {
                    Messages.showErrorDialog(project, "Error creating file: ${ex.message}", "Error")
                    ex.printStackTrace() // Log the exception for debugging purposes
                }
            }
            return
        }
    }

    private fun selectDirectory(project: Project, baseDir: VirtualFile): VirtualFile? {
        val path = Messages.showInputDialog(
            project,
            "\"Enter the path to the target directory (relative to project root) or leave blank for the project root:\"",
            "Select Targer Directory",
            Messages.getQuestionIcon(),
            "",
            null
        ) ?: return null

        if (path.isEmpty()) {
            return baseDir
        }

        val targetFile = LocalFileSystem
            .getInstance()
            .findFileByPath("${baseDir.path}/$path")
            ?: kotlin.run {
                Messages.showErrorDialog(
                    project,
                    "Invalid directory path specified.",
                    "Error"
                )
                return null
            }
        if (!targetFile.isDirectory) {
            Messages.showErrorDialog(project, "Invalid directory path specified.", "Error")
            return null
        }
        return targetFile
    }
}