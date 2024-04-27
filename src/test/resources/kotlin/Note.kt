package icons.myiconpack

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import icons.MyIconPack

public val MyIconPack.Note: ImageVector
    get() {
        if (_note != null) {
            return _note!!
        }
        _note = Builder(name = "Note", defaultWidth = 48.0.dp, defaultHeight = 48.0.dp,
                viewportWidth = 960.0f, viewportHeight = 960.0f).apply {
            path(fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(120.0f, 720.0f)
                lineTo(120.0f, 660.0f)
                lineTo(600.0f, 660.0f)
                lineTo(600.0f, 720.0f)
                lineTo(120.0f, 720.0f)
                close()
                moveTo(120.0f, 510.0f)
                lineTo(120.0f, 450.0f)
                lineTo(840.0f, 450.0f)
                lineTo(840.0f, 510.0f)
                lineTo(120.0f, 510.0f)
                close()
                moveTo(120.0f, 300.0f)
                lineTo(120.0f, 240.0f)
                lineTo(840.0f, 240.0f)
                lineTo(840.0f, 300.0f)
                lineTo(120.0f, 300.0f)
                close()
            }
        }
        .build()
        return _note!!
    }

private var _note: ImageVector? = null
