package pl.pszklarska.pubversionchecker

import com.intellij.codeInspection.LocalQuickFixOnPsiElement
import com.intellij.lang.Language
import com.intellij.openapi.project.Project
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType

class DependencyQuickFix(psiElement: PsiElement, private val latestVersion: String) :
    LocalQuickFixOnPsiElement(psiElement) {
    override fun getFamilyName(): String = "Update dependency"

    override fun getText(): String = "Update dependency"

    override fun invoke(project: Project, file: PsiFile, startElement: PsiElement, endElement: PsiElement) {
        val factory = JavaPsiFacade.getInstance(project).elementFactory
        val psiExpression =
            factory.createDummyHolder(
                "^$latestVersion", IElementType(
                    "text",
                    Language.findLanguageByID("yaml")
                ), null
            )
        startElement.replace(psiExpression)
    }
}