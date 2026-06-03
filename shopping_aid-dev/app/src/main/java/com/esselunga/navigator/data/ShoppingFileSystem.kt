package com.esselunga.navigator.data.local

import android.content.Context
import com.esselunga.navigator.data.ShoppingItem
import com.esselunga.navigator.data.ListDiff
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class ShoppingFileSystem(private val context: Context) {
    private val fileName = "shared_shopping_list.json"
    private val originalFileName = "original_shopping_list.json"
    private val gson = Gson()
    private val file = File(context.filesDir, fileName)
    private val originalFile = File(context.filesDir, originalFileName)

    // Save the current list and, if it's the first time, also save it as the original for future comparisons.
    fun saveList(items: List<ShoppingItem>) {
        try {
            val jsonString = gson.toJson(items)
            file.writeText(jsonString)
            // Only save the original if it doesn't exist yet, to keep the first version for comparison
            if (!originalFile.exists()) {
                originalFile.writeText(jsonString)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Wipe the original file and save the provided list as the new original, regardless of whether it already exists or not.
    fun forceSaveOriginal(items: List<ShoppingItem>) {
        try {
            val jsonString = gson.toJson(items)
            originalFile.writeText(jsonString)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun loadList(): List<ShoppingItem> {
        return try {
            if (!file.exists()) return emptyList()
            val jsonString = file.readText()
            val type = object : TypeToken<List<ShoppingItem>>() {}.type
            val result: List<ShoppingItem>? = gson.fromJson<List<ShoppingItem>>(jsonString, type)
            result ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun getListDiff(): ListDiff {
        val original = try {
            if (!originalFile.exists()) return ListDiff(emptyList(), emptyList(), emptyList())
            val jsonString = originalFile.readText()
            val type = object : TypeToken<List<ShoppingItem>>() {}.type
            val result: List<ShoppingItem>? = gson.fromJson<List<ShoppingItem>>(jsonString, type)
            result ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
        val modified = loadList()
        return ListDiff.calculate(original, modified)
    }

    fun clearOriginal() {
        originalFile.delete()
    }
}
