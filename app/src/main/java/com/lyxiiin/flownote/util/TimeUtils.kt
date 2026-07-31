package com.lyxiiin.flownote.util

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/** 完整日期时间：2026-07-28 14:30 */
private val FORMATTER_DATE_TIME: DateTimeFormatter =
    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

/** 仅日期：2026-07-28 */
private val FORMATTER_DATE: DateTimeFormatter =
    DateTimeFormatter.ofPattern("yyyy-MM-dd")

/** 仅时间：14:30 */
private val FORMATTER_TIME: DateTimeFormatter =
    DateTimeFormatter.ofPattern("HH:mm")

/** 列表短日期：07-28 */
private val FORMATTER_SHORT_DATE: DateTimeFormatter =
    DateTimeFormatter.ofPattern("MM-dd")

/** 将毫秒时间戳转为 LocalDateTime（系统时区） */
private fun Long.toLocalDateTime() =
    Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDateTime()

/** 格式化为完整日期时间，如 "2026-07-28 14:30" */
fun Long.toDateTimeString(): String =
    toLocalDateTime().format(FORMATTER_DATE_TIME)

/** 格式化为日期，如 "2026-07-28" */
fun Long.toDateString(): String =
    toLocalDateTime().format(FORMATTER_DATE)

/** 格式化为时间，如 "14:30" */
fun Long.toTimeString(): String =
    toLocalDateTime().format(FORMATTER_TIME)

/**
 * 列表项智能时间显示：
 * - 今天 → "14:30"
 * - 今年 → "07-28"
 * - 更早 → "2025-12-01"
 */
fun Long.toSmartDateString(): String {
    val date = toLocalDateTime().toLocalDate()
    val today = LocalDate.now()
    return when {
        date == today -> toTimeString()
        date.year == today.year -> date.format(FORMATTER_SHORT_DATE)
        else -> toDateString()
    }
}

/** 相对时间描述，如 "刚刚"、"5分钟前"、"3小时前"、"2天前" */
fun Long.toRelativeString(): String {
    val diff = System.currentTimeMillis() - this
    val minutes = diff / 60_000
    val hours = minutes / 60
    val days = hours / 24
    return when {
        minutes < 1 -> "刚刚"
        minutes < 60 -> "${minutes}分钟前"
        hours < 24 -> "${hours}小时前"
        days < 30 -> "${days}天前"
        else -> toDateString()
    }
}
