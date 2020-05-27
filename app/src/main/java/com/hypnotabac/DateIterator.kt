package com.hypnotabac
/* Sample code taken from https://gist.github.com/elye/5c107e9c8e9121d785f4ddfa103f5808 */
data class Date (private val year: Int, private val month: Int, private val day: Int)
    : Comparable<Date> {

    companion object {
        const val MONTHS_IN_A_YEAR = 12
    }

    init {
        if (month > MONTHS_IN_A_YEAR || month <  0) {
            throw IllegalStateException("Month must between 1 - $MONTHS_IN_A_YEAR")
        }
        if (day > daysInMonth(month, year)) {
            throw IllegalStateException("Day $day not valid in month $month of year $year")
        }
    }

    override operator fun compareTo(other: Date): Int {
        if (this.year > other.year) return 1
        if (this.year < other.year) return -1
        if (this.month > other.month) return 1
        if (this.month < other.month) return -1
        if (this.day > other.day) return 1
        if (this.day < other.day) return -1
        return 0
    }

    operator fun inc(): Date {
        return when {
            (day < daysInMonth(month, year))
            -> Date(year, month, day+1)
            (month < MONTHS_IN_A_YEAR)
            -> Date(year, month + 1, 1)
            else -> Date(year + 1, 1, 1)
        }
    }

    private fun daysInMonth(month: Int, year: Int): Int {
        return when (month) {
            4, 6, 9, 11 -> 30
            2 -> if (leapYear(year)) 29 else 28
            else -> 31
        }
    }

    private fun leapYear(year: Int): Boolean {
        return when {
            year % 400 == 0 -> true
            year % 100 == 0 -> false
            else -> year % 4 == 0
        }
    }

    operator fun rangeTo(that: Date) = DateRange(this, that)
}

class DateRange(override val start: Date, override val endInclusive: Date)
    : ClosedRange<Date>, Iterable<Date> {

    override fun iterator(): Iterator<Date> {
        return DateIterator(start, endInclusive)
    }

}

class DateIterator(start: Date, private val endInclusive: Date) : Iterator<Date> {
    private var current = start

    override fun hasNext(): Boolean {
        return current <= endInclusive
    }

    override fun next(): Date {
        return current++
    }
}