package uz.ziyoakademiya.app.core.data.local

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import uz.ziyoakademiya.app.core.domain.model.*

object Converters {
    private val gson = Gson()

    fun fromStringList(list: List<String>): String = gson.toJson(list)
    fun toStringList(json: String): List<String> = try {
        gson.fromJson(json, object : TypeToken<List<String>>() {}.type) ?: emptyList()
    } catch (e: Exception) {
        emptyList()
    }

    fun fromFormulaList(list: List<FormulaItem>): String = gson.toJson(list)
    fun toFormulaList(json: String): List<FormulaItem> = try {
        gson.fromJson(json, object : TypeToken<List<FormulaItem>>() {}.type) ?: emptyList()
    } catch (e: Exception) {
        emptyList()
    }

    fun fromWorkedExampleList(list: List<WorkedExample>): String = gson.toJson(list)
    fun toWorkedExampleList(json: String): List<WorkedExample> = try {
        gson.fromJson(json, object : TypeToken<List<WorkedExample>>() {}.type) ?: emptyList()
    } catch (e: Exception) {
        emptyList()
    }

    fun fromCommonMistakeList(list: List<CommonMistake>): String = gson.toJson(list)
    fun toCommonMistakeList(json: String): List<CommonMistake> = try {
        gson.fromJson(json, object : TypeToken<List<CommonMistake>>() {}.type) ?: emptyList()
    } catch (e: Exception) {
        emptyList()
    }

    fun fromGuidedPracticeList(list: List<GuidedPracticeItem>): String = gson.toJson(list)
    fun toGuidedPracticeList(json: String): List<GuidedPracticeItem> = try {
        gson.fromJson(json, object : TypeToken<List<GuidedPracticeItem>>() {}.type) ?: emptyList()
    } catch (e: Exception) {
        emptyList()
    }

    fun fromOptionList(list: List<QuestionOption>): String = gson.toJson(list)
    fun toOptionList(json: String): List<QuestionOption> = try {
        gson.fromJson(json, object : TypeToken<List<QuestionOption>>() {}.type) ?: emptyList()
    } catch (e: Exception) {
        emptyList()
    }

    fun fromStringMap(map: Map<String, String>): String = gson.toJson(map)
    fun toStringMap(json: String): Map<String, String> = try {
        gson.fromJson(json, object : TypeToken<Map<String, String>>() {}.type) ?: emptyMap()
    } catch (e: Exception) {
        emptyMap()
    }
}
