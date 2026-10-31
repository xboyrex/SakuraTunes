/**
 * Sakura Tunes Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.sakuratunes.music.ui.utils

import java.text.DecimalFormat

fun numberFormatter(n: Int) =
    DecimalFormat("#,###")
        .format(n)
        .replace(",", ".")
