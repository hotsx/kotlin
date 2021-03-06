/*
 * Copyright 2010-2015 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.kotlin.idea.search

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.LocalSearchScope
import com.intellij.psi.search.SearchScope
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.psi.KtFile

infix fun SearchScope.and(otherScope: SearchScope): SearchScope = intersectWith(otherScope)
infix fun SearchScope.or(otherScope: SearchScope): SearchScope = union(otherScope)
operator fun SearchScope.minus(otherScope: GlobalSearchScope): SearchScope = this and !otherScope
operator fun GlobalSearchScope.not(): GlobalSearchScope = GlobalSearchScope.notScope(this)

fun Project.allScope(): GlobalSearchScope = GlobalSearchScope.allScope(this)

fun Project.projectScope(): GlobalSearchScope = GlobalSearchScope.projectScope(this)

fun PsiFile.fileScope(): GlobalSearchScope = GlobalSearchScope.fileScope(this)

fun GlobalSearchScope.restrictToKotlinSources() = GlobalSearchScope.getScopeRestrictedByFileTypes(this, KotlinFileType.INSTANCE)

fun SearchScope.restrictToKotlinSources(): SearchScope {
    return when (this) {
        is GlobalSearchScope -> restrictToKotlinSources()
        is LocalSearchScope -> {
            val ktElements = scope.filter { it.containingFile is KtFile }
            when (ktElements.size) {
                0 -> GlobalSearchScope.EMPTY_SCOPE
                scope.size -> this
                else -> LocalSearchScope(ktElements.toTypedArray())
            }
        }
        else -> this
    }
}