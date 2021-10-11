package uz.triples.smartstaffsolution

import kotlin.random.Random

private var array = ArrayList<ArrayList<Int>>()
private var changeFrom = -1
private var changeTo = -1

fun main() {
    print("Row count: ")
    val rowCount: Int = readLine()?.toInt() ?: 0
    print("Column count: ")
    val columnCount = readLine()?.toInt() ?: 0

    populateAndPrintArray(rowCount, columnCount)

    print("Row: ")
    val row: Int = readLine()?.toInt() ?: 0
    print("Column: ")
    val column = readLine()?.toInt() ?: 0

    if (row <= 0 || row > rowCount || column <= 0 || column > columnCount) {
        print("Out of array's size")
        return
    }

    print("Change ${array[row-1][column-1]} to ")

    changeTo = readLine()?.toInt() ?: 0
    changeFrom = array[row-1][column-1]

    changeColorFromLeftTopToRightBottom(row-1, column-1)
    printArray()
}

private fun changeColorFromLeftTopToRightBottom(row: Int, column: Int) {
    array[row][column] = changeTo

    if ((row > 0) && (row < array.size))
        if (array[row - 1][column] == changeFrom) changeColorFromLeftTopToRightBottom(row - 1, column)

    if ((row >= 0) && (row < array.size - 1))
        if (array[row + 1][column] == changeFrom) changeColorFromLeftTopToRightBottom(row + 1, column)

    if ((column > 0) && (column < array[row].size))
        if (array[row][column - 1] == changeFrom) changeColorFromLeftTopToRightBottom(row, column - 1)

    if ((column >= 0) && (column < array[row].size - 1))
        if (array[row][column + 1] == changeFrom) changeColorFromLeftTopToRightBottom(row, column + 1)
}

fun printArray() {
    for (n in 0 until array.size) {
        for (k in 0 until array[n].size) {
            print("${array[n][k]} ")
        }
        println()
    }
}

fun populateAndPrintArray(row: Int, column: Int) {
    for (n in 0 until row) {
        val raw = arrayListOf<Int>()
        for (k in 0 until column) {
            val randomNum = Random.nextInt(0, 2)
            raw.add(k, randomNum)
        }
        array.add(n, raw)
    }
    printArray()
}