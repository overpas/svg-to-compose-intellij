package icons

import androidx.compose.ui.graphics.vector.ImageVector
import icons.myiconpack.`Ab-testing`
import kotlin.collections.List as ____KtList

public object MyIconPack

private var __AllIcons: ____KtList<ImageVector>? = null

public val MyIconPack.AllIcons: ____KtList<ImageVector>
  get() {
    if (__AllIcons != null) {
      return __AllIcons!!
    }
    __AllIcons= listOf(`Ab-testing`)
    return __AllIcons!!
  }
